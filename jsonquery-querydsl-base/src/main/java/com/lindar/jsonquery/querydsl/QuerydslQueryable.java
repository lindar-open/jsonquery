package com.lindar.jsonquery.querydsl;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Created by Steven on 23/01/2017.
 */
public interface QuerydslQueryable<E> {
    Predicate toPredicate(PathBuilder<E> pathBuilder);
}
