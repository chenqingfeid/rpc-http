package com.sdklite.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents HTTP {@code POST} method
 * 
 * @author johnsonlee
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {

    /**
     * The HTTP request content type
     * 
     * @return HTTP request content type
     */
    String contentType() default "application/x-www-form-urlencoded";

    /**
     * The HTTP request headers
     * 
     * @return HTTP reqeust headers
     */
    String[] headers() default {};

}
