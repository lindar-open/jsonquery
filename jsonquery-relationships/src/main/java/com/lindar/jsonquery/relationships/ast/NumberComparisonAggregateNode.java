package com.lindar.jsonquery.relationships.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public abstract class NumberComparisonAggregateNode<E extends Number & Comparable<E>> extends BasicComparisonAggregateNode<E, NumberComparisonAggregateNode.NumberAggregateOperation> {



    public enum NumberAggregateOperation {
        SUM, COUNT, COUNT_DISTINCT, AVG, MAX, MIN
    }
}
