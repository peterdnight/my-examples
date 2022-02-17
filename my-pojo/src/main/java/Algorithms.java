
import java.util.HashSet ;
import java.util.Set ;
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
	int findLongestSubstringLength ( String inputText ) {

		//
		// Slide a window over the string, adjusting width via start and end
		//
		var windowCharacters = new HashSet<Character>( ) ;
		var windowStartIndex = 0 ;
		var size = inputText.length( ) ;
		var longestSubStringFound = 0 ;

		debug( ) ;

		for ( var windowEndIndex = 0; windowEndIndex < size; windowEndIndex++ ) {

			debug( inputText.substring( windowStartIndex, windowEndIndex ) ) ;

			while ( windowCharacters.contains( inputText.charAt( windowEndIndex ) ) ) {

				windowCharacters.remove( inputText.charAt( windowStartIndex ) ) ;
				windowStartIndex++ ;

			}

			windowCharacters.add( inputText.charAt( windowEndIndex ) ) ;

			var currentWindowLength = windowEndIndex - windowStartIndex + 1 ;

			if ( currentWindowLength > longestSubStringFound )
				debug( "\n" ) ;

			longestSubStringFound = currentWindowLength > longestSubStringFound
					? currentWindowLength
					: longestSubStringFound ;

		}

		return longestSubStringFound ;

	}

	//
	// from
	// https://leetcode.com/problems/longest-substring-without-repeating-characters
	//
	// @formatter:off
	public int lengthOfLongestSubstring ( String s ) {
        int l = 0;
        int r = 0;
        Set<Character> set = new HashSet<>();
        int maxdiff = 0;
        while (r < s.length()) {
            char rc = s.charAt(r);
            char lc = s.charAt(l);
            if(set.contains(rc)) {
                set.remove(lc);
                l++;
            } else {
                set.add(rc);
                r++;
            }
            maxdiff = Math.max(r - l, maxdiff);
        }
        return maxdiff;
    }
	// @formatter:on

	//
	//
	// test driver
	//
	//
	public static void main ( String args[] ) {

		var algorithms = new Algorithms( ) ;

		var inputText = "abcabcaadklbbasdfghjkllmn" ;

		//
		// Code Reviewed
		//
		printSection( "code reviewed: findLongestSubstringLength",
				"inputText", inputText,
				"result", algorithms.findLongestSubstringLength( inputText ) ) ;

		//
		// Leet
		//
		printSection( "leetcode: lengthOfLongestSubstring",
				"inputText", inputText,
				"result", algorithms.lengthOfLongestSubstring( inputText ) ) ;

		//
		// random string
		//
		var randomString = buildRandomString( 2000 ) ;
		printSection( "code reviewed: findLongestSubstringLength",
				"inputText size", randomString.length( ),
				"result", algorithms.findLongestSubstringLength( randomString ) ) ;

	}

	//
	//  Copy paste this file into: https://www.jdoodle.com/online-java-compiler/ to view output
	//

//			 ------------------- Debug  Output-----------------------
//			                                       
//			                   a                   
//			                  ab                   
//			                 abc                 bca                 cab                 abc                 bca                   a                  ad                 adk                   
//			                adkl                   
//			               adklb                   b                  ba                 bas                basd               basdf                   
//			              basdfg                   
//			             basdfgh                   
//			            basdfghj                   
//			           basdfghjk                   
//			          basdfghjkl                   l                  lm
//		
//		
//			*
//			**
//			***
//			****
//			*****     code reviewed: findLongestSubstringLength
//			****
//			***
//			**
//			*
//			inputText:                    'abcabcaadklbbasdfghjkllmn'
//			result:                       '10'
//		
//		
//		
//			*
//			**
//			***
//			****
//			*****     leetcode: lengthOfLongestSubstring
//			****
//			***
//			**
//			*
//			inputText:                    'abcabcaadklbbasdfghjkllmn'
//			result:                       '10'
//		
//		
//		
//			 ------------------- Debug  Output-----------------------
//			                                       
//			                   g                   
//			                  gt                   
//			                 gto                   
//			                gtom                   
//			               gtomr                   
//			              gtomra                   
//			             gtomran                   
//			            gtomranw                   
//			           gtomranwe                   
//			          gtomranwei                   
//			         gtomranweiv                   
//			        gtomranweivx                   
//			       gtomranweivxp                   
//			      gtomranweivxpj                   
//			     gtomranweivxpjk               xpjkv              xpjkvl                   l                  ls                 lsx                 sxl                sxlt                          
//			    oxzuefigwjsdvlym                   m                  mh                 mhd                mhdn               mhdnf              mhdnfr             mhdnfri      
//		
//		
//			*
//			**
//			***
//			****
//			*****     code reviewed: findLongestSubstringLength
//			****
//			***
//			**
//			*
//			inputText size:               '2000'
//			result:                       '16'

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

	static String buildRandomString ( int sizeOfArray ) {

		var randomLetters = IntStream.range( 0, sizeOfArray )
				.map( iteration -> 'a' + ThreadLocalRandom.current( ).nextInt( 0, 26 ) )
				.collect( StringBuilder::new,
						StringBuilder::appendCodePoint,
						StringBuilder::append )
				.toString( ) ;
		// .collect(Collectors.joining( )) ;

		return randomLetters ;

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
