ACC.minicart = {
	
	$layer:$('#miniCartLayer'),

	bindMiniCart: function ()
	{
		$(document).on('mouseenter', '.miniCart', this.showMiniCart);
		$(document).on('mouseleave', '.miniCart', this.hideMiniCart);
	},
	
	showMiniCart: function ()
	{
		if(parseInt($('.mini-cart-count').text())>5){$('.cartItemslisting').addClass('more') }else{$('.cartItemslisting').removeClass('more')};
		if(ACC.minicart.$layer.data("hover"))
		{			
			return;
		}
		
		if(ACC.minicart.$layer.data("needRefresh") != false){
			ACC.minicart.getMiniCartData(function(){
				ACC.minicart.$layer.fadeIn(function(){
					ACC.minicart.$layer.data("hover", true);
					ACC.minicart.$layer.data("needRefresh", false);
				});
			})
		}
		
		ACC.minicart.$layer.fadeIn(function(){
			ACC.minicart.$layer.data("hover", true);			
		});
	},
	
	hideMiniCart: function ()
	{
		ACC.minicart.$layer.fadeOut(function(){
			ACC.minicart.$layer.data("hover", false);
		});
	},
	
	getMiniCartData : function(callback)
	{ 
		$.ajax({
			url: ACC.minicart.$layer.attr("data-rolloverPopupUrl"),
			cache: false,
			type: 'GET',
			success: function (result)
			{
			  ACC.minicart.$layer.html(result);             
              $('#loading-cart').hide();
              callback();
			}
		});
		return false;
	},

	refreshMiniCartCount : function()
	{
		$.ajax({
			dataType: "json",
			url: ACC.minicart.$layer.attr("data-refreshMiniCartUrl") + Math.floor(Math.random() * 101) * (new Date().getTime()),
			success: function (data)
			{
				$(".miniCart .count, .mini-cart-count").html(data.miniCartCount);
				$(".miniCart .price").html(data.miniCartPrice);
				ACC.minicart.$layer.data("needRefresh", true);				
			}
		});
	}
};

$(document).ready(function ()
{
	//ACC.minicart.bindMiniCart();
});

