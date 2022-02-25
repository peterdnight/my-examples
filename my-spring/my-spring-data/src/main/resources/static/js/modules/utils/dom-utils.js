
export default function DomUtils() {
}

//
// JS 6 helpers
//

DomUtils.findById = function ( domId ) {

    return document.getElementById( domId );

}

DomUtils.findByCss = function ( cssSelector ) {

    return document.querySelector( cssSelector );

}
DomUtils.findAllByCss = function ( cssSelector ) {

    return document.querySelectorAll( cssSelector );

}

DomUtils.findByClass = function ( className ) {

    return document.querySelector( `.${ className }` );

}

DomUtils.findAllByClass = function ( className ) {

    return document.querySelector( `.${ className }` );

}


DomUtils.onClick = function ( domElement, theFunction ) {

    domElement.addEventListener( 'click', theFunction );

}

DomUtils.onChange = function ( domElement, theFunction ) {

    domElement.addEventListener( 'change', theFunction );

}
