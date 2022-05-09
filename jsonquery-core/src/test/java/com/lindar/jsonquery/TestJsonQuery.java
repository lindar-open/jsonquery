package com.lindar.jsonquery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lindar.jsonquery.ast.LogicalNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by stevenhills on 24/09/2016.
 */
@RunWith(value = JUnit4.class)
public class TestJsonQuery {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void writeAndReadEmptyQuery() throws IOException {
        LogicalNode andNode = new LogicalNode(LogicalNode.LogicalOperation.AND);
        andNode.setReference("test-reference");
        String json = objectMapper.writeValueAsString(andNode);

        LogicalNode logicalNode = objectMapper.readValue(json, LogicalNode.class);
        assertEquals("test-reference", logicalNode.getReference());
    }

    @Test
    public void readEmptyQuery() throws IOException {
        String query = "{\"type\":\".LogicalNode\",\"reference\":\"test-reference\",\"enabled\":true,\"operation\":\"AND\",\"items\":[]}";
        LogicalNode logicalNode = objectMapper.readValue(query, LogicalNode.class);
        assertNotNull(logicalNode);
    }
}
