package com.lindar.jsonquery.ast;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class JsonQueryVisitorAdapter<R, C> implements JsonQueryVisitor<R, C> {
    public R visit(LogicalNode logicalNode, C context) {
        return null;
    }

    public R visit(BigDecimalComparisonNode bigDecimalComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(DateComparisonNode dateComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(DateInstantComparisonNode dateComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(DateLocalDateComparisonNode node, C context) {
        return null;
    }

    @Override
    public R visit(BooleanComparisonNode booleanComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(EnumComparisonNode enumComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(LookupComparisonNode lookupComparisonNode, C context) {
        return null;
    }

    @Override
    public R visit(LogicalRelationshipNode logicalRelationshipNode, C context) {
        return null;
    }

    @Override
    public R visit(RelatedRelationshipNode relatedRelationshipNode, C context) {
        return null;
    }

    @Override
    public R visit(StringComparisonAggregateNode stringComparisonAggregateNode, C context) {
        return null;
    }

    @Override
    public R visit(LogicalAggregateNode logicalAggregateNode, C context) {
        return null;
    }

    @Override
    public R visit(BigDecimalComparisonAggregateNode bigDecimalComparisonAggregateNode, C context) {
        return null;
    }

    @Override
    public R visit(EnumComparisonAggregateNode enumComparisonAggregateNode, C context) {
        return null;
    }

    public R visit(NumberComparisonNode numberComparisonNode, C context) {
        return null;
    }

    public R visit(StringComparisonNode stringComparisonNode, C context) {
        return null;
    }

}
