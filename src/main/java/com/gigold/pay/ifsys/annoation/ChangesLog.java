package com.gigold.pay.ifsys.annoation;

import java.lang.annotation.*;

/**
 * 记录变更记录
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChangesLog {
	String description() default "";
	String event() default "";

}
