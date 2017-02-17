package com.lindar.jsonquery.querydsl.jpa.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Steven on 17/02/2017.
 */
@EqualsAndHashCode(of="type")
public class AffiliateType implements Serializable {

    private static final Map<String, AffiliateType> TYPES = new LinkedHashMap<>();

    public static final AffiliateType ORGANIC = new AffiliateType("ORGANIC", "Organic");

    public static AffiliateType getInstance(final String type){
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public AffiliateType(){}

    public AffiliateType(String type, String friendlyType){
        this.friendlyType = friendlyType;
        this.setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }
}

