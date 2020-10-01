package com.lindar.jsonquery.querydsl.sql;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lindar.jsonquery.ast.*;
import com.lindar.jsonquery.querydsl.sql.domain.Brand;
import com.lindar.jsonquery.querydsl.sql.domain.Player;
import com.lindar.jsonquery.querydsl.sql.domain.PlayerAttrition;
import com.lindar.jsonquery.querydsl.sql.domain.gen.SAffiliate;
import com.lindar.jsonquery.querydsl.sql.domain.gen.SBrand;
import com.lindar.jsonquery.querydsl.sql.domain.gen.SPlayer;
import com.lindar.jsonquery.querydsl.sql.domain.gen.SPlayerAttrition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by stevenhills on 25/09/2016.
 */
@RunWith(JUnit4.class)
@ContextConfiguration(value = "classpath:**/context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
public class TestQuerydslJqlJsonQueryVisitor {

    @PersistenceContext
    private EntityManager entityManager;

    private QuerydslSqlJsonQueryVisitor visitor;
    private PathBuilder<Player> playerEntity;
    private PathBuilder<PlayerAttrition> playerAttritionEntity;
    private PathBuilder<Brand> brand;

    private Map<String, RelationalPath> joinPaths = Maps.newHashMap();
    private Map<String, ForeignKey> joinKeys = Maps.newHashMap();

    private QuerydslSqlSpec querydslSqlSpec;

    SQLQuery subQuery;

    @Before
    public void setUp() {
        playerEntity = new PathBuilder(Player.class, "player");
        playerAttritionEntity = new PathBuilder(PlayerAttrition.class, "player_attrition");
        brand = new PathBuilder(Brand.class, "brand");

        joinPaths = Maps.newHashMap();
        joinPaths.put("brand", SBrand.brand);
        joinPaths.put("affiliate", SAffiliate.affiliate);
        joinPaths.put("attritions", SPlayerAttrition.playerAttrition);

        joinKeys = Maps.newHashMap();
        joinKeys.put("brand", SPlayer.player.fK6tx3suvslb18ek5a7oioakloy);
        joinKeys.put("affiliate", SPlayer.player.fK3km136hryrwkc1bfaevucieqc);
        joinKeys.put("attritions", SPlayer.player._fKfp16sfcuohhohnt3mjfcq1ngo);

        subQuery = new SQLQuery();
        subQuery.from(SPlayer.player);
        subQuery.select(SPlayer.player.id);

        querydslSqlSpec = new QuerydslSqlSpec();
        QuerydslSqlSpec.Key brandKeyId = QuerydslSqlSpec.Key.builder()
            .parentClass(Player.class)
            .path("brand.id")
            .build();

        QuerydslSqlSpec.Key brandKeyType = QuerydslSqlSpec.Key.builder()
            .parentClass(Player.class)
            .path("brand.type")
            .build();

        QuerydslSqlSpec.JoinSpec joinSpec = QuerydslSqlSpec.JoinSpec.builder()
            .field("id")
            .joins(
                Lists.newArrayList(
                    QuerydslSqlSpec.Join.builder()
                        .foreignKey(SPlayer.player.fK6tx3suvslb18ek5a7oioakloy)
                        .joinEntity(SBrand.brand)
                        .joinEntityClass(Brand.class)
                        .build()
                )
            )
            .build();

        QuerydslSqlSpec.Key attritionKey = QuerydslSqlSpec.Key.builder()
            .parentClass(Player.class)
            .path("attritions")
            .build();

        QuerydslSqlSpec.RelationshipSpec attritionSpec = QuerydslSqlSpec.RelationshipSpec.builder()
            .relationshipEntity(SPlayerAttrition.playerAttrition)
            .relationshipEntityClass(PlayerAttrition.class)
            .foreignKey("player_id")
            .primaryKey("id")
            .build();

        querydslSqlSpec.getJoinSpecs().put(brandKeyId, joinSpec);
        querydslSqlSpec.getJoinSpecs().put(brandKeyType, joinSpec);
        querydslSqlSpec.getRelationshipSpecs().put(attritionKey, attritionSpec);

        visitor = new QuerydslSqlJsonQueryVisitor(subQuery, querydslSqlSpec);
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
        brand = null;
        joinPaths.clear();
        joinPaths = null;
        joinKeys.clear();
        joinKeys = null;
        querydslSqlSpec = null;
    }

    @Test
    public void testGeneratedQueryWithJoin() throws Exception {

        LookupComparisonNode lookupNode = new LookupComparisonNode();
        lookupNode.setField("brand.id");
        lookupNode.setOperation(LookupComparisonOperation.IN);
        ArrayList<Long> values = new ArrayList<>();
        values.add(1L);
        lookupNode.setValue(values);

        StringComparisonNode stringNode = new StringComparisonNode();
        stringNode.setField("type");
        stringNode.setOperation(StringComparisonOperation.IN);
        ArrayList<String> values2 = new ArrayList<>();
        values2.add("hello");
        stringNode.setValue(values2);

        SQLQuery sqlQuery = new SQLQuery();
        sqlQuery.from(playerEntity);
        sqlQuery.select(playerEntity.getString("username"));


        Predicate visit = visitor.visit(lookupNode, playerEntity);
        Predicate visit2 = visitor.visit(stringNode, playerEntity);
        subQuery.where(new BooleanBuilder().and(visit).and(visit2));
        sqlQuery.where(SPlayer.player.id.in(subQuery));

        //sqlQuery.join(brand).on(playerEntity.get("brand_id").eq(brand.get("id")));

        //sqlQuery.join(SBrand.brand).on(SPlayer.player.fK6tx3suvslb18ek5a7oioakloy.on(SBrand.brand));

        System.out.println(sqlQuery.getSQL().getSQL());
    }

    @Test
    public void testVisitBigDecimalComparisonAggregateNode() {
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);
        List<BigDecimal> values = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN);


        assertToString("count(player.deposits) > 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.COUNT,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("sum(player.deposits) > 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("avg(player.deposits) > 0.0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.AVG,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("count(distinct player.deposits) > 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.COUNT_DISTINCT,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("max(player.deposits) > 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.MAX,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("min(player.deposits) > 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.MIN,
                AggregateComparisonOperation.GREATER_THAN,
                value));


        assertToString("sum(player.deposits) = 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.EQUALS,
                value));

        assertToString("sum(player.deposits) >= 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.GREATER_THAN_OR_EQUAL,
                value));

        assertToString("sum(player.deposits) < 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.LESS_THAN,
                value));

        assertToString("sum(player.deposits) <= 0",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.LESS_THAN_OR_EQUAL,
                value));

        assertToString("sum(player.deposits) between 0 and 10",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.BETWEEN,
                values));


        assertToString("!(sum(player.deposits) = 0)",
            createBigDecimalComparisonAggregateNodePredicate("deposits",
                NumberComparisonAggregateNode.NumberAggregateOperation.SUM,
                AggregateComparisonOperation.EQUALS,
                value, true));

    }

    @Test
    public void testVisitStringComparisonAggregateNode() {
        List<Integer> value = Lists.newArrayList(0);
        List<Integer> values = Lists.newArrayList(0, 10);


        assertToString("count(player.promocode) > 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("count(distinct player.promocode) > 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT_DISTINCT,
                AggregateComparisonOperation.GREATER_THAN,
                value));

        assertToString("count(player.promocode) = 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.EQUALS,
                value));

        assertToString("count(player.promocode) >= 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.GREATER_THAN_OR_EQUAL,
                value));

        assertToString("count(player.promocode) < 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.LESS_THAN,
                value));

        assertToString("count(player.promocode) <= 0",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.LESS_THAN_OR_EQUAL,
                value));

        assertToString("count(player.promocode) between 0 and 10",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.BETWEEN,
                values));

        assertToString("!(count(player.promocode) > 0)",
            createStringComparisonAggregateNodePredicate("promocode",
                StringAggregateOperation.COUNT,
                AggregateComparisonOperation.GREATER_THAN,
                value, true));

    }


    @Test
    public void testVisitStringComparisonNode() {
        List<String> value = Lists.newArrayList("test");
        List<String> values = Lists.newArrayList("test", "another test");


        assertToString("player.promocode = test",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.EQUALS,
                value));

        assertToString("player.promocode like test%",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.BEGINS_WITH,
                value));

        assertToString("player.promocode like %test%",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.CONTAINS,
                value));

        assertToString("player.promocode = ",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.EMPTY,
                value));

        assertToString("player.promocode like %test",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.ENDS_WITH,
                value));

        assertToString("matches(test,player.promocode)",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.REGEX,
                value));

        assertToString("player.promocode in [test, another test]",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.IN,
                values));

        assertToString("!(player.promocode = test)",
            createStringComparisonNodePredicate("promocode",
                StringComparisonOperation.EQUALS,
                values, true));

        assertToString("brand.type = test",
            createStringComparisonNodePredicate("brand.type",
                StringComparisonOperation.EQUALS,
                value));
    }

    @Test
    public void testVisitBigDecimalComparisonNode() {
        List<BigDecimal> value = Lists.newArrayList(BigDecimal.ZERO);
        List<BigDecimal> values = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN);

        assertToString("player.deposits = 0",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.EQUALS,
                value));

        assertToString("player.deposits > 0",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.GREATER_THAN,
                value));

        assertToString("player.deposits >= 0",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.GREATER_THAN_OR_EQUAL,
                value));

        assertToString("player.deposits < 0",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.LESS_THAN,
                value));

        assertToString("player.deposits <= 0",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.LESS_THAN_OR_EQUAL,
                value));

        assertToString("player.deposits between 0 and 10",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.BETWEEN,
                values));

        assertToString("player.deposits is null",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.EMPTY,
                value));

        assertToString("!(player.deposits = 0)",
            createBigDecimalComparisonNodePredicate("deposits",
                NumberComparisonOperation.EQUALS,
                value, true));
    }

    @Test
    public void testVisitDateComparisonNode() {


    }

    @Test
    public void testVisitLogicalNode() {
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

        assertToString("player.deposits < 0 && player.deposits > 0",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, node2))
        );

        assertToString("player.deposits < 0 || player.deposits > 0",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, node2))
        );

        assertToString("player.deposits < 0 && player.deposits <= 0 && player.deposits >= 0",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("player.deposits < 0 && (player.deposits <= 0 || player.deposits >= 0)",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(node1, logicalNodeOr))
        );

        assertToString("player.deposits < 0 || player.deposits <= 0 && player.deposits >= 0",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("player.deposits < 0 || (player.deposits <= 0 || player.deposits >= 0)",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.OR, Lists.newArrayList(node1, logicalNodeOr))
        );


        assertToString("brand.type like 0 escape '!'",
            createLogicalNodePredicate(LogicalNode.LogicalOperation.AND, Lists.newArrayList(nodeRelated))
        );

    }

    @Test
    public void testVisitLogicalAggregateNode() {
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

        assertToString("sum(player.deposits) < 0 && sum(player.deposits) > 0",
            createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, node2))
        );

        assertToString("sum(player.deposits) < 0 || sum(player.deposits) > 0",
            createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.OR, Lists.newArrayList(node1, node2))
        );

        assertToString("sum(player.deposits) < 0 && sum(player.deposits) <= 0 && sum(player.deposits) >= 0",
            createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("sum(player.deposits) < 0 && (sum(player.deposits) <= 0 || sum(player.deposits) >= 0)",
            createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.AND, Lists.newArrayList(node1, logicalNodeOr))
        );

        assertToString("sum(player.deposits) < 0 || sum(player.deposits) <= 0 && sum(player.deposits) >= 0",
            createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation.OR, Lists.newArrayList(node1, logicalNodeAnd))
        );

        assertToString("sum(player.deposits) < 0 || sum(player.deposits) <= 0 || sum(player.deposits) >= 0",
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
    ) {
        return createBigDecimalComparisonAggregateNodePredicate(field, aggregateOperation, comparisonOperation, value, false);

    }

    private Predicate createBigDecimalComparisonAggregateNodePredicate(String field,
                                                                       NumberComparisonAggregateNode.NumberAggregateOperation aggregateOperation,
                                                                       AggregateComparisonOperation comparisonOperation,
                                                                       List<BigDecimal> value,
                                                                       boolean negate
    ) {
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
    ) {
        return createStringComparisonAggregateNodePredicate(field, aggregateOperation, comparisonOperation, value, false);
    }

    private Predicate createStringComparisonAggregateNodePredicate(String field,
                                                                   StringAggregateOperation aggregateOperation,
                                                                   AggregateComparisonOperation comparisonOperation,
                                                                   List<Integer> value,
                                                                   boolean negate
    ) {
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
    ) {
        return createStringComparisonNodePredicate(field, comparisonOperation, value, false);

    }

    private Predicate createStringComparisonNodePredicate(String field,
                                                          StringComparisonOperation comparisonOperation,
                                                          List<String> value,
                                                          boolean negate
    ) {
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
    ) {
        return createBigDecimalComparisonNodePredicate(field, comparisonOperation, value, false);
    }

    private Predicate createBigDecimalComparisonNodePredicate(String field,
                                                              NumberComparisonOperation comparisonOperation,
                                                              List<BigDecimal> value,
                                                              boolean negate
    ) {
        BigDecimalComparisonNode node = new BigDecimalComparisonNode();
        node.setField(field);
        node.setNegate(negate);
        node.setOperation(comparisonOperation);
        node.setValue(value);

        return node.accept(visitor, playerEntity);
    }

    private Predicate createLogicalNodePredicate(LogicalNode.LogicalOperation operation, List<Node> nodes) {
        LogicalNode node = new LogicalNode(operation);
        node.setItems(nodes);
        return node.accept(visitor, playerEntity);
    }

    private Predicate createLogicalAggregateNodePredicate(LogicalAggregateNode.LogicalAggregateOperation operation, List<AggregateNode> nodes) {
        LogicalAggregateNode node = new LogicalAggregateNode(operation);
        node.setItems(nodes);
        return node.accept(visitor, playerEntity);
    }

    protected static void assertToString(String expected, Expression<?> expr) {
        assertEquals(expected, expr.toString());
    }
}
