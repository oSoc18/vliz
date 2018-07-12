var map = L.map('map', {zoomControl:true}).setView([50.3791104480105, -2.19580078125], 5);

L.tileLayer.provider('Esri.OceanBasemap').addTo(map);

//create a new dictionary for feature colors
let dictionary = new Map();

console.log("Loading...");

$.getJSON("http://127.0.0.1:8080/seabed?action=getGeoJSON&minLat=51&maxLat=52&minLong=2&maxLong=3", function(json){
	console.log("LOADED!");
	console.log(json);
	L.geoJson(json, {	  
	       style: function(feature) {return {color: GetColor(feature) } ; }
	   ,
	   onEachFeature: function (feature, layer) {
		var seaarea = 0
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
					+ seaArea 
		layer.bindPopup( list );  
	   }
	}).addTo(map);
	
	
	console.log("Added to map");

});

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
/*
var states = [{"type": "Feature","bbox": [45.433373,-4.441717,45.437539,-4.43505],"geometry": { "type": "Polygon","coordinates": [[ [-4.43505, 45.434623], [-4.441717, 45.433373], [-4.439634, 45.437539], [-4.43505, 45.434623] ]] }, "properties": { "OBJECTID": "452833", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic lower abyssal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.422175,-4.440155,45.429206,-4.430259],"geometry": { "type": "Polygon","coordinates": [[ [-4.430259, 45.423998], [-4.440155, 45.422175], [-4.43755, 45.429206], [-4.4313, 45.429206], [-4.430259, 45.423998] ]] }, "properties": { "OBJECTID": "452857", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic lower abyssal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.331289,-2.527137,45.362539,-2.514637],"geometry": { "type": "Polygon","coordinates": [[ [-2.520192, 45.361151], [-2.517762, 45.354206], [-2.515679, 45.337539], [-2.514637, 45.331289], [-2.519498, 45.332678], [-2.521929, 45.337539], [-2.524012, 45.341706], [-2.526095, 45.352123], [-2.527137, 45.362539], [-2.520192, 45.361151] ]] }, "properties": { "OBJECTID": "452943", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.328512,-2.559863,45.362105,-2.544845],"geometry": { "type": "Polygon","coordinates": [[ [-2.559863, 45.362105], [-2.55422, 45.357331], [-2.551095, 45.352123], [-2.549012, 45.347956], [-2.546929, 45.341706], [-2.544845, 45.335456], [-2.545192, 45.328512], [-2.550748, 45.328512], [-2.55422, 45.332331], [-2.557345, 45.339623], [-2.559428, 45.356289], [-2.559863, 45.362105] ]] }, "properties": { "OBJECTID": "452947", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.327123,-2.544845,45.360977,-2.527137],"geometry": { "type": "Polygon","coordinates": [[ [-2.544324, 45.360977], [-2.537727, 45.357678], [-2.534429, 45.352123], [-2.532345, 45.343789], [-2.530262, 45.337539], [-2.528179, 45.333373], [-2.527137, 45.327123], [-2.537554, 45.327123], [-2.538595, 45.335456], [-2.540679, 45.341706], [-2.542762, 45.345873], [-2.544845, 45.350039], [-2.544324, 45.360977] ]] }, "properties": { "OBJECTID": "452948", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.30681,-3.230261,45.329727,-3.200052],"geometry": { "type": "MultiPolygon","coordinates": [[[ [-3.203872, 45.311324], [-3.200052, 45.307678], [-3.205261, 45.30754], [-3.203872, 45.311324] ]], [[ [-3.203872, 45.311324], [-3.210469, 45.309415], [-3.219584, 45.30681], [-3.226962, 45.309067], [-3.230261, 45.314623], [-3.228698, 45.329727], [-3.225747, 45.326428], [-3.220191, 45.323651], [-3.213941, 45.319484], [-3.207691, 45.315317], [-3.203872, 45.311324] ]]] }, "properties": { "OBJECTID": "452955", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic upper bathyal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.30681,-3.219584,45.311324,-3.203872],"geometry": { "type": "Polygon","coordinates": [[ [-3.205261, 45.30754], [-3.219584, 45.30681], [-3.210469, 45.309415], [-3.203872, 45.311324], [-3.205261, 45.30754] ]] }, "properties": { "OBJECTID": "452956", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlanto-Mediterranean mid bathyal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.293078,-3.185469,45.298446,-3.179832],"geometry": { "type": "Polygon","coordinates": [[ [-3.179832, 45.293078], [-3.185469, 45.29379], [-3.183876, 45.298446], [-3.179832, 45.293078] ]] }, "properties": { "OBJECTID": "452961", "WEB_DESC": "A6.3: Deep-sea sand", "Allcomb": "Atlantic upper bathyal sand", "WEB_CLASS": "A6.3" }}, {"type": "Feature","bbox": [45.284284,-3.183876,45.298446,-3.164636],"geometry": { "type": "MultiPolygon","coordinates": [[[ [-3.179832, 45.293078], [-3.172275, 45.292401], [-3.168803, 45.288581], [-3.164636, 45.284299], [-3.171667, 45.284284], [-3.176655, 45.288822], [-3.179832, 45.293078] ]], [[ [-3.179832, 45.293078], [-3.183876, 45.298446], [-3.179219, 45.296915], [-3.179832, 45.293078] ]]] }, "properties": { "OBJECTID": "452969", "WEB_DESC": "A6: Deep-sea bed", "Allcomb": "Atlantic upper bathyal coarse sediment", "WEB_CLASS": "DSeaCS" }}, {"type": "Feature","bbox": [45.274345,-3.171667,45.284299,-3.154914],"geometry": { "type": "Polygon","coordinates": [[ [-3.168282, 45.27876], [-3.171667, 45.284284], [-3.164636, 45.284299], [-3.161511, 45.28129], [-3.158386, 45.278165], [-3.154914, 45.274345], [-3.163247, 45.274345], [-3.168282, 45.27876] ]] }, "properties": { "OBJECTID": "452976", "WEB_DESC": "A6.4: Deep-sea muddy sand", "Allcomb": "Atlantic upper bathyal sandy mud to muddy sand", "WEB_CLASS": "A6.4" }}, {"type": "Feature","bbox": [45.25754,-2.353179,45.29379,-2.343804],"geometry": { "type": "Polygon","coordinates": [[ [-2.343804, 45.27556], [-2.343804, 45.26879], [-2.347554, 45.25754], [-2.351095, 45.264623], [-2.353179, 45.283373], [-2.353179, 45.289623], [-2.350054, 45.29379], [-2.346929, 45.289623], [-2.343804, 45.27556] ]] }, "properties": { "OBJECTID": "452983", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.25004,-2.291721,45.265456,-2.282346],"geometry": { "type": "Polygon","coordinates": [[ [-2.283804, 45.265456], [-2.282346, 45.260456], [-2.284429, 45.25629], [-2.286859, 45.251428], [-2.291721, 45.25004], [-2.290679, 45.260456], [-2.289637, 45.264623], [-2.283804, 45.265456] ]] }, "properties": { "OBJECTID": "452985", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.249779,-2.343804,45.285456,-2.332346],"geometry": { "type": "Polygon","coordinates": [[ [-2.343804, 45.26879], [-2.343804, 45.27556], [-2.340679, 45.28129], [-2.337554, 45.285456], [-2.334429, 45.28129], [-2.332346, 45.27504], [-2.332346, 45.264623], [-2.334429, 45.254206], [-2.337814, 45.249779], [-2.343804, 45.26879] ]] }, "properties": { "OBJECTID": "452987", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.240665,-2.286859,45.264623,-2.266721],"geometry": { "type": "Polygon","coordinates": [[ [-2.281304, 45.240665], [-2.286859, 45.241012], [-2.285471, 45.245873], [-2.279915, 45.248651], [-2.27436, 45.251428], [-2.271929, 45.25629], [-2.269846, 45.260456], [-2.266721, 45.264623], [-2.266721, 45.253165], [-2.270193, 45.247262], [-2.275748, 45.244484], [-2.281304, 45.240665] ]] }, "properties": { "OBJECTID": "452993", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.238929,-2.311512,45.279206,-2.301096],"geometry": { "type": "Polygon","coordinates": [[ [-2.301096, 45.26254], [-2.301096, 45.25629], [-2.302311, 45.240317], [-2.309082, 45.238929], [-2.311512, 45.24379], [-2.311512, 45.26254], [-2.309429, 45.266706], [-2.306304, 45.271915], [-2.302137, 45.279206], [-2.301096, 45.26254] ]] }, "properties": { "OBJECTID": "452995", "WEB_DESC": "A5.27: Deep circalittoral sand", "Allcomb": "A5.27", "WEB_CLASS": "A5.27" }}, {"type": "Feature","bbox": [45.2354,-2.044638,45.332331,-1.949013],"geometry": { "type": "Polygon","coordinates": [[ [-1.995327, 45.2354], [-2.003527, 45.240317], [-2.009082, 45.243095], [-2.012554, 45.248998], [-2.015679, 45.254206], [-2.017763, 45.258373], [-2.019846, 45.26254], [-2.021929, 45.266706], [-2.024013, 45.272956], [-2.026096, 45.277123], [-2.028179, 45.28129], [-2.030263, 45.28754], [-2.032346, 45.291706], [-2.034429, 45.295873], [-2.036513, 45.302123], [-2.038596, 45.30629], [-2.042242, 45.311498], [-2.044638, 45.315665], [-2.039638, 45.317748], [-2.035471, 45.319831], [-2.029915, 45.323651], [-2.025054, 45.326081], [-2.018804, 45.328164], [-2.010471, 45.330248], [-2.002138, 45.332331], [-1.981304, 45.332331], [-1.976443, 45.329901], [-1.970193, 45.325734], [-1.963943, 45.321567], [-1.961166, 45.318095], [-1.954395, 45.313928], [-1.951096, 45.308373], [-1.949013, 45.304206], [-1.94936, 45.288928], [-1.954916, 45.286151], [-1.962381, 45.280595], [-1.966721, 45.276081], [-1.970888, 45.271915], [-1.975054, 45.267748], [-1.978527, 45.263928], [-1.985297, 45.259762], [-1.989638, 45.253165], [-1.993804, 45.248998], [-1.995327, 45.2354] ]] }, "properties": { "OBJECTID": "452996", "WEB_DESC": "A5.15: Deep circalittoral coarse sediment", "Allcomb": "A5.15", "WEB_CLASS": "A5.15" }}];



L.geoJson(states, {	  
	       style: function(feature) {return {color: GetColor(feature) } ; }
	   ,
	   onEachFeature: function (feature, layer) {
		var seaarea = 0
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
					+ seaArea 
		layer.bindPopup( list );  
	   }
	}).addTo(map);*/



var firstCoor;
map.on({mousedown : 
	function(e){
		if(e.originalEvent.ctrlKey){
			map.dragging.disable();
			firstCoor = e.latlng;
			console.log(firstCoor);
			console.log("Gor first coor")
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
		}
	}

});

function deleteRectangle(){
	map.removeLayer(polygon);    
}

function GetColor(feature){
	if(dictionary.has(feature.properties.WEB_CLASS)) return dictionary.get(feature.properties.WEB_CLASS);
	else {
		var color = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		dictionary.set(feature.properties.WEB_CLASS,color);
		
		return color;
	}

}

