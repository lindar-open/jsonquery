package com.lindar.jsonquery.ast;

import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Created by stevenhills on 25/09/2016.
 */
@EqualsAndHashCode(callSuper = true)
public class BigDecimalComparisonNode extends NumberComparisonNode<BigDecimal> {
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return null;
    }
}
