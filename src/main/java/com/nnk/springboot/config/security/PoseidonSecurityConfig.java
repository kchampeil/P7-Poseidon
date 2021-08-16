package com.nnk.springboot.config.security;

import com.nnk.springboot.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class PoseidonSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PoseidonSecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                /* pages authorized without connection */
                .antMatchers(
                        "/",
                        "/login",
                        "/logout",
                        "/403",
                        "/underConstruction",
                        "/css/*")
                .permitAll()
                /* pages authorized for any role (ADMIN or USER) */
                .antMatchers(
                        "/bidList/**",
                        "/curvePoint/**",
                        "/rating/**",
                        "/ruleName/**",
                        "/trade/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                /* pages authorized for ADMIN role only */
                .antMatchers("/user/**")
                .hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()

                /* use default login form and redirect user to bidList page (/bidList/list) once logged in */
                .and().formLogin()
                .defaultSuccessUrl("/bidList/list", true)
                .failureUrl("/login?error=true")

                /* use default logout page and redirect user to the home page (/) once logged out */
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/app-logout", "POST"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")

                /* redirect to the error page if user not authorized to access a page */
                .and().exceptionHandling()
                .accessDeniedPage("/app/error");
    }


    /**
     * prepare the AuthenticationProvider by setting UserDetailsService and PasswordEncoder
     *
     * @return custom DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
