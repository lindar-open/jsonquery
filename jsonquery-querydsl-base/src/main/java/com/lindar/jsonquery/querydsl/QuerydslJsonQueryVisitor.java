package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigDecimal;
import java.time.*;
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

    private final static QuerydslLocalDateCalculator LOCAL_DATE_CALCULATOR = new QuerydslLocalDateCalculator();
    private final static QuerydslInstantCalculator INSTANT_CALCULATOR = new QuerydslInstantCalculator();

    protected abstract ImmutablePair<String, PathBuilder> processPath(String field, PathBuilder entity);

    public Predicate visit(StringComparisonNode node, PathBuilder entity) {
        if (!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> stringPathJoin = processPath(node.getField(), entity);
        //StringExpression stringPath = Expressions.stringOperation(Ops.STRING_CAST, new Expression[]{stringPathJoin.getValue().getString(stringPathJoin.getKey())});
        StringExpression stringPath = stringPathJoin.getValue().getString(stringPathJoin.getKey()).stringValue();
        Predicate predicate = getStringOperationPredicate(node, stringPath);
        return                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              negateIfNeeded(predicate, node);
    }

    @Override
    public Predicate visit(DateComparisonNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        DateTemplate<Date> dateExpression = Expressions.dateTemplate(Date.class, "date({0})", entity.getDate(node.getField(), Date.class));

        Predicate predicate = getDateComparisonPredicate(node, dateExpression);
        return negateIfNeeded(predicate, node);
    }

    @Override
    public Predicate visit(DateInstantComparisonNode node, PathBuilder entity) {
        DateExpression<Instant> dateExpression = entity.getDate(node.getField(), Instant.class);
        return INSTANT_CALCULATOR.toPredicate(node, dateExpression);
    }

    @Override
    public Predicate visit(DateLocalDateComparisonNode node, PathBuilder entity) {
        DateExpression<LocalDate> dateExpression = entity.getDate(node.getField(), LocalDate.class);
        return LOCAL_DATE_CALCULATOR.toPredicate(node, dateExpression);
    }

    @Override
    public Predicate visit(BooleanComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);
        return pathJoin.getValue().getBoolean(pathJoin.getKey()).eq(node.getValue());
    }

    @Override
    public Predicate visit(BigDecimalComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);

        NumberPath<BigDecimal> numberPath = pathJoin.getValue().getNumber(pathJoin.getKey(), BigDecimal.class);

        Predicate predicate = getBigDecimalPredicate(node, numberPath);
        return negateIfNeeded(predicate, node);
    }

    @Override
    public Predicate visit(EnumComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);

        StringExpression stringPath = pathJoin.getValue().getEnum(pathJoin.getKey(), Enum.class).stringValue();
        Predicate predicate = getEnumOperationPredicate(node, stringPath);
        return negateIfNeeded(predicate, node);
    }

    private static Predicate getStringOperationPredicate(StringComparisonNode node, StringExpression stringPath) {
        String singleValue = getFirstValue(node.getValue(), "");
        switch(node.getOperation()) {
            case EQUALS:
                return stringPath.eq(singleValue);
            case CONTAINS:
                return stringPath.like("%"+ singleValue +"%");
            case REGEX:
                return stringPath.matches(singleValue);
            case BEGINS_WITH:
                return stringPath.like(singleValue +"%");
            case ENDS_WITH:
                return stringPath.like("%"+ singleValue);
            case EMPTY:
                return stringPath.eq("");
            case IN:
                return stringPath.in(node.getValue());
        }
        throw new IllegalArgumentException("Unsupported Date operator " + node.getOperation());
    }

    private static Predicate getBigDecimalPredicate(BigDecimalComparisonNode node, NumberPath<BigDecimal> numberPath) {
        BigDecimal singleValue = getFirstValue(node.getValue(), BigDecimal.ZERO);
        switch (node.getOperation()){
            case EQUALS:
                return numberPath.eq(singleValue);
            case GREATER_THAN:
                return numberPath.gt(singleValue);
            case LESS_THAN:
                return numberPath.lt(singleValue);
            case GREATER_THAN_OR_EQUAL:
                return numberPath.goe(singleValue);
            case LESS_THAN_OR_EQUAL:
                return numberPath.loe(singleValue);
            case BETWEEN:
                return numberPath.between(singleValue, node.getValue().get(1));
            case EMPTY:
                return numberPath.isNull();
            case IN:
                return numberPath.in(node.getValue());
        }
        throw new IllegalArgumentException("Unsupported BigDecimal operator " + node.getOperation());
    }

    private static Predicate getDateComparisonPredicate(DateComparisonNode node, DateTemplate<Date> dateExpression) {
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

    private static Predicate getEnumOperationPredicate(EnumComparisonNode node, StringExpression stringPath) {
        String singleValue = getFirstValue(node.getValue(), "");
        switch(node.getOperation()){
            case EQUALS:
                return stringPath.eq(singleValue);
            case EMPTY:
                return stringPath.isNull();
            case IN:
                return stringPath.in(node.getValue());
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + node.getOperation());
        }
    }

    public Predicate visit(LogicalNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        List<Predicate> predicates = node.getItems().stream().map(n -> n.accept(this, entity)).collect(Collectors.toList());
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

        Predicate predicate = createIntegerPredicate(numberPath, node.getValue(), node.getOperation());
        return negateIfNeeded(predicate, node);
    }

    @Override
    public Predicate visit(LogicalAggregateNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        List<Predicate> predicates = node.getItems()
                .stream()
                .map(n -> n.accept(this, entity))
                .collect(Collectors.toList());

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

        if (node.getValue() == null || node.getValue().isEmpty()){
            return new BooleanBuilder();
        }

        Predicate predicate = getBigDecimalAggregatePredicate(node, numberPath);
        return negateIfNeeded(predicate, node);
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

        Predicate predicate = createIntegerPredicate(numberPath, node.getValue(), node.getOperation());
        return negateIfNeeded(predicate, node);
    }

    private static Predicate createIntegerPredicate(NumberExpression numberPath, List<Integer> value, AggregateComparisonOperation operation) {
        Integer singleValue = getFirstValue(value, 0);
        switch (operation) {
            case EQUALS:
                return  numberPath.eq(singleValue);
            case GREATER_THAN:
                return  numberPath.gt(singleValue);
            case LESS_THAN:
                return  numberPath.lt(singleValue);
            case GREATER_THAN_OR_EQUAL:
                return  numberPath.goe(singleValue);
            case LESS_THAN_OR_EQUAL:
                return  numberPath.loe(singleValue);
            case BETWEEN:
                return  numberPath.between(singleValue, value.get(1));
        }
        throw new IllegalArgumentException("Unsupported Integer operation " + operation);
    }

    private static Predicate getBigDecimalAggregatePredicate(BigDecimalComparisonAggregateNode node, NumberExpression numberPath) {
        BigDecimal singleValue = getFirstValue(node.getValue(), BigDecimal.ZERO);
        switch (node.getOperation()){
            case EQUALS:
                return numberPath.eq(singleValue);
            case GREATER_THAN:
                return numberPath.gt(singleValue);
            case LESS_THAN:
                return numberPath.lt(singleValue);
            case GREATER_THAN_OR_EQUAL:
                return numberPath.goe(singleValue);
            case LESS_THAN_OR_EQUAL:
                return numberPath.loe(singleValue);
            case BETWEEN:
                return numberPath.between(singleValue, node.getValue().get(1));
            case EMPTY:
                return numberPath.isNull();
        }
        throw new IllegalArgumentException("Unsupported BigDecimal aggregate operator " + node.getOperation());
    }

    private static <T> T getFirstValue(List<T> value, T defaultValue) {
        if (value != null && !value.isEmpty()) {
            return value.get(0);
        }
        return defaultValue;
    }

    private static Predicate fromPreset(DateComparisonNode dateComparisonNode, DateExpression<Date> dateExpression){
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

    private static Predicate fromRelative(DateComparisonNode dateComparisonNode, DateExpression<Date> dateExpression){
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

    private static Predicate fromAbsolute(DateComparisonNode dateComparisonNode, DateExpression<Date> dateExpression){
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

    private static Date fromRelativeDate(DateComparisonNode dateComparisonNode){
        if(dateComparisonNode.getRelativePeriod() == null){
            return fromLocalDate(LocalDate.now().minusDays(0));
        }
        switch(dateComparisonNode.getRelativePeriod()){
            case HOUR:
                return fromLocalDateTime(LocalDateTime.now().minusHours(dateComparisonNode.getRelativeValue()));
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

    private static List<Date> fromRelativeDateDays(DateComparisonNode dateComparisonNode){
        if(dateComparisonNode.getRelativeDays() == null) return new ArrayList<>();

        return dateComparisonNode.getRelativeDays().getDaysOfWeek().stream()
                .map(dayOfWeek -> fromLocalDate(LocalDate.now().minusWeeks(dateComparisonNode.getRelativeValue()).with(dayOfWeek)))
                .collect(Collectors.toList());
    }

    private static Date fromLocalDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static Date fromLocalDateTime(LocalDateTime date){
        return Date.from(date.toInstant(ZoneOffset.UTC));
    }

    private static Predicate negateIfNeeded(Predicate predicate, ComparisonNode node) {
        return node.isNegate() ? predicate.not() : predicate;
    }

    private static Predicate negateIfNeeded(Predicate predicate, ComparisonAggregateNode<?> node) {
        return node.isNegate() ? predicate.not() : predicate;
    }
}
