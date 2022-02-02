package org.sample.bootdemo ;

import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@RestController
@RequestMapping ( "/api" )
public class MyRestApi {
	
	
	@Autowired
	CsapMeterUtilities meterUtilities ;
	

	@Autowired
	ObjectMapper jacksonMapper ;
	

	@GetMapping ( "/hi" )
	public String hi ( ) {
		
		meterUtilities.incrementCounter( "csap.hi" ) ;
		
		meterUtilities.incrementCounter( "never.hi" ) ;

		return "hi" ;

	}

}
