package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "player_ig_financial_daily", uniqueConstraints={
        @UniqueConstraint(columnNames = {"player_id", "instant_game_id", "period"})
})
@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class PlayerIGFinancialDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="player_id", referencedColumnName="id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="instant_game_id", referencedColumnName="id")
    private InstantGame instantGame;

    private BigDecimal wagers = BigDecimal.ZERO;
}
