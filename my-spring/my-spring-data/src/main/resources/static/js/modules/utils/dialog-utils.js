import  "../../../../webjars/alertifyjs/1.13.1/alertify.js";
import  "../../../../webjars/jquery/3.6.0/jquery.min.js";


export default function Dialogs() {
}

console.log( `\n\n Waiting for doc ready, current: ${ document.readyState } \n\n` )
document.addEventListener( "DOMContentLoaded", loadModule );
function loadModule( event ) {


    Dialogs.configureCsapAlertify();

}

if ( document.readyState == "complete" ) {
    loadModule( null );
}

Dialogs.csapInfo = function ( message ) {
    alertify.csapInfo( message );
}

Dialogs.csapWarning = function ( message ) {
    alertify.csapWarning( message );
}


Dialogs.isFunction = function ( functionToCheck ) {
    return functionToCheck && {}.toString.call( functionToCheck ) === '[object Function]';
    //    return typeof functionToCheck === "function" ;
};

Dialogs.dialog_factory_builder = function ( configuration ) {

    let factory = function () {
        return Dialogs.build_alertify_factory( configuration );
    };


    return factory;
};


//                  configuration.content,
//                configuration.onresize,
//                configuration.onclose,
//                configuration.onok,
//                configuration.onshow,
//                configuration.width,
//                configuration.height,
//                configuration.buttons
Dialogs.build_alertify_factory = function (
    configuration ) {

    let _windowResizeTimer = null;


    // let isResizeEnabled = true ;

    let alertifyResize = function ( alertifyDialog ) {

        if ( !alertifyDialog.elements ) {
            console.log( `build_alertify_factory.alertifyResize(): dialog not visible, skipping resize` );
            return;
        }

        let targetWidth = Math.round( $( window ).outerWidth( true ) ) - 20;
        let targetHeight = Math.round( $( window ).outerHeight( true ) ) - 20;

        if ( Dialogs.isFunction( configuration.getWidth ) ) {
            let specWidth = Math.round( configuration.getWidth() );
            //alertifyDialog.elements.dialog.style.marginLeft = (targetWidth - specWidth) + "px";
            targetWidth = specWidth;
        }
        if ( Dialogs.isFunction( configuration.getHeight ) ) {
            let specHeight = Math.round( configuration.getHeight() );
            //alertifyDialog.elements.dialog.style.marginTop = (targetHeight - specHeight) + "px";
            targetHeight = specHeight;
        }

        console.log( `alertify_frameless_factory() targetWidth: ${ targetWidth },  targetHeight: ${ targetHeight } ` );

        alertifyDialog.elements.dialog.style.maxWidth = 'none';
        alertifyDialog.elements.dialog.style.width = targetWidth + "px";
        alertifyDialog.elements.dialog.style.maxHeight = 'none';
        alertifyDialog.elements.dialog.style.height = targetHeight + "px";

        if ( Dialogs.isFunction( configuration.onresize ) ) {
            configuration.onresize( targetWidth, targetHeight );
        } else {
            console.log( "resize function not found" );
        }

    };

    let alertifyWindowResizeScheduler = function ( alertifyDialog ) {

        clearTimeout( _windowResizeTimer );

        // put in background to wait for alertify instantiation
        _windowResizeTimer = setTimeout( function () {
            alertifyResize( alertifyDialog );
        }, 500 );
    };

    let maximizable = false;
    if ( configuration.maximizable ) {
        maximizable = configuration.maximizable;
    }

    let factory = {
        build: function () {
            // Move dom content from template
            this.setContent( configuration.content );

            this.setting( {
                onok: function ( closeEvent ) {

                    console.log( "dialogFactory(): dialog event:  ", JSON.stringify( closeEvent ) );

                    if ( Dialogs.isFunction( configuration.onok ) ) {
                        configuration.onok( closeEvent );
                    }

                },
            } );

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
                        console.log( "alertify maxed" );

                        if ( Dialogs.isFunction( configuration.onmax ) ) {
                            configuration.onmax( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight );
                        }


                        if ( Dialogs.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight );
                        }

                    },
                    onrestored: function () {
                        console.log( "alertify restored" );

                        if ( Dialogs.isFunction( configuration.onrestore ) ) {
                            configuration.onrestore( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight );
                        }


                        if ( Dialogs.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight );
                        }

                    },

                    onresized: function () {
                        console.log( "alertify resized" );

                        if ( Dialogs.isFunction( configuration.onresize ) ) {
                            configuration.onresize( this.elements.dialog.offsetWidth, this.elements.dialog.offsetHeight );
                        }
                        // isResizeEnabled = false ;
                    },

                    onclose: function () {
                        console.log( "alertify onclose" );

                        if ( Dialogs.isFunction( configuration.onclose ) ) {
                            configuration.onclose( this );
                        }

                    },

                    onshow: function () {

                        let currentDialog = this;

                        alertifyResize( currentDialog );

                        $( window ).resize( function () {
                            alertifyWindowResizeScheduler( currentDialog )
                        } );



                        if ( Dialogs.isFunction( configuration.onshow ) ) {
                            configuration.onshow();
                        }

                    },
                }

            };
        }

    }
    return factory;
}

