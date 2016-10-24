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
 * SBrand is a Querydsl query type for SBrand
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SBrand extends com.querydsl.sql.RelationalPathBase<SBrand> {

    private static final long serialVersionUID = -1441275190;

    public static final SBrand brand = new SBrand("brand");

    public final StringPath colour = createString("colour");

    public final NumberPath<java.math.BigDecimal> commission = createNumber("commission", java.math.BigDecimal.class);

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath externalId = createString("externalId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath initials = createString("initials");

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final DatePath<java.sql.Date> launchDate = createDate("launchDate", java.sql.Date.class);

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public final com.querydsl.sql.PrimaryKey<SBrand> primary = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<SPlayer> _fK6tx3suvslb18ek5a7oioakloy = createInvForeignKey(id, "brand_id");

    public final com.querydsl.sql.ForeignKey<SAffiliate> _fKe38l4vffjs4n3o4yv8ujkf2ox = createInvForeignKey(id, "brand_id");

    public SBrand(String variable) {
        super(SBrand.class, forVariable(variable), "null", "brand");
        addMetadata();
    }

    public SBrand(String variable, String schema, String table) {
        super(SBrand.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public SBrand(Path<? extends SBrand> path) {
        super(path.getType(), path.getMetadata(), "null", "brand");
        addMetadata();
    }

    public SBrand(PathMetadata metadata) {
        super(SBrand.class, metadata, "null", "brand");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(colour, ColumnMetadata.named("colour").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(commission, ColumnMetadata.named("commission").withIndex(7).ofType(Types.DECIMAL).withSize(19).withDigits(2));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(externalId, ColumnMetadata.named("external_id").withIndex(8).ofType(Types.VARCHAR).withSize(100));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(initials, ColumnMetadata.named("initials").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(launchDate, ColumnMetadata.named("launch_date").withIndex(10).ofType(Types.DATE).withSize(10));
        addMetadata(name, ColumnMetadata.named("name").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(type, ColumnMetadata.named("type").withIndex(12).ofType(Types.VARCHAR).withSize(100));
    }

}

