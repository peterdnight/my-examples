package org.sample ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
public class DemoApplicationTests {



	@Test
	public void contextLoads ( ) {

//		logger.info( Helpers.testHeader( ) ) ;

		System.out.println( "hi" ) ;

	}

}
