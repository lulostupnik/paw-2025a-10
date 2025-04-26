package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

@Component
public class PawUserDetailsService implements UserDetailsService {
    private final UserService us;

    @Autowired
    public PawUserDetailsService(UserService userService) {
        this.us = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final UserPassword user = us.findByEmailWithPass(username).orElseThrow(() ->
                new UsernameNotFoundException("No user by the name " + username));
        final Collection<? extends GrantedAuthority> authorities = Arrays.asList( //@TODO check this
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}

