package com.github.baoti.pioneer.app;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by liuyedong on 14-12-25.
 */
@Qualifier
@Target({FIELD, PARAMETER, METHOD})
@Documented
@Retention(RUNTIME)
public @interface ForApp {
}
