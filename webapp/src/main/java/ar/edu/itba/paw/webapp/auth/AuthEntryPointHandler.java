package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String context = httpServletRequest.getContextPath();
        boolean hasRememberMeCookie = false;
        if(httpServletRequest.getCookies() != null) {
            for (var cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("remember-me")) {
                    hasRememberMeCookie = true;
                    break;
                }
            }
        }
        if(hasRememberMeCookie || auth instanceof RememberMeAuthenticationToken) {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else {
            httpServletResponse.sendRedirect(context + "/login");
        }
    }
}
