var map = L.map('map', {zoomControl:true}).setView([39.7392, -94.9847], 4);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

function drawRectangle() {
    document.getElementById("drawRecButton").style.color = "red";


    map.on('click', function(e) {
	    
	    document.getElementById("drawRecButton").style.color = "blue";
	    document.getElementById("recBound1").innerHTML = "Lat, Lon : "  + e.latlng.lat + ", " + e.latlng.lng;
	});

    rectangle =  new L.rectangle([  [17.853290, 34.980469], [10.876465, 14.853516]]);
	map.addLayer(rectangle);
}