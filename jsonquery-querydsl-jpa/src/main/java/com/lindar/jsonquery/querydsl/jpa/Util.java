package com.lindar.jsonquery.querydsl.jpa;

import com.querydsl.core.types.Predicate;

/**
 * Created by Steven on 30/09/2016.
 */
public class Util {
    public static boolean isPredicateEmpty(Predicate predicate){
        if((predicate == null || "".equals(predicate.toString()))){
            return true;
        }
        return false;
    }
}
