package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RelatedRelationshipNode extends BaseRelationshipNode {

    private String field;
    private boolean negate = false;

    private LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
    private LogicalAggregateNode aggregations = new LogicalAggregateNode(LogicalAggregateNode.LogicalAggregateOperation.AND);

    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}
