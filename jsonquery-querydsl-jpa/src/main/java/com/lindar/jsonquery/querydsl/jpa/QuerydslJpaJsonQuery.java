package com.lindar.jsonquery.querydsl.jpa;

import com.lindar.jsonquery.JsonQuery;
import com.lindar.jsonquery.ast.Node;
import com.lindar.jsonquery.relationships.JsonQueryWithRelationships;
import com.lindar.jsonquery.relationships.ast.RelationshipNode;
import com.mysema.commons.lang.Assert;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.experimental.UtilityClass;

/**
 * Created by stevenhills on 24/09/2016.
 */
@UtilityClass
public class QuerydslJpaJsonQuery {

    public static void applyPredicateAsSubquery(EntityPathBase joinEntity, BooleanBuilder applyTo, PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        applyTo.and(toPredicateAsSubquery(joinEntity, entity, jsonQueryWithRelationships));
    }

    public static void applyPredicateAsSubquery(EntityPathBase joinEntity, BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery){
        applyTo.and(toPredicateAsSubquery(joinEntity, entity, jsonQuery));
    }

    public static void applyPredicateAsSubquery(BooleanBuilder applyTo, PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        applyTo.and(toPredicateAsSubquery(entity, jsonQueryWithRelationships));
    }

    public static void applyPredicateAsSubquery(BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery){
        applyTo.and(toPredicateAsSubquery(entity, jsonQuery));
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, JsonQuery jsonQuery){
        return toPredicateAsSubquery(entity, entity, jsonQuery);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        return toPredicateAsSubquery(entity, entity, jsonQueryWithRelationships);
    }

    public static Predicate toPredicateAsSubquery(EntityPathBase joinEntity, PathBuilder entity, JsonQuery jsonQuery){
        JPAQuery subquery = new JPAQuery();
        subquery.select(entity).from(entity);
        Predicate predicate = toPredicate(subquery, entity, jsonQuery);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.in(subquery);
    }

    public static Predicate toPredicateAsSubquery(EntityPathBase joinEntity, PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        JPAQuery subquery = new JPAQuery();
        subquery.select(entity).from(entity);
        Predicate predicate = toPredicate(subquery, entity, jsonQueryWithRelationships);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.in(subquery);
    }


    public static Predicate toPredicate(JPAQuery jpaQuery, PathBuilder entity, JsonQuery jsonQuery){
        Assert.notNull(jpaQuery, "JPAQuery cannot be null");
        Assert.notNull(jpaQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(jpaQuery);
        return jsonQuery.getConditions().accept(visitor, entity);
    }

    public static Predicate toPredicate(JPAQuery jpaQuery, PathBuilder entity, JsonQueryWithRelationships jsonQueryWithRelationships){
        Assert.notNull(jpaQuery, "JPAQuery cannot be null");
        Assert.notNull(jpaQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(jpaQuery);
        Predicate conditionsPredicate = jsonQueryWithRelationships.getConditions().accept(visitor, entity);
        Predicate relationshipsPredicate = jsonQueryWithRelationships.getRelationships().accept(visitor, entity);

        return ExpressionUtils.allOf(conditionsPredicate, relationshipsPredicate);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, Node node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.accept(visitor, entity);
        applyTo.and(predicate);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, RelationshipNode node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.accept(visitor, entity);
        applyTo.and(predicate);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, JsonQueryWithRelationships node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.getRelationships().accept(visitor, entity);
        applyTo.and(predicate);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, JsonQuery node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.getConditions().accept(visitor, entity);
        applyTo.and(predicate);
    }


}
