package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Created by stevenhills on 25/09/2016.
 */
@Entity
@Data
public class Player {
    @Id
    private long id;
    private String username;
    private BigDecimal deposits;
    private Instant lastLoginDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player", fetch = FetchType.LAZY)
    private List<PlayerAttrition> attritions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player", fetch = FetchType.LAZY)
    private List<PlayerIGFinancialDaily> igFinancialDaily;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Affiliate affiliate;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "players", fetch = FetchType.LAZY)
    private Set<PlayerList> lists;
}
