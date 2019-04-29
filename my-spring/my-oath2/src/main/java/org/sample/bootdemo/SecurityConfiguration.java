package org.sample.bootdemo ;

import java.util.ArrayList ;
import java.util.Collection ;
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
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider ;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration ;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.context.annotation.Import ;
import org.springframework.core.convert.converter.Converter ;
import org.springframework.http.ResponseEntity ;
import org.springframework.security.authentication.AbstractAuthenticationToken ;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder ;
import org.springframework.security.config.annotation.web.builders.HttpSecurity ;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter ;
import org.springframework.security.core.Authentication ;
import org.springframework.security.core.GrantedAuthority ;
import org.springframework.security.core.authority.SimpleGrantedAuthority ;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder ;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken ;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService ;
import org.springframework.security.oauth2.core.oidc.OidcIdToken ;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo ;
import org.springframework.security.oauth2.core.oidc.user.OidcUser ;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority ;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority ;
import org.springframework.security.oauth2.jwt.Jwt ;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter ;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler ;
import org.springframework.web.client.RestTemplate ;
import org.springframework.web.util.UriComponentsBuilder ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;

@Configuration

@ConfigurationProperties ( prefix = "my-examples.security" )
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
@Import ( {
		SecurityAutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class,
		OAuth2ResourceServerAutoConfiguration.class } )
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String	ROLE					= "ROLE_" ;

	Logger						logger					= LoggerFactory.getLogger( getClass() ) ;

	// https://www.keycloak.org/docs/latest/securing_apps/index.html

	public static final String	CSAP_VIEW				= "ViewRole" ;
	public static final String	AUTHENTICATED			= "AUTHENTICATED" ;

	public static final String	ADMIN					= "admin" ;

	public static final String	USER					= "user" ;

	boolean						enabled					= false ;
	String						oathUserTokenName		= "not-specified" ;
	String						oathServiceClaimName	= "not-specified" ;
	String						oathClientServiceName	= "not-specified" ;

	@Inject
	ObjectMapper				jsonMapper ;

	@Inject
	OAuth2ClientProperties		oathProps ;

	@PostConstruct
	public void showConfiguration () {

		String	claimInfo	= Helpers.padLine( "user authorities token" ) + getOathUserTokenName()
				+ Helpers.padLine( "oath service claim" ) + getOathServiceClaimName()
				+ Helpers.padLine( "oath service client name" ) + getOathClientServiceName() ;

		String	regInfo		= oathProps.getRegistration().keySet().stream()
			.map( key -> {
									Registration registration	= oathProps.getRegistration().get( key ) ;
									StringBuilder p				= new StringBuilder( Helpers.padLine( "Registration" ) + key ) ;
									p.append( Helpers.padLine( "Client ID" ) + registration.getClientId() ) ;
									p.append( Helpers.padLine( "Client Name" ) + registration.getClientName() ) ;
									p.append( Helpers.padLine( "Grant Type" ) + registration.getAuthorizationGrantType() ) ;
									if ( registration.getScope() != null ) {
										p.append( Helpers.padLine( "Scopes" ) + registration.getScope().toString() ) ;
									}
									return p.toString() ;
								} )
			.collect( Collectors.joining( "\n" ) ) ;

		String	propInfo	= oathProps.getProvider().keySet().stream()
			.map( key -> {
									Provider	provider	= oathProps.getProvider().get( key ) ;
									StringBuilder p			= new StringBuilder( Helpers.padLine( "Provider: " ) + key ) ;
									p.append( Helpers.padLine( "Auth uri" ) + provider.getAuthorizationUri() ) ;
									p.append( Helpers.padLine( "Issuer uri" ) + provider.getIssuerUri() ) ;
									return p.toString() ;
								} )
			.collect( Collectors.joining( "\n" ) ) ;

		logger.info( Helpers.header( claimInfo + "\n" + regInfo + "\n" + propInfo ) ) ;
	}

	// @Override
	protected void configure (
								final AuthenticationManagerBuilder auth )
			throws Exception {

		logger.info( Helpers.header( "Adding in memory configuration..." ) ) ;

		auth.inMemoryAuthentication()
			.withUser( "peter" ).password( passwordEncoder().encode( "peter" ) ).roles( "peter" )
			.and()
			.withUser( USER ).password( passwordEncoder().encode( USER ) ).roles( "USER" )
			.and()
			.withUser( ADMIN ).password( passwordEncoder().encode( ADMIN ) ).roles( "ADMIN" ) ;
	}

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
				.antMatchers( WebClientController.URI_CLIENT_SELECTION_HI, WebClientController.URI_AUTO_SELECTION_HI ).permitAll()
				.antMatchers( MyRestApi.URI_OPEN_API_HI ).permitAll()
				.antMatchers( MyRestApi.URI_AUTHORIZED_HI ).hasRole( CSAP_VIEW )
				.antMatchers( MyRestApi.URI_AUTHENTICATED_HI ).hasAnyRole ( AUTHENTICATED )
				.antMatchers( "/**" ).permitAll()
				
				// alternatives
				// .antMatchers( MyRestApi.URI_AUTHENTICATED_HI ).hasAuthority( "SCOPE_csap-roles-scope" )
				// .antMatchers( MyRestApi.URI_AUTHENTICATED_HI ).authenticated()
				
				.and()
					.exceptionHandling()
					.accessDeniedPage( MyRestApi.URI_ACCESS_DENIED )
				
				.and()
					.formLogin()
						// default to list of clients: default to the PREFERRED
						// .loginPage( "/oauth2/authorization/keycloak-user-auth" )
					
				.and()
					.oauth2ResourceServer()
						.jwt()
							.jwtAuthenticationConverter(oathServiceAuthoritiesMapper())
							.and()
					
				// https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2login-advanced-map-authorities
				.and()
					.oauth2Login()
						//.loginPage( "my login" ) // defaults to  "/oauth2/authorization/<oauth client id>"
						.userInfoEndpoint()
							//.customUserType( customUserType, WebClientConfig.KEYCLOAK_CLIENT_ROLE )
                			.userAuthoritiesMapper( authorities  -> oathUserAuthoritiesMapper( authorities ))
                			.and()
                			
				.and()
        			.oauth2Client()
                			
                // https://info.michael-simons.eu/2017/12/28/use-keycloak-with-your-spring-boot-2-application/
				.and()
					.logout()
						.logoutSuccessUrl( "/" )
						.addLogoutHandler( openIdLogoutHandler )
					
				;


	}

	// @formatter:on
	private Converter<Jwt, ? extends AbstractAuthenticationToken> oathServiceAuthoritiesMapper () {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter() {
			protected Collection<GrantedAuthority> extractAuthorities ( Jwt jwt ) {

				Collection<GrantedAuthority> myAuthorities = null ;

				logger.info( "jwt: audience: {}, \n claims: {}", jwt.getAudience(), jwt.getClaims() ) ;

				try {
					Map<String, Object>	resourceClaims		= jwt.getClaimAsMap( "resource_access" ) ;

					JsonNode			csapServiceClaim	= jsonMapper.readTree( resourceClaims.get( "csap-service" ).toString() ) ;
					logger.info( "csapServiceClaim: {}", Helpers.jsonPrint( csapServiceClaim ) ) ;

					myAuthorities = Helpers.jsonStream( csapServiceClaim.path( "roles" ) )
						.map( JsonNode::asText )
						.map( csapServiceRole -> {
							return new SimpleGrantedAuthority( ROLE + csapServiceRole ) ;
						} )
						.collect( Collectors.toList() ) ;

					SimpleGrantedAuthority csapAuthenticatedAuthority = new SimpleGrantedAuthority( ROLE + AUTHENTICATED ) ;
					myAuthorities.add( csapAuthenticatedAuthority ) ;

				} catch ( Exception e ) {
					logger.warn( "Failed to to find service claim: {}", jwt.getClaims().toString() ) ;
				}

				// String types = resourceClaims.values().stream()
				// .map( Object::getClass )
				// .map( Class::getName )
				// .collect( Collectors.joining("\n\t") );
				// logger.info( "types: {}", types );

				return myAuthorities ;
			}
		} ;

		return jwtAuthenticationConverter ;
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

			if ( authentication != null && (authentication instanceof OAuth2AuthenticationToken) ) {
				logger.info( "Authentication type: {}", authentication.getClass().getName() ) ;
				propagateLogoutToOauth2Provider( (OidcUser) authentication.getPrincipal() ) ;
			} else {
				logger.warn( "Ignoring logout attempt - authentication principle is null" ) ;
			}
		}

		private void propagateLogoutToOauth2Provider (
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

	public String getOathUserTokenName () {
		return oathUserTokenName ;
	}

	public void setOathUserTokenName (
										String claimName ) {
		this.oathUserTokenName = claimName ;
	}

	/**
	 * 
	 * default role mapping in spring security is USER_ROLE and is hardcoded
	 * 
	 * - couple of options to set roles, but note it ALWAYS involves custom mappings
	 * 
	 * 
	 * @Link https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2login-advanced-map-authorities-grantedauthoritiesmapper
	 * 
	 * 
	 * @see DefaultOAuth2UserService#loadUser(org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest)
	 * 
	 * @param authorities
	 * @return
	 */
	private Collection<? extends GrantedAuthority> oathUserAuthoritiesMapper ( Collection<? extends GrantedAuthority> authorities ) {
		Set<GrantedAuthority> mappedAuthorities = new HashSet<>() ;

		authorities.forEach( authority -> {

			logger.info( "authority: {}", authority ) ;

			// SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority( authority.getAuthority() ) ;
			// mappedAuthorities.add( grantedAuthority ) ;

			if ( OidcUserAuthority.class.isInstance( authority ) ) {
				OidcUserAuthority	oidcUserAuthority	= (OidcUserAuthority) authority ;

				OidcIdToken			idToken				= oidcUserAuthority.getIdToken() ;
				OidcUserInfo		userInfo			= oidcUserAuthority.getUserInfo() ;

				logger.info( "claims: {}", userInfo.getClaims() ) ;
				// Map the claims found in idToken and/or userInfo
				// to one or more GrantedAuthority's and add it to mappedAuthorities
				Optional<String> csapClaims = userInfo.getClaims().keySet().stream()
					.filter( claimKey -> claimKey.equals( getOathUserTokenName() ) )
					.findFirst() ;

				if ( csapClaims.isPresent() ) {
					logger.debug( "csapClaims: {}", csapClaims.get() ) ;
					try {
						logger.debug( "type: {} ", userInfo.getClaims().get( getOathUserTokenName() ).getClass().getName() ) ;
						ArrayList<String> claimRoles = (ArrayList<String>) userInfo.getClaims().get( getOathUserTokenName() ) ;
						claimRoles.stream()
							.forEach( role -> {
								logger.debug( "role: {}", role ) ;
								mappedAuthorities.add( new SimpleGrantedAuthority( ROLE + role ) ) ;
							} ) ;

					} catch ( Exception e ) {
						logger.warn( "Failed to read roles: {}", Helpers.buildSampleStack( e ) ) ;
					}
				} else {
					logger.warn( "No csap claims found" ) ;
				}

				SimpleGrantedAuthority csapAuthenticatedAuthority = new SimpleGrantedAuthority( ROLE + AUTHENTICATED ) ;
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
	}

	public String getOathServiceClaimName () {
		return oathServiceClaimName ;
	}

	public void setOathServiceClaimName ( String oathServiceClaimName ) {
		this.oathServiceClaimName = oathServiceClaimName ;
	}

	public String getOathClientServiceName () {
		return oathClientServiceName ;
	}

	public void setOathClientServiceName ( String oathClientServiceName ) {
		this.oathClientServiceName = oathClientServiceName ;
	}

}
