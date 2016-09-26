package com.lindar.jsonquery.querydsl.jpa;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.Lists;
import com.lindar.jsonquery.ast.*;
import com.lindar.jsonquery.querydsl.jpa.domain.Player;
import com.lindar.jsonquery.querydsl.jpa.domain.PlayerAttrition;
import com.lindar.jsonquery.relationships.ast.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPQLSerializer;
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
        visitor = new QuerydslJpaJsonQueryVisitor();
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

    /*@Test
    @DatabaseSetup("/sampleData.xml")
    public void testFind() throws Exception {

        StringComparisonNode stringNode = new StringComparisonNode();
        stringNode.setField("username");
        stringNode.setOperation(StringComparisonOperation.BEGINS_WITH);
        ArrayList<String> values = new ArrayList<String>();
        values.add("random");
        stringNode.setValue(values);

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
        holder.relationships.getItems().add(relatedRelationshipNode);

        JPAQuery query = new JPAQuery(entityManager);
        PathBuilder entity = new PathBuilder(Player.class, "player");
        Predicate conditions = holder.conditions.accept(visitor, entity);
        Predicate relationships = holder.relationships.accept(visitor, entity);
        Predicate predicate = ExpressionUtils.allOf(conditions, relationships);
        query.select(entity).from(entity).where(predicate);
        List<Player> fetch = query.fetch();

        //assertToString("", conditions);

    }*/

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

    }

    @Test
    public void testVisitStringComparisonAggregateNode(){
        List<Integer> value = Lists.newArrayList(0);
        List<Integer> values = Lists.newArrayList(0, 10);


        assertToString("count(player.promocode) > ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("count(distinct player.promocode) > ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT_DISTINCT,
                        AggregateComparisonOperation.GREATER_THAN,
                        value));

        assertToString("count(player.promocode) = ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.EQUALS,
                        value));

        assertToString("count(player.promocode) >= ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.GREATER_THAN_OR_EQUAL,
                        value));

        assertToString("count(player.promocode) < ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.LESS_THAN,
                        value));

        assertToString("count(player.promocode) <= ?1",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.LESS_THAN_OR_EQUAL,
                        value));

        assertToString("count(player.promocode) between ?1 and ?2",
                createStringComparisonAggregateNodePredicate("promocode",
                        StringComparisonAggregateNode.StringAggregateOperation.COUNT,
                        AggregateComparisonOperation.BETWEEN,
                        values));

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
            BigDecimalComparisonAggregateNode node = new BigDecimalComparisonAggregateNode();
            node.setField(field);
            node.setAggregateOperation(aggregateOperation);
            node.setNegate(false);
            node.setOperation(comparisonOperation);
            node.setValue(value);

            return node.accept(visitor, playerEntity);

    }

    private Predicate createStringComparisonAggregateNodePredicate(String field,
                                                                       StringComparisonAggregateNode.StringAggregateOperation aggregateOperation,
                                                                       AggregateComparisonOperation comparisonOperation,
                                                                       List<Integer> value
    ){
        StringComparisonAggregateNode node = new StringComparisonAggregateNode();
        node.setField(field);
        node.setAggregateOperation(aggregateOperation);
        node.setNegate(false);
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
