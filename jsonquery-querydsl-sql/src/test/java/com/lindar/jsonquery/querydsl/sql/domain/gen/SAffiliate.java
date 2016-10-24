package com.lindar.jsonquery.querydsl.sql.domain.gen;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * SAffiliate is a Querydsl query type for SAffiliate
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SAffiliate extends com.querydsl.sql.RelationalPathBase<SAffiliate> {

    private static final long serialVersionUID = -801389808;

    public static final SAffiliate affiliate = new SAffiliate("affiliate");

    public final NumberPath<Long> brandId = createNumber("brandId", Long.class);

    public final StringPath contact = createString("contact");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath email = createString("email");

    public final StringPath externalId = createString("externalId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath type = createString("type");

    public final com.querydsl.sql.PrimaryKey<SAffiliate> primary = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<SBrand> fKe38l4vffjs4n3o4yv8ujkf2ox = createForeignKey(brandId, "id");

    public final com.querydsl.sql.ForeignKey<SPlayer> _fK3km136hryrwkc1bfaevucieqc = createInvForeignKey(id, "affiliate_id");

    public SAffiliate(String variable) {
        super(SAffiliate.class, forVariable(variable), "null", "affiliate");
        addMetadata();
    }

    public SAffiliate(String variable, String schema, String table) {
        super(SAffiliate.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public SAffiliate(Path<? extends SAffiliate> path) {
        super(path.getType(), path.getMetadata(), "null", "affiliate");
        addMetadata();
    }

    public SAffiliate(PathMetadata metadata) {
        super(SAffiliate.class, metadata, "null", "affiliate");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(brandId, ColumnMetadata.named("brand_id").withIndex(12).ofType(Types.BIGINT).withSize(19));
        addMetadata(contact, ColumnMetadata.named("contact").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(email, ColumnMetadata.named("email").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(externalId, ColumnMetadata.named("external_id").withIndex(8).ofType(Types.VARCHAR).withSize(100));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(name, ColumnMetadata.named("name").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(phone, ColumnMetadata.named("phone").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(type, ColumnMetadata.named("type").withIndex(11).ofType(Types.VARCHAR).withSize(100));
    }

}

