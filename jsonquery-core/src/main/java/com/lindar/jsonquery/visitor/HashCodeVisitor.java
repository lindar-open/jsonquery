package com.lindar.jsonquery.visitor;

import com.lindar.jsonquery.ast.*;

/**
 * Created by Steven on 29/09/2016.
 */
public class HashCodeVisitor implements JsonQueryVisitor<Integer, Void> {

    public static final HashCodeVisitor DEFAULT = new HashCodeVisitor();

    private HashCodeVisitor() { }

    public Integer visit(BaseNode node) {
        return node.getReference().hashCode();
    }
    public Integer visit(Node node) {
        return node.hashCode();
    }


    @Override
    public Integer visit(StringComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(LogicalNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(BigDecimalComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(DateComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(BooleanComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(EnumComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(LookupComparisonNode node, Void context) {
        return visit((BaseNode) node);
    }

    @Override
    public Integer visit(LogicalRelationshipNode node, Void context) {
        return visit((Node) node);
    }

    @Override
    public Integer visit(RelatedRelationshipNode node, Void context) {
        return visit((Node) node);
    }

    @Override
    public Integer visit(StringComparisonAggregateNode node, Void context) {
        return visit((Node) node);
    }

    @Override
    public Integer visit(LogicalAggregateNode node, Void context) {
        return visit((Node) node);
    }

    @Override
    public Integer visit(BigDecimalComparisonAggregateNode node, Void context) {
        return visit((Node) node);
    }

    @Override
    public Integer visit(EnumComparisonAggregateNode node, Void context) {
        return visit((Node) node);
    }
}
