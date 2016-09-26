package com.lindar.jsonquery.relationships.ast;

import lombok.Data;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public abstract class BasicComparisonAggregateNode<E, A extends Enum> extends ComparisonAggregateNode<E> {
    private AggregateComparisonOperation operation;
    private A aggregateOperation;

}
