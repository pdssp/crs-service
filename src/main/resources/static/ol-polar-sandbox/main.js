/**
 * Download coordinate reference system definition.
 *
 * @param {*} source Coordinate Reference System code (Identifier, WKT)
 * @param {*} longFirst force longitude first if true
 * @returns
 */
async function downloadCRS(source, longFirst=false) {
    const url = '../crs/define?source=' + source + '&longitudeFirst=' + longFirst + '&format=application/json';
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
    }
    const json = await response.json();
    const projection = new ol.proj.Projection({
        code: json.code,
        extent: json.domainOfValidity,
        units: json.units,
        axisOrientation: json.axisDirection[0] == "east" ? "enu" : "neu"
    });
    return projection;
}

/**
 * Download the forward and inverse transforms between coordinate reference systems.
 *
 * @param {*} source source coordinate reference system
 * @param {*} target target coordinate reference system
 */
async function downloadOperation(source, target) {
    const sourceLongFirst = source.getAxisOrientation().startsWith("e");
    const targetLongFirst = target.getAxisOrientation().startsWith("e");
    const url = '../crs/operation?source=' + source.getCode() + '&sourceLongitudeFirst=' + sourceLongFirst + '&target=' + target.getCode() + '&targetLongitudeFirst=' + targetLongFirst + '&format=text/javascript';
    const res = await fetch(url);
    const txt = await res.text();
    const operation = eval('(' + txt + ')');
    return new operation();
}

// Register the CRS and transforms we will use
const epsg3857 = ol.proj.get("EPSG:3857"); //get CRS from openlayer
const epsg3031 = await downloadCRS("EPSG:3031"); //get CRS from server
ol.proj.addProjection(epsg3031);

// Register operation between CRS
const op3857to3031 = await downloadOperation(epsg3857,epsg3031);
function measure(fn, labelTarget) {
    let count = 0;
    let totalTimeSpent = 0;

    const targetElement = document.getElementById(labelTarget)

    return function(...args) {
        //WARNING: For performance counter precision to be in microseconds, the server have to send specific response headers.
        //         See https://stackoverflow.com/a/65959796/2678097 for details
        const t0 = window.performance.now()
        let result = fn(...args)
        const t1 = window.performance.now()
        totalTimeSpent += (t1 - t0)
        // count++
        // console.log(count++);
        if (++count % 100 === 0) {
            targetElement.textContent = `${count}+ calls. Total time spent: ~ ${totalTimeSpent.toFixed(2)} ms`
        }
        return result
    }
}

ol.proj.addCoordinateTransforms(
    epsg3857, epsg3031,
    measure(op3857to3031.transform, "forward-count").bind(op3857to3031),
    measure(op3857to3031.inverseTransform, "inverse-count").bind(op3857to3031)
);


// Create the map
const layers = [
    new ol.layer.Tile({
        source: new ol.source.OSM(),
    })
];

const map = new ol.Map({
    layers: layers,
    target: 'map',
    view: new ol.View({
        projection: epsg3031,
        center: [0, 0],
        extent: epsg3031.getExtent(),
        zoom: 0,
    }),
});
