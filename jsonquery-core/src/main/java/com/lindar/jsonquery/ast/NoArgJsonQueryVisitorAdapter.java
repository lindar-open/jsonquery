package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public abstract class NoArgJsonQueryVisitorAdapter<R> implements JsonQueryVisitor<R, Void> {

    public abstract R visit(LogicalNode logicalNode);
    public abstract R visit(StringComparisonNode andNode);

    public R visit(LogicalNode logicalNode, Void context) {
        return visit(logicalNode);
    }

    public R visit(StringComparisonNode stringComparisonNode, Void context) {
        return visit(stringComparisonNode);
    }
}
