package com.sgr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean //(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/register", "/index", "/webhook", "/login")
                .permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/admin/old/**").hasAuthority("ADMIN")
                /*.antMatchers("/dashboard/home*//**").hasAnyRole("USER", "ADMIN")
         .antMatchers("/dashboard/users*//**").hasRole("ADMIN")
         .antMatchers("/rest/users*//**").hasRole("ADMIN")*/
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                /*.successForwardUrl("/home")*/
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/forbidden");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/vendors/**", "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/scss/**", "/img/**", "/font/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
