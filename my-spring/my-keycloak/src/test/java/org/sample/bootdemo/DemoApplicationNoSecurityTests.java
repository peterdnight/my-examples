package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;
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
import org.springframework.test.context.ActiveProfiles ;
import org.springframework.test.web.servlet.MockMvc ;
import org.springframework.test.web.servlet.ResultActions ;
import org.springframework.test.web.servlet.setup.MockMvcBuilders ;
import org.springframework.web.context.WebApplicationContext ;

@SpringBootTest
@ActiveProfiles ( "test,no-security" )

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@DisplayName ( "Demo Application: Security Disabled" )

public class DemoApplicationNoSecurityTests {

	Logger					logger	= LoggerFactory.getLogger( getClass() ) ;

	@Inject
	WebApplicationContext	wac ;
	MockMvc					mockMvc ;

	@BeforeAll
	void beforeAll ()
			throws Exception {

		logger.info( Helpers.testHeader() ) ;

		this.mockMvc = MockMvcBuilders.webAppContextSetup( this.wac ).build() ;
	}

	@Test
	void contextLoads () {}

	@Test
	public void verifyHiIsNotSecured ()
			throws Exception {
		
		logger.info( Helpers.testHeader()) ;
		
		// mock does much validation.....
		ResultActions	resultActions	= mockMvc.perform(
			get( MyRestApi.URI_API_HI )
				// .param( "sampleParam1", "sampleValue1" )
				// .param( "sampleParam2", "sampleValue2" )
				.accept( MediaType.TEXT_PLAIN ) ) ;

		//
		String			result			= resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentTypeCompatibleWith( MediaType.TEXT_PLAIN ) )
			.andReturn().getResponse().getContentAsString() ;
		logger.info( "result:\n" + result ) ;

		assertThat( result ).contains( "hi" ) ;

	}

}
