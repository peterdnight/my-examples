function CsapCommon() {
}

CsapCommon.enableCorsCredentials = function () {

    // traditional is critical for servlet based param processing.
    // timeout : deployTimeout, never timeout

    console.log( "JQuery ajaxSetup: disabled cache, enabled CORS via xhrFields, traditional params" ) ;
    $.ajaxSetup( {
        cache: false,
        traditional: true,
        headers: {
            'Authorization': bearerAuth
        },
        xhrFields: {
            withCredentials: true // CORS Supports @CrossOrigin
        }
    } ) ;
} ;

CsapCommon.splitUpperCase = function ( key ) {

    let resultWithSpaces = "" ; // lowercase first word
    for ( let i = 0 ; i < key.length ; i++ ) {
        if ( key.charAt( i ) == "_" )
            continue ; // strip underscore
// if ( i > 0 && key.charAt(i-1) == key.charAt(i-1).toUpperCase() ) {
// // do not space consecutiive upper let
// }


        if ( i > 0 && ( key.charAt( i ).toUpperCase() == key.charAt( i ) ) ) {

            // hack for OS
            if ( key.charAt( i ) != "S" && key.charAt( i - 1 ) != "0"
                    && ( !( key.charAt( i ) >= 0 && key.charAt( i ) <= 9 ) ) )
                resultWithSpaces += " " ;
        }
        resultWithSpaces += key.charAt( i ) ;

    }
    return resultWithSpaces ;
} ;


// jqplot requires consistent length
CsapCommon.padMissingPointsInArray = function ( arrayContainer ) {

    let longestArray = null ;
    for ( let i = 0 ; i < arrayContainer.length ; i++ ) {

        let curArray = arrayContainer[ i ] ;
        if ( longestArray == null || longestArray.length < curArray.length )
            longestArray = curArray ;
    }

    for ( let i = 0 ; i < arrayContainer.length ; i++ ) {

        let curArray = arrayContainer[ i ] ;
        if ( longestArray.length == curArray.length )
            continue ;

        // make them the same size.
        for ( let j = 0 ; j < longestArray.length ; j++ ) {
            let longestPoint = longestArray[ j ] ;
            let curPoint = curArray[ j ] ;
            // console.log("padd handler") ;
            if ( curPoint == undefined || longestPoint[0] != curPoint[0] ) {

                let padPoint = [ longestPoint[0], 0 ] ;

                curArray.splice( j, 0, padPoint ) ;
            }
        }

    }

} ;



CsapCommon.handleConnectionError = function ( command, errorThrown ) {  // function
    // handleConnectionError(
    // command,
    // errorThrown
    // ) {

    if ( errorThrown == "abort" ) {
        console.log( "Request was aborted: " + command ) ;
        return ;
    }
    let message = "Failed to send request to server. Try again in a few minutes." ;
    message += "<br><br>Command: " + command
    message += '<br><br>Server Response:<pre class="error" >' + errorThrown + "</pre>" ;

    let errorDialog = alertify.confirm( message ) ;

    errorDialog.setting( {
        title: "Host Connection Error: Reload Page?",
        resizable: false,

        'onok': function () {
            document.location.reload( true ) ;
        },
        'oncancel': function () {
            alertify.warning( "Wait a few seconds and try again," ) ;
        }

    } ) ;

    $( 'body' ).css( 'cursor', 'default' ) ;
} ;


CsapCommon.isFunction = function ( functionToCheck ) {
    return functionToCheck && { }.toString.call( functionToCheck ) === '[object Function]' ;
//    return typeof functionToCheck === "function" ;
} ;

CsapCommon.dialog_factory_builder = function ( configuration ) {

    let factory = function () {
        return CsapCommon.build_alertify_factory( configuration ) ;
    } ;


    return factory ;
} ;


