package com.lindar.jsonquery.relationships;

import com.lindar.jsonquery.JsonQuery;
import com.lindar.jsonquery.relationships.ast.LogicalRelationshipNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JsonQueryWithRelationships extends JsonQuery {
    private LogicalRelationshipNode relationships = new LogicalRelationshipNode(LogicalRelationshipNode.LogicalOperation.AND);
}
