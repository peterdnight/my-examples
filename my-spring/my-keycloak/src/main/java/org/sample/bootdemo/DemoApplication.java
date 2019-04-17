package org.sample.bootdemo ;

import javax.annotation.PostConstruct ;

import org.keycloak.adapters.springboot.KeycloakAutoConfiguration ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration ;
import org.springframework.boot.context.properties.ConfigurationProperties ;

@SpringBootApplication ( exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class,
		KeycloakAutoConfiguration.class } )
@ConfigurationProperties ( prefix = "keycloak" )
public class DemoApplication {

	static Logger logger = LoggerFactory.getLogger( DemoApplication.class ) ;

	public static void main (
								String[] args ) {

		logger.info( "Starting boot" ) ;

		SpringApplication.run( DemoApplication.class, args ) ;

	}

	String	authServerUrl ;
	String	resource ;

	@PostConstruct
	public void setup () {
		logger.info( Helpers.header( "{}{} {}{}" ),
			Helpers.padLine( "keycloak url" ), getAuthServerUrl(),
			Helpers.padLine( "keycloak resource" ), getResource() ) ;
	}

	public String getAuthServerUrl () {
		return authServerUrl ;
	}

	public void setAuthServerUrl (
									String authServerUrl ) {
		this.authServerUrl = authServerUrl ;
	}

	public String getResource () {
		return resource ;
	}

	public void setResource (
								String resource ) {
		this.resource = resource ;
	}

}
