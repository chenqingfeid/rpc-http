package com.sdklite.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents HTTP {@code GET} method
 * 
 * @author johnsonlee
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {

    /**
     * The HTTP request headers
     * 
     * @return HTTP reqeust headers
     */
    String[] headers() default {};

}
