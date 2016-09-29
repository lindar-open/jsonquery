package com.lindar.jsonquery.querydsl.jpa;

import com.google.common.collect.HashBasedTable;
import com.lindar.jsonquery.ast.*;
import com.lindar.jsonquery.relationships.ast.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class QuerydslJpaJsonQueryVisitor implements JsonQueryVisitor<Predicate, PathBuilder>,
        JsonQueryRelationshipVisitor<Predicate, PathBuilder>,
        JsonQueryAggregateVisitor<Predicate, PathBuilder> {

    protected static final Pattern DOT = Pattern.compile("\\.");

    protected HashBasedTable<PathBuilder, String, PathBuilder> joins = HashBasedTable.create();
    protected JPAQuery query;

    public QuerydslJpaJsonQueryVisitor(JPAQuery query){
        this.query = query;
    }

    public Collection<PathBuilder> getJoins(){
        return joins.values();
    }

    public Predicate visit(StringComparisonNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> stringPathJoin = doJoinIfNeeded(node.getField(), entity);

        StringPath stringPath = stringPathJoin.getValue().getString(stringPathJoin.getKey());

        String singleValue = "";
        if(node.getValue() != null && !node.getValue().isEmpty()){
            singleValue = node.getValue().get(0);
        }

        Predicate predicate = null;
        switch(node.getOperation()){

            default:
            case EQUALS:
                predicate = stringPath.eq(singleValue);
                break;
            case CONTAINS:
                predicate = stringPath.like("%"+singleValue+"%");
                break;
            case REGEX:
                predicate = Expressions.predicate(Ops.MATCHES, Expressions.constant(singleValue), stringPath);
                break;
            case BEGINS_WITH:
                predicate = stringPath.like(singleValue+"%");
                break;
            case ENDS_WITH:
                predicate = stringPath.like("%"+singleValue);
                break;
            case EMPTY:
                predicate = stringPath.isEmpty();
                break;
            case IN:
                predicate = stringPath.in(node.getValue());
                break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(BigDecimalComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), context);

        NumberPath<BigDecimal> numberPath = pathJoin.getValue().getNumber(pathJoin.getKey(), BigDecimal.class);

        Predicate predicate = null;

        BigDecimal singleValue = BigDecimal.ZERO;
        if(node.getValue() != null && !node.getValue().isEmpty()){
            singleValue = node.getValue().get(0);
        }

        switch (node.getOperation()){

            case EQUALS:
                predicate = numberPath.eq(singleValue);
                break;
            case GREATER_THAN:
                predicate = numberPath.gt(singleValue);
                break;
            case LESS_THAN:
                predicate = numberPath.lt(singleValue);
                break;
            case GREATER_THAN_OR_EQUAL:
                predicate = numberPath.goe(singleValue);
                break;
            case LESS_THAN_OR_EQUAL:
                predicate = numberPath.loe(singleValue);
                break;
            case BETWEEN:
                predicate = numberPath.between(singleValue, node.getValue().get(1));
                break;
            case EMPTY:
                predicate = numberPath.isNull();
                break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(DateComparisonNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        DateTemplate<Date> dateExpression = Expressions.dateTemplate(Date.class, "date({0})", entity.getDate(node.getField(), Date.class));

        switch(node.getOperation()){

            case RELATIVE:
                return fromRelative(node, dateExpression);
            case ABSOLUTE:
                return fromAbsolute(node, dateExpression);
            case PRESET:
                return fromPreset(node, dateExpression);
        }

        throw new IllegalArgumentException("Unsupported Date operator " + node.getOperation());
    }

    @Override
    public Predicate visit(BooleanComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), context);
        return pathJoin.getValue().getBoolean(pathJoin.getKey()).eq(node.getValue());
    }

    @Override
    public Predicate visit(EnumComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), context);

        StringExpression stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class).stringValue();

        String singleValue = "";
        if(node.getValue() != null && !node.getValue().isEmpty()){
            singleValue = node.getValue().get(0);
        }

        Predicate predicate = null;
        switch(node.getOperation()){

            case EQUALS:
                predicate = stringPath.eq(singleValue);
                break;
            case EMPTY:
                predicate = stringPath.isEmpty();
                break;
            case IN:
                predicate = stringPath.in(node.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + node.getOperation());
        }

        return predicate;
    }

    @Override
    public Predicate visit(LookupComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        Field field = FieldUtils.getField(context.getType(), node.getField(), true);
        if(field != null && field.isAnnotationPresent(ManyToMany.class)){
            return manyLookupVisit(node, context);
        }

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), context);

        NumberExpression longPath = pathJoin.getValue().getNumber(pathJoin.getKey(), Long.class);

        Long singleValue = 0L;
        if(node.getValue() != null && !node.getValue().isEmpty()){
            singleValue = node.getValue().get(0);
        }

        Predicate predicate = null;
        switch(node.getOperation()){

            case EQUALS:
                predicate = longPath.eq(singleValue);
                break;
            case EMPTY:
                predicate = longPath.isNull();
                break;
            case IN:
                predicate = longPath.in(node.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + node.getOperation());
        }

        return predicate;
    }

    private Predicate manyLookupVisit(LookupComparisonNode node, PathBuilder context) {
        Field field = FieldUtils.getField(context.getType(), node.getField(), true);

        if(field == null){
            throw new IllegalArgumentException();
        }

        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if(manyToMany == null){
            throw new IllegalArgumentException();

        }
        Class<?> relatedClass = PathBuilderValidator.FIELDS.validate(context.getType(), node.getField(), Object.class);

        String primaryKey;
        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(context.getType(), Id.class);
        if(fieldsListWithAnnotation.isEmpty()){
            primaryKey = "id";
        } else {
            primaryKey = fieldsListWithAnnotation.get(0).getName();
        }

        PathMetadata pathMetadata = PathMetadataFactory.forCollectionAny(context.getSet(node.getField(), Object.class));
        return new PathBuilder(relatedClass, pathMetadata).get(primaryKey).in(node.getValue());
    }

    public Predicate visit(LogicalNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        List<Predicate> predicates = node.getItems().stream().map(n -> {
            return n.accept(this, entity);
        }).collect(Collectors.toList());

        BooleanBuilder predicate = new BooleanBuilder();
        switch(node.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }

        return predicate;
    }

    @Override
    public Predicate visit(LogicalRelationshipNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        List<Predicate> predicates = node.getItems().stream()
                .map(n -> n.accept(this, entity))
                .collect(Collectors.toList());

        switch(node.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }
        throw new IllegalArgumentException("Unsupported Logical Relationship operator " + node.getOperation());
    }

    @Override
    public Predicate visit(RelatedRelationshipNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        JPAQuery subquery = new JPAQuery<>();

        String primaryKey;
        String foreignKey;
        Class<?> relatedClass = PathBuilderValidator.FIELDS.validate(context.getType(), node.getField(), Object.class);

        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(context.getType(), Id.class);
        if(fieldsListWithAnnotation.isEmpty()){
            primaryKey = "id";
        } else {
            primaryKey = fieldsListWithAnnotation.get(0).getName();
        }

        Field relationshipField = FieldUtils.getField(context.getType(), node.getField(), true);
        if(relationshipField == null){
            throw new IllegalArgumentException();
        }

        if(relationshipField.isAnnotationPresent(OneToMany.class)){
            OneToMany annotation = relationshipField.getAnnotation(OneToMany.class);
            foreignKey = annotation.mappedBy();
        } else {
            String className = context.getType().getSimpleName();
            className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
            if(FieldUtils.getField(relatedClass, className) == null){
                throw new IllegalArgumentException();
            }
            foreignKey = className;
        }

        PathBuilder subqueryEntity = new PathBuilder(relatedClass, relatedClass.getSimpleName());

        PathBuilder subqueryKey = subqueryEntity.get(foreignKey).get(primaryKey);

        Predicate conditionsPredicate = visit(node.getConditions(), subqueryEntity);
        Predicate havingPredicate = visit(node.getAggregations(), subqueryEntity);

        if((conditionsPredicate == null || "".equals(conditionsPredicate.toString())) && (havingPredicate == null || "".equals(havingPredicate.toString()))){
            return new BooleanBuilder();
        }

        subquery.select(subqueryKey)
                .from(subqueryEntity)
                .where(conditionsPredicate)
                .groupBy(subqueryKey)
                .having(havingPredicate);

        return context.in(subquery);
    }

    @Override
    public Predicate visit(StringComparisonAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), entity);

        StringExpression stringPath = pathJoin.getValue().getString(pathJoin.getKey());

        NumberExpression numberPath = null;

        switch (node.getAggregateOperation()){

            case COUNT:
                numberPath = stringPath.count();
                break;
            case COUNT_DISTINCT:
                numberPath = stringPath.countDistinct();
                break;
        }

        Predicate predicate = null;

        Integer singleValue = node.getValue().get(0);

        switch (node.getOperation()){

            case EQUALS:
                predicate = numberPath.eq(singleValue);
                break;
            case GREATER_THAN:
                predicate = numberPath.gt(singleValue);
                break;
            case LESS_THAN:
                predicate = numberPath.lt(singleValue);
                break;
            case GREATER_THAN_OR_EQUAL:
                predicate = numberPath.goe(singleValue);
                break;
            case LESS_THAN_OR_EQUAL:
                predicate = numberPath.loe(singleValue);
                break;
            case BETWEEN:
                predicate = numberPath.between(singleValue, node.getValue().get(1));
                break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(LogicalAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        List<Predicate> predicates = node.getItems().stream().map(n -> {
            return n.accept(this, entity);
        }).collect(Collectors.toList());

        switch(node.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }
        throw new IllegalArgumentException("Unsupported Logical aggregate operator " + node.getOperation());
    }

    @Override
    public Predicate visit(BigDecimalComparisonAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), entity);

        NumberExpression numberPath = pathJoin.getValue().getNumber(pathJoin.getKey(), BigDecimal.class);

        switch (node.getAggregateOperation()){

            case SUM:
                numberPath = numberPath.sum();
                break;
            case COUNT:
                numberPath = numberPath.count();
                break;
            case COUNT_DISTINCT:
                numberPath = numberPath.countDistinct();
                break;
            case AVG:
                numberPath = numberPath.avg();
                break;
            case MAX:
                numberPath = numberPath.max();
                break;
            case MIN:
                numberPath = numberPath.min();
                break;
        }

        Predicate predicate = null;

        BigDecimal singleValue = node.getValue().get(0);

        switch (node.getOperation()){

            case EQUALS:
                predicate = numberPath.eq(singleValue);
                break;
            case GREATER_THAN:
                predicate = numberPath.gt(singleValue);
                break;
            case LESS_THAN:
                predicate = numberPath.lt(singleValue);
                break;
            case GREATER_THAN_OR_EQUAL:
                predicate = numberPath.goe(singleValue);
                break;
            case LESS_THAN_OR_EQUAL:
                predicate = numberPath.loe(singleValue);
                break;
            case BETWEEN:
                predicate = numberPath.between(node.getValue().get(0), node.getValue().get(1));
                break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(EnumComparisonAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(node.getField(), entity);

        StringExpression stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class).stringValue();

        NumberExpression numberPath = null;

        switch (node.getAggregateOperation()){

            case COUNT:
                numberPath = stringPath.count();
                break;
            case COUNT_DISTINCT:
                numberPath = stringPath.countDistinct();
                break;
        }

        Predicate predicate = null;

        Integer singleValue = node.getValue().get(0);

        switch (node.getOperation()){

            case EQUALS:
                predicate = numberPath.eq(singleValue);
                break;
            case GREATER_THAN:
                predicate = numberPath.gt(singleValue);
                break;
            case LESS_THAN:
                predicate = numberPath.lt(singleValue);
                break;
            case GREATER_THAN_OR_EQUAL:
                predicate = numberPath.goe(singleValue);
                break;
            case LESS_THAN_OR_EQUAL:
                predicate = numberPath.loe(singleValue);
                break;
            case BETWEEN:
                predicate = numberPath.between(singleValue, node.getValue().get(1));
                break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    private String toSafeReference(String reference){
        return reference.replace("-", "_");
    }

    private Predicate fromPreset(DateComparisonNode dateComparisonNode, DateTemplate<Date> dateExpression){

        switch (dateComparisonNode.getPresetOperation()){

            case TODAY:
                return dateExpression.eq(new Date());
            case YESTERDAY:
                return dateExpression.eq(fromLocalDate(LocalDate.now().minusDays(1)));
            case CURRENT_WEEK:
                return dateExpression.goe(fromLocalDate(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 1)))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 7))));
            case LAST_WEEK:
                return dateExpression.goe(fromLocalDate(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 1)))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 7))));
            case CURRENT_MONTH:
                return dateExpression.goe(fromLocalDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()))));
            case LAST_MONTH:
                return dateExpression.goe(fromLocalDate(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()))));
            case CURRENT_YEAR:
                return dateExpression.goe(fromLocalDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()))));
            case LAST_YEAR:
                return dateExpression.goe(fromLocalDate(LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear())))
                        .and(dateExpression.loe(fromLocalDate(LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear()))));
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    private Predicate fromRelative(DateComparisonNode dateComparisonNode, DateTemplate<Date> dateExpression){

        switch (dateComparisonNode.getRelativeOperation()){

            case IN_THE_LAST:
                return dateExpression.goe(fromRelativeDate(dateComparisonNode));
            case MORE_THAN:
                return dateExpression.lt(fromRelativeDate(dateComparisonNode));
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    private Predicate fromAbsolute(DateComparisonNode dateComparisonNode, DateTemplate<Date> dateExpression){
        Date startDate = dateComparisonNode.getDateValue().get(0);
        Date endDate = new Date();
        if(dateComparisonNode.getDateValue().size() > 1){
            endDate = dateComparisonNode.getDateValue().get(1);
        }

        switch(dateComparisonNode.getDateOperation()){
            case EQUALS:
                return Expressions.booleanOperation(Ops.EQ, dateExpression, Expressions.constant(startDate));
            case GREATER_THAN:
                return Expressions.booleanOperation(Ops.GT, dateExpression, Expressions.constant(startDate));
            case LESS_THAN:
                return Expressions.booleanOperation(Ops.LT, dateExpression, Expressions.constant(startDate));
            case GREATER_THAN_OR_EQUAL:
                return Expressions.booleanOperation(Ops.GOE, dateExpression, Expressions.constant(startDate));
            case LESS_THAN_OR_EQUAL:
                return Expressions.booleanOperation(Ops.LOE, dateExpression, Expressions.constant(startDate));
            case BETWEEN:
                return Expressions.booleanOperation(Ops.BETWEEN, dateExpression, Expressions.constant(startDate), Expressions.constant(endDate));
        }
        throw new IllegalArgumentException("Date operation not supported");
    }

    private Date fromRelativeDate(DateComparisonNode dateComparisonNode){
        switch(dateComparisonNode.getRelativePeriod()){
            case DAY:
                return fromLocalDate(LocalDate.now().minusDays(dateComparisonNode.getRelativeValue()));
            case WEEK:
                return fromLocalDate(LocalDate.now().minusWeeks(dateComparisonNode.getRelativeValue()));
            case MONTH:
                return fromLocalDate(LocalDate.now().minusMonths(dateComparisonNode.getRelativeValue()));
            case YEAR:
                return fromLocalDate(LocalDate.now().minusYears(dateComparisonNode.getRelativeValue()));
        }
        throw new IllegalArgumentException("Date operation not supported");
    }

    private Date fromLocalDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private ImmutablePair<String, PathBuilder> doJoinIfNeeded(String field, PathBuilder entity){
        String[] fieldParts = DOT.split(field);

        // join needed
        if(fieldParts.length > 1){
            String[] parent = new String[fieldParts.length - 1];
            System.arraycopy(fieldParts, 0, parent, 0, fieldParts.length - 1);

            PathBuilder<?> parentAlias = doJoin(entity, StringUtils.join(parent, "."), "");
            return ImmutablePair.of(fieldParts[fieldParts.length - 1], parentAlias);
        }

        // not needed
        return ImmutablePair.of(field, entity);
    }

    private PathBuilder<?> doJoin (
            PathBuilder<?> entity,
            String path,
            String reference) {

        String safeReference = toSafeReference(reference);
        String mapReference = safeReference;
        PathBuilder<?> rv = joins.get(entity, mapReference);
        if (rv == null) {
            if (path.contains(".")) {
                String[] tokens = DOT.split(path);
                String[] parent = new String[tokens.length - 1];
                System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
                String parentKey = StringUtils.join(parent, ".");
                entity = doJoin(entity, parentKey, safeReference);
                rv = new PathBuilder(Object.class, StringUtils.join(tokens, "_") + safeReference);
                query.leftJoin((EntityPath)entity.get(tokens[tokens.length - 1]), rv);
            } else {
                rv = new PathBuilder(Object.class, path + safeReference);
                query.leftJoin((EntityPath)entity.get(path), rv);
            }
            joins.put(entity, mapReference, rv);
        }
        return rv;
    }

}
