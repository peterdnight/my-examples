/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample ;

import java.io.File ;
import java.io.IOException ;
import java.math.BigDecimal ;
import java.math.RoundingMode ;
import java.net.URISyntaxException ;
import java.net.URL ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.StandardCopyOption ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.EnumSet ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;
import java.util.concurrent.ConcurrentHashMap ;
import java.util.concurrent.TimeUnit ;
import java.util.function.BiConsumer ;
import java.util.function.BinaryOperator ;
import java.util.function.Function ;
import java.util.function.Predicate ;
import java.util.function.Supplier ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;
import java.util.stream.Collector ;
import java.util.stream.Stream ;
import java.util.stream.StreamSupport ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.text.WordUtils ;
import org.apache.logging.log4j.Level ;
import org.apache.logging.log4j.LogManager ;
import org.apache.logging.log4j.core.LoggerContext ;
import org.apache.logging.log4j.core.config.Configurator ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.fasterxml.jackson.core.JsonProcessingException ;
import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.SerializationFeature ;
import com.fasterxml.jackson.databind.node.ArrayNode ;
import com.fasterxml.jackson.databind.node.ObjectNode ;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule ;

/**
 *
 * Constants shared in multiple packages or components
 * 
 * 
 * @author someDeveloper
 */
public class Helpers {

	static String LOG4J2_TEST_YML = "log4j2-test.yml" ;

	final static private Logger logger = LoggerFactory.getLogger( Helpers.class ) ;

	public static final String CSAP_INSTALL_VARIABLE = "CSAP_FOLDER" ;

	// csap-data publication keys shared between events and agent
	public static final String COLLECTION_APPLICATION = "application" ;
	public static final String COLLECTION_OS_PROCESS = "os-process" ;
	public static final String COLLECTION_HOST = "host" ;
	public static final String COLLECTION_JAVA = "java" ;

	public final static String OVERWRITTEN = "set by profile yml file" ;

	final static String DEFAULT_CSAP_CONFIGURATION = System.getProperty( "user.home" ) + "/csap/" ;

	final public static String DEFINITION_FOLDER_NAME = "definition" ;

	final public static String LINE = "\n" ;

	public static String TC_HEAD = LINE + "\n\t Test Start" + LINE ;

	public static String SETUP_HEAD = LINE + "\n\t Test Setup" + LINE ;;

	volatile static boolean is_csap_initialize_run_once = false ;

	static File csapInstallationFolder ;
	static File junitFolder ;

	//
	// - main method invoked when running springboot application
	//
//	public static void run ( Class<?> clazz , String[] args ) {
//
//		var profileActive = System.getProperty( "spring.profiles.active" ) ;
//
//		// triggers default log4j.yml (json mode) to be loaded; post startup - others
//		// may override.
//		logger.info( "system property spring.profiles.active: '{}'", profileActive ) ;
//
//		if ( profileActive == null ) {
//
//			LOG4J2_TEST_YML = "log4j2-desktop.yml" ; // ansi colors, readable
//			initialize( " -Dspring.profiles.active not set - assuming desktop mode" ) ;
//
//		} else {
//
//			add_CSAP_path_profiles( ) ;
//
//		}
//
//		var context = SpringApplication.run( clazz, args ) ;
//
//		logger.debug( "activeProfiles: '{}'", List.of( context.getEnvironment( ).getActiveProfiles( ) ) ) ;
//
//	}

	public static String header ( String message ) {

		return Helpers.LINE + "\n " + message + Helpers.LINE ;

	}

	public static String testHeader ( String message ) {

		return header( arrowMessage( Helpers.pad( "   Test:" ) + message ) ) ;

	}

	public static String highlightHeader ( String message ) {

		return header( arrowMessage( "   " + message ) ) ;

	}

	public static String arrowMessage ( String message ) {

		return "\n*\n**\n***\n****\n*****" + message + "\n****\n***\n**\n*" ;

	}

	public static String testHeader ( ) {

		var fullSourceClass = Thread.currentThread( ).getStackTrace( )[2].getClassName( ) ;

		var fullSourceClassArray = fullSourceClass.split( Pattern.quote( "." ) ) ;

		var content = Helpers.pad( "  Test:" )
				+ fullSourceClassArray[fullSourceClassArray.length - 1] + "."
				+ Thread.currentThread( ).getStackTrace( )[2].getMethodName( ) ;

		return header( arrowMessage( content ) ) ;

	}

