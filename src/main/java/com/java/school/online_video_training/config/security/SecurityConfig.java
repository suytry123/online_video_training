package com.java.school.online_video_training.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.java.school.online_video_training.config.jwt.FilterChainExceptionHandler;
import com.java.school.online_video_training.config.jwt.JwtLoginFilter;
import com.java.school.online_video_training.config.jwt.TokenVerifyFIlter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
		  prePostEnabled = true, 
		  securedEnabled = true, 
		  jsr250Enabled = true)
public class SecurityConfig {
	
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	private final FilterChainExceptionHandler filterChainExceptionHandler;
	private AuthenticationConfiguration authenticationConfiguration;
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.addFilter(new JwtLoginFilter(authenticationManager(authenticationConfiguration))) // Add JWT login filter
			.addFilterBefore(filterChainExceptionHandler, JwtLoginFilter.class) // Add exception handler filter before JwtLoginFilter
			.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class) // Add logging filter before UsernamePasswordAuthenticationFilter
			.addFilterAfter(new TokenVerifyFIlter(), JwtLoginFilter.class)
//			.addFilter(new JwtLoginFilter(authenticationManager(authenticationConfiguration)))
//			.addFilterBefore(filterChainExceptionHandler, JwtLoginFilter.class)
//			.addFilterAfter(new TokenVerifyFIlter(), JwtLoginFilter.class)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeHttpRequests()
			.antMatchers("/","index.html","css/**","js/**", "/api/auth/**", 
					"/register", "/login", "/registerForm", "verify-email").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**", "/swagger-ui/**").permitAll()
			//.antMatchers(HttpMethod.PUT, "/categories/**").hasAuthority(PermissionEnum.CATEGORY_WRITE.getDescription())
			.anyRequest()
			.authenticated();
		return http.build();
	}
	
	@Bean
	AuthenticationManager authenticationManager(
	        AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    return authenticationConfiguration.getAuthenticationManager();
	}
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(getAuthenticationProvider());
	}
	
	@Bean
	public AuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return authenticationProvider;
	}
}
