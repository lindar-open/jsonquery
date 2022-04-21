package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 26/09/2016.
 */
public enum NumberComparisonOperation implements ComparisonOperation {
    EQUALS(1),
    GREATER_THAN(1),
    LESS_THAN(1),
    GREATER_THAN_OR_EQUAL(1),
    LESS_THAN_OR_EQUAL(1),
    BETWEEN(2),
    EMPTY(0),
    IN(-1);

    private final int maxArgumentCount;
    private final int minArgumentCount;

    NumberComparisonOperation(int argumentCount) {
        this.maxArgumentCount = argumentCount;
        this.minArgumentCount = argumentCount;
    }

    NumberComparisonOperation(int minArgumentCount, int maxArgumentCount) {
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
