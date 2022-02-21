package org.sample.springdata.http ;

import static org.assertj.core.api.Assertions.assertThat ;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get ;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content ;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status ;

import java.util.Arrays ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

import org.hibernate.engine.internal.StatisticalLoggingSessionEventListener ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.sample.springdata.api.EmployeeRestApis ;
import org.sample.springdata.db.EmployeeRepository ;
import org.sample.springdata.utils.EmpHelpers ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.context.ApplicationContext ;
import org.springframework.http.MediaType ;
import org.springframework.test.web.servlet.MockMvc ;
import org.springframework.test.web.servlet.ResultActions ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )

@SpringBootTest

@AutoConfigureMockMvc

public class HttpEndToEndTests {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	final static int TEST_RECORD_COUNT = 1000 ;

	@Autowired
	ObjectMapper jsonMapper ;

	@Autowired
	ApplicationContext applicationContext ;

	@Autowired
	EmployeeRestApis employeeRestController ;

	@Autowired
	EmployeeRepository employeeRepository ;

	@BeforeAll
	void beforeAll ( ) {

		logger.info( Utils.testHeader( "loading test data" ) ) ;

		Utils.setLogToInfo( StatisticalLoggingSessionEventListener.class.getName( ) ) ;

		var testEmployees = IntStream.rangeClosed( 1, TEST_RECORD_COUNT )
				.mapToObj( EmpHelpers::buildRandomizeEmployee )
				.collect( Collectors.toList( ) ) ;

		employeeRepository.saveAll( testEmployees ) ;

	}

	@Test
	void contextLoads ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var beanListing = Arrays.stream( applicationContext.getBeanDefinitionNames( ) )
				.map( name -> {

					return Utils.lpad( name ) ;

				} )
				.collect( Collectors.joining( "\n\t" ) ) ;

		// logger.info( "beans: {} \n {}", applicationContext.getBeanDefinitionCount( ),
		// beanListing ) ;

		assertThat( applicationContext.getBeanDefinitionCount( ) )
				.as( "limited beans loaded for @DataJpaTest" )
				.isGreaterThan( 300 ) ;

	}

	@Test
	void verify_first_page_of_birthdays ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employeeReport = employeeRestController.getBirthMonthEmployeesPageAble( 10, 0 ) ;

		logger.info( Utils.buildDescription(
				"employees: ",
				"number", employeeReport.path( "employees" ).size( ),
				"first", Utils.jsonPrint( employeeReport.path( "employees" ) ) //
		) ) ;

		assertThat( employeeReport.path( "employees" ).size( ) )
				.isGreaterThan( 0 ) ;

	}

	@Test
	public void verify_max_page_size ( @Autowired MockMvc mockMvc )
		throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		// mock does much validation.....
		var resultActions = mockMvc.perform(
				get( EmployeeRestApis.BIRTHDAY_EMPLOYEES_PAGEABLE )

						.param( "pageSize", EmployeeRestApis.MAX_PAGE_SIZE + "0" )
						.param( "pageNumber", "0" ) //
		) ;

		//
		var errorReport = resultActions
				.andExpect( status( ).is4xxClientError( ) )
				.andReturn( ).getResponse( ).getContentAsString( ) ;

		logger.info( "errorReport: {}", errorReport ) ;

		var pageReport = jsonMapper.readTree( errorReport ) ;

		assertThat( pageReport.path( "error" ).asBoolean( ) )
				.isTrue( ) ;

		assertThat( pageReport.path( "reason" ).asText( ) )
				.startsWith( "getBirthMonthEmployeesPageAble.pageSize: must be less than or equal to" ) ;

//		assertThat( errorReport )
//				.contains( "peter" ) ;

	}

	@Test
	public void http_get_birthday_report ( @Autowired MockMvc mockMvc )
		throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		// mock does much validation.....
		var resultActions = mockMvc.perform(
				get( EmployeeRestApis.BIRTHDAY_EMPLOYEES_PAGEABLE )

						.param( "pageSize", "12" )
						.param( "pageNumber", "0" )
						.accept( MediaType.APPLICATION_JSON_VALUE ) ) ;

		//
		String result = resultActions
				.andExpect( status( ).isOk( ) )
				.andExpect( content( ).contentTypeCompatibleWith( MediaType.APPLICATION_JSON_VALUE ) )
				.andReturn( ).getResponse( ).getContentAsString( ) ;

		var birthDayReport = jsonMapper.readTree( result ) ;

		logger.info( "view report:\n {} \n, first Employee: {}",
				Utils.jsonPrint( birthDayReport.path( "view" ) ),
				Utils.jsonPrint( birthDayReport.path( "employees" ).path( 0 ) ) ) ;

		assertThat( birthDayReport.path( "view" ).path( "currentCount" ).asInt( ) )
				.isGreaterThan( 0 ) ;

	}

	@Test
	public void http_get_employee_portal ( @Autowired MockMvc mockMvc )
		throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		// mock does much validation.....
		ResultActions resultActions = mockMvc.perform(
				get( "/" )
						.accept( MediaType.TEXT_HTML_VALUE ) ) ;

		//
		var employeeHtmlReport = resultActions
				.andExpect( status( ).isOk( ) )
				.andExpect( content( ).contentTypeCompatibleWith( MediaType.TEXT_HTML_VALUE ) )
				.andReturn( ).getResponse( ).getContentAsString( ) ;

		logger.debug( "result:\n {}", employeeHtmlReport ) ;

		assertThat( employeeHtmlReport )
				.contains( "Null Pointer Exception Advice" ) ;

	}

	@Test
	public void verify_get_date ( @Autowired MockMvc mockMvc ) throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		var urlPath = "/date" ;
		var resultActions = mockMvc.perform( get( urlPath )
				.contentType( "application/json" ) )
				.andExpect( status( ).isOk( ) ) ;

		var content = resultActions.andReturn( ).getResponse( ).getContentAsString( ) ;

		logger.info( Utils.buildDescription( "date report",
				"urlPath", urlPath,
				"content", content ) ) ;

	}

}
