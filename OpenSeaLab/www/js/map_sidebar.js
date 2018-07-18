var map = L.map('map', {zoomControl:true}).setView([50.3791104480105, -2.19580078125], 3);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

var draw;
var rectangle;

var loadedLayer = undefined;

//create a new dictionary for feature colors
let dictionary = new Map();

var URLpart0 ="http://127.0.0.1:8080/seabed?action=getGeoJSON&minLat=";
var URLpart1="&maxLat=";
var URLpart2="&minLong=";
var URLpart3="&maxLong=";

L.tileLayer.wms('http://ows.emodnet-bathymetry.eu/wms', {
    layers: 'mean_rainbowcolour', transparent: true,
    format: 'image/png'
}).addTo(map);

map.on({
	
	'draw:created': function (event) {
		console.log("Drawing started");
		rectangle = event.layer;
		console.log(rectangle);
	},
	'draw:drawstop': function (event) {
		console.log(rectangle.getLatLngs());
		rectangle.addTo(map);

		var coors = rectangle.getLatLngs()[0];
		var lats = coors.map(point => point.lat);
		var lons = coors.map(point => point.lng);
		document.getElementById("minLat").value = String(Math.min.apply(null, lats));
		document.getElementById("maxLat").value = String(Math.max.apply(null, lats));
		document.getElementById("minLong").value = String(Math.min.apply(null, lons));
		document.getElementById("maxLong").value = String(Math.max.apply(null, lons));

		getStatistics();
		getDataFromCoords();

	}
});


function getStyle(feature){
   var clr;
	if(dictionary.has(feature.properties.WEB_CLASS)){
		clr = dictionary.get(feature.properties.WEB_CLASS);
	} else {
		clr = "#"+ intToRGB(hashCode(feature.properties.WEB_CLASS)); //hexGenerator();
		dictionary.set(feature.properties.WEB_CLASS,clr);
	}
	return {color : clr, weight : 0.3};
}

function prepFeature(feature, layer){
	var list = feature.properties.Allcomb ;
	popupOptions = {maxWidth: 200};
                
	layer.bindPopup( /*list.toString(), popupOptions*/ "hey" );  
}

function addSeabedLayer(json){
	clearData();
    loadedLayer = L.geoJson(json,
	   { style: getStyle
      , onEachFeature : prepFeature
		})
    loadedLayer.addTo(map); 	
}


function loadDataFrom(url){
	$.getJSON(url, function(json){ 
		var button = document.getElementById("validateCoordinates");
		button.textContent = "Get data";
		button.disabled = false;
		clearRect();

		addSeabedLayer(json); 
	});
}

// load data from the coordinates
function getDataFromCoords(){

	var minLat = document.getElementById("minLat").value;
	var maxLat = document.getElementById("maxLat").value;
	var minLong = document.getElementById("minLong").value;
	var maxLong = document.getElementById("maxLong").value;

	if(minLat == "" || maxLat == "" || minLong == "" || maxLong == ""){
		alert("Specify an area first");
		return;
	}

	if((maxLat - minLat) * (maxLong - minLong) > 10){
		alert("The selected area is too big to display. (You can load statistics though)");
		return;
	} 


   URLcoordinates = 	URLpart0 + minLat +
							URLpart1 + maxLat +
							URLpart2 + minLong + 
							URLpart3 + maxLong;			
	loadDataFrom(URLcoordinates);
	var button = document.getElementById("validateCoordinates");

	button.textContent = "loading...";
	button.disabled = true;
}



function hashCode(str) { // java String#hashCode
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
       hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
} 

function intToRGB(i){
    var c = (i & 0x00FFFFFF)
        .toString(16)
        .toUpperCase();

    return "00000".substring(0, 6 - c.length) + c;
}

function drawRectangleFromInput(){
	var minLat = document.getElementById('minLat').value;
	var minLng = document.getElementById('minLong').value;
	var maxLat = document.getElementById('maxLat').value;
	var maxLng = document.getElementById('maxLong').value;

	firstCoor = L.latLng(minLat, minLng);
	var lastCoor = L.latLng(maxLat, maxLng);
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
	var button = document.getElementById("validateStats");
	button.textContent = "loading...";
	button.disabled = true;
	loadStatsFrom(statsURLcoordinates);
}
function loadStatsFrom(url){
	$.getJSON(url, function(json){

		var button = document.getElementById("validateStats");
		button.textContent = "Get Stats";
		button.disabled = false;

		var div = document.getElementById('statsOutput');
		var divInit = document.getElementById('statsInit');

		div.innerHTML = "";
		console.log(json);
		JSON.parse(JSON.stringify(json), function (key, value) {
			if(isInt(value)){

				var y = document.createElement("div");
				y.id = "wrapper";

				var x = document.createElement("div");
			    x.className = "seaBedColorSquare";
				x.style.backgroundColor = "#"+ intToRGB(hashCode(key));
				
				y.appendChild(x);
				
				var x1 = document.createElement("div");
			    x1.innerHTML = String(value).substring(0,8).concat("%    "+String(key));
			    
			    y.appendChild(x1);
			   	div.insertBefore(y,divInit);
			}
			
		});
	} );
}

function clearData(){
	if(loadedLayer != undefined){
		loadedLayer.clearLayers();
		map.removeLayer(loadedLayer);
		loadedLayer = undefined;
	}
}

function clearRect(){
	if(rectangle != null){
		map.removeLayer(rectangle);
		rectangle = null;
	}
}



function isInt(value) {
  return !isNaN(value) && !isNaN(parseInt(value, 10));
}

function enableDrawing(){
	clearRect();
	draw = new L.Draw.Rectangle(map);
	draw.enable();
}



var layer = new ol.layer.Image({
	extent: [-36, 25, 43, 85],
	source: new ol.source.ImageWMS({
		url: 'http://ows.emodnet-bathymetry.eu/wms',
		// refer to the section layer name to find the name of the layer 
		params: {'LAYERS': 'mean_atlas_land'}			
	})
}); 



