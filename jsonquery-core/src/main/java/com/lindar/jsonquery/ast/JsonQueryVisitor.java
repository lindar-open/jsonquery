package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public interface JsonQueryVisitor<R, C>  {
    R visit(StringComparisonNode stringComparisonNode, C context);
    R visit(LogicalNode logicalNode, C context);
    R visit(BigDecimalComparisonNode bigDecimalComparisonNode, C context);
    R visit(DateComparisonNode dateComparisonNode, C context);
    R visit(DateInstantComparisonNode dateComparisonNode, C context);

    R visit(DateLocalDateComparisonNode node, C context);

    R visit(BooleanComparisonNode booleanComparisonNode, C context);

    R visit(EnumComparisonNode enumComparisonNode, C context);

    R visit(LookupComparisonNode lookupComparisonNode, C context);

    R visit(LogicalRelationshipNode logicalRelationshipNode, C context);
    R visit(RelatedRelationshipNode relatedRelationshipNode, C context);
    R visit(StringComparisonAggregateNode stringComparisonAggregateNode, C context);
    R visit(LogicalAggregateNode logicalAggregateNode, C context);
    R visit(BigDecimalComparisonAggregateNode bigDecimalComparisonAggregateNode, C context);
    R visit(EnumComparisonAggregateNode enumComparisonAggregateNode, C context);

}
