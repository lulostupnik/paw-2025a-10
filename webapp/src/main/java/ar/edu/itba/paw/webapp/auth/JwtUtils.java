package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    private final static long ACCESS_EXPIRATION_TIME_MILLIS = 10 * 60 * 1000; // 10 minutes
    private final static long REFRESH_EXPIRATION_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000; // 1 week
    private final String EMAIL_CLAIM = "email";
    private final String ROLE_CLAIM = "role";
    private final String SELF_URL_CLAIM = "selfUrl";
    private final String TOKEN_TYPE_CLAIM = "tokenType";

    private final Key jwtSecretKey;

    public JwtUtils(String jwtSecretB64) {
        byte[] bytes = Base64.getDecoder().decode(jwtSecretB64);
        jwtSecretKey = Keys.hmacShaKeyFor(bytes);
    }

    private String generateToken(ServletUriComponentsBuilder uriBuilder, User user, JwtType type, long expirationTimeMilis) {
        return Jwts.builder()
                .setClaims(buildClaims(uriBuilder, user, type))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMilis))
                .signWith(jwtSecretKey)
                .compact();
    }

    private Claims buildClaims(ServletUriComponentsBuilder uriBuilder, User user, JwtType type) {
        final Claims claims = Jwts.claims();
        claims.put(TOKEN_TYPE_CLAIM, type);

        if (!type.isRefreshToken()) {
            claims.put(EMAIL_CLAIM, user.getEmail());

            if (user.getRole() != null) {
                claims.put(ROLE_CLAIM, user.getRole());
            }

            final String selfUrl = uriBuilder
                    .path(UriUtils.USERS_URL + "/")
                    .path(String.valueOf(user.getId()))
                    .build().toString();

            claims.put(SELF_URL_CLAIM, selfUrl);
        }
        return claims;
    }

    public String generateAccessToken(ServletUriComponentsBuilder uriBuilder, User user) {
        return generateToken(uriBuilder, user, JwtType.ACCESS, ACCESS_EXPIRATION_TIME_MILLIS);
    }

    public String generateRefreshToken(ServletUriComponentsBuilder uriBuilder, User user) {
        return generateToken(uriBuilder, user, JwtType.REFRESH, REFRESH_EXPIRATION_TIME_MILLIS);
    }

    public JwtDetails validate(String token) {
        try {
            final Jws<Claims> parsed = Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            final Claims claims = parsed.getBody();

            if (claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
                throw new ExpiredJwtException(parsed.getHeader(), claims, "JWT token expired");
            }

            return new JwtDetails.Builder()
                    .setId(claims.getId())
                    .setToken(token)
                    .setEmail(claims.getSubject())
                    .setIssuedDate(claims.getIssuedAt())
                    .setExpirationDate(claims.getExpiration())
                    .setTokenType(JwtType.fromCode((claims.get(TOKEN_TYPE_CLAIM).toString())))
                    .build();

        } catch (SignatureException ex) {
            LOGGER.warn("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.warn("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.warn("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.warn("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("JWT claims string is empty - {}", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.warn("JWT claims {}", ex.getMessage());
        }
        return null;
    }
}