	/**
	 * Junit setup - initiallizes log4j, but note every class is including - so a
	 * guard is setup to prevent reloading of log4j
	 * 
	 * @param description
	 */
	public static void initialize ( String description ) {

		// boot dev tools does a double start to inject into the path
		// System.out.println( CSAP.buildFilteredStack( new Exception( "CSAP CAll Path"
		// ), "." ) ) ;

		if ( ! is_csap_initialize_run_once ) {

			is_csap_initialize_run_once = true ;

			// Configure in prps file
			// CSAP.setLogToDebug( ConfigFileApplicationListener.class.getName()
			// );

			var startMessage = new StringBuilder( CSAP_INSTALL_VARIABLE + " not set - docker,desktop,junit mode" ) ;
			startMessage.append( Helpers.padLine( "Working Directory" ) + System.getProperty( "user.dir" ) ) ;
			startMessage.append( Helpers.padLine( "source location" )
					+ Helpers.class.getProtectionDomain( ).getCodeSource( ).getLocation( ).getFile( ) ) ;
			System.out.println( Helpers.header( startMessage.toString( ) ) ) ;

			loadLogConfiguration( LOG4J2_TEST_YML ) ;

			add_CSAP_path_profiles( ) ;

			// File testFolder = new File( "target/junit" );
			// logger.info( "Deleting: {}", testFolder.getAbsolutePath() );
			// FileUtils.deleteQuietly( testFolder );

			// https://logging.apache.org/log4j/2.0/faq.html
			logger.info( description ) ;

		}

	}

	private static void loadLogConfiguration ( String logLocation ) {

		try {

			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext( false ) ;

			URL url = Helpers.class.getClassLoader( ).getResource( logLocation ) ;
			
			System.out.println( "loading: " +  url.toURI( ) ) ;

			context.setConfigLocation( url.toURI( )) ;

			logger.info( "loaded: {}", logLocation ) ;

		} catch ( Exception e ) {

			System.out.println( "Failed to load" + e.getMessage( ) ) ;

		}

	}

//	private static void loadLogConfiguration ( String logLocation ) {
//
//		var bootstrapMessage = new StringBuilder( ) ;
//
//		Resource logConfiguration = new ClassPathResource( logLocation ) ;
//		var testLocation = new File( logLocation ) ;
//
//		if ( testLocation.exists( ) ) {
//
//			logConfiguration = new FileSystemResource( logLocation ) ;
//
//		}
//
//		try {
//
//			var logConfigFile = logConfiguration.getURL( ) ;
//
//			if ( logConfigFile == null ) {
//
//				System.out.println( "ERROR: Failed to find log configuration file in classpath: "
//						+ logConfiguration ) ;
//
//				System.exit( 99 ) ;
//
//			}
//
//			bootstrapMessage.append( Helpers.padNoLine( "log file" ) + logConfiguration ) ;
//			bootstrapMessage.append( Helpers.padLine( "found" ) + logConfigFile.toURI( ).getPath( ) ) ;
//
//			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext( false ) ;
//			// this will force a reconfiguration
//			context.setConfigLocation( logConfigFile.toURI( ) ) ;
//			logger.info( bootstrapMessage.toString( ) ) ;
//
//			// System.out.println( bootstrapMessage.toString( ) + "\n\n" ) ;
//		} catch ( Exception e ) {
//
//			System.out.println( "ERROR: Failed to resolve path: " + logConfiguration
//					+ "\n " + Helpers.buildCsapStack( e ) ) ;
//			System.exit( 99 ) ;
//
//		}
//
//		// Now dump nicely formatted classpath.
//		// sbuf.append( "\n\n ====== JVM Classpath is: \n"
//		// + WordUtils.wrap( System.getProperty( "java.class.path"
//		// ).replaceAll( ";", " " ), 140 ) );
//	}

