package org.sample.springdata.utils ;

import java.time.LocalDate ;
import java.time.Period ;
import java.util.concurrent.ThreadLocalRandom ;

import org.sample.springdata.db.Employee ;

public class EmpHelpers {

	public static final int MIN_AGE = 20 ;
	public static final int MIDDLE_AGE = 50 ;
	public static final int MAX_AGE = 99 ;
	public static final int TEST_MONTH = 02 ;

	public static final int AGE_MODULA_SELECTOR = 3 ;
	public static final int MONTH_MODULA_SELECTOR = 4 ;

	/**
	 * 
	 * @param dataSetCustomizer: used to tune the year and month ranges of generated
	 *                           user
	 * @return
	 */
	public static Employee buildRandomizeEmployee ( int dataSetCustomizer ) {

		var minimumAge = LocalDate.now( ).getYear( ) - MIN_AGE ;
		var ageRandomLimit = LocalDate.now( ).getYear( ) - MIDDLE_AGE ;

		//
		// Seed data with some Older employees
		//
		if ( dataSetCustomizer % AGE_MODULA_SELECTOR == 0 ) {

			minimumAge = LocalDate.now( ).getYear( ) - MIDDLE_AGE ;
			ageRandomLimit = LocalDate.now( ).getYear( ) - MAX_AGE ;

		}

		long minDay = LocalDate.of( ageRandomLimit, 1, 1 ).toEpochDay( ) ;
		long maxDay = LocalDate.of( minimumAge - 1, 12, 31 ).toEpochDay( ) ;
		long randomDay = ThreadLocalRandom.current( ).nextLong( minDay, maxDay ) ;
		var randomBirthDay = LocalDate.ofEpochDay( randomDay ) ; // LocalDate.now( ) ;

		var randomAge = Period.between( randomBirthDay, LocalDate.now( ) ).getYears( ) ;

		var birthDay = LocalDate.ofEpochDay( randomDay ) ;

		if ( dataSetCustomizer % MONTH_MODULA_SELECTOR == 0 ) {

			birthDay = birthDay.withMonth( TEST_MONTH ) ;

		}

		Employee emp = new Employee(
				"sam-" + Utils.buildRandomString( 3 ),
				randomAge,
				birthDay ) ;
		return emp ;

	}

}
