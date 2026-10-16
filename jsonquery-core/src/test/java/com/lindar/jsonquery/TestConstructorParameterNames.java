package com.lindar.jsonquery;

import com.lindar.jsonquery.ast.LogicalAggregateNode;
import com.lindar.jsonquery.ast.LogicalNode;
import com.lindar.jsonquery.ast.LogicalRelationshipNode;
import org.junit.Test;

import java.lang.reflect.Parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Spring Data 6 reads these nodes from MongoDB through their only constructor, so it needs the parameter names
 * in the class files. Only {@code javac -parameters} writes them.
 */
public class TestConstructorParameterNames {

    @Test
    public void logicalNodeKeepsConstructorParameterName() {
        assertOperationParameterName(LogicalNode.class);
    }

    @Test
    public void logicalRelationshipNodeKeepsConstructorParameterName() {
        assertOperationParameterName(LogicalRelationshipNode.class);
    }

    @Test
    public void logicalAggregateNodeKeepsConstructorParameterName() {
        assertOperationParameterName(LogicalAggregateNode.class);
    }

    private static void assertOperationParameterName(Class<?> type) {
        Parameter parameter = type.getDeclaredConstructors()[0].getParameters()[0];

        assertTrue(parameter.isNamePresent());
        assertEquals("operation", parameter.getName());
    }
}
