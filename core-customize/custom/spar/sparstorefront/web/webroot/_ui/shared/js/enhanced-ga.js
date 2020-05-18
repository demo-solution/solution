// Enhanced Google Analytics Implementation
//Use "UA-81471740-1" for PROD and "UA-81471740-2" for STAGE
//var googleAnalyticsTrackingId = "UA-81471740-1",
var googleAnalyticsTrackingId = "UA-81471740-2",
	data = {},
	cartItems = [];
	
(function(i, s, o, g, r, a, m) {
	i['GoogleAnalyticsObject'] = r;
	i[r] = i[r] || function() {
		(i[r].q = i[r].q || []).push(arguments)
	}, i[r].l = 1 * new Date();
	a = s.createElement(o), m = s.getElementsByTagName(o)[0];
	a.async = 1;
	a.src = g;
	m.parentNode.insertBefore(a, m)
})(window, document, 'script', '//www.google-analytics.com/analytics.js','ga');
ga('create', googleAnalyticsTrackingId, 'auto');
ga('require', 'ec');

//GA Tracking Object Setup
var enhancedGATracking ={
	addImpression: function (productId, productName, productPrice, productQty, productCategory, productBrand){
		ga("ec:addImpression", {
			'id' : productId,
			'name' : productName,
			'price' : productPrice,
			'quantity' : productQty,	
			'category':productCategory,
			'brand':productBrand,		
			"list": productCategory
		});		
	},
	addProduct:	function (productId, productName, productPrice, productBrand, productCategory, productQty){	
		ga("ec:addProduct", {
			  "id": productId,
			  "name": productName,
			  "price": productPrice,
			  "brand": productBrand,
			  "category": productCategory,
			  "quantity" : productQty
			});
	}
}
//PRODUCT - LISTING
$('.single-products').each(function(){
	var productId = $(this).find('input[name="partNumber"]').val(),
		productName = $(this).find('.variantImage img').attr('title'),
		productPrice = $(this).find('.orignal-price').text(),
		productPrice = productPrice.trim(),	
		productPrice = productPrice.replace(/Rs/g,''),
		productBrand=$(this).find('.product-name-list.h3').text(),
		productCategory=$('.breadcrumb-section .breadcrumb').find('li.active').text(),
		productQty =$(this).find('input[name="pdpAddtoCartInput"]').val();
		 enhancedGATracking.addImpression(productId, productName, productPrice, productQty, productCategory, productBrand);		//Add Impression		
		 ga('send', 'event',productCategory,'impression', {'nonInteraction': 1});
		 
		//Product Click - Add to cart from Product Listing
		$(this).on('click', '.add_to_cart_form .add-btn', function(e) { //Add to Cart Tracking
			var productQty = $(this).parents('.cart').prev().find('input[name="pdpAddtoCartInput"]').val();	
			 if (localStorage.getItem("defaultStoreLocation")) {
				enhancedGATracking.addProduct(productId, productName, productPrice, productBrand, productCategory, productQty);						
				ga("ec:setAction", "add", {
					"list": productCategory
				});
				ga("send", "event", productCategory, "click", "Add to Cart");
			 }
		     else {
			        ACC.product.displayAddToCartPopup();
		        }
		});
});

//PRODUCT - PDP
if ($('body').hasClass('page-productDetails')) {
	var productId = $('.product-list').find('input[name="productCodePost"]').val(),
		productName = $('.product-list').find('.pdpInfoBx h4').text(),
		productPrice = $('.product-list .priceTextsBG').find('.priceMain').text().trim(),
		productPrice = productPrice.replace(/Rs/g,''),
		productQty = $('#pdpAddtoCartInput').val(),
		productBrand=$('.product-list .priceTextsBG').find('.brand').text(),
		productCategory=$('.product-list ul.breadcrumb li').last().prev().find('a').text();	
	
		//enhancedGATracking.addImpression(productId, productName, productPrice, productQty, productCategory, productBrand);		//Product Impression
		//ga('send', 'event','ec','Impression', {'nonInteraction': 1});
	
	function productClick(eventTrigger) {
		var productQty = $('#pdpAddtoCartInput').val();
			enhancedGATracking.addProduct(productId, productName, productPrice, productBrand, productCategory, productQty);		// Product Click 
			ga("ec:setAction", "detail", {
				"list" : productCategory
			});
			ga("send", "event", "Detail action", "click", eventTrigger);
			 
	}
		productClick('Product Click');		
		$('.product-list').find('#addToCartButton').on('click',function(e) {
			 if (localStorage.getItem("defaultStoreLocation")) 
			 {
				 productClick('Add To Cart');	
			}	
	    });
}

//PRODUCT - CART
if ($('body').hasClass('page-cartPage')){
	$('.cartItemsBx .cart-item-details').each(function(){		//Product Add/Remove Cart
		var productId = $(this).find('.color-black').attr('href'),
			productId = productId.substr(productId.lastIndexOf('/') + 1),
			productName = $(this).find('.thumb').find('img').attr('title'),
			//productPrice = $(this).find('.total').find('.item-heading').next().text(),
			productPrice = $(this).find('.total').find('.item-heading').end().text().replace('SUBTOTAL',''),
			productPrice = productPrice.trim(),
			productPrice = productPrice.replace(/Rs/g,''),
			productQty = $(this).find('input[name="quantity"]').val(),	
			productQty = productQty.trim(),
			productCategory=$(this).prev().find('h5').text(),
			productCategory = productCategory.trim(),
			productBrand="N.A.";		
			enhancedGATracking.addProduct(productId, productName, productPrice, productBrand, productCategory, productQty);
			ga('send', 'event', "Product Cart Items",'Cart Items');	
			//ga("send", "pageview");
			ga("ec:setAction", "remove");
			ga("send", "event", "detail view", "click", "removeFromCart");
			
			
		// Capture Cart Data
		data = {
				"productId" : productId,  
				"productName" : productName,
				"productPrice" : productPrice,					
				"productBrand" : productBrand,
				"productCategory" : productCategory,
				"productQty" : productQty
		  }	
		cartItems.push(data);
		localStorage.setItem('products', JSON.stringify(cartItems));	
	});
}

//Checkout Flow Delivery Address to Payment
if($('body').hasClass('page-multiStepCheckoutSummaryPage')){
	var savedCartItems = localStorage.getItem("products");
	savedCartItems = JSON.parse(savedCartItems);	
	//var steps = $('.checkout-steps').find('li.active').find('.round-tab').find('i').text();
	var steps = $('.checkout-steps').find('li.active').find('.checktab').text();
	savedCartItems.forEach(function(key){
		enhancedGATracking.addProduct(key['productId'], key['productName'], key['productPrice'], key['productBrand'], key['productCategory'], key['productQty']);
		ga('send', 'event', " " + " " + steps);	
		
		//ga("send", "pageview");
		//ga("ec:setAction", "click")
			
	});
	
	
}
//Order Confirmation
if($('body').hasClass('page-orderConfirmationPage')){
	if(performance.navigation.type==0){
		ga('ec:setAction', 'purchase', {          
			  'id': 			$('.order-details .margTop').find('h5.order-no').text(),   
			  'affiliation': 	"Spar Online - Store", 			       
			  'revenue': 		$('.order-details .margTop').find('h5.order-amount').text().replace(/[^0-9.]/g,''),
			  
			});
		ga('send', 'event', "Purchase", "Order Confirmation");		
		//ga("send", "pageview");
		localStorage.removeItem("products");
	}
		
}

