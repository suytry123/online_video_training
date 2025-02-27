package com.java.school.online_video_training.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.java.school.online_video_training.config.security.PermissionEnum.*;

@Configuration
@EnableGlobalMethodSecurity(
		  prePostEnabled = true, 
		  securedEnabled = true, 
		  jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeHttpRequests()
			.antMatchers("/","index.html","css/**","js/**").permitAll()
			.antMatchers("/courses").hasRole("AUTHOR")
			//.antMatchers("/categories").hasRole("AUTHOR")
			//.antMatchers(HttpMethod.POST, "/categories").hasAuthority(CATEGORY_WRITE.getDescription())
			//.antMatchers(HttpMethod.GET, "/categories").hasAuthority(CATEGORY_READ.getDescription())
			.anyRequest()
			.authenticated()
			.and()
			.httpBasic();
	}
	
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		//User user1 = new User("dara", passwordEncoder.encode("Dara123"), Collections.emptyList());
		UserDetails user1 = User.builder()
				.username("dara")
				.password(passwordEncoder.encode("dara123"))
				//.roles()
				.authorities(RoleEnum.AUTHOR.getAuthorities())
				.build();
		
		UserDetails user2 = User.builder()
				.username("thida")
				.password(passwordEncoder.encode("thida123"))
				.authorities(RoleEnum.ADMIN.getAuthorities())
				.build();
		
		//UserDetails
		UserDetailsService userDetailsService = new InMemoryUserDetailsManager(user1 , user2);
		return userDetailsService;
	}
}
