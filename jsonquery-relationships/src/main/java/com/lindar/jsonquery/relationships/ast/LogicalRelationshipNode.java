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
public class LogicalRelationshipNode extends BaseRelationshipNode {

    private final LogicalOperation operation;
    private List<RelatedRelationshipNode> items = new ArrayList<RelatedRelationshipNode>();

    public <R, C> R accept(JsonQueryRelationshipVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public <R, C> R accept(JsonQueryRelationshipVisitor<R, C> v) {
        return v.visit(this, null);
    }

    public enum LogicalOperation {
        AND, OR
    }
}
