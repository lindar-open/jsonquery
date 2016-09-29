package com.lindar.jsonquery.querydsl.jpa.domain;

import com.google.common.collect.Sets;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Steven on 29/09/2016.
 */
@Entity
@Table(name = "player_list")
@Data
public class PlayerList  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Player> players = Sets.newHashSet();
}
