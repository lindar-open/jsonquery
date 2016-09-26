package com.lindar.jsonquery.querydsl.jpa;

import com.lindar.jsonquery.ast.Node;
import com.lindar.jsonquery.relationships.JsonQueryWithRelationships;
import com.lindar.jsonquery.relationships.ast.RelationshipNode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.experimental.UtilityClass;

/**
 * Created by stevenhills on 24/09/2016.
 */
@UtilityClass
public class QuerydslJpaJsonQuery {

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        JPAQuery subquery = new JPAQuery();
        subquery.select(entity).from(entity);
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(subquery);
        Predicate conditionsPredicate = jsonQueryWithRelationships.getConditions().accept(visitor, entity);
        Predicate relationshipsPredicate = jsonQueryWithRelationships.getRelationships().accept(visitor, entity);

        Predicate predicate = ExpressionUtils.allOf(conditionsPredicate, relationshipsPredicate);

        subquery.where(predicate);

        BooleanExpression in = entity.in(subquery);

        applyTo.and(in);
    }

    public static void applyPredicate(Predicate applyTo, PathBuilder entity, Node node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.accept(visitor, entity);
        ExpressionUtils.and(applyTo, predicate);
    }

    public static void applyPredicate(Predicate applyTo, PathBuilder entity, RelationshipNode node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.accept(visitor, entity);
        ExpressionUtils.and(applyTo, predicate);
    }
}
