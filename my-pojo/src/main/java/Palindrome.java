
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class Palindrome {

	static boolean isDebug = true ;

	//
	//
	// Given a string s, find the length of the longest substring without repeating
	// characters
	// original:
	// https://leetcode.com/problems/longest-palindromic-substring/discuss/1778053/Java-Expand-Around-Center
	//
	//
	public String longestPalindrome ( String inputText ) {

		if ( inputText == null || inputText.length( ) <= 1 ) {

			return inputText ;

		}

		var startOfPalindromeIndex = 0 ;
		var endOfPalindromeIndex = 0 ;

		debug( ) ;

		for ( var inputIndex = 0; inputIndex < inputText.length( ); inputIndex++ ) {

			var oddPalindromeLength = expandAroundCenter( inputText, inputIndex, inputIndex ) ;
			var evenPalindromeLength = expandAroundCenter( inputText, inputIndex, inputIndex + 1 ) ;

			var maxLengthAtCurrentPosition = Math.max( oddPalindromeLength, evenPalindromeLength ) ;

			var message = String.format( "%10s [odd: %s, even: %s]\n",
					inputText.substring( startOfPalindromeIndex, endOfPalindromeIndex + 1 ),
					oddPalindromeLength,
					evenPalindromeLength ) ;
			debug( message ) ;

			if ( ( endOfPalindromeIndex - startOfPalindromeIndex ) < maxLengthAtCurrentPosition ) {

				startOfPalindromeIndex = inputIndex - ( maxLengthAtCurrentPosition - 1 ) / 2 ;
				endOfPalindromeIndex = inputIndex + maxLengthAtCurrentPosition / 2 ;

			}

		}

		return inputText.substring( startOfPalindromeIndex, endOfPalindromeIndex + 1 ) ;

	}

	private int expandAroundCenter ( String inputString , int left , int right ) {

		while ( left >= 0
				&& right < inputString.length( )
				&& inputString.charAt( left ) == inputString.charAt( right ) ) {

			left-- ;
			right++ ;

		}

		debug( inputString.substring( left + 1, right ), 8 ) ;

		return right - left - 1 ;

	}

	//
	//
	// test driver
	//
	//
	public static void main ( String args[] ) {

		var algorithms = new Palindrome( ) ;

		var inputText = "cababad" ;

		//
		// Code Reviewed
		//
		printSection( "code reviewed: longestPalindrome",
				"inputText", inputText,
				"result", algorithms.longestPalindrome( inputText ) ) ;

		inputText = "wqabbbarex" ;
		printSection( "code reviewed: longestPalindrome",
				"inputText", inputText,
				"result", algorithms.longestPalindrome( inputText ) ) ;

		inputText = "wqabarex" ;
		printSection( "code reviewed: longestPalindrome",
				"inputText", inputText,
				"result", algorithms.longestPalindrome( inputText ) ) ;


		inputText = buildRandomString(100, 3);
		printSection( "code reviewed: longestPalindrome",
				"inputText", inputText,
				"result", algorithms.longestPalindrome( inputText ) ) ;

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

	static String buildRandomString ( int sizeOfArray, int charLimit ) {

		var randomLetters = IntStream.range( 0, sizeOfArray )
				.map( iteration -> 'a' + ThreadLocalRandom.current( ).nextInt( 0, charLimit) )
				.collect( StringBuilder::new,
						StringBuilder::appendCodePoint,
						StringBuilder::append )
				.toString( ) ;
		// .collect(Collectors.joining( )) ;

		return randomLetters ;

	}

	static String buildRandomString ( int sizeOfArray ) {

		return buildRandomString( sizeOfArray, 26 ) ;

	}

	static void debug ( ) {

		System.out.print( "\n\n\n ------------------- Debug  Output-----------------------\n" ) ;

	}

	static void debug ( String msg ) {

		debug( msg, 20 ) ;

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
