package org.sample.bootdemo ;

import java.io.IOException ;
import java.math.BigDecimal ;
import java.math.RoundingMode ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.EnumSet ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;
import java.util.concurrent.ConcurrentHashMap ;
import java.util.function.BiConsumer ;
import java.util.function.BinaryOperator ;
import java.util.function.Function ;
import java.util.function.Predicate ;
import java.util.function.Supplier ;
import java.util.stream.Collector ;
import java.util.stream.Stream ;
import java.util.stream.StreamSupport ;

import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.text.WordUtils ;
import org.apache.logging.log4j.Level ;
import org.apache.logging.log4j.core.config.Configurator ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.fasterxml.jackson.core.JsonProcessingException ;
import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.SerializationFeature ;
import com.fasterxml.jackson.databind.node.ArrayNode ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

public class Helpers {
	final public static String LINE = "\n_______________________________________________________________________________________________\n" ;

	public static String header (
									String message ) {
		return "\n\n" + LINE + "\n " + message + LINE ;
	}

	public static String testHeader (
										String message ) {
		return "\n\n" + LINE
				+ "\n Testing: " + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + message
				+ LINE ;
	}

	public static String testHeader () {
		return "\n\n" + LINE + "\n Testing: " + Thread.currentThread().getStackTrace()[2].getMethodName()
				+ LINE ;
	}

	final static private Logger logger = LoggerFactory.getLogger( Helpers.class ) ;

