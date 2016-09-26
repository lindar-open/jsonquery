package com.lindar.jsonquery.relationships.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public interface JsonQueryAggregateVisitor<R, C> {
    R visit(LogicalRelationshipNode logicalRelationshipNode, C context);
    R visit(RelatedRelationshipNode relatedRelationshipNode, C context);


    R visit(StringComparisonAggregateNode stringComparisonAggregateNode, C context);

    R visit(LogicalAggregateNode logicalAggregateNode, C context);

    R visit(BigDecimalComparisonAggregateNode bigDecimalComparisonAggregateNode, C context);
}
