package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.Data;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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