	public static void add_CSAP_path_profiles ( ) {

		var springboot_additional_profile_path = DEFAULT_CSAP_CONFIGURATION ;

		if ( System.getenv( "CSAP_LOG4J" ) != null ) {

			loadLogConfiguration( System.getenv( "CSAP_LOG4J" ) ) ;

		}

		if ( System.getProperty( "localhostSettingsFolder" ) != null ) {

			springboot_additional_profile_path = System.getProperty( "localhostSettingsFolder" ) ;

		}

		if ( isCsapFolderSet( ) ) {

			springboot_additional_profile_path = System.getenv( CSAP_INSTALL_VARIABLE )
					+ "/" + DEFINITION_FOLDER_NAME + "/" ;

		} else {

			csapInstallationFolder = new File( System.getProperty( "user.dir" ) + "/target/csap-platform" ) ;

			if ( csapInstallationFolder.exists( ) ) {

				junitFolder = new File( System.getProperty( "user.dir" ) + "/target/junit" ) ;

				logger.warn( "'" + CSAP_INSTALL_VARIABLE + "'"
						+ " environment variable not found: running in desktop mode"
						+ Helpers.padLine( "Cleaning up folders from previous runs" )
						+ Helpers.padLog( "csapInstallationFolder" )
						+ Helpers.padLog( "junitFolder" ),
						csapInstallationFolder,
						junitFolder ) ;

				FileUtils.deleteQuietly( csapInstallationFolder ) ;
				FileUtils.deleteQuietly( junitFolder ) ;

			}

		}

		if ( ! springboot_additional_profile_path.endsWith( "/" ) ) {

			logger.error( "spring.config.location '{}' MUST end with a trailing '/' , exiting",
					springboot_additional_profile_path ) ;

			// System.exit( 99 );
		}

		logger.debug( "springboot_additional_profile_path: {}", springboot_additional_profile_path ) ;

		File externalConfiguration = new File( springboot_additional_profile_path ) ;

		if ( ! externalConfiguration.exists( ) ) {

			externalConfiguration.mkdirs( ) ;

		}

		if ( externalConfiguration.isDirectory( ) ) {

			logger.warn( "Updating system property"
					+ Helpers.padLine( "spring.config.location" ) + externalConfiguration.toPath( ).toUri( ) ) ;

			// spring.config.location
			System.setProperty( "spring.config.additional-location", externalConfiguration.toPath( ).toUri( )
					.toString( ) ) ;

		} else {

			logger.warn( Helpers.header( "Location is not a folder: '{}'."
					+ "\n\t no additional application-<profile>.yml file(s) will be loaded" ),
					externalConfiguration.toPath( ).toUri( ) ) ;

		}

	}

	public static boolean isCsapFolderSet ( ) {

		if ( System.getenv( CSAP_INSTALL_VARIABLE ) == null ) {

			return false ;

		}

		return true ;

	}

	public static File getJunitFolder ( ) {

		return junitFolder ;

	}

	static Pattern camelPattern = Pattern.compile( "(?<=[a-z])[A-Z]" ) ;

	public static String camelToSnake ( String camel ) {

		var reallySimpleName = camel.split( Pattern.quote( "$" ), 2 )[0] ;
		reallySimpleName = reallySimpleName.replaceAll( "_", "-" ) ;
		reallySimpleName = reallySimpleName.replaceAll( "/", "-" ) ;
		Matcher camelMatcher = camelPattern.matcher( reallySimpleName ) ;
		String snake = camelMatcher.replaceAll( match -> "-" + match.group( ) ) ;
		return snake.toLowerCase( ) ;

	}

	public static final long ONE_SECOND_MS = 1000 ;
	public static final long ONE_MINUTE_MS = 60 * 1000 ;

	public static final long MB_FROM_BYTES = 1024 * 1024 * 1 ;

	static public TimeUnit parseTimeUnit ( String checkUnit , TimeUnit defaultUnit ) {

		try {

			return TimeUnit.valueOf( checkUnit ) ;

		} catch ( Exception e ) {

			logger.warn( "Unsupported java TimeUnit: {}", e.getMessage( ) ) ;

		}

		return defaultUnit ;

	}

	static public String autoFormatNanos ( long nanos ) {

		return timeUnitPresent( TimeUnit.NANOSECONDS.toMillis( nanos ) ) ;

	}

	static double HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis( 1 ) ;

	static public String autoFormatMillis ( long millis ) {

		var pretty = millis + " " + timeUnitToAbbeviation( TimeUnit.MILLISECONDS ) ;

		if ( millis > HOUR_IN_MILLIS ) {

			pretty = Helpers.roundIt( millis / HOUR_IN_MILLIS, 1 ) + " " + timeUnitToAbbeviation( TimeUnit.HOURS ) ;

		} else if ( millis > 60000 ) {

			pretty = Helpers.roundIt( millis / 60000d, 1 ) + " " + timeUnitToAbbeviation( TimeUnit.MINUTES ) ;

		} else if ( millis > 2000 ) {

			pretty = Helpers.roundIt( millis / 1000d, 1 ) + " " + timeUnitToAbbeviation( TimeUnit.SECONDS ) ;

		}

		return pretty ;

	}

