
// http://requirejs.org/docs/api.html#packages
// Packages are not quite as ez as they appear, review the above
require.config( {

} );
require( [], function ( ) {
    console.log( "\n\n ************** module loaded *** \n\n" );


    var $templateFilter;
    var _refreshTimer;

    $( document ).ready( function () {

        initialize();

    } );

    function initialize() {

        CsapCommon.configureCsapAlertify();

        CsapCommon.labelTableRows( $( "table" ) );

        $( "#csapPageLabel" ).hide();

        $( "#csapPageVersion" ).css( "margin-left", "3em" );

        $( "table a.simple" ).click( function ( e ) {
            e.preventDefault();
            var url = $( this ).attr( 'href' );
            window.open( url, '_blank' );
        } );

        $( "img.csapDocImage" )
                .attr( "title", "Click to toggle display size" )
                .click(
                        function ( e ) {
                            $( this ).toggleClass(
                                    'csapDocImageEnlarged' );
                        } );

        $( "button.smallSubmit" ).click(
                function () {
                    alertify
                            .error(
                                    "Submitting request. Page will auto-refresh on complete",
                                    0 );
                    $( this ).parent().submit();
                } );

        $templateFilter = $( "#api-filter" );

        $templateFilter.off().keyup( function () {
            console.log( "Applying template filter" );
            clearTimeout( _refreshTimer );
            _refreshTimer = setTimeout( function () {
                applyFilter();
            }, 500 );
        } );
    }

    jQuery.expr[':'].icontains = function ( element, index, match ) {
        
        // console.log(`element`, element, `index: ${ index}, match `, match) ;
        return jQuery( element ).text().toUpperCase()
                .indexOf( match[3].toUpperCase() ) >= 0;
        
    };

    function applyFilter() {

        var $body = $( "table#api tbody" );
        var $rows = $( 'tr', $body );

        var filter = $templateFilter.val();

        console.log( "filter", filter );

        if ( filter.length > 0 ) {
            $rows.hide();
            $( 'td div.api-path:icontains("' + filter + '")', $rows ).parent().parent().show();
        } else {
            $rows.show();
        }
    }



} );