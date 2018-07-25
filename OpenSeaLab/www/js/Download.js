
/**
  * Download statistics summary
  *
  * The Tag definition
  *
  *@param{String} filenme - the name of the file
  *@param {String} text - the content of the file
  */
function download(filename, texte){
	var element = document.createElement('a');
	element.setAttribute('href','data:text/plain;charset=utf8,' + encodeURIComponent(texte));
	element.setAttribute('download',filename);
	element.style.display ='none';
	document.body.appendChild(element);
	element.click();
	document.body.removeChild(element);
}

/**
  *Start file download.
  */
document.getElementById("dwn-btn").addEventListener("click", function(){

    // min Longitude , min Latitude , max Longitude , max Latitude
    var texte = document.getElementById("minLong").value +","+document.getElementById("minLat").value +","+document.getElementById("maxLong").value +","+document.getElementById("maxLat").value +"\n";

    var allChild = document.getElementsByClassName("statsValue");
    
    texte += (allChild.length).toString();
    console.log(allChild.length);
    for(var i = 0; i < allChild.length; i++){
        texte += (allChild[i].textContent).toString() + "\n";
    }
    
    // Generate download of statistics.txt file with some content    
    var filename = "statistics.txt";
    download(filename, texte);
}, false);
