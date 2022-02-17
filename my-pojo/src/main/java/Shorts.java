
import java.util.HashSet ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class Shorts {

	static boolean isDebug = true ;

	//
	//
	// Given a string s, find the length of the longest substring without repeating
	// characters
	//
	//
	int lengthOfLongestSubstring ( String inputText ) {

		var charSet = new HashSet<Character>( ) ;
		var front = 0 ;
		var size = inputText.length( ) ;
		var result = 0 ;

		for ( var rear = 0; rear < size; rear++ ) {

			
			while ( charSet.contains( inputText.charAt( rear ) ) ) {

				charSet.remove( inputText.charAt( front ) ) ;
				front++ ;

			}

			charSet.add( inputText.charAt( rear ) ) ;
			int temp = rear - front + 1 ;
			result = temp > result ? temp : result ;

		}

		return result ;

	}

	//
	//
	// test driver
	//
	//
	public static void main ( String args[] ) {

		var shorts = new Shorts( ) ;

		var inputText = "abcabcbb" ;
		printSection( "lengthOfLongestSubstring",
				"inputText", inputText,
				"result", shorts.lengthOfLongestSubstring( inputText ) ) ;

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

	static void debug ( String msg ) {

		if ( msg == null ) {

			System.out.print( "\n\n\n ------------------- Debug  -----------------------\n" ) ;

		} else {

			System.out.print( msg ) ;

		}

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
