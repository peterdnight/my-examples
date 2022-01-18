package org.sample.sorting ;

import java.util.Arrays ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

import static org.sample.Helpers.* ;

public class SimpleBubbleSort {

	void sortToLargest ( int someNumbers[] , int limitLargest ) {

		int arrayLength = someNumbers.length ;

		for ( int i = 0; i < limitLargest - 1; i++ ) {

			for ( int j = 0; j < arrayLength - i - 1; j++ ) {

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
}
