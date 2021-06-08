package com.lindar.jsonquery.querydsl;

import com.lindar.jsonquery.ast.BaseDateComparisonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static <T extends Comparable> List<LocalDate> fromRelativeDateDays(BaseDateComparisonNode<T> dateComparisonNode) {
        if (dateComparisonNode.getRelativeDays() == null) return new ArrayList<>();

        return dateComparisonNode.getRelativeDays().getDaysOfWeek().stream()
                .map(dayOfWeek -> LocalDate.now().minusWeeks(dateComparisonNode.getRelativeValue()).with(dayOfWeek))
                .collect(Collectors.toList());
    }
}
