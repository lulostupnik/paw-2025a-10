package ar.edu.itba.paw.webapp.config;


import ar.edu.itba.paw.webapp.auth.*;
import ar.edu.itba.paw.webapp.auth.filters.AuthAnywhereFilter;
import ar.edu.itba.paw.webapp.auth.filters.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

                .antMatchers(HttpMethod.GET, "/api/users").permitAll()// TODO:revisar
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.PATCH, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}").access("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/interests").permitAll()// TODO:revisar
                .antMatchers(HttpMethod.GET, "/api/users/{id}/rating").permitAll()// TODO:revisar
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/interests").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}/interests/{interestId}").access("@accessHelper.isCurrentUser(#id)")

                .antMatchers(HttpMethod.GET, "/api/users/{id}/profilePicture").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/profilePicture").access("@accessHelper.isCurrentUser(#id)")

                .antMatchers(HttpMethod.PUT, "/api/users/{id}/password").access("@accessHelper.isCurrentUser(#id)")
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/blocked").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/cities", "/api/cities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/cities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/cities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/cities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/careers", "/api/careers/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/careers").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/careers/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/careers/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/universities", "/api/universities/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/universities").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/universities/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/universities/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/interests", "/api/interests/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/interests").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, "/api/interests/*").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/api/interests/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/journeys", "/api/journeys/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/journeys").access("isAuthenticated()")
                .antMatchers(HttpMethod.PUT, "/api/journeys/{id}").access("@accessHelper.isUserJourneyOwner(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/{id}").access("@accessHelper.isUserJourneyOwner(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/journeys/{journeyId}/tips").access("@accessHelper.isUserJourneyOwner(#journeyId)")
                .antMatchers(HttpMethod.PUT, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId)")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/{journeyId}/tips/{tipId}").access("@accessHelper.isUserTipOwner(#journeyId, #tipId) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/journeys/{id}/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/journeys/*/responses/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/events").access("isAuthenticated()")
                .antMatchers(HttpMethod.PUT, "/api/events/{id}").access("@accessHelper.isUserEventOwner(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/events/{id}").access("@accessHelper.isUserEventOwner(#id) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/events/*/responses").permitAll()
                .antMatchers(HttpMethod.POST, "/api/events/*/responses").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/*/responses/*").access("hasRole('ADMIN')")

                .antMatchers(HttpMethod.POST, "/api/events/*/attendances").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/events/*/attendances").access("isAuthenticated()")

                .antMatchers(HttpMethod.GET, "/api/events/*/ratings").permitAll()
                .antMatchers(HttpMethod.POST, "/api/events/{eventId}/ratings").access("@accessHelper.isUserEventAttendee(#eventId)")
                .antMatchers(HttpMethod.PUT, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId)")
                .antMatchers(HttpMethod.DELETE, "/api/events/{eventId}/ratings/{ratingId}").access("@accessHelper.isUserRatingOwner(#eventId, #ratingId) or hasRole('ADMIN')")

                .antMatchers(HttpMethod.GET, "/api/reports", "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")
                .antMatchers(HttpMethod.POST, "/api/reports").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")
                .antMatchers(HttpMethod.PUT, "/api/reports/{id}").access("hasRole('ADMIN') and isAuthenticated()")

                .antMatchers(HttpMethod.GET, "/api/images/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/images").access("isAuthenticated()")
                .antMatchers(HttpMethod.DELETE, "/api/images/*").access("hasRole('ADMIN')")


                .antMatchers("/universities", "/careers", "/interests", "/cities").permitAll()
                .antMatchers("/events/create", "/journeys/create", "/interests/edit").access("isAuthenticated()")
                .antMatchers(HttpMethod.GET,"/events", "/", "/events/{id}", "/journeys", "/journeys/{id}", "/images/{id}", "/blocked").permitAll()
                .antMatchers(HttpMethod.POST, "/users/{id}/block", "/users/{id}/unblock","/reports/{id}/status").access("hasRole('ADMIN')")
                .antMatchers("/dashboard/**","/interests/**", "/careers/**", "/universities/**","/cities/**", "/users/**", "/reports/{id}").access("hasRole('ADMIN')")
                .antMatchers("/journeys/tips/{id}/**").access("@accessHelper.isUserTipOwner(#id)")
                .antMatchers("/journeys/{id}/update", "/journeys/{id}/tips/create").access("@accessHelper.isUserJourneyOwner(#id)")
                .antMatchers("/events/{id}/update").access("@accessHelper.isUserEventOwner(#id)")
                .antMatchers("/journeys/{id}/delete").access("(@accessHelper.isUserJourneyOwner(#id) or hasRole('ADMIN'))")
                .antMatchers("/events/{id}/delete").access("(@accessHelper.isUserEventOwner(#id) or hasRole('ADMIN'))")
                .antMatchers("/journeys/reply/{id}/delete").access("hasRole('ADMIN') ")
                .antMatchers("/events/reply/{id}/delete").access("hasRole('ADMIN')" )
                .antMatchers(HttpMethod.POST, "/journeys/*", "/events/*/attend").access("isAuthenticated()")
                .antMatchers("/**").access("isAuthenticated()")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    // response.addHeader("WWW-Authenticate", "Basic realm=\"GoTogether\"");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                })

                // Disable client-side cache handling
                .and().headers().cacheControl().disable()

                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authAnywhereFilter, UsernamePasswordAuthenticationFilter.class)

                // Enable CORS and disable csrf rules
                .cors().and().csrf().disable();
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
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("X-GoTogether-AuthToken", "X-GoTogether-RefreshToken", "WWW-Authenticate", "Link"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(TimeUnit.HOURS.toSeconds(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
