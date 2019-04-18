package org.sample.bootdemo ;

import org.keycloak.adapters.springboot.KeycloakAutoConfiguration ;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver ;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents ;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider ;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory ;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate ;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter ;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.ComponentScan ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.context.annotation.Import ;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder ;
import org.springframework.security.config.annotation.web.builders.HttpSecurity ;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper ;
import org.springframework.security.core.session.SessionRegistryImpl ;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder ;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy ;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy ;

@Configuration
@ConfigurationProperties ( prefix = "my-examples.security" )
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
@Import ( {
		SecurityAutoConfiguration.class,
		KeycloakAutoConfiguration.class } )
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
	
	// https://www.keycloak.org/docs/latest/securing_apps/index.html

	public static final String	CSAP_VIEW	= "csap-view" ;

	public static final String	ADMIN		= "admin" ;

	public static final String	USER		= "user" ;

	Logger						logger		= LoggerFactory.getLogger( getClass() ) ;

	boolean						enabled		= false ;
	boolean						basic		= false ;

	// @Component
	// public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
	// Logger logger = LoggerFactory.getLogger( getClass() ) ;
	//
	// @Override
	// public void onApplicationEvent (
	// InteractiveAuthenticationSuccessEvent event ) {
	// KeycloakPrincipal<KeycloakSecurityContext> userDetails = (KeycloakPrincipal) event.getAuthentication().getPrincipal() ;
	// logger.info( Helpers.testHeader( "User Logged in" ) ) ;
	// Helpers.printDetails( userDetails ) ;
	// }
	// }
	
    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

	@Autowired
	public void configureGlobal (
									AuthenticationManagerBuilder auth )
			throws Exception {

		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider() ;
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
			new SimpleAuthorityMapper() ) ;
		auth.authenticationProvider( keycloakAuthenticationProvider ) ;
	}

	@Bean
	public KeycloakSpringBootConfigResolver KeycloakConfigResolver () {
		return new KeycloakSpringBootConfigResolver() ;
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy () {
		return new RegisterSessionAuthenticationStrategy(
			new SessionRegistryImpl() ) ;
	}
	

	@Autowired
    public KeycloakClientRequestFactory keycloakClientRequestFactory;
	
    @Bean
    public KeycloakRestTemplate keycloakRestTemplate() {
    	
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

	// @Override
	// protected void configure (
	// final AuthenticationManagerBuilder auth )
	// throws Exception {
	//
	// logger.info( "Adding in memory configuration..." ) ;
	//
	// auth.inMemoryAuthentication()
	// .withUser( USER ).password( passwordEncoder().encode( USER ) ).roles( "USER" )
	// .and()
	// .withUser( ADMIN ).password( passwordEncoder().encode( ADMIN ) ).roles( "ADMIN" ) ;
	// }

	@Override
	protected void configure (
								final HttpSecurity httpSecurity )
			throws Exception {

		logger.info( "Configuring httpSecurity..." ) ;

		super.configure( httpSecurity ) ;

		// @formatter:off
		httpSecurity
			.csrf().disable()
			
//			.httpBasic().and()
			
			.authorizeRequests()
			.antMatchers( MyRestApi.URI_OPEN_API_HI ).anonymous()
				.antMatchers( MyRestApi.URI_SECURE_API_HI ).hasRole( CSAP_VIEW )
				.antMatchers( "/login*" ).permitAll()
				.anyRequest().authenticated()
				
				.and()
				
			.formLogin()
				;
//				

//			.formLogin()
//				.disable()
//				.loginPage( "/login.html" )
//				.loginProcessingUrl("/perform_login")
//				.defaultSuccessUrl("/homepage.html", true)
//				.failureUrl("/login.html?error=true")
				//.failureHandler(authenticationFailureHandler())
//				.and()
				

				
//			.logout()
////				.logoutUrl( "/perform_logout" )
////				.logoutSuccessHandler(logoutSuccessHandler())
//				.deleteCookies( "JSESSIONID" ) ;
		
		// 
		// @formatter:on

		if ( isBasic() ) {
			httpSecurity.httpBasic() ;
		}

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

	public boolean isBasic () {
		return basic ;
	}

	public void setBasic (
							boolean basic ) {
		this.basic = basic ;
	}
}