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
public abstract class BasicComparisonNode<E, T extends ComparisonOperation> extends ComparisonNode {
    private T operation;
    private List<E> value = new ArrayList<>();
}
