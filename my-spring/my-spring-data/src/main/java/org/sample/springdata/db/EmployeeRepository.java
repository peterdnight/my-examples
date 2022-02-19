package org.sample.springdata.db ;

import java.time.LocalDate ;
import java.util.List ;

import org.springframework.data.domain.Page ;
import org.springframework.data.domain.PageRequest ;
import org.springframework.data.jpa.repository.JpaRepository ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.query.Param ;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	//
	// Spring data will generate the implementation
	// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.core-concepts
	//

	long count ( ) ;

	List<Employee> findByBirthDay ( @Param ( "birthDay" ) LocalDate birthDay ) ;

	//
	// sql dialects:
	// http://www.hsqldb.org/doc/2.0/guide/builtinfunctions-chapt.html#bfc_extract_datetime
	//
	static final String MONTH_SELECTOR = " FROM Employee aDbRecord WHERE EXTRACT(MONTH FROM aDbRecord.birthDay) = :birthMonthFromInput" ;
	@Query ( "SELECT aDbRecord " + MONTH_SELECTOR )
	List<Employee> findByBirthMonth ( @Param ( "birthMonthFromInput" ) int month ) ;

	@Query ( value = "SELECT * FROM Employee WHERE EXTRACT(MONTH FROM birth_day) = ?1" , //
			countQuery = "SELECT count(*)  FROM Employee WHERE EXTRACT(MONTH FROM birth_day) = ?1" , //
			nativeQuery = true //
	)
	Page<Employee> findByBirthMonthPageable ( @Param ( "birthMonthFromInput" ) int month , PageRequest pageRequest ) ;

	Employee findByName ( String name ) ;

	Employee findByAge ( int age ) ;

	@Query ( "SELECT aDbRecord FROM Employee aDbRecord WHERE LOWER(aDbRecord.name) = LOWER(:name)" )
	Employee retrieveByName ( @Param ( "name" ) String name ) ;

	// @Query("SELECT f FROM Employee f WHERE LOWER(f.name) = LOWER(:name)")
	List<Employee> findByAgeLessThan ( @Param ( "name" ) int age ) ;

}
