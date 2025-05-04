package ar.edu.itba.paw.webapp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    public CustomAuthenticationFailureHandler() {
        // Setting default failure URL
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        LOGGER.debug("Authentication failure: {}", exception.getClass().getName());
        LOGGER.debug("Exception message: {}", exception.getMessage());

        String redirectUrl = "/login?error=true";
        Throwable cause = exception.getCause();

        if (cause instanceof DisabledException) {
            LOGGER.info("DisabledException caught: {}", exception.getMessage());
            redirectUrl = "/blocked?reason=" + exception.getMessage();
        } else if (cause instanceof LockedException) {
            redirectUrl = "/blocked?reason=account_locked";
        } else if (cause instanceof AccountStatusException) {
            redirectUrl = "/blocked?reason=account_issue";
        } else if (cause instanceof BadCredentialsException) {
            redirectUrl = "/login?error=bad_credentials";
        }

        LOGGER.info("Redirecting to: {}", redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}