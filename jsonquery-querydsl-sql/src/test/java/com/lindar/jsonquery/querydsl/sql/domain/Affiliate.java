package com.lindar.jsonquery.querydsl.sql.domain;

import lombok.Data;

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
    private String type;
}