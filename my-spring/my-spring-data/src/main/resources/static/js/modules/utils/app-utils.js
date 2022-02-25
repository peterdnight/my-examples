// import "../../../webjars/jquery/3.6.0/jquery.min.js";

console.log( "\n\n loading module \n\n" );

export default function DemoUtils() {
}

DemoUtils.enableCorsCredentials = function () {

    // traditional is critical for servlet based param processing.
    // timeout : deployTimeout, never timeout

    console.log( "JQuery ajaxSetup: disabled cache, enabled CORS via xhrFields, traditional params" );
    $.ajaxSetup( {
        cache: false,
        traditional: true,
        headers: {
            'Authorization': bearerAuth
        },
        xhrFields: {
            withCredentials: true // CORS Supports @CrossOrigin
        }
    } );
};

DemoUtils.splitUpperCase = function ( key ) {

    let resultWithSpaces = ""; // lowercase first word
    for ( let i = 0; i < key.length; i++ ) {
        if ( key.charAt( i ) == "_" )
            continue; // strip underscore
        // if ( i > 0 && key.charAt(i-1) == key.charAt(i-1).toUpperCase() ) {
        // // do not space consecutiive upper let
        // }


        if ( i > 0 && ( key.charAt( i ).toUpperCase() == key.charAt( i ) ) ) {

            // hack for OS
            if ( key.charAt( i ) != "S" && key.charAt( i - 1 ) != "0"
                && ( !( key.charAt( i ) >= 0 && key.charAt( i ) <= 9 ) ) )
                resultWithSpaces += " ";
        }
        resultWithSpaces += key.charAt( i );

    }
    return resultWithSpaces;
};


// jqplot requires consistent length
DemoUtils.padMissingPointsInArray = function ( arrayContainer ) {

    let longestArray = null;
    for ( let i = 0; i < arrayContainer.length; i++ ) {

        let curArray = arrayContainer[ i ];
        if ( longestArray == null || longestArray.length < curArray.length )
            longestArray = curArray;
    }

    for ( let i = 0; i < arrayContainer.length; i++ ) {

        let curArray = arrayContainer[ i ];
        if ( longestArray.length == curArray.length )
            continue;

        // make them the same size.
        for ( let j = 0; j < longestArray.length; j++ ) {
            let longestPoint = longestArray[ j ];
            let curPoint = curArray[ j ];
            // console.log("padd handler") ;
            if ( curPoint == undefined || longestPoint[ 0 ] != curPoint[ 0 ] ) {

                let padPoint = [ longestPoint[ 0 ], 0 ];

                curArray.splice( j, 0, padPoint );
            }
        }

    }

};



DemoUtils.handleConnectionError = function ( command, errorThrown ) {  // function
    // handleConnectionError(
    // command,
    // errorThrown
    // ) {

    if ( errorThrown == "abort" ) {
        console.log( "Request was aborted: " + command );
        return;
    }
    let message = "Failed to send request to server. Try again in a few minutes.";
    message += "<br><br>Command: " + command
    message += '<br><br>Server Response:<pre class="error" >' + errorThrown + "</pre>";

    let errorDialog = alertify.confirm( message );

    errorDialog.setting( {
        title: "Host Connection Error: Reload Page?",
        resizable: false,

        'onok': function () {
            document.location.reload( true );
        },
        'oncancel': function () {
            alertify.warning( "Wait a few seconds and try again," );
        }

    } );

    $( 'body' ).css( 'cursor', 'default' );
};



DemoUtils.prefixColumnEntryWithNumbers = function ( $table ) {
    $( "tr td:first-child", $table ).each( function ( index ) {
        let $label = jQuery( '<div/>', {
            class: 'tableLabel',
            text: ( index + 1 ) + "."

        } );
        $( this ).prepend( $label );
        $label.css( "height", $( this ).css( "height" ) );
    } );
}

DemoUtils.configureCsapToolsMenu = function () {

    let $toolsMenu = $( "header .csapOptions select" );

    $toolsMenu.change( function () {
        let item = $( "header .csapOptions select" ).val();

        if ( item != "default" ) {
            console.log( "launching: " + item );
            if ( item.indexOf( "logout" ) == -1 ) {
                DemoUtils.openWindowSafely( item, "_blank" );
            } else {
                document.location.href = item;
            }
            $( "header .csapOptions select" ).val( "default" )
        }

        $toolsMenu.val( "default" );
        $toolsMenu.selectmenu( "refresh" );

    } );
    //    $toolsMenu.selectmenu( {
    //        width: "10em",
    //        change: function () {
    //            let item = $( "header div.csapOptions select" ).val();
    //
    //            if ( item != "default" ) {
    //                console.log( "launching: " + item );
    //                if ( item.indexOf( "logout" ) == -1 ) {
    //                    CsapCommon.openWindowSafely( item, "_blank" );
    //                } else {
    //                    document.location.href = item;
    //                }
    //                $( "header div.csapOptions select" ).val( "default" )
    //            }
    //
    //            $toolsMenu.val( "default" );
    //            $toolsMenu.selectmenu( "refresh" );
    //        }
    //
    //    } );

    //    $("#csapTools-button", $toolsMenu.parent()).css("margin-right", "1em") 
}

DemoUtils.openWindowSafely = function ( url, windowFrameName ) {

    // console.log("window frame name: " + getValidWinName( windowFrameName)
    // + "
    // url: " + encodeURI(url)
    // + " encodeURIComponent:" + encodeURIComponent(url)) ;

    window.open( encodeURI( url ), DemoUtils.getValidWinName( windowFrameName ) );

}

DemoUtils.getValidWinName = function ( inputName ) {
    let regex = new RegExp( "-", "g" );
    let validWindowName = inputName.replace( regex, "" );

    regex = new RegExp( " ", "g" );
    validWindowName = validWindowName.replace( regex, "" );

    return validWindowName;
}
