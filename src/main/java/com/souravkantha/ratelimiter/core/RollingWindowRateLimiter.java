package com.souravkantha.ratelimiter.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RollingWindowRateLimiter  {

    String key();
    
    int requestsRatePerWindow();
    
    WindowTimeUnit timeUnit() default WindowTimeUnit.SECOND;
    
    String fallbackMethod();

}

