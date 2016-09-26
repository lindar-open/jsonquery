package com.lindar.jsonquery.querydsl.jpa;

import com.google.common.collect.HashBasedTable;
import com.lindar.jsonquery.ast.*;
import com.lindar.jsonquery.relationships.ast.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.persistence.Id;
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
    public Predicate visit(DateComparisonNode dateComparisonNode, PathBuilder entity) {
        DateTemplate<Date> dateExpression = Expressions.dateTemplate(Date.class, "date({0})", entity.getDate(dateComparisonNode.getField(), Date.class));

        switch(dateComparisonNode.getOperation()){

            case RELATIVE:
                return fromRelative(dateComparisonNode, dateExpression);
            case ABSOLUTE:
                return fromAbsolute(dateComparisonNode, dateExpression);
            case PRESET:
                return fromPreset(dateComparisonNode, dateExpression);
        }

        throw new IllegalArgumentException("Unsupported Date operator " + dateComparisonNode.getOperation());
    }

    @Override
    public Predicate visit(BooleanComparisonNode booleanComparisonNode, PathBuilder context) {
        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(booleanComparisonNode.getField(), context);
        return pathJoin.getValue().getBoolean(pathJoin.getKey()).eq(booleanComparisonNode.getValue());
    }

    @Override
    public Predicate visit(EnumComparisonNode enumComparisonNode, PathBuilder context) {
        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(enumComparisonNode.getField(), context);

        StringExpression stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class).stringValue();

        String singleValue = "";
        if(enumComparisonNode.getValue() != null && !enumComparisonNode.getValue().isEmpty()){
            singleValue = enumComparisonNode.getValue().get(0);
        }

        Predicate predicate = null;
        switch(enumComparisonNode.getOperation()){

            case EQUALS:
                predicate = stringPath.eq(singleValue);
                break;
            case EMPTY:
                predicate = stringPath.isEmpty();
                break;
            case IN:
                predicate = stringPath.in(enumComparisonNode.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + enumComparisonNode.getOperation());
        }

        return predicate;
    }

    @Override
    public Predicate visit(LookupComparisonNode lookupComparisonNode, PathBuilder context) {
        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(lookupComparisonNode.getField(), context);

        NumberExpression longPath = pathJoin.getValue().getNumber(pathJoin.getKey(), Long.class);

        Long singleValue = 0L;
        if(lookupComparisonNode.getValue() != null && !lookupComparisonNode.getValue().isEmpty()){
            singleValue = lookupComparisonNode.getValue().get(0);
        }

        Predicate predicate = null;
        switch(lookupComparisonNode.getOperation()){

            case EQUALS:
                predicate = longPath.eq(singleValue);
                break;
            case EMPTY:
                predicate = longPath.isNull();
                break;
            case IN:
                predicate = longPath.in(lookupComparisonNode.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + lookupComparisonNode.getOperation());
        }

        return predicate;
    }

    public Predicate visit(LogicalNode logicalNode, PathBuilder entity) {
        List<Predicate> predicates = logicalNode.getItems().stream().map(node -> {
            return node.accept(this, entity);
        }).collect(Collectors.toList());

        BooleanBuilder predicate = new BooleanBuilder();
        switch(logicalNode.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }

        return predicate;
    }

    @Override
    public Predicate visit(LogicalRelationshipNode logicalRelationshipNode, PathBuilder entity) {
        List<Predicate> predicates = logicalRelationshipNode.getItems().stream()
                .map(node -> node.accept(this, entity))
                .collect(Collectors.toList());

        switch(logicalRelationshipNode.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }
        throw new IllegalArgumentException("Unsupported Logical Relationship operator " + logicalRelationshipNode.getOperation());
    }

    @Override
    public Predicate visit(RelatedRelationshipNode relatedRelationshipNode, PathBuilder context) {
        JPAQuery subquery = new JPAQuery<>();

        String primaryKey;
        String foreignKey;
        Class<?> relatedClass = PathBuilderValidator.FIELDS.validate(context.getType(), relatedRelationshipNode.getField(), Object.class);

        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(context.getType(), Id.class);
        if(fieldsListWithAnnotation.isEmpty()){
            primaryKey = "id";
        } else {
            primaryKey = fieldsListWithAnnotation.get(0).getName();
        }


        Field relationshipField = FieldUtils.getField(context.getType(), relatedRelationshipNode.getField(), true);
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

        subquery.select(subqueryKey)
                .from(subqueryEntity)
                .where(visit(relatedRelationshipNode.getConditions(), subqueryEntity))
                .groupBy(subqueryKey)
                .having(visit(relatedRelationshipNode.getAggregations(), subqueryEntity));

        return context.in(subquery);
    }




    @Override
    public Predicate visit(StringComparisonAggregateNode stringComparisonAggregateNode, PathBuilder entity) {

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(stringComparisonAggregateNode.getField(), entity);

        StringExpression stringPath = pathJoin.getValue().getString(pathJoin.getKey());

        NumberExpression numberPath = null;

        switch (stringComparisonAggregateNode.getAggregateOperation()){

            case COUNT:
                numberPath = stringPath.count();
                break;
            case COUNT_DISTINCT:
                numberPath = stringPath.countDistinct();
                break;
        }

        Predicate predicate = null;

        Integer singleValue = stringComparisonAggregateNode.getValue().get(0);

        switch (stringComparisonAggregateNode.getOperation()){

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
                predicate = numberPath.between(singleValue, stringComparisonAggregateNode.getValue().get(1));
                break;
        }

        if(stringComparisonAggregateNode.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(LogicalAggregateNode logicalAggregateNode, PathBuilder entity) {
        List<Predicate> predicates = logicalAggregateNode.getItems().stream().map(node -> {
            return node.accept(this, entity);
        }).collect(Collectors.toList());

        switch(logicalAggregateNode.getOperation()){

            case AND:
                return ExpressionUtils.allOf(predicates);
            case OR:
                return ExpressionUtils.anyOf(predicates);
        }
        throw new IllegalArgumentException("Unsupported Logical aggregate operator " + logicalAggregateNode.getOperation());
    }

    @Override
    public Predicate visit(BigDecimalComparisonAggregateNode bigDecimalComparisonAggregateNode, PathBuilder entity) {

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(bigDecimalComparisonAggregateNode.getField(), entity);

        NumberExpression numberPath = pathJoin.getValue().getNumber(pathJoin.getKey(), BigDecimal.class);

        switch (bigDecimalComparisonAggregateNode.getAggregateOperation()){

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

        BigDecimal singleValue = bigDecimalComparisonAggregateNode.getValue().get(0);

        switch (bigDecimalComparisonAggregateNode.getOperation()){

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
                predicate = numberPath.between(bigDecimalComparisonAggregateNode.getValue().get(0), bigDecimalComparisonAggregateNode.getValue().get(1));
                break;
        }

        if(bigDecimalComparisonAggregateNode.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(EnumComparisonAggregateNode enumComparisonAggregateNode, PathBuilder entity) {

        ImmutablePair<String, PathBuilder> pathJoin = doJoinIfNeeded(enumComparisonAggregateNode.getField(), entity);

        StringExpression stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class).stringValue();

        NumberExpression numberPath = null;

        switch (enumComparisonAggregateNode.getAggregateOperation()){

            case COUNT:
                numberPath = stringPath.count();
                break;
            case COUNT_DISTINCT:
                numberPath = stringPath.countDistinct();
                break;
        }

        Predicate predicate = null;

        Integer singleValue = enumComparisonAggregateNode.getValue().get(0);

        switch (enumComparisonAggregateNode.getOperation()){

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
                predicate = numberPath.between(singleValue, enumComparisonAggregateNode.getValue().get(1));
                break;
        }

        if(enumComparisonAggregateNode.isNegate()){
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
