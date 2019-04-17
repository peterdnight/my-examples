package org.sample.bootdemo ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import javax.inject.Inject ;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken ;
import org.springframework.security.core.annotation.AuthenticationPrincipal ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@RestController
//@RequestMapping ( MyRestApi.URI_BASE )
public class MyRestApi {

	public final static String	URI_BASE	= "/api" ;
	private final static String	URI_HI		= "/hi" ;
	public final static String	URI_API_HI		= URI_BASE + URI_HI ;
	
	@Inject
	ObjectMapper jsonMapper ;
	
	

	@GetMapping 
	public JsonNode defaultGet ( @AuthenticationPrincipal KeycloakAuthenticationToken customUser ) {
		
		ObjectNode hi = jsonMapper.createObjectNode() ;
		
		hi.put( "message", "default-get" ) ;
		hi.put( "secured-url", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_API_HI ).toUriString() ) ;
		var formatedTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) ;
		hi.put( "time", formatedTime ) ;
		
		hi.set("principal", Helpers.getDetails( customUser )) ;
		
		
		return hi ;
	}
	

	@GetMapping ( URI_API_HI )
	public JsonNode hi ( @AuthenticationPrincipal KeycloakAuthenticationToken customUser ) {
		
		ObjectNode hi = jsonMapper.createObjectNode() ;
		
		hi.put( "message", "hi" ) ;
		hi.put( "logout", ServletUriComponentsBuilder.fromCurrentContextPath().path( "/sso/logout" ).toUriString() ) ;
		var formatedTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) ;
		hi.put( "time", formatedTime ) ;
		
		hi.set("principal", Helpers.getDetails( customUser )) ;
		
		
		return hi ;
	}
	
	public String hiTest() {
		return "hi" ;
	}
	
	@GetMapping ( URI_BASE + "/open/hi" )
	public String openHi () {
		return "open hi: no security" ;
	}

}
