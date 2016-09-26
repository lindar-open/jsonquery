package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class StringComparisonNode extends BasicComparisonNode<String, StringComparisonOperation> {

    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

}
