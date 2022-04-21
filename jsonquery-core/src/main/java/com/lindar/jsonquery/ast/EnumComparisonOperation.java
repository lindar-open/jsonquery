package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 26/09/2016.
 */
public enum EnumComparisonOperation implements ComparisonOperation {
    EQUALS(1),
    EMPTY(0),
    IN(1, 99);

    private final int maxArgumentCount;
    private final int minArgumentCount;

    EnumComparisonOperation(int argumentCount) {
        this(argumentCount, argumentCount);
    }

    EnumComparisonOperation(int minArgumentCount, int maxArgumentCount) {
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
