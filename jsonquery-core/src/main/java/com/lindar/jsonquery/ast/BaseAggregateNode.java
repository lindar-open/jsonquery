package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@EqualsAndHashCode(of="reference")
public abstract class BaseAggregateNode implements AggregateNode, Serializable {
    private String reference = UUID.randomUUID().toString();
    private boolean enabled = true;
}
