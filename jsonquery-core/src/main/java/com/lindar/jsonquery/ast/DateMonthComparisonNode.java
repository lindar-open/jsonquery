package com.lindar.jsonquery.ast;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Created by stevenhills on 26/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DateMonthComparisonNode extends DateComparisonNode {

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return v.visit(this, null);
    }
}
