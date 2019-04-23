package org.sample.bootdemo ;

import java.util.ArrayList ;
import java.util.HashSet ;
import java.util.Map ;
import java.util.Optional ;
import java.util.Set ;
import java.util.stream.Collectors ;

import javax.annotation.PostConstruct ;
import javax.inject.Inject ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty ;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties ;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.context.annotation.Import ;
import org.springframework.http.ResponseEntity ;
import org.springframework.security.config.annotation.web.builders.HttpSecurity ;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter ;
import org.springframework.security.core.Authentication ;
import org.springframework.security.core.GrantedAuthority ;
import org.springframework.security.core.authority.SimpleGrantedAuthority ;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper ;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder ;
import org.springframework.security.oauth2.core.oidc.OidcIdToken ;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo ;
import org.springframework.security.oauth2.core.oidc.user.OidcUser ;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority ;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority ;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler ;
import org.springframework.web.client.RestTemplate ;
import org.springframework.web.util.UriComponentsBuilder ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@Configuration

@ConfigurationProperties ( prefix = "my-examples.security" )
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
@Import ( {
		SecurityAutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class } )
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	Logger						logger			= LoggerFactory.getLogger( getClass() ) ;

	// https://www.keycloak.org/docs/latest/securing_apps/index.html

	public static final String	CSAP_VIEW		= "ViewRole" ;
	public static final String	AUTHENTICATED	= "AUTHENTICATED" ;

	public static final String	ADMIN			= "admin" ;

	public static final String	USER			= "user" ;

	boolean						enabled			= false ;
	String						tokenClaimName		= "" ;

	@Inject
	ObjectMapper				jsonMapper ;

	@Inject
	OAuth2ClientProperties		oathProps ;

	@PostConstruct
	public void showConfiguration () {

		String	claimInfo	= Helpers.padLine( "claim name" ) + getTokenClaimName() ;

		String	regInfo		= oathProps.getRegistration().values().stream()
			.map( registration -> {
									StringBuilder p = new StringBuilder( Helpers.padLine( "Registration" ) ) ;
									p.append( Helpers.padLine( "Client ID" ) + registration.getClientId() ) ;
									p.append( Helpers.padLine( "Client Name" ) + registration.getClientName() ) ;
									p.append( Helpers.padLine( "Grant Type" ) + registration.getAuthorizationGrantType() ) ;
									p.append( Helpers.padLine( "Scopes" ) + registration.getScope().toString() ) ;
									return p.toString() ;
								} )
			.collect( Collectors.joining( "\n" ) ) ;

		String	propInfo	= oathProps.getProvider().values().stream()
			.map( provider -> {
									StringBuilder p = new StringBuilder( Helpers.padLine( "Provider" ) ) ;
									p.append( Helpers.padLine( "Auth uri" ) + provider.getAuthorizationUri() ) ;
									return p.toString() ;
								} )
			.collect( Collectors.joining( "\n" ) ) ;

		logger.info( Helpers.header( claimInfo + "\n" + regInfo + "\n" + propInfo ) ) ;
	}

	private GrantedAuthoritiesMapper userOpenIdAuthoritiesMapper () {
		return (
					authorities ) -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>() ;

			authorities.forEach( authority -> {

				logger.info( "authority: {}", authority ) ;
				if ( OidcUserAuthority.class.isInstance( authority ) ) {
					OidcUserAuthority	oidcUserAuthority	= (OidcUserAuthority) authority ;

					OidcIdToken			idToken				= oidcUserAuthority.getIdToken() ;
					OidcUserInfo		userInfo			= oidcUserAuthority.getUserInfo() ;

					logger.info( "claims: {}", userInfo.getClaims() ) ;
					// Map the claims found in idToken and/or userInfo
					// to one or more GrantedAuthority's and add it to mappedAuthorities
					Optional<String> csapClaims = userInfo.getClaims().keySet().stream()
						.filter( claimKey -> claimKey.equals( getTokenClaimName() ) )
						.findFirst() ;

					if ( csapClaims.isPresent() ) {
						logger.debug( "csapClaims: {}", csapClaims.get() ) ;
						try {
							logger.debug( "type: {} ", userInfo.getClaims().get( getTokenClaimName() ).getClass().getName() ) ;
							ArrayList<String> claimRoles = (ArrayList<String>) userInfo.getClaims().get( getTokenClaimName() ) ;
							claimRoles.stream()
								.forEach( role -> {
									logger.debug( "role: {}", role ) ;
									mappedAuthorities.add( new SimpleGrantedAuthority( "ROLE_" + role ) ) ;
								} ) ;

						} catch ( Exception e ) {
							logger.warn( "Failed to read roles: {}", Helpers.buildSampleStack( e ) ) ;
						}
					} else {
						logger.warn( "No csap claims found" ) ;
					}

					SimpleGrantedAuthority csapAuthenticatedAuthority = new SimpleGrantedAuthority( "ROLE_" + AUTHENTICATED ) ;
					mappedAuthorities.add( csapAuthenticatedAuthority ) ;

				} else if ( OAuth2UserAuthority.class.isInstance( authority ) ) {
					OAuth2UserAuthority	oauth2UserAuthority	= (OAuth2UserAuthority) authority ;

					Map<String, Object>	userAttributes		= oauth2UserAuthority.getAttributes() ;
					logger.info( "userAttributes: {}", userAttributes ) ;

					// Map the attributes found in userAttributes
					// to one or more GrantedAuthority's and add it to mappedAuthorities

				}
			} ) ;

			return mappedAuthorities ;
		} ;
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

	@Inject
	OpenIdLogoutHandler openIdLogoutHandler ;

	// @formatter:off
	@Override
	protected void configure (
								final HttpSecurity httpSecurity )
			throws Exception {

		logger.info( Helpers.header("Configuring httpSecurity..." )) ;

		httpSecurity
		
			.csrf().disable()

			.authorizeRequests()
				.antMatchers( MyRestApi.URI_ANON_API_HI ).anonymous()
				.antMatchers( MyRestApi.URI_OPEN_API_HI ).permitAll()
				.antMatchers( MyRestApi.URI_AUTHORIZED_HI ).hasRole( CSAP_VIEW )
				.antMatchers( MyRestApi.URI_AUTHENTICATED_HI ).hasRole( AUTHENTICATED )
				.antMatchers( "/**" ).permitAll()
				
				.and()
					.exceptionHandling()
					.accessDeniedPage( MyRestApi.URI_ACCESS_DENIED )
				
				// https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2login-advanced-map-authorities
				.and()
					.oauth2Login()
						.userInfoEndpoint()
                			.userAuthoritiesMapper(this.userOpenIdAuthoritiesMapper())
                			.and()
				
                // https://info.michael-simons.eu/2017/12/28/use-keycloak-with-your-spring-boot-2-application/
				.and()
					.logout()
						.logoutSuccessUrl( "/" )
						.addLogoutHandler( openIdLogoutHandler )
					
				;


	}
	// @formatter:on

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

	@Bean
	public OpenIdLogoutHandler getOpenIdLogoutHandler () {
		return new OpenIdLogoutHandler() ;
	}

	public class OpenIdLogoutHandler extends SecurityContextLogoutHandler {
		Logger					logger			= LoggerFactory.getLogger( getClass() ) ;

		private RestTemplate	restTemplate	= new RestTemplate() ;

		@Override
		public void logout (
								HttpServletRequest request,
								HttpServletResponse response,
								Authentication authentication ) {
			super.logout( request, response, authentication ) ;

			if ( authentication != null ) {
				propagateLogoutToKeycloak( (OidcUser) authentication.getPrincipal() ) ;
			} else {
				logger.warn( "Ignoring logout attempt - authentication principle is null" ) ;
			}
		}

		private void propagateLogoutToKeycloak (
													OidcUser user ) {

			String					endSessionEndpoint	= user.getIssuer() + "/protocol/openid-connect/logout" ;

			UriComponentsBuilder	builder				= UriComponentsBuilder									//
				.fromUriString( endSessionEndpoint )															//
				.queryParam( "id_token_hint", user.getIdToken().getTokenValue() ) ;

			logger.info( "Logging out: {}", builder.toUriString() ) ;

			ResponseEntity<String> logoutResponse = restTemplate.getForEntity( builder.toUriString(), String.class ) ;
			if ( logoutResponse.getStatusCode().is2xxSuccessful() ) {
				logger.info( "Successfulley logged out in Keycloak" ) ;
			} else {
				logger.info( "Could not propagate logout to Keycloak" ) ;
			}
		}
	}

	public String getTokenClaimName () {
		return tokenClaimName ;
	}

	public void setTokenClaimName (
								String claimName ) {
		this.tokenClaimName = claimName ;
	}
}
