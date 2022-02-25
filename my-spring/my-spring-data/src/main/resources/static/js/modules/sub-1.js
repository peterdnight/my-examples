

import dialogs from "./utils/dialog-utils.js";
import utils from "./utils/app-utils.js";
import dom from "./utils/dom-utils.js";
// import net from "./utils/net-utils.js";

console.log( "\n\n loading module sub-1 ... \n\n" );   


let appScope ;

class Sub_Interface {
	
	static showValues() {
		
		appScope.showValues() ;
		
	} 
	
	
}
 export default Sub_Interface;

console.log( `\n\n Waiting for doc ready, current: ${ document.readyState } \n\n` )
document.addEventListener( "DOMContentLoaded", loadModule );
function loadModule( event ) {


    appScope = new sub_demo_module( globalThis.settings );
    appScope.initialize();

}

if ( document.readyState  == "complete" ) {
    loadModule( null ) ;
}




function sub_demo_module () {

    const _showValueButton = dom.findById( 'show-value' );


    let counterVariable = 1;




    this.initialize = function() {

        console.log( `initialization ` );

        //$( 'header' ).text( 'Hi from jQuery!' );

        registerEvents();

    }
    
    

    function registerEvents () {

        async function showValuesUsingUtils (  ) {
            showValues();
        }
        //_showValueButton.addEventListener( 'click', showValuesUsingUtils );

        // utils.onClick( _showValueButton,  (showValuesUsingUtils) ) ; 
        dom.onClick( _showValueButton,  () => { showValues() } ) ; 

    }

    function showValues () {

		counterVariable++ ;
		
        console.log( `showValues: counterVariable ${ counterVariable }` );

        dialogs.csapInfo( ` Hi from module sub-1, times invoked: ${ counterVariable }` );
    }
    
    this.showValues = showValues ;
}

