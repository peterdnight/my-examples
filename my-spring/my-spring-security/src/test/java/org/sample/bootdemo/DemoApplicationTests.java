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
import org.springframework.test.context.ActiveProfiles ;
import org.springframework.test.web.servlet.MockMvc ;
import org.springframework.test.web.servlet.ResultActions ;
import org.springframework.test.web.servlet.setup.MockMvcBuilders ;
import org.springframework.web.context.WebApplicationContext ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.SerializationFeature ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@SpringBootTest
@ActiveProfiles ( "test" )

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@DisplayName ( "Demo Application: full context with security enabled" )

public class DemoApplicationTests {

	Logger					logger	= LoggerFactory.getLogger( getClass() ) ;

	@Inject
	WebApplicationContext	wac ;
	MockMvc					mockMvc ;

	@Inject
	ObjectMapper			jsonMapper ;

	@BeforeAll
	void beforeAll ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		this.mockMvc = MockMvcBuilders.webAppContextSetup( this.wac ).apply( springSecurity() ).build() ;
	}

	@Test
	void contextLoads () {}

	@Test
	@DisplayName ( "security ignored by default" )
	public void verifySecurityWithAuthenticatedUser ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( "/hi" )
				.with(  user( "peter" ) )
				.accept( MediaType.TEXT_PLAIN ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.TEXT_PLAIN ) )
			.andReturn().getResponse().getContentAsString() ;
		logger.info( "result: {}", result ) ;

		assertThat( result ).contains( (new MyRestApi()).hi() ) ;

	}

	@Test
	@DisplayName ( "login form when secure is hit" )
	public void verifyLoginForm ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions			resultActions	= mockMvc.perform(
			get( "/hi" ).with( anonymous() )
				.accept( MediaType.TEXT_PLAIN ) ) ;

		//
		MockHttpServletResponse	response		= resultActions
			.andExpect( status().is3xxRedirection() )
			.andReturn().getResponse() ;

		jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false ) ;
		ObjectNode r = jsonMapper.convertValue( response, ObjectNode.class ) ;
		logger.info( "result:\n {}", Helpers.jsonPrint( r ) ) ;

		assertThat( response.getRedirectedUrl() ).isEqualTo( "http://localhost/login" ) ;

	}

	@Test
	@DisplayName ( "response when user and password is correct" )
	public void verifyHiWithUserAndPass ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( "/hi" )
				.with( user( "admin" ).password( "admin" ).roles( "USER", "ADMIN" ) )
				// .param( "sampleParam1", "sampleValue1" )
				// .param( "sampleParam2", "sampleValue2" )
				.accept( MediaType.TEXT_PLAIN ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.TEXT_PLAIN ) )
			.andReturn().getResponse().getContentAsString() ;
		logger.info( "result: {}", result ) ;

		assertThat( result ).contains( (new MyRestApi()).hi() ) ;

	}

}
