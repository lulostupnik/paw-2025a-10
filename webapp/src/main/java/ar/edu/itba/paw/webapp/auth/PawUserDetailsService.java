package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.UserPassword;
import ar.edu.itba.paw.webapp.exception.EmailNotVerifiedException;
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

    @Autowired
    public PawUserDetailsService(UserService userService) {
        this.us = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final UserPassword user = us.findByEmailWithPass(username).orElseThrow(() ->
                new UsernameNotFoundException("No user by the name " + username));
        Collection<? extends GrantedAuthority> authorities;

        if(user.isBlocked()){
            throw new DisabledException("User is blocked");
        }
        if(!user.isVerified()){
            throw new EmailNotVerifiedException("User is not verified");
        }
        if(user.getRole().equals("admin")) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else{
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}

