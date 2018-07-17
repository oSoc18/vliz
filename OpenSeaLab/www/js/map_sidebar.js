var map = L.map('map', {zoomControl:true}).setView([50.3791104480105, -2.19580078125], 5);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

var draw;
var rectangle;



//create a new dictionary for feature colors
let dictionary = new Map();

var URLpart0 ="http://127.0.0.1:8080/seabed?action=getGeoJSON&minLat=";
var URLpart1="&maxLat=";
var URLpart2="&minLong=";
var URLpart3="&maxLong=";




map.on({
	'click': function () {
		if(rectangle != undefined){
			map.removeLayer(rectangle);
		}

		draw = new L.Draw.Rectangle(map);
		draw.enable();
	},
	'draw:created': function (event) {
		rectangle = event.layer;
	},
	'draw:drawstop': function (event) {
		console.log(rectangle.getLatLngs());
		rectangle.addTo(map);

		var coors = rectangle.getLatLngs();
		var lats = coors.map(point => point.lat);
		var lons = coors.map(point => point.lng);
		document.getElementById("minLat").value = String(Math.min.apply(null, lats));
		document.getElementById("maxLat").value = String(Math.max.apply(null, lats));
		document.getElementById("minLong").value = String(Math.min.apply(null, lons));
		document.getElementById("maxLong").value = String(Math.max.apply(null, lons));

	}
});



// load data from the coordinates
function getDataFromCoords(){
   URLcoordinates = 	URLpart0 + document.getElementById("minLat").value +
							URLpart1 + document.getElementById("maxLat").value +
							URLpart2 + document.getElementById("minLong").value + 
							URLpart3 + document.getElementById("maxLong").value;			
	loadDataFrom(URLcoordinates);
	var button = document.getElementById("validateCoordinates");
	button.textContent = "loading...";
	button.disabled = true;
}

function drawRectangleFromInput(){
	var minLat = document.getElementById('minLat').value;
	var minLng = document.getElementById('minLong').value;
	var maxLat = document.getElementById('maxLat').value;
	var maxLng = document.getElementById('maxLong').value;

	firstCoor = L.latLng(minLat, minLng);
	var lastCoor = L.latLng(maxLat, maxLng);
	if(polygon != null){
		map.removeLayer(polygon);
 	}
	polygon = L.polygon([
				    firstCoor,
				    [firstCoor.lat, lastCoor.lng],
				    lastCoor,
				    [lastCoor.lat, firstCoor.lng]
				]);
	polygon.addTo(map);
}


function randomHex() {
	var hexNumbers = [0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F']
	// picking a random item of the array
	return hexNumbers[Math.floor(Math.random() * hexNumbers.length)];
}


// Genarates a Random Hex color
function hexGenerator() {
    hexValue = ['#'];
    for (var i = 0; i < 6; i += 1) {
        hexValue.push(randomHex());
    }
    return hexValue.join('');
}


function getStyle(feature){
   var clr;
	if(dictionary.has(feature.properties.WEB_CLASS)){
		clr = dictionary.get(feature.properties.WEB_CLASS);
	} else {
		clr = hexGenerator();
		dictionary.set(feature.properties.WEB_CLASS,clr);
	}
	return {color : clr};
}

function prepFeature(feature, layer){
	var seaarea = 0;
	if(feature.geometry.type == "MultiPolygon"){
		var i;
		for(i = 0; i < feature.geometry.coordinates.length; i++) {
			 seaArea = geodesicArea(layer.getLatLngs()[i]);
		} 
	}else{
		seaArea = geodesicArea(layer.getLatLngs()); 		
	}
	var list = "<dd>" + feature.properties.Allcomb + "</dd>"
			+ "<dt>Area : </dt>"
			+ seaArea ;
	layer.bindPopup( list );  
}

function addSeabedLayer(json){
    L.geoJson(json,
	   { style: getStyle
      , onEachFeature : prepFeature
		}).addTo(map); 
}


function loadDataFrom(url){
	$.getJSON(url, function(json){ 
		var button = document.getElementById("validateCoordinates");
		button.textContent = "Get data";
		button.disabled = false;
		addSeabedLayer(json); 
	});
}



function geodesicArea(latLngs) {
			var pointsCount = latLngs.length,
				area = 0.0,
				d2r = Math.PI / 180,
				p1, p2;
			if (pointsCount > 2) {
				for (var i = 0; i < pointsCount; i++) {
					p1 = latLngs[i];
					p2 = latLngs[(i + 1) % pointsCount];
					area += ((p2.lng - p1.lng) * d2r) *
						(2 + Math.sin(p1.lat * d2r) + Math.sin(p2.lat * d2r));
				}
				area = area * 6378137.0 * 6378137.0 / 2.0;
			}

			return Math.abs(area);
}


function randomHex() {
	var hexNumbers = [0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F']
	// picking a random item of the array
	return hexNumbers[Math.floor(Math.random() * hexNumbers.length)];
}


// Genarates a Random Hex color
function hexGenerator() {
    hexValue = ['#'];
    for (var i = 0; i < 6; i += 1) {
        hexValue.push(randomHex());
    }
    return hexValue.join('');
}

function drawRectangleFromInput(){
	var minLat = document.getElementById('minLat').value;
	var minLng = document.getElementById('minLong').value;
	var maxLat = document.getElementById('maxLat').value;
	var maxLng = document.getElementById('maxLong').value;

	firstCoor = L.latLng(minLat, minLng);
	var lastCoor = L.latLng(maxLat, maxLng);
	/*if(polygon != null){
		map.removeLayer(polygon);
 	}*/
	polygon = L.polygon([
				    firstCoor,
				    [firstCoor.lat, lastCoor.lng],
				    lastCoor,
				    [lastCoor.lat, firstCoor.lng]
				]);
	polygon.addTo(map);
	URLcoordinates = URLpart0.concat(firstCoor.lat,URLpart1.concat(lastCoor.lat,URLpart2.concat(firstCoor.lng,URLpart3)))+lastCoor.lng;
}

function getStatistics(){
	var URLpart0a ="http://127.0.0.1:8080/seabed?action=getStats&minLat=";
	var minLat = document.getElementById('minLat').value;
	var minLng = document.getElementById('minLong').value;
	var maxLat = document.getElementById('maxLat').value;
	var maxLng = document.getElementById('maxLong').value;

	var statsURLcoordinates = URLpart0a.concat(minLat,URLpart1.concat(maxLat,URLpart2.concat(minLng,URLpart3)))+maxLng;
	loadStatsFrom(statsURLcoordinates);


}

function loadStatsFrom(url){
	console.log(url);
	$.get(url, function(json){
		console.log("finished ---");
		console.log(json);
	});
}
