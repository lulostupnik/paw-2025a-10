package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.interfaces.services.TokenService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuthAnywhereFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_TYPE = "Basic";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_TYPE)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String credsBase64 = authHeader.substring(AUTH_HEADER_TYPE.length() + 1).trim();
            final byte[] credsBytes = Base64.decode(credsBase64.getBytes(StandardCharsets.UTF_8));
            final String credsDecoded = new String(credsBytes, StandardCharsets.UTF_8);

            final int indexOfColon = credsDecoded.indexOf(':');
            final String email = credsDecoded.substring(0, indexOfColon);
            final String credentials = credsDecoded.substring(indexOfColon + 1);

            final Optional<User> maybeUser = userService.findUserByEmail(email);
            if (maybeUser.isPresent()) {
                final User user = maybeUser.get();
                final Optional<Token> maybeToken = tokenService.getByToken(credentials);

                if (maybeToken.isPresent() && tokenService.isTokenValid(maybeToken.get(), user.getId())) {
                    userService.verifyUser(user.getId());
                    authenticateAs(request, user.getEmail());
                    tokenService.delete(maybeToken.get());
                    issueTokens(request, response, user);
                } else if (maybeToken.isEmpty()) {
                    // Password login. The AuthenticationManager rejects unverified (disabled) or blocked
                    // (locked) accounts; that just leaves the request anonymous.
                    final Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, credentials)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    issueTokens(request, response, user);
                }

            }
        } catch (Exception e) {
            // Si el token no es válido esto pasa a ser una request anonima
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void issueTokens(final HttpServletRequest request, final HttpServletResponse response, final User user) {
        final ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
        uriBuilder.path("/api");
        response.setHeader("X-GoTogether-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user));
        response.setHeader("X-GoTogether-RefreshToken", jwtTokenUtil.generateRefreshToken(uriBuilder, user));
    }

    private void authenticateAs(final HttpServletRequest request, final String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
