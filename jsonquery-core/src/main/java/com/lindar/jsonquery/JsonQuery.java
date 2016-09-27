package com.lindar.jsonquery;

import com.lindar.jsonquery.ast.LogicalNode;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public class JsonQuery implements Serializable{
    private LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
}
