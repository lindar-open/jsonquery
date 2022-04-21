package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LogicalAggregateNode extends BaseAggregateNode {

    private final LogicalAggregateOperation operation;
    private List<AggregateNode> items = new ArrayList<>();

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public <R, C> R accept(JsonQueryAggregateVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public enum LogicalAggregateOperation {
        AND, OR
    }
}
