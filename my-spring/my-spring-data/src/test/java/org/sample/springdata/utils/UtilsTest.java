package org.sample.springdata.utils ;

import static org.assertj.core.api.Assertions.assertThat ;

import org.junit.jupiter.api.Test ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class UtilsTest {

	Logger logger = LoggerFactory.getLogger( getClass( ) ) ;

	static {

		Utils.initialize( "Test Setup Complete" ) ;

	}

	@Test
	void verify_random_employee_birth_month ( ) {

		logger.info( Utils.testHeader( ) ) ;

		var employee = EmpHelpers.buildRandomizeEmployee( EmpHelpers.MONTH_MODULA_SELECTOR ) ;

		logger.info( Utils.buildDescription(
				"employee generator: ",
				"employee", employee //
		) ) ;

		assertThat(
				employee.getId( ) )
						.isNull( ) ;

		assertThat(
				employee.getName( ) )
						.isNotEmpty( ) ;

		assertThat(
				employee.getBirthDay( ).getMonthValue( ) )
						.isEqualTo( EmpHelpers.TEST_MONTH ) ;

	}

}
