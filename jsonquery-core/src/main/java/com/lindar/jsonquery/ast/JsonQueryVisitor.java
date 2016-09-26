package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public interface JsonQueryVisitor<R, C>  {
    R visit(StringComparisonNode stringComparisonNode, C context);
    R visit(LogicalNode logicalNode, C context);
    R visit(BigDecimalComparisonNode bigDecimalComparisonNode, C context);
    R visit(DateComparisonNode dateComparisonNode, C context);

    R visit(BooleanComparisonNode booleanComparisonNode, C context);

    R visit(EnumComparisonNode enumComparisonNode, C context);

    R visit(LookupComparisonNode lookupComparisonNode, C context);
}
