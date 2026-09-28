package com.lindar.jsonquery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lindar.jsonquery.ast.*;
import org.junit.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Spring Boot 3 services read and write JsonQuery with Jackson 2 and Spring Boot 4 services with Jackson 3,
 * so each must read what the other writes without losing anything.
 */
public class TestJsonQueryWireCompatibility {

    private final ObjectMapper jackson2 = new ObjectMapper();
    private final JsonMapper jackson3 = JsonMapper.builder().build();

    @Test
    public void jackson3ReadsWhatJackson2Writes() throws Exception {
        String written = jackson2.writeValueAsString(sampleQuery());

        JsonQuery read = jackson3.readValue(written, JsonQuery.class);

        assertEquals(jackson2.readTree(written), jackson2.readTree(jackson2.writeValueAsString(read)));
    }

    @Test
    public void jackson2ReadsWhatJackson3Writes() throws Exception {
        String written = jackson3.writeValueAsString(sampleQuery());

        JsonQuery read = jackson2.readValue(written, JsonQuery.class);

        assertEquals(jackson3.readTree(written), jackson3.readTree(jackson3.writeValueAsString(read)));
    }

    @Test
    public void jackson3ReadsMinimalClassTypeIds() {
        String query = "{\"type\":\".LogicalNode\",\"reference\":\"test-reference\",\"enabled\":true,\"operation\":\"AND\",\"items\":[]}";

        LogicalNode logicalNode = jackson3.readValue(query, LogicalNode.class);

        assertEquals("test-reference", logicalNode.getReference());
    }

    private static JsonQuery sampleQuery() {
        StringComparisonNode username = new StringComparisonNode();
        username.setField("username");
        username.setOperation(StringComparisonOperation.CONTAINS);
        username.setValue(List.of("steven"));

        EnumComparisonNode affiliateType = new EnumComparisonNode();
        affiliateType.setField("affiliate.type");
        affiliateType.setOperation(EnumComparisonOperation.EQUALS);
        affiliateType.setValue(List.of("ORGANIC"));

        LookupComparisonNode lists = new LookupComparisonNode();
        lists.setField("lists");
        lists.setOperation(LookupComparisonOperation.IN);
        lists.setValue(List.of(1L, 2L));

        DateComparisonNode.RelativeDays weekdays = new DateComparisonNode.RelativeDays();
        weekdays.setMonday(true);
        weekdays.setFriday(true);
        DateComparisonNode registered = new DateComparisonNode();
        registered.setField("registered");
        registered.setOperation(DateComparisonNode.Operation.PRESET);
        registered.setPresetOperation(DateComparisonNode.PresetOperation.LAST_WEEK);
        registered.setRelativeDays(weekdays);

        BigDecimalComparisonNode deposits = new BigDecimalComparisonNode();
        deposits.setField("deposits");
        deposits.setOperation(NumberComparisonOperation.GREATER_THAN);
        deposits.setValue(List.of(new BigDecimal("10.50")));

        BigDecimalComparisonAggregateNode totalDeposits = new BigDecimalComparisonAggregateNode();
        totalDeposits.setField("deposits");
        totalDeposits.setAggregateOperation(NumberComparisonAggregateNode.NumberAggregateOperation.SUM);
        totalDeposits.setOperation(AggregateComparisonOperation.GREATER_THAN);
        totalDeposits.setValue(List.of(new BigDecimal("100")));

        RelatedRelationshipNode attritions = new RelatedRelationshipNode();
        attritions.setField("attritions");
        attritions.getConditions().getItems().add(deposits);
        attritions.getAggregations().getItems().add(totalDeposits);

        LogicalNode either = new LogicalNode(LogicalNode.LogicalOperation.OR);
        either.getItems().add(username);
        either.getItems().add(affiliateType);

        JsonQuery query = new JsonQuery();
        query.getConditions().setReference("root");
        query.getConditions().getItems().add(either);
        query.getConditions().getItems().add(lists);
        query.getConditions().getItems().add(registered);
        query.getConditions().getItems().add(attritions);
        return query;
    }
}
