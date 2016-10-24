package com.lindar.jsonquery.querydsl.sql;

import com.google.common.collect.Maps;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Steven on 06/10/2016.
 */
@Data
public class QuerydslSqlSpec {

    private Map<Key, RelationshipSpec> relationshipSpecs = Maps.newHashMap();
    private Map<Key, JoinSpec> joinSpecs = Maps.newHashMap();
    private Map<Key, String> fieldMappings = Maps.newHashMap();

    @Data
    @Builder
    public static class Key {
        private Class parentClass;
        private String path;
    }

    @Data
    @Builder
    public static class JoinSpec {
        private List<Join> joins;
        private String field;
    }

    @Data
    @Builder
    public static class Join {
        private ForeignKey foreignKey;
        private RelationalPathBase joinEntity;
        private Class joinEntityClass;
    }

    @Data
    @Builder
    public static class RelationshipSpec {
        private RelationalPathBase relationshipEntity;
        private Class relationshipEntityClass;
        private String foreignKey;
        private String primaryKey;
    }
}
