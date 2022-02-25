
export default function NetUtils() {
}


NetUtils.httpGetJson = async function ( urlString = '', parameters = {} ) {

    let url = urlString;

    if ( parameters
        && Object.keys( parameters ).length > 0
        && Object.getPrototypeOf( parameters ) === Object.prototype ) {
        url += '?' + ( new URLSearchParams( parameters ) ).toString();
        // url = new URL( urlString, globalThis.settings.BASE_URL );
        // url.search = new URLSearchParams( data ).toString();
    }

    // Default options are marked with *

    const options = {
        method: 'GET', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            // 'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url

    }

    const response = await fetch( url, options );

    return response.json(); // parses JSON response into native JavaScript objects
}


NetUtils.httpPostJson = async function ( url = '', data = {} ) {

    // Default options are marked with *

    const options = {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify( data ) // body data type must match "Content-Type" header
    };

    const response = await fetch( url, options );

    return response.json(); // parses JSON response into native JavaScript objects
}

NetUtils.httpPostForm = async function ( url = '', data = {} ) {


    // Default options are marked with *
    const response = await fetch( url, {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            //'Content-Type': 'application/json'
            //'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: getFormData( data )  // body data type must match "Content-Type" header
    } );
    return response.json(); // parses JSON response into native JavaScript objects
}

function getFormData( object ) {

    const formData = new FormData();

    Object.keys( object )
        .forEach( key =>
            formData.append(
                key,
                object[ key ] ) );

    formData.append( "peter", "/test/now" );

    return formData;
}


NetUtils.httpDelete = async function ( url = '', data = {} ) {
    // Default options are marked with *
    const response = await fetch( url, {
        method: 'DELETE', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify( data ) // body data type must match "Content-Type" header
    } );
    return response.json(); // parses JSON response into native JavaScript objects
}

