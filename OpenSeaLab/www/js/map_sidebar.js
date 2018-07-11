var map = L.map('map', {zoomControl:true}).setView([39.7392, -94.9847], 4);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

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