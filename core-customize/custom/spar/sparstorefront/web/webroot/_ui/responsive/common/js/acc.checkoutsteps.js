ACC.checkoutsteps = {

	_autoload: [
		"permeateLinks"
	],
			
	permeateLinks: function() {
	
		$(document).on("click",".js-checkout-step",function(e){
			e.preventDefault();
			if($(this).parent().hasClass('disabled')){return;}
			window.location=$(this).closest("a").attr("href")
		})		
	}


};