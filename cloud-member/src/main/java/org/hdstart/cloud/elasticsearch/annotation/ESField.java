package org.hdstart.cloud.elasticsearch.annotation;

import org.hdstart.cloud.elasticsearch.constant.ESFieldType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ESField {
    ESFieldType type(); // text, keyword, long, integer, date, etc.
    boolean keywordSubField() default false; // 是否自动添加 keyword 子字段
    boolean isList() default false;       // 是否是集合类型（List）
    boolean nested() default false;       // 是否声明为 nested 类型
}
