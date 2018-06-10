/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckType {
    String id();

    String name();

    int timeout() default 1;

    int maxVl() default 1;

    boolean experimental() default false;

    boolean alert() default true;

    boolean cancel() default true;

    boolean ban() default true;
}
