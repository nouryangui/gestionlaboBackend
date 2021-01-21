package com.eureka.zuul.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import com.eureka.common.security.JwtConfig;

@EnableWebSecurity 	// Enable security config. This annotation denotes config for spring security.
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtConfig jwtConfig;
 
	@Override
  	protected void configure(HttpSecurity http) throws Exception {
    	   http
		.csrf().disable()
		    // make sure we use stateless session; session won't be used to store user's state.
	 	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
		    // handle an authorized attempts 
		    .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)) 	
		   // Add a filter to validate the tokens with every request
		.and()
		   // Add a filter to validate the tokens with every request
		   .addFilterBefore(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
		// authorization requests config
		.authorizeRequests()
		   // allow all who are accessing "auth" service
		.antMatchers(HttpMethod.POST, "/auth-service/auth/**").permitAll()
		.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

		.antMatchers(HttpMethod.POST, "/auth-service/register/**").permitAll()	
		.antMatchers(HttpMethod.GET, "/member-service/**").permitAll()
		.antMatchers(HttpMethod.GET, "/publication-service/**").permitAll()
		.antMatchers(HttpMethod.GET, "/event-service/**").permitAll()
		.antMatchers(HttpMethod.GET, "/tool-service/**").permitAll()

		.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

		   // must be an admin if trying to access admin area (authentication is also required here)
		   .antMatchers(HttpMethod.POST,"/member-service/**").hasAuthority("admin")
		   // Any other request must be authenticated
		   .anyRequest().authenticated(); 
	}
	
	@Bean
  	public JwtConfig jwtConfig() {
    	   return new JwtConfig();
  	}
}