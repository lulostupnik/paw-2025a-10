package ar.edu.itba.paw.webapp.filter;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Provider
public class AcceptLanguageFilter implements ContainerRequestFilter {

    private static final String ES_LOCALE = "es";
    private static final String EN_LOCALE = "en";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();

        final Locale selectedOrDefaultLocale = acceptableLanguages.stream()
                .filter(locale -> Arrays.asList(ES_LOCALE, EN_LOCALE)
                        .contains(locale.getLanguage().toLowerCase()))
                .findFirst()
                .orElse(new Locale(EN_LOCALE));

        LocaleContextHolder.setLocale(selectedOrDefaultLocale);
    }
}
