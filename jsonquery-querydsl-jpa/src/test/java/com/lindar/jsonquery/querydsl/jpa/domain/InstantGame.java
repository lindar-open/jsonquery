package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Data
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class InstantGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="provider", referencedColumnName="id")
    private InstantGameProvider provider;
}