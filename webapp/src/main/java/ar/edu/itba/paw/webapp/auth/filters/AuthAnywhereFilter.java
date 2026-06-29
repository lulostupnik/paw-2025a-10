package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.interfaces.services.TokenService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JwtUtils;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuthAnywhereFilter extends OncePerRequestFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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

    @Autowired
    private MessageSource messageSource;

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

            int indexOfColon = credsDecoded.indexOf(':');
            final String email = credsDecoded.substring(0, indexOfColon);
            final String credentials = credsDecoded.substring(indexOfColon + 1);

            final Optional<User> maybeUser = userService.findUserByEmail(email);
            if (maybeUser.isPresent()) {
                final User user = maybeUser.get();
                final Optional<Token> maybeToken = tokenService.getByToken(credentials);

                if (maybeToken.isPresent()) {
                    final Token tkn = maybeToken.get();
                    if (tkn.isExpired() || tkn.getUser().getId().longValue() != user.getId()) {
                        writeError(request, response, Response.Status.UNAUTHORIZED, "auth.invalidCredentials");
                        return;
                    }
                    authenticateAs(request, user.getEmail());
                    tokenService.delete(tkn);          // OTP
                } else if (!user.isValidated()) {
                    userService.resendVerificationEmail(user.getEmail());
                    writeError(request, response, Response.Status.FORBIDDEN, "exception.UserNotVerifiedException");
                    return;
                } else {
                    final Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, credentials)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

                final ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
                uriBuilder.path("/api");
                response.setHeader("X-GoTogether-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user));
                response.setHeader("X-GoTogether-RefreshToken", jwtTokenUtil.generateRefreshToken(uriBuilder, user));
            }
        } catch (Exception e) {
            writeError(request, response, Response.Status.UNAUTHORIZED, "auth.invalidCredentials");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateAs(final HttpServletRequest request, final String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void writeError(final HttpServletRequest request, final HttpServletResponse response,
                            final Response.Status status, final String messageKey) throws IOException {
        SecurityContextHolder.clearContext();
        final String message = messageSource.getMessage(messageKey, null, messageKey, request.getLocale());
        response.setStatus(status.getStatusCode());
        if (status == Response.Status.UNAUTHORIZED) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"GoTogether\"");
        }
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OBJECT_MAPPER.writeValue(response.getWriter(), ErrorDto.fromException(status, message));
    }
}
