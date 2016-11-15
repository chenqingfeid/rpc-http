package com.sdklite.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents HTTP {@code PUT} method
 * 
 * @author johnsonlee
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {

    /**
     * The HTTP request content type
     * 
     * @return HTTP request content type
     */
    String contentType();

    /**
     * The HTTP request headers
     * 
     * @return HTTP reqeust headers
     */
    String[] headers() default {};

}
