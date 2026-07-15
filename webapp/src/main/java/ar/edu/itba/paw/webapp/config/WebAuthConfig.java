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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("ar.edu.itba.paw.webapp.auth")
@PropertySource("classpath:application.properties")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private GoTogetherUserDetailsService userDetailsService;
    @Autowired
    private AccessHelper accessHelper;
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
                .antMatchers(HttpMethod.HEAD, "/api/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/").permitAll()

                .antMatchers(HttpMethod.GET, "/api/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()

                .antMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.PATCH, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/interests").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}/interests/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}/rating").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/{id}/interests").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}/interests/*").access("@accessHelper.isCurrentUser(#id)")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/profilePicture").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/profilePicture").access("@accessHelper.isCurrentUser(#id)")


                .antMatchers(HttpMethod.GET, "/api/cities", "/api/cities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/cities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/cities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/cities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/countries", "/api/countries/*").permitAll()

                .antMatchers(HttpMethod.GET, "/api/careers", "/api/careers/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/careers").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/careers/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/careers/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/universities", "/api/universities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/universities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/universities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/universities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/interests", "/api/interests/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/interests").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/interests/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/interests/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/journeys", "/api/journeys/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/journeys").access("isAuthenticated()")
                .antMatchers(HttpMethod.PATCH, "/api/journeys/{id}").access("isAuthenticated()")

                .antMatchers(HttpMethod.POST, "/api/journeys/{journeyId}/tips").access("@accessHelper.isUserJourneyOwner(#journeyId)")
                .antMatchers(HttpMethod.PATCH, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId)")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/journeys/{id}/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.PATCH, "/api/journeys/*/responses/*").access("isAuthenticated()")

                // Event attendance (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.GET, "/api/events/{eventId}/attendances").access("@accessHelper.isUserEventOwner(#eventId)")
                .antMatchers(HttpMethod.GET, "/api/events/{eventId}/attendances/{userId}").access("@accessHelper.isCurrentUser(#userId) or @accessHelper.isUserEventOwner(#eventId)")
                .antMatchers(HttpMethod.POST, "/api/events/{eventId}/attendances").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/{eventId}/attendances/{userId}").access("@accessHelper.isCurrentUser(#userId) or @accessHelper.isUserEventOwner(#eventId)")

                // Event responses (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.POST, "/api/events/*/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.PATCH, "/api/events/*/responses/*").access("isAuthenticated()")

                // Event ratings (específicos ANTES de la regla general)
                .antMatchers(HttpMethod.POST, "/api/events/{eventId}/ratings").access("@accessHelper.isUserEventAttendee(#eventId)")
                .antMatchers(HttpMethod.PUT, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId)")
                .antMatchers(HttpMethod.DELETE, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId) or hasRole('ADMIN')")

                // Event flyer: only the event owner may replace it (específico ANTES de la regla general)
                .antMatchers(HttpMethod.PUT, "/api/events/{id}/flyer").access("@accessHelper.isUserEventOwner(#id)")

                // Events general (DESPUÉS de las reglas específicas)
                .antMatchers(HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/events").access("isAuthenticated()")
                .antMatchers(HttpMethod.PATCH, "/api/events/{id}").access("isAuthenticated()")

                .antMatchers(HttpMethod.GET, "/api/reports", "/api/reports/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/api/reports").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/reports/{id}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH, "/api/reports/{id}").access("hasRole('ADMIN')")

                .antMatchers("/**").access("isAuthenticated()")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    response.addHeader("WWW-Authenticate", "Bearer realm=\"GoTogether\"");
                    final String message = messageSource.getMessage("error.unauthorized", null,
                            "Authentication is required to access this resource.", LocaleContextHolder.getLocale());
                    writeErrorResponse(response, Response.Status.UNAUTHORIZED, message);
                })

                .accessDeniedHandler((request, response, ex) -> {
                    final String message = messageSource.getMessage("error.accessDenied", null,
                            "Access denied. You do not have the necessary permissions.", LocaleContextHolder.getLocale());
                    writeErrorResponse(response, Response.Status.FORBIDDEN, message);
                })

                // Disable client-side cache handling
                .and().headers().cacheControl().disable()

                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authAnywhereFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf().disable();
    }

    private void writeErrorResponse(HttpServletResponse response, Response.Status status, String message) throws IOException {
        response.setStatus(status.getStatusCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorDto.fromException(status, message));
    }

    @Bean
    public JwtUtils jwtTokenUtil(@Value("${jwtSecret.key}") String jwtSecret) {
        return new JwtUtils(jwtSecret);
    }
}
