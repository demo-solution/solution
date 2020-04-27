ACC.carousel = {

	_autoload: [
		["bindCarousel", $(".js-owl-carousel").length >0]
	],

	carouselConfig:{
		"default":{
			navigation:true,
			navigationText : ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
			pagination:false,
			itemsCustom : [[0, 1], [640, 2], [1024, 3], [1400, 7]]
		},
		"rotating-image":{
			navigation:false,
			pagination:true,
			singleItem : true,
		},
		"lazy-reference":{
			navigation:true,
			navigationText : ["<!--span class='glyphicon glyphicon-chevron-left'></span-->", "<!--span class='glyphicon glyphicon-chevron-right'></span-->"],
			pagination:false,
			items:4,
			/*itemsDesktop : [5000,7], 
			itemsDesktopSmall : [1200,3], 
			itemsTablet: [768,4], */
			itemsMobile : [480,2], 
			lazyLoad:true,		
		},
		"PLP-reference":{
			navigation:true,
			navigationText : ["<!--span class='glyphicon glyphicon-chevron-left'></span-->", "<!--span class='glyphicon glyphicon-chevron-right'></span-->"],
			pagination:false,
			items:5,
			/*itemsDesktop : [5000,7], 
			itemsDesktopSmall : [1200,3], 
			itemsTablet: [768,4], 
			itemsMobile : [480,3],*/ 
			lazyLoad:true,		
		}
	},

	bindCarousel: function(){
		
		$(".js-owl-carousel").each(function(){
			var $c = $(this)
			$.each(ACC.carousel.carouselConfig,function(key,config){
				//console.log($('.plpCarousel').is(":visible")+':::'+key)
				if($c.hasClass("js-owl-"+key)){					
					var $e = $(".js-owl-"+key);
					$e.owlCarousel(config);
				}
			});
		});

	}

};