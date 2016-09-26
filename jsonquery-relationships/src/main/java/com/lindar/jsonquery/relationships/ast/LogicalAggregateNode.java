package com.lindar.jsonquery.relationships.ast;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@RequiredArgsConstructor
public class LogicalAggregateNode extends BaseAggregateNode {

    private final LogicalAggregateOperation operation;
    private List<AggregateNode> items = new ArrayList<AggregateNode>();

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
