package org.sample.springdata.db;

import java.util.List ;

import org.springframework.data.jpa.repository.JpaRepository ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.query.Param ;

public interface EmployeeRepo extends JpaRepository<Employee, Long>{

	Employee findByName(String name);
	
	Employee findByAge(int age);
	
	@Query("SELECT f FROM Employee f WHERE LOWER(f.name) = LOWER(:name)")
	Employee retrieveByName(@Param("name") String name);
	
	//@Query("SELECT f FROM Employee f WHERE LOWER(f.name) = LOWER(:name)")
	List<Employee> findByAgeLessThan(@Param("name") int age);

}
