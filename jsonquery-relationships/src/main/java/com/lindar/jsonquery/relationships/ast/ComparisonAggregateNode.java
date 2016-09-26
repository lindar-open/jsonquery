package com.lindar.jsonquery.relationships.ast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public abstract class ComparisonAggregateNode<E> extends BaseAggregateNode {

    private boolean negate = false;
    private String field;
    private List<E> value = new ArrayList<E>();
}
