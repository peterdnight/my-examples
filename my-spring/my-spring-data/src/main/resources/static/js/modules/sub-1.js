

console.log(`loading imports`) ;

import { _dialogs, _dom } from "./utils/all-utils.js";

// import _dialogs from "./utils/dialog-utils.js";
// import _utils from "./utils/app-utils.js";
// import _dom from "./utils/dom-utils.js";
// import _net from "./utils/net-utils.js";


let appScope;

class Sub_Interface {

    static showValues() {

        appScope.showValues();

    }


}
export default Sub_Interface;


_dom.onReady( function () {

    appScope = new sub_demo_module( globalThis.settings );
    appScope.initialize();

} );


function sub_demo_module() {

    const _showValueButton = _dom.findById( 'show-value' );


    let counterVariable = 1;




    this.initialize = function () {

        _dom.logHead( `binding button actions` );

        //$( 'header' ).text( 'Hi from jQuery!' );

        registerEvents();

    }



    function registerEvents() {

        async function showValuesUsingUtils() {
            showValues();
        }
        //_showValueButton.addEventListener( 'click', showValuesUsingUtils );

        // utils.onClick( _showValueButton,  (showValuesUsingUtils) ) ; 
        _dom.onClick( _showValueButton, () => { showValues() } );

    }

    function showValues() {

        counterVariable++;

        console.log( `showValues: counterVariable ${ counterVariable }` );

        _dialogs.csapInfo( ` Hi from module sub-1, times invoked: ${ counterVariable }` );
    }

    this.showValues = showValues;
}

