package org.sample.springdata.api ;

import java.time.LocalDate ;
import java.util.List ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

import javax.validation.constraints.Max ;
import javax.validation.constraints.Min ;

import org.sample.springdata.db.Employee ;
import org.sample.springdata.db.EmployeeRepository ;
import org.sample.springdata.utils.EmpHelpers ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.data.domain.PageRequest ;
import org.springframework.data.domain.Sort ;
import org.springframework.validation.annotation.Validated ;
import org.springframework.web.bind.annotation.DeleteMapping ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ArrayNode ;

@RestController ( "/api" )

@Validated

public class EmployeeRestApis {

	public static final int MAX_PAGE_SIZE = 100 ;

	public static final String BIRTHDAY_EMPLOYEES_PAGEABLE = "/birthdayEmployeesPageable" ;

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	EmployeeRepository employeeRepository ;
	ObjectMapper jsonMapper ;

	public EmployeeRestApis ( EmployeeRepository employeeRepository, ObjectMapper jsonMapper ) {

		this.employeeRepository = employeeRepository ;
		this.jsonMapper = jsonMapper ;

	}

	@GetMapping ( "/birthMonthEmployees" )
	public JsonNode getBirthMonthEmployees ( ) {

		logger.info( Utils.highlightHeader( "finding birth Month employees" ) ) ;

		var employees = employeeRepository.findByBirthMonth( LocalDate.now( ).getMonthValue( ) ) ;

		var birthdayReport = jsonMapper.createObjectNode( ) ;
		var page = birthdayReport.putObject( "view" ) ;
		page.put( "totalCount", employees.size( ) ) ;
		birthdayReport.set( "employees", jsonMapper.convertValue( employees, ArrayNode.class ) ) ;

		return birthdayReport ;

	}

	@GetMapping ( BIRTHDAY_EMPLOYEES_PAGEABLE )
	public JsonNode getBirthMonthEmployeesPageAble (
														@RequestParam ( defaultValue = "10" ) @Min ( 1 ) @Max ( MAX_PAGE_SIZE ) int pageSize ,
														@RequestParam ( defaultValue = "0" ) int pageNumber ) {

		logger.info( Utils.highlightHeader( "finding birth Month employees" ) ) ;

		var pageRequest = PageRequest.of( pageNumber, pageSize, Sort.by( "name" ) ) ;

		var employeePage = employeeRepository.findAllByBirthMonth(
				LocalDate.now( ).getMonthValue( ),
				pageRequest //
		) ;

		var birthdayReport = jsonMapper.createObjectNode( ) ;
		var page = birthdayReport.putObject( "view" ) ;
		page.put( "pageRequest", pageRequest.toString( ) ) ;
		page.put( "currentPage", employeePage.getNumber( ) ) ;
		page.put( "currentCount", employeePage.getNumberOfElements( ) ) ;
		page.put( "totalPages", employeePage.getTotalPages( ) ) ;
		page.put( "totalCount", employeePage.getTotalElements( ) ) ;

		birthdayReport.set( "employees", jsonMapper.convertValue( employeePage.toList( ), ArrayNode.class ) ) ;

		return birthdayReport ;

	}

	@GetMapping ( "/employee/listing" )
	public List<Employee> getEmployees ( ) {

		logger.info( Utils.highlightHeader( "finding all employees" ) ) ;

		var employees = employeeRepository.findAll( ) ;

		return employees ;

	}

	@GetMapping ( "/employee/count" )
	public JsonNode getEmployeeCount ( ) {

		logger.info( Utils.highlightHeader( "Counting employees" ) ) ;

		var result = jsonMapper.createObjectNode( ) ;

		result.put( "count", employeeRepository.count( ) ) ;

		return result ;

	}

	@PostMapping ( "/test-data" )
	public JsonNode addTestData ( @RequestParam ( defaultValue = "8" ) int number ) {

		var result = jsonMapper.createObjectNode( ) ;

		logger.info( Utils.highlightHeader( "loading {} employees" ), number ) ;

		result.put( "number-added", number ) ;

		var testEmployees = IntStream.rangeClosed( 1, number )
				.mapToObj( EmpHelpers::buildRandomizeEmployee )
				.collect( Collectors.toList( ) ) ;

		employeeRepository.saveAll( testEmployees ) ;

		return result ;

	}

	@DeleteMapping ( "/employees" )
	public JsonNode deleteEmployees ( ) {

		var result = jsonMapper.createObjectNode( ) ;

		var countInDb = employeeRepository.count( ) ;

		logger.info( Utils.highlightHeader( "Deleting employees {}" ), countInDb ) ;

		employeeRepository.deleteAll( ) ;

		result.put( "test ids deleted", countInDb ) ;

		return result ;

	}

	@GetMapping ( "/date" )
	public JsonNode getDate ( @RequestParam ( defaultValue = "2" ) @Min ( 1 ) @Max ( 2 ) int pageSize ) {

		var resultReport = jsonMapper.createObjectNode( ) ;

		resultReport.put( "date", LocalDate.now( ).toString( ) ) ;

		return resultReport ;

	}

}
