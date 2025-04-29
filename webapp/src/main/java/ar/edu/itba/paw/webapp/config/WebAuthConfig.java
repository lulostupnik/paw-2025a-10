package ar.edu.itba.paw.webapp.config;

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
                .antMatchers(HttpMethod.POST, "/events/{id}/delete", "/journeys/{id}/delete", "journey-replies/{id}/delete", "event-replies/{id}/delete").hasRole("ADMIN")
                .antMatchers( "/register").anonymous()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/dashboard/**").hasRole("ADMIN")
//                    .antMatchers("/events/**", "/journeys/**").permitAll()
                    .antMatchers("/events/create", "/journeys/create").authenticated()
                    .antMatchers("/events/*/reply", "/journeys/*/reply", "/events/*/attend").authenticated()
                    .antMatchers(HttpMethod.GET,"/events", "/events/{id}", "/journeys", "/journeys/{id}", "/images/{id}").permitAll()
                    .antMatchers("/**").authenticated()
                .and().formLogin()
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .defaultSuccessUrl("/explore", false)
                    .loginPage("/login")
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
