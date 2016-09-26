package com.lindar.jsonquery.relationships;

import com.lindar.jsonquery.ast.LogicalNode;
import com.lindar.jsonquery.relationships.ast.LogicalRelationshipNode;
import lombok.Data;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public class JsonQueryWithRelationships {
    private LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
    private LogicalRelationshipNode relationships = new LogicalRelationshipNode(LogicalRelationshipNode.LogicalOperation.AND);
}
