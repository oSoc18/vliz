var map = L.map('map', {zoomControl:true}).setView([45.423998, -4.430259], 5);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

//create a new dictionary for feature colors
let dictionary = new Map();

/*var states = [{
"type": "Feature",
"properties": {"party": "Republican"},
"geometry": {
    "type": "Polygon",
    "coordinates": [ [
    [-4.430259, 45.423998], [-4.440155,45.422175], [-4.43755,45.429206], [-4.4313,45.429206], [-4.430259, 45.423998] 
    ] ]
}
}, {
"type": "Feature",
"properties": {"party": "Democrat"},
"geometry": {
    "type": "Polygon",
    "coordinates": [[
        [-109.05, 41.00],
        [-102.06, 40.99],
        [-102.03, 36.99],
        [-109.04, 36.99],
        [-109.05, 41.00]
    ]]
}
}];*/

/*L.geoJson(states, {
style: function(feature) {
        return {color: "#ff0000"};

}
}).addTo(map);*/

console.log("Loading...");

$.getJSON("http://127.0.0.1:8080/seabed", function(json){
	console.log("LOADED!");
	console.log(json);
	L.geoJson(json, {
		style: GetColor(); 
		//function(feature) {return "#00ff00";}
	}).addTo(map);
	
	console.log("Added to map");

});

var myStyle = {"color": "#ff7800", "weight": 4, "opacity": 0.65};



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

