package com.lindar.jsonquery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lindar.jsonquery.ast.LogicalNode;
import com.lindar.jsonquery.ast.StringComparisonNode;
import com.lindar.jsonquery.ast.Node;
import com.lindar.jsonquery.ast.StringComparisonOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by stevenhills on 24/09/2016.
 */
@RunWith(value = JUnit4.class)
public class TestJsonQuery {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException {

        StringComparisonNode stringNode = new StringComparisonNode();
        stringNode.setField("username");
        stringNode.setOperation(StringComparisonOperation.BEGINS_WITH);
        ArrayList<String> values = new ArrayList<>();
        values.add("random");
        stringNode.setValue(values);

        ArrayList<Node> nodes = Lists.newArrayList();
        nodes.add(stringNode);

        LogicalNode andNode = new LogicalNode(LogicalNode.LogicalOperation.AND);
        andNode.setItems(nodes);

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(andNode));

        BigDecimal bigDecimal;
    }
}
