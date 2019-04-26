package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous ;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user ;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity ;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get ;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content ;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status ;

import javax.inject.Inject ;

import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.http.MediaType ;
import org.springframework.mock.web.MockHttpServletResponse ;
import org.springframework.security.test.context.support.WithMockUser ;
import org.springframework.test.context.ActiveProfiles ;
import org.springframework.test.web.servlet.MockMvc ;
import org.springframework.test.web.servlet.ResultActions ;
import org.springframework.test.web.servlet.setup.MockMvcBuilders ;
import org.springframework.web.context.WebApplicationContext ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@SpringBootTest
@ActiveProfiles ( "test" )

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@DisplayName ( "Demo Application: full context with Mock Security" )

public class DemoApplicationMockSecurityTests {

	Logger					logger	= LoggerFactory.getLogger( getClass() ) ;

	@Inject
	WebApplicationContext	wac ;
	MockMvc					mockMvc ;

	@Inject
	ObjectMapper			jsonMapper ;

	@Inject
	MyRestApi				myRestApi ;

	@BeforeAll
	void beforeAll ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		this.mockMvc = MockMvcBuilders.webAppContextSetup( this.wac ).apply( springSecurity() ).build() ;
	}

	@Test
	void contextLoads () {}

	@Test
	@WithMockUser(roles=SecurityConfiguration.CSAP_VIEW)
	@DisplayName ( "security with mock user" )
	public void verifySecurityWithMockUser ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( MyRestApi.URI_AUTHORIZED_HI )
				.accept( MediaType.APPLICATION_JSON_UTF8_VALUE  ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON_UTF8_VALUE  ) )
			.andReturn().getResponse().getContentAsString() ;
		
		logger.info( "result: {}", result ) ;

		assertThat( result ).matches( myRestApi.resultTestPattern() ) ;

	}

	@Test
	@DisplayName ( "security with authenticated user" )
	public void verifySecurityWithAuthenticatedUser ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( MyRestApi.URI_AUTHORIZED_HI )
				.with( user( "peter" ).roles( SecurityConfiguration.CSAP_VIEW ) )
				.accept( MediaType.APPLICATION_JSON_UTF8_VALUE  ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON_UTF8_VALUE  ) )
			.andReturn().getResponse().getContentAsString() ;
		logger.info( "result: {}", result ) ;

		assertThat( result ).matches( myRestApi.resultTestPattern() ) ;

	}

	@Test
	@DisplayName ( "login form when secure is hit" )
	public void verifyLoginForm ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions			resultActions	= mockMvc.perform(
			get( MyRestApi.URI_AUTHORIZED_HI ).with( anonymous() )
				.accept( MediaType.APPLICATION_JSON_UTF8_VALUE  ) ) ;

		//
		MockHttpServletResponse	response		= resultActions
			.andExpect( status().is3xxRedirection() )
			.andReturn().getResponse() ;

		Helpers.printDetails( response ) ;

		assertThat( response.getRedirectedUrl() ).isEqualTo( "http://localhost/oauth2/authorization/keycloak-with-login" ) ;

	}

	@Test
	@DisplayName ( "response when user and password is correct" )
	public void verifyHiWithUserAndPass ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( MyRestApi.URI_AUTHORIZED_HI )
				.with( user( "admin" ).password( "admin" ).roles( SecurityConfiguration.CSAP_VIEW ) )
				// .param( "sampleParam1", "sampleValue1" )
				// .param( "sampleParam2", "sampleValue2" )
				.accept( MediaType.APPLICATION_JSON_UTF8_VALUE ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON_UTF8_VALUE ) )
			.andReturn().getResponse().getContentAsString() ;
		logger.info( "result: {}", result ) ;

		assertThat( result ).matches( myRestApi.resultTestPattern() ) ;

	}

}
