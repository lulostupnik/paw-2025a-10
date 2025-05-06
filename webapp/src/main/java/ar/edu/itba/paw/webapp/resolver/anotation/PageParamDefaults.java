package ar.edu.itba.paw.webapp.resolver.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageParamDefaults {
    int defaultPage() default -1;
    int defaultSize() default -1;
//    int maxSize() default -1;
//    int minSize() default -1;
}