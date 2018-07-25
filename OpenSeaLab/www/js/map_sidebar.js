var map = L.map('map', {zoomControl:true}).setView([47.3791104480105, -2.19580078125], 4);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

var autoShowCached = false;
var autoShowCachedMinZoom = 8;

var draw;
var rectangle;

var loadedLayer = undefined;

//create a new dictionary for feature colors
let dictionary = new Map();

// layer source http://portal.emodnet-bathymetry.eu/services/#wms
let bathymetryOWSMaps = ["mean","mean_rainbowcolour","mean_multicolour","mean_atlas_land","source_references","contours","products"];


var layer = "seabed"
var URLpart0 ="http://127.0.0.1:8080/"+layer+"?action=getGeoJSON&minLat=";

var URLpart0Stats ="http://127.0.0.1:8080/"+layer+"?action=getStats&minLat=";
//var seabedtype = "EUSM2016_simplified200"

// DOESN'T WORK at the moment, works when commented out and using the other method
/*var hostLocal = "127.0.0.1";
var host = "172.21.190.147:8080"
var URLpart0 ="http://"+host+"/seabed?action=getGeoJSON&minLat=";
var URLpart0Stats ="http://"+host+"/seabed?action=getStats&minLat=";*/

var URLpart1="&maxLat=";
var URLpart2="&minLong=";
var URLpart3="&maxLong=";
var URLPart4="&type=";

document.getElementById("minLat").value = "";
document.getElementById("maxLat").value = "";
document.getElementById("minLong").value = "";
document.getElementById("maxLong").value = "";



var baseMaps = [];

function BathymetryCheck(layerNum){
	document.getElementById('loadingSVG').style.zIndex = "4";
	var layerName = "Bathymetry-opt" + (layerNum).toString();
	console.log("curr "+ layerName + layerNum);
	if(document.getElementById(layerName).checked == true){
	   	console.log("checked "  + layerNum);
		baseMaps.push( L.tileLayer.wms('http://ows.emodnet-bathymetry.eu/wms', {
	   		id: layerName,
		    layers: bathymetryOWSMaps[layerNum], transparent: true,
		    format: 'image/png',
		    opacity: 0.75
			}) );
		(baseMaps[baseMaps.length -1]).addTo(map);
	}else{
		console.log("removing");
		for (var key of baseMaps) { 
		    console.log(key.wmsParams.id);
		    if(key.wmsParams.id == layerName){
		    	console.log(layerName + " Match!");
		    	map.removeLayer(key);
		    }
		}
	}
	document.getElementById('loadingSVG').style.zIndex = "0";	

}




$('.btn-expand-collapse').click(function(e) {
	$('.navbar-primary').toggleClass('collapsed');
	map.invalidateSize();
});


// Draw the rectangle on the map
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

		document.getElementById('loadingSVG').style.zIndex = "4";

		getDataFromCoords();

	},
	//'moved': loadForView
});


var lastNorth;
var lastEast;
function loadForView(){
		if(!autoLoadCached){
			return;
		}


		if(map.getZoom() <= autoShowCachedMinZoom){
			return;
		}
		var bnds = map.getBounds();
		var n = Math.ceil(bnds.getNorth());
		var e = Math.ceil(bnds.getEast());
		if(lastNorth == n && lastEast == e){
			return;
		}
		lastNorth = n;
		lastEast = e;

		getDataForCoords(""+bnds.getSouth(), ""+bnds.getNorth(), ""+bnds.getWest(), ""+bnds.getEast(), "True");
}

////// Adding seabed Habitat Data to the map


function getStyle(feature){
   var clr;
	if(dictionary.has(feature.properties.WEB_CLASS)){
		clr = dictionary.get(feature.properties.WEB_CLASS);
	} else if (feature.properties.WEB_CLASS) {
		clr = "#"+ intToRGB(hashCode(feature.properties.WEB_CLASS)); //hexGenerator();
		dictionary.set(feature.properties.WEB_CLASS,clr);
	}else if (feature.properties.folk_5_substrate_class) {
		clr = "#"+ intToRGB(hashCode(feature.properties.folk_5_substrate_class)); //hexGenerator();
		dictionary.set(feature.properties.folk_5_substrate_class,clr);
	}
	return {color : clr, weight : 0.0, fillOpacity : .75};
}

function prepFeature(feature, layer){
	var list = feature.properties.Allcomb ;
	popupOptions = {maxWidth: 200};

	layer.bindPopup( /*list.toString(), popupOptions*/ "hey" );
}

function addSeabedLayer(json){
	

	var geojsonMarkerOptions = {
		 radius: 8,
		 fillColor: "#ff7800",
		 color: "#000",
		 weight: 1,
		 opacity: 1,
		 fillOpacity: 0.8
	};


	console.log("adding seabed");

    loadedLayer = L.geoJson(json,
	   { style: getStyle
      , onEachFeature : prepFeature
		, pointToLayer: function (feature, latlng) {
        return L.circleMarker(latlng, geojsonMarkerOptions);}
		});
    document.getElementById('loadingSVG').style.zIndex = "0";
    loadedLayer.addTo(map);
}


function loadDataFrom(url){
	console.log("about to add seabed");
	console.log(url);
	clearData();
	$.getJSON(url, function(json){
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
	getDataForCoords(minLat, maxLat, minLong, maxLong, "False");
}

function getDataForCoords(minLat, maxLat, minLong, maxLong, caching){
	if(minLat == "" || maxLat == "" || minLong == "" || maxLong == ""){
		alert("Specify an area first");
		return;
	}

    URLcoordinates = minLat +
						URLpart1 + maxLat +
						URLpart2 + minLong + 
						URLpart3 + maxLong +
						"&cacheOnly=" + caching;
	loadDataFrom(URLpart0 + URLcoordinates);
	loadStatsFrom(URLpart0Stats + URLcoordinates);

}



// Get statistics from the URL

function loadStatsFrom(url){
	$.getJSON(url, function(json){

		var div = document.getElementById('statsOutput');

		console.log(json);
		JSON.parse(JSON.stringify(json), function (key, value) {
			if(isInt(value) && value != 0.0){

				var y = document.createElement("div");
				y.id = "wrapper";

				var x = document.createElement("div");
			    x.className = "seaBedColorSquare";
				x.style.backgroundColor = "#"+ intToRGB(hashCode(key));

				y.appendChild(x);

				var x1 = document.createElement("div");
			    x1.innerHTML = String(value).substring(0,8).concat("%    "+String(key));
			    x1.className = "statsValue";

			    y.appendChild(x1);
			   	div.insertBefore(y,null);
			}

		});
	} );
  undisableBtn();
}

// Create a hash for the seabed habitat type based on its unique WEB_CLASS

function hashCode(str) {
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



function clearData(){
	
	var divDel = document.getElementById('statsOutput');
	divDel.innerHTML = "";

	clearRect();
	
	if(loadedLayer != undefined){
		loadedLayer.clearLayers();
		map.removeLayer(loadedLayer);
		loadedLayer = undefined;
	}
  disableBtn();
}

function deleteButton(){
	document.getElementById("minLat").value = "";
	document.getElementById("maxLat").value = "";
	document.getElementById("minLong").value = "";
	document.getElementById("maxLong").value = "";
	clearData();
	clearRect();
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
  //delete a rectangle annimation on the map
  rectAnnimation();
  
  //drawing rectangle
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

/**
  * Delete a rectangle annimation on the map
  */
function rectAnnimation(){
  document.getElementById("rect-pop").style.display = "none";
}

/**
  * UnDisable download button when have statictics summary
  */
function undisableBtn() {
  document.getElementById("dwn-btn").disabled = false;
}
/**
  * Disable download button when don't have statictics summary
  */
function disableBtn() {
    document.getElementById("dwn-btn").disabled = true;
}
