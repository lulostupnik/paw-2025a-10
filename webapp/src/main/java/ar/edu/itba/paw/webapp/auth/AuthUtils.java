package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {

    private AuthUtils() {
    }

    public static Long getCurrentUserId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        final Object principal = auth.getPrincipal();
        if (principal instanceof GoTogetherUserDetails) {
            return ((GoTogetherUserDetails) principal).getUserId();
        }
        return null;
    }
}
