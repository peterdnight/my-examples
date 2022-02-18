package org.sample.springdata ;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.sample.springdata.api.MyRestApi ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.test.context.SpringBootTest ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@SpringBootTest
public class MyRestApiTest {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	@Test
	public void testHi ( ) {

		logger.info( Utils.testHeader( ) ) ;

		MyRestApi api = new MyRestApi( ) ;

		logger.info( Utils.highlightHeader( "api response: {}" ),
				api.hi( ) ) ;

		assertThat( api.hi( ) ).isEqualTo( "hi" ) ;

	}

}
