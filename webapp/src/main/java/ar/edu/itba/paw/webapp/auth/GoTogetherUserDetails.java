package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * GoTogether UserDetails implementation that includes the user's database ID.
 * This allows us to access the user ID in @PreAuthorize expressions
 * without needing to look up the user by email every time.
 */
public class GoTogetherUserDetails extends User {

    private final long userId;

    public GoTogetherUserDetails(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            long userId
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
