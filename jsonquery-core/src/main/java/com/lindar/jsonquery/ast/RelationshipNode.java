package com.lindar.jsonquery.ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lindar.jsonquery.ast.JsonQueryConstants;
import com.lindar.jsonquery.ast.Node;

/**
 * Created by stevenhills on 24/09/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property= JsonQueryConstants.JSON_TYPE_PROPERTY)
public interface RelationshipNode extends Node{
}