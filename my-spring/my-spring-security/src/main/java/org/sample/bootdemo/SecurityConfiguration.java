package org.sample.bootdemo ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.context.annotation.Import ;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder ;
import org.springframework.security.config.annotation.web.builders.HttpSecurity ;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter ;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder ;

@Configuration
@ConfigurationProperties ( prefix = "my-examples.security" )
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
@Import ( {
		SecurityAutoConfiguration.class } )
// @EnableWebSecurity: configured via yaml
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String ADMIN = "admin" ;

	public static final String USER = "user" ;

	Logger	logger	= LoggerFactory.getLogger( getClass() ) ;

	boolean	enabled	= false ;

	@Override
	protected void configure (
								final AuthenticationManagerBuilder auth )
			throws Exception {

		logger.info( "Adding in memory configuration..." ) ;

		auth.inMemoryAuthentication()
			.withUser( USER ).password( passwordEncoder().encode( USER ) ).roles( "USER" )
			.and()
			.withUser( ADMIN ).password( passwordEncoder().encode( ADMIN ) ).roles( "ADMIN" ) ;
	}

	@Override
	protected void configure (
								final HttpSecurity http )
			throws Exception {

		// @formatter:off
		http
			.csrf().disable()
			
			.httpBasic().and()
			
			.authorizeRequests()
				.antMatchers( "/admin/**" ).hasRole( "ADMIN" )
				.antMatchers( "/anonymous*" ).anonymous()
				.antMatchers( "/login*" ).permitAll()
				.anyRequest().authenticated()
				.and()

			.formLogin()
				.and()
//				.disable()
				
			//	.loginPage( "/login.html" )
			// .loginProcessingUrl("/perform_login")
			// .defaultSuccessUrl("/homepage.html", true)
			// //.failureUrl("/login.html?error=true")
			// .failureHandler(authenticationFailureHandler())
			//	.and()
				
			.logout()
				.logoutUrl( "/perform_logout" )
				.deleteCookies( "JSESSIONID" ) ;
		// .logoutSuccessHandler(logoutSuccessHandler());
		// @formatter:on

	}

	@Bean
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder() ;
	}

	public boolean isEnabled () {
		return enabled ;
	}

	public void setEnabled (
								boolean disabled ) {
		this.enabled = disabled ;
	}
}
