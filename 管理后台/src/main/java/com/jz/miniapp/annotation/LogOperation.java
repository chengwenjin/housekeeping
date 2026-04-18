package com.jz.miniapp.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    
    String module() default "";
    
    String action() default "";
    
    String description() default "";
}
