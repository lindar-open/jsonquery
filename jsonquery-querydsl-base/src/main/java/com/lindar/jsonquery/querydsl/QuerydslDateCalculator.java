package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.BaseDateComparisonNode;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateExpression;

public interface QuerydslDateCalculator<T extends Comparable, N extends BaseDateComparisonNode<T>> {
    Predicate toPredicate(N node, DateExpression<T> dateExpression);
}
