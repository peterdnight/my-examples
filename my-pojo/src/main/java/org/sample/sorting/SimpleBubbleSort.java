package org.sample.sorting ;

import java.util.Arrays ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

import org.apache.commons.lang3.StringUtils ;
import org.sample.Helpers ;

import static org.sample.Helpers.* ;

public class SimpleBubbleSort {

	void sortToLargest ( int someNumbers[] , int limitLargest ) {

		int arrayLength = someNumbers.length ;

		for ( int unSortedIndex = 0; unSortedIndex < limitLargest - 1; unSortedIndex++ ) {

			for ( int j = 0; j < arrayLength - unSortedIndex - 1; j++ ) {

				if ( someNumbers[j] > someNumbers[j + 1] ) {

					// swap arr[j+1] and arr[j]
					int temp = someNumbers[j] ;
					someNumbers[j] = someNumbers[j + 1] ;
					someNumbers[j + 1] = temp ;

				}

			}

		}

	}

	void sortToLargest ( int someNumbers[] ) {

		sortToLargest( someNumbers, someNumbers.length ) ;

	}

	String intsToString ( int[] someNumbers ) {

		return IntStream.of( someNumbers ) // returns IntStream
				.boxed( )
				.collect( Collectors.toList( ) )
				.toString( ) ;

	}

	// Driver method to test above
	public static void main ( String args[] ) {

		SimpleBubbleSort myBubble = new SimpleBubbleSort( ) ;

		int someNumbers[] = {
				64, 34, 25, 12, 22, 11, 90
		} ;

		int someMoreNumbers[] = someNumbers.clone( ) ;

		printSection( "Original array", Arrays.toString( someNumbers ) ) ;

		myBubble.sortToLargest( someNumbers ) ;

		printSection( "Sorted array", Arrays.toString( someNumbers ) ) ;

		var limit = 2 ;
		myBubble.sortToLargest( someMoreNumbers, limit ) ;
		
		var someMoreNumbersWithLimit = Arrays.copyOfRange(
				someMoreNumbers,
				someMoreNumbers.length - limit,
				someMoreNumbers.length ) ;

		printSection( "Sorted array limit: " + limit, 
				Arrays.toString( someMoreNumbersWithLimit ) );

	}
	
	public static void printSection ( String header , Object... items ) {
		
		System.out.println( buildDescription( highlightHeader( header ), items ) ) ;
		
	}

	public static String buildDescription ( String header , Object... items ) {

		StringBuilder desc = new StringBuilder( header ) ;

		boolean doPad = true ;

		for ( var item : items ) {

			var asString = "null" ;

			if ( item != null ) {

				asString = item.toString( ) ;

			}

			if ( doPad ) {

				if ( StringUtils.isEmpty( asString ) ) {

					desc.append( "\n" ) ;

				} else {

					desc.append( padLine( asString ) ) ;

				}

			} else {

				desc.append( asString ) ;

			}

			doPad = ! doPad ;

		}

		return desc.toString( ) ;

	}
	

	static public String padLine ( String content ) {

		return "\n    " + StringUtils.rightPad( content + ":", 30 ) + "  " ;

	}
	
	
}
