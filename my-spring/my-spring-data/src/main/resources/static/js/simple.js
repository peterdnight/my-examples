// peter7
$( document ).ready( initialize );

var tableThemes = [ "metro-dark", "blue" ]; // Other themes need to be added to landing page
var currentTheme = 0;

var needsInit = true;

function initialize () {

    $( '#currentTimeButton' ).click( getTime );

    $( '#time-cors-button' ).click( getTimeCors );



    CsapCommon.configureCsapAlertify();
    CsapCommon.labelTableRows( $( "table" ) )

    $( '#themeTablesButton' ).click( function() {

        var $table = $( 'table' );
        console.log( "currentTheme:", tableThemes[ currentTheme ] );

        if ( needsInit ) {
            needsInit = false;
            $table.tablesorter( {
                widgets: [ 'uitheme', 'filter' ],
                theme: tableThemes[ currentTheme ]
            } );


            $( "table" ).removeClass( "simple" ).css( "width", "80em" )
                .css( "margin", "2em" );

        } else {

            $table[ 0 ].config.theme = tableThemes[ currentTheme ];
            $table.trigger( 'applyWidgets' );
        }


        if ( ++currentTheme >= tableThemes.length )
            currentTheme = 0;


    } );

}

function getTimeCors () {

    $( 'body' ).css( 'cursor', 'wait' );
    $.get( "http://peter.lab.sensus.net:8080/cors/currentTime",
        function( data ) {
            // alertify.alert(data) ;
            alertify.dismissAll();
            alertify.csapWarning( '<pre style="font-size: 0.8em">' + data
                + "</pre>" )
            $( 'body' ).css( 'cursor', 'default' );
        }, 'text' // I expect a JSON response
    );

}

function getTime () {

    $( 'body' ).css( 'cursor', 'wait' );
    $.get( "currentTime",
        function( data ) {
            // alertify.alert(data) ;
            alertify.dismissAll();
            alertify.csapWarning( '<pre style="font-size: 0.8em">' + data
                + "</pre>" )
            $( 'body' ).css( 'cursor', 'default' );
        }, 'text' // I expect a JSON response
    );

}