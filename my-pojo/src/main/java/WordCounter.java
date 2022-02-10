
import java.util.Arrays ;
import java.util.concurrent.ThreadLocalRandom ;
import java.util.stream.IntStream ;

public class WordCounter {

	static boolean isDebug = true ;

	int maxNumberOfWords ( String wordToFind , String inputText ) {

		printSection( "word count",
				"wordToFind", wordToFind,
				"inputText", inputText ) ;

		var inputLetterCounts = buildLetterCounts( inputText ) ;

		print( "letters", Arrays.toString( inputLetterCounts ) ) ;

		var wordCount = 0 ;

		wordLoop: do {

			for ( var i = 0; i < wordToFind.length( ); i++ ) {

				var charToFind = wordToFind.charAt( i ) - 'a' ;

				inputLetterCounts[ charToFind ]-- ;

				if ( inputLetterCounts[ charToFind ] < 0 ) {

					break wordLoop ;

				}

			}

			wordCount++ ;

		} while ( true ) ;

		return wordCount ;

	}

	private int[] buildLetterCounts ( String inputText ) {

		var lettersFound = new int[26] ;

		for ( var i = 0; i < inputText.length( ); i++ ) {

			lettersFound[ inputText.charAt( i ) - 'a' ]++ ;

		}
		return lettersFound ;

	}

	int maxNumberOfWordsWithIndex ( String wordToFind , String inputText ) {

		printSection( "word count",
				"wordToFind", wordToFind,
				"inputText", inputText ) ;

		var inputTextLetterCount = buildLetterCounts( inputText ) ;

		print( "inputTextLetterCount", Arrays.toString( inputTextLetterCount ) ) ;

		var targetWordLetterCount = buildLetterCounts( wordToFind ) ;

		print( "targetWordLetterCount", Arrays.toString( targetWordLetterCount ) ) ;

		var wordCount = inputText.length( ) ;

		for ( var i = 0; i < wordToFind.length( ); i++ ) {

			var charToFindIndex = wordToFind.charAt( i ) - 'a' ;

			var textCount = inputTextLetterCount[ charToFindIndex ] ;
			var targetCount = targetWordLetterCount[ charToFindIndex ] ;

			var mostMatches = textCount / targetCount ;

			wordCount = Integer.min( wordCount, mostMatches ) ;

			debug( padLeft( wordToFind.charAt( i ) + ": " + targetCount + "[" + mostMatches + "]", 10 ) ) ;

		}

		return wordCount ;

	}

	public static void main ( String args[] ) {

		var wordCounter = new WordCounter( ) ;

		print( "maximum available", wordCounter.maxNumberOfWords( "balloon", "loonbalxballpoonz" ) ) ;

		print( "maximum available", wordCounter.maxNumberOfWords( "balloon", "balloonballoonballoon" ) ) ;

		print( "maximum available", wordCounter.maxNumberOfWordsWithIndex( "balloon", "balloonballoonballoon" ) ) ;

		print( "maximum available", wordCounter.maxNumberOfWordsWithIndex( "balloon", "" ) ) ;

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

				desc.append( "'" + item + "'") ;

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
