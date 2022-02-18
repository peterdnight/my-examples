package org.sample.springdata.db ;

import static org.assertj.core.api.Assertions.assertThat ;

import java.time.LocalDate ;
import java.time.Period ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.TestInstance ;
import org.sample.springdata.utils.Utils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.boot.autoconfigure.domain.EntityScan ;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest ;
import org.springframework.context.ApplicationContext ;

@TestInstance ( TestInstance.Lifecycle.PER_CLASS )

@DataJpaTest ( showSql = false )
public class RepoTests {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	static final int TEST_INSERTS = 1000 ;
	static final int MIN_AGE = 20 ;
	static final int MIDDLE_AGE = 50 ;
	static final int MAX_AGE = 99 ;

	@BeforeAll
	void beforeAll ( )
		throws Exception {

		logger.info( Utils.testHeader( "loading test data" ) ) ;

		IntStream.range( 1, TEST_INSERTS )
				.forEach( uniqueId -> {

					var ageRandomLimit = LocalDate.now( ).getYear( ) - MIDDLE_AGE ;

					if ( uniqueId % 3 == 0 ) {

						ageRandomLimit = LocalDate.now( ).getYear( ) - MAX_AGE ;

					}

					long minDay = LocalDate.of( ageRandomLimit, 1, 1 ).toEpochDay( ) ;
					long maxDay = LocalDate.of( LocalDate.now( ).getYear( ) - MIN_AGE - 1, 12, 31 ).toEpochDay( ) ;
					long randomDay = ThreadLocalRandom.current( ).nextLong( minDay, maxDay ) ;
					var randomBirthDay = LocalDate.ofEpochDay( randomDay ) ; // LocalDate.now( ) ;

					var randomAge = Period.between( randomBirthDay, LocalDate.now( ) ).getYears( ) ;

					Employee emp = new Employee(
							"sam-" + Utils.buildRandomString( 3 ),
							randomAge,
							LocalDate.ofEpochDay( randomDay ) ) ;

					repository.save( emp ) ;

				} ) ;

	}

	//
	// Disable scanning based on classpath
	//
	@SpringBootApplication
	@EntityScan ( basePackageClasses = EmployeeRepo.class )
	public static class Simple_Application {

	}

	@Autowired
	ApplicationContext applicationContext ;

	@Test
	public void contextLoads ( ) {

		logger.info( Utils.testHeader( ) ) ;

		logger.info( "beans: {}", applicationContext.getBeanDefinitionCount( ) ) ;

		assertThat( applicationContext.getBeanDefinitionCount( ) )
				.as( "limited beans loaded for @DataJpaTest" )
				.isLessThan( 100 ) ;

	}

	@Autowired
	EmployeeRepo repository ;

	@Test
	void list_all_employees_younger_than ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = repository.findByAgeLessThan( MIDDLE_AGE ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"younger then " + MIDDLE_AGE, employees.size( ),
				"first", employees.get( 0 ),
				"last", employees.get( employees.size( ) - 1 ) ) ) ;

		assertThat( employees.size( ) ).isGreaterThan( TEST_INSERTS / 4 ) ;

		assertThat( employees.get( 0 ).getAge( ) ).isLessThan( TEST_INSERTS / 4 ) ;

	}

	@Test
	void verify_no_children ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = repository.findByAgeLessThan( MIN_AGE ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"younger then " + MIDDLE_AGE, employees.size( ),
				"employees", employees) ) ;

		assertThat( employees.size( ) ).isEqualTo( 0 ) ;


	}

	@Test
	void add_employee ( ) {

		logger.info( Utils.testHeader( ) ) ;

		Employee emp = new Employee( "junit-1", 99, LocalDate.now( ) ) ;

		repository.save( emp ) ;

		logger.info( "added: ", emp.toString( ) ) ;

		assertThat( emp.getId( ) )
				.as( "ID" )
				.isNotNull( ) ;

	}

	@Test
	void list_all_employees ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employees = repository.findAll( ) ;

		logger.info( Utils.buildDescription( "employees: ",
				"total", employees.size( ),
				"first", employees.get( 0 ),
				"last", employees.get( employees.size( ) - 1 ) ) ) ;

		assertThat( employees.size( ) ).isGreaterThan( TEST_INSERTS / 4 ) ;

	}

}
