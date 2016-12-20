package com.lindar.jsonquery.querydsl.jpa;

import com.google.common.collect.HashBasedTable;
import com.lindar.jsonquery.ast.LookupComparisonNode;
import com.lindar.jsonquery.querydsl.QuerydslJsonQueryVisitor;
import com.lindar.jsonquery.relationships.ast.RelatedRelationshipNode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderValidator;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Created by stevenhills on 24/09/2016.
 */
public class QuerydslJpaJsonQueryVisitor extends QuerydslJsonQueryVisitor {

    protected static final Pattern DOT = Pattern.compile("\\.");

    protected HashBasedTable<PathBuilder, String, PathBuilder> joins = HashBasedTable.create();
    protected JPAQuery query;
    protected Stack<JPAQuery> queryStack = new Stack<>();

    public QuerydslJpaJsonQueryVisitor(JPAQuery query){
        this.query = query;
    }

    private Predicate manyLookupVisit(LookupComparisonNode node, PathBuilder context) {
        String[] fieldParts = DOT.split(node.getField());

        Field field = FieldUtils.getField(context.getType(), fieldParts[0], true);

        if(field == null){
            throw new IllegalArgumentException();
        }

        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if(manyToMany == null){
            throw new IllegalArgumentException();

        }
        Class<?> relatedClass = PathBuilderValidator.FIELDS.validate(context.getType(), fieldParts[0], Object.class);

        String primaryKey;
        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(context.getType(), Id.class);
        if(fieldsListWithAnnotation.isEmpty()){
            primaryKey = "id";
        } else {
            primaryKey = fieldsListWithAnnotation.get(0).getName();
        }

        PathMetadata pathMetadata = PathMetadataFactory.forCollectionAny(context.getSet(fieldParts[0], Object.class));
        if(node.isNegate()){
            return new PathBuilder(relatedClass, pathMetadata).get(primaryKey).in(node.getValue()).not();
        }
        return new PathBuilder(relatedClass, pathMetadata).get(primaryKey).in(node.getValue());
    }

    @Override
    public Predicate visit(LookupComparisonNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        String[] fieldParts = DOT.split(node.getField());
        Field field = FieldUtils.getField(context.getType(), fieldParts[0], true);
        if(field != null && field.isAnnotationPresent(ManyToMany.class)){
            return manyLookupVisit(node, context);
        }

        ImmutablePair<String, PathBuilder> pathJoin = processPath(node.getField(), context);

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

        if(node.isNegate()){
            return predicate.not();
        }

        return predicate;
    }

    @Override
    public Predicate visit(RelatedRelationshipNode node, PathBuilder context) {
        if(!node.isEnabled()) return new BooleanBuilder();

        JPAQuery subquery = new JPAQuery<>();

        String primaryKey;
        String foreignKey;
        Class<?> relatedClass = PathBuilderValidator.FIELDS.validate(context.getType(), node.getField(), Object.class);

        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(context.getType(), Id.class);
        if(fieldsListWithAnnotation.isEmpty()){
            primaryKey = "id";
        } else {
            primaryKey = fieldsListWithAnnotation.get(0).getName();
        }

        Field relationshipField = FieldUtils.getField(context.getType(), node.getField(), true);
        if(relationshipField == null){
            throw new IllegalArgumentException();
        }

        if(relationshipField.isAnnotationPresent(OneToMany.class)){
            OneToMany annotation = relationshipField.getAnnotation(OneToMany.class);
            foreignKey = annotation.mappedBy();
        } else {
            String className = context.getType().getSimpleName();
            className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
            if(FieldUtils.getField(relatedClass, className) == null){
                throw new IllegalArgumentException();
            }
            foreignKey = className;
        }

        PathBuilder subqueryEntity = new PathBuilder(relatedClass, relatedClass.getSimpleName());

        PathBuilder subqueryKey = subqueryEntity.get(foreignKey).get(primaryKey);


        subquery.select(subqueryKey)
            .from(subqueryEntity);

        queryStack.push(query);
        query = subquery;

        Predicate conditionsPredicate = visit(node.getConditions(), subqueryEntity);
        Predicate havingPredicate = visit(node.getAggregations(), subqueryEntity);

        query = queryStack.pop();
        if((conditionsPredicate == null || "".equals(conditionsPredicate.toString())) && (havingPredicate == null || "".equals(havingPredicate.toString()))){
            return new BooleanBuilder();
        }


        subquery.where(conditionsPredicate)
            .groupBy(subqueryKey)
            .having(havingPredicate);

        if(node.isNegate()){
            return context.in(subquery).not();
        } else {
            return context.in(subquery);
        }
    }


    @Override
    protected ImmutablePair<String, PathBuilder> processPath(String field, PathBuilder entity) {
        String[] fieldParts = DOT.split(field);

        // join needed
        if(fieldParts.length > 1){
            String[] parent = new String[fieldParts.length - 1];
            System.arraycopy(fieldParts, 0, parent, 0, fieldParts.length - 1);

            PathBuilder<?> parentAlias = doJoin(entity, StringUtils.join(parent, "."), "");
            return ImmutablePair.of(fieldParts[fieldParts.length - 1], parentAlias);
        }

        // not needed
        return ImmutablePair.of(field, entity);
    }

    private PathBuilder<?> doJoin (
        PathBuilder<?> entity,
        String path,
        String reference) {

        String safeReference = toSafeReference(reference);

        if (path.contains(".")) {
            String[] tokens = DOT.split(path);
            safeReference = (StringUtils.join(tokens, "_") + reference);
        } else {
            safeReference = path + safeReference;
        }

        String mapReference = safeReference;
        PathBuilder<?> rv = joins.get(entity, mapReference);

        if (rv == null) {
            if (path.contains(".")) {
                String[] tokens = DOT.split(path);
                String[] parent = new String[tokens.length - 1];
                System.arraycopy(tokens, 0, parent, 0, tokens.length - 1);
                String parentKey = StringUtils.join(parent, ".");
                entity = doJoin(entity, parentKey, safeReference);
                rv = new PathBuilder(Object.class, safeReference);
                query.leftJoin((EntityPath)entity.get(tokens[tokens.length - 1]), rv);
            } else {
                rv = new PathBuilder(Object.class, safeReference);
                query.leftJoin((EntityPath)entity.get(path), rv);
            }
            joins.put(entity, mapReference, rv);
        }
        return rv;
    }

    private String toSafeReference(String reference){
        return reference.replace("-", "_");
    }

}
