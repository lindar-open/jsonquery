package com.lindar.jsonquery.querydsl.jpa;

import com.lindar.jsonquery.JsonQuery;
import com.lindar.jsonquery.ast.Node;
import com.lindar.jsonquery.ast.RelationshipNode;
import com.lindar.jsonquery.querydsl.QuerydslQueryable;
import com.mysema.commons.lang.Assert;
import com.querydsl.core.BooleanBuilder;
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

    public static void applyPredicateAsSubquery(EntityPathBase joinEntity, BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery){
        applyTo.and(toPredicateAsSubquery(joinEntity, entity, jsonQuery));
    }


    public static void applyPredicateAsSubquery(BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery){
        applyTo.and(toPredicateAsSubquery(entity, jsonQuery));
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, JsonQuery jsonQuery){
        return toPredicateAsSubquery(entity, entity, jsonQuery);
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


    public static Predicate toPredicateAsSubquery(EntityPathBase joinEntity, PathBuilder entity, QuerydslQueryable queryable){
        JPAQuery subquery = new JPAQuery();
        subquery.select(entity).from(entity);
        Predicate predicate = toPredicate(subquery, entity, queryable);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.in(subquery);
    }

    public static Predicate toPredicateAsSubquery(EntityPathBase joinEntity, PathBuilder entity, Node node){
        JPAQuery subquery = new JPAQuery();
        subquery.select(entity).from(entity);
        Predicate predicate = toPredicate(subquery, entity, node);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.in(subquery);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, Node node){
        return toPredicateAsSubquery(entity, entity, node);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, QuerydslQueryable queryable){
        return toPredicateAsSubquery(entity, entity, queryable);
    }

    public static Predicate toPredicate(JPAQuery jpaQuery, PathBuilder entity, QuerydslQueryable queryable){
        Assert.notNull(jpaQuery, "JPAQuery cannot be null");
        Assert.notNull(jpaQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(jpaQuery);
        return queryable.getQueryableNode().accept(visitor, entity);
    }

    public static Predicate toPredicate(JPAQuery jpaQuery, PathBuilder entity, Node node){
        Assert.notNull(jpaQuery, "JPAQuery cannot be null");
        Assert.notNull(jpaQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(jpaQuery);
        return node.accept(visitor, entity);
    }

    public static Predicate toPredicate(JPAQuery jpaQuery, PathBuilder entity, JsonQuery jsonQuery){
        Assert.notNull(jpaQuery, "JPAQuery cannot be null");
        Assert.notNull(jpaQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(jpaQuery);
        return jsonQuery.getConditions().accept(visitor, entity);
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

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, JsonQuery node){
        QuerydslJpaJsonQueryVisitor visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        Predicate predicate = node.getConditions().accept(visitor, entity);
        applyTo.and(predicate);
    }
}
