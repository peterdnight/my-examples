package org.sample.bootdemo ;

import org.springframework.boot.autoconfigure.SpringBootApplication ;

@SpringBootApplication
public class DemoApplication {

	public static void main ( String[] args ) {

		// SpringApplication.run(DemoApplication.class, args);
		Helpers.run( DemoApplication.class, args ) ;

	}

}
