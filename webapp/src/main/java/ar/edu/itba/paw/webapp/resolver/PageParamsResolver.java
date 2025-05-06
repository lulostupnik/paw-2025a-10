package ar.edu.itba.paw.webapp.resolver;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.webapp.resolver.anotation.PageParamDefaults;
import ar.edu.itba.paw.webapp.resolver.anotation.PageParamPrefix;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageParamsResolver implements HandlerMethodArgumentResolver {

    // Global defaults
    private static final int GLOBAL_DEFAULT_PAGE = 1;
    private static final int GLOBAL_DEFAULT_SIZE = 10;
    private static final int GLOBAL_MIN_SIZE = 1;
    private static final int GLOBAL_MAX_SIZE = 100;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageParams.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        String prefix = "";
        PageParamPrefix suffixAnnotation = parameter.getParameterAnnotation(PageParamPrefix.class);
        if (suffixAnnotation != null) {
            prefix = suffixAnnotation.value();
        }

        String pageParamName = prefix.isEmpty() ? "page": prefix + "Page";
        String sizeParamName = prefix.isEmpty() ? "size": prefix + "Size";


        // Optional controller override
        PageParamDefaults defaults = parameter.getParameterAnnotation(PageParamDefaults.class);

        int defaultPage = (defaults != null && defaults.defaultPage() > 0) ? defaults.defaultPage() : GLOBAL_DEFAULT_PAGE;
        int defaultSize = (defaults != null && defaults.defaultSize() > 0) ? defaults.defaultSize() : GLOBAL_DEFAULT_SIZE;
//        int minSize = (defaults != null && defaults.minSize() > 0) ? defaults.minSize() : GLOBAL_MIN_SIZE;
//        int maxSize = (defaults != null && defaults.maxSize() > 0) ? defaults.maxSize() : GLOBAL_MAX_SIZE;

        int page = parseOrDefault(webRequest.getParameter(pageParamName), defaultPage);
        int size = parseOrDefault(webRequest.getParameter(sizeParamName), defaultSize);

//        size = Math.max(minSize, Math.min(maxSize, size));
        size = Math.max(GLOBAL_MIN_SIZE, Math.min(GLOBAL_MAX_SIZE, size));
        return new PageParams(page, size);
    }

    private int parseOrDefault(String param, int defaultValue) {
        try {
            return param != null ? Integer.parseInt(param) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
