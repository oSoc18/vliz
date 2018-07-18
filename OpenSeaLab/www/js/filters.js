function boxCheck(id){
	if(id== Seabed){
		document.getElementById("AddFilter").innerHTML = " Add  Seabed filter here."
	}
	else{
		if(id == Bathymetry){
			document.getElementById("AddFilter").innerHTML = " Add  Bathymetry filter here."
		}
	}
	
}