	static public String timeUnitPresent ( long numberOfMillis ) {

		return autoFormatMillis( numberOfMillis ) ;

	}

	static public String timeUnitToAbbeviation ( TimeUnit maxUnit ) {

		var maxDesc = maxUnit.toString( ) ;

		switch ( maxUnit ) {

		case MILLISECONDS:
			maxDesc = "ms" ;
			break ;

		case SECONDS:
			maxDesc = "s" ;
			break ;

		case MINUTES:
			maxDesc = "m" ;
			break ;

		case HOURS:
			maxDesc = "h" ;
			break ;

		default:
			break ;

		}

		return maxDesc ;

	}

	static public String printBytesWithUnits ( long numBytes ) {

		double prettyCount = numBytes ;
		var rounding = 0 ;
		var units = " bytes" ;

		if ( prettyCount > 1024 ) {

			rounding = 2 ;
			prettyCount = prettyCount / 1024 ;
			units = " kb" ;

		}

		if ( prettyCount > 1024 ) {

			rounding = 2 ;
			prettyCount = prettyCount / 1024 ;
			units = " mb" ;

		}

		if ( prettyCount > 1024 ) {

			rounding = 2 ;
			prettyCount = prettyCount / 1024 ;
			units = " gb" ;

		}

		return Helpers.roundIt( prettyCount, rounding ) + units ;

	}

	public static String buildDescription ( String header , Object... items ) {

		StringBuilder desc = new StringBuilder( header ) ;

		boolean doPad = true ;

		for ( var item : items ) {

			var asString = "null" ;

			if ( item != null ) {

				asString = item.toString( ) ;

			}

			if ( doPad ) {

				if ( StringUtils.isEmpty( asString ) ) {

					desc.append( "\n" ) ;

				} else {

					desc.append( Helpers.padLine( asString ) ) ;

				}

			} else {

				desc.append( asString ) ;

			}

			doPad = ! doPad ;

		}

		return desc.toString( ) ;

	}

	public static String buildDescription ( String header , Map<?, ?> items ) {

		StringBuilder desc = new StringBuilder( header ) ;

		for ( var parameter : items.entrySet( ) ) {

			desc.append( Helpers.padLine( parameter.getKey( ).toString( ) ) + parameter.getValue( ).toString( ) ) ;

		}

		return desc.toString( ) ;

	}

	public static String buildDescription ( String header , JsonNode items ) {

		StringBuilder desc = new StringBuilder( header ) ;

		Helpers.asStreamHandleNulls( items ).forEach( name -> {

			desc.append( Helpers.padLine( name ) + items.path( name ).toString( ) ) ;

		} ) ;

		return desc.toString( ) ;

	}

	public static void copyFolder ( Path src , Path dest )
		throws IOException {

		Files.walk( src )
				.forEach( source -> copy( source, dest.resolve( src.relativize( source ) ) ) ) ;

	}

	private static void copy ( Path source , Path dest ) {

		try {

			Files.copy( source, dest, StandardCopyOption.REPLACE_EXISTING ) ;

		} catch ( Exception e ) {

			throw new RuntimeException( e.getMessage( ), e ) ;

		}

	}

	public static <T> Predicate<T> distinctByKey ( Function<? super T, Object> keyExtractor ) {

		Map<Object, Boolean> map = new ConcurrentHashMap<>( ) ;
		return t -> map.putIfAbsent( keyExtractor.apply( t ), Boolean.TRUE ) == null ;

	}

	public static Stream<String> asStreamHandleNulls ( JsonNode jsonTree ) {

		// handle empty lists
		if ( jsonTree == null || ! jsonTree.isObject( ) ) {

			return ( new ArrayList<String>( ) ).stream( ) ;

		}

		return Helpers.asStream( jsonTree.fieldNames( ) ) ;

	}

	public static List<String> jsonList ( JsonNode node ) {

		if ( node == null ) {

			return List.of( ) ;

		}

		return jsonStream( node ).map( JsonNode::asText ).collect( java.util.stream.Collectors.toList( ) ) ;

	}

	public static Stream<JsonNode> jsonStream ( JsonNode node ) {

		return StreamSupport.stream( node.spliterator( ), false ) ;

	}

	public static <T> Stream<T> asStream ( Iterator<T> sourceIterator ) {

		return asStream( sourceIterator, false ) ;

	}

