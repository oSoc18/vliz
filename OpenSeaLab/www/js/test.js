$(function() {
	console.log("hello!")
	$('#validateCoordinates').click(function(){
		console.log($("#coord1").val())
		$.ajax({
			url : '/seabed',
			type : 'GET',
			data : {
				coord1: $('#coord1').val(),
				coord2: $('#coord2').val(),
				coord3: $('#coord3').val(),
				coord4: $('#coord4').val(),
			},
			success : function(response) {
				console.log("good")
			},error : function(error){
				
			}
		});	
	})
})