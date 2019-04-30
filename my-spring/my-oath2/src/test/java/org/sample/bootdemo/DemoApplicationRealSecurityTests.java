package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;

import javax.inject.Inject ;

import org.junit.jupiter.api.Assumptions ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
//import org.keycloak.adapters.springboot.KeycloakSpringBootProperties ;
//import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate ;
//import org.keycloak.authorization.client.AuthzClient ;
//import org.keycloak.authorization.client.Configuration ;
//import org.keycloak.representations.AccessTokenResponse ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment ;
import org.springframework.boot.test.web.client.TestRestTemplate ;
import org.springframework.boot.web.server.LocalServerPort ;
import org.springframework.http.ResponseEntity ;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient ;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService ;
import org.springframework.security.oauth2.client.registration.ClientRegistration ;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository ;
import org.springframework.test.context.ActiveProfiles ;
import org.springframework.web.reactive.function.client.WebClient ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@SpringBootTest ( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles ( "test" )

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@DisplayName ( "Demo Application: full context with Real Security" )

class DemoApplicationRealSecurityTests {

	Logger							logger	= LoggerFactory.getLogger( getClass() ) ;

	@LocalServerPort
	private int						testPort ;

	@Inject
	ObjectMapper					jsonMapper ;

	@Inject
	MyRestApi						myRestApi ;

	@Inject
	WebClientController				webClientController ;

	@Inject
	WebClient						webClientService ;

	@Inject
	SecurityConfiguration			securityConfig ;

	@Inject
	TestRestTemplate				testRestTemplate ;

	@Inject
	OAuth2AuthorizedClientService	authorizedClientService ;

	@Autowired
	ClientRegistrationRepository	clientRegistrationRepository ;

	@BeforeAll
	void beforeAll ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

	}

	@Test
	void contextLoads () {}

	@Test
	@DisplayName ( "oauth api: service client using http context" )
	void verifyServiceApiUsingClientContext ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		String						simpleUrl				= "http://localhost:" + testPort + WebClientController.URI_AUTO_SELECTION_HI ;
		

		Helpers.setLogToDebug( WebClient.class.getPackageName() ) ;
		ResponseEntity<ObjectNode>	restTemplateResponse	= testRestTemplate
			.getForEntity(
				simpleUrl,
				ObjectNode.class ) ;
		Helpers.setLogToInfo( WebClient.class.getPackageName()  ) ;

		JsonNode					webClientResponse		= restTemplateResponse.getBody() ;
		logger.debug( "webClientResponse: {}", Helpers.jsonPrint( webClientResponse ) ) ;
		assertThat( webClientResponse.at( "/response/message" ).asText() ).isEqualTo( "authenticated-get" ) ;

	}

	@Test
	@DisplayName ( "oauth api: service client direct" )
	void verifyServiceApiUsingAnonymousContext ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		String simpleUrl = "http://localhost:" + testPort + myRestApi.URI_AUTHENTICATED_HI ;

		// String webClientResponse = webClientController.getContentUsingWebClient( myRestApi.URI_AUTHENTICATED_HI );

		Helpers.setLogToDebug( WebClient.class.getPackageName() ) ;
		ObjectNode webClientBody = webClientService
			.get()
			.uri( simpleUrl )
			// .attributes( oauth2AuthorizedClient(securityConfig.getOathClientServiceName()))
			.retrieve()
			.bodyToMono( ObjectNode.class ).block() ;
		Helpers.setLogToInfo( WebClient.class.getPackageName()  ) ;

		logger.debug( "webClientResponse: {}", Helpers.jsonPrint( webClientBody ) ) ;
		assertThat( webClientBody.at( "/message" ).asText() ).isEqualTo( "authenticated-get" ) ;

	}

	@Test
	@DisplayName ( "oauth api: user authentication and authorization" )
	void verifyUserLoginAndAccess ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		Assumptions.assumeTrue( false, "User flows not tested" ) ;

	}

	@Test
	@DisplayName ( "oauth api: clientRegistration and provider details" )
	void verifyOathClientRegistration () {

		logger.info( Helpers.testHeader() ) ;

		ClientRegistration clientReg = clientRegistrationRepository.findByRegistrationId( securityConfig.getOathClientServiceName() ) ;
		assertThat( clientReg ).isNotNull() ;

		logger.info( "auth uri: {} ", clientReg.getProviderDetails().getAuthorizationUri() ) ;
		assertThat( clientReg.getProviderDetails().getAuthorizationUri() )
			.endsWith( "/auth/realms/csap-default/protocol/openid-connect/auth" ) ;

	}

	// @Test
	// @DisplayName ( "secure endpoint access via authz token" )
	// void verifySecureApiUsingAuthzClient () {
	//
	// String simpleUrl = "http://localhost:" + testPort + MyRestApi.URI_AUTHORIZED_HI ;
	//
	// logger.info( Helpers.testHeader( simpleUrl ) ) ;
	//
	// AccessTokenResponse accessResponse = getAccessToken() ;
	//
	// TestRestTemplate testRestTemplate = new TestRestTemplate() ;
	// testRestTemplate.getRestTemplate().setInterceptors(
	// Collections.singletonList( (
	// request,
	// body,
	// execution ) -> {
	// request.getHeaders()
	// .add( "Authorization", "Bearer " + accessResponse.getToken() ) ;
	// return execution.execute( request, body ) ;
	// } ) ) ;
	//
	// ResponseEntity<ObjectNode> restTemplateResponse = testRestTemplate
	// .getForEntity(
	// simpleUrl,
	// ObjectNode.class ) ;
	//
	// logger.info( "millis using spring rest template with bearer token: {}", restTemplateResponse.getBody().get( "millis" ) ) ;
	//
	// logger.debug( "{} response: {}", simpleUrl, Helpers.jsonPrint( Helpers.getDetails( restTemplateResponse ) ) ) ;
	// logger.info( "{} response: {}", simpleUrl, Helpers.jsonPrint( Helpers.getDetails( restTemplateResponse.getBody() ) ) ) ;
	//
	// assertThat( restTemplateResponse.getStatusCode() ).isEqualTo( HttpStatus.OK ) ;
	// assertThat( restTemplateResponse.getHeaders().get( HttpHeaders.SET_COOKIE ).toString() ).contains( "JSESSIONID" ) ;
	// assertThat( restTemplateResponse.getBody().get( "message" ).asText() ).isEqualTo( "secured-get" ) ;
	//
	// }

	// @Inject
	// KeycloakSpringBootProperties keyProps ;
	//
	// private AccessTokenResponse getAccessToken () {
	// Configuration c = new Configuration() ;
	// c.setRealm( keyProps.getRealm() ) ;
	// c.setAuthServerUrl( keyProps.getAuthServerUrl() ) ;
	// c.setResource( keyProps.getResource() ) ;
	// c.setCredentials( keyProps.getCredentials() );
	// AuthzClient authzClient = AuthzClient.create( c ) ;
	//
	// // AuthzClient authzClient = AuthzClient.create() ;
	// AccessTokenResponse accessResponse = authzClient.obtainAccessToken( "peter", "peter" ) ;
	// return accessResponse ;
	// }

}
