package com.lindar.jsonquery.ast;

import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@EqualsAndHashCode(callSuper = true)
public abstract class NumberComparisonNode<E extends Number & Comparable<E>> extends BasicComparisonNode<E, NumberComparisonOperation> {

}
