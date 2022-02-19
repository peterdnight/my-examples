package org.sample.springdata.ui ;

import java.io.IOException ;

import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.apache.commons.lang3.exception.ExceptionUtils ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.http.HttpStatus ;
import org.springframework.web.bind.annotation.ControllerAdvice ;
import org.springframework.web.bind.annotation.ExceptionHandler ;
import org.springframework.web.bind.annotation.ResponseStatus ;

import com.fasterxml.jackson.core.JsonParseException ;

@ControllerAdvice
public class Error_Handling {

	final Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

//	@Autowired
//	CsapMeterUtilities metricUtilities ;

	@ResponseStatus ( value = HttpStatus.INTERNAL_SERVER_ERROR , reason = "Exception during processing, examine server Logs" )
	@ExceptionHandler ( Exception.class )
	public void defaultHandler ( HttpServletRequest request , Exception e ) {

		commonHandling( request, e ) ;

	}

	private void commonHandling ( HttpServletRequest request , Exception e ) {

		request.setAttribute( "csapFiltered", Utils.buildSampleStack( e ) ) ;
		logger.warn( "{}: {}", request.getRequestURI( ), Utils.buildSampleStack( e ) ) ;
		logger.debug( "Full exception", e ) ;
//		metricUtilities.incrementCounter( CsapGlobalId.EXCEPTION.id ) ;

	}

	@ResponseStatus ( value = HttpStatus.INTERNAL_SERVER_ERROR , reason = "Exception during processing, examine server Logs" )
	@ExceptionHandler ( NullPointerException.class )
	public void handleNullPointer ( HttpServletRequest request , Exception e ) {

		commonHandling( request, e ) ;

	}

	@ResponseStatus ( value = HttpStatus.INTERNAL_SERVER_ERROR , reason = "Exception during processing, examine server Logs" )
	@ExceptionHandler ( JsonParseException.class )
	public void handleJsonParsing ( HttpServletRequest request , Exception e ) {

		commonHandling( request, e ) ;

	}

	// ClientAbort which extends ioexception cannot have response written cannot
	// have a response written
	@ExceptionHandler ( IOException.class )
	public void handleIOException ( HttpServletRequest request , Exception e , HttpServletResponse response ) {

		String stackFrames = ExceptionUtils.getStackTrace( e ) ;

		if ( stackFrames.contains( "ClientAbortException" ) ) {

			logger.info( "ClientAbortException found: " + e.getMessage( ) ) ;

		} else {

			commonHandling( request, e ) ;

			try {

				response.setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value( ) ) ;
				response.getWriter( )
						.print( HttpStatus.INTERNAL_SERVER_ERROR.value( )
								+ " : Exception during processing, examine server Logs" ) ;

			} catch ( IOException e1 ) {

				// TODO Auto-generated catch block
				e1.printStackTrace( ) ;

			}

		}

	}

}
