package com.gigold.pay.ifsys.annoation;
import com.gigold.pay.ifsys.bo.TypeRole;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenkuan
 * on 2017/2/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Authority {
    // 默认验证
    TypeRole value() default TypeRole.Anyone;

}