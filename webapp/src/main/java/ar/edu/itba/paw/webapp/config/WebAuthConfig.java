package ar.edu.itba.paw.webapp.config;


import ar.edu.itba.paw.webapp.auth.*;
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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
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
        http.userDetailsService(userDetailsService)
                .sessionManagement()
                .invalidSessionUrl("/")
                .and().authorizeRequests()
                .antMatchers("/register", "/login", "/reset-password", "/forgot_pass", "/validate", "/not-verified").anonymous()
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
                .and().formLogin()
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .defaultSuccessUrl("/explore", false)
                .loginPage("/login")
                .failureHandler(failureHandler)
                .and().rememberMe()
                .rememberMeParameter("j_rememberme")
                .userDetailsService(userDetailsService)
                .key(authKey)
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .and().exceptionHandling()
                .accessDeniedPage("/errors/403")
                .authenticationEntryPoint(authEntryPointHandler())
                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/resources/css/**", "/resources/js/**", "/resources/images/**",
                        "/resources/favicon.ico", "/errors/*", "/resources/icons/**");
    }
    @Bean
    public JwtUtils jwtTokenUtil(@Value("classpath:jwtSecret.key") Resource jwtKeyRes) throws IOException {
        return new JwtUtils(jwtKeyRes);
    }
}