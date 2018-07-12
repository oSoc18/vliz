
//* Loop through all dropdown buttons to toggle between hiding and showing its dropdown content - This allows the user to have multiple dropdowns without any conflict */
var dropdown = document.getElementsByClassName("dropdown-btn");
var i;

for (i = 0; i < dropdown.length; i++) {
  dropdown[i].addEventListener("click", function() {
    this.classList.toggle("active");
    var dropdownContent = this.nextElementSibling;
    if (dropdownContent.style.display === "block") {
      dropdownContent.style.display = "none";
    } else {
      dropdownContent.style.display = "block";
    }
  });
}


   var map = L.map('map', {drawControl: true}).setView([51.505, -0.09], 1);
     L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
         attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
     }).addTo(map);
	 
   map.on('click', function(e) {
        document.getElementById("recBound1").innerHTML = "Lat, Lon : "  + e.latlng.lat + ", " + e.latlng.lng;
    });
	
	var startcoor;
	map.on({mousedown: function(e){
	
		if(e.originalEvent.ctrlKey){
		console.log(e);
			console.log('Button press');
			startcoor = e.latlng;
			map.dragging.disable();
		}
	}});
	map.on({mouseup: function(e){
		if(e.originalEvent.ctrlKey){
			console.log('Button reseased');
			var endCoor = e.latlng;
			
			var polygone = L.polygon([
				startcoor,
				[startcoor.lat,endCoor.lng],
				endCoor,
				[endCoor.lat, startcoor.lng],
			]);
			map.addLayer(polygone);
			map.dragging.enable();
		}
	}})
	
function drawRectangle() {
   document.getElementById("drawRecButton").style.color = "red";

   map.on('click', function(e) {
        
        document.getElementById("drawRecButton").style.color = "blue";
        document.getElementById("recBound1").innerHTML = "Lat, Lon : "  + e.latlng.lat + ", " + e.latlng.lng;
    });

   rectangle =  new L.rectangle([  [17.853290, 34.980469], [10.876465, 14.853516]]);
    map.addLayer(rectangle);
	
	var dimRectangle = rctangle.getLatLngs();
}

//* Loop through all dropdown buttons to toggle between hiding and showing its dropdown content - This allows the user to have multiple dropdowns without any conflict */
var dropdown = document.getElementsByClassName("dropdown-btn");
var i;

for (i = 0; i < dropdown.length; i++) {
  dropdown[i].addEventListener("click", function() {
    this.classList.toggle("active");
    var dropdownContent = this.nextElementSibling;
    if (dropdownContent.style.display === "block") {
      dropdownContent.style.display = "none";
    } else {
      dropdownContent.style.display = "block";
    }
  });
}

function openNav(){
	document.getElementById("mySidenav").style.width="250px";
	document.getElementById("main").style.marginLeft = "250px";
	
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
    document.getElementById("main").style.marginLeft= "0";
}