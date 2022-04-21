package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.DateLocalDateComparisonNode;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class QuerydslLocalDateCalculator extends BaseQuerydslDateCalculator<LocalDate, DateLocalDateComparisonNode> {

    public Predicate fromRelative(DateLocalDateComparisonNode dateComparisonNode, DateExpression<LocalDate> dateExpression) {

        switch (dateComparisonNode.getRelativeOperation()) {
            case IN_THE_LAST:
                return dateExpression.gt(fromRelativeDate(dateComparisonNode));
            case MORE_THAN:
                return dateExpression.lt(fromRelativeDate(dateComparisonNode));
            case IS:
                return dateExpression.eq(fromRelativeDate(dateComparisonNode));
            case DAY:
                return dateExpression.in(DateUtils.fromRelativeDateDays(dateComparisonNode));
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    public Predicate fromPreset(DateLocalDateComparisonNode dateComparisonNode, DateExpression<LocalDate> dateExpression) {
        switch (dateComparisonNode.getPresetOperation()) {
            case TODAY:
                return dateExpression.eq(LocalDate.now());
            case YESTERDAY:
                return dateExpression.eq(LocalDate.now().minusDays(1));
            case CURRENT_WEEK:
                return dateExpression.goe(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 1))
                        .and(dateExpression.loe(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 7)));
            case LAST_WEEK:
                return dateExpression.goe(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 1))
                        .and(dateExpression.loe(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 7)));
            case CURRENT_MONTH:
                return dateExpression.goe(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))
                        .and(dateExpression.loe(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())));
            case LAST_MONTH:
                return dateExpression.goe(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()))
                        .and(dateExpression.loe(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())));
            case CURRENT_YEAR:
                return dateExpression.goe(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))
                        .and(dateExpression.loe(LocalDate.now().with(TemporalAdjusters.lastDayOfYear())));
            case LAST_YEAR:
                return dateExpression.goe(LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()))
                        .and(dateExpression.loe(LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear())));
            case ANNIVERSARY:
                return dateExpression.month().eq(LocalDate.now().getMonthValue()).and(dateExpression.dayOfMonth().eq(LocalDate.now().getDayOfMonth()));
        }
        throw new IllegalArgumentException("Date operation not supported");
    }

    public Predicate fromAbsolute(DateLocalDateComparisonNode dateComparisonNode, DateExpression<LocalDate> dateExpression) {
        LocalDate startDate = dateComparisonNode.getDateValue().get(0);
        LocalDate endDate = LocalDate.now();
        if (dateComparisonNode.getDateValue().size() > 1) {
            endDate = dateComparisonNode.getDateValue().get(1);
        }
        switch (dateComparisonNode.getDateOperation()) {
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
        throw new IllegalArgumentException("Date operation not supported " + dateComparisonNode.getDateOperation());
    }

    private LocalDate fromRelativeDate(DateLocalDateComparisonNode dateComparisonNode) {
        if (dateComparisonNode.getRelativePeriod() == null) {
            return LocalDate.now().minusDays(0);
        }
        switch (dateComparisonNode.getRelativePeriod()) {
            case DAY:
                return LocalDate.now().minusDays(dateComparisonNode.getRelativeValue());
            case WEEK:
                return LocalDate.now().minusWeeks(dateComparisonNode.getRelativeValue());
            case MONTH:
                return LocalDate.now().minusMonths(dateComparisonNode.getRelativeValue());
            case YEAR:
                return LocalDate.now().minusYears(dateComparisonNode.getRelativeValue());
        }
        throw new IllegalArgumentException("Date operation [" + dateComparisonNode.getRelativePeriod() + "] not supported for LocalDate");
    }
}
