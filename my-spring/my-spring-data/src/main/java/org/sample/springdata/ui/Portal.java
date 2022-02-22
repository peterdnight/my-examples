package org.sample.springdata.ui ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.cache.annotation.CacheConfig ;
import org.springframework.stereotype.Controller ;
import org.springframework.ui.Model ;
import org.springframework.web.bind.annotation.GetMapping ;

@Controller
public class Portal {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	
	@GetMapping ( value = "/" )
	public String get ( Model springViewModel )
		throws Exception {

		logger.info( Utils.highlightHeader( "landing page" ) ) ;

		springViewModel.addAttribute( "portalClass", Portal.class.getCanonicalName( ) ) ;

		return "employee-portal" ;

	}

	//
	//
	//
	// Common Integration Escapes
	//
	//
	//
	@GetMapping ( "/malformedTemplate" )
	public String malformedTemplate ( Model springViewModel ) {

		logger.info( "Sample thymeleaf controller" ) ;

		springViewModel.addAttribute( "dateTime",
				LocalDateTime.now( ).format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) ) ;

		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		return "/malformed-example" ;

	}

	@GetMapping ( "/missingTemplate" )
	public String missingTempate ( Model springViewModel ) {

		logger.info( "Sample thymeleaf controller" ) ;

		springViewModel.addAttribute( "dateTime",
				LocalDateTime.now( ).format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) ) ;

		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		return "/missingTemplate" ;

	}

	@GetMapping ( "/testException" )
	public String testException ( ) {

		logger.info( "simple log" ) ;
		throw new RuntimeException( "Spring Rest Exception" ) ;

	}

	@GetMapping ( "/testNullPointer" )
	public String testNullPointer ( ) {

		if ( System.currentTimeMillis( ) > 1 ) {

			throw new NullPointerException( "For testing only" ) ;

		}

		return "hello" ;

	}

}
