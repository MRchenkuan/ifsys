package com.gigold.pay.ifsys.annoation;

/**
 * Created by chenkuan
 * on 2016/10/20.
 */

import java.lang.annotation.*;

/**
 * 邮件通知功能
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Notice {
    String value() default "";
}
