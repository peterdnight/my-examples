

import utils from "./utilities.js";

console.log( "\n\n loading module \n\n" );


let appScope ;


class Sub_Interface {
	
	static showValues() {
		
		appScope.showValues() ;
		
	} 
	
	
}

export default Sub_Interface;


document.addEventListener( "DOMContentLoaded", function( event ) {

    console.log( `\n\n Waiting for doc ready, current: ${ document.readyState } \n\n` )

    appScope = new sub_demo_module( globalThis.settings );
    appScope.initialize();

} );

function getInstance() {
	
}


function sub_demo_module () {

    const $showValueButton = document.getElementById( 'show-value' );


    let s = 1;




    this.initialize = function() {

        console.log( `sub_demo_module ` );

        //$( 'header' ).text( 'Hi from jQuery!' );

        registerEvents();

    }
    
    this.showValues = showValues ;

    function registerEvents () {

        async function showValuesUsingUtils ( brand ) {
            showValues();
        }

        $showValueButton.addEventListener( 'click', showValuesUsingUtils );

    }

    function showValues () {

        console.log( `showValues` );

        alertify.csapInfo( ` Hi from module sub-1, times invoked: ${ s++ }` );
    }
}

