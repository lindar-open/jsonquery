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
public class LogicalRelationshipNode extends BaseRelationshipNode {

    private final LogicalOperation operation;
    private List<RelatedRelationshipNode> items = new ArrayList<>();

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public enum LogicalOperation {
        AND, OR
    }
}
