package com.lindar.jsonquery.querydsl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestJPAEntityUtils {

    @Entity
    @Table(name = "tbl_segment")
    static class Segment {
        @Id
        private long segmentId;
        @OneToMany(mappedBy = "owner")
        private List<SegmentMember> members;
    }

    @Entity
    static class SegmentMember {
        @Id
        private long id;
        @ManyToOne
        private Segment owner;
    }

    @Test
    public void tableNameComesFromJakartaTableAnnotation() {
        assertEquals("tbl_segment", JPAEntityUtils.getTableNameFromEntity(Segment.class));
    }

    @Test
    public void primaryKeyComesFromJakartaIdAnnotation() {
        assertEquals("segmentId", JPAEntityUtils.getPrimaryKeyFromEntity(Segment.class));
    }

    @Test
    public void foreignKeyComesFromJakartaOneToManyMappedBy() {
        assertEquals("owner_id", JPAEntityUtils.getForeignKeyFromField(Segment.class, "members"));
    }
}
