package ar.edu.itba.paw.webapp.paging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageParamCustomizer {

    int defaultPage() default PaginationConstants.DEFAULT_PAGE;
    int defaultSize() default PaginationConstants.DEFAULT_SIZE;
    String pageParamName() default PaginationConstants.PAGE_PARAM_NAME;
    String sizeParamName() default PaginationConstants.SIZE_PARAM_NAME;
}