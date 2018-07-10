$(function() {
	$.ajax({
		url : '/seabed',
		type : 'GET',
		data : {
			action :"stat"
		},
		success : function(response) {
			console.log("good")
		},error : function(error){
			
		}
	});

})