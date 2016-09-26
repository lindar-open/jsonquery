package com.lindar.jsonquery.querydsl.jpa;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.Lists;
import com.lindar.jsonquery.ast.*;
import com.lindar.jsonquery.querydsl.jpa.domain.Player;
import com.lindar.jsonquery.querydsl.jpa.domain.PlayerAttrition;
import com.lindar.jsonquery.relationships.ast.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by stevenhills on 25/09/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:**/context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
public class TestQuerydslJpaJsonQueryVisitor {

    @PersistenceContext
    private EntityManager entityManager;

    private QuerydslJpaJsonQueryVisitor visitor;
    private PathBuilder<Player> playerEntity;
    private PathBuilder<PlayerAttrition> playerAttritionEntity;


    @Before
    public void setUp() {
        visitor = new QuerydslJpaJsonQueryVisitor(new JPAQuery());
        playerEntity = new PathBuilder(Player.class, "player");
        playerAttritionEntity = new PathBuilder(PlayerAttrition.class, "player_attrition");

    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    @After
    public void tearDown() {
        visitor = null;
        playerEntity = null;
        playerAttritionEntity = null;
    }

    @Test
    //@DatabaseSetup("/sampleData.xml")
    public void testGeneratedQueryWithJoin() throws Exception {

        StringComparisonNode stringNode = new StringComparisonNode();
        stringNode.setField("brand.type");
        stringNode.setOperation(StringComparisonOperation.BEGINS_WITH);
        ArrayList<String> values = new ArrayList<String>();
        values.add("random");
        stringNode.setValue(values);

        StringComparisonNode stringNode2 = new StringComparisonNode();
        stringNode2.setField("brand.type");
        stringNode2.setOperation(StringComparisonOperation.ENDS_WITH);
        ArrayList<String> values2 = new ArrayList<String>();
        values2.add("something");
        stringNode2.setValue(values);

        BigDecimalComparisonNode decimalNode = new BigDecimalComparisonNode();
        decimalNode.setField("deposits");
        decimalNode.setOperation(NumberComparisonOperation.GREATER_THAN);
        ArrayList<BigDecimal> decimalValues = new ArrayList<BigDecimal>();
        decimalValues.add(BigDecimal.ZERO);
        decimalNode.setValue(decimalValues);

        RelatedRelationshipNode relatedRelationshipNode = new RelatedRelationshipNode();
        relatedRelationshipNode.setField("attritions");
        relatedRelationshipNode.getConditions().getItems().add(decimalNode);

        Holder holder = new Holder();
        holder.conditions.getItems().add(stringNode);
        holder.conditions.getItems().add(stringNode2);
        holder.relationships.getItems().add(relatedRelationshipNode);

        JPAQuery query = new JPAQuery(entityManager);
        PathBuilder entity = new PathBuilder(Player.class, "player");
        Predicate conditions = holder.conditions.accept(visitor, entity);
        Predicate relationships = holder.relationships.accept(visitor, entity);
        Predicate predicate = ExpressionUtils.allOf(conditions, relationships);

        query.select(entity).from(entity);

        query.where(predicate);

        query.fetch();
        assertToString("(select player from Player player where brand.type like ?1 escape '!' and brand.type like ?2 escape '!' and player in (select PlayerAttrition.player.id from PlayerAttrition PlayerAttrition where PlayerAttrition.deposits > ?3 group by PlayerAttrition.player.id))", query);

    }

    @Test
    public void testVisitBigDecimalComparisonAggregateNode(){
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);
        List<BigDecimal> values = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN);


        assertToString("count(player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.COUNT,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("sum(player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("avg(player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.AVG,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("count(distinct player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.COUNT_DISTINCT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("max(player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.MAX,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("min(player.deposits) > ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.MIN,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));


        assertToString("sum(player.deposits) = ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.EQUALS,
                        value));

        assertToString("sum(player.deposits) >= ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.GREATER_THAN_OR_EQUAL,
                        value));

        assertToString("sum(player.deposits) < ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.LESS_THAN,
                        value));

