package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.BaseDateComparisonNode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateExpression;

public abstract class BaseQuerydslDateCalculator<T extends Comparable, N extends BaseDateComparisonNode<T>> implements QuerydslDateCalculator<T, N> {


    @Override
    public Predicate toPredicate(N node, DateExpression<T> dateExpression) {
        if (!node.isEnabled()) return new BooleanBuilder();


        Predicate predicate = new BooleanBuilder();
        switch (node.getOperation()) {

            case RELATIVE:
                predicate = fromRelative(node, dateExpression);
                break;
            case ABSOLUTE:
                predicate = fromAbsolute(node, dateExpression);
                break;
            case PRESET:
                predicate = fromPreset(node, dateExpression);
                break;
        }

        if (node.isNegate()) {
            return predicate.not();
        }

        return predicate;
    }


    protected abstract Predicate fromRelative(N dateComparisonNode, DateExpression<T> dateExpression);

    protected abstract Predicate fromAbsolute(N dateComparisonNode, DateExpression<T> dateExpression);

    protected abstract Predicate fromPreset(N dateComparisonNode, DateExpression<T> dateExpression);
}
