package com.lindar.jsonquery.ast;

import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@EqualsAndHashCode(callSuper = true)
public class StringComparisonNode extends BasicComparisonNode<String, StringComparisonOperation> {

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}
