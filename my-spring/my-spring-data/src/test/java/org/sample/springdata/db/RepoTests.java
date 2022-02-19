package org.sample.springdata.db ;

import static org.assertj.core.api.Assertions.assertThat ;

import java.time.LocalDate ;
import java.time.Period ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.Disabled ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.sample.springdata.utils.EmpHelpers ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.boot.autoconfigure.domain.EntityScan ;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest ;
import org.springframework.context.ApplicationContext ;
import org.springframework.data.domain.PageRequest ;
import org.springframework.data.domain.Sort ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )

@DataJpaTest ( showSql = false )
final class RepoTests {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	static final int TEST_RERCORD_COUNT = 1000 ;

	@Autowired
	ApplicationContext applicationContext ;

	@Autowired
	EmployeeRepository employeeRepository ;

	@BeforeAll
	void beforeAll ( )
		throws Exception {

		logger.info( Utils.testHeader( "loading test data" ) ) ;

		IntStream.rangeClosed( 1, TEST_RERCORD_COUNT )
				.forEach( uniqueId -> {

					employeeRepository.save(
							EmpHelpers.buildRandomizeEmployee( uniqueId ) ) ;

				} ) ;

	}

	//
	// Disable scanning based on classpath
	//
	@SpringBootApplication
	@EntityScan ( basePackageClasses = EmployeeRepository.class )
	static class Simple_Application {

	}

	@Test
	void contextLoads ( ) {

		logger.info( Utils.testHeader( ) ) ;

		logger.info( "beans: {}", applicationContext.getBeanDefinitionCount( ) ) ;

		assertThat( applicationContext.getBeanDefinitionCount( ) )
				.as( "limited beans loaded for @DataJpaTest" )
				.isLessThan( 100 ) ;

	}

	@Test
	void find_employees_with_birthdays_in_current_month ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var testMonth = EmpHelpers.TEST_MONTH ;

		var employees = employeeRepository.findByBirthMonth( testMonth ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"born in month " + testMonth, employees.size( ),
				"first", employees.get( 0 ),
				"last", employees.get( employees.size( ) - 1 ) ) ) ;

		assertThat( employees.size( ) ).isGreaterThan( 0 ) ;

	}

	@Test
	void find_employees_with_birthdays_in_current_month_paginated ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var testMonth = EmpHelpers.TEST_MONTH ;

		var pageAndSortSelector = PageRequest.of( 0, 10, Sort.by( "name" ) ) ;
		var employeesPageable = employeeRepository.findAll( pageAndSortSelector ) ;

		employeesPageable = employeeRepository.findByBirthMonthPageable( testMonth, pageAndSortSelector ) ;

		logger.info( Utils.buildDescription(
				"employees: ",
				"pageAndSortSelector", pageAndSortSelector,
				"page - current", employeesPageable.getNumber( ),
				"count - current", employeesPageable.getNumberOfElements( ),
				"page - total", employeesPageable.getTotalPages( ),
				"count - total", employeesPageable.getTotalElements( ),
				"first", employeesPageable.stream( ).findFirst( ) //

		) ) ;

		assertThat( employeesPageable.getTotalElements( ) )
				.isGreaterThan( ( TEST_RERCORD_COUNT / ( EmpHelpers.MONTH_MODULA_SELECTOR + 1 ) ) - 10 ) ;

	}

	@Test
	void count_all_employees ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var numberOfEmployees = employeeRepository.count( ) ;

		logger.info( Utils.buildDescription(
				"generated select count: ",
				"numberOfEmployees", numberOfEmployees ) ) ;

		assertThat( numberOfEmployees ).isGreaterThanOrEqualTo( TEST_RERCORD_COUNT ) ;

	}

	@Test
	void list_all_employees ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = employeeRepository.findAll( ) ;

		logger.info( Utils.buildDescription(
				"employees: ",
				"total", employees.size( ),
				"first", employees.get( 0 ),
				"last", employees.get( employees.size( ) - 1 ) ) ) ;

		assertThat( employees.size( ) ).isGreaterThan( TEST_RERCORD_COUNT / 4 ) ;

	}

	@Test
	void list_all_employees_paginated ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var pageAndSortSelector = PageRequest.of( 0, 10, Sort.by( "name" ) ) ;
		var employeesPageable = employeeRepository.findAll( pageAndSortSelector ) ;

		logger.info( Utils.buildDescription(
				"employees: ",
				"pageAndSortSelector", pageAndSortSelector,
				"page - current", employeesPageable.getNumber( ),
				"count - current", employeesPageable.getNumberOfElements( ),
				"page - total", employeesPageable.getTotalPages( ),
				"count - total", employeesPageable.getTotalElements( ),
				"first", employeesPageable.stream( ).findFirst( ) //

		) ) ;

		assertThat( employeesPageable.getTotalPages( ) )
				.isGreaterThan( ( TEST_RERCORD_COUNT / 10 ) - 10 ) ;

	}

	@Test
	void find_employees_by_birthday ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employee = EmpHelpers.buildRandomizeEmployee( 19 ) ;

		employeeRepository.save( employee ) ;

		logger.info( "added: {}", employee.toString( ) ) ;

		assertThat( employee.getId( ) )
				.as( "ID" )
				.isNotNull( ) ;

		var matchingEmployees = employeeRepository.findByBirthDay( employee.getBirthDay( ) ) ;

		logger.info( Utils.buildDescription( "employees birthdays: ",
				"number", matchingEmployees.size( ),
				"matchingEmployees", matchingEmployees ) ) ;

	}

	@Test
	void list_all_employees_younger_than ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = employeeRepository.findByAgeLessThan( EmpHelpers.MIDDLE_AGE ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"younger then " + EmpHelpers.MIDDLE_AGE, employees.size( ),
				"first", employees.get( 0 ),
				"last", employees.get( employees.size( ) - 1 ) ) ) ;

		assertThat( employees.size( ) ).isGreaterThan( TEST_RERCORD_COUNT / 4 ) ;

		assertThat( employees.get( 0 ).getAge( ) ).isLessThan( TEST_RERCORD_COUNT / 4 ) ;

	}

	@Test
	void verify_all_employees_are_adults ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = employeeRepository.findByAgeLessThan( EmpHelpers.MIN_AGE ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"younger then " + EmpHelpers.MIDDLE_AGE, employees.size( ),
				"employees", employees ) ) ;

		assertThat( employees.size( ) ).isEqualTo( 0 ) ;

	}

	@Test
	void add_employee ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var emp = EmpHelpers.buildRandomizeEmployee( 19 ) ;

		employeeRepository.save( EmpHelpers.buildRandomizeEmployee( 19 ) ) ;

		logger.info( "added: ", emp.toString( ) ) ;

		assertThat( emp.getId( ) )
				.as( "ID" )
				.isNotNull( ) ;

	}

}
