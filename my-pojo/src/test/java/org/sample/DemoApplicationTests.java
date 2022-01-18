package org.sample ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
public class DemoApplicationTests {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Helpers.initialize( "Test Setup Complete" ) ;

	}

	@Test
	public void contextLoads ( ) {

		logger.info( Helpers.testHeader( ) ) ;

		System.out.println( "hi" ) ;

	}

}
