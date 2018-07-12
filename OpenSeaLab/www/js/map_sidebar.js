var map = L.map('map', {zoomControl:true}).setView([45.433373, -4.441717], 10);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

//create a new dictionary for feature colors
let dictionary = new Map();

/*console.log("Loading...");

$.getJSON("http://127.0.0.1:8080/seabed", {action:"getGeoJSON"}, function(json){
	console.log("LOADED!");
	console.log(json);
	L.geoJson(json, {
		style: GetColor, 	
	   onEachFeature: function (feature, layer) {
	   		//var seeArea = L.GeometryUtil.geodesicArea(layer.getLatLngs());
	   		if(feature.properties.WEB_DESC){
	   			layer.bindPopup( feature.properties.WEB_DESC);
	   		}else{
	   			console.log("No WEB_DESC feature");
	   		}
	       
	   }
		//add here return color depend on feature
	}).addTo(map);


	
	
	console.log("Added to map");

});*/

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

var states = [{"type": "Feature","bbox": [45.433373,-4.441717,45.437539,-4.43505],"geometry": { "type": "Polygon","coordinates": [[ [-4.43505, 45.434623], [-4.441717, 45.433373], [-4.439634, 45.437539], [-4.43505, 45.434623] ]] }, "properties": { "OBJECTID": "452833", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic lower abyssal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.422175,-4.440155,45.429206,-4.430259],"geometry": { "type": "Polygon","coordinates": [[ [-4.430259, 45.423998], [-4.440155, 45.422175], [-4.43755, 45.429206], [-4.4313, 45.429206], [-4.430259, 45.423998] ]] }, "properties": { "OBJECTID": "452857", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic lower abyssal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}];



L.geoJson(states, {
	  
	       style: function(feature) {return {color: "#ff0000"};}
	   ,
	   onEachFeature: function (feature, layer) {
	   		var seaArea = geodesicArea(layer.getLatLngs());
	   		if(feature.properties.WEB_CLASS){
	   			var list = "<dd>" + feature.properties.Allcomb + "</dd>"
		           + "<dt>Area : </dt>"
		           + seaArea
		           
	   			 layer.bindPopup( list );
	   			}
	      
	   }
	}).addTo(map);




var boundry1 = null;
var boundry2 = null;
var drawnRec = false;
var clicked = false;

function selectCordRectangle() {

	clicked = true;
		map.on('click', function(e) {
			if(clicked){
				 if(boundry1 == null){
			    	boundry1 = e.latlng;
			    	document.getElementById("recBound1").innerHTML = "Boundry 1 Lat, Lon : "  + boundry1.lat + ", " + boundry1.lng;
			    	drawnRec = false;
			    } else if(boundry2 == null && e.latlng != boundry1){
			    	boundry2 = e.latlng;
			    	document.getElementById("recBound2").innerHTML = "Boundry 2 Lat, Lon : "  + boundry2.lat + ", " + boundry2.lng;
			    	drawnRec = false;
			    	clicked = false;
			    }	
			}
  
		});
	

    
}

function clearCordRectangle(){
	boundry1 = null;
	boundry2 = null;
	drawnRec = false;
	document.getElementById("recBound1").innerHTML = "";
	document.getElementById("recBound2").innerHTML = "";
}

function drawRectangle(){
	
	if(boundry1 != null && boundry2 != null && !drawnRec){
    	rectangle =  new L.rectangle([  [boundry1.lat, boundry1.lng], [boundry2.lat, boundry2.lng]]);
		map.addLayer(rectangle);
		drawnRec = true;
		
    }else{
    	alert("Select all the bounds!");
    }
    
}

function deleteRectangle(){
	map.removeLayer(rectangle);
	boundry1 = null;
	boundry2 = null;
	drawnRec = false;
	document.getElementById("recBound1").innerHTML = "";
	document.getElementById("recBound2").innerHTML = "";
    
}


function GetRandomInt(min, max){
	return Math.floor(Math.random()*(max - min + 1) + min);
}

function GetColor(feature){
	if(dictionary.has(feature)) return dictionary.get(feature);
	else {
		var r = GetRandomInt(0, 255);
		var g = GetRandomInt(0, 255);
		var b = GetRandomInt(0, 255);
		var color = "rgb(" + r + "," + g + "," + b + ")";
		dictionary.set(feature,color);
		return color;
	}

}

