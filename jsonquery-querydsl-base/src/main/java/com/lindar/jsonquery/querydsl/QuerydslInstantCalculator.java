package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.BaseDateComparisonNode;
import com.lindar.jsonquery.ast.DateInstantComparisonNode;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.BooleanUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.stream.Collectors;

public class QuerydslInstantCalculator extends BaseQuerydslDateCalculator<Instant, DateInstantComparisonNode> {

    @Override
    protected Predicate fromRelative(DateInstantComparisonNode dateComparisonNode, DateExpression<Instant> dateExpression) {

        if (dateComparisonNode.getRelativePeriod() == BaseDateComparisonNode.RelativePeriod.MINUTE) {
            switch (dateComparisonNode.getRelativeOperation()) {
                case IN_THE_LAST:
                    return dateExpression.goe(Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.MINUTES));
                case MORE_THAN:
                    return dateExpression.lt(Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.MINUTES));
                case IS:
                    ZonedDateTime instant = Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.MINUTES).atZone(ZoneOffset.UTC);
                    return dateExpression.between(instant.withMinute(0).withSecond(0).withNano(0).toInstant(), instant.plusHours(1).withMinute(0).withSecond(0).withNano(0).toInstant());
            }
        } else if (dateComparisonNode.getRelativePeriod() == BaseDateComparisonNode.RelativePeriod.HOUR) {
            switch (dateComparisonNode.getRelativeOperation()) {
                case IN_THE_LAST:
                    return dateExpression.goe(Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.HOURS));
                case MORE_THAN:
                    return dateExpression.lt(Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.HOURS));
                case IS:
                    ZonedDateTime instant = Instant.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.HOURS).atZone(ZoneOffset.UTC);
                    return dateExpression.between(instant.withMinute(0).withSecond(0).withNano(0).toInstant(), instant.plusHours(1).withMinute(0).withSecond(0).withNano(0).toInstant());
            }
        } else {
            switch (dateComparisonNode.getRelativeOperation()) {
                case IN_THE_LAST:
                    return dateExpression.goe(fromRelativeDate(dateComparisonNode).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
                case MORE_THAN:
                    return dateExpression.lt(fromRelativeDate(dateComparisonNode).atStartOfDay(ZoneOffset.UTC).toInstant());
                case IS:
                    LocalDate date = fromRelativeDate(dateComparisonNode);
                    return dateExpression.between(date.atStartOfDay(ZoneOffset.UTC).toInstant(), date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
                case DAY:
                    return ExpressionUtils.anyOf(
                            DateUtils.fromRelativeDateDays(dateComparisonNode).stream()
                                    .map(period -> dateExpression.goe(period.atStartOfDay(ZoneOffset.UTC).toInstant())
                                            .and(dateExpression.lt(period.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant())))
                                    .collect(Collectors.toList())
                    );
            }
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    @Override
    protected Predicate fromPreset(DateInstantComparisonNode
                                           dateComparisonNode, DateExpression<Instant> dateExpression) {

        switch (dateComparisonNode.getPresetOperation()) {

            case TODAY:
                return dateExpression.goe(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)).and(dateExpression.lt(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
            case YESTERDAY:
                return dateExpression.goe(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)).and(dateExpression.lt(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)));
            case CURRENT_WEEK:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 1)))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().with(WeekFields.of(Locale.UK).dayOfWeek(), 7).plusDays(1))));
            case LAST_WEEK:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 1)))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().minusWeeks(1).with(WeekFields.of(Locale.UK).dayOfWeek(), 7).plusDays(1))));
            case CURRENT_MONTH:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1))));
            case LAST_MONTH:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1))));
            case CURRENT_YEAR:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).plusDays(1))));
            case LAST_YEAR:
                return dateExpression.goe(fromLocalDateInstant(LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear())))
                        .and(dateExpression.lt(fromLocalDateInstant(LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear()).plusDays(1))));
            case ANNIVERSARY:
                return dateExpression.month().eq(LocalDate.now().getMonthValue()).and(dateExpression.dayOfMonth().eq(LocalDate.now().getDayOfMonth()));
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    @Override
    protected Predicate fromAbsolute(DateInstantComparisonNode
                                             dateComparisonNode, DateExpression<Instant> dateExpression) {
        Instant startDate = dateComparisonNode.getDateValue().get(0);
        Instant endDate = Instant.now();
        if (dateComparisonNode.getDateValue().size() > 1) {
            endDate = dateComparisonNode.getDateValue().get(1);
        }

        if (BooleanUtils.isTrue(dateComparisonNode.getWithTime())) {
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
        } else {
            switch (dateComparisonNode.getDateOperation()) {
                case EQUALS:
                    return dateExpression.goe(startDate.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()).and(dateExpression.lt(startDate.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()));
                case GREATER_THAN:
                    return Expressions.booleanOperation(Ops.GOE, dateExpression, Expressions.constant(startDate.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()));
                case LESS_THAN:
                    return Expressions.booleanOperation(Ops.LT, dateExpression, Expressions.constant(startDate.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
                case GREATER_THAN_OR_EQUAL:
                    return Expressions.booleanOperation(Ops.GOE, dateExpression, Expressions.constant(startDate.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()));
                case LESS_THAN_OR_EQUAL:
                    return Expressions.booleanOperation(Ops.LT, dateExpression, Expressions.constant(startDate.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()));
                case BETWEEN:
                    return dateExpression.goe(startDate.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()).and(dateExpression.lt(endDate.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()));
            }
        }

        throw new IllegalArgumentException("Date operation not supported");
    }

    private LocalDate fromRelativeDate(DateInstantComparisonNode dateComparisonNode) {

        if (dateComparisonNode.getRelativePeriod() == null) {
            return LocalDate.now();
        }

        switch (dateComparisonNode.getRelativePeriod()) {
            case DAY:
                return LocalDate.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.DAYS);
            case WEEK:
                return LocalDate.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.WEEKS);
            case MONTH:
                return LocalDate.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.MONTHS);
            case YEAR:
                return LocalDate.now().minus(dateComparisonNode.getRelativeValue(), ChronoUnit.YEARS);
        }

        throw new IllegalArgumentException("Date operation [" + dateComparisonNode.getRelativePeriod() + "] not supported for LocalDate");
    }

    private Instant fromLocalDateInstant(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
