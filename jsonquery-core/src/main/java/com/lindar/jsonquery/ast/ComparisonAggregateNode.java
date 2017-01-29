package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ComparisonAggregateNode<E> extends BaseAggregateNode {

    private boolean negate = false;
    private String field;
    private List<E> value = new ArrayList<E>();
}
