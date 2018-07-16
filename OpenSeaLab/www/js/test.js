$(function() {
	$('#validateCoordinates').click(function(){
		$.ajax({
			url : '/seabed',
			type : 'GET',
			data : {
				action:"getGeoJSON",
				minLat: "51.428",
				minLong: "2.122",
				maxLat: "51.548",
				maxLong: "6.03"
				
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