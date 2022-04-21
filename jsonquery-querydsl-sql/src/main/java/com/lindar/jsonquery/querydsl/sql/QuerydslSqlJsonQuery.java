package com.lindar.jsonquery.querydsl.sql;

import com.lindar.jsonquery.JsonQuery;
import com.lindar.jsonquery.ast.Node;
import com.lindar.jsonquery.ast.RelationshipNode;
import com.lindar.jsonquery.querydsl.QuerydslQueryable;
import com.mysema.commons.lang.Assert;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLQuery;
import lombok.experimental.UtilityClass;

/**
 * Created by stevenhills on 24/09/2016.
 */
@UtilityClass
public class QuerydslSqlJsonQuery {


    public static void applyPredicateAsSubquery(PathBuilder joinEntity, BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery, QuerydslSqlSpec querydslSqlSpec){
        applyTo.and(toPredicateAsSubquery(joinEntity, entity, jsonQuery, querydslSqlSpec));
    }

    public static void applyPredicateAsSubquery(BooleanBuilder applyTo, PathBuilder entity, JsonQuery jsonQuery, QuerydslSqlSpec querydslSqlSpec){
        applyTo.and(toPredicateAsSubquery(entity, jsonQuery, querydslSqlSpec));
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, JsonQuery jsonQuery, QuerydslSqlSpec querydslSqlSpec){
        return toPredicateAsSubquery(entity, entity, jsonQuery, querydslSqlSpec);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder joinEntity, PathBuilder entity, JsonQuery jsonQuery, QuerydslSqlSpec querydslSqlSpec){
        SQLQuery subquery = new SQLQuery();
        subquery.select(entity.get("id")).from(entity);
        Predicate predicate = toPredicate(subquery, entity, jsonQuery, querydslSqlSpec);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.get("id").in(subquery);
    }

    public static Predicate toPredicate(SQLQuery sqlQuery, PathBuilder entity, JsonQuery jsonQuery, QuerydslSqlSpec querydslSqlSpec){
        Assert.notNull(sqlQuery, "SQLQuery cannot be null");
        Assert.notNull(sqlQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslSqlJsonQueryVisitor visitor = new QuerydslSqlJsonQueryVisitor(sqlQuery, querydslSqlSpec);
        return jsonQuery.getConditions().accept(visitor, entity);
    }

    public static Predicate toPredicate(SQLQuery sqlQuery, PathBuilder entity, QuerydslQueryable queryable, QuerydslSqlSpec querydslSqlSpec){
        Assert.notNull(sqlQuery, "SQLQuery cannot be null");
        Assert.notNull(sqlQuery.getMetadata().getProjection(), "Query Projection must be set before predicate");

        QuerydslSqlJsonQueryVisitor visitor = new QuerydslSqlJsonQueryVisitor(sqlQuery, querydslSqlSpec);
        return queryable.getQueryableNode().accept(visitor, entity);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, Node node, QuerydslSqlSpec querydslSqlSpec){
        QuerydslSqlJsonQueryVisitor visitor = new QuerydslSqlJsonQueryVisitor(new SQLQuery(), querydslSqlSpec);
        Predicate predicate = node.accept(visitor, entity);
        applyTo.and(predicate);
    }

    public static void applyPredicate(BooleanBuilder applyTo, PathBuilder entity, RelationshipNode node, QuerydslSqlSpec querydslSqlSpec){
        QuerydslSqlJsonQueryVisitor visitor = new QuerydslSqlJsonQueryVisitor(new SQLQuery(), querydslSqlSpec);
        Predicate predicate = node.accept(visitor, entity);
        applyTo.and(predicate);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder entity, QuerydslQueryable queryable, QuerydslSqlSpec querydslSqlSpec){
        return toPredicateAsSubquery(entity, entity, queryable, querydslSqlSpec);
    }

    public static Predicate toPredicateAsSubquery(PathBuilder joinEntity, PathBuilder entity, QuerydslQueryable queryable, QuerydslSqlSpec querydslSqlSpec){
        SQLQuery subquery = new SQLQuery();
        subquery.select(entity.get("id")).from(entity);
        Predicate predicate = toPredicate(subquery, entity, queryable, querydslSqlSpec);
        if(Util.isPredicateEmpty(predicate)){
            return new BooleanBuilder();
        }
        subquery.where(predicate);
        return joinEntity.get("id").in(subquery);
    }
}
