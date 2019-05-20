package org.sample.bootdemo ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;
import java.util.Collections ;

import javax.servlet.http.HttpServletRequest ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.ResponseEntity ;
import org.springframework.security.authentication.AbstractAuthenticationToken ;
import org.springframework.security.core.Authentication ;
import org.springframework.security.core.annotation.AuthenticationPrincipal ;
import org.springframework.security.core.context.SecurityContextHolder ;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient ;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService ;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken ;
import org.springframework.security.oauth2.core.OAuth2AccessToken ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RestController ;
import org.springframework.web.client.RestTemplate ;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@RestController
// @RequestMapping ( MyRestApi.URI_BASE )
public class MyRestApi {

	private static final String	ACCESS_DENIED			= "Access Denied" ;

	Logger						logger					= LoggerFactory.getLogger( getClass() ) ;

	public final static String	URI_ANONYMOUS			= "/anonymous" ;
	public final static String	URI_AUTHENTICATED		= "/authenticated" ;
	public final static String	URI_OPEN				= "/open" ;
	public final static String	URI_AUTHORIZED			= "/authorized" ;
	private final static String	URI_HI					= "/hi" ;
	private final static String	URI_PROXY				= "/proxy" ;
	public final static String	URI_AUTHENTICATED_HI	= URI_AUTHENTICATED + URI_HI ;
	public final static String	URI_AUTHENTICATED_PROXY	= URI_AUTHENTICATED + URI_PROXY ;
	public final static String	URI_AUTHORIZED_HI		= URI_AUTHORIZED + URI_HI ;
	public final static String	URI_OPEN_API_HI			= URI_OPEN + URI_HI ;
	public final static String	URI_ANON_API_HI			= URI_ANONYMOUS + URI_HI ;

	public final static String	URI_ACCESS_DENIED		= URI_OPEN + "/accessDenied" ;

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
			if ( !location.equals( ACCESS_DENIED ) ) {
				hi.put( "test authorized", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_AUTHORIZED_HI ).toUriString() ) ;
				hi.put( "test authenticated",
					ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_AUTHENTICATED_HI ).toUriString() ) ;
				hi.put( "test proxy",
					ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_AUTHENTICATED_PROXY ).toUriString() ) ;
				hi.put( "test open", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_OPEN_API_HI ).toUriString() ) ;

				hi.put( "test webclient: specified",
					ServletUriComponentsBuilder.fromCurrentContextPath()
						.path( WebClientController.URI_CLIENT_SELECTION_HI ).toUriString() ) ;

				hi.put( "test webclient: auto",
					ServletUriComponentsBuilder.fromCurrentContextPath()
						.path( WebClientController.URI_AUTO_SELECTION_HI ).toUriString() ) ;

				hi.put( "test webclient: machine",
					ServletUriComponentsBuilder.fromCurrentContextPath()
						.path( WebClientController.URI_ANONYMOUS_SELECTION_HI ).toUriString() ) ;

				hi.put( "test anonymous", ServletUriComponentsBuilder.fromCurrentContextPath().path( URI_ANON_API_HI ).toUriString() ) ;
				hi.put( "springboot", ServletUriComponentsBuilder.fromCurrentContextPath().path( "/manage" ).toUriString() ) ;
				hi.put( "logout", ServletUriComponentsBuilder.fromCurrentContextPath().path( "/logout" ).toUriString() ) ;

				hi.set( "userInfo", Helpers.getDetails( customUser.getPrincipal() ).path( "userInfo" ) ) ;
				hi.set( "full-principal", Helpers.getDetails( customUser ) ) ;
			}
		} catch ( Exception e ) {

			logger.info( Helpers.buildSampleStack( e ) ) ;
		}

		return hi ;
	}

	@GetMapping ( URI_AUTHORIZED_HI )
	public JsonNode authorizedHi (
									@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		var			location	= "authorized-get" ;

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

		var location = "authenticated-get" ;

		return buildApiResponse( customUser, location ) ;
	}

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService ;

	@GetMapping ( URI_AUTHENTICATED_PROXY )
	public JsonNode proxyHi (	OAuth2AuthenticationToken oauthToken,
								@AuthenticationPrincipal AbstractAuthenticationToken customUser,
								HttpServletRequest httpRequest ) {

		var			location	= "authenticated-proxy" ;
		
		if ( oauthToken == null) {
			return buildApiResponse( customUser, location + "-error: missing authenticated user" ) ;
		}
		
		
		ObjectNode	response	= jsonMapper.createObjectNode() ;
		response.put( "location", location ) ;
		// JsonNode response = buildApiResponse( customUser, location ) ;

		
		// alternate get authToken from context
		// Authentication authentication = SecurityContextHolder
		// .getContext()
		// .getAuthentication() ;
		//
		// OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication ;

		
		OAuth2AuthorizedClient	authorizedClient	= this.authorizedClientService.loadAuthorizedClient(
			oauthToken.getAuthorizedClientRegistrationId(),
			oauthToken.getName() ) ;

		OAuth2AccessToken		accessToken			= authorizedClient.getAccessToken() ;

		RestTemplate			myRestTemplate		= new RestTemplate() ;
		myRestTemplate.setInterceptors(
			Collections.singletonList( (
											request,
											body,
											execution ) -> {
				request.getHeaders()
					.add( "Authorization", "Bearer " + accessToken.getTokenValue() ) ;
				return execution.execute( request, body ) ;
			} ) ) ;

		String						simpleUrl				= "http://localhost:" + httpRequest.getServerPort()
				+ MyRestApi.URI_AUTHORIZED_HI ;

		ResponseEntity<ObjectNode>	restTemplateResponse	= myRestTemplate
			.getForEntity(
				simpleUrl,
				ObjectNode.class ) ;

		response.set( "proxy-response", restTemplateResponse.getBody() ) ;

		return response ;
	}

	@GetMapping ( URI_ACCESS_DENIED )
	public JsonNode accessDenied (
									@AuthenticationPrincipal AbstractAuthenticationToken customUser ) {

		return buildApiResponse( customUser, ACCESS_DENIED ) ;
	}

}
