package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Steven on 26/09/2016.
 */
@Entity
@Data
public class Affiliate {
    @Id
    private long id;

    @Convert(converter = AffiliateTypeConverter.class)
    private AffiliateType type;
}