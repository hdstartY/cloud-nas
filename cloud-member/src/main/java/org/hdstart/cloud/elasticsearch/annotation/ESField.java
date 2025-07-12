package org.hdstart.cloud.elasticsearch.annotation;

import org.hdstart.cloud.constant.es.ESFieldType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ESField {
    ESFieldType type(); // text, keyword, long, integer, date, etc.
    boolean keywordSubField() default false; // 是否自动添加 keyword 子字段
}