	public static <T> Stream<T> asStream ( Iterator<T> sourceIterator , boolean parallel ) {

		Iterable<T> iterable = ( ) -> sourceIterator ;
		return StreamSupport.stream( iterable.spliterator( ), parallel ) ;

	}

	private static final ObjectMapper _jsonMapper = new ObjectMapper( ) ;
	static {

		// Early registration - used in CsapUser to render oath oid objects
		_jsonMapper.registerModule( new JavaTimeModule( ) ) ;
		_jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false ) ;
		_jsonMapper.configure( SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false ) ;
		_jsonMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false ) ;

	}

	public static String jsonPrint ( JsonNode j ) {

		try {

			return WordUtils.wrap( _jsonMapper.writerWithDefaultPrettyPrinter( ).writeValueAsString( j ), 180, "\n\t\t",
					true ) ;

		} catch ( JsonProcessingException e ) {

			logger.warn( "Failed rendering a json object: {}", Helpers.buildCsapStack( e ) ) ;

		}

		return "FAILED_TO_PARSE" ;

	}

	public static ArrayNode mergeAttributes ( ObjectNode original , ObjectNode update ) {

		ArrayNode results = _jsonMapper.createArrayNode( ) ;

		Helpers.asStreamHandleNulls( update )
				.forEach( attributeName -> {

					var attributeOriginal = original.path( attributeName ) ;
					var attributeUpdate = update.path( attributeName ) ;

					if ( attributeUpdate.isObject( ) && attributeOriginal.isObject( ) ) {

						results.addAll( mergeAttributes( (ObjectNode) attributeOriginal,
								(ObjectNode) attributeUpdate ) ) ;

					} else {

						original.set( attributeName, attributeUpdate ) ;
						results.add( "merged: " + attributeName ) ;

					}

				} ) ;

		return results ;

	}

	public static String jsonPrint ( ObjectMapper jacksonMapper , JsonNode j )
		throws JsonProcessingException {

		return jacksonMapper.writerWithDefaultPrettyPrinter( ).writeValueAsString( j ) ;

	}

	public static JsonNode buildGenericObjectReport ( Object theItem ) {

		JsonNode jsonDetails = null ;

		try {

			if ( theItem instanceof String ) {

				jsonDetails = _jsonMapper.readTree( (String) theItem ) ;

			} else {

				//
				// iterates thru object looking for getters and/or public properties
				//
				jsonDetails = _jsonMapper.convertValue( theItem, ObjectNode.class ) ;
//				jsonDetails = _jsonMapper.valueToTree( theItem ) ;

			}

		} catch ( Exception e ) {

			logger.warn( buildCsapStack( e ) ) ;

		}

		logger.debug( "{}:\n {}", Thread.currentThread( ).getStackTrace( )[2].getMethodName( ), jsonPrint(
				jsonDetails ) ) ;

		return jsonDetails ;

	}

	public static String getRequestSource ( ) {

		String stack = Arrays.asList( Thread.currentThread( ).getStackTrace( ) ).stream( )
				.filter( stackElement -> {

					return ( ! stackElement.getClassName( ).equals( Helpers.class.getName( ) ) )
							&& ( ! stackElement.getClassName( ).startsWith( "java." ) ) ;

				} )
				.map( StackTraceElement::toString )
				.findFirst( )
				.orElse( "Stack not found" ) ;
		return stack ;

	}

	public static String buildCsapStack ( Throwable possibleNestedThrowable ) {

		return buildFilteredStack( possibleNestedThrowable, "csap" ) ;

	}

	public static String buildFilteredStack ( Throwable possibleNestedThrowable , String pattern ) {

		// add the class name and any message passed to constructor
		final StringBuffer result = new StringBuffer( ) ;

		Throwable currentThrowable = possibleNestedThrowable ;

		int nestedCount = 1 ;

		while ( currentThrowable != null ) {

			if ( nestedCount == 1 ) {

				result.append( Helpers.LINE ) ;
				result.append( "\nCSAP Exception, Filter:  " + pattern ) ;

			} else {

				result.append( "\n Nested Count: " ) ;
				result.append( nestedCount ) ;
				result.append( Helpers.LINE ) ;

			}

			result.append( "\n\n Exception: " + currentThrowable
					.getClass( )
					.getName( ) ) ;
			result.append( "\n Message: " + currentThrowable.getMessage( ) ) ;
			result.append( "\n\n StackTrace: \n" ) ;

			// add each element of the stack trace
			List<StackTraceElement> traceElements = Arrays.asList( currentThrowable.getStackTrace( ) ) ;

			Iterator<StackTraceElement> traceIt = traceElements.iterator( ) ;

			while ( traceIt.hasNext( ) ) {

				StackTraceElement element = traceIt.next( ) ;
				String stackDesc = element.toString( ) ;

				if ( pattern == null || stackDesc.contains( pattern ) ) {

					result.append( stackDesc ) ;
					result.append( "\n" ) ;

				}

			}

			result.append( Helpers.LINE ) ;
			currentThrowable = currentThrowable.getCause( ) ;
			nestedCount++ ;

		}

		return result.toString( ) ;

	}

	public static <T> Predicate<T> not ( Predicate<T> t ) {

		return t.negate( ) ;

	}

	// public static double roundIt ( double toBeTruncated, int precision ) {
	// return BigDecimal.valueOf( toBeTruncated )
	// .setScale( precision, RoundingMode.HALF_UP )
	// .doubleValue() ;
	// }

	public static double roundIt ( double toBeTruncated , int precision ) {

		double result = 0 ;

		try {

			result = BigDecimal.valueOf( toBeTruncated )
					.setScale( precision, RoundingMode.HALF_UP )
					.doubleValue( ) ;

		} catch ( Exception e ) {

			logger.debug( "Failed to convert: {}, {} {}", toBeTruncated, precision, e ) ;

		}

		// String val = Double.toString( result ) ;
		// if ( precision == 0) {
		// return val.substring( 0,val.length()-2 ) ;
		// }
		// return val;
		return result ;

	}

	public static String alphaNumericOnly ( String input ) {

		return input.replaceAll( "[^A-Za-z0-9]", "_" ) ;

	}

	public static String note ( String message ) {

		return Helpers.header( message ) ;

	}

	static public String padNoLine ( String content ) {

		return "    " + StringUtils.rightPad( content + ":", 30 ) + "  " ;

	}

	static public String padLine ( String content ) {

		return "\n    " + StringUtils.rightPad( content + ":", 30 ) + "  " ;

	}

	static public String padLog ( String content ) {

		return "\n    " + StringUtils.rightPad( content + ":", 30 ) + "  {}" ;

	}

	public static String pad ( Object input ) {

		return StringUtils.rightPad( input.toString( ), 25 ) ;

	}

	public static String lpad ( String input ) {

		return StringUtils.leftPad( input, 15 ) ;

	}

	public static void logLevel ( String className , Level l ) {

		Configurator.setAllLevels( className, l ) ;

	}

	public static void setLogToDebug ( String className ) {

		Configurator.setAllLevels( className, Level.DEBUG ) ;

	}

	public static void setLogToInfo ( String className ) {

		Configurator.setAllLevels( className, Level.INFO ) ;

	}

	public static String stripHtmlTags ( String input ) {

		return input.replaceAll( "\\<[^>]*>", "" ) ;

	}

	public static class Collectors {
		public static ArrayNodeCollector toArrayNode ( ) {

			return new ArrayNodeCollector( ) ;

		}

		static public class ArrayNodeCollector implements Collector<JsonNode, ArrayNode, ArrayNode> {

			@Override
			public Supplier<ArrayNode> supplier ( ) {

				// This provides a Function which creates
				// a new instance of the accumulation type.
				// In my case, it has to return a method which
				// creates an ArrayNode.
				return _jsonMapper::createArrayNode ;

			}

			@Override
			public BiConsumer<ArrayNode, JsonNode> accumulator ( ) {

				// This is pretty simple, it dictates how you
				// wish to accumulate values. Also returns a
				// Function.
				return ArrayNode::add ;

			}

			@Override
			public BinaryOperator<ArrayNode> combiner ( ) {

				// This guy is slightly more complicated; because
				// Streams can run in parallel, they obviously have
				// to be merged at some point - this is where that
				// merge takes place.
				return ( x , y ) -> {

					x.addAll( y ) ;
					return x ;

				} ;

			}

			@Override
			public Function<ArrayNode, ArrayNode> finisher ( ) {

				// As mentioned, you can convert your accumulation type
				// to your final return type - that is what this Function
				// does.
				return accumulator -> accumulator ;

			}

			@Override
			public Set<Characteristics> characteristics ( ) {

				// This method returns a list of characteristics associated
				// with the Collector. For example, a thread-safe Collector
				// may return Characteristics.CONCURRENT.
				return EnumSet.of( Characteristics.UNORDERED ) ;

			}

		}
	}
}
