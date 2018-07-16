var map = L.map('map', {zoomControl:true}).setView([50.3791104480105, -2.19580078125], 5);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

//create a new dictionary for feature colors
let dictionary = new Map();

var testURL = "http://127.0.0.1:8080/seabed?action=getGeoJSON&minLat=51&maxLat=52&minLong=2&maxLong=3";

function loadDataFrom(url){
	
	$.getJSON(url, function(json){
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



var URLpart0 ="http://127.0.0.1:8080/seabed?action=getGeoJSON&minLat=";
var URLpart1="&maxLat=";
var URLpart2="&minLong=";
var URLpart3="&maxLong=";

var firstCoor;
map.on({mousedown : 
	function(e){
		if(e.originalEvent.ctrlKey){
			map.dragging.disable();
			firstCoor = e.latlng;
			console.log(firstCoor);
			console.log("Got first coor")
		}
	}
});

var polygon;
map.on({mouseup : 
	function(e){
		if(e.originalEvent.ctrlKey){
			map.dragging.enable();
			if(polygon != null){
				map.removeLayer(polygon);
 			}
 			var lastCoor = e.latlng;
			polygon = L.polygon([
				    firstCoor,
				    [firstCoor.lat, lastCoor.lng],
				    lastCoor,
				    [lastCoor.lat, firstCoor.lng]
				])
			polygon.addTo(map);
			//create url to get data
			var URLcoordinates = URLpart0.concat(firstCoor.lat,URLpart1.concat(lastCoor.lat,URLpart2.concat(firstCoor.lng,URLpart3)))+lastCoor.lng;						
			loadDataFrom(URLcoordinates);
		}
	}
});


function GetColor(feature){
	if(dictionary.has(feature.properties.WEB_CLASS)) return dictionary.get(feature.properties.WEB_CLASS);
	else {
		var color = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		dictionary.set(feature.properties.WEB_CLASS,color);
		
		return color;
	}

}

