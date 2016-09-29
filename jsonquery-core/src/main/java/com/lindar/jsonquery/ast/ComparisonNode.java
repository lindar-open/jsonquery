package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by stevenhills on 24/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ComparisonNode extends BaseNode {

    private boolean negate = false;
    private String field;
}
