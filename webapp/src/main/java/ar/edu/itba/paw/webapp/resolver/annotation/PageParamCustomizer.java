package ar.edu.itba.paw.webapp.resolver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageParamCustomizer {

    int defaultPage() default 1;
    int defaultSize() default 10;
    String pageParamName() default "page";
    String sizeParamName() default "size";
}