package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.UserRoles;
import ar.edu.itba.paw.models.exceptions.UserValidatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
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
    private final UserService us;
    private static final Logger LOGGER = LoggerFactory.getLogger(PawUserDetailsService.class);


    @Autowired
    public PawUserDetailsService(UserService userService) {
        this.us = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = us.findUserByEmail(username).orElseThrow(() -> {
            LOGGER.warn("Failed login attempt: No user found with username '{}'", username);
            return new UsernameNotFoundException("No user by the name " + username);
        });
        Collection<? extends GrantedAuthority> authorities;


        if(user.isBlocked()){
            LOGGER.warn("User is blocked");
            throw new DisabledException("User is blocked");
        }
        if(!user.isValidated()){
            LOGGER.warn("User is not verified");
            throw new UserValidatedException("User is not verified");
        }
        if (user.getRole() == UserRoles.ADMIN) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else{
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}

