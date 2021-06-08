package com.lindar.jsonquery.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Created by stevenhills on 26/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DateLocalDateComparisonNode extends BaseDateComparisonNode<LocalDate> {

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return v.visit(this, null);
    }
}
