

import "./my-globals.js";

import utils from "./utilities.js";
import subMod from "./sub-1.js";



document.addEventListener( "DOMContentLoaded", function( event ) {

    console.log( `\n\n Waiting for doc ready, current: ${ document.readyState } \n\n` )

    utils.configureCsapAlertify();
    utils.labelTableRows( $( "table" ) )

    let appScope = new demo_application( globalThis.settings );
    appScope.initialize();

} );


function demo_application ( mySettings ) {

    const $countEmployeesButton = $( "#employee-count-link" );

    const $countInput = $( "#employee-count" );

    const $numberOfEmployeesToAdd = $( "#number-of-employees" );

    const settings = mySettings;



    this.initialize = function() {

        console.log( `Build demo_application ` );

        //$( 'header' ).text( 'Hi from jQuery!' );

        registerEvents();

    }

    function registerEvents () {

        console.log( `register ui events, $countEmployeesButton length: ${ $countEmployeesButton.length }` );




        $countEmployeesButton.click( function( e ) {

            console.log( `countEmployeesButton.click: ` );

            e.preventDefault();

            subMod.showValues();

            fetch( $( this ).attr( "href" ) )
                .then( response => response.json() )
                .then( data => {
                    console.log( `got response: `, data );
                    $countInput.val( data.count );
                } );

        } );

        $numberOfEmployeesToAdd.change( function() {

            let numberSelected = $numberOfEmployeesToAdd.val();

            if ( numberSelected == 0 ) {
                return;
            }
            // rest back to 0 to provide user feedback
            $numberOfEmployeesToAdd.val( 0 );


            let parameters = {
                number: numberSelected
            };

            console.log( `parameters: `, parameters );

            utils.httpPostForm(
                settings.BASE_URL + "test-data",

                {
                    number: numberSelected
                } )

                .then( jsonResponse => {
                    console.log( jsonResponse );
                    alertify.csapInfo( JSON.stringify( jsonResponse, null, "\t" ) );
                    $countEmployeesButton.trigger( "click" );
                } );

        } );



        $( "#clear-db" ).click( function( e ) {

            utils.httpDelete(
                settings.BASE_URL + "employees", null )

                .then( jsonResponse => {
                    console.log( jsonResponse );
                    alertify.csapInfo( JSON.stringify( jsonResponse, null, "\t" ) );
                    $countEmployeesButton.trigger( "click" );
                } );

            //			$.delete("/employees")
            //				.done(function(commandResults) {
            //
            //					alertify.csapInfo(JSON.stringify(commandResults, null, "\t"));
            //
            //					$countEmployeesButton.trigger("click");
            //				})
            //				.fail(function(jqXHR, textStatus, errorThrown) {
            //					alertify.csapWarning("Failed Operation: " + jqXHR.statusText + "Contact support");
            //				});

        } );



    }

    return this;

}



//
//  Reference samples with error handing and dynamic module loading
//


// error handling  not included for samples
//    fetch( $( this ).attr( "href" ) + "e" )
//        .then( response => response.json() )
//        .then( data => {
//            console.log( data );
//
//            if ( data.status ) {
//                alertify.csapWarning( "Failed Operation: " + JSON.stringify( data, null, "\t" ) );
//            }
//        } );


//    alertify.csapWarning( "started" ) ;

//    async function loadMain () {
//        const main = await import( JQUERY_URL );
//        // ... use myModule
//        main.initializeWhenReady();
//    }
//
//    loadMain();
//
