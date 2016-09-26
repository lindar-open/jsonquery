package com.lindar.jsonquery.ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by stevenhills on 24/09/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public interface Node {
    <R, C> R accept(JsonQueryVisitor<R, C> v, C context);
    <R, C> R accept(JsonQueryVisitor<R, C> v);
}
