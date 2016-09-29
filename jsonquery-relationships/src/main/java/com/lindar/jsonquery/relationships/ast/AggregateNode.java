package com.lindar.jsonquery.relationships.ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lindar.jsonquery.ast.JsonQueryConstants;

/**
 * Created by stevenhills on 24/09/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property=JsonQueryConstants.JSON_TYPE_PROPERTY)
public interface AggregateNode {
    <R, C> R accept(JsonQueryAggregateVisitor<R, C> v, C context);
    <R, C> R accept(JsonQueryAggregateVisitor<R, C> v);
}