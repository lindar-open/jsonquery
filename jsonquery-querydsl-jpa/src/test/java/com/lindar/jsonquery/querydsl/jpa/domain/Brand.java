package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Steven on 26/09/2016.
 */
@Entity
@Data
public class Brand {
    @Id
    private long id;
    private String type;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private BrandNote note;
}