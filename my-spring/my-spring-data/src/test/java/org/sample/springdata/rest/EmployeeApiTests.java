package org.sample.springdata.rest ;

import static org.assertj.core.api.Assertions.assertThat ;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get ;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status ;

import java.time.LocalDate ;
import java.util.Arrays ;
import java.util.List ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

import org.hibernate.engine.internal.StatisticalLoggingSessionEventListener ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.mockito.Mockito ;
import org.sample.springdata.api.EmployeeRestApis ;
import org.sample.springdata.db.Employee ;
import org.sample.springdata.db.EmployeeRepository ;
import org.sample.springdata.utils.EmpHelpers ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest ;
import org.springframework.boot.test.mock.mockito.MockBean ;
import org.springframework.context.ApplicationContext ;
import org.springframework.data.domain.Page ;
import org.springframework.data.domain.PageImpl ;
import org.springframework.data.domain.PageRequest ;
import org.springframework.data.domain.Sort ;
import org.springframework.test.web.servlet.MockMvc ;

import com.fasterxml.jackson.databind.ObjectMapper ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )

@WebMvcTest ( controllers = EmployeeRestApis.class )

public class EmployeeApiTests {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	@MockBean
	EmployeeRepository employeeRepository ;
	

	@Autowired
	ObjectMapper jsonMapper ;

	static int FIND_PASS_COUNT = EmployeeRestApis.MAX_PAGE_SIZE - 10 ;
	static int FIND_PASS_MONTH  = LocalDate.now( ).getMonthValue( ) ;
	List<Employee> testEmployees ;

	@BeforeAll
	void beforeAll ( ) {

		logger.info( Utils.testHeader( "build mock objects" ) ) ;

		testEmployees = IntStream.rangeClosed( 1, 10 )
				.mapToObj( EmpHelpers::buildRandomizeEmployee )
				.collect( Collectors.toList( ) ) ;

		var pageRequest = PageRequest.of( 0, FIND_PASS_COUNT, Sort.by( "name" ) ) ;

		var pageEmployees = new PageImpl<Employee>( testEmployees ) ;

		// pageEmployees.

		Mockito.when(
				employeeRepository

						.findAllByBirthMonth(
								FIND_PASS_MONTH,
								pageRequest ) )

				.thenReturn( (Page<Employee>) pageEmployees ) ;

	}

	@Autowired
	ApplicationContext applicationContext ;

	@Test
	void contextLoads ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var beanListing = Arrays.stream( applicationContext.getBeanDefinitionNames( ) )
				.map( name -> {

					return Utils.lpad( name ) ;

				} )
				.collect( Collectors.joining( "\n\t" ) ) ;

		logger.info( "beans: {} \n {}", applicationContext.getBeanDefinitionCount( ) ) ;

		assertThat( applicationContext.getBeanDefinitionCount( ) )
				.as( "limited beans loaded for @WebMvcTest" )
				.isLessThan( 130 ) ;

	}

	@Test
	public void verify_get_paginated_birthdays ( @Autowired MockMvc mockMvc ) throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		var urlPath = EmployeeRestApis.BIRTHDAY_EMPLOYEES_PAGEABLE ;
		var resultActions = mockMvc.perform( get( urlPath )

				.param( "pageSize", Integer.toString( FIND_PASS_COUNT ) )
				.param( "pageNumber", "0" ) //

				.contentType( "application/json" ) )
				.andExpect( status( ).isOk( ) ) ;

		var content = resultActions.andReturn( ).getResponse( ).getContentAsString( ) ;

		logger.info( Utils.buildDescription( "birthday report",
				"urlPath", urlPath,
				"content", content ) ) ;
		
		var pageReport = jsonMapper.readTree( content ) ;
		

		assertThat( pageReport.path( "employees" ).size( ) )
				.isEqualTo( testEmployees.size( ) ) ;

	}

	@Test
	public void verify_validation_error_handling ( @Autowired MockMvc mockMvc ) throws Exception {

		logger.info( Utils.testHeader( ) ) ;

		var urlPath = EmployeeRestApis.BIRTHDAY_EMPLOYEES_PAGEABLE ;
		var resultActions = mockMvc.perform( get( urlPath )

				.param( "pageSize", Integer.toString( EmployeeRestApis.MAX_PAGE_SIZE + 10 ) )
				.param( "pageNumber", "0" ) //

				.contentType( "application/json" ) )
				.andExpect( status( ).is4xxClientError( )) ;

		var content = resultActions.andReturn( ).getResponse( ).getContentAsString( ) ;

		logger.info( Utils.buildDescription( "birthday report",
				"urlPath", urlPath,
				"content", content ) ) ;
		
		var pageReport = jsonMapper.readTree( content ) ;
		

		

		assertThat( pageReport.path( "error" ).asBoolean( ))
				.isTrue( ) ;


		assertThat( pageReport.path( "reason" ).asText( ))
				.startsWith( "getBirthMonthEmployeesPageAble.pageSize: must be less than or equal to" );

		assertThat( pageReport.path( "employees" ).size( ) )
				.isEqualTo( 0 ) ;

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