/**
 * 
 *  create alertify templates
 * 
 * @see https://github.com/csap-platform/csap-core/wiki/Release-Notes
 * 
 */
Dialogs.configureCsapAlertify = function () {

    console.log( `\n\n  Dialogs.configureCsapAlertify: setting themes  \n\n` );

    // http://alertifyjs.com/
    alertify.defaults.glossary.title = "CSAP"
    alertify.defaults.theme.ok = "ui positive mini button";
    alertify.defaults.theme.cancel = "ui black mini button";
    alertify.defaults.notifier.position = "top-left";
    alertify.defaults.closableByDimmer = false;
    // alertify.defaults.theme.ok = "pushButton";
    // alertify.defaults.theme.cancel = "btn btn-danger";

    alertify.csapWarning = function ( message, head ) {
        let $warning = jQuery( '<div/>', {} );

        if ( head ) {
            let $errorHeader = jQuery( '<div/>', { text: head } ).appendTo( $warning );
            $errorHeader
                .css( "font-size", "14pt" )
                .css( "font-weight", "bold" )
                .css( "text-align", "left" )
                .css( "margin-left", "7px" );
        }
        $warning.append( jQuery( '<div/>', {
            class: "warning",
            html: message
        } ) );


        $warning.append( jQuery( '<button/>', {
            class: "csap-button close-csap",
            html: "Close"
        } ) );


        return alertify.error( $warning.html(), 0 );
    }


    alertify.csapInfo = function ( message, wrapText, head ) {
        let $info = jQuery( '<div/>', {} );

        if ( head ) {
            let $errorHeader = jQuery( '<div/>', { text: head } ).appendTo( $info );
            $errorHeader
                .css( "font-size", "14pt" )
                .css( "font-weight", "bold" )
                .css( "text-align", "left" )
                .css( "margin-left", "7px" );
        }

        let $messagePanel = jQuery( '<div/>', {
            class: "code",
            html: message
        } );

        if ( wrapText ) {
            $messagePanel.css( "white-space", "pre-wrap" );
            $messagePanel.css( "color", "blue" );
        }
        $info.append( $messagePanel );

        let canDismiss = false;

        $info.append( jQuery( '<button/>', {
            class: "csap-button csap-code-dismiss-button",
            html: "Close"
        } ) );


        let $theAlert = alertify.notify( $info.html(), "info", 0, function () {
            console.log( 'dismissed' );
        } );

        $theAlert.ondismiss = function () {
            return canDismiss;
        };

        setTimeout( () => {
            $( ".csap-code-dismiss-button" ).click( function () {
                canDismiss = true;
                $theAlert.dismiss();
            } );
        }, 500 );
        // let $theAlert = alertify.alert( $info.html());
        // $theAlert.set('frameless', true);
        return $theAlert;
    }

}
