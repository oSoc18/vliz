var map = L.map('map', {zoomControl:true}).setView([39.7392, -94.9847], 4);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

var boundry1 = null;
var boundry2 = null;
var drawnRec = false;

function selectCordRectangle() {

    map.on('click', function(e) {
	    if(boundry1 == null){
	    	document.getElementById("recBound1").innerHTML = "Boundry 1 Lat, Lon : "  + e.latlng.lat + ", " + e.latlng.lng;
	    	boundry1 = e.latlng;
	    } else if(boundry2 == null){
	    	document.getElementById("recBound2").innerHTML = "Boundry 2 Lat, Lon : "  + e.latlng.lat + ", " + e.latlng.lng;
	    	boundry2 = e.latlng;
	    }	    
	});
    
}

function clearCordRectangle(){
	var boundry1 = null;
	var boundry2 = null;
	var drawnRec = false;
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
    
}