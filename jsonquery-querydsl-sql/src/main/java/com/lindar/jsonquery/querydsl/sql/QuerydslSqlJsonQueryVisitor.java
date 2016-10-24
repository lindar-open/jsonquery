package com.lindar.jsonquery.querydsl.sql;

import com.google.common.base.CaseFormat;
import com.lindar.jsonquery.ast.LookupComparisonNode;
import com.lindar.jsonquery.querydsl.QuerydslJsonQueryVisitor;
import com.lindar.jsonquery.relationships.ast.RelatedRelationshipNode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLQuery;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.regex.Pattern;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class QuerydslSqlJsonQueryVisitor extends QuerydslJsonQueryVisitor {

    protected static final Pattern DOT = Pattern.compile("\\.");

    protected SQLQuery query;

    private QuerydslSqlSpec querydslSqlSpec;

    public QuerydslSqlJsonQueryVisitor(SQLQuery query,
                                       QuerydslSqlSpec querydslSqlSpec){
        this.query = query;
        this.querydslSqlSpec = querydslSqlSpec;
    }

    @Override
    public Predicate visit(RelatedRelationshipNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        QuerydslSqlSpec.RelationshipSpec relationshipSpec = querydslSqlSpec.getRelationshipSpecs().get(QuerydslSqlSpec.Key.builder().parentClass(entity.getType()).path(node.getField()).build());

        if(relationshipSpec == null){
            throw new IllegalArgumentException("No relationship found");
        }

        PathBuilder subqueryEntity = new PathBuilder(relationshipSpec.getRelationshipEntityClass(), relationshipSpec.getRelationshipEntity().getMetadata());

        PathBuilder subqueryKey = subqueryEntity.get(relationshipSpec.getForeignKey());

        Predicate conditionsPredicate = visit(node.getConditions(), subqueryEntity);
        Predicate havingPredicate = visit(node.getAggregations(), subqueryEntity);


        if(Util.isPredicateEmpty(conditionsPredicate) && Util.isPredicateEmpty(havingPredicate)){
            return new BooleanBuilder();
        }

        SQLQuery subquery = new SQLQuery();
        subquery.select(subqueryKey)
            .from(subqueryEntity)
            .where(conditionsPredicate)
            .groupBy(subqueryKey)
            .having(havingPredicate);

        return entity.get(relationshipSpec.getPrimaryKey()).in(subquery);
    }

    @Override
    public Predicate visit(LookupComparisonNode node, PathBuilder entity) {
        if(!node.isEnabled()) return new BooleanBuilder();

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), entity);

        NumberExpression longPath = pathJoin.getValue().getNumber(pathJoin.getKey(), Long.class);

        Long singleValue = 0L;
        if(node.getValue() != null && !node.getValue().isEmpty()){
            singleValue = node.getValue().get(0);
        }

        Predicate predicate = null;
        switch(node.getOperation()){

            case EQUALS:
                predicate = longPath.eq(singleValue);
                break;
            case EMPTY:
                predicate = longPath.isNull();
                break;
            case IN:
                predicate = longPath.in(node.getValue());
                break;
            default:
                throw new IllegalArgumentException("Unsupported Enum operator " + node.getOperation());
        }

        return predicate;
    }


    protected ImmutablePair<String, PathBuilder> processPath(String field, PathBuilder entity){
        QuerydslSqlSpec.Key specKey = QuerydslSqlSpec.Key.builder()
            .parentClass(entity.getType())
            .path(field)
            .build();

        QuerydslSqlSpec.JoinSpec joinSpec = querydslSqlSpec.getJoinSpecs().get(specKey);

        if(joinSpec == null){
            return ImmutablePair.of(querydslSqlSpec.getFieldMappings().getOrDefault(specKey, fieldToSql(field)), entity);
        }

        for(QuerydslSqlSpec.Join join : joinSpec.getJoins()){
            entity = new PathBuilder(join.getJoinEntity().getType(), join.getJoinEntity().getMetadata());
            query.leftJoin(entity).on(join.getForeignKey().on(join.getJoinEntity()));
            //ProjectableSQLQuery projectableSQLQuery = query.leftJoin(join.getForeignKey(), join.getJoinEntity());
        }

        return ImmutablePair.of(joinSpec.getField(), entity);
    }

    private String fieldToSql(String field){
        String[] split = DOT.split(field);
        StringBuilder sb = new StringBuilder();

        for(String part : split){
            if(sb.length() > 0){
                sb.append("_").append(part);
            } else {
                sb.append(part);
            }
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sb.toString());
    }
}
