$(function() {
	$('#validateCoordinates').click(function(){
		$.ajax({
			url : '/seabed',
			type : 'GET',
			data : {
				action:"getGeoJSON",
				minLat: "42.155978345541975",
				minLong: "-31.54521798022688",
				maxLat: "52.39094513355302",
				maxLong: "0.9743132697731198"
				
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