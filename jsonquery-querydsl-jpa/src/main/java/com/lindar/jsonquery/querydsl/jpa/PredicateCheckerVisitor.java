package com.lindar.jsonquery.querydsl.jpa;

import com.querydsl.core.types.*;

/**
 * Created by Steven on 29/09/2016.
 */
public class PredicateCheckerVisitor implements Visitor<Boolean, Void> {

    private static final PredicateCheckerVisitor INSTANCE = new PredicateCheckerVisitor();

    public PredicateCheckerVisitor getInstance(){
        return INSTANCE;
    }

    @Override
    public Boolean visit(Constant<?> constant, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(FactoryExpression<?> factoryExpression, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(Operation<?> operation, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(ParamExpression<?> paramExpression, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(Path<?> path, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(SubQueryExpression<?> subQueryExpression, Void aVoid) {
        return null;
    }

    @Override
    public Boolean visit(TemplateExpression<?> templateExpression, Void aVoid) {
        return null;
    }
}
