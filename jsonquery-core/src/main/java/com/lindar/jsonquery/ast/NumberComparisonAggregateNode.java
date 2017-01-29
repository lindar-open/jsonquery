package com.lindar.jsonquery.ast;

import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@EqualsAndHashCode(callSuper = true)
public abstract class NumberComparisonAggregateNode<E extends Number & Comparable<E>> extends BasicComparisonAggregateNode<E, NumberComparisonAggregateNode.NumberAggregateOperation> {

    public enum NumberAggregateOperation {
        SUM, COUNT, COUNT_DISTINCT, AVG, MAX, MIN
    }
}
