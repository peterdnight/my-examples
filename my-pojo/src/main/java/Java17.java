
import java.text.NumberFormat ;
import java.util.Arrays ;
import java.util.List ;
import java.util.Locale ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

public class Java17 {

	static boolean isDebug = true ;

	//
	//
	// TextBlock support
	//
	//
	String JSON_TEXT_BLOCK = """
			{
				"simple": "test"
			}

			""" ;

	void verify_json_block ( ) {

		printSection( "java inline text blocks", "JSON_TEXT_BLOCK", JSON_TEXT_BLOCK ) ;

	}

	//
	//
	// Record sample
	//
	//
	public record Person (
			String name ,
			int age) {
	};

	void verify_records ( ) {

		var demoRecord = new Person( "samm", 99 ) ;

		printSection( "demoRecord:",
				"record toString()", demoRecord,
				"name()", demoRecord.name( ),
				"age()", demoRecord.age( ) ) ;

	}

	//
	//
	// Switch Enhancements: java 17
	//
	//

	void verify_multi_switch ( int age ) {

		var description = switch ( age ) {

			case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 -> String.format( "child: %20d", age ) ;

			case 13, 14, 15, 16, 17, 18, 19 -> String.format( "teenager: %20d", age ) ;

			default -> String.format( "adult: %20d", age ) ;

		} ;

		printSection( "verify_multi_switch:",
				"description", description ) ;

	}

	//
	//
	// Switch With Objects: java 17 preview only
	//
	//

	void verify_switchedObjects ( Object demoPerson ) {

		var description = "java 17 preview mode - enable and uncomment" ;

//		var description = switch ( demoPerson ) {
//
//			case Integer someInt -> String.format( "Integer: %40d", someInt ) ;
//
//			case String someString -> String.format( "String: %40s", someString ) ;
//
//			case Person person -> String.format( "Person: %40s", person.toString( ) ) ;
//
//			default -> demoPerson.toString( ) ;
//
//		} ;

		printSection( "verify_switch:",
				"description", description ) ;

	}

	//
	//
	// Sealed objects to limit implementations
	//
	//
	// permits Shape.Circle,Shape.Rectangle
	sealed interface Shape {

		record Circle ( int radius) implements Shape {
		}

		record Rectangle ( int width , int height) implements Shape {
		}
	}

	void verify_sealedObjects ( ) {

		var myCircle = new Shape.Circle( 5 ) ;
		var myRectangle = new Shape.Rectangle( 3, 10 ) ;

		var myShapes = List.of( myCircle, myRectangle ) ;

		printSection( "verify_sealedObjects:",
				"shapes ", myShapes ) ;

		// java 17 preview
		// switch ( myShapes.get(0 ) {
		// case Circle c -> drawCircle ( c ) ;

	}

	//
	//
	// Java 12 teeing collector
	//
	//
	public void verify_teeing_collector_to_calculate_average ( List<Integer> numbers ) {

		double mean = numbers.stream( )
				.collect( Collectors.teeing(
						Collectors.summingDouble( i -> i ),
						Collectors.counting( ),
						( sum , count ) -> sum / count ) ) ;

		printSection( "teeing collector:",
				"numbers ", numbers,
				"mean", mean ) ;

	}

	//
	//
	// Java 12 Number Formatter
	//
	//
	public void verify_numberFormatter ( int theNumber ) {
		
		var myLocal = new Locale( "en", "US" ) ;

		var likesShort = NumberFormat.getCompactNumberInstance(
				myLocal,
				NumberFormat.Style.SHORT ) ;
		likesShort.setMaximumFractionDigits( 2 ) ;

		var likesLong = NumberFormat.getCompactNumberInstance(
				myLocal,
				NumberFormat.Style.LONG ) ;
		likesLong.setMaximumFractionDigits( 2 ) ;

		printSection( "teeing collector:",
				"original", theNumber,
				"likesShort ", likesShort.format( theNumber ),
				"likesLong", likesLong.format( theNumber ) ) ;

	}

	//
	//
	// Test Harness
	//
	//
	public static void main ( String args[] ) {

		var examples = new Java17( ) ;

		examples.verify_json_block( ) ;

		examples.verify_records( ) ;

		examples.verify_multi_switch( 5 ) ;
		examples.verify_multi_switch( 15 ) ;
		examples.verify_multi_switch( 25 ) ;

		examples.verify_switchedObjects( new Person( "peter", 99 ) ) ;

		examples.verify_switchedObjects( "some name" ) ;

		examples.verify_switchedObjects( 55 ) ;

		examples.verify_sealedObjects( ) ;

		examples.verify_teeing_collector_to_calculate_average( buildRandomIntegers( 10 ) ) ;

		examples.verify_numberFormatter( 912345 ) ;

	}

	//
	//
	// Record sample
	//
	//
	static List<Integer> buildRandomIntegers ( int sizeOfArray ) {

		var randomNumbers = IntStream.range( 0, sizeOfArray )
				.map( iteration -> ThreadLocalRandom.current( ).nextInt( 0, 100 ) )
				.boxed( )
				.collect( Collectors.toList( ) ) ;

		return randomNumbers ;

	}

	static void debug ( String msg ) {

		if ( isDebug )
			System.out.print( msg ) ;

	}

	static void print ( Object... items ) {

		System.out.println( buildDescription( "\n", items ) ) ;

	}

	static void printSection ( String header , Object... items ) {

		System.out.println( buildDescription( arrowMessage( header ), items ) ) ;

	}

	static String arrowMessage ( String message ) {

		return "\n\n\n*\n**\n***\n****\n" + padRight( "*****", 10 ) + message + "\n****\n***\n**\n*" ;

	}

	static String buildDescription ( String header , Object... items ) {

		var desc = new StringBuilder( header ) ;

		var itemsProcessed = 0 ;

		for ( var item : items ) {

			if ( itemsProcessed++ % 2 == 0 ) {

				desc.append( "\n" + padRight( item + ":", 30 ) ) ;

			} else {

				desc.append( "'" + item + "'" ) ;

			}

		}

		return desc.toString( ) ;

	}

	static String padRight ( Object stringToPad , int numSpaces ) {

		return String.format( "%-" + numSpaces + "s", stringToPad ) ;

	}

	static String padLeft ( Object stringToPad , int numSpaces ) {

		return String.format( "%" + numSpaces + "s", stringToPad ) ;

	}

}
