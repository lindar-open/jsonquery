package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Steven on 26/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumComparisonNode extends BasicComparisonNode<String, EnumComparisonOperation> {
    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}
