ACC.cartitem = {

_autoload :
[ "bindCartItem"
],

bindCartItem : function() {
	
	$('#viewcartLayout').on("click", '.remove-entry-button', function(e) {
		e.preventDefault();		
		updateViewCart($(this).attr('id').split("_")[1], true);
	});
	
	$('#viewcartLayout')
	    .on("change", '.update-entry-quantity-input', function(e) {
		    //var maxOrderQuantity=$(this).parents('form').find('.productMaxOrderQuantity').val();
		    //if($(this).val()> maxOrderQuantity){$(this).val(maxOrderQuantity)}
		    updateViewCart($(this).parents('.updateForm').attr('id').split("updateCartForm")[1], false);
	    });
	$('#viewcartLayout').on("blur", '.update-entry-quantity-input', function(e) {
		e.stopPropagation();
		e.preventDefault();
	})

	$('#viewcartLayout')
	    .on("click", '.js-qty-selector-plus, .js-qty-selector-minus', function(e) {
		    e.stopPropagation();
		    e.preventDefault();
		    var initialVal=$(this).parents('form').find('.update-entry-quantity-input').val();
		    var curntIndx = $(this).parents('.updateForm').attr('id').split("updateCartForm")[1];
		    var curntVal = $('#updateCartForm' + curntIndx + ' input[name=quantity]').val();
		    var maxOrderQuantity = $('#updateCartForm' + curntIndx + ' .productMaxOrderQuantity').val();
		    if ($(this).hasClass('js-qty-selector-minus')) {
			    if (curntVal == 1) { return; }
			    curntVal--;
		    }
		    else {			    
			    curntVal++;
			    $('#loading-image').show();
				$('#cboxOverlay').show();
		    }
		    $('#updateCartForm' + curntIndx + ' input[name=quantity]').val(curntVal);		   	   
		     updateViewCart(curntIndx, false);
	    });
},

checkQtySelector : function(self, mode) {
	var input = $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
	var inputVal = parseInt(input.val());
	var max = input.data("max");
	
	var minusBtn = $(self).parents(".js-qty-selector").find(".cart-js-qty-selector-minus");
	var plusBtn = $(self).parents(".js-qty-selector").find(".cart-js-qty-selector-plus");
	
	$(self).parents(".js-qty-selector").find(".btn").removeAttr("disabled");
	
	if (mode == "minus") {
		if (inputVal != 1) {
			ACC.productDetail.updateQtyValue(self, inputVal - 1)
			if (inputVal - 1 == 1) {
				minusBtn.attr("disabled", "disabled")
			}
			
		}
		else {
			minusBtn.attr("disabled", "disabled")
		}
	}
	else if (mode == "reset") {
		ACC.productDetail.updateQtyValue(self, 1)

	}
	else if (mode == "plus") {
		if (inputVal != max) {
			ACC.productDetail.updateQtyValue(self, inputVal + 1)
			if (inputVal == max) {
				plusBtn.attr("disabled", "disabled")
			}
		}
		else {
			plusBtn.attr("disabled", "disabled")
		}
	}
	else if (mode == "input") {
		if (inputVal == 1) {
			$(self).parents(".js-qty-selector").find(".js-qty-selector-minus")
			    .attr("disabled", "disabled")
		}
		else if (inputVal == max) {
			//ACC.productDetail.updateQtyValue(self, max);
			$(self).parents(".js-qty-selector").find(".js-qty-selector-plus").attr("disabled", "disabled")
		}
		else if (inputVal < 1) {
			ACC.productDetail.updateQtyValue(self, 1)
			$(self).parents(".js-qty-selector").find(".js-qty-selector-minus")
			    .attr("disabled", "disabled")
		}
		else if (inputVal >= max) {
			ACC.productDetail.updateQtyValue(self, max);
			$(self).parents(".js-qty-selector").find(".js-qty-selector-plus").attr("disabled", "disabled")
		}
	}
	
},
};

function updateViewCart(param, boolen) {
	var form = $('#updateCartForm' + param);
	var productCode = form.find('input[name=productCode]').val();
	var initialCartQuantity = form.find('input[name=initialQuantity]');
	var cartQuantity = form.find('input[name=quantity]');
	var newCartQuantity = form.find('input[name=quantity]').val();
	if (boolen) {
		ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val());
		cartQuantity.val(0);
		initialCartQuantity.val(0);
	}
	else {
		if (initialCartQuantity != newCartQuantity) {
			
			ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity);
		}
	}
	$.ajax({
	type : "post",
	url : ACC.config.encodedContextPath + '/cart/update',
	data : form.serialize(),
	beforeSend: function(){
		 	$('#loading-image').show();
			$('#cboxOverlay').show();
	},
	success : function(data) {
		$('#viewcartLayout').html($(data).find('#viewcartLayout').html());
		ACC.minicart.refreshMiniCartCount();
		var a = $(data).find('.global-alerts')
		if (a) {
			$('.global-alerts').remove();
			$('#noStoreSelected').before(a)
		}
		$('#loading-image').hide();
		$('#cboxOverlay').hide();
		window.location.reload();
	}
	});
}