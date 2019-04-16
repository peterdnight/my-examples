package org.sample.bootdemo ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;

@SpringBootApplication
public class DemoApplication {
	

	static Logger logger = LoggerFactory.getLogger( DemoApplication.class ) ;

	public static void main (
								String[] args ) {
		
		logger.info("Starting boot") ;
		
		SpringApplication.run( DemoApplication.class, args ) ;
		
	}

}
