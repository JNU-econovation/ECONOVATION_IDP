package com.econovation.idpcommon.annotation;


import com.econovation.idpcommon.interfaces.SwaggerExampleExceptions;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorExceptionsExample {
    Class<? extends SwaggerExampleExceptions> value();
}
