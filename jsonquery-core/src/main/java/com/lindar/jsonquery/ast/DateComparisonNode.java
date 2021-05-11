package com.lindar.jsonquery.ast;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stevenhills on 26/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DateComparisonNode extends ComparisonNode {

    private Operation operation;

    private PresetOperation presetOperation;

    private RelativeOperation relativeOperation;
    private RelativePeriod relativePeriod;
    private RelativeDays relativeDays;
    private int relativeValue;

    private DateOperation dateOperation;
    private List<Date> dateValue = Lists.newArrayList();

    public enum DateOperation implements ComparisonOperation {
        EQUALS(1),
        GREATER_THAN(1),
        LESS_THAN(1),
        GREATER_THAN_OR_EQUAL(1),
        LESS_THAN_OR_EQUAL(1),
        BETWEEN(2),
        EMPTY(0);

        private int maxArgumentCount;
        private int minArgumentCount;

        DateOperation(int argumentCount){
            this.maxArgumentCount = argumentCount;
            this.minArgumentCount = argumentCount;
        }

        DateOperation(int minArgumentCount, int maxArgumentCount){
            this.minArgumentCount = minArgumentCount;
            this.maxArgumentCount = maxArgumentCount;
        }

        public int getMinArgumentCount() {
            return minArgumentCount;
        }

        public int getMaxArgumentCount() {
            return maxArgumentCount;
        }

    }

    public enum PresetOperation {
        TODAY,
        YESTERDAY,
        CURRENT_WEEK,
        LAST_WEEK,
        CURRENT_MONTH,
        LAST_MONTH,
        CURRENT_YEAR,
        LAST_YEAR,
        ANNIVERSARY
    }

    public enum Operation {
        RELATIVE,
        ABSOLUTE,
        PRESET,
    }

    public enum RelativeOperation {
        IN_THE_LAST,
        MORE_THAN,
        IS,
        DAY
    }

    public enum RelativePeriod {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    @Data
    public static class RelativeDays {
        private boolean monday = false;
        private boolean tuesday = false;
        private boolean wednesday = false;
        private boolean thursday = false;
        private boolean friday = false;
        private boolean saturday = false;
        private boolean sunday = false;

        public List<DayOfWeek> getDaysOfWeek(){
            List<DayOfWeek> daysOfWeek = new ArrayList<>();
            if(monday) daysOfWeek.add(DayOfWeek.MONDAY);
            if(tuesday) daysOfWeek.add(DayOfWeek.TUESDAY);
            if(wednesday) daysOfWeek.add(DayOfWeek.WEDNESDAY);
            if(thursday) daysOfWeek.add(DayOfWeek.THURSDAY);
            if(friday) daysOfWeek.add(DayOfWeek.FRIDAY);
            if(saturday) daysOfWeek.add(DayOfWeek.SATURDAY);
            if(sunday) daysOfWeek.add(DayOfWeek.SUNDAY);
            return daysOfWeek;
        }
    }

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public <R, C> R accept(JsonQueryVisitor<R, C> v) {
        return v.visit(this, null);
    }
}
