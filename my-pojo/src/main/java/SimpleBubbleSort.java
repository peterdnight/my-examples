
import java.util.Arrays ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.Collectors ;
import java.util.stream.IntStream ;

public class SimpleBubbleSort {

	static boolean isDebug = true ;

	void findLargest ( int someNumbers[] , int maxDesired ) {

		for ( var bubbleIterations = 0; bubbleIterations < maxDesired; bubbleIterations++ ) {

			var indexOfSortedItems = someNumbers.length - bubbleIterations - 1 ;

			debug( "\n" ) ;

			for ( var bubbleUpIndex = 0; bubbleUpIndex < indexOfSortedItems; bubbleUpIndex++ ) {

				if ( someNumbers[ bubbleUpIndex ] > someNumbers[ bubbleUpIndex + 1 ] ) {

					debug( padLeft( "moving:" + padLeft( bubbleUpIndex + "[" + someNumbers[ bubbleUpIndex ] + "]", 7 ),
							20 ) ) ;
					int temp = someNumbers[ bubbleUpIndex ] ;
					someNumbers[ bubbleUpIndex ] = someNumbers[ bubbleUpIndex + 1 ] ;
					someNumbers[ bubbleUpIndex + 1 ] = temp ;

				}

			}

		}

	}

	void sortToLargest ( int someNumbers[] ) {

		findLargest( someNumbers, someNumbers.length ) ;

	}

	String intsToString ( int[] someNumbers ) {

		return IntStream.of( someNumbers ) // returns IntStream
				.boxed( )
				.collect( Collectors.toList( ) )
				.toString( ) ;

	}

	void sortDemo ( int[] someNumbers ) {

		printSection( "BubbleSortDemo",
				"input", Arrays.toString( someNumbers ) ) ;

		int sortAllNumbers[] = someNumbers.clone( ) ;

		sortToLargest( sortAllNumbers ) ;

		print( "test", "sorted full array",
				"values", Arrays.toString( sortAllNumbers ) ) ;

		var limit = 2 ;

		int sortWithLimitNumbers[] = someNumbers.clone( ) ;
		findLargest( sortWithLimitNumbers, limit ) ;

		var someMoreNumbersWithLimit = Arrays.copyOfRange(
				sortWithLimitNumbers,
				sortWithLimitNumbers.length - limit,
				sortWithLimitNumbers.length ) ;

		print( "test", "sorted array with limit",
				"limit", limit,
				"values", Arrays.toString( someMoreNumbersWithLimit ) ) ;

	}

	public static void main ( String args[] ) {

		SimpleBubbleSort myBubble = new SimpleBubbleSort( ) ;

		int someNumbers[] = {
				64, 34, 25, 12, 22, 11, 90
		} ;

		myBubble.sortDemo( someNumbers ) ;

		myBubble.sortDemo( buildRandomIntegers( 20 ) ) ;

	}

	static int[] buildRandomIntegers ( int sizeOfArray ) {

		var randomNumbers = IntStream.range( 0, sizeOfArray )
				.map( iteration -> ThreadLocalRandom.current( ).nextInt( 0, 100 ) )
				.toArray( ) ;

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

				desc.append( item ) ;

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
