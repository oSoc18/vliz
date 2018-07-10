$(function() {
	$.ajax({
		url : '/seabed',
		type : 'GET',
		data : {
			action :"stat"
		},
		success : function(response, status) {
			console.log("good")
		},error : function(error, status){
			
		}
	});

})