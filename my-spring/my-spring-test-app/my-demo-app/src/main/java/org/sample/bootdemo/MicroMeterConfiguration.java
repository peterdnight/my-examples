package org.sample.bootdemo;

import java.time.Duration ;

import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;

import io.micrometer.core.instrument.Meter ;
import io.micrometer.core.instrument.config.MeterFilter ;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig ;


@Configuration
public class MicroMeterConfiguration {
	
	
//	@Bean
//	public MeterFilter addCsapNameTag () {
//
//		return MeterFilter.commonTags( Tags.of( "csapName", csapInformation.getName() ) ) ;
//		// return MeterFilter.renameTag("com.example", "mytag.region", "mytag.area");
//
//	}
	
	private static final Duration	HISTOGRAM_EXPIRY	= Duration.ofSeconds( 30 ) ;
	private static final Duration	STEP				= Duration.ofSeconds( 5 ) ;

	@Bean
	public MeterFilter addCsapHistogramTag () {

		return new MeterFilter() {
			@Override
		    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {

		            return DistributionStatisticConfig.builder()
		                    .percentiles( 0.5, 0.95 )
		                    .percentilesHistogram( true )
		                    //.percentilePrecision( 3 )
		                    //.bufferLength()
		                    .expiry( HISTOGRAM_EXPIRY )
		                    .build()
		                    .merge(config);

		    }
//			public DistributionStatisticConfig configure (	Meter.Id id,
//															DistributionStatisticConfig config ) {
//				
//				return config.merge( DistributionStatisticConfig.builder()
//					.percentilesHistogram( true )
//					.percentiles( 0.95 ) // (5)
////					.expiry( HISTOGRAM_EXPIRY ) // (6)
////					.bufferLength( (int) (HISTOGRAM_EXPIRY.toMillis() / STEP.toMillis()) ) // (7)
//					.build() ) ;
//			}

		} ;
	}

	// @Bean
	// MeterRegistryCustomizer<MeterRegistry> customizer () {
	// return ( registry ) -> registry.config().commonTags( "application", "petr" ) ;
	// }
	//
	// @Bean
	// public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags () { // (2)
	// return registry -> registry.config()
	// .commonTags( "host", csapInformation.getHostName(), "service", csapInformation.getName() ) // (3)
	// .meterFilter( MeterFilter.deny( id -> { // (4)
	// String uri = id.getTag( "uri" ) ;
	// return uri != null && uri.startsWith( "/swagger" ) ;
	// } ) )
	// .meterFilter( new MeterFilter() {
	// @Override
	// public DistributionStatisticConfig configure ( Meter.Id id,
	// DistributionStatisticConfig config ) {
	// return config.merge( DistributionStatisticConfig.builder()
	// .percentilesHistogram( true )
	// .percentiles( 0.5, 0.75, 0.95 ) // (5)
	// .expiry( HISTOGRAM_EXPIRY ) // (6)
	// .bufferLength( (int) (HISTOGRAM_EXPIRY.toMillis() / STEP.toMillis()) ) // (7)
	// .build() ) ;
	// }
	// } ) ;
	// }

}