//                  configuration.content,
//                configuration.onresize,
//                configuration.onclose,
//                configuration.onok,
//                configuration.onshow,
//                configuration.width,
//                configuration.height,
//                configuration.buttons
CsapCommon.build_alertify_factory = function (
        configuration ) {

    let _windowResizeTimer = null ;


// let isResizeEnabled = true ;

    let alertifyResize = function ( alertifyDialog ) {

        if ( !alertifyDialog.elements ) {
            console.log( `build_alertify_factory.alertifyResize(): dialog not visible, skipping resize` ) ;
            return ;
        }

        let targetWidth = Math.round( $( window ).outerWidth( true ) ) - 20 ;
        let targetHeight = Math.round( $( window ).outerHeight( true ) ) - 20 ;

        if ( CsapCommon.isFunction( configuration.getWidth ) ) {
            let specWidth = Math.round( configuration.getWidth() ) ;
            //alertifyDialog.elements.dialog.style.marginLeft = (targetWidth - specWidth) + "px";
            targetWidth = specWidth ;
        }
        if ( CsapCommon.isFunction( configuration.getHeight ) ) {
            let specHeight = Math.round( configuration.getHeight() ) ;
            //alertifyDialog.elements.dialog.style.marginTop = (targetHeight - specHeight) + "px";
            targetHeight = specHeight ;
        }

        console.log( `alertify_frameless_factory() targetWidth: ${targetWidth},  targetHeight: ${ targetHeight } ` ) ;

        alertifyDialog.elements.dialog.style.maxWidth = 'none' ;
        alertifyDialog.elements.dialog.style.width = targetWidth + "px" ;
        alertifyDialog.elements.dialog.style.maxHeight = 'none' ;
        alertifyDialog.elements.dialog.style.height = targetHeight + "px" ;

        if ( CsapCommon.isFunction( configuration.onresize ) ) {
            configuration.onresize( targetWidth, targetHeight ) ;
        } else {
            console.log( "resize function not found" ) ;
        }

    } ;

    let alertifyWindowResizeScheduler = function ( alertifyDialog ) {

        clearTimeout( _windowResizeTimer ) ;

        // put in background to wait for alertify instantiation
        _windowResizeTimer = setTimeout( function () {
            alertifyResize( alertifyDialog ) ;
        }, 500 ) ;
    } ;

    let maximizable = false ;
    if ( configuration.maximizable ) {
        maximizable = configuration.maximizable ;
    }

    let factory = {
        build: function () {
            // Move dom content from template
            this.setContent( configuration.content ) ;

            this.setting( {
                onok: function ( closeEvent ) {

                    console.log( "dialogFactory(): dialog event:  ", JSON.stringify( closeEvent ) ) ;

                    if ( CsapCommon.isFunction( configuration.onok ) ) {
                        configuration.onok( closeEvent ) ;
                    }

                },
            } ) ;

        },

        setup: function () {
            return {

                buttons: configuration.buttons,

                options: {
                    // title: "Kubernetes YAML Deployment",
                    resizable: true,
                    autoReset: false,
                    movable: true,
                    maximizable: maximizable,
                    frameless: true,
                    title: false,
                    overflow: false,
                    transition: "fade", // fade, zoom, pulse

                    onmaximized: function () {
                        console.log( "alertify maxed" ) ;

                        if ( CsapCommon.isFunction( configuration.onmax ) ) {
                            configuration.onmax( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight ) ;
                        }


                        if ( CsapCommon.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight ) ;
                        }

                    },
                    onrestored: function () {
                        console.log( "alertify restored" ) ;

                        if ( CsapCommon.isFunction( configuration.onrestore ) ) {
                            configuration.onrestore( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight ) ;
                        }


                        if ( CsapCommon.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight ) ;
                        }

                    },

                    onresized: function () {
                        console.log( "alertify resized" ) ;

                        if ( CsapCommon.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight ) ;
                        }
// isResizeEnabled = false ;
                    },

                    onclose: function () {
                        console.log( "alertify onclose" ) ;

                        if ( CsapCommon.isFunction( configuration.onclose ) ) {
                            configuration.onclose( this ) ;
                        }

                    },

                    onshow: function () {

                        let currentDialog = this ;

                        alertifyResize( currentDialog ) ;

                        $( window ).resize( function () {
                            alertifyWindowResizeScheduler( currentDialog )
                        } ) ;



                        if ( CsapCommon.isFunction( configuration.onshow ) ) {
                            configuration.onshow() ;
                        }

                    },
                }

            } ;
        }

    }
    return factory ;
}


