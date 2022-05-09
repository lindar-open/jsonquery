package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class InstantGameProvider  {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private ProviderEnum id;

    private String name;
}
