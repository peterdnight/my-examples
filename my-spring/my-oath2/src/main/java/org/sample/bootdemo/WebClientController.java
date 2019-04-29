
package org.sample.bootdemo ;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId ;

import java.io.IOException ;

import javax.servlet.http.HttpServletRequest ;

import org.apache.commons.lang3.StringUtils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RestController ;
import org.springframework.web.reactive.function.client.WebClient ;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

@RestController
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
public class WebClientController {

	Logger						logger					= LoggerFactory.getLogger( getClass() ) ;

	private final static String	URI_WEBCLIENT			= "/webclient" ;
	private final static String	URI_HI					= "/hi" ;
	public final static String	URI_CLIENT_SELECTION_HI	= URI_WEBCLIENT + URI_HI + "/client" ;
	public final static String	URI_AUTO_SELECTION_HI	= URI_WEBCLIENT + URI_HI + "/auto" ;

	private final WebClient		webClient ;

	private final String		uri ;
	private final ObjectMapper	jsonMapper ;

	public WebClientController( WebClient webClient, MyRestApi myRestApi, ObjectMapper jsonMapper ) {

		logger.info( Helpers.header( "uri: {}" ), myRestApi.URI_AUTHENTICATED_HI ) ;

		this.webClient	= webClient ;
		this.uri		= myRestApi.URI_AUTHENTICATED_HI ;
		this.jsonMapper	= jsonMapper ;
	}

	@GetMapping ( URI_CLIENT_SELECTION_HI )
	public JsonNode hiUsingSpecificClient ()
			throws IOException {

		ObjectNode	result		= jsonMapper.createObjectNode() ;

		String		targetUrl	= ServletUriComponentsBuilder.fromCurrentContextPath().path( this.uri ).toUriString() ;
		logger.info( "targetUrl: {}", targetUrl ) ;

		result.put( "web client target", targetUrl ) ;
		try {

			String body = getContentUsingWebClient( targetUrl ) ;

			logger.info( "uri: {}, \n\n body(first 20): {}", uri, StringUtils.substring( body, 0, 20 ) ) ;

			result.set( "response", jsonMapper.readTree( body ) ) ;
		} catch ( Exception e ) {
			
			result.put( "response", Helpers.buildSampleStack( e ) ) ;
			logger.warn(  Helpers.buildSampleStack( e ) );
		}

		return result ;
	}

	public String getContentUsingWebClient ( String source ) {

		String body = this.webClient
			.get()
			.uri( source )
			.attributes( clientRegistrationId( WebClientConfig.KEYCLOAK_CLIENT_ROLE ) )
			.retrieve()
			.bodyToMono( String.class )
			.block() ;

		return body ;
	}

	// this picks the "default" client
	@GetMapping ( URI_AUTO_SELECTION_HI )
	JsonNode hiUsingAutoClient ( HttpServletRequest request )
			throws IOException {

		ObjectNode	result		= jsonMapper.createObjectNode() ;

		String		targetUrl	= ServletUriComponentsBuilder.fromCurrentContextPath().path( this.uri ).toUriString() ;
		logger.info( "targetUrl: {}", targetUrl ) ;

		result.put( "web client target", targetUrl ) ;
		try {

			String body = this.webClient
					.get()
					.uri( targetUrl )
					.retrieve()
					.bodyToMono( String.class )
					.block() ;

			logger.info( "uri: {}, \n\n body(first 20): {}", uri, StringUtils.substring( body, 0, 20 ) ) ;

			result.set( "response", jsonMapper.readTree( body ) ) ;
		} catch ( Exception e ) {
			
			result.put( "response", Helpers.buildSampleStack( e ) ) ;
			logger.warn(  Helpers.buildSampleStack( e ) );
		}

		return result ;

	}
}
