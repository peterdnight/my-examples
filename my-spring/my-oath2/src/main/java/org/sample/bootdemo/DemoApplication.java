package org.sample.bootdemo ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;

@SpringBootApplication ( exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class,
		OAuth2ResourceServerAutoConfiguration.class
} )
@ConfigurationProperties ( prefix = "keycloak" )
public class DemoApplication {

	static Logger logger = LoggerFactory.getLogger( DemoApplication.class ) ;

	public static void main (
								String[] args ) {

		logger.info( "Starting boot" ) ;

		SpringApplication.run( DemoApplication.class, args ) ;

	}

}
