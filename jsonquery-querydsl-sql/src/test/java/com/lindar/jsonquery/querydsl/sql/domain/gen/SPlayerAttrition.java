package com.lindar.jsonquery.querydsl.sql.domain.gen;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * SPlayerAttrition is a Querydsl query type for SPlayerAttrition
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPlayerAttrition extends com.querydsl.sql.RelationalPathBase<SPlayerAttrition> {

    private static final long serialVersionUID = 1692695886;

    public static final SPlayerAttrition playerAttrition = new SPlayerAttrition("player_attrition");

    public final NumberPath<Integer> bingoCardsBrought = createNumber("bingoCardsBrought", Integer.class);

    public final NumberPath<Integer> bingoGamesPlayed = createNumber("bingoGamesPlayed", Integer.class);

    public final NumberPath<java.math.BigDecimal> bingoWagers = createNumber("bingoWagers", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> bingoWins = createNumber("bingoWins", java.math.BigDecimal.class);

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final NumberPath<java.math.BigDecimal> deposits = createNumber("deposits", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> igWagers = createNumber("igWagers", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> igWins = createNumber("igWins", java.math.BigDecimal.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final NumberPath<java.math.BigDecimal> netCash = createNumber("netCash", java.math.BigDecimal.class);

    public final NumberPath<Integer> numDeposits = createNumber("numDeposits", Integer.class);

    public final NumberPath<Integer> numReversals = createNumber("numReversals", Integer.class);

    public final NumberPath<Integer> numWithdrawals = createNumber("numWithdrawals", Integer.class);

    public final NumberPath<Integer> numWithdrawalsRequested = createNumber("numWithdrawalsRequested", Integer.class);

    public final DatePath<java.sql.Date> period = createDate("period", java.sql.Date.class);

    public final NumberPath<Long> playerId = createNumber("playerId", Long.class);

    public final NumberPath<java.math.BigDecimal> reversals = createNumber("reversals", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> wagers = createNumber("wagers", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> wins = createNumber("wins", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> withdrawals = createNumber("withdrawals", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> withdrawalsRequested = createNumber("withdrawalsRequested", java.math.BigDecimal.class);

    public final com.querydsl.sql.PrimaryKey<SPlayerAttrition> primary = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<SPlayer> fKfp16sfcuohhohnt3mjfcq1ngo = createForeignKey(playerId, "id");

    public SPlayerAttrition(String variable) {
        super(SPlayerAttrition.class, forVariable(variable), "null", "player_attrition");
        addMetadata();
    }

    public SPlayerAttrition(String variable, String schema, String table) {
        super(SPlayerAttrition.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public SPlayerAttrition(Path<? extends SPlayerAttrition> path) {
        super(path.getType(), path.getMetadata(), "null", "player_attrition");
        addMetadata();
    }

    public SPlayerAttrition(PathMetadata metadata) {
        super(SPlayerAttrition.class, metadata, "null", "player_attrition");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bingoCardsBrought, ColumnMetadata.named("bingo_cards_brought").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(bingoGamesPlayed, ColumnMetadata.named("bingo_games_played").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(bingoWagers, ColumnMetadata.named("bingo_wagers").withIndex(8).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(bingoWins, ColumnMetadata.named("bingo_wins").withIndex(9).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(deposits, ColumnMetadata.named("deposits").withIndex(10).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(igWagers, ColumnMetadata.named("ig_wagers").withIndex(11).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(igWins, ColumnMetadata.named("ig_wins").withIndex(12).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(netCash, ColumnMetadata.named("net_cash").withIndex(13).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(numDeposits, ColumnMetadata.named("num_deposits").withIndex(14).ofType(Types.INTEGER).withSize(10));
        addMetadata(numReversals, ColumnMetadata.named("num_reversals").withIndex(15).ofType(Types.INTEGER).withSize(10));
        addMetadata(numWithdrawals, ColumnMetadata.named("num_withdrawals").withIndex(16).ofType(Types.INTEGER).withSize(10));
        addMetadata(numWithdrawalsRequested, ColumnMetadata.named("num_withdrawals_requested").withIndex(17).ofType(Types.INTEGER).withSize(10));
        addMetadata(period, ColumnMetadata.named("period").withIndex(18).ofType(Types.DATE).withSize(10));
        addMetadata(playerId, ColumnMetadata.named("player_id").withIndex(24).ofType(Types.BIGINT).withSize(19));
        addMetadata(reversals, ColumnMetadata.named("reversals").withIndex(19).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(wagers, ColumnMetadata.named("wagers").withIndex(20).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(wins, ColumnMetadata.named("wins").withIndex(21).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(withdrawals, ColumnMetadata.named("withdrawals").withIndex(22).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(withdrawalsRequested, ColumnMetadata.named("withdrawals_requested").withIndex(23).ofType(Types.DECIMAL).withSize(19).withDigits(2));
    }

}

