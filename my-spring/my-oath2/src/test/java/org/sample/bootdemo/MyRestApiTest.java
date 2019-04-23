package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;

@TestInstance ( TestInstance.Lifecycle.PER_METHOD )
@DisplayName ( "Rest Api: Direct" )

class MyRestApiTest {

	Logger	logger	= LoggerFactory.getLogger( getClass() ) ;

	@Test
	void testHi () {
		logger.info( Helpers.testHeader() );

		MyRestApi api = new MyRestApi(new ObjectMapper()) ;
		
		JsonNode result = api.authorizedHi( null ) ;
		
		logger.info( "result: {}", Helpers.jsonPrint( result ) );

		assertThat( result.toString() ).matches( api.resultTestPattern() ) ;

	}

}
