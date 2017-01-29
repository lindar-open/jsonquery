package com.lindar.jsonquery.ast;

import com.lindar.jsonquery.ast.ComparisonOperation;

/**
 * Created by stevenhills on 26/09/2016.
 */
public enum AggregateComparisonOperation implements ComparisonOperation {
    EQUALS(1),
    GREATER_THAN(1),
    LESS_THAN(1),
    GREATER_THAN_OR_EQUAL(1),
    LESS_THAN_OR_EQUAL(1),
    BETWEEN(2),
    EMPTY(0);

    private int maxArgumentCount;
    private int minArgumentCount;

    AggregateComparisonOperation(int argumentCount) {
        this.maxArgumentCount = argumentCount;
        this.minArgumentCount = argumentCount;
    }

    AggregateComparisonOperation(int minArgumentCount, int maxArgumentCount) {
        this.minArgumentCount = minArgumentCount;
        this.maxArgumentCount = maxArgumentCount;
    }

    public int getMinArgumentCount() {
        return minArgumentCount;
    }

    public int getMaxArgumentCount() {
        return maxArgumentCount;
    }

}
