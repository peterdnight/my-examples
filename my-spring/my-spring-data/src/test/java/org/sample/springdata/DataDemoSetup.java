package org.sample.springdata ;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.context.ApplicationContext ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@SpringBootTest ( classes = DataDemoApplication.class )
public class DataDemoSetup {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	@Autowired
	private ApplicationContext applicationContext ;

	@Test
	public void contextLoads ( ) {

		logger.info( Utils.testHeader( ) ) ;


		logger.info( "beans: {}", applicationContext.getBeanDefinitionCount( ) ) ;

		assertThat( applicationContext.getBeanDefinitionCount( ) )
				.as( "Spring Bean count" )
				.isGreaterThan( 100 ) ;

	}

}
