package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 26/09/2016.
 */
public enum StringComparisonOperation implements ComparisonOperation {
    EQUALS(1),
    CONTAINS(1),
    REGEX(1),
    BEGINS_WITH(1),
    ENDS_WITH(1),
    EMPTY(0),
    IN(1, 99);

    private final int maxArgumentCount;
    private final int minArgumentCount;

    StringComparisonOperation(int argumentCount) {
        this(argumentCount, argumentCount);
    }

    StringComparisonOperation(int minArgumentCount, int maxArgumentCount) {
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
