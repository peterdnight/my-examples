
import java.util.HashSet ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class Algorithms {

	static boolean isDebug = true ;

	//
	//
	// Given a string s, find the length of the longest substring without repeating
	// characters
	//
	//
	int findLongestSubstringWithNoRepeats ( String inputText ) {

		//
		// Slide a window over the string, adjusting width via start and end
		//
		var windowCharacters = new HashSet<Character>( ) ;
		var start = 0 ;
		var size = inputText.length( ) ;
		var longestSubString = 0 ;

		debug( ) ;

		for ( var end = 0; end < size; end++ ) {

			debug( inputText.substring( start, end ) ) ;

			while ( windowCharacters.contains( inputText.charAt( end ) ) ) {

				windowCharacters.remove( inputText.charAt( start ) ) ;
				start++ ;

			}

			windowCharacters.add( inputText.charAt( end ) ) ;

			int temp = end - start + 1 ;
			if ( temp > longestSubString ) {
				debug("\n") ;
			}
			longestSubString = temp > longestSubString ? temp : longestSubString ;
			

		}

		return longestSubString ;

	}

	//
	//
	// test driver
	//
	//
	public static void main ( String args[] ) {

		var algorithms = new Algorithms( ) ;

		var inputText = "abcabcaadklbbasdfghjkll" ;

		printSection( "lengthOfLongestSubstring",
				"inputText", inputText,
				"result", algorithms.findLongestSubstringWithNoRepeats( inputText ) ) ;

	}

	//
	//
	// Helpers
	//
	//
	static int[] buildRandomIntegers ( int sizeOfArray ) {

		var randomNumbers = IntStream.range( 0, sizeOfArray )
				.map( iteration -> ThreadLocalRandom.current( ).nextInt( 0, 100 ) )
				.toArray( ) ;

		return randomNumbers ;

	}

	static void debug ( ) {

		System.out.print( "\n\n\n ------------------- Debug  -----------------------\n" ) ;

	}

	
	static void debug ( String msg ) {

		debug( msg, 10 ) ;

	}

	static void debug ( String msg , int width ) {

		System.out.print( padLeft( msg, width ) ) ;

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
