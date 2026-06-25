package ar.edu.itba.paw.webapp.config;


import ar.edu.itba.paw.webapp.auth.*;
import ar.edu.itba.paw.webapp.auth.filters.AuthAnywhereFilter;
import ar.edu.itba.paw.webapp.auth.filters.JwtFilter;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("ar.edu.itba.paw.webapp.auth")
@PropertySource("classpath:application.properties")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PawUserDetailsService userDetailsService;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;
    @Autowired
    private JwtFilter jwtTokenFilter;

    @Autowired
    private AuthAnywhereFilter authAnywhereFilter;

    @Autowired
    private MessageSource messageSource;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthConfig.class);



    @Value("${auth.key}")
    private String authKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthEntryPointHandler authEntryPointHandler() {
        return new AuthEntryPointHandler();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        LOGGER.info("Configuring security (has key from properties file = {})", !authKey.isEmpty());
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                // Allow CORS preflight requests to pass through security.
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/register", "/login", "/reset-password", "/forgot_pass", "/validate", "/not-verified").anonymous()

                .antMatchers(HttpMethod.HEAD, "/api/").access("isAuthenticated()")
                .antMatchers(HttpMethod.GET, "/api/").permitAll()

                .antMatchers(HttpMethod.GET, "/api/users").access("hasRole('ADMIN')") // TODO: revisar
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.PATCH, "/api/users/{id}").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/interests").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}/interests/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}/rating").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/{id}/interests").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/interests").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}/interest").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}/interests/*").access("@accessHelper.isCurrentUser(#id)")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/profilePicture").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/profilePicture").access("@accessHelper.isCurrentUser(#id)")


                .antMatchers(HttpMethod.GET, "/api/cities", "/api/cities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/cities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/cities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/cities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/cities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/careers", "/api/careers/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/careers").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/careers/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/careers/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/careers/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/universities", "/api/universities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/universities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/universities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/universities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/universities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/interests", "/api/interests/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/interests").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/interests/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/interests/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/interests/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/journeys", "/api/journeys/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/journeys").access("isAuthenticated()")
                .antMatchers(HttpMethod.PUT, "/api/journeys/{id}").access("@accessHelper.isUserJourneyOwner(#id)")
                .antMatchers(HttpMethod.PATCH, "/api/journeys/{id}").access("@accessHelper.isUserJourneyOwner(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/{id}").access("@accessHelper.isUserJourneyOwner(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/journeys/{journeyId}/tips").access("@accessHelper.isUserJourneyOwner(#journeyId)")
                .antMatchers(HttpMethod.PUT, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId)")
                .antMatchers(HttpMethod.PATCH, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId)")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/journeys/{id}/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/*/responses/*").access("hasRole('ADMIN')")

                // Event attendance (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.GET, "/api/events/*/attendance").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/*/attendance").access("isAuthenticated()")
                .antMatchers(HttpMethod.GET, "/api/events/{eventId}/attendances").access("@accessHelper.isUserEventOwner(#eventId)")
                .antMatchers(HttpMethod.GET, "/api/events/{eventId}/attendances/{userId}").access("@accessHelper.isUserEventOwner(#eventId)")
                .antMatchers(HttpMethod.POST, "/api/events/*/attendances").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/*/attendances/*").access("isAuthenticated()")

                // Event responses (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.POST, "/api/events/*/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/*/responses/*").access("hasRole('ADMIN')")

                // Event ratings (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.POST, "/api/events/{eventId}/ratings").access("@accessHelper.isUserEventAttendee(#eventId)")
                .antMatchers(HttpMethod.PUT, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId)")
                .antMatchers(HttpMethod.DELETE, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId) or hasRole('ADMIN')")

                // Events general (DESPUÉS de las reglas específicas)
                .antMatchers(HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/events").access("isAuthenticated()")
                .antMatchers(HttpMethod.PUT, "/api/events/{id}").access("@accessHelper.isUserEventOwner(#id)")
                .antMatchers(HttpMethod.PATCH, "/api/events/{id}").access("@accessHelper.isUserEventOwner(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/events/{id}").access("@accessHelper.isUserEventOwner(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/reports", "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")
                .antMatchers(HttpMethod.POST, "/api/reports").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")
                .antMatchers(HttpMethod.PATCH, "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")

                .antMatchers(HttpMethod.GET, "/api/images/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/images").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/images/*").access("hasRole('ADMIN')")


                .antMatchers("/**").access("isAuthenticated()")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    // response.addHeader("WWW-Authenticate", "Basic realm=\"GoTogether\"");
                    writeErrorResponse(response, Response.Status.UNAUTHORIZED, ex.getMessage());
                })

                .accessDeniedHandler((request, response, ex) -> {
                    String message;
                    try {
                        message = messageSource.getMessage("error.accessDenied", null, "Access denied. You do not have the necessary permissions.", LocaleContextHolder.getLocale());
                    } catch (Exception e) {
                        message = "Access denied. You do not have the necessary permissions.";
                    }
                    writeErrorResponse(response, Response.Status.FORBIDDEN, message);
                })

                // Disable client-side cache handling
                .and().headers().cacheControl().disable()

                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authAnywhereFilter, UsernamePasswordAuthenticationFilter.class)

                .cors().and().csrf().disable();
    }

    private void writeErrorResponse(HttpServletResponse response, Response.Status status, String message) throws IOException {
        response.setStatus(status.getStatusCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorDto.fromException(status, message));
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/resources/css/**", "/resources/js/**", "/resources/images/**",
                        "/resources/favicon.ico", "/errors/*", "/resources/icons/**");
    }
    @Bean
    public JwtUtils jwtTokenUtil(@Value("${jwtSecret.key}") String jwtSecret) {
        return new JwtUtils(jwtSecret);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setExposedHeaders(Arrays.asList(
                "X-GoTogether-AuthToken",
                "X-GoTogether-RefreshToken",
                "WWW-Authenticate",
                "ETag",
                "Last-Modified",
                "Content-Disposition",
                "Location",
                "Link",
                "X-Total-Count"
        ));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
