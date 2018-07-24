
/**
  * Download statistics summary
  *
  * The Tag definition
  *
  *@param{String} filenme - the name of the file
  *@param {String} text - the content of the file
  */
function download(filename, text){
	var element = document.createElement('a');
	element.setAttribute('href','data:text/plain;charset=utf8,' + encodeURIComponent(text));
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
  
    // Generate download of statistics.txt file with some content
    var text = document.getElementById("statsOutput").value;
    var filename = "statistics.txt";
    
    download(filename, text);
}, false);
