package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.UserRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;

@Component
public class PawUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PawUserDetailsService.class);

    @Autowired
    public PawUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userService.findUserByEmail(username).orElseThrow(() -> {
            LOGGER.warn("Failed login attempt: No user found with username '{}'", username);
            return new UsernameNotFoundException("No user by the name " + username);
        });

        LOGGER.debug("Loading user details for: {}", username);

        Collection<? extends GrantedAuthority> authorities = determineAuthorities(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                user.isValidated(),
                true,
                true,
                !user.isBlocked(),
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> determineAuthorities(UserRoles role) {
        return switch (role) {
            case ADMIN -> List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            case USER -> List.of(new SimpleGrantedAuthority("ROLE_USER"));
        };
    }
}