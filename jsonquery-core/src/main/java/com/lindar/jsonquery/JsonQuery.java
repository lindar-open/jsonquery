package com.lindar.jsonquery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lindar.jsonquery.ast.LogicalNode;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonQuery implements Serializable{
    private LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
}
