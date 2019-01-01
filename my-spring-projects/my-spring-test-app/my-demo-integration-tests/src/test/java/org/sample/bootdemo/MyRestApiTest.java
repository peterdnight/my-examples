package org.sample.bootdemo;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.Test ;

public class MyRestApiTest {
	
	

	@Test
	public void testHi () { 
		 
		MyRestApi api = new MyRestApi() ;
		
		assertThat( api.hi() ).isEqualTo( "hi" ) ;
		
	}

}
