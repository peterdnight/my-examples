package org.sample.bootdemo ;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

@TestInstance ( TestInstance.Lifecycle.PER_METHOD )
@DisplayName ( "Rest Api: Direct" )

class MyRestApiTest {

	Logger	logger	= LoggerFactory.getLogger( getClass() ) ;

	@Test
	void testHi () {

		MyRestApi api = new MyRestApi() ;

		assertThat( api.hi() ).isEqualTo( "hi" ) ;

	}

}
