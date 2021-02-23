package com.cwj.myapplication.sdk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wenjia.Cheng  cwj1714@163.com
 * @date 2021/2/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface IgnoreTraceMethodCostMethod {
}
