package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;

import javax.inject.Inject ;

import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment ;
import org.springframework.boot.test.web.client.TestRestTemplate ;
import org.springframework.boot.web.client.RestTemplateBuilder ;
import org.springframework.boot.web.server.LocalServerPort ;
import org.springframework.http.HttpHeaders ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.test.context.ActiveProfiles ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@SpringBootTest ( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles ( "test" )

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@DisplayName ( "Demo Application: full context with Real Security" )

public class DemoApplicationRealSecurityTests {

	Logger				logger	= LoggerFactory.getLogger( getClass() ) ;

	@LocalServerPort
	private int			testPort ;

	@Inject
	RestTemplateBuilder	restTemplateBuilder ;

	@Inject
	ObjectMapper		jsonMapper ;

	@Inject
	MyRestApi			myRestApi ;

	@BeforeAll
	void beforeAll ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

	}

	@Test
	void contextLoads () {}

	@Test
	@DisplayName ( "user and password correct" )
	public void verifyHiWithUserAndPass ()
			throws Exception {

		String simpleUrl = "http://localhost:" + testPort + MyRestApi.URI_SECURE_API_HI ;

		logger.info( Helpers.testHeader( simpleUrl ) ) ;

		TestRestTemplate		restTemplateWithAuth	= new TestRestTemplate(
			SecurityConfiguration.USER, SecurityConfiguration.USER ) ;

		ResponseEntity<String>	responseFromCredQuery	= restTemplateWithAuth
			.getForEntity(
				simpleUrl,
				String.class ) ;

		Helpers.printDetails( responseFromCredQuery ) ;

		assertThat( responseFromCredQuery.getStatusCode() ).isEqualTo( HttpStatus.OK ) ;
		assertThat( responseFromCredQuery.getHeaders().get( HttpHeaders.SET_COOKIE ).toString() ).contains( "JSESSIONID" ) ;

		assertThat( responseFromCredQuery.getBody() ).matches( myRestApi.resultTestPattern() ) ;

	}

}
