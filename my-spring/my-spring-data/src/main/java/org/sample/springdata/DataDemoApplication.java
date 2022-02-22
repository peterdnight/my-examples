package org.sample.springdata ;

import javax.annotation.PostConstruct ;

import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry ;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer ;

import com.zaxxer.hikari.HikariDataSource ;

@SpringBootApplication
public class DataDemoApplication implements WebMvcConfigurer {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	public static void main ( String[] args ) {

		SpringApplication.run( DataDemoApplication.class, args ) ;

	}

	//
	// Autoconfigured
	//
	@Autowired ( required = false )
	HikariDataSource dataSource ;

	@PostConstruct
	public void showSpringBootAutoConfigurations ( ) {

		if ( dataSource == null ) {

			logger.info( "DataSource not configured" ) ;

		} else {

			logger.info( Utils.highlightHeader( "Datasource: spring.datasource.hikari.*" ) ) ;

			logger.info( Utils.buildDescription( "DB Settings: ",
					"url", dataSource.getJdbcUrl( ),
					"user", dataSource.getUsername( ),
					"cred", dataSource.getPassword( ),
					"class", dataSource.getDriverClassName( ),
					"timeout - connect", Utils.autoFormatMillis( dataSource.getConnectionTimeout( ) ),
					"timeout - idle", Utils.autoFormatMillis( dataSource.getIdleTimeout( ) ) ) ) ;

		}

	}

	@Override
	public void addResourceHandlers ( ResourceHandlerRegistry registry ) {

		// add common cache policies
		Utils.addResourceHandlers( registry ) ;

	}

}
