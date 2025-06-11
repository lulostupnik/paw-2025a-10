package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.auth.AuthEntryPointHandler;
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
    @Autowired
    private PawUserDetailsService  userDetailsService;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

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
        LOGGER.info("Configuring security (has key from properties file = {})", authKey.length() > 0);
        http.userDetailsService(userDetailsService)
                .sessionManagement()
                .invalidSessionUrl("/")
                .and().authorizeRequests()
                .antMatchers("/register", "/login", "/reset-password", "/forgot_pass", "/validate", "/not-verified").anonymous()
                .antMatchers("/universities", "/careers", "/interests", "/cities").permitAll()
                .antMatchers("/events/create", "/journeys/create", "interests/edit").access("isAuthenticated() and !@accessHelper.isUserBlocked()")
                .antMatchers(HttpMethod.GET,"/events", "/", "/events/{id}", "/journeys", "/journeys/{id}", "/images/{id}", "/blocked").permitAll()
                .antMatchers(HttpMethod.POST, "/users/{id}/block", "/users/{id}/unblock","/reports/{id}/status").access("hasRole('ADMIN') and !@accessHelper.isUserBlocked()")
                .antMatchers("/dashboard/**","/interests/**", "/careers/**", "/universities/**","/cities/**", "/users/**", "/reports/{id}").access("hasRole('ADMIN') and !@accessHelper.isUserBlocked()")
                .antMatchers("/journeys/tips/{id}/**").access("@accessHelper.isUserTipOwner(#id) and !@accessHelper.isUserBlocked()")
                .antMatchers("/journeys/{id}/update", "/journeys/{id}/tips/create").access("@accessHelper.isUserJourneyOwner(#id) and !@accessHelper.isUserBlocked()")
                .antMatchers("/events/{id}/update").access("@accessHelper.isUserEventOwner(#id) and !@accessHelper.isUserBlocked()")
                .antMatchers("/journeys/{id}/delete").access("(@accessHelper.isUserJourneyOwner(#id) or hasRole('ADMIN')) and !@accessHelper.isUserBlocked()")
                .antMatchers("/events/{id}/delete").access("(@accessHelper.isUserEventOwner(#id) or hasRole('ADMIN')) and !@accessHelper.isUserBlocked()")
                .antMatchers("/journeys/reply/{id}/delete").access("hasRole('ADMIN') and !@accessHelper.isUserBlocked() ")
                .antMatchers("/events/reply/{id}/delete").access("hasRole('ADMIN') and !@accessHelper.isUserBlocked()" )
                .antMatchers(HttpMethod.POST, "/journeys/*", "/events/*/attend").access("isAuthenticated() and !@accessHelper.isUserBlocked()")
                .antMatchers("/**").access("isAuthenticated() and !@accessHelper.isUserBlocked()")
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
}