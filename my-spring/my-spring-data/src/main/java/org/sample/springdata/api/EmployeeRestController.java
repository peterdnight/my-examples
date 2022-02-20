package org.sample.springdata.api ;

import java.time.LocalDate ;
import java.util.List ;
import java.util.stream.IntStream ;

import org.sample.springdata.db.Employee ;
import org.sample.springdata.db.EmployeeRepository ;
import org.sample.springdata.utils.EmpHelpers ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.data.domain.PageRequest ;
import org.springframework.data.domain.Sort ;
import org.springframework.web.bind.annotation.DeleteMapping ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.fasterxml.jackson.databind.JsonNode ;
import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.databind.node.ArrayNode ;

@RestController ( "/api" )
public class EmployeeRestController {

	public static final String BIRTHDAY_EMPLOYEES_PAGEABLE = "/birthdayEmployeesPageable" ;

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	EmployeeRepository employeeRepository ;
	ObjectMapper jsonMapper ;

	public EmployeeRestController ( EmployeeRepository employeeRepository, ObjectMapper jsonMapper ) {

		this.employeeRepository = employeeRepository ;
		this.jsonMapper = jsonMapper ;

	}

	@GetMapping ( "/birthdayEmployees" )
	public List<Employee> getBirthMonthEmployees (  ) {

		logger.info( Utils.highlightHeader( "finding birth Month employees" ) ) ;

		var employeePage = employeeRepository.findAllByBirthMonth(
				LocalDate.now( ).getMonthValue( ),
				PageRequest.of( 0, 10, Sort.by( "name" ) ) //
		) ;

		return employeePage.toList( ) ;

	}

	@GetMapping ( BIRTHDAY_EMPLOYEES_PAGEABLE )
	public JsonNode getBirthMonthEmployeesPageAble ( 
	                                                       @RequestParam( defaultValue = "10")  int pageSize, 
	                                                       @RequestParam( defaultValue = "0")  int pageNumber ) {

		logger.info( Utils.highlightHeader( "finding birth Month employees" ) ) ;

		var employeePage = employeeRepository.findAllByBirthMonth(
				LocalDate.now( ).getMonthValue( ),
				PageRequest.of( pageNumber, pageSize, Sort.by( "name" ) ) //
		) ;
		
		var birthdayReport = jsonMapper.createObjectNode( ) ;
		var page = birthdayReport.putObject( "view" ) ;
		page.put( "currentPage", employeePage.getNumber( )) ;
		page.put( "currentCount", employeePage.getNumberOfElements( )) ;
		page.put( "totalPages", employeePage.getTotalPages( )) ;
		page.put( "totalCount", employeePage.getTotalElements( )) ;
		
		birthdayReport.set( "employees", jsonMapper.convertValue( employeePage.toList( ), ArrayNode.class ) ) ;

		return birthdayReport ;

	}

	@GetMapping ( "/employees" )
	public List<Employee> getEmployees ( ) {

		logger.info( Utils.highlightHeader( "finding all employees" ) ) ;

		var employees = employeeRepository.findAll( ) ;

		return employees ;

	}

	@PostMapping ( "/test-data" )
	public JsonNode addTestData ( @RequestParam ( defaultValue = "10" ) int number ) {

		var result = jsonMapper.createObjectNode( ) ;

		logger.info( Utils.highlightHeader( "loading {} employees" ), number ) ;

		result.put( "test ids created", number ) ;

		IntStream.rangeClosed( 1, number )
				.forEach( uniqueId -> {

					employeeRepository.save(
							EmpHelpers.buildRandomizeEmployee( uniqueId ) ) ;

				} ) ;

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

}
