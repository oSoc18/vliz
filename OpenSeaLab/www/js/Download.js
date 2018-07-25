
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
  /*
    var texte = "";

    var allChild = document.getElementsByClassName("wrapper");
    
    for(var i = 0;allChild.length -1; i++){
        texte.push(allChild[i].textContent);
    }
    */
    // Generate download of statistics.txt file with some content    
    var texte = document.getElementById("statsOutput").textContent;
    var filename = "statistics.txt";
    
    download(filename, texte);
}, false);