CsapCommon.labelTableRows = function ( $table ) {
    $( "tr td:first-child", $table ).each( function ( index ) {
        let $label = jQuery( '<div/>', {
            class: 'tableLabel',
            text: ( index + 1 ) + "."

        } ) ;
        $( this ).prepend( $label ) ;
        $label.css( "height", $( this ).css( "height" ) ) ;
    } ) ;
}

CsapCommon.configureCsapToolsMenu = function () {

    let $toolsMenu = $( "header .csapOptions select" ) ;

    $toolsMenu.change( function () {
        let item = $( "header .csapOptions select" ).val() ;

        if ( item != "default" ) {
            console.log( "launching: " + item ) ;
            if ( item.indexOf( "logout" ) == -1 ) {
                CsapCommon.openWindowSafely( item, "_blank" ) ;
            } else {
                document.location.href = item ;
            }
            $( "header .csapOptions select" ).val( "default" )
        }

        $toolsMenu.val( "default" ) ;
        $toolsMenu.selectmenu( "refresh" ) ;

    } ) ;
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

CsapCommon.configureCsapAlertify = function () {
    // http://alertifyjs.com/
    alertify.defaults.glossary.title = "CSAP"
    alertify.defaults.theme.ok = "ui positive mini button" ;
    alertify.defaults.theme.cancel = "ui black mini button" ;
    alertify.defaults.notifier.position = "top-left" ;
    alertify.defaults.closableByDimmer = false ;
    // alertify.defaults.theme.ok = "pushButton";
    // alertify.defaults.theme.cancel = "btn btn-danger";

    alertify.csapWarning = function ( message, head ) {
        let $warning = jQuery( '<div/>', { } ) ;
        
        if ( head ) {
			let $errorHeader = jQuery( '<div/>', { text: head } ).appendTo(  $warning ) ;
			$errorHeader
				.css("font-size", "14pt")
				.css("font-weight", "bold")
				.css("text-align", "left") 
				.css("margin-left", "7px") ;
		}
        $warning.append( jQuery( '<div/>', {
            class: "warning",
            html: message
        } ) ) ;


        $warning.append( jQuery( '<button/>', {
            class: "csap-button close-csap",
            html: "Close"
        } ) ) ;


        return alertify.error( $warning.html(), 0 ) ;
    }


    alertify.csapInfo = function ( message, wrapText, head ) {
        let $info = jQuery( '<div/>', { } ) ;
        
    	if ( head ) {
			let $errorHeader = jQuery( '<div/>', { text: head } ).appendTo(  $info ) ;
			$errorHeader
				.css("font-size", "14pt")
				.css("font-weight", "bold")
				.css("text-align", "left") 
				.css("margin-left", "7px") ;
		}

        let $messagePanel = jQuery( '<div/>', {
            class: "code",
            html: message
        } ) ;

        if ( wrapText ) {
            $messagePanel.css("white-space", "pre-wrap") ;
            $messagePanel.css("color", "blue") ;
        }
        $info.append( $messagePanel ) ;

        let canDismiss = false ;

        $info.append( jQuery( '<button/>', {
            class: "csap-button csap-code-dismiss-button",
            html: "Close"
        } ) ) ;


        let $theAlert = alertify.notify( $info.html(), "info", 0, function () {
            console.log( 'dismissed' ) ;
        } ) ;

        $theAlert.ondismiss = function () {
            return canDismiss ;
        } ;

        setTimeout( () => {
            $( ".csap-code-dismiss-button" ).click( function () {
                canDismiss = true ;
                $theAlert.dismiss() ;
            } ) ;
        }, 500 ) ;
        // let $theAlert = alertify.alert( $info.html());
        // $theAlert.set('frameless', true);
        return $theAlert ;
    }

}

CsapCommon.openWindowSafely = function ( url, windowFrameName ) {

    // console.log("window frame name: " + getValidWinName( windowFrameName)
    // + "
    // url: " + encodeURI(url)
    // + " encodeURIComponent:" + encodeURIComponent(url)) ;

    window.open( encodeURI( url ), CsapCommon.getValidWinName( windowFrameName ) ) ;

}

CsapCommon.getValidWinName = function ( inputName ) {
    let regex = new RegExp( "-", "g" ) ;
    let validWindowName = inputName.replace( regex, "" ) ;

    regex = new RegExp( " ", "g" ) ;
    validWindowName = validWindowName.replace( regex, "" ) ;

    return validWindowName ;
}
