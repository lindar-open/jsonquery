package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public interface JsonQueryAggregateVisitor<R, C> {
    R visit(StringComparisonAggregateNode stringComparisonAggregateNode, C context);

    R visit(LogicalAggregateNode logicalAggregateNode, C context);

    R visit(BigDecimalComparisonAggregateNode bigDecimalComparisonAggregateNode, C context);

    R visit(EnumComparisonAggregateNode enumComparisonAggregateNode, C context);
}
