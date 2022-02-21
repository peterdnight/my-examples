package org.sample.springdata.api ;

import javax.validation.ConstraintViolationException ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.validation.FieldError ;
import org.springframework.web.bind.MethodArgumentNotValidException ;
import org.springframework.web.bind.annotation.ControllerAdvice ;
import org.springframework.web.bind.annotation.ExceptionHandler ;
import org.springframework.web.bind.annotation.ResponseBody ;
import org.springframework.web.bind.annotation.ResponseStatus ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;

@ControllerAdvice
public class REST_Error_Handler {

	final Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	@Autowired
	ObjectMapper jsonMapper ;

//	@Autowired
//	CsapMeterUtilities metricUtilities ;

	@ResponseStatus ( HttpStatus.BAD_REQUEST )
	@ResponseBody
	@ExceptionHandler ( {
			MethodArgumentNotValidException.class, ConstraintViolationException.class
	} )
	public JsonNode handleValidationExceptions ( Exception exception ) {

		var errorReport = jsonMapper.createObjectNode( ) ;

		errorReport.put( "error", true ) ;
		errorReport.put( "reason", exception.getMessage( ) ) ;
		var errorItems = errorReport.putArray( "items" ) ;

		if ( exception instanceof MethodArgumentNotValidException ) {

			var methodArgError = (MethodArgumentNotValidException) exception ;
			
			methodArgError.getBindingResult( ).getAllErrors( ).forEach( ( error ) -> {

				var fieldName = ( (FieldError) error ).getField( ) ;
				
				var itemReport = errorItems.addObject( ) ;
				itemReport.put( "name", fieldName ) ;
				itemReport.put( "message", error.getDefaultMessage( ) ) ;

			} ) ;

		} else if ( exception instanceof ConstraintViolationException ) {

			var methodArgError = (ConstraintViolationException) exception ;
			methodArgError.getConstraintViolations( ).forEach( ( error ) -> {

				
				var itemReport = errorItems.addObject( ) ;
				itemReport.put( "parameter", error.getPropertyPath( ).toString( ) ) ;
				itemReport.put( "failedValue", error.getInvalidValue( ).toString( ) ) ;
				itemReport.put( "message", error.getMessage( ) ) ;

			} ) ;

		}

		return errorReport ;

	}

}
