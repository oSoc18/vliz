$(function() {
	console.log("hello!")
	$('#validateCoordinates').click(function(){
		$.ajax({
			url : '/seabed',
			type : 'GET',
			success : function(response) {
				console.log("hello")
				console.log(response)
				console.log(JSON.parse(response))
			},error : function(error){
				console.log("error")
				console.log(error)
			}
		});	
	})
})