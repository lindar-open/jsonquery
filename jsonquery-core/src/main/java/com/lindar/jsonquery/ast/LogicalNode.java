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
public class LogicalNode extends BaseNode {

    private final LogicalOperation operation;
    private List<Node> items = new ArrayList<Node>();

    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public enum LogicalOperation {
        AND, OR
    }
}
