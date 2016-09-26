package com.lindar.jsonquery.ast;

import lombok.Data;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
public abstract class ComparisonNode extends BaseNode {

    private boolean negate = false;
    private String field;
}
