import java.util.ArrayList ;
import java.util.Comparator ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.TreeMap ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class SampleLogProcessor {
	
	//
	//  Copy paste this file into: https://www.jdoodle.com/online-java-compiler/ to view output
	//

	record MatchSum ( int skipped , String mostFrequentTuple ) {
	};

	public MatchSum findTopQuestions ( String alexaLogs ) {

		var skipped = 0 ;

		var lines = alexaLogs.split( "\n" ) ;

		var sessionToQuestionMap = new HashMap<String, ArrayList<String>>( ) ;
		// var questionToCount = new HashMap<String, Integer>( ) ;
		

		debug() ;

		for ( var logLine : lines ) {

			var currentLine = logLine.split( "," ) ;

			if ( currentLine.length == 4 ) {

				var sessionId = currentLine[ 1 ] ;
				var question = currentLine[ 3 ] ;
				
				//debug(sessionId + " " + question + "\n") ;

				var sessionQuestions = sessionToQuestionMap.get( sessionId ) ;

				if ( sessionQuestions == null ) {

					sessionQuestions = new ArrayList<String>( ) ;
					sessionToQuestionMap.put( sessionId, sessionQuestions ) ;
					

				}

				sessionQuestions.add( question ) ;

			} else {

				skipped++ ;

			}

		}
		debug("sessionToQuestionMap: " + sessionToQuestionMap.toString( ) + "\n") ;

		
		debug() ;
		var pairsToCount = new HashMap<String, Integer>( ) ;

		for ( var questions : sessionToQuestionMap.values( ) ) {

			// generate pairs
			for ( var leftIndex = 0; leftIndex < questions.size( ) -1 ; leftIndex++ ) {

				for ( var rightIndex = leftIndex + 1; rightIndex < questions.size( ); rightIndex++ ) {
					
					var leftKey = questions.get( leftIndex) ;
					var rightKey = questions.get( rightIndex ) ;
					var pairingId = leftKey + rightKey ;
					if ( rightKey.compareTo( leftKey ) < 0) {
						pairingId = rightKey + leftKey ;
					}
					
					var currentCount = pairsToCount.get( pairingId ) ;
					
					if ( currentCount == null ) {
						currentCount = 0 ;
					}

					
					var updatedCount = currentCount.intValue( ) + 1 ;
					
					pairsToCount.put( pairingId,  updatedCount) ;
					
					debug( String.format("%10s, %s", pairingId, updatedCount) ) ;

				}

			}

		}
		
		debug( "\n pairsToCount: " +  pairsToCount.toString( ) + "\n") ;

		// sort by value, return the first 2

		var bvc = new ValueComparator(pairsToCount);
		var sorted_map = new TreeMap<String, Integer>(bvc);
		sorted_map.putAll( pairsToCount );
		
		debug( "\n sorted_map: " +  sorted_map.toString( ) + "\n") ;

		return new MatchSum( skipped, sorted_map.firstKey( )) ;

	}
	
	class ValueComparator implements Comparator<String> {
	    Map<String, Integer> base;

	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}

	//
	//
	// test driver
	//
	//
	public static void main ( String args[] ) {

		var logProcessor = new SampleLogProcessor( ) ;

		var sampleLogs = """
				10-Mar-2020 10:34:00, S1, CA, Q1
				10-Mar-2020 10:30:00, S1, CA, Q2
				10-Mar-2020 10:32:00, S1, CA, Q3
				11-Mar-2020 11:27:00, S2, CA, Q4
				11-Mar-2020 11:29:00, S2, CA, Q2
				12-Mar-2020 18:34:00, S3, CA, Q1
				12-Mar-2020 18:36:00, S3, CA, Q3
				15-Mar-2020 17:24:00, S4, CA, Q1
				15-Mar-2020 17:26:00, S4, CA, Q2
				15-Mar-2020 17:28:00, S4, CA, Q3
				""" ;

		printSection( "processLogs",
				"sampleLogs", sampleLogs,
				"result", logProcessor.findTopQuestions( sampleLogs ) ) ;

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

	static String buildRandomString ( int sizeOfArray , int charLimit ) {

		var randomLetters = IntStream.range( 0, sizeOfArray )
				.map( iteration -> 'a' + ThreadLocalRandom.current( ).nextInt( 0, charLimit ) )
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
