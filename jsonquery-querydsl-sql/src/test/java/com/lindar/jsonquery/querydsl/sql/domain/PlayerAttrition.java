package com.lindar.jsonquery.querydsl.sql.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by stevenhills on 25/09/2016.
 */
@Entity
@Data
public class PlayerAttrition {
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_id", referencedColumnName="id")
    private Player player;

    @Temporal(value = TemporalType.DATE)
    private Date period;

    private BigDecimal deposits;

    private String promocode;

}
