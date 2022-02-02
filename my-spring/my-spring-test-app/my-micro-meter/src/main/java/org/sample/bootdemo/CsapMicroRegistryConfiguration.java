package org.sample.bootdemo;

import java.time.Duration ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.Optional ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.context.annotation.Configuration ;

import io.micrometer.core.instrument.Meter ;
import io.micrometer.core.instrument.Tag ;
import io.micrometer.core.instrument.config.MeterFilter ;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig ;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry ;


@Configuration
public class CsapMicroRegistryConfiguration {
	

	final Logger logger = LoggerFactory.getLogger( getClass( ) ) ;
	
	
	public CsapMicroRegistryConfiguration() {
		
		
		logger.warn( Helpers.testHeader( ) );
		
	}
	

	public static final String CSAP_COLLECTION_TAG = "csap-collection" ;
	
	public static final double MEAN_PERCENTILE = 0.5 ;

	private static final Duration STEP = Duration.ofSeconds( 30 ) ;
	
	private static final Duration HISTOGRAM_EXPIRY = Duration.ofSeconds( 60 ) ;
	
	
	private static final List<String> NEVER_COLLECTED_PREFIXES = List.of(
			"process.start.time", 
			"never." ) ;

	private static final List<String> NEVER_COLLECTED_SUFFIXES = List.of(
			".percentile" ) ;

	private static final List<String> NEVER_COLLECTED_URIS = List.of(
			"/swagger" ) ;

	private static final List<String> PRUNE_TAGS_FOR_NAME_PREFIXES = List.of(
			"csap." ) ;

	private static final List<String> ALWAYS_COLLECTED_PREFIXES = List.of(
			// "http.server.requests", // lots of overhead: use csap instead on targeted
			// urls
			"csap.",
			"cache.",
			"jvm.",
			"process.",
			"system.",
			"tomcat.",
			"log4j2.events" ) ;

	public static final List<String> AGGREGATE_CATEGORIES = List.of(
			"http.server.requests",
			"cache.",
			"jvm.memory.used",
			"jvm.memory.max",
			"jvm.memory.committed",
			"tomcat.",
			"log4j2.events" ) ;
	
	public SimpleMeterRegistry buildCsapRegistry ( ) {

		logger.debug( Helpers.header( "Building custom registry" ) ) ;

		var simpleRegistry = new SimpleMeterRegistry( ) ;

		simpleRegistry.config( )

				// .commonTags( "host", hostId, "service", serviceId )

				// remove unwatched uris
				.meterFilter( MeterFilter.deny( id -> {

					Optional<String> neverCollect = NEVER_COLLECTED_URIS.stream( )
							.filter( alwaysPrefix -> id.getName( ).startsWith( alwaysPrefix ) )
							.findFirst( ) ;

					return neverCollect.isPresent( ) ;

				} ) )

				// remove name prefixes
				.meterFilter( MeterFilter.deny( id -> {

					Optional<String> neverCollect = NEVER_COLLECTED_PREFIXES.stream( )
							.filter( neverPrefix -> id.getName( ).startsWith( neverPrefix ) )
							.findFirst( ) ;

					return neverCollect.isPresent( ) ;

				} ) )

				// remove name suffixes
				.meterFilter( MeterFilter.deny( id -> {

					Optional<String> neverCollect = NEVER_COLLECTED_SUFFIXES.stream( )
							.filter( neverPrefix -> id.getName( ).endsWith( neverPrefix ) )
							.findFirst( ) ;

					return neverCollect.isPresent( ) ;

				} ) )

				// remove tags for some names for readability & conciseness
				.meterFilter( new MeterFilter( ) {
					@Override
					public Meter.Id map ( Meter.Id id ) {

						Optional<String> pruneTags = PRUNE_TAGS_FOR_NAME_PREFIXES.stream( )
								.filter( prunePrefix -> id.getName( ).startsWith( prunePrefix ) )
								.findFirst( ) ;

						if ( pruneTags.isPresent( ) ) {

							List<Tag> tags = new ArrayList<>( ) ;
							return id.replaceTags( tags ) ;

						}

						return id ;

					}

				} )

				// add csap collection tags used to limit meters reported
				.meterFilter( new MeterFilter( ) {
					@Override
					public Meter.Id map ( Meter.Id id ) {

						Optional<String> alwaysCollect = ALWAYS_COLLECTED_PREFIXES.stream( )
								.filter( alwaysPrefix -> id.getName( ).startsWith( alwaysPrefix ) )
								.findFirst( ) ;

						if ( alwaysCollect.isPresent( ) ) {

							if ( id.getName( ).startsWith( "jvm.threads.daemon" ) ) {

								logger.debug( "{} adding tag {}", id.getName( ), CSAP_COLLECTION_TAG ) ;

							}

							return id.withTag( Tag.of( CSAP_COLLECTION_TAG, "true" ) ) ;

						}

						return id ;

					}

				} )

				// add 50% and 95% intervals, and max value support
				.meterFilter( new MeterFilter( ) {
					@Override
					public DistributionStatisticConfig configure ( Meter.Id id , DistributionStatisticConfig config ) {

						return DistributionStatisticConfig.builder( )
								// .percentilesHistogram( true )
								.percentiles( MEAN_PERCENTILE, 0.95 )
								.expiry( HISTOGRAM_EXPIRY )
								.bufferLength( (int) ( HISTOGRAM_EXPIRY.toMillis( ) / STEP.toMillis( ) ) )
								.build( )
								.merge( config ) ;

					}

				} ) ;

		return simpleRegistry ;

	}

}