        assertToString("sum(player.deposits) <= ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.LESS_THAN_OR_EQUAL,
                        value));

        assertToString("sum(player.deposits) between ?1 and ?2",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.BETWEEN,
                        values));


        assertToString("not sum(player.deposits) = ?1",
                createBigDecimalComparisonAggregateNodePredicate("deposits",
                        NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                        AggregateComparisonOperation.EQUALS,
                        value, true));

    }

    @Test
    public void testVisitStringComparisonAggregateNode(){
        List<Integer> value = Lists.newArrayList(0);
        List<Integer> values = Lists.newArrayList(0, 10);


        assertToString("count(player.promocode) > ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("count(distinct player.promocode) > ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT_DISTINCT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("count(player.promocode) = ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.EQUALS,
                        value));

        assertToString("count(player.promocode) >= ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.GREATER_THAN_OR_EQUAL,
                        value));

        assertToString("count(player.promocode) < ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.LESS_THAN,
                        value));

        assertToString("count(player.promocode) <= ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.LESS_THAN_OR_EQUAL,
                        value));

        assertToString("count(player.promocode) between ?1 and ?2",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.BETWEEN,
                        values));

        assertToString("not count(player.promocode) > ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value, true));

    }


    @Test
    public void testVisitStringComparisonNode() {
        List<String> value = Lists.newArrayList("test");
        List<String> values = Lists.newArrayList("test", "another test");


        assertToString("player.promocode = ?1",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.EQUALS,
                        value));

        assertToString("player.promocode like ?1 escape '!'",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.BEGINS_WITH,
                        value));

        assertToString("player.promocode like ?1 escape '!'",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.CONTAINS,
                        value));

        assertToString("length(player.promocode) = 0",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.EMPTY,
                        value));

        assertToString("player.promocode like ?1 escape '!'",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.ENDS_WITH,
                        value));

        assertToString("?1 like player.promocode escape '!'",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.REGEX,
                        value));

        assertToString("player.promocode in (?1)",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.IN,
                        values));

        assertToString("not player.promocode = ?1",
                createStringComparisonNodePredicate("promocode",
                        StringComparisonOperation.EQUALS,
                        values, true));

        assertToString("brand.type = ?1",
                createStringComparisonNodePredicate("brand.type",
                        StringComparisonOperation.EQUALS,
                        value));
    }

    @Test
    public void testVisitBigDecimalComparisonNode() {
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);
        List<BigDecimal> values = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN);

        assertToString("player.deposits = ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.EQUALS,
                        value));

        assertToString("player.deposits > ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.GREATER_THAN,
                        value));

        assertToString("player.deposits >= ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.GREATER_THAN_OR_EQUAL,
                        value));

        assertToString("player.deposits < ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.LESS_THAN,
                        value));

        assertToString("player.deposits <= ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.LESS_THAN_OR_EQUAL,
                        value));

        assertToString("player.deposits between ?1 and ?2",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.BETWEEN,
                        values));

        assertToString("player.deposits is null",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.EMPTY,
                        value));

        assertToString("not player.deposits = ?1",
                createBigDecimalComparisonNodePredicate("deposits",
                        NumberComparisonOperation.EQUALS,
                        value, true));
    }

    @Test
    public void testVisitDateComparisonNode() {


    }

    @Test
    public void testVisitLogicalNode(){
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);

        BigDecimalComparisonNode node1 = new BigDecimalComparisonNode();
        node1.setField("deposits");
        node1.setNegate(false);
        node1.setOperation(NumberComparisonOperation.LESS_THAN);
        node1.setValue(value);

        BigDecimalComparisonNode node2 = new BigDecimalComparisonNode();
        node2.setField("deposits");
        node2.setNegate(false);
        node2.setOperation(NumberComparisonOperation.GREATER_THAN);
        node2.setValue(value);

        BigDecimalComparisonNode node3 = new BigDecimalComparisonNode();
        node3.setField("deposits");
        node3.setNegate(false);
        node3.setOperation(NumberComparisonOperation.LESS_THAN_OR_EQUAL);
        node3.setValue(value);

        BigDecimalComparisonNode node4 = new BigDecimalComparisonNode();
        node4.setField("deposits");
        node4.setNegate(false);
        node4.setOperation(NumberComparisonOperation.GREATER_THAN_OR_EQUAL);
        node4.setValue(value);

        StringComparisonNode nodeRelated = new StringComparisonNode();
        nodeRelated.setField("brand.type");
        nodeRelated.setNegate(false);
        nodeRelated.setOperation(StringComparisonOperation.CONTAINS);
        nodeRelated.setValue(Lists.newArrayList("something"));


        LogicalNode logicalNodeAnd = new LogicalNode(LogicalNode.LogicalOperation.AND);
        logicalNodeAnd.setItems(Lists.newArrayList(node3, node4));

        LogicalNode logicalNodeOr = new LogicalNode(LogicalNode.LogicalOperation.OR);
        logicalNodeOr.setItems(Lists.newArrayList(node3, node4));

        assertToString("player.deposits < ?1 and player.deposits > ?1",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, node2))
        );

        assertToString("player.deposits < ?1 or player.deposits > ?1",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, node2))
        );

        assertToString("player.deposits < ?1 and (player.deposits <= ?1 and player.deposits >= ?1)",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("player.deposits < ?1 and (player.deposits <= ?1 or player.deposits >= ?1)",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, logicalNodeOr))
        );

        assertToString("player.deposits < ?1 or player.deposits <= ?1 and player.deposits >= ?1",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("player.deposits < ?1 or (player.deposits <= ?1 or player.deposits >= ?1)",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, logicalNodeOr))
        );


        assertToString("brand.type like ?1 escape '!'",
                createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(nodeRelated))
        );

    }

    @Test
    public void testVisitLogicalAggregateNode(){
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);

        BigDecimalComparisonAggregateNode node1 = new BigDecimalComparisonAggregateNode();
        node1.setField("deposits");
        node1.setNegate(false);
        node1.setAggregateOperation(NumberComparisonAggregateNode.NumberAggregateOperation.SUM);
        node1.setOperation(AggregateComparisonOperation.LESS_THAN);
        node1.setValue(value);

        BigDecimalComparisonAggregateNode node2 = new BigDecimalComparisonAggregateNode();
        node2.setField("deposits");
        node2.setNegate(false);
        node2.setAggregateOperation(NumberComparisonAggregateNode.NumberAggregateOperation.SUM);
        node2.setOperation(AggregateComparisonOperation.GREATER_THAN);
        node2.setValue(value);

        BigDecimalComparisonAggregateNode node3 = new BigDecimalComparisonAggregateNode();
        node3.setField("deposits");
        node3.setNegate(false);
        node3.setAggregateOperation(NumberComparisonAggregateNode.NumberAggregateOperation.SUM);
        node3.setOperation(AggregateComparisonOperation.LESS_THAN_OR_EQUAL);
        node3.setValue(value);

        BigDecimalComparisonAggregateNode node4 = new BigDecimalComparisonAggregateNode();
        node4.setField("deposits");
        node4.setNegate(false);
        node4.setAggregateOperation(NumberComparisonAggregateNode.NumberAggregateOperation.SUM);
        node4.setOperation(AggregateComparisonOperation.GREATER_THAN_OR_EQUAL);
        node4.setValue(value);


        LogicalAggregateNode logicalNodeAnd = new LogicalAggregateNode(LogicalAggregateNode.LogicalAggregateOperation.AND);
        logicalNodeAnd.setItems(Lists.newArrayList(node3, node4));

        LogicalAggregateNode logicalNodeOr = new LogicalAggregateNode(LogicalAggregateNode.LogicalAggregateOperation.OR);
        logicalNodeOr.setItems(Lists.newArrayList(node3, node4));

        assertToString("sum(player.deposits) < ?1 and sum(player.deposits) > ?1",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, node2))
        );

        assertToString("sum(player.deposits) < ?1 or sum(player.deposits) > ?1",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.OR, Lists.newArrayList(node1, node2))
        );

        assertToString("sum(player.deposits) < ?1 and (sum(player.deposits) <= ?1 and sum(player.deposits) >= ?1)",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("sum(player.deposits) < ?1 and (sum(player.deposits) <= ?1 or sum(player.deposits) >= ?1)",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, logicalNodeOr))
        );

        assertToString("sum(player.deposits) < ?1 or sum(player.deposits) <= ?1 and sum(player.deposits) >= ?1",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.OR, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("sum(player.deposits) < ?1 or (sum(player.deposits) <= ?1 or sum(player.deposits) >= ?1)",
                createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.OR, Lists.newArrayList(node1, logicalNodeOr))
        );
    }


    public static class Holder {
        public LogicalNode conditions = new LogicalNode(LogicalNode.LogicalOperation.AND);
        public LogicalRelationshipNode relationships = new LogicalRelationshipNode(LogicalRelationshipNode.LogicalOperation.AND);
    }


    private Predicate createBigDecimalComparisonAggregateNodePredicate(String field,
                                                    NumberComparisonAggregateNode.NumberAggregateOperation aggregateOperation,
                                                    AggregateComparisonOperation comparisonOperation,
                                                    List<BigDecimal> value
                                                    ){
            return createBigDecimalComparisonAggregateNodePredicate(field, aggregateOperation, comparisonOperation, value, false);

    }

    private Predicate createBigDecimalComparisonAggregateNodePredicate(String field,
                                                                       NumberComparisonAggregateNode.NumberAggregateOperation aggregateOperation,
                                                                       AggregateComparisonOperation comparisonOperation,
                                                                       List<BigDecimal> value,
                                                                       boolean negate
    ){
        BigDecimalComparisonAggregateNode node = new BigDecimalComparisonAggregateNode();
        node.setField(field);
        node.setAggregateOperation(aggregateOperation);
        node.setNegate(negate);
        node.setOperation(comparisonOperation);
        node.setValue(value);

        return node.accept(visitor, playerEntity);

    }

    private Predicate createStringComparisonAggregateNodePredicate(String field,
                                                                       StringAggregateOperation aggregateOperation,
                                                                       AggregateComparisonOperation comparisonOperation,
                                                                       List<Integer> value
    ){
        return createStringComparisonAggregateNodePredicate(field, aggregateOperation, comparisonOperation, value, false);
    }

    private Predicate createStringComparisonAggregateNodePredicate(String field,
                                                                   StringAggregateOperation aggregateOperation,
                                                                   AggregateComparisonOperation comparisonOperation,
                                                                   List<Integer> value,
                                                                   boolean negate
    ){
        StringComparisonAggregateNode node = new StringComparisonAggregateNode();
        node.setField(field);
        node.setAggregateOperation(aggregateOperation);
        node.setNegate(negate);
        node.setOperation(comparisonOperation);
        node.setValue(value);

        return node.accept(visitor, playerEntity);
    }


    private Predicate createStringComparisonNodePredicate(String field,
                                                          StringComparisonOperation comparisonOperation,
                                                          List<String> value
    ){
        return createStringComparisonNodePredicate(field, comparisonOperation, value, false);

    }

    private Predicate createStringComparisonNodePredicate(String field,
                                                          StringComparisonOperation comparisonOperation,
                                                                  List<String> value,
                                                          boolean negate
    ){
        StringComparisonNode node = new StringComparisonNode();
        node.setField(field);
        node.setNegate(negate);
        node.setOperation(comparisonOperation);
        node.setValue(value);

        return node.accept(visitor, playerEntity);

    }

    private Predicate createBigDecimalComparisonNodePredicate(String field,
                                                              NumberComparisonOperation comparisonOperation,
                                                              List<BigDecimal> value
    ){
        return createBigDecimalComparisonNodePredicate(field, comparisonOperation, value, false);
    }

    private Predicate createBigDecimalComparisonNodePredicate(String field,
                                                              NumberComparisonOperation comparisonOperation,
                                                              List<BigDecimal> value,
                                                              boolean negate
    ){
        BigDecimalComparisonNode node = new BigDecimalComparisonNode();
        node.setField(field);
        node.setNegate(negate);
        node.setOperation(comparisonOperation);
        node.setValue(value);

        return node.accept(visitor, playerEntity);
    }

    private Predicate createLogicalNodePredicate(LogicalNode.LogicalOperation operation, List<Node> nodes){
        LogicalNode node = new LogicalNode(operation);
        node.setItems(nodes);
        return node.accept(visitor, playerEntity);
    }

    private Predicate createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation operation, List<AggregateNode> nodes){
        LogicalAggregateNode node = new LogicalAggregateNode(operation);
        node.setItems(nodes);
        return node.accept(visitor, playerEntity);
    }

    protected static void assertToString(String expected, Expression<?> expr) {
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT, null);
        assertEquals(expected, serializer.handle(expr).toString().replace("\n", " "));
    }
}
