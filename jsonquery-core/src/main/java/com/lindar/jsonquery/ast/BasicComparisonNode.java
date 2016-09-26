package com.lindar.jsonquery.ast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public abstract class BasicComparisonNode<E, T extends ComparisonOperation> extends ComparisonNode {
    private T operation;
    private List<E> value = new ArrayList<E>();
}
