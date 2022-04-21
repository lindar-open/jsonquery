package com.lindar.jsonquery.ast;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenhills on 26/09/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDateComparisonNode<T extends Comparable<? super T>> extends ComparisonNode {

    private Operation operation;

    private PresetOperation presetOperation;

    private RelativeOperation relativeOperation;
    private RelativePeriod relativePeriod;
    private RelativeDays relativeDays;
    private int relativeValue;

    private DateOperation dateOperation;
    private List<T> dateValue = Lists.newArrayList();

    public enum DateOperation implements ComparisonOperation {
        EQUALS(1),
        GREATER_THAN(1),
        LESS_THAN(1),
        GREATER_THAN_OR_EQUAL(1),
        LESS_THAN_OR_EQUAL(1),
        BETWEEN(2),
        EMPTY(0);

        private final int maxArgumentCount;
        private final int minArgumentCount;

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
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    @Data
    public static class RelativeDays implements Serializable {
        private Boolean monday = false;
        private Boolean tuesday = false;
        private Boolean wednesday = false;
        private Boolean thursday = false;
        private Boolean friday = false;
        private Boolean saturday = false;
        private Boolean sunday = false;

        public List<DayOfWeek> getDaysOfWeek(){
            List<DayOfWeek> daysOfWeek = new ArrayList<>();
            if(monday != null && monday) daysOfWeek.add(DayOfWeek.MONDAY);
            if(tuesday != null && tuesday) daysOfWeek.add(DayOfWeek.TUESDAY);
            if(wednesday != null && wednesday) daysOfWeek.add(DayOfWeek.WEDNESDAY);
            if(thursday != null && thursday) daysOfWeek.add(DayOfWeek.THURSDAY);
            if(friday != null && friday) daysOfWeek.add(DayOfWeek.FRIDAY);
            if(saturday != null && saturday) daysOfWeek.add(DayOfWeek.SATURDAY);
            if(sunday != null && sunday) daysOfWeek.add(DayOfWeek.SUNDAY);
            return daysOfWeek;
        }
    }
}
