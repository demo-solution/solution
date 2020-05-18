var promoMsgImgAry = {
bestDeal : "BestDeal.jpg",
combiOffer : "NewCombiOffer.jpg",
regularOffer : "NewRegularOffer.jpg",
}
ACC.product = {

_autoload :
[ "initPLPPageEvents", "bindToAddToCartForm", "enableStorePickupButton", "enableAddToCartButton", 
  "enableVariantSelectors", "bindFacets",'removeDuplicate','productPercentage', 'productBogoMsg',"lazyLoading",'removePriceUnavialablePDP'],

bindFacets : function() {
	$(document).on("click", ".js-show-facets", function(e) {
		e.preventDefault();
		
		ACC.colorbox.open("Select Refinements", {
		href : "#product-facet",
		inline : true,
		width : "320px",
		onComplete : function() {
			
			$(document).on("click", ".js-product-facet .js-facet-name", function(e) {
				e.preventDefault();
				$(".js-product-facet  .js-facet").removeClass("active");
				$(this).parents(".js-facet").addClass("active");
				$.colorbox.resize()
			})
		},
		onClosed : function() {
			$(document).off("click", ".js-product-facet .js-facet-name");
		}
		});
	});
	
	enquire.register("screen and (min-width:" + screenSmMax + ")", function() {
		$("#cboxClose").click();
	});
	
},
removeDuplicate:function(){	
	$('.product-image-wrapper .single-products').each(function(){	  		
		if($(this).find('.var-selector').find('.item-description').length !== 0){			
			var str=$(this).find('.var-selector').find('.item-description').text();		// Full String
		    var remainingStr=$(this).find('.item-description').find('.qty').text(); 	// Description String without Price
		    var $textWapper = $(this).find('.var-selector').find('.item-description');  // Description String Wrapper Selector
		    var priceValue=str.substring(str.lastIndexOf("-")); 						// Getting Price Value
		    var strArray = remainingStr.split(" ");										// Convert String to Array
		    var strIndex = strArray.indexOf('-');										// Match Case '-'
		    var strIndexX = strArray.indexOf('X');										// Match Case 'X'
		    
		    if(remainingStr.match(/-/)){			    	
			    if(strIndex === 1){
			    	if(strArray.length > 1){
			    		var newStr = strArray[0].toUpperCase() + " " + priceValue;			    		
			    		$textWapper.text(newStr);
			    	}
			    }else if(strIndex === 2 || strIndex > 2){			    	
			    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + priceValue;
			    	$textWapper.text(newStr);
			    }else if(strIndexX === 1){
			    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + strArray[2].toUpperCase() + " " + priceValue;
			    	$textWapper.text(newStr);
			    }
			    else if(strIndexX === 2){
			    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + strArray[2].toUpperCase() + " " + strArray[3].toUpperCase() + " " + priceValue;
			    	$textWapper.text(newStr);
			    }
			    else {
			    	if(strIndex === -1){	    	
				    	if(strArray.length > 1){		    		
				    		var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + priceValue;
				    		$textWapper.text(newStr);
				    	}
				    }
			    }
		    }else if(remainingStr.match(/^[A-Z]/i)) {		    	
		    	 var num = remainingStr.match(/-?\d+\.?\d*/);
		    	 var productQnty=remainingStr.substring(remainingStr.lastIndexOf(num));
		    	     productQnty=productQnty.split(" ").splice(-1);    	 
		    	 var newStr = productQnty + " " + priceValue;
		    	 	 $textWapper.text(newStr);
		    }else{
				if(strIndex === -1){	    	
				    	if(strArray.length > 1){		    		
				    		var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + priceValue;
				    		$textWapper.text(newStr);
				    	}
				  }
			}		
			
		}else{
			$(this).find('.var-selector').find('option').each(function(){	
					var newStr;
					var str=$(this).text();										// Full String	    		   
				    var priceValue=str.substring(str.lastIndexOf("-")); 		// Getting Price Value
				    	priceValue = priceValue.split(" ");			    
				    var strArray = str.split(" ");							    // Convert String to Array			    
				    var strIndex = strArray.indexOf('-');						// Match Case '-'
				    var strIndexX = strArray.indexOf('X');						// Match Case 'X'	
			    	
			    	if(strIndex === 1){				    	
			    		 	newStr = strArray[0].toUpperCase() + " " + priceValue[0];				   
					    	strArray = newStr.slice(0);
					    	strArray = strArray + " ";	
					    	$(this).text(strArray);
				    }else if(strIndex === 2){
				    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + priceValue[0];
				    	strArray = newStr.slice(0);
				    	strArray = strArray + " ";	
				    	$(this).text(strArray);
				    }else if(strIndexX === 1){
				    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + strArray[2].toUpperCase() + " " + priceValue[0];
				    	strArray = newStr.slice(0);
				    	strArray = strArray + " ";	
				    	$(this).text(strArray);
				    }
				    else if(strIndexX === 2){
				    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + strArray[2].toUpperCase() + " " + strArray[3].toUpperCase() + " " + priceValue[0];
				    	strArray = newStr.slice(0);
				    	strArray = strArray + " ";	
				    	$(this).text(strArray);
				    }else if(str.match(/^[A-Z]/i)) {	    	     		    
   			    	 var num = str.match(/-?\d+\.?\d*/);
			    	 var productQnty=str.substring(str.lastIndexOf(num));
			    	     productQnty=productQnty.split(" ").splice(-1);    	 
			    	 var newStr = productQnty + " " + priceValue[0];
			    	 	strArray = newStr.slice(0);
				    	strArray = strArray + " ";	
				    	$(this).text(strArray);
			    }else if(strIndexX === 0){
			    	var newStr = strArray[0].toUpperCase() + " " + strArray[1].toUpperCase() + " " + priceValue[0];
			    	strArray = newStr.slice(0);
			    	strArray = strArray + " ";	
			    	$(this).text(strArray);
			    }	
		    });				
		}
		
	});
},

/*removePriceUnavailable: function (){
	 $(document).ready(function(){
   	  $(".single-products select > option").each(function() {
   		    if(this.text.match('Price')){
   		      $(this).hide();
   		    }
   		});
   	});
 },*/
 
removePriceUnavialablePDP : function(){
		if ($('body').hasClass('page-productDetails'))
		{
			$("li:contains('Price Unavailable')").css({visibility:"hidden"});
		}
},

productPercentage: function(){		
	$('.single-products').each(function(){		
		var productDiscountChk = $(this).find('#productDiscountStatusChk').val();
		if(productDiscountChk != 'null' && productDiscountChk != "" && productDiscountChk !='0.0' && productDiscountChk > 0){				
			$(this).find('#productDiscount').css('display','block');	
			$(this).find('.product-img').find('.over').hide();	
			$(this).find('.over').hide();
		}else{			
			$(this).find('#productDiscount').css('display','none');	
			$(this).find('.product-img').find('.over').show();		
			$(this).find('.over').show();
		}
	});	
	
	var productDiscountChkPDP = $('.page-productDetails').find('#productDiscountStatusChk').val();
	if(productDiscountChkPDP != 'null' && productDiscountChkPDP != "" && productDiscountChkPDP != "0.0" && productDiscountChkPDP > 0 ){		
		$('.page-productDetails').find('.promoImg').addClass('hide');
		$('.page-productDetails').find('#productDiscount').show('slow');
	}else{		
		$('.page-productDetails').find('.promoImg').show();
		$('.page-productDetails').find('#productDiscount').hide('fast');	
	}
},
productBogoMsg: function(){	
	$('.single-products').each(function(){		
		var bogoStatusChk = $(this).find('#bogoMsgStatusChk').val();
		if(bogoStatusChk == 'true'){	
			$(this).find('#productBogoMsg').show('slow');
			$(this).find('.over').hide();
			$(this).find('.details').find('.priceBx').find('.standard-price').hide();			
		}		
	});
	
	var bogoStatusChkPDP = $('.page-productDetails').find('#bogoMsgStatusChk').val();
	if(bogoStatusChkPDP == 'true'){
		$('.page-productDetails').find('.promoImg').hide();
		$('.page-productDetails').find('#productBogoMsg').show('slow');
		$('.page-productDetails').find('.priceTextsBG').find('.standard-price').hide();
	}else{
		$('.page-productDetails').find('.promoImg').show();
	}
},
lazyLoading : function() {
    var requestSent = false;
	var counter = 1;
	var pageCount = $('ul.pagination:first li').length;
	var currentId = $('.products-container > .container').length - 1;
	if (counter <= pageCount) {
		$(window).scroll(function() {
			if ($(window).scrollTop() + $(window).height() + 65 >= $(document).height()) {			   
				var url = $('.pagination li a').attr('href');
				var lastSegment = url.substring(url.lastIndexOf('='));
				url = url.replace(lastSegment, "=" + counter);
				var currentId = $('.container:last').find('.desc-selector').attr('id');
				var wrap = $('div.products-container');
				if(!requestSent){
				  requestSent = true;
				  $.ajax({
					url : url,
					beforeSend : function() {
						$('#loading-image').show();
					},
					success : function(data) {
						var $content = $('<div />').html(data);
						$content.find(".col-md-3.col-sm-4.col-xs-6.separator").appendTo(wrap);
						counter = counter + 1;
						$('[data-toggle="tooltip"]').tooltip();
						
						$('.add-btn').click(function() {
							ACC.product.bindToAddToCartForm();
						});
						ACC.product.productPercentage();
						ACC.product.productBogoMsg();
						ACC.product.removeDuplicate();
					},
					complete : function() {
						$('#loading-image').hide();
						$(function() {
							$('body').load(function() {
								ACC.product.initPLPPageEvents();
							});
						});
						requestSent = false;
					}
					});
				}				
			}
		});
	}
},

enableAddToCartButton : function() {
	$('.js-add-to-cart').removeAttr("disabled");
},

enableVariantSelectors : function() {
	$('.variant-select').removeAttr("disabled");
},

bindToAddToCartForm : function() {
	var addToCartForm = $('.add_to_cart_form');
	addToCartForm.ajaxForm({
		success : ACC.product.displayAddToCartPopup
	});
},

bindToAddToCartStorePickUpForm : function() {
	var addToCartStorePickUpForm = $('#colorbox #add_to_cart_storepickup_form');
	addToCartStorePickUpForm.ajaxForm({
		success : ACC.product.displayAddToCartPopup
	});
},

enableStorePickupButton : function() {
	$('.js-pickup-in-store-button').removeAttr("disabled");
},

displayAddToCartPopup : function(cartResult, statusText, xhr, formElement) {
	$('#addToCartLayer').remove();
	
	if (typeof ACC.minicart.refreshMiniCartCount == 'function') {
		ACC.minicart.refreshMiniCartCount();
	}
	var titleHeader = $('#addToCartTitle').html();
	if (localStorage.getItem("defaultStoreLocation") == null) {
		/*titleHeader = 'Item is not Added to Your Shopping Cart';*/
	}
	if (titleHeader == undefined) {
		/*titleHeader = 'Added to Your Shopping Cart';*/
	}
	var myHTML = "";
	var javaSession = false;
	if (typeof cartResult == "undefined") {
		myHTML = "Please provide order delivery / pick up location before adding an item to the cart";
		javaSession = true;
	}
	else {
		myHTML = "<div>" + cartResult.addToCartLayer + "</div>";
		if ($(myHTML).text().indexOf("Please provide order") >= 0) {
			javaSession = true;
		}
	}
	if (javaSession) {
		localStorage.removeItem("defaultStoreLocation");
		localStorage.removeItem("storeLocator");
	/*	titleHeader = 'Item is not Added to Your Shopping Cart';*/
		javaSession = false;
	}
	
	ACC.colorbox.open(titleHeader, {
	html : myHTML,
	width : "500px",
	onClosed : function() {
		if (localStorage.getItem("defaultStoreLocation") == null) {
			$('body').scrollTop(0);
			$('#menu1').trigger("click");
		}
		
	}
	});
	
	$('#addToCartLayer').fadeIn(function() {
		
		if (typeof timeoutId != 'undefined') {
			clearTimeout(timeoutId);
		}
		timeoutId = setTimeout(function() {
			$.colorbox.close();
			$('#addToCartLayer').fadeOut(function() {
				$('#addToCartLayer').remove();
			});
			/*
			 * if ($('.cart_popup_error_msg').is(':empty')) { $.colorbox.close();
			 * $('#addToCartLayer').fadeOut(function() { $('#addToCartLayer').remove(); }); }
			 */
		}, 1000);
	});
	
	var productCode = $('[name=productCodePost]', formElement).val();
	var quantityField = $('[name=qty]', formElement).val();
	
	var quantity = 1;
	if (quantityField != undefined) {
		quantity = quantityField;
	}
	if (localStorage.getItem("defaultStoreLocation") !== null) {
		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);
	}
	$('.add_to_cart_form [id*=variantAdd_], #addToCartButton').removeAttr("disabled");
},
checkPLPQtySelector : function(self, mode) {
	var input = $(self).parents(".js-qty-selector").find(".plp-js-qty-selector-input");
	if (parseInt(input.val()) == 0) {
		var inputVal = parseInt(input.val() + 1);
	}
	else {
		var inputVal = parseInt(input.val());
	}
	var max = input.data("max");	
	var minusBtn = $(self).parents(".js-qty-selector").find(".plp-js-qty-selector-minus");
	var plusBtn = $(self).parents(".js-qty-selector").find(".plp-js-qty-selector-plus");
	
	$(self).parents(".js-qty-selector").find(".btn").removeAttr("disabled");
	
	if (mode == "minus") {
		if (inputVal != 1) {
			ACC.product.updatePLPQtyValue(self, inputVal - 1)
			if (inputVal - 1 == 1 || inputVal - 1 == 0) {
				minusBtn.attr("disabled", "disabled")
			}
			
		}
		else {
			minusBtn.attr("disabled", "disabled")
		}
	}
	else if (mode == "reset") {
		ACC.product.updatePLPQtyValue(self, 1)

	}
	else if (mode == "plus") {
		if (inputVal != max) {
			if (parseInt(input.val()) == 0) {
				ACC.product.updatePLPQtyValue(self, inputVal)
			}
			else {
				ACC.product.updatePLPQtyValue(self, inputVal + 1)
				/*if (inputVal + 1 == max || inputVal + 1 >= 25) {
					plusBtn.attr("disabled", "disabled")
				}*/
			}
		}
		else {
			plusBtn.attr("disabled", "disabled")
		}
	}
	else if (mode == "input") {
		if (inputVal == 1) {
			$(self).parents(".js-qty-selector").find(".plp-js-qty-selector-minus")
			    .attr("disabled", "disabled")
		}
		else if (inputVal == max) {
			$(self).parents(".js-qty-selector").find(".plp-js-qty-selector-plus")
			    .attr("disabled", "disabled");
		}
		else if (inputVal < 1) {
			ACC.product.updatePLPQtyValue(self, 1)
			$(self).parents(".js-qty-selector").find(".plp-js-qty-selector-minus")
			    .attr("disabled", "disabled")
		}
		else if (inputVal > max) {
			ACC.product.updatePLPQtyValue(self, max)
			$(self).parents(".js-qty-selector").find(".plp-js-qty-selector-plus")
			    .attr("disabled", "disabled");
		}
	}
	
},
updatePLPQtyValue : function(self, value) {
	var $this = $(self).parents(".js-qty-selector")
	var input = $this.find(".plp-js-qty-selector-input");
	var index = $this.find(".productIndex").val();
	var addtocartQtyId = "#qty_".concat(index);
	$this.parent().find(addtocartQtyId).val(value);
	input.val(value);
	
},
initPLPPageEvents : function() {
	$(document).on("click",'.var-selector', function(){
   	  $(".single-products select > option").each(function() {
   		    if(this.text.match('Price Unavailable')){
   		      $(this).hide();
   		    }
   		});
   	});
	
	$(document).on("click", '.js-qty-selector .plp-js-qty-selector-minus', function() {
		ACC.product.checkPLPQtySelector(this, "minus");
	}), $(document).on("click", '.js-qty-selector .plp-js-qty-selector-plus', function() {
		ACC.product.checkPLPQtySelector(this, "plus");
	}), $(document)
	    .on("keydown", '.js-qty-selector .plp-js-qty-selector-input', function(e) {
		    if (($(this).val() != " " && ((e.which >= 48 && e.which <= 57) || (e.which >= 96 && e.which <= 105))) || e.which == 8 || e.which == 46 || e.which == 37 || e.which == 39 || e.which == 9) {
		    }
		    else if (e.which == 38) {
			    ACC.product.checkPLPQtySelector(this, "plus");
		    }
		    else if (e.which == 40) {
			    ACC.product.checkPLPQtySelector(this, "minus");
		    }
		    else {
			    e.preventDefault();
		    }
	    }), $(document).on("keyup", '.js-qty-selector .plp-js-qty-selector-input', function(e) {
		ACC.product.checkPLPQtySelector(this, "input");
		ACC.product.updatePLPQtyValue(this, $(this).val());
	}), $(document)
	    .ready(function() {
		    $('body')
		        .on("change", 'select[id^="selectedpart_"]', function() {
		        	$(this).parents('.single-products').find('#productDiscount').text("");
			        var index = $(this).attr("id").split("_")[1];
			        var newCode = $(this).val();
			        var $this = $(this).parents('.product-image-wrapper');
			        $this.find('#productDiscount').addClass('hide');
			        $
			            .ajax({
			            url : ACC.config.contextPath + "/search/plp/display/variant/details",
			            type : "POST",
			            data : {
				            'productCode' : $(this).val()
			            },
			            success : function(data) {
				            var values = data.split("_");
				            var price = values[0];
				            var stockLevelStatus = values[1];
				            var stockLevel = values[2];
				            var description = values[6];
				            var unitMRP = values[4];
				            var savings = values[5];
				            var name = values[6];
				            var imageURL = values[7];
				            var variantURL = values[8];
				            var promoMessage = values[9];	
				            var productBogoChk = values[10];
				            var promotionDiscountChk = values[11];
				            
				           
				            
                          if (parseFloat(price.split('Rs')[1]) < parseFloat(unitMRP.split('Rs')[1])) {
					            var priceDiv = "<span class='prev-price'>MRP &nbsp;" + unitMRP + "</span> <p class='orignal-price'>" + price + "</p> <span class='standard-price'>Save &nbsp;" + savings + "</span>";
				            }
				            else {
					            var priceDiv = "<span class='prevnolinethrough-price'>MRP &nbsp;" + unitMRP + "</span> <p class='orignal-price'>" + price + "</p> <span class='standard-price custom-tooltip'>Save &nbsp; " + savings + "</span>";
				            }
				            
				            if (promoMessage == 'combiOffer' || promoMessage == 'bestDeal' || promoMessage == 'regularOffer') {
					            /*
								 * var promoMessageDiv= "<span class='var-message'> Please add item
								 * to the cart, to avail offers. </span>";
								 */
					            promoMessageImgUpdate = baseURL + '/_ui/responsive/common/images/' + promoMsgImgAry[promoMessage];
					            var promoLogo = $this.find('#logoImg_' + index)
					                .removeClass('hidden').addClass('promoMessage')
					                .html('<img src=' + promoMessageImgUpdate + ' />');
					            var priceDiv = "<span class='prevnolinethrough-price'>MRP &nbsp;" + unitMRP + "</span> <p class='orignal-price'>" + price + "</p> <span class='standard-price custom-tooltip'>" + savings + "</span>";
					            
					            if (savings.length > 50) {
						            
						            $('.priceBx').on('mouseenter', '.custom-tooltip', function() {
							            $(this).attr('title', savings).tooltip();
						            });
						            var newSaving = savings.substring(0, 50).concat("...");
						            var priceDiv = "<span class='prevnolinethrough-price'>MRP &nbsp;" + unitMRP + "</span> <p class='orignal-price'>" + price + "</p> <span class='standard-price custom-tooltip'>" + newSaving + "</span>";
					            }
					            
				            }
				            else {
					            /* var promoMessageDiv="<span class='var-message'></span>"; */
					            var promoLogo = $this.find('#logoImg_' + index).addClass('hidden');
				            }
				            var tooltipDescription = description;
				            if (description.length > 25) {
					            description = description.substring(0, 25).concat("...");
				            }
				            
				            $this.find('#variantDescription_' + index)
				                .html("<h5>" + description + "</h5>");
				            $this.find('#variantDescription_' + index)
				                .attr("data-original-title", tooltipDescription);
				            $this.find('#variantPrice_' + index)
				                .html(priceDiv /* + promoMessageDiv */);
				            $this.find('#variantLogo_' + index).html(promoLogo);
				            var addtocartQtyId = "#qty_".concat(index);
				            var stockLevelId = "#variantStockLevel_".concat(index);
				            var input = $(stockLevelId);
				            $this.find(stockLevelId).attr("data-max", stockLevel);
				            ACC.product.checkPLPQtySelector(input, "reset");
				            $this.find('#variantProductName_' + index).val(name);
				            $this.find('#variantPartNumber_' + index).val(newCode);
				            $this.find('#variantProductCode_' + index).val(newCode);
				            $this.find('#variantProductPrice_' + index).val(price);
				            if (imageURL == null || imageURL == "") {
					            imageURL = baseURL + "/_ui/responsive/theme-blue/images/missing_product_150x150.jpg";
				            }
				            var variantImageDivId = "#variantImage_".concat(index).concat(" img");
				            $this.find(variantImageDivId).attr("src", imageURL);
				            $this.find(variantImageDivId).attr("alt", name);
				            $this.find(variantImageDivId).attr("title", name);
				            var variantImageDivId = "#variantImageAnchor_".concat(index);
				            $this.find(variantImageDivId)
				                .attr("href", ACC.config.encodedContextPath + variantURL);
				            // $(variantImageDivId).attr("title",
				            // desctiption);
				            if (stockLevelStatus == 'outOfStock') {
					            $this.find("#variantAdd_" + index).attr("disabled", "disabled")
					                .attr("type", "button").addClass('out-of-stock')
					                .html('Out of Stock');
					            $this.find("#variantAdd_" + index).parents('.spinner')
					                .siblings('.js-qty-selector').find('input, button')
					                .attr("disabled", "disabled");
					            // $(addtocartQtyId).val(0);
					            // $(stockLevelId).val(0);
				            }
				            else if (price == 'Unavailable') {
					            $this.find("#variantAdd_" + index).attr("disabled", "disabled");
					            $this.find(addtocartQtyId).val(0);
					            $this.find(stockLevelId).val(0);
				            }
				            else {
					            $this.find("#variantAdd_" + index).removeAttr("disabled")
					                .attr("type", "submit").removeClass('out-of-stock')
					                .html('<i class="fa fa-shopping-cart"></i>Add')
					                .parents('.spinner').siblings('.js-qty-selector')
					                .find('input, button').removeAttr("disabled");
					            $this.find(addtocartQtyId).val(1);
					            $this.find(stockLevelId).val(1);
				            }	
				            
				            	//Bogo Check				            	
				            	$('#bogoMsgStatusChk').val(productBogoChk);	
				            	if(productBogoChk == 'true'){	
				            		var msg=$this.find('.priceBx').find('.standard-price').text();
				            		$('#productBogoMsg').text(msg).show();					        	  
					        	    $this.find('.priceBx').find('.standard-price').hide();	
					            	$this.find('.over').hide();
					            }else{
					            	$('#productBogoMsg').hide();
					            	 $this.find('.details').find('.standard-price').show();	
					            	 $this.find('.over').show();
					            }
				            	
				            	//Promotion Discount Check
				            	if(promotionDiscountChk != 'null' && promotionDiscountChk != "0.0" && promotionDiscountChk > 0){	
				            		var roundOff = Math.round(promotionDiscountChk);				            		
				            		$this.find('#productDiscount').text(roundOff + "% OFF");
				            		$this.find('#productDiscount').removeClass('hide');
				            		$this.find('.product-img').find('.over').hide();
				            	}else{
				            		$this.find('#productDiscount').addClass('hide');
				            	}
				            	
				            	
			            }			            
			        });
		        });
	    });
}

};