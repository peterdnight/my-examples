package org.sample.bootdemo ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.security.authentication.AbstractAuthenticationToken ;
import org.springframework.security.core.annotation.AuthenticationPrincipal ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RestController ;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@RestController
// @RequestMapping ( MyRestApi.URI_BASE )
public class MyRestApi {

	Logger						logger					= LoggerFactory.getLogger( getClass() ) ;

	public final static String	URI_ANONYMOUS			= "/anonymous" ;
	public final static String	URI_AUTHENTICATED		= "/authenticated" ;
	public final static String	URI_OPEN				= "/open" ;
	public final static String	URI_AUTHORIZED			= "/authorized" ;
	private final static String	URI_HI					= "/hi" ;
	public final static String	URI_AUTHENTICATED_HI	= URI_AUTHENTICATED + URI_HI ;
	public final static String	URI_AUTHORIZED_HI		= URI_AUTHORIZED + URI_HI ;
	public final static String	URI_OPEN_API_HI			= URI_OPEN + URI_HI ;
	public final static String	URI_ANON_API_HI			= URI_ANONYMOUS + URI_HI ;

	ObjectMapper				jsonMapper ;

	@Autowired
	public MyRestApi( ObjectMapper jsonMapper ) {
		this.jsonMapper = jsonMapper ;
	}

	@GetMapping
	public JsonNode defaultGet (
									@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var			location	= "default-get" ;

		ObjectNode	hi			= buildApiResponse( customUser, location ) ;

		return hi ;
	}

	private ObjectNode buildApiResponse (
											AbstractAuthenticationToken customUser,
											String location ) {
		ObjectNode hi = jsonMapper.createObjectNode() ;
		hi.put( "message", location ) ;
		var formatedTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss, MMMM d  uuuu " ) ) ;
		hi.put( "time", formatedTime ) ;
		hi.put( "millis", System.currentTimeMillis() ) ;

		try {
			hi.put( "landing page", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() ) ;
			hi.put( "test authorized", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_AUTHORIZED_HI ).toUriString() ) ;
			hi.put( "test authenticated", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_AUTHENTICATED_HI ).toUriString() ) ;
			hi.put( "test open", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_OPEN_API_HI ).toUriString() ) ;
			hi.put( "test anonymous", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_ANON_API_HI ).toUriString() ) ;
			hi.put( "springboot", ServletUriComponentsBuilder.fromCurrentContextPath().path( "/manage" ).toUriString() ) ;
			hi.put( "sso/logout", ServletUriComponentsBuilder.fromCurrentContextPath().path( "/sso/logout" ).toUriString() ) ;
		} catch ( Exception e ) {

			logger.info( Helpers.buildSampleStack( e ) ) ;
		}

		hi.set( "principal", Helpers.getDetails( customUser ) ) ;
		return hi ;
	}

	@GetMapping ( URI_AUTHORIZED_HI )
	public JsonNode authorizedHi (
							@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var			location	= "secured-get" ;

		ObjectNode	hi			= buildApiResponse( customUser, location ) ;

		return hi ;
	}

	public String resultTestPattern () {
		return ".*message.*time.*" ;
	}

	@GetMapping ( URI_OPEN_API_HI )
	public JsonNode openHi (
								@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var location = "unsecured-get" ;

		return buildApiResponse( customUser, location ) ;
	}

	@GetMapping ( URI_ANON_API_HI )
	public JsonNode anonymousHi (
									@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var location = "anonymous-get" ;

		return buildApiResponse( customUser, location ) ;
	}


	@GetMapping ( URI_AUTHENTICATED_HI )
	public JsonNode authHi (
									@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var location = "anonymous-get" ;

		return buildApiResponse( customUser, location ) ;
	}

}
