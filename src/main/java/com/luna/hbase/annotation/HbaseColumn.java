package com.luna.hbase.annotation;

import java.lang.annotation.*;

/**
 * @author Austin Wong
 * @description Hbase column注解
 * @date 2022/4/1 10:37
 * @since JDK1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HbaseColumn {
    String value();
}
