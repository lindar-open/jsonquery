package com.lindar.jsonquery.relationships.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class StringComparisonAggregateNode extends BasicComparisonAggregateNode<Integer, StringComparisonAggregateNode.StringAggregateOperation> {

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }


    public enum StringAggregateOperation {
        COUNT, COUNT_DISTINCT
    }

}