	public static void printDetails (
										Object theItem ) {

		ObjectMapper jsonMapper = new ObjectMapper() ;
		jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false ) ;
		ObjectNode r = jsonMapper.convertValue( theItem, ObjectNode.class ) ;
		logger.info( "{}:\n {}", Thread.currentThread().getStackTrace()[2].getMethodName(), Helpers.jsonPrint( r ) ) ;
	}

	public static JsonNode getDetails (
											Object theItem ) {

		ObjectMapper jsonMapper = new ObjectMapper() ;
		jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false ) ;
		
		JsonNode jsonDetails =null ;
		try {
			if ( theItem instanceof String ) {
				jsonDetails =  jsonMapper.readTree( (String) theItem ) ;
			} else {
				jsonDetails = jsonMapper.convertValue( theItem, ObjectNode.class ) ;
			}
		} catch ( Exception e ) {
			logger.warn( buildSampleStack( e ) );
		}
		
		logger.debug( "{}:\n {}", Thread.currentThread().getStackTrace()[2].getMethodName(), Helpers.jsonPrint( jsonDetails ) ) ;

		return jsonDetails ;
	}

	public static final long	ONE_SECOND_MS	= 1000 ;
	public static final long	ONE_MINUTE_MS	= 60 * 1000 ;

	public static final long	MB_FROM_BYTES	= 1024 * 1024 * 1 ;

	public static <T> Predicate<T> distinctByKey (
													Function<? super T, Object> keyExtractor ) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>() ;
		return t -> map.putIfAbsent( keyExtractor.apply( t ), Boolean.TRUE ) == null ;
	}

	public static Stream<String> asStreamHandleNulls (
														ObjectNode jsonTree ) {

		// handle empty lists
		if ( jsonTree == null ) {
			return (new ArrayList<String>()).stream() ;
		}

		return asStream( jsonTree.fieldNames() ) ;
	}

	public static Stream<JsonNode> jsonStream (
												JsonNode node ) {
		return StreamSupport.stream( node.spliterator(), false ) ;
	}

	public static <T> Stream<T> asStream (
											Iterator<T> sourceIterator ) {
		return asStream( sourceIterator, false ) ;
	}

	public static <T> Stream<T> asStream (
											Iterator<T> sourceIterator,
											boolean parallel ) {
		Iterable<T> iterable = () -> sourceIterator ;
		return StreamSupport.stream( iterable.spliterator(), parallel ) ;
	}

	private static final ObjectMapper _jsonMapper = new ObjectMapper() ;

	public static String jsonPrint (
										JsonNode j ) {
		try {
			return WordUtils.wrap( _jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString( j ), 180, "\n\t\t", true ) ;
		} catch ( JsonProcessingException e ) {
			logger.warn( "Failed rendering a json object: {}", buildSampleStack( e ) ) ;
		}
		return "FAILED_TO_PARSE" ;
	}

	public static String jsonPrint (
										ObjectMapper jacksonMapper,
										JsonNode j )
			throws JsonProcessingException {
		return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString( j ) ;
	}

	public static String getRequestSource () {
		String stack = Arrays.asList( Thread.currentThread().getStackTrace() ).stream()
			.filter( stackElement -> {
				return (!stackElement.getClassName().equals( Helpers.class.getName() ))
						&& (!stackElement.getClassName().startsWith( "java." )) ;
			} )
			.map( StackTraceElement::toString )
			.findFirst()
			.orElse( "Stack not found" ) ;
		return stack ;
	}

	public static String buildSampleStack (
											Throwable possibleNestedThrowable ) {
		return buildFilteredStack( possibleNestedThrowable, "sample" ) ;
	}

	public static String buildFilteredStack (
												Throwable possibleNestedThrowable,
												String pattern ) {
		// add the class name and any message passed to constructor
		final StringBuffer	result				= new StringBuffer() ;

		Throwable			currentThrowable	= possibleNestedThrowable ;

		int					nestedCount			= 1 ;
		while ( currentThrowable != null ) {

			if ( nestedCount == 1 ) {

				result.append( LINE ) ;
				result.append( "\nCSAP Exception, Filter:  " + pattern ) ;
			} else {
				result.append( "\n Nested Count: " ) ;
				result.append( nestedCount ) ;
				result.append( LINE ) ;
			}
			result.append( "\n\n Exception: " + currentThrowable
				.getClass()
				.getName() ) ;
			result.append( "\n Message: " + currentThrowable.getMessage() ) ;
			result.append( "\n\n StackTrace: \n" ) ;

			// add each element of the stack trace
			List<StackTraceElement>		traceElements	= Arrays.asList( currentThrowable.getStackTrace() ) ;

			Iterator<StackTraceElement>	traceIt			= traceElements.iterator() ;
			while ( traceIt.hasNext() ) {
				StackTraceElement	element		= traceIt.next() ;
				String				stackDesc	= element.toString() ;
				if ( pattern == null || stackDesc.contains( pattern ) ) {
					result.append( stackDesc ) ;
					result.append( "\n" ) ;
				}
			}
			result.append( LINE ) ;
			currentThrowable = currentThrowable.getCause() ;
			nestedCount++ ;
		}
		return result.toString() ;
	}

	public static <T> Predicate<T> not (
											Predicate<T> t ) {
		return t.negate() ;
	}

	public static double roundIt (
									double toBeTruncated,
									int precision ) {
		return BigDecimal.valueOf( toBeTruncated )
			.setScale( precision, RoundingMode.HALF_UP )
			.doubleValue() ;
	}

	public static String alphaNumericOnly (
											String input ) {
		return input.replaceAll( "[^A-Za-z0-9]", "_" ) ;
	}

	public static String note (
								String message ) {
		return "\n\n" + LINE + "\n " + message + LINE ;
	}

	static public String padLine (
									String content ) {
		return "\n    " + StringUtils.rightPad( content + ":", 30 ) + "  " ;
	}

	public static String pad (
								String input ) {
		return StringUtils.rightPad( input, 25 ) ;
	}

	public static String lpad (
								String input ) {
		return StringUtils.leftPad( input, 15 ) ;
	}

	public static void logLevel (
									String className,
									Level l ) {
		Configurator.setAllLevels( className, l ) ;
	}

	public static void setLogToDebug (
										String className ) {
		Configurator.setAllLevels( className, Level.DEBUG ) ;
	}

	public static void setLogToInfo (
										String className ) {
		Configurator.setAllLevels( className, Level.INFO ) ;
	}

	public static class Collectors {
		public static ArrayNodeCollector toArrayNode () {
			return new ArrayNodeCollector() ;
		}

		static public class ArrayNodeCollector implements Collector<JsonNode, ArrayNode, ArrayNode> {

			@Override
			public Supplier<ArrayNode> supplier () {
				// This provides a Function which creates
				// a new instance of the accumulation type.
				// In my case, it has to return a method which
				// creates an ArrayNode.
				return _jsonMapper::createArrayNode ;
			}

			@Override
			public BiConsumer<ArrayNode, JsonNode> accumulator () {
				// This is pretty simple, it dictates how you
				// wish to accumulate values. Also returns a
				// Function.
				return ArrayNode::add ;
			}

			@Override
			public BinaryOperator<ArrayNode> combiner () {
				// This guy is slightly more complicated; because
				// Streams can run in parallel, they obviously have
				// to be merged at some point - this is where that
				// merge takes place.
				return (
							x,
							y ) -> {
					x.addAll( y ) ;
					return x ;
				} ;
			}

			@Override
			public Function<ArrayNode, ArrayNode> finisher () {
				// As mentioned, you can convert your accumulation type
				// to your final return type - that is what this Function
				// does.
				return accumulator -> accumulator ;
			}

			@Override
			public Set<Characteristics> characteristics () {
				// This method returns a list of characteristics associated
				// with the Collector. For example, a thread-safe Collector
				// may return Characteristics.CONCURRENT.
				return EnumSet.of( Characteristics.UNORDERED ) ;

			}

		}
	}

}
