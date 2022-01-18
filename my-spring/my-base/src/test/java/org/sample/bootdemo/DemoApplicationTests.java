package org.sample.bootdemo ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )
@SpringBootTest ( classes = DemoApplicationTests.Simple_Application.class , webEnvironment = WebEnvironment.RANDOM_PORT )
public class DemoApplicationTests {
	

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;
	

	static {
		Helpers.initialize( "Test Setup Complete" ) ;
	}

	

	/**
	 * 
	 * Simple test app that excludes security autoconfiguration
	 * ManagementWebSecurityAutoConfiguration.class
	 */
	@SpringBootApplication ( exclude = {
			SecurityAutoConfiguration.class,
			ManagementWebSecurityAutoConfiguration.class,
			LdapAutoConfiguration.class
	} )
	public static class Simple_Application {

		@RestController
		static public class Hello {

			@GetMapping ( "/hi" )
			public String hi ( ) {

				return "Hello" +
						LocalDateTime.now( )
								.format( DateTimeFormatter
										.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) ;

			}

			@Autowired
			ObjectMapper jsonMapper ;

		}

	}

	@Test
	public void contextLoads ( ) {
		
		logger.info( Helpers.testHeader( ) );

		System.out.println( "hi" ) ;

	}

}
