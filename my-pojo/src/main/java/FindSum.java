
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Optional ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class FindSum {

	static boolean isDebug = true ;

	record MatchSum ( int left , int right) {
	};

	static boolean doesArrayContainSum ( int desiredSum , int[] numbers ) {

		Arrays.sort( numbers ) ;

		Optional<MatchSum> optionalMatch = Optional.empty( ) ;

		var leftOffset = 0 ;
		var rightOffset = numbers.length - 1 ;

		debug( null ) ;

		while ( leftOffset < rightOffset ) {

			var sum = numbers[leftOffset] + numbers[rightOffset] ;

			debug( padLeft( String.format( "sum: %4s [%s,%s] [%s,%s]",
					sum,
					leftOffset, numbers[leftOffset],
					rightOffset, numbers[rightOffset] ),
					20 ) ) ;

			if ( sum == desiredSum ) {

				return true ;

			} else if ( sum < desiredSum ) {

				leftOffset++ ;

			} else { // A[i] + A[j] > sum

				rightOffset-- ;

			}

		}

		return false ;

	}

	static List<MatchSum> findLocationOfItemsSumming ( int desiredSum , int[] numbers ) {

		var matchedIndexes = new ArrayList<MatchSum>( ) ;

		debug( null ) ;

		for ( var firstIndex = 0; firstIndex < numbers.length - 1; firstIndex++ ) {

			for ( int largerIndex = firstIndex + 1; largerIndex < numbers.length; largerIndex++ ) {

				var sum = numbers[firstIndex] + numbers[largerIndex] ;

				var matchFlag = "" ;

				if ( sum == desiredSum ) {

					matchFlag += "*" ;
					matchedIndexes.add( new MatchSum( firstIndex, largerIndex ) ) ;

				}
				// debug( padLeft( String.format( matchFlag + "sum: %4s [%s,%s]", sum,
				// firstIndex, largerIndex ), 20 ) ) ;

			}

			debug( "\n" ) ;

		}

		// debug ( "matchedIndexes: " + matchedIndexes.toString( ).length( ) );

		return matchedIndexes ;

	}

	static List<MatchSum> fasterFindLocationOfItemsSumming ( int desiredSum , int[] numbers ) {

		var matchedIndexes = new ArrayList<MatchSum>( ) ;

		var valueMap = new HashMap<Integer, Integer>( ) ;

		debug( null ) ;

		for ( var firstIndex = 0; firstIndex < numbers.length; firstIndex++ ) {

			var diffFromTarget = desiredSum - numbers[firstIndex] ;

			if ( valueMap.containsKey( diffFromTarget ) ) {

				matchedIndexes.add( new MatchSum( firstIndex, valueMap.get( diffFromTarget ) ) ) ;

			}

			valueMap.put( numbers[firstIndex], firstIndex ) ;

//			debug( padLeft( String.format( "diff: %4s [%s,%s]", diffFromTarget, numbers.get( firstIndex ), firstIndex ),
//					20 ) ) ;
			debug( "\n" ) ;

		}

		return matchedIndexes ;

	}

	//
	//
	// Test Harness
	//
	//
	public static void main ( String args[] ) {

		var examples = new FindSum( ) ;

		var numbers = List.of( 2, 7, 11, 15 ) ;

		var desiredSum = 9 ;

		var matchedIndexes = findLocationOfItemsSumming( desiredSum, asArray( numbers ) ) ;

		printSection( "MatchSum Demo",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"matchedIndexes", matchedIndexes ) ;

		desiredSum = 1 ;
		printSection( "MatchSum Demo",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"matchedIndexes", findLocationOfItemsSumming( desiredSum, asArray( numbers ) ) ) ;

		numbers = List.of( 3, 3, 2, 4 ) ;
		desiredSum = 6 ;

		printSection( "MatchSum Demo",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"matchedIndexes", findLocationOfItemsSumming( desiredSum, asArray( numbers ) ) ) ;

		printSection( "FasterMatchSum Demo",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"matchedIndexes", fasterFindLocationOfItemsSumming( desiredSum, asArray( numbers ) ) ) ;

		printSection( "doesArrayContainSum",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"doesArrayContainSum", doesArrayContainSum( desiredSum, asArray( numbers ) ) ) ;

		desiredSum = 99 ;
		printSection( "doesArrayContainSum",
				"desiredSum", desiredSum,
				"numbers", numbers,
				"doesArrayContainSum", doesArrayContainSum( desiredSum, asArray( numbers ) ) ) ;

		isDebug = false ;

//		for ( var testIterations = 0; testIterations < 1; testIterations++ ) {
//
//			numbers = buildRandomIntegers( 100, 10 ) ;
//			desiredSum = 6 ;
//
//			printSection( "Find First Fast",
//					"desiredSum", desiredSum,
//					"numbers", numbers,
//					"first match", findFirstLocationOfItemsSumming( desiredSum, numbers ) ) ;
//
//			printSection( "double loop",
//					"desiredSum", desiredSum,
//					"numbers", numbers,
//					"number of matches (all)", findLocationOfItemsSumming( desiredSum, numbers ).size( ),
//					"first thousand chars", String.format( "%.1000s",
//							findLocationOfItemsSumming( desiredSum, numbers ).toString( ) ) ) ;
//
//			printSection( "map",
//					"desiredSum", desiredSum,
//					"numbers", numbers,
//					"number of matches (unique)", fasterFindLocationOfItemsSumming( desiredSum, numbers ).size( ),
//					"first thousand chars", String.format( "%.1000s",
//							fasterFindLocationOfItemsSumming( desiredSum, numbers ).toString( ) ) ) ;
//
//		}

		// nums = [2,7,11,15], target = 9

	}

	static int[] asArray ( List<Integer> numbers ) {

		return numbers.stream( ).mapToInt( Integer::intValue ).toArray( ) ;

	}

	//
	//
	// Record sample
	//
	//
	static int[] buildRandomIntegers ( int sizeOfArray ) {

		var randomNumbers = IntStream.range( 0, sizeOfArray )
				.map( iteration -> ThreadLocalRandom.current( ).nextInt( 0, 100 ) )
				.toArray( ) ;

		return randomNumbers ;

	}

	static void debug ( String msg ) {

		if ( isDebug ) {

			if ( msg == null ) {

				System.out.print( "\n\n\n ------------------- Debug  -----------------------\n" ) ;

			} else {

				System.out.print( msg ) ;

			}

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
