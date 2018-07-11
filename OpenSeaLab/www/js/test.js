$(function() {)
	$('#validateCoordinates').click(function(){
		$.ajax({
			url : '/seabed',
			type : 'GET',
			data : {
				action:"getGeoJSON",
				minLat: val,
				minLong: val,
				maxLat: val,
				maxLong: val
				
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