package com.lindar.jsonquery.querydsl;

import com.google.common.base.CaseFormat;
import com.google.common.primitives.Primitives;
import com.querydsl.core.util.ReflectionUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Steven on 05/10/2016.
 */
@UtilityClass
@Slf4j
public class JPAEntityUtils {

    public static String getTableNameFromEntity(Class entityClass){

        // try and find the table name from the @table annotation
        Table tableAnnotation = (Table)entityClass.getAnnotation(Table.class);

        if(tableAnnotation != null){
            if(!StringUtils.isEmpty(tableAnnotation.name())){
                return tableAnnotation.name();
            }
        }

        // table name not found.. try and guess
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName());
    }

    public static String getPrimaryKeyFromEntity(Class entityClass){

        // try and find primary key from @id annotation
        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(entityClass, Id.class);
        if(!fieldsListWithAnnotation.isEmpty()){
            return fieldsListWithAnnotation.get(0).getName();
        }

        // primary key not found, use default key
        return "id";
    }

    public static String getForeignKeyFromField(Class entityClass, String fieldName){

        Field field = FieldUtils.getField(entityClass, fieldName, true);

        if(field == null){
            throw new IllegalArgumentException("Field not found on class");
        }

        OneToMany annotation = field.getAnnotation(OneToMany.class);

        if(annotation != null && !StringUtils.isEmpty(annotation.mappedBy())){
            return annotation.mappedBy() + "_id";
        }

        Class relatedClass = getFieldClassFromProperty(entityClass, fieldName);

        String className = entityClass.getSimpleName();
        className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
        if(FieldUtils.getField(relatedClass, className) == null){
            throw new IllegalArgumentException();
        }
        return className + "_id";
    }

    public static String getJoinTableNameFromField(Class entityClass, String fieldName){

        Class clazz = getFieldClassFromProperty(entityClass, fieldName);
        if(clazz == null){
            throw new IllegalArgumentException("Field not found on class");
        }

        return getTableNameFromEntity(clazz);
    }


    public static Class<?> getFieldClassFromProperty(Class entityClass, String property){
        while(!entityClass.equals(Object.class)) {
            try {
                Field e = entityClass.getDeclaredField(property);
                if(Map.class.isAssignableFrom(e.getType())) {
                    return ReflectionUtils.getTypeParameterAsClass(e.getGenericType(), 1);
                }

                if(Collection.class.isAssignableFrom(e.getType())) {
                    return ReflectionUtils.getTypeParameterAsClass(e.getGenericType(), 0);
                }

                return Primitives.wrap(e.getType());
            } catch (NoSuchFieldException var5) {
                entityClass = entityClass.getSuperclass();
            }
        }

        return null;
    }

    public static String convertPropertyToSqlField(String property){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, property);
    }
}
