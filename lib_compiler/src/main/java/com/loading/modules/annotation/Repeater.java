package com.loading.modules.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeater {
    /**
     * auto parse only support format I***Service
     * default from I***Service to ***ModuleMgr
     * <p>
     * otherwise set repeaterName
     */
    String repeaterName() default "";

    /**
     * default repeater package is element type package
     */
    String repeaterPackage() default "";
}
