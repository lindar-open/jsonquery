package com.lindar.jsonquery.relationships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lindar.jsonquery.ast.LogicalNode;
import com.lindar.jsonquery.ast.StringComparisonNode;
import com.lindar.jsonquery.ast.StringComparisonOperation;
import com.lindar.jsonquery.relationships.ast.LogicalRelationshipNode;
import com.lindar.jsonquery.relationships.ast.RelatedRelationshipNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

/**
 * Created by stevenhills on 24/09/2016.
 */

@RunWith(value = JUnit4.class)
public class TestJsonQueryRelationships {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException {

        StringComparisonNode stringNode = new StringComparisonNode();
        stringNode.setField("username");
        stringNode.setOperation(StringComparisonOperation.BEGINS_WITH);
        ArrayList<String> values = new ArrayList<String>();
        values.add("random");
        stringNode.setValue(values);

        RelatedRelationshipNode relatedRelationshipNode = new RelatedRelationshipNode();
        relatedRelationshipNode.setField("attrition");
        relatedRelationshipNode.getConditions().getItems().add(stringNode);

        Holder holder = new Holder();
        holder.conditions.getItems().add(stringNode);
        holder.relationships.getItems().add(relatedRelationshipNode);

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(holder));

    }


    public static class Holder {
        public LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
        public LogicalRelationshipNode relationships = new LogicalRelationshipNode(LogicalRelationshipNode.LogicalOperation.AND);
    }
}