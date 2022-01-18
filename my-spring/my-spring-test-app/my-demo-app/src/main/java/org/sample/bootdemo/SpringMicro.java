package org.sample.bootdemo ;

import java.text.DecimalFormat ;
import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;
import java.util.List ;
import java.util.concurrent.TimeUnit ;
import java.util.stream.Collectors ;

import javax.servlet.http.HttpServletRequest ;

import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.web.bind.annotation.CrossOrigin ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ObjectNode ;

import io.micrometer.core.instrument.Meter ;
import io.micrometer.core.instrument.MeterRegistry ;
import io.micrometer.core.instrument.Tag ;
import io.micrometer.core.instrument.Timer ;
import io.micrometer.core.instrument.distribution.CountAtBucket ;
import io.micrometer.core.instrument.distribution.HistogramSnapshot ;
import io.micrometer.core.instrument.distribution.ValueAtPercentile ;

@RestController
@RequestMapping( "/spring-micro")
public class SpringMicro {

	@Autowired
	ObjectMapper jacksonMapper ;

	@Autowired
	MeterRegistry microMeterRegistry ;

	@CrossOrigin
	@GetMapping ("/metrics/micrometers" )
	public ObjectNode metricsMicrometer (
											@RequestParam ( defaultValue = "none" ) String filter ,
											HttpServletRequest request ) {

		ObjectNode collection = jacksonMapper.createObjectNode( ) ;
		ObjectNode source = collection.putObject( "source" ) ;
		source.put( "host", "theHost" ) ;

		if ( request != null ) {

			source.put( "url", request.getRequestURL( ).toString( ) ) ;

		}

		source.put( "collected", LocalDateTime.now( ).format( DateTimeFormatter.ofPattern(
				"HH:mm:ss, MMMM d uuuu" ) ) ) ;

		microMeterRegistry
				.getMeters( )
				.stream( )
				.forEach( meter -> {

					String aggregateId = buildMicroMeterId( meter ) ;

					boolean addData = true ;

					if ( filter != null ) {

						if ( ! filter.equals( "none" ) ) {

							addData = aggregateId.contains( filter ) ;

						}

					}

					if ( addData ) {

						addMicroMeter( collection, meter, aggregateId ) ;

					}

				} ) ;

		return collection ;

	}

	DecimalFormat format2Decimals = new DecimalFormat( "#.###" ) ;

	private void addMicroMeter ( ObjectNode collection , Meter meter , String aggregateId ) {

		ObjectNode measurements = collection.putObject( aggregateId ) ;

		Timer meterTimer = microMeterRegistry.find( meter.getId( ).getName( ) ).tags( meter.getId( ).getTags( ) )
				.timer( ) ;

		if ( meterTimer != null ) {

			measurements.put( "count", meterTimer.count( ) ) ;
			measurements.put( "mean-in-ms", format2Decimals.format( meterTimer.mean( TimeUnit.MILLISECONDS ) ) ) ;
			measurements.put( "max-in-ms", format2Decimals.format( meterTimer.max( TimeUnit.MILLISECONDS ) ) ) ;
			measurements.put( "total-in-ms", format2Decimals.format( meterTimer.totalTime( TimeUnit.MILLISECONDS ) ) ) ;

			HistogramSnapshot snapShot = meterTimer.takeSnapshot( ) ;
			measurements.put( "snap-count", snapShot.count( ) ) ;
			measurements.put( "snap-total-time", snapShot.total( ) ) ;
			measurements.put( "snap-mean", format2Decimals.format( snapShot.mean( ) ) ) ;
			measurements.put( "snap-max", format2Decimals.format( snapShot.max( ) ) ) ;

			for ( ValueAtPercentile valueAtPercentile : snapShot.percentileValues( ) ) {

				measurements.put( "snap-percentile-in-ms-" + valueAtPercentile.percentile( ),
						format2Decimals.format( valueAtPercentile.value( TimeUnit.MILLISECONDS ) ) ) ;
				measurements.put( "snap-percentile-in-ms-" + valueAtPercentile.percentile( ),
						format2Decimals.format( valueAtPercentile.value( TimeUnit.MILLISECONDS ) ) ) ;

			}

			CountAtBucket[] bucketCounts = snapShot.histogramCounts( ) ;

			if ( bucketCounts.length > 0 ) {
				// for ( CountAtBucket countAtBucket : bucketCounts ) {
				// // measurements.put( "count-in-bucket-" + countAtBucket.bucket() ,
				// countAtBucket.count()) ;
				// }

				measurements.put( "snap-bucket-count", bucketCounts[bucketCounts.length - 1].count( ) ) ;

			}

		} else {

			meter.measure( ).forEach( measurement -> {

				// ObjectNode collectedItem = measurements.addObject() ;
				measurements.put( measurement.getStatistic( ).name( ), measurement.getValue( ) ) ;

				// measurement.get
			} ) ;

		}

	}

	// SimpleDateFormat timeDayFormat = new SimpleDateFormat( "HH:mm:ss , MMM d" ) ;

	static public String buildMicroMeterId ( Meter meter ) {

		String id = meter.getId( ).getName( ) ;
		List<Tag> tags = meter.getId( ).getTags( ) ;

		if ( ! tags.isEmpty( ) ) {

			id += "[" +
					tags.stream( )
							.filter( tag -> {

								// exception=None,method=GET,outcome=SUCCESS,status=200,uri=/**/*.css
								if ( tag.getKey( ).equals( "exception" ) && tag.getValue( ).equals( "None" ) )
									return false ;
								if ( tag.getKey( ).equals( "method" ) && tag.getValue( ).equals( "GET" ) )
									return false ;
								if ( tag.getKey( ).equals( "outcome" ) && tag.getValue( ).equals( "SUCCESS" ) )
									return false ;
								if ( tag.getKey( ).equals( "outcome" ) && tag.getValue( ).equals( "REDIRECTION" ) )
									return false ;
								if ( tag.getKey( ).equals( "status" ) && tag.getValue( ).equals( "200" ) )
									return false ;
								return true ;

							} )
							.map( tag -> {

								if ( tag.getKey( ).equals( "uri" ) || tag.getKey( ).equals( "name" ) )
									return tag.getValue( ) ;
								return tag.getKey( ) + "=" + tag.getValue( ) ;

							} )
							.collect( Collectors.joining( "," ) )
					+ "]" ;

		}

		return id ;

	}

}
