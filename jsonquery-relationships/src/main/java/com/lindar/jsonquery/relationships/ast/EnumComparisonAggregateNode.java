package com.lindar.jsonquery.relationships.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class EnumComparisonAggregateNode extends BasicComparisonAggregateNode<Integer, EnumAggregateOperation> {

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }


}
