/**
 * 
 * @see 
 * 
 */

import "./utils/all-utils.js";

import dialogs from "./utils/dialog-utils.js";
import utils from "./utils/app-utils.js";
import dom from "./utils/dom-utils.js";
import net from "./utils/net-utils.js";

import subModule from "./sub-1.js";


function moduleLoader( event ) {

    console.log( `\n\n Waiting for doc ready, current: ${ document.readyState } \n\n` )

    // utils.configureCsapAlertify();
    utils.prefixColumnEntryWithNumbers( $( "table" ) )

    let appScope = new sample_demo( globalThis.settings );


    appScope.initialize();

}

document.addEventListener( "DOMContentLoaded", moduleLoader );

if ( document.readyState == "complete" ) {
    loadModule( null );
}

/**
 * 
 *  Simple application for demonstating latest features of es6
 * 
 * @param {*} mySettings 
 * @returns 
 */
function sample_demo( mySettings ) {


    const countEmployees_link = dom.findById( "employee-count-link" );

    const count_input = dom.findById( "employee-count" );

    const numberOfEmployees_select = dom.findById( "number-of-employees" );

    const settings = mySettings;



    this.initialize = function () {

        console.log( `Build demo_application ` );

        //$( 'header' ).text( 'Hi from jQuery!' );

        registerEvents();

    }

    function registerEvents() {


        console.log( `register ui events, $countEmployeesButton length: ${ countEmployees_link.length }` );
        
        dom.onClick( countEmployees_link, ( event ) => {

            event.preventDefault();
            console.log( `countEmployees_link.click: ` );

            //
            // dynamic module load. versus: subModule.showValues() ;
            //
            // const newLocal = import( './sub-1.js' ).then( ( moduleDefinition ) => {

            //     //console.log( `Sub_Interface: `, moduleDefinition );
            //     moduleDefinition.default.showValues();
            // } );

            //
            // hit server for count data
            //
            const url = event.currentTarget.getAttribute( "href" );
            net.httpGetJson( url, { demoParam: "peter" } )

                .then( jsonResponse => {

                    console.log( jsonResponse );
                    count_input.value = jsonResponse.count ;

                } );

        } );


        // $numberOfEmployeesToAdd.change( function () {
        dom.onChange( numberOfEmployees_select, ( event ) => {

            let numberSelected = numberOfEmployees_select.value;

            if ( numberSelected == 0 ) {
                return;
            }
            // rest back to 0 to provide user feedback
            numberOfEmployees_select.value = 0 ;


            let parameters = {
                number: numberSelected
            };

            console.log( `parameters: `, parameters );

            net.httpPostForm(
                settings.BASE_URL + "test-data",

                {
                    number: numberSelected
                } )

                .then( jsonResponse => {
                    console.log( jsonResponse );
                    dialogs.csapInfo( JSON.stringify( jsonResponse, null, "\t" ) );
                    countEmployees_link.click();
                } );

        } );



        dom.onClick( dom.findById( "clear-db" ) ,  ( event ) => {

            net.httpDelete(
                settings.BASE_URL + "employees", null )

                .then( jsonResponse => {
                    console.log( jsonResponse );
                    dialogs.csapInfo( JSON.stringify( jsonResponse, null, "\t" ) );
                    countEmployees_link.click();
                } );

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
