package ar.edu.itba.paw.webapp.config;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private static final Locale EN = new Locale("en");
    private static final Locale ES = new Locale("es");
    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(EN, ES);
    private static final Locale DEFAULT_LOCALE = EN;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale requestLocale = super.resolveLocale(request);
        return resolveSupportedOrDefault(requestLocale);
    }

    @Override
    public void setLocale(HttpServletRequest request,
                          HttpServletResponse response,
                          Locale locale) {
        setDefaultLocale(resolveSupportedOrDefault(locale));
    }

    /**
     * Returns the supported locale (en or es) if matched, otherwise returns the default.
     */
    private Locale resolveSupportedOrDefault(Locale locale) {
        if (locale == null) {
            return DEFAULT_LOCALE;
        }

        String language = locale.getLanguage().toLowerCase();

        for (Locale supported : SUPPORTED_LOCALES) {
            if (supported.getLanguage().equals(language)) {
                return supported;
            }
        }

        return DEFAULT_LOCALE;
    }
}