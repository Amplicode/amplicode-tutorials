package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .anyRequest()
                .authenticated());
        http.headers(Customizer.withDefaults());
        http.sessionManagement(Customizer.withDefaults());
        http.formLogin(Customizer.withDefaults());
        http.anonymous(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.userDetailsService(inMemoryUserDetailsService());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    public UserDetailsService inMemoryUserDetailsService() {
        User.UserBuilder users = User.builder();
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(users.username("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build());
        userDetailsManager.createUser(users.username("user")
                .password("{noop}user")
                .roles("USER")
                .build());
        return userDetailsManager;
    }
}
