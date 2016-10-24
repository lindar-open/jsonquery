package com.lindar.jsonquery.querydsl.sql.domain.gen;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * SPlayer is a Querydsl query type for SPlayer
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPlayer extends com.querydsl.sql.RelationalPathBase<SPlayer> {

    private static final long serialVersionUID = -1334580226;

    public static final SPlayer player = new SPlayer("player");

    public final NumberPath<Long> acquisitionId = createNumber("acquisitionId", Long.class);

    public final StringPath addressCity = createString("addressCity");

    public final StringPath addressCountry = createString("addressCountry");

    public final StringPath addressFormatted = createString("addressFormatted");

    public final NumberPath<java.math.BigDecimal> addressLatitude = createNumber("addressLatitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> addressLongitude = createNumber("addressLongitude", java.math.BigDecimal.class);

    public final StringPath addressPostcode = createString("addressPostcode");

    public final StringPath addressState = createString("addressState");

    public final StringPath addressStreet = createString("addressStreet");

    public final BooleanPath addressValid = createBoolean("addressValid");

    public final NumberPath<Long> affiliateId = createNumber("affiliateId", Long.class);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balanceHighest = createNumber("balanceHighest", java.math.BigDecimal.class);

    public final NumberPath<Integer> bingoCardsBrought = createNumber("bingoCardsBrought", Integer.class);

    public final NumberPath<Integer> bingoGamesPlayed = createNumber("bingoGamesPlayed", Integer.class);

    public final NumberPath<java.math.BigDecimal> bingoWagers = createNumber("bingoWagers", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> bingoWins = createNumber("bingoWins", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> bonusBalance = createNumber("bonusBalance", java.math.BigDecimal.class);

    public final NumberPath<Long> brandId = createNumber("brandId", Long.class);

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath currency = createString("currency");

    public final NumberPath<java.math.BigDecimal> deposits = createNumber("deposits", java.math.BigDecimal.class);

    public final DatePath<java.sql.Date> dob = createDate("dob", java.sql.Date.class);

    public final StringPath email = createString("email");

    public final StringPath externalId = createString("externalId");

    public final DateTimePath<java.sql.Timestamp> firstDepositDate = createDateTime("firstDepositDate", java.sql.Timestamp.class);

    public final StringPath firstName = createString("firstName");

    public final StringPath fullName = createString("fullName");

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> igWagers = createNumber("igWagers", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> igWins = createNumber("igWins", java.math.BigDecimal.class);

    public final DateTimePath<java.sql.Timestamp> lastBingoWagerDate = createDateTime("lastBingoWagerDate", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> lastDepositDate = createDateTime("lastDepositDate", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> lastIgWagerDate = createDateTime("lastIgWagerDate", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> lastLoginDate = createDateTime("lastLoginDate", java.sql.Timestamp.class);

    public final StringPath lastLoginDevice = createString("lastLoginDevice");

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath lifeCycle = createString("lifeCycle");

    public final DateTimePath<java.sql.Timestamp> lifeCycleDate = createDateTime("lifeCycleDate", java.sql.Timestamp.class);

    public final NumberPath<java.math.BigDecimal> lp = createNumber("lp", java.math.BigDecimal.class);

    public final StringPath mobileNumber = createString("mobileNumber");

    public final StringPath mobileNumberRaw = createString("mobileNumberRaw");

    public final BooleanPath mobileNumberValid = createBoolean("mobileNumberValid");

    public final NumberPath<java.math.BigDecimal> netCash = createNumber("netCash", java.math.BigDecimal.class);

    public final NumberPath<Integer> numDeposits = createNumber("numDeposits", Integer.class);

    public final NumberPath<Integer> numReversals = createNumber("numReversals", Integer.class);

    public final NumberPath<Integer> numWithdrawals = createNumber("numWithdrawals", Integer.class);

    public final NumberPath<Integer> numWithdrawalsRequested = createNumber("numWithdrawalsRequested", Integer.class);

    public final StringPath promocode = createString("promocode");

    public final NumberPath<java.math.BigDecimal> realBalance = createNumber("realBalance", java.math.BigDecimal.class);

    public final DateTimePath<java.sql.Timestamp> registrationDate = createDateTime("registrationDate", java.sql.Timestamp.class);

    public final StringPath registrationDevice = createString("registrationDevice");

    public final StringPath restriction = createString("restriction");

    public final NumberPath<java.math.BigDecimal> reversals = createNumber("reversals", java.math.BigDecimal.class);

    public final StringPath status = createString("status");

    public final BooleanPath subscribedEmail = createBoolean("subscribedEmail");

    public final BooleanPath subscribedPhone = createBoolean("subscribedPhone");

    public final BooleanPath subscribedPost = createBoolean("subscribedPost");

    public final BooleanPath subscribedSms = createBoolean("subscribedSms");

    public final StringPath telephoneNumber = createString("telephoneNumber");

    public final StringPath telephoneNumberRaw = createString("telephoneNumberRaw");

    public final BooleanPath telephoneNumberValid = createBoolean("telephoneNumberValid");

    public final StringPath trackerCustom = createString("trackerCustom");

    public final NumberPath<Long> trackerId = createNumber("trackerId", Long.class);

    public final StringPath username = createString("username");

    public final StringPath vip = createString("vip");

    public final NumberPath<java.math.BigDecimal> withdrawals = createNumber("withdrawals", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> withdrawalsRequests = createNumber("withdrawalsRequests", java.math.BigDecimal.class);

    public final com.querydsl.sql.PrimaryKey<SPlayer> primary = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<SAffiliate> fK3km136hryrwkc1bfaevucieqc = createForeignKey(affiliateId, "id");

    public final com.querydsl.sql.ForeignKey<SBrand> fK6tx3suvslb18ek5a7oioakloy = createForeignKey(brandId, "id");

    public final com.querydsl.sql.ForeignKey<SPlayerAttrition> _fKfp16sfcuohhohnt3mjfcq1ngo = createInvForeignKey(id, "player_id");


    public SPlayer(String variable) {
        super(SPlayer.class, forVariable(variable), "null", "player");
        addMetadata();
    }

    public SPlayer(String variable, String schema, String table) {
        super(SPlayer.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public SPlayer(Path<? extends SPlayer> path) {
        super(path.getType(), path.getMetadata(), "null", "player");
        addMetadata();
    }

    public SPlayer(PathMetadata metadata) {
        super(SPlayer.class, metadata, "null", "player");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(acquisitionId, ColumnMetadata.named("acquisition_id").withIndex(68).ofType(Types.BIGINT).withSize(19));
        addMetadata(addressCity, ColumnMetadata.named("address_city").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressCountry, ColumnMetadata.named("address_country").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressFormatted, ColumnMetadata.named("address_formatted").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressLatitude, ColumnMetadata.named("address_latitude").withIndex(9).ofType(Types.DECIMAL).withSize(10).withDigits(8));
        addMetadata(addressLongitude, ColumnMetadata.named("address_longitude").withIndex(10).ofType(Types.DECIMAL).withSize(11).withDigits(8));
        addMetadata(addressPostcode, ColumnMetadata.named("address_postcode").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressState, ColumnMetadata.named("address_state").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressStreet, ColumnMetadata.named("address_street").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(addressValid, ColumnMetadata.named("address_valid").withIndex(14).ofType(Types.BIT).withSize(1));
        addMetadata(affiliateId, ColumnMetadata.named("affiliate_id").withIndex(69).ofType(Types.BIGINT).withSize(19));
        addMetadata(balance, ColumnMetadata.named("balance").withIndex(15).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(balanceHighest, ColumnMetadata.named("balance_highest").withIndex(16).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(bingoCardsBrought, ColumnMetadata.named("bingo_cards_brought").withIndex(17).ofType(Types.INTEGER).withSize(10));
        addMetadata(bingoGamesPlayed, ColumnMetadata.named("bingo_games_played").withIndex(18).ofType(Types.INTEGER).withSize(10));
        addMetadata(bingoWagers, ColumnMetadata.named("bingo_wagers").withIndex(19).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(bingoWins, ColumnMetadata.named("bingo_wins").withIndex(20).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(bonusBalance, ColumnMetadata.named("bonus_balance").withIndex(21).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(brandId, ColumnMetadata.named("brand_id").withIndex(70).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(currency, ColumnMetadata.named("currency").withIndex(28).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deposits, ColumnMetadata.named("deposits").withIndex(29).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(dob, ColumnMetadata.named("dob").withIndex(30).ofType(Types.DATE).withSize(10));
        addMetadata(email, ColumnMetadata.named("email").withIndex(31).ofType(Types.VARCHAR).withSize(255));
        addMetadata(externalId, ColumnMetadata.named("external_id").withIndex(32).ofType(Types.VARCHAR).withSize(100));
        addMetadata(firstDepositDate, ColumnMetadata.named("first_deposit_date").withIndex(33).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(firstName, ColumnMetadata.named("first_name").withIndex(34).ofType(Types.VARCHAR).withSize(255));
        addMetadata(fullName, ColumnMetadata.named("full_name").withIndex(35).ofType(Types.VARCHAR).withSize(255));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(36).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(igWagers, ColumnMetadata.named("ig_wagers").withIndex(37).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(igWins, ColumnMetadata.named("ig_wins").withIndex(38).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(lastBingoWagerDate, ColumnMetadata.named("last_bingo_wager_date").withIndex(39).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastDepositDate, ColumnMetadata.named("last_deposit_date").withIndex(40).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastIgWagerDate, ColumnMetadata.named("last_ig_wager_date").withIndex(41).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastLoginDate, ColumnMetadata.named("last_login_date").withIndex(42).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastLoginDevice, ColumnMetadata.named("last_login_device").withIndex(43).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastName, ColumnMetadata.named("last_name").withIndex(44).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lifeCycle, ColumnMetadata.named("life_cycle").withIndex(45).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lifeCycleDate, ColumnMetadata.named("life_cycle_date").withIndex(46).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lp, ColumnMetadata.named("lp").withIndex(47).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(mobileNumber, ColumnMetadata.named("mobile_number").withIndex(22).ofType(Types.VARCHAR).withSize(255));
        addMetadata(mobileNumberRaw, ColumnMetadata.named("mobile_number_raw").withIndex(23).ofType(Types.VARCHAR).withSize(255));
        addMetadata(mobileNumberValid, ColumnMetadata.named("mobile_number_valid").withIndex(24).ofType(Types.BIT).withSize(1));
        addMetadata(netCash, ColumnMetadata.named("net_cash").withIndex(48).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(numDeposits, ColumnMetadata.named("num_deposits").withIndex(49).ofType(Types.INTEGER).withSize(10));
        addMetadata(numReversals, ColumnMetadata.named("num_reversals").withIndex(50).ofType(Types.INTEGER).withSize(10));
        addMetadata(numWithdrawals, ColumnMetadata.named("num_withdrawals").withIndex(51).ofType(Types.INTEGER).withSize(10));
        addMetadata(numWithdrawalsRequested, ColumnMetadata.named("num_withdrawals_requested").withIndex(52).ofType(Types.INTEGER).withSize(10));
        addMetadata(promocode, ColumnMetadata.named("promocode").withIndex(53).ofType(Types.VARCHAR).withSize(255));
        addMetadata(realBalance, ColumnMetadata.named("real_balance").withIndex(54).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(registrationDate, ColumnMetadata.named("registration_date").withIndex(55).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(registrationDevice, ColumnMetadata.named("registration_device").withIndex(56).ofType(Types.VARCHAR).withSize(255));
        addMetadata(restriction, ColumnMetadata.named("restriction").withIndex(57).ofType(Types.VARCHAR).withSize(255));
        addMetadata(reversals, ColumnMetadata.named("reversals").withIndex(58).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(status, ColumnMetadata.named("status").withIndex(59).ofType(Types.VARCHAR).withSize(255));
        addMetadata(subscribedEmail, ColumnMetadata.named("subscribed_email").withIndex(60).ofType(Types.BIT).withSize(1));
        addMetadata(subscribedPhone, ColumnMetadata.named("subscribed_phone").withIndex(61).ofType(Types.BIT).withSize(1));
        addMetadata(subscribedPost, ColumnMetadata.named("subscribed_post").withIndex(62).ofType(Types.BIT).withSize(1));
        addMetadata(subscribedSms, ColumnMetadata.named("subscribed_sms").withIndex(63).ofType(Types.BIT).withSize(1));
        addMetadata(telephoneNumber, ColumnMetadata.named("telephone_number").withIndex(25).ofType(Types.VARCHAR).withSize(255));
        addMetadata(telephoneNumberRaw, ColumnMetadata.named("telephone_number_raw").withIndex(26).ofType(Types.VARCHAR).withSize(255));
        addMetadata(telephoneNumberValid, ColumnMetadata.named("telephone_number_valid").withIndex(27).ofType(Types.BIT).withSize(1));
        addMetadata(trackerCustom, ColumnMetadata.named("tracker_custom").withIndex(72).ofType(Types.VARCHAR).withSize(255));
        addMetadata(trackerId, ColumnMetadata.named("tracker_id").withIndex(71).ofType(Types.BIGINT).withSize(19));
        addMetadata(username, ColumnMetadata.named("username").withIndex(64).ofType(Types.VARCHAR).withSize(255));
        addMetadata(vip, ColumnMetadata.named("vip").withIndex(65).ofType(Types.VARCHAR).withSize(255));
        addMetadata(withdrawals, ColumnMetadata.named("withdrawals").withIndex(66).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(withdrawalsRequests, ColumnMetadata.named("withdrawals_requests").withIndex(67).ofType(Types.DECIMAL).withSize(19).withDigits(2));
    }

}

