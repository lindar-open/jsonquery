package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by stevenhills on 24/09/2016.
 */
public abstract class QuerydslJsonQueryVisitor implements JsonQueryVisitor<Predicate, PathBuilder>, JsonQueryAggregateVisitor<Predicate, PathBuilder> {

    public Predicate visit(StringComparisonNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> stringPathJoin = processPath(node.getField(), entity);

        //StringExpression stringPath = Expressions.stringOperation(Ops.STRING_CAST, new Expression[]{stringPathJoin.getValue().getString(stringPathJoin.getKey())});
        StringExpression stringPath = stringPathJoin.getValue().getString(stringPathJoin.getKey()).stringValue();

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
                predicate = stringPath.matches(singleValue);
                break;
            case BEGINS_WITH:
                predicate = stringPath.like(singleValue+"%");
                break;
            case ENDS_WITH:
                predicate = stringPath.like("%"+singleValue);
                break;
            case EMPTY:
                predicate = stringPath.eq("");
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

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);

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
            case IN:
                predicate = numberPath.in(node.getValue());
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

        Predicate predicate = new BooleanBuilder();
        switch(node.getOperation()){

            case RELATIVE:
                predicate = fromRelative(node, dateExpression); break;
            case ABSOLUTE:
                predicate = fromAbsolute(node, dateExpression); break;
            case PRESET:
                predicate = fromPreset(node, dateExpression); break;
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(BooleanComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);
        return pathJoin.getValue().getBoolean(pathJoin.getKey()).eq(node.getValue());
    }

    @Override
    public Predicate visit(EnumComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);

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
                predicate = stringPath.isNull();
                break;
            case IN:
                predicate = stringPath.in(node.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + node.getOperation());
        }

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
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
    public Predicate visit(StringComparisonAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), entity);

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

        Integer singleValue = 0;
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

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), entity);

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

        if(node.getValue() == null || node.getValue().isEmpty()){
            return new BooleanBuilder();
        }

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
                predicate = numberPath.between(node.getValue().get(0), node.getValue().get(1));
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
    public Predicate visit(EnumComparisonAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), entity);

        EnumPath stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class);

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

        Integer singleValue = 0;
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
            case ANNIVERSARY:
                return dateExpression.month().eq(LocalDate.now().getMonthValue()).and(dateExpression.dayOfMonth().eq(LocalDate.now().getDayOfMonth()));
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    private Predicate fromRelative(DateComparisonNode dateComparisonNode, DateTemplate<Date> dateExpression){

        switch (dateComparisonNode.getRelativeOperation()){
            case IN_THE_LAST:
                return dateExpression.gt(fromRelativeDate(dateComparisonNode));
            case MORE_THAN:
                return dateExpression.lt(fromRelativeDate(dateComparisonNode));
            case IS:
                return dateExpression.eq(fromRelativeDate(dateComparisonNode));
            case DAY:
                return dateExpression.in(fromRelativeDateDays(dateComparisonNode));
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

        if(dateComparisonNode.getRelativePeriod() == null){
            return fromLocalDate(LocalDate.now().minusDays(0));
        }

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

    private List<Date> fromRelativeDateDays(DateComparisonNode dateComparisonNode){
        if(dateComparisonNode.getRelativeDays() == null) return new ArrayList<>();

        return dateComparisonNode.getRelativeDays().getDaysOfWeek().stream()
                .map(dayOfWeek -> fromLocalDate(LocalDate.now().minusWeeks(dateComparisonNode.getRelativeValue()).with(TemporalAdjusters.nextOrSame(dayOfWeek))))
                .collect(Collectors.toList());
    }

    private Date fromLocalDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected abstract ImmutablePair<String, PathBuilder> processPath(String field, PathBuilder entity);

}
