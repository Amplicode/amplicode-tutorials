package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.requestMatchers(HttpMethod.GET, "/vets")
			.permitAll()
			.anyRequest()
			.authenticated());
		http.headers(Customizer.withDefaults());
		http.sessionManagement(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.anonymous(Customizer.withDefaults());
		http.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}

	@Bean
	public UserDetailsService inMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
		// The builder will ensure the passwords are encoded before saving in memory
		User.UserBuilder users = User.builder()
			.passwordEncoder(passwordEncoder::encode);
		InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
		userDetailsManager.createUser(users.username("admin")
			.password("admin")
			.roles("ADMIN")
			.build());
		userDetailsManager.createUser(users.username("user")
			.password("user")
			.roles("USER")
			.build());
		return userDetailsManager;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
