package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.auth.CustomAuthenticationFailureHandler;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
@PropertySource("classpath:application.properties")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthConfig.class);

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private AccessHelper accessHelper;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

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
        LOGGER.info("Configuring security (has key from properties file = {})", authKey.length() > 0);
        http.userDetailsService(userDetailsService)
                .sessionManagement()
                .invalidSessionUrl("/")
                .and().authorizeRequests()
                .antMatchers("/register", "/login", "/blocked").permitAll() // Make sure /blocked is accessible
                .antMatchers(HttpMethod.POST, "/events/{id}/delete", "/journeys/{id}/delete", "journey-replies/{id}/delete", "event-replies/{id}/delete",
                        "profile/{id}/block", "profile/{id}/unblock").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/dashboard/**","interests/**", "careers/**", "/universities/**","cities/**").hasRole("ADMIN")
                .antMatchers("/journeys/{id}/update").access("@accessHelper.isUserJourneyOwner(#id)")
                .antMatchers("/events/{id}/update").access("@accessHelper.isUserEventOwner(#id)")
                .antMatchers("/events/create", "/journeys/create").authenticated()
                .antMatchers("/events/*/reply", "/journeys/*/reply", "/events/*/attend").authenticated()
                .antMatchers(HttpMethod.GET,"/events", "/", "/events/{id}", "/journeys", "/journeys/{id}", "/images/{id}","/universities","/universities/{id}").permitAll()
                .antMatchers("/**").authenticated()
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
                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/resources/css/**", "/resources/js/**", "/resources/images/**",
                        "/resources/favicon.ico", "/errors/*", "/resources/icons/**");
    }
}