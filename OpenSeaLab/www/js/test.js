$(function() {
	console.log("hello!")
	$('#validateCoordinates').click(function(){
		$.ajax({
			url : '/seabed',
			type : 'GET',
			data : {
				action:"geoJSON"
			},
			success : function(response) {
				console.log("hello")
				console.log(response)
			},error : function(error){
				console.log("error")
				console.log(error)
			}
		});	
	})
})