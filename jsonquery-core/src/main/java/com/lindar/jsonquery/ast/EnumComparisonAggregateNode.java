package com.lindar.jsonquery.ast;

import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@EqualsAndHashCode(callSuper = true)
public class EnumComparisonAggregateNode extends BasicComparisonAggregateNode<Integer, EnumAggregateOperation> {

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }


}
