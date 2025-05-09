package ar.edu.itba.paw.webapp.paging;

import ar.edu.itba.paw.models.PageParams;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageParamsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageParams.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {


        PageParamCustomizer defaults = parameter.getParameterAnnotation(PageParamCustomizer.class);
        int defaultPage = (defaults != null && defaults.defaultPage() > 0) ? defaults.defaultPage() : PaginationConstants.DEFAULT_PAGE;
        int defaultSize = (defaults != null && defaults.defaultSize() > 0) ? defaults.defaultSize() : PaginationConstants.DEFAULT_SIZE;

        String pageParamName = (defaults != null && !defaults.pageParamName().isEmpty()) ? defaults.pageParamName():PaginationConstants.PAGE_PARAM_NAME;
        String sizeParamName = (defaults != null && !defaults.sizeParamName().isEmpty()) ? defaults.sizeParamName():PaginationConstants.SIZE_PARAM_NAME;

        int page = parsePositiveIntOrDefault(webRequest.getParameter(pageParamName), defaultPage);
        int size = parsePositiveIntOrDefault(webRequest.getParameter(sizeParamName), defaultSize);
        size = Math.max(PaginationConstants.MIN_SIZE, Math.min(PaginationConstants.MAX_SIZE, size));

        return new PageParams(page, size);
    }

    private int parsePositiveIntOrDefault(String param, int defaultValue) {
        if (param == null) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(param);
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException  e) {
            return defaultValue;
        }
    }
}
