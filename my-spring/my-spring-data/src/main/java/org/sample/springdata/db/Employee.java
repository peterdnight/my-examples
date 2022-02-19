package org.sample.springdata.db ;

import java.time.LocalDate ;
import java.time.Period ;
import java.util.concurrent.ThreadLocalRandom ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;

import org.sample.springdata.utils.Utils ;

@Entity
public class Employee {

	@Id
	@GeneratedValue ( strategy = GenerationType.AUTO )
	private Long id ;
	private String name ;
	private Integer age ;
	private LocalDate birthDay ;

	public Employee ( String name, int age, LocalDate birthDay ) {

		this.name = name ;
		this.age = age ;
		this.birthDay = birthDay ;

	}
	

	
	public Employee () {
		
	}

	public Long getId ( ) {

		return id ;

	}

	public void setId ( Long id ) {

		this.id = id ;

	}

	public String getName ( ) {

		return name ;

	}

	public void setName ( String name ) {

		this.name = name ;

	}

	public Integer getAge ( ) {

		return age ;

	}

	public void setAge ( Integer age ) {

		this.age = age ;

	}

	public LocalDate getBirthDay ( ) {
	
		return birthDay ;
	
	}

	public void setBirthDay ( LocalDate birthDay ) {
	
		this.birthDay = birthDay ;
	
	}

	@Override
	public String toString ( ) {

		return "Employee [id=" + id + ", name=" + name + ", age=" + age + ", birthDay=" + birthDay + "]" ;

	}

}
