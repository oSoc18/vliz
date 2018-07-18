
$(function() {
	$('#bath').click(function(){
		$.ajax({
			url : '/bathymetry',
			type : 'GET',
			data : {
				action:"getStats",
				minLat: "51.428",
				minLong: "2.122",
				maxLat: "51.548",
				maxLong: "3.03"
				
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
