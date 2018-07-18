
$(function() {
	$('#validateCoordinates').click(function(){
		console.log($("#minLat").val())
		$.ajax({
			url : '127.0.0.1:8080/seabed',
			type : 'GET',
			data : {
				action:"getGeoJSON",
				minLat: $("#minLat").val()/*"51.428"*/,
				minLong: $("#minLong").val()/*"2.122"*/,
				maxLat: $("#maxLat").val()/*"51.548"*/,
				maxLong: $("#maxLong").val()/*"6.03"*/
				
			},
			success : function(response) {
				console.log("hello")
				console.log(response)
			},error : function(error){
				console.log("error")
				console.log($("#minLat").val())
				console.log($("#minLong").val())
				console.log($("#maxLat").val())
				console.log($("#maxLong").val())
				console.log(error)
			}
		});	
	})
})
