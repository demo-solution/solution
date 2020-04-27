// JavaScript Document by SHIVA
var addressform = false;
var disableLrStatus = false;
var addressTown = "";
var enterPress = false;
var baseURL = document.location.origin;
var savedCity = $("#sparAddressForm input[name='townCity']").val();


if (baseURL.split('localhost').length > 1) {
	baseURL = baseURL + "/store"
}

if (baseURL.split('hclsdhyb01.hcldigilabs.com').length > 1) {
	baseURL = baseURL + "/store"
}

$(".txtOverflowElipsis").text(function(index, currentText) {
	var a = $(this).attr("data");
	currentText = currentText.replace(/[^\x20-\x7E]/gmi, "");
	if ($(this).text().length >= a) {
		return currentText.substr(0, a) + "...";
	}
});
function loadSocialMediaScript(){
	var commonResourcePath = $('#commonResourcePath').val();
	var s = document.createElement("script");
    s.type = "text/javascript";
    s.src = commonResourcePath + "/js/social-media.js";
    // Use any selector
    $("head").append(s);
}
$("#sparAddressForm input[name='townCity']").attr('readOnly', true);
$("#sparAddressForm input[name='postcode']").attr('readOnly', false);
$("#sparAddressForm input[name='area']").attr('readOnly', true);
$('#deliveryCityName').attr('readOnly', true);

var mainMenu = {
  // Menu Subcategory Animation
  menusubCatAnimate : function() {
	   $('.vertical-menu-list>li')
       .each(function(index) {
	        var a = $(this).find('.v-menu-list>li'), b = $(this)
	            .find('.vertical-dropdown-menu'), c = a.size(), d = $(this).siblings().size();
	        if (10 > d) {
		        $('.block-vertical-menu-list').addClass('noviewmore');
	        }
	        else {
		        $('.block-vertical-menu-list').removeClass('noviewmore');
		        d = 10;
	        }
	        // console.log('d::'+d)
	        if (c > d) {
		        b.addClass('row2');
	        }
	        if (c > 2 * d) {
		        b.addClass('row3');
	        }       
	       
       });
	  $('.block-vertical-menu-list')
	      .on(
	          "mouseenter",
	          ".v-menu-list>li",
	          function() {
		          var CL3 = $(this).find('.v-menu-Sublist');
		          if (!CL3.attr("style")) {
			          var liW = $(this).width(), ma = liW - 25, Coffset = $(this).parents('.menu-back-containter').offset(), TL2 = $(
			              this).offset().top
			              - Coffset.top, LL2 = $(this).offset().left - Coffset.left - 12, L2H = $(this).height(), CL3O = CL3
			              .offset(), CL3li = CL3.find('.vertical-dropdown-submenu li'), CL3liT = CL3li.size(), CL3vSM = CL3
			              .find('.vertical-dropdown-submenu'), cl2RW = 10;

			          var T2B = Math.floor(TL2 / L2H)
			          var L2R = Math.floor(LL2 / liW)
			          if ((L2R == 2) && (ma > 150)) {
				          ma = 150
			          }

			          CL3.animate({
				          "left" : '' + ma + 'px'
			          }, 500);

			if ((!$('.noviewmore').is(":visible")) && ($(".more-cat").hasClass('on'))) {
				          cl2RW = $('.vertical-menu-list>li').size();
			          }
			         
			          if (CL3liT > 10) {
				          CL3vSM.addClass('rowSub2');
				          CL3liT = Math.ceil(CL3liT / 2);
			          }
			          if ((L2R == 2) && (CL3liT > cl2RW)) {
				          CL3vSM.removeClass('rowSub2');
				          CL3.animate({
					          "left" : '' + 150 + 'px'
				          }, 500);
			          }
			          var togoTop = Math.floor(CL3liT / 2);
			          if (togoTop > T2B) {
				          togoTop = T2B;
			          }
			          if (T2B + Math.ceil(CL3liT / 2) > cl2RW) {
				          togoTop = togoTop + ((T2B + Math.ceil(CL3liT / 2)) - cl2RW)
			          }

			          if ((CL3liT >= 2) && (CL3liT <= cl2RW)) {
				          CL3vSM.css({
					          "margin-top" : '-' + (togoTop * L2H) + 'px'
				          })
			          }

			          if (T2B <= 0) {
				          CL3vSM.removeAttr("style");
			          }
			          if (T2B >= cl2RW) {
				          CL3vSM.css({
				            "margin-top" : '' + 0 + 'px',
				            "top" : "auto",
				            "bottom" : "-1px"
				          })
			          }

		          }
	          });
  },
  showMoreCat : function() {
	  $('.top-navigation').each(function() {
		  var menuheightDefault = null;
		  var menuA = $('.vertical-menu-content'), menuB = menuA.height(), 
		  menuC = menuA.find('.menu-back-containter'), menuD = menuA.children().height()
		  var totalBlockHeight = $(this).find('ul.vertical-menu-list').children().length;
		  var defaultBlockHeight = $('.block-vertical-menu .vertical-menu-content').height();
		  $(this).find('.block-vertical-menu-list').on('click', '.more-cat', function() {
			  if ($(this).hasClass('on')) {
				  $(this).removeClass('on');
				  $(this).find('.vimor').text('VIEW MORE');
				  $(this).prev().animate({
					  height : defaultBlockHeight
				  }, {
					  duration : 500,
					    complete : function() {
						    menuA.removeAttr("style");
						    menuC.removeAttr("style");
					    }				  
					  
				  });
			  } else {
				  $(this).prev().animate({
					  height : totalBlockHeight * 29
				  }, {
					  duration : 500,
					    complete : function() {
					    	 menuC.height(totalBlockHeight * 31);
					    }  
					  
				  });
				  $(defaultBlockHeight).find('.menu-back-containter').height(menuA.parent().height())
				  $(this).find('.vimor').text('VIEW LESS');
				  $(this).addClass('on');
			  }
		  });
	  });
  }

}
$(document)
    .ready(
        function() {
        	/* -----------------OLD GOOGLE API CODE-------------------*/
	     google.maps.event.addDomListener(window, 'load', ACC.storefinder.initialize('storelocator-query'));
	      google.maps.event.addDomListener(window, 'load', ACC.storefinder.initialize('storelocator-query1'));
	       google.maps.event.addDomListener(window, 'load', ACC.storefinder.initialize('storelocator-query-reg'));	        
	        
	       $('#storelocator-query1').on('keyup',function() {
	        	  var input = document.getElementById('storelocator-query1');
	        	  var southWest = new google.maps.LatLng(12.975821448855658, 77.59847820229163);
	        	  var northEast = new google.maps.LatLng(12.977366551144343, 77.60006379770846);
	        	  var bangaloreBounds = new google.maps.LatLngBounds(southWest, northEast);
	        	  var options = {
	        	    bounds : bangaloreBounds,
	        	    componentRestrictions : {
	        		    country : "in"
	        	    }
	        	  };
	        	  var autocomplete = new google.maps.places.Autocomplete(input, options);
	        	 google.maps.event.addListener(autocomplete, 'place_changed', function() {  		
	        		var place = autocomplete.getPlace();	
	        		var address = $("#sparAddressForm input[name='townCity']").val();	        		
	        		address = address.trim();	
	        		if(address == address.toUpperCase()){
	        			address = address.toLowerCase();
	        			address = address.charAt(0).toUpperCase() + address.substr(1);	
	        		}else if(address == address.toLowerCase()){
	        			address = address.charAt(0).toUpperCase() + address.substr(1);
	        		}	        		
	         		var stlocadd = place.formatted_address;
	         		if (ACC.storefinder.storeAreaValidation(address, stlocadd)) {	
	         			$('#storelocator-query1').closest('.has-error').find('.help-block').hide();
	         			$('#storelocator-query1').closest('.has-error').removeClass('has-error');
	         			areaDetailsInfo(place);
	         		}       		
	        		
	        	 });
	        });
	        $('#storelocator-query-reg').on('keyup',function() {
	        	  var input = document.getElementById('storelocator-query-reg');
	        	  var southWest = new google.maps.LatLng(12.975821448855658, 77.59847820229163);
	        	  var northEast = new google.maps.LatLng(12.977366551144343, 77.60006379770846);
	        	  var bangaloreBounds = new google.maps.LatLngBounds(southWest, northEast);
	        	  var options = {
	        	    bounds : bangaloreBounds,
	        	    componentRestrictions : {
	        		    country : "in"
	        	    }
	        	  };
	        	  var autocomplete = new google.maps.places.Autocomplete(input, options);
	        	 google.maps.event.addListener(autocomplete, 'place_changed', function() {  		
	        		var place = autocomplete.getPlace();	
	        			areaDetailsInfo(place);
	        	 });
	        });
        	
	    
      function areaDetailsInfo(place){
	        	$('#locality_error1').hide();
     			var myData = place.address_components;	        		
      		    var mylocalArea = "";
      		    var myPostalCode = "";
      		    var longFormatted_address = "";
      		    var locality="";
      		    var formatted_address = place.address_components;
      		    var stlocadd = place.formatted_address;
      		    $.each(myData, function(i) {
      		    		longFormatted_address += this.long_name;			    
      			    if (i !== myData.length - 1) {
      				    longFormatted_address += ',';
      			    }
      			    if (this.types.indexOf("postal_code") > -1) {
      				    myPostalCode = this.long_name;	
      				    $('#sparAddressForm .info-text');
      			    }else{
      			    	myPostalCode = " ";
      			    	$('#sparAddressForm .info-text');
      			    }
      			    if (this.types.indexOf("locality") > -1) {
      			    	if(formatted_address.indexOf(this.long_name) !=-1){
      			    		locality = this.long_name;
      			    	}
      			    }
      		    });
      		  $.each(myData, function(i) {
  		    	if (this.types.indexOf("locality") > -1) {
  		    		locality = this.long_name;
  				    return false;
  			    } else if (this.types.indexOf("politicals") > -1) {
  			    	locality = this.long_name;
  				    return false;
  			    }
  		    });
      		    
      		    $.each(myData, function(i) {
      		    	if (this.types.indexOf("route") > -1) {
      				    mylocalArea = this.long_name;
      				    return false;
      			    } else if (this.types.indexOf("sublocality_level_1") > -1) {
      				    mylocalArea = this.long_name;
      				    return false;
      			    } else if (this.types.indexOf("sublocality_level_2") > -1) {
      				    mylocalArea = this.long_name;
      				    return false;
      			    } else if (this.types.indexOf("locality") > -1) {
      				    mylocalArea = this.long_name;
      				    return false;
      			    } else{
      			    	mylocalArea=longFormatted_address;
      			    }
      		    });
      		    //Value Setting for Spar Address Form   
      		  var longAreaAddress= $("#storelocator-query1").val();	
      		  var longAreaAddressReg=$("#storelocator-query-reg").val();
      		  
 		     if((null != longAreaAddress && longAreaAddress.includes("Shimoga")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Shimoga"))){ 
 		    	 $("#sparAddressForm input[name='townCity']").val("Shimoga"); 		    	 
 		     }else if((null != longAreaAddress && longAreaAddress.includes("Shivamogga")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Shivamogga"))){
 		    	 $("#sparAddressForm input[name='townCity']").val("Shivamogga");
 		     }else if((null != longAreaAddress && longAreaAddress.includes("Hyderabad")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Hyderabad"))){
 		    	 $("#sparAddressForm input[name='townCity']").val("Hyderabad");
 		     }else if((null != longAreaAddress && longAreaAddress.includes("Secunderabad")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Secunderabad"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Secunderabad");
		     }else if((null != longAreaAddress && longAreaAddress.includes("Bengaluru")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Bengaluru"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Bengaluru");
		     }else if((null != longAreaAddress && longAreaAddress.includes("Bangalore")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Bangalore"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Bangalore");
		     }else if((null != longAreaAddress && longAreaAddress.includes("Gurugram")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Gurugram"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Gurugram");
		     }else if((null != longAreaAddress && longAreaAddress.includes("Gurgaon")) || (null != longAreaAddressReg && longAreaAddressReg.includes("Gurgaon"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Gurgaon");
		     }		     
 		     else{ 		    	 
 			    if((null != stlocadd && stlocadd.includes(locality))){			    	
 			    	$("#sparAddressForm input[name='townCity']").val(locality);		    	 
 			    }else{			    	
 			    	$("#sparAddressForm input[name='townCity']").val(storeSession.city);
 			    }	    
 			   }
      		    $("#sparAddressForm input[name='postcode']").val(myPostalCode);
    		    $("#sparAddressForm input[name='area']").val(mylocalArea);
	        }
	        $('#deliveryCityName').css('color', '#464646');
	        $(".ContactUsLink").prepend('<span class="hidden-xs hidden-sm img-icons call-icon"></span>');
	        $(".DownloadLink").prepend('<span class="hidden-xs hidden-sm img-icons download-icon"></span>');
	        $(".HelpLink").prepend('<span class="hidden-xs hidden-sm img-icons help-icon"></span>');
	        // Initialize tooltips
	        $('.nav-tabs > li a[title]').tooltip();

	        $('.outOfStock,.radio-red input[type="radio"]').attr("disabled", "disabled");
	        $('.productItemList>li').eq(0).removeAttr("class").addClass('productMenuItemsHeading')
	        $('.productItemList>li').eq(1).removeAttr("class").addClass('productMenuItemsFirst')

	        // Wizard
	        $('a[data-toggle="tab"]').on('show.bs.tab', function(e) {

		        var $target = $(e.target);

		        if ($target.parent().hasClass('disabled')) {
			        return false;
		        }
	        });

	        $(".next-step").click(function(e) {

		        var $active = $('.wizard .nav-tabs li.active');
		        $active.next().removeClass('disabled');
		        nextTab($active);

	        });
	        $(".prev-step").click(function(e) {

		        var $active = $('.wizard .nav-tabs li.active');
		        prevTab($active);

	        });

	        // changes in these both function for store locator by
	        // vishwa

	        $(document).on("click", '.store-finder-navigation input[type="radio"][name="store"]', function() {
		        $(this).parent().addClass('on').siblings().removeClass('on')
		        $('#collectionCenter').trigger("click");
		        $('#cncSlot').html($(this).attr("dataSlot"));
	        });

	        $(document).on("click", '.clickNCollectData>ul>li>input[type="radio"]', function() {
		        $(this).parent().addClass('on').siblings().removeClass('on')
	        });

	        $('#homedelivery,#collectionCenter').click(
	            function() {	            	
		            if ($(this).attr("id") != "collectionCenter") {
			            $('.store-finder-navigation input[name="store"][type="radio"]').removeAttr("checked").parent().removeClass('on');
			            if (ACC.storemapped.storeData !== "") {			            	
				            $('#cncSlot').html(ACC.storemapped.storeData["data1"][0].dslots);
			            }else if(ACC.storefinder.storeData !== ""){
			            	$('#cncSlot').html(ACC.storefinder.storeData["data1"][0].dslots);
			            }
		            } else {		            	
			            $('#homedelivery').removeAttr("checked");
			            // $('#collectionCenter').attr("checked","checked");
		            }
		            $('input[name="deliveryType"]').val($(this).attr("data"));
		            // $('input[name="deliveryCityName"]').val($('#cityName').val())
	            });

	        // Change end here

	        $('[data-toggle="tooltip"]').tooltip();

	        /*
	         * $('.vertical-menu-list>li, .vertical-dropdown-menu,
	         * .vertical-dropdown-submenu').on("mouseenter",function(){
	         * $('.menu-back-containter').show(); })
	         * $('.vertical-menu-list>li,.menu-back-containter').on("mouseleave",function(){
	         * $('.menu-back-containter').hide(); })
	         */

	        $('.sideMenuIcon').each(function() {
		        $(this).next().find('a').append($(this));
	        })

	        if ($('.checkout-block').is(":visible")) {
		        $('div.form-group.has-error').each(function() {
			        if ($(this).find('div.form-group').size() > 0) {
				        $(this).find('div.form-group').append($(this).find('.help-block')).addClass('has-error')
				        $(this).removeClass('has-error');
			        }
		        })
	        }

	        if ((window.location.href.split('optionalDetails').length > 1)
	            && (localStorage.getItem("optionalDetailsPage") != null)) {
		        var optionalDetailsPageArry = JSON.parse(localStorage.getItem("optionalDetailsPage"));
		        $('.register-section input').each(function(index) {
			        $(this).val(optionalDetailsPageArry[index]);
		        })
		        localStorage.removeItem("optionalDetailsPage");
		        $('#userOTP').focus().val('');
		        $('#myModal').modal({
			        refresh : true
		        });
	        }

	        if ((window.location.href.split('delivery-address/').length > 1)
	            && (localStorage.getItem("optionalDetailsPage") != null)) {
		        var optionalDetailsPageArry = JSON.parse(localStorage.getItem("optionalDetailsPage"));
		        $('#sparAddressForm input').each(function(index) {
			        $(this).val(optionalDetailsPageArry[index]);
		        })
		        // sessionStorage.removeItem("optionalDetailsPage");
		        $('#userOTP').focus().val('');
		        $('#myModal').modal({
			        refresh : true
		        });
	        }

	        if ($('input[name="whetherEmployee"][type="checkbox"]').is(":checked")) {
		        $('#test').fadeToggle();
		        $('.profile-update-form .form-group').next().removeClass('hidden');
	        }

	        $('input[name="whetherEmployee"][type="checkbox"]').click(function() {
		        $('#test').fadeToggle();
	        });
	        $('input[name="dateOfBirth"]').datepicker({
	          maxDate : new Date(),
	          changeYear : true,
	          yearRange : "1900:+nn"
	        });
	        $(".dateOfBirth").click(function() {
		        $('input[name="dateOfBirth"]').datepicker("show");
	        });

	        $('input[name="dateOfJoining"]').datepicker({
	          maxDate : new Date(),
	          changeYear : true,
	          yearRange : "1900:+nn"
	        });
	        $(".dateOfJoining").click(function() {
		        $('input[name="dateOfJoining"]').datepicker("show");
	        });

	        $('.active[role="presentation"]').prevAll().addClass('active').removeClass('disabled');

	        if ($('#addressbook .address-header').size() > 0) {
		        if (localStorage.getItem("addressform") == "true") {
			        $('#addressform').show();
			        $('#addressbook,#nwAddressBtn,#previousStep').hide();			        
		        } else {
			        $('#addressform,#addressCancel,#clickNCollect').hide();
			        $('#addressCancel').parents('ul').hide();
		        }

	        } else {
		        $('#addressform,#previousStep').show();
		        $('#addressbook,#nwAddressBtn,#addressCancel').hide();
		        localStorage.setItem('addressform', 'false');
	        }

	        if ($('#address1').is(":visible")) {
		        $('#' + localStorage.getItem("deliveryMode") + '').prop("checked", "checked");
	        }

	        $('#addressCancel').click(function() {
		        $('#addressform,#addressCancel').hide();
		        $('#addressbook,#nwAddressBtn,#previousStep').show();
		        localStorage.setItem('addressform', 'false');
		        window.location.href = window.location.href.split('delivery-address/')[0] + 'delivery-address/add'
	        })

	        if (window.location.href.split('edit').length > 1) {	
	        	localStorage.removeItem('currentAddress');
		        $('#addressform,#addressCancel').show().siblings().hide();
		        $('#addressbook,#nwAddressBtn,#previousStep').hide();
		        $('#addressCancel').parents('ul').show();
		        localStorage.getItem("addressform", "true");
	        }

	        $('#nwAddressBtn').click(function() {
	        	localStorage.removeItem('currentAddress');
		        $('#addressform,#addressCancel').show();
		        $('#addressCancel').parents('ul').show();
		        $('#addressbook,#nwAddressBtn,#previousStep').hide();
		        $('#addressSubmit').removeAttr("disabled", "disabled");
	        });

	        $('#addressbook input[type="radio"][name="address"], .clickNCollectData input[type="radio"]').click(
	            function() {
		            $('#addressbook input[type="radio"][name="address"], .clickNCollectData input[type="radio"]')
		                .removeAttr("checked").parent().removeClass('on')
		            $(this).prop("checked", "checked");
	            })

	        $('#deliveryType2').on("click", function() {
		        $('#clickNCollect,.collectNcenterPhoneBx').show();
		        $('#addressform,#addressCancel,#addressbook,#nwAddressBtn').hide();
		        $('#addressbook input[type="radio"][name="address"]').removeAttr("checked").parent().removeClass('on');
		        $('#addressSubmit').removeAttr("disabled", "disabled");

	        })
	        $('#deliveryType1').on("click", function() {
		        $('.clickNCollectData>ul>li>input[type="radio"]').removeAttr("checked").parent().removeClass('on')
		        if ($('#addressbook .address-header').size() > 0) {
			        $('#clickNCollect,#addressform,#addressCancel,.collectNcenterPhoneBx').hide();
			        $('#addressbook,#nwAddressBtn').show();
		        } else {
			        $('#addressform,#previousStep').show();
			        $('#addressbook,#nwAddressBtn,#addressCancel,#clickNCollect,.collectNcenterPhoneBx').hide();
		        }

	        })

	        if ($('.deliverycntnr').is(":visible")) {
		        localStorage.removeItem("CNCMobile");
		        if (localStorage.getItem("optionalDetailsPage") != null) {
			        localStorage.removeItem("optionalDetailsPage");
		        } else {
			        $('input[type="radio"][value="' + $('.deliverycntnr').attr("defaultDelivery") + '"]').trigger("click");
			        if (localStorage.getItem("cncCenter") != null) {
				        $('#deliveryType2').attr("disabled", "disabled")
			        }
		        }

		        if (localStorage.getItem("CNCCLICK") != null) {
			        localStorage.removeItem("CNCCLICK");
			        document.getElementById("cnc.phone").value = localStorage.getItem("CNCPhoneNo")
			        $('#deliveryType2').trigger("click");
			        $('#cncModal').modal({
				        refresh : true
			        });
		        }
	        }
	        
	        jQuery(document).ready(function ($) {
	        	var jssor_slider1;
	        	 var jssor_slider2_starter = function(containerId) {
	 		        var options = {
	 		          $AutoPlay : true,
	 		          $AutoPlayInterval : 5000,
	 		          $BulletNavigatorOptions : {
	 			          $Class : $JssorBulletNavigator$	 			        
	 		          },
	 		          $ThumbnailNavigatorOptions : {
	 		            $Class : $JssorThumbnailNavigator$,
	 		            $ChanceToShow : 2,
	 		            Rows : 1,
	 		            $Cols :10,
	 		            $NoDrag : true,
	 		            $Scale : false,
	 		            $AutoCenter : 1,
	 		            $Lanes : 2,
	 		          }
	 		        };
	 		        jssor_slider1 = new $JssorSlider$(containerId, options);
	 	        };
	 	       function ScaleSlider(id) {
			        var refSize = $(id).parent().width();
			        if (refSize) {
				        // refSize = Math.min(refSize, 855);
				        jssor_slider1.$ScaleWidth(refSize);
			        } else {
				        window.setTimeout(ScaleSlider, 30);
			        }
		        }
	 	        function jssorNavigation(id){
 		   		  var selector=$(id + ' ' + '.banner-wrapper');		  
 		   		  if(selector.length == 1){			  
 		   			  $(id + ' ' + '.thumbnavigator').hide();			 
 		   		  }
		        }
		        
		       if ($('#homepage_slider').is(":visible")) {
			        jssor_slider2_starter("homepage_slider");
			        ScaleSlider('#homepage_slider');
			        jssorNavigation('#homepage_slider');
			        $(window).bind('load', ScaleSlider);
			        $(window).bind('resize', ScaleSlider);
			        $(window).bind('orientationchange', ScaleSlider);
		        }
		        
		        if ($('#homepage_slider').is(":visible")) {
			        if ($('body').width() > 769) {
				        $('.block-vertical-menu').addClass("homeMenu");
				        $('.banner').addClass("homepage");
			        }
		        }		        
		        
		         if ($('#homepage_slider2').is(":visible")) {
		        	jssor_slider2_starter("homepage_slider2");
		        	ScaleSlider('#homepage_slider2');	
		        	jssorNavigation('#homepage_slider2');
		        }
		        if ($('#homepage_slider1').is(":visible")) {
			        jssor_slider2_starter("homepage_slider1");
			        ScaleSlider('#homepage_slider1');	
			        jssorNavigation('#homepage_slider1');
		        }
	        });
	        
	        // SPAR CART Pop up //
	        $('#menu').hover(function() {
		        $('.flyout').removeClass('hidden');
	        });
	        $('.flyout').mouseleave(function() {
		        $('.flyout').addClass('hidden');
	        });	        

	        // LOCATION Pop up //
	        $('#menu1').click(function() {	        	
		        $('#locality_error').hide()
		        $('.flyout1').removeClass('hidden1');
		        
		        $('.noStoreData').hide();
		        $('#storelocator-query').val("");
		        if (localStorage.getItem("storeLocator") != null) {
			        $('.flyout2').removeClass('hidden2');
			        var storeSession = JSON.parse(localStorage.getItem("storeLocator"));

			        $('#cityName option[value="' + storeSession.city + '"]').attr("selected", "selected");
			        $('#cityName').val(storeSession.city);
			        $("#storelocator-query").val(storeSession.area);
			        if(ACC.storemapped.storeData["data"] != ""){
			        	ACC.storemapped.refreshNavigation();
			        }else if (ACC.storefinder.storeData["data"] != ""){
			        	ACC.storefinder.refreshNavigation();
			        }			        
			        $('.store-finder-navigation-list').removeClass('on');
			        $('.bottomLinkBx').show();
			        for (var i = 0; i < storeSession.radio.length; i++) {
				        $('#' + storeSession.radio[i] + '').trigger("click");
			        }

		        }
		        $('#storelocator-query').focus();
	        });

	        /*
	         * $('.searchLoc').click(function(){
	         * $('.flyout2').removeClass('hidden2'); });
	         */
	        $('.panel-group').on('hidden.bs.collapse', toggleIcon);
	        $('.panel-group').on('shown.bs.collapse', toggleIcon);

	        /*
	         * $('.v-menu-Sublist .vertical-dropdown-submenu').each(function(){
	         * if($(this).find('li').size()>5){ $(this).addClass('rowSub2') } })
	         */
	        $('.v-menu-Sublist').siblings('ul').find('a').wrapInner('<span class="submenuWraper" />').append(
	            '<span class="doubleRightArrow"/>');
	        mainMenu.menusubCatAnimate();

	        $('.jQAcordan .panel-heading').click(function() {
		        if (!$(this).parents('.jQAcordan').hasClass('nocollapse')) {
			        if ($(this).hasClass('on')) {
				        $(this).removeClass('on').find('.icon').removeClass('iconNw');
			        } else {
				        $('.jQAcordan .panel-heading').removeClass('on').find('.icon').removeClass('iconNw');
				        $('.jQAcordan .panel-heading').siblings().slideUp();
				        $(this).addClass('on').find('.icon').addClass('iconNw');

			        }
		        } else {
			        if ($(this).hasClass('on')) {
				        $(this).removeClass('on').find('.icon').removeClass('iconNw');
			        } else {
				        $(this).addClass('on').find('.icon').addClass('iconNw');
			        }
		        }

		        $(this).siblings().slideToggle()/*
		         * .find('.subjQaccodan
		         * .subjQheading').removeClass('subon').siblings().slideUp()
		         */;

	        })

	        $('.AccrBx')
	            .click(
	                function() {
		                $('.accOrdRowBx .AccrBxS').hide();
		                if ($(this).hasClass('on')) {
			                $(this).removeClass('on').parents('.accOrdRowBx').removeClass('activeRow').find('.icon')
			                    .removeClass('iconNw');
		                } else {
			                $('.AccrBx').removeClass('on').find('.icon').removeClass('iconNw');
			                $('.accOrdRowBx').removeClass('activeRow');
			                $(this).addClass('on').parents('.accOrdRowBx').addClass('activeRow').find('.icon').addClass(
			                    'iconNw');
			                $(this).parent('.AccrBxP').siblings('.AccrBxS').show();
		                }
	                });

	        $('.subjQaccodan .subjQheading').click(function() {
		        if ($(this).hasClass('subon')) {
			        $(this).removeClass('subon').find('.subIcon').removeClass('subIconNw');
		        } else {
			        $('.jQAcordan .subjQheading').removeClass('subon').find('.subIcon').removeClass('subIconNw');
			        $('.jQAcordan .subjQheading').siblings().slideUp();
			        $(this).addClass('subon').find('.subIcon').addClass('subIconNw');
		        }
		        $(this).siblings().slideToggle();
	        })

	        /*
	         * $('.v-menu-Sublist').each(function(){
	         * console.log($(this).siblings().find('li>a').width())
	         * $(this).css({"right":''+(parseInt($(this).siblings().find('li>a').width())+50)+'px'}); })
	         */

	        $('.plpCarousel .owl-item').mouseenter(function() {
		        var curIndx = $(this).index();
		        $(this).addClass('on').siblings().removeClass('on');
		        $('.featureProductBx').css({
			        "left" : '' + ($(this).offset().left - $('.plpCarousel').offset().left) + 'px'
		        })
		        $('.featureProductBx .featureProduct').eq(curIndx).fadeIn().siblings().fadeOut();

	        })

	        $('.featureProductBx .featureProduct').mouseenter(function() {
		        $(this).stop(true, true).fadeIn();
		        var curIndx = $(this).index();
		        $('.plpCarousel .owl-item').eq(curIndx).addClass('on').siblings().removeClass('on');

	        })
	        $('.featureProductBx .featureProduct,.plpCarousel').mouseleave(function() {
		        $('.plpCarousel .owl-item').removeClass('on');
		        $('.featureProductBx .featureProduct').fadeOut();
	        });
	        $('.owl-buttons>*').mouseenter(function() {
		        $('.plpCarousel').trigger("mouseleave");
	        })

	        $('.closeLoc').click(function() {
		        $('.flyout2').addClass('hidden2');
		        $('.flyout1').addClass('hidden1');
		        $('#locality_error').hide()
	        });
	        var mylocationExpendtxt = "";
	        $('.bottomLinkBx').click(function() {
		        // var expTst=$(this).find('a');
		        $(this).hide();
		        $('.store-finder-navigation-list').addClass('on');
		        // if($(this).hasClass('on')){
		        // $(this).removeClass('on');
		        // $('.store-finder-navigation-list').removeClass('on');expTst.text(mylocationExpendtxt);}
		        // else{ $(this).addClass('on');
		        // $('.store-finder-navigation-list').addClass('on');
		        // mylocationExpendtxt=expTst.text();expTst.text("View
		        // Less C&C Centers")}
	        });

	        // SPAR PROMISE //

	        $('#tabs>li').click(
	            function() {
		            var curLiClk = $(this).index();
		            if ($(this).hasClass('on')) {
			            $('#tabs>li').removeClass('on');
			            $('#content, #indicator').fadeOut();
		            } else {
			            $('#tabs>li').removeClass('on');
			            $(this).addClass('on');
			            $('#content, #indicator').fadeIn();
			            $('#content>section').eq(curLiClk).show().siblings().hide()

			            var indicator = $('#indicator'), indicatorHalfWidth = indicator.width() / 2, lis = $('#tabs')
			                .children('li');
		            }

	            });

	        /*
	         * $('#tabs').tabs('#content section', { effect: 'fade', fadeOutSpeed:
	         * 0, fadeInSpeed: 400, onBeforeClick: function(event, index) { var li =
	         * lis.eq(index), newPos = li.position().left + (li.width()/2) -
	         * indicatorHalfWidth; indicator.stop(true).animate({ left: newPos },
	         * 600, 'easeInOutExpo'); } });
	         */
	        /* Code change by pallavi for OTP */
	        $(document).on("click", '#generateOTPForCheckout,#generateOTP', function(e) {
		        e.preventDefault();
		        if ($('#clickNCollect').is(":visible")) {
			        localStorage.removeItem("CNCCLICK")
			        var CNCphone = $('.collectNcenterPhoneBx input').val();
			        if ((CNCphone == "") || (CNCphone.length != 10) || isNaN(CNCphone)) {
				        alertModal("Please enter your 10 digit mobile number");

				        return false;
			        }
			        localStorage.setItem("CNCCLICK", "true");
			        localStorage.setItem("CNCPhoneNo", CNCphone)
			        generateCNCOTP()
		        } else {

			        var phoneNo = document.getElementById('address.phone').value;
			        if ((document.getElementById('address.phone').value == "") || (phoneNo.length != 10) || isNaN(phoneNo)) {
				        alertModal("Please enter your 10 digit mobile number");
				        document.getElementById('address.phone').focus();
				        return false;
			        }

			        var optionalDetailsPageArry = [];
			        if ($(this).attr("id") == "generateOTP") {
				        localStorage.setItem('addressform', 'true');
				        $('#sparAddressForm input').each(function(index) {
					        optionalDetailsPageArry[index] = $(this).val();
				        })
			        } else {
				        $('.register-section input').each(function(index) {
					        optionalDetailsPageArry[index] = $(this).val();
				        })
			        }

			        localStorage.setItem("optionalDetailsPage", JSON.stringify(optionalDetailsPageArry));
			        generateOTP();
		        }

	        })

	        $(document).on("click", '#otpconfirmbtn', function(e) {
		        e.preventDefault();
		        validateUserEnteredOTP();
	        })

	        $(document).on("click", '#cncotpconfirmbtn', function(e) {
		        e.preventDefault();
		        validateCncUserEnteredOTP();
	        })
	        /* Code change by pallavi for OTP END */
	        /* Delivery Address implementation *** tanveers ** Start */
	        	$(document).on("click", '#addressbook input[type="radio"][name="address"]', function(e){
		              $('#loading-image').show();
		              $('#cboxOverlay').show();
		              var addressId = $(this).parents('#deliveryFormSelect').find('#editaddress').val(),
		              $this = $(this);
		              
		              var deliveryForm = $(this).parents('#deliveryFormSelect'),     			              
		              deliveryArea = $(this).parents('#deliveryFormSelect').find('.addressParaOne').find('.addressLongAddress').text(),
		              postalCode = $('#deliveryPostalCode').val();	
		              var addressTown = $(this).parents('#deliveryFormSelect').find('.addressParaOne').find('.addressTown').text();
		              addressTown = addressTown.trim();  
		              
		            //Pincode avilability code
		              if($(this).parent().parent().find('.addressParaOne .pinCode').text().trim())
		            	  {
		            	  location.href="javascript:void(0)";
		            	  }
		              else{
		            	  window.location.href=baseURL+"/checkout/multi/delivery-address/edit/?editAddressCode="+addressId;
		            	 // $("#pincodeAlert").show()
		            	  
		              }
		            
		              // Old Addresses Without Long Address
		              var savedArea = $(this).parents('#deliveryFormSelect').find('.addressParaOne').find('.addressArea').text(),		             
		              savedCity = $(this).parents('#deliveryFormSelect').find('.addressParaOne').find('.addressTown').text(),
		              //savedCityPostalCode = $(this).parents('#deliveryFormSelect').find('.addressParaOne').find('.pinCode').text(),
		              // savedCityPostalCode = savedCityPostalCode.trim();
		              savedDeliveryArea = savedArea + savedCity;
		              
		              $('input:radio[name='+ $this.attr('name')+']').parents('form').removeClass('currentAddress');
		              $this.parents('#deliveryFormSelect').addClass('currentAddress');	
		              
		              $('form.currentAddress').find('#deliveryAddressTown').val(addressTown); 		
		              localStorage.removeItem('currentAddress');
		              $.ajax({
		        	  url : baseURL + "/store-finder/mappedStore",
		        	  data : {
		        	      "addressId" : addressId
		        	  },
		        	  type : "get",
		        	  success : function(data) {		        		  
		        		  data = $.parseJSON(data);			        		  
		        		  if (jQuery.isEmptyObject(data) === false) {		        	     
			        		  if(ACC.storemapped.storeMappingDataValidation()){			        			  
			        			  $('.global-alerts').hide();
			        			  $('form.currentAddress').find('#addressFindStore').val(data.name);		        			     
		        		          $('#addressSubmit').removeAttr("disabled", "disabled");		        		          
		        		          $('#loading-image').hide();
		         				  $('#cboxOverlay').hide();
		        		          return false;
			        		  }        		                 		             
    		        	  }else{        			              
    			              if (deliveryForm.find('.addressParaOne span').hasClass('addressLongAddress')) { 
    	    		            $('#deliveryAddressTown').val(addressTown);    			                		                
    			                var longFormatted_address = deliveryArea;
    			                var regPattern = deliveryArea.replace(/[^\x20-\x7E]/gmi, "");
    			                var localArea = encodeURI(regPattern);
    			               
    			                var address = addressTown;

    			                if (localArea.indexOf('&') > -1) {
    				               localArea = localArea.replace("&", "%26");
    			                }

    			                ACC.storemapped.googleAddressStatusCheck(address, postalCode, deliveryArea, longFormatted_address, addressId, localArea);

    			              } else {	    			            	      
   			                   $('#deliveryAddressTown').val(addressTown);    			             
    			                var longFormatted_address = savedDeliveryArea;
    			                var regPattern = longFormatted_address.replace(/[^\x20-\x7E]/gmi, "");
    			                var geocoder = new google.maps.Geocoder();
    			                var localArea = encodeURI(regPattern);

    			                var address = addressTown;

    			                if (localArea.indexOf('&') > -1) {
    				               localArea = localArea.replace("&", "%26");
    			                }
    			                ACC.storemapped.googleAddressStatusCheck(address, postalCode, savedDeliveryArea, longFormatted_address, addressId, localArea);

    			              }	
    		        	    }				        	 
    		        	  },complete:function(){
    		        		  $('#loading-image').hide();
    	    				  $('#cboxOverlay').hide(); 
    		        		  $this.removeClass('currentAddress');
    		        	  }
    		        	  
		              });
		            
	        	   });
	        if (window.location.pathname.indexOf("/multi/delivery-address/add") >= 0) {
          	  $(this).find('#sparAddressForm .info-text').hide();
            }
	        
	        var pinCode=$("#sparAddressForm input[name='postcode']").val();
          	if(pinCode=="NA")
          	{
          		$("#pincodeAlert").css("display","block");
          		$("input[name='postcode']").keyup(function(){
          		  $("#pincodeAlert").css("display","none");
          		});
          	} 
          	else
          	{
          		$("#pincodeAlert").css("display","none");
          	}
          	
          	
	        $('#homePageForm').on('submit', function(e) {
		        e.preventDefault();
		        $.ajax({
		          type : 'post',
		          url : $('#homePageForm').attr("action"),
		          data : $('#homePageForm').serialize(),
		          success : function() {
			          $('.closeLoc').trigger("click");
			          window.location.reload()
		          }
		        });

	        });

	        $(document).on("click", '.signOutBtn', function() {
		        localStorage.removeItem("defaultStoreLocation");
		        localStorage.removeItem("storeLocator");
		        localStorage.clear();
		        sessionStorage.clear();
	        });
	        // Prevent User to Click until page load
	        
	        //Start-Facebook Pixel code 
	        $(function() {
		        if ($('body').hasClass('page-productGrid') || $('body').hasClass('page-search')
		            || $('body').hasClass('page-multiStepCheckoutSummaryPage')) {
			        $(document).on('click', '.add_to_cart_form [id*=variantAdd_], #addToCartButton', function(e) {
				        $(this).attr('disabled', 'disabled');
			        });
			        $('#loading-image').show();
			        $('#cboxOverlay').show();
		        }
		        $(window).on('load', function() {
			        $(document).on('click', '.add_to_cart_form [id*=variantAdd_], #addToCartButton', function(e) {
				        $(this).removeAttr('disabled');
			        });
			        $('#loading-image').hide();
			        $('#cboxOverlay').hide();
			        $('body.page-productGrid, body.page-search').css('pointer-events', 'all'); // activate
			        // pointer-events
		        });
	        });
	       
	        /*$(document).on('click', '.add_to_cart_form [id*=variantAdd_], #addToCartButton', function(e) {
		        e.preventDefault();
		        e.stopPropagation();
		        $(this).attr("disabled", "disabled");
		        var priceVal = $(this).parent().find('input[name="productPostPrice"]').val();
		        if (localStorage.getItem("defaultStoreLocation")) {
			        fbq('track', 'AddToCart', {
			          value : priceVal,
			          currency : 'INR'
			        });
			        $(this).parents('form').submit();
		        } else {
			        ACC.product.displayAddToCartPopup();
		        }
		        return false;
		        $(this).prop("disabled", false);
	        });*/
	       
	      //AddToCart Button
	     $(document).on('click', ' #addToCartButton', function(e) {
		       e.preventDefault();
		       e.stopPropagation();
		        $(this).attr("disabled", "disabled");
		        var productId = $('.product-list').find('input[name="productCodePost"]').val(),
		        productName = $('.product-list').find('.pdpInfoBx h4').text(),
		        productQty = $('#pdpAddtoCartInput').val(),
		        productPrice = $('.product-list .priceTextsBG').find('.priceMain').text().replace(/Rs/g,''),
		        productCategory=$('.product-list ul.breadcrumb li').last().prev().find('a').text();
		       // priceVal = $(this).parent().find('input[name="productPostPrice"]').val();
		        currency="INR";
		        if (localStorage.getItem("defaultStoreLocation")) {
			        fbq('track', 'AddToCart', {
			        ContentId: productId,
			        ContentName:productName,
			        ContentQty:productQty,
			        ContentPrice: productPrice,
			        ContentCategory:productCategory, 
			       // value : priceVal,
			       //currency : priceVal
			        Currency:currency,
			      });
			        
			        $(this).parents('form').submit();
		        } else {
			        ACC.product.displayAddToCartPopup();
		        }
		        return false;
		        $(this).prop("disabled", false);
		        
	        });
	      //ADD button 
	     $(this).on('click', '.add_to_cart_form [id*=variantAdd_],.add-btn', function(e) {
	        	productId = $(this).parents('.cart').prev().find('input[name="partNumber"]').val(),
	        	productName = $(this).parents('.cart').find('input[name="productNamePost"]').val(),
	        	productPrice = $(this).parents('.cart').find('input[name="productPostPrice"]').val(),
	        	productCategory=$('.breadcrumb-section .breadcrumb').find('li.active').text(),
	        	productQty =$(this).parents('.cart').prev().find('input[name="pdpAddtoCartInput"]').val();
	        	currency="INR";
	        	 if (localStorage.getItem("defaultStoreLocation")) {
	        	fbq('track', 'AddToCart',
	        		{  
		        		ContentId:productId,
		        		ContentName:productName, 
		        		ContentPrice:productPrice,
		        		ContentCategory:productCategory,
		        		ContentQty:productQty,
		        		 Currency:currency,
		        	})
	        	 }
		     else {
			        ACC.product.displayAddToCartPopup();
		        }
	        	});
	     
	        if (window.location.pathname.indexOf("/optionalDetails") >= 0) {
		        if (getCookie("register") === "") {
			        fbq('track', 'CompleteRegistration');
			        createCookie("register", "register", 1);
		        }
	        }

	        function createCookie(cname, value, days) {
		        var date = new Date();
		        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		        var expires = "; expires=" + date.toGMTString();
		        document.cookie = cname + "=" + value + expires + "; path=/";
	        }

	        function deleteCookie(cname) {
		        createCookie(cname, "", -1);
	        }

	        function getCookie(cname) {
		        var name = cname + "=";
		        var ca = document.cookie.split(';');
		        for (var i = 0; i < ca.length; i++) {
			        var c = ca[i];
			        while (c.charAt(0) == ' ') {
				        c = c.substring(1);
			        }
			        if (c.indexOf(name) == 0) {
				        return c.substring(name.length, c.length);
			        }
		        }
		        return "";
	        }

	       /* if (window.location.pathname.indexOf("/orderConfirmation") >= 0) {
		        if (getCookie("orderCon") !== "orderCon") {
			        var orderTotalValue = $("#orderTotalValue").val();
			        fbq('track', 'Purchase', {
			          value : orderTotalValue,
			          //currency : 'INR',
			          store:'Spar Online Store'
			        });
			        createCookie("orderCon", "orderCon", 1);
		        }
	        }*/
	        // At Order Confirmation
	        if (window.location.pathname.indexOf("/orderConfirmation") >= 0) {
		        if (getCookie("orderCon") !== "orderCon") {
		        	 id= $('.order-details .margTop').find('h5.order-no').text(), 
		        	 qty= $('.order-details .margTop').find('h5.order-item').text(),
		        	 currency="INR";
		        	store="Spar Online - Store";
			        var orderTotalValue = $("#orderTotalValue").val();
			        fbq('track', 'Purchase', {
			          OrderNumber:id,
			          TotalItem:qty,
			          Currency:currency,
			          Store:store,
			          TotalAmout : orderTotalValue
			         
			        });
			        createCookie("orderCon", "orderCon", 1);
			        }
		        }
	        /* $(document).on('click', '.minicart-checkout', function(e) {
		        deleteCookie("orderCon");
		        fbq('track', 'InitiateCheckout');
	        });*/
	        
	        //PROCEED TO CHECKOUT button
	       $('.checkoutButton').click(function() {
	        	deleteCookie("orderCon");
	        	var allProductId = "";
	        	var allProductName = "";
	        	var allQuantity = 0;
	        	var allCategory = "";
	        	$('.cartItemsBx .cart-item-details').each(function(){	
	        	productId = $(this).find('.color-black').attr('href'),
	        	productId = productId.substr(productId.lastIndexOf('/') + 1),
	        	allProductId = allProductId + productId + ",";
	        	productName = $(this).find('.thumb').find('img').attr('title'),
	        	allProductName = allProductName + productName + ",";
				//productPrice = $('.cartItemsBx .cart-item-details').find('.total').find('.item-heading').next().text(),
	        	productPrice = $('.priceBlock').find('.totalPrice').find('.price-total-wrapper').find('.price-total-value').text().replace(/Rs/g,''),
				productQty = $(this).find('input[name="quantity"]').val(),
				allQuantity = allQuantity + parseInt(productQty);
	        	productCategory= $(this).prev().find('h5').text(),
	        	allCategory = allCategory + productCategory;
	        	currency="INR";
	        	//alert (productId + ', ' + productName + ', ' + productPrice + ', ' + productQty + ', ' + productCategory); 
	        	});
	        	
	        	allProductId = allProductId.substring(0,allProductId.length-1);
	        	allProductName = allProductName.substring(0,allProductName.length-1);
	        	allCategory = allCategory.substring(0,allCategory.length-1);
	        	fbq('track', 'InitiateCheckout',
		        	{
			        	ContentId:allProductId,
			            ContentPrice:productPrice,
			            ContentName:allProductName,
			            ContentCategory:allCategory,
			            ContentQty:allQuantity,
			            Currency:currency,
	                 });
	        	/*createCookie("cart", "cartValues", 1);{
	        	var cartValues=allProductId + ', ' + productPrice + ', ' + allProductName + ', ' + allCategory + ', ' + allQuantity;
	        	var cartCookies=JSON.stringify(cartValues);
	        	document.cookie=cartCookies;}*/
	        });
	     
	        //VIEW CONTENT: on click image of product on page product grid.
	       if ($('body').hasClass('page-productGrid')) {
	        	$('.variantImage').click(function() {
	        	deleteCookie("orderCon");
	        	productId = $(this).parents('.single-products').find('input[name="partNumber"]').val(),
	        	productName = $(this).parents('.single-products').find('.variantImage img').attr('title'),
		        productPrice = $(this).parents('.single-products').find('.orignal-price').text().replace(/Rs/g,''),
		        productCategory=$('.breadcrumb-section .breadcrumb').find('li.active').text(),
				productQty =$('.single-products').find('input[name="pdpAddtoCartInput"]').val();
	        	currency="INR";
		        fbq('track', 'ViewContent',
		        	{
			        	ContentId:productId,
			            ContentPrice:productPrice,
			            ContentName:productName,
			            ContentCategory:productCategory,
			            ContentQty:productQty,
			            Currency:currency,
		            });
	        });
	       }
	     //VIEW CONTENT: on click image of product on page cart.
	 	    	   $(this).on('click', '.thumb', function(e) {
	 		       deleteCookie("orderCon");
	 	        	productId = $(this).parents('.cartItemsBx .cart-item-details').find('.color-black').attr('href'),
	 	        	productId = productId.substr(productId.lastIndexOf('/') + 1),
	 	        	productName = $(this).parents('.cartItemsBx .cart-item-details').find('.thumb').find('img').attr('title'),
	 				productPrice = $(this).parents('.cartItemsBx .cart-item-details').find('.total').find('.item-heading').next().text().replace(/Rs/g,''),
	 				productQty = $(this).parents('.cartItemsBx .cart-item-details').find('input[name="quantity"]').val(),	
	 	        	productCategory= $(this).parents('.cartItemsBx .cart-item-details').prev().find('h5').text(),
	 	        	currency="INR";
	 	        	//alert (productId + ', ' + productName + ', ' + productPrice + ', ' + productQty + ', ' + productCategory);
	 	        	 fbq('track', 'ViewContent',
	 		        	{
	 			        	ContentId:productId,
	 			            ContentPrice:productPrice,
	 			            ContentName:productName,
	 			            ContentCategory:productCategory,
	 			            ContentQty:productQty,
	 			           Currency:currency,
	 	        	});
	 	       });
	        $(document).on("keyup", ".js-site-search-input", function(e) {
		        var searchData = $('form[name="search_form_SearchBox"]').find(".js-site-search-input").val();
		        facebookSearchData(searchData);
	        });
	        $(document).on("keypress", ".js-site-search-input", function(e) {
		        if (e.which == 13) {
			        var searchData = $('form[name="search_form_SearchBox"]').find(".js-site-search-input").val();
			        facebookSearchData(searchData);
		        }
	        });
	        $(document).on("submit", '.search_form', function(e) {
		        var searchData = $('form[name="search_form_SearchBox"]').find(".js-site-search-input").val();
		        facebookSearchData(searchData);

	        });

	        $(document).on("submit", '#sparRegisterForm', function(e) {
		        deleteCookie("register");
	        });
	        function facebookSearchData(searchData) {
		        if (searchData.length > 0) {
			        fbq('track', 'Search', {
				        search_string : searchData
			        });
		        }
	        }
	        //END- Facebook pixel code
	        
	        // Wallet Start Here----
	        var walletIntialAmt = $('#customerWalletAmount').val();
	        var balanceDue = $('.paymentBxs .amt-paid>span').text().slice(2);
	        $("input#customerWalletAmount").change(function() {
		        var userEnterAmt = $(this).val();
		        var regEx = /^(?!0\d)\d*(\.\d+)?$/mg;
		        if (regEx.test(userEnterAmt)) {
			        if (parseFloat(walletIntialAmt) < parseFloat(userEnterAmt) || parseFloat(userEnterAmt) <= 0) {
				        alert("Please enter valid Amount");
				        return false;
			        }
		        } else {
			        alert('Please enter valid value');
			        return false;
		        }
	        });

	        $(document).on(
	            "click",
	            "#payment_wallet_button",
	            function(e) {
		            e.preventDefault();
		            e.stopPropagation();
		            var a = $('#walletPaymentProcessForm'), url = a.attr("action");
		            $.ajax({
		              url : url,
		              type : "post",
		              data : a.serialize(),
		              success : function(response) {
			              $('.paymentBxs .amt-paid>span').text(response.orderWalletAmount.formattedValue);
			              $('.paymentBxs #customerWalletAmount').val(response.customerWalletTotal.value);
			              var usedFromWallet = walletIntialAmt - response.customerWalletTotal.value;
			              $('.pay-summary .wallet').removeClass('hidden');
			              $('.pay-summary .wallet').find('.pay-items').next().find('.amt').text(
			                  "Rs" + (parseFloat(balanceDue) - response.orderWalletAmount.formattedValue.slice(2)));			              
			              $('#balanceTotalDue').val(response.orderWalletAmount.value);
			              $('#payment_wallet_button').attr("disabled", "disabled");
			              if (parseInt(response.orderWalletAmount.value) == "0") {
				              $('#payment_button,#payment_button1').attr("disabled", "disabled").css('opacity', '.5');
				              $('#payment_confirm_button').removeClass("hidden").prop("disabled", false);
			              } else {
				              $('#payment_button,#payment_button1').removeAttr("disabled").css('opacity', '1');
				              $('#payment_confirm_button').attr("disabled", "disabled")
			              }
			              $('.pay-detail span.remove-entry-button').click(function() {
				              $('.pay-summary .wallet').addClass('hidden');
				              $('#payment_confirm_button').addClass("hidden");
				              $('.paymentBxs .amt-paid>span').html(balanceDue);
				              $('#payment_wallet_button').prop("disabled", false);
				              $('#payment_button,#payment_button1').prop("disabled", false).css('opacity', '1');
				              // alert("customerwallet
				              // "+response.customerWalletTotal.value
				              // +" &
				              // "
				              // +response.paidByWalletAmount.value);
				              // var
				              // newCustomerWallet=parseFloat(walletIntialAmt)
				              // +
				              // parseFloat(response.paidByWalletAmount.value);
				              // $('.paymentBxs
				              // #customerWalletAmount').val(newCustomerWallet);
				              $('.paymentBxs #customerWalletAmount').val(walletIntialAmt);
				              var a1 = $('#cancelWalletPaymentDetailsForm'), cancelUrl = a1.attr("action");
				              $.ajax({
				                url : cancelUrl,
				                type : "post",
				                data : a.serialize(),
				                success : function(response) {
					                // $('.paymentBxs
					                // #customerWalletAmount').val(response.customerWalletTotal.value);
				                }
				              });
			              });

		              }
		            });

	            });
	        $('#customerWalletAmount').keypress(function(e) {
		        if (e.which == 13) {// Enter key pressed
			        $('#payment_wallet_button').click();// Trigger
			        // search button
			        // click event
			        return false;
		        }
	        });
	        // Wallet End Here----
	        $('#vouchercode').keyup(function(){
	  	    	  this.value=this.value.toUpperCase();
	            });	
	          $(document).on(
	            "submit",
	            '#voucherCodeForm',
	            function(e) {
		            e.preventDefault();
		            $('.global-alerts').remove();
		            localStorage.removeItem("ReleaseMassage");
		            var urlToCheckVoucherValueExceeding = $('#voucherCodeForm').attr("action");		            
		            var voucherCode = $("#vouchercode").val();
		            var that = $(this);
		            var requestSent = false;
		            if (!requestSent) {
			            requestSent = true;
			            $.ajax({
			              cache : false,
			              url : urlToCheckVoucherValueExceeding,
			              data : that.serialize(),
			              type : "get",
			              success : function(msg) {
			            	
				              if (!that.hasClass('VoucherFormSubmition')) {
					              if (voucherCode == "" || voucherCode == "null" || voucherCode == undefined) {
						              alertModal('Please enter coupon code');
						              return false;
					              } else if (msg === '') {
					            	
						              alertModal('Coupon code is invalid. Please enter a valid coupon code.')
						              return;
					              }else if (msg.startsWith("Voucher", 0))
					            	  {
					            	  alertModal(msg);
					            	  }
					              else if (msg == "false") {
						              $('#cboxOverlay').show();
						              formSubmition();
					              } else {
						              confirmModal(
						                  'Entire coupon amount will be consumed against this order. Do you want to proceed?',
						                  'formSubmition()')
						              return;
					              }
				              }
			              }
			            });

		            }
		            return false;
	            });
	        if ($('#voucherCodeForm')) {
		        var a = localStorage.getItem("ReleaseMassage");
		        if (a != null) {
			        var b = a.split('$$');
			        if (b[0] != "" && b[1] != "undefined") {
				        var c = '<div class="global-alerts"><div class="' + b[1]
				            + '"><button class="close" aria-hidden="true" data-dismiss="alert" type="button">x</button>' + b[0]
				            + '</div></div>'
				        $('.content-wrapper>div:eq(0)').before(c);
				        localStorage.removeItem("ReleaseMassage")
			        }
		        }
	        }
	        $(document).on("click", '#releaseBtn', function(e) {
		        $('#loading-image').show();
		        $('#cboxOverlay').show();
		        var voucherCode = $("#vouchercode").val();
		        var urlToReleaseVoucher = $(".js-voucher-release").data("url");
		        window.location.href = urlToReleaseVoucher + "?vouchercode=" + voucherCode;
		        /*
		         * var delay = 2000; $.ajax({ cache : false, url :
		         * urlToReleaseVoucher, data : { "vouchercode" : voucherCode }, type :
		         * "get", success : function(msg) { setTimeout(function() {
		         * $('#voucherCodeForm').removeClass('VoucherFormSubmition').attr("action",
		         * $(".user-confirmation").data("url")) var a =
		         * $(msg).find('.global-alerts'), b = a.find('.globalAlertMsg');
		         * localStorage.setItem("ReleaseMassage",
		         * a.find('.globalAlertMsg').text() + '$$' +
		         * a.find('.alert').attr("class")) //
		         * $('.content-wrapper>div:eq(0)').before(a);
		         * $('#cboxOverlay').hide(); $('#loading-image').hide();
		         * $('body').scrollTop(0); window.location.reload(); }, delay); } //
		         * $('#voucherCodeForm').addClass('VoucherFormSubmition').attr("action", //
		         * $(".js-voucher-apply").data("url")).submit(); });
		         */
	        });

	        // Voucher End Here----
        });

function formSubmition() {
	var delay = 2000;
	$("#confirmModal").hide();
	$('#loading-image').show();
	var voucherCode = $("#vouchercode").val();
	var urlApply = $(".js-voucher-apply").data("url");
	$('#voucherCodeForm').addClass('VoucherFormSubmition');
	window.location.href = urlApply + "?vouchercode=" + voucherCode;
	/*
	 * $.ajax({ cache : false, url : urlApply, data : { "vouchercode" :
	 * voucherCode }, type : "get", success : function(msg) {
	 * setTimeout(function() {
	 * $('#voucherCodeForm').removeClass('VoucherFormSubmition').attr("action",
	 * $(".user-confirmation").data("url")) var a = $(msg).find('.global-alerts'),
	 * b = a.find('.globalAlertMsg'); localStorage.setItem("ReleaseMassage",
	 * a.find('.globalAlertMsg').text() + '$$' + a.find('.alert').attr("class")) //
	 * $('.content-wrapper>div:eq(0)').before(a); $('#cboxOverlay').hide();
	 * $('#loading-image').hide(); $('body').scrollTop(0);
	 * window.location.reload(); }, delay); } //
	 * $('#voucherCodeForm').addClass('VoucherFormSubmition').attr("action", //
	 * $(".js-voucher-apply").data("url")).submit(); });
	 */
}

function nextTab(elem) {
	$(elem).next().find('a[data-toggle="tab"]').click();
}
function prevTab(elem) {
	$(elem).prev().find('a[data-toggle="tab"]').click();
}

$(function() {
	var action;
	$(".number-spinner button").mousedown(function() {
		btn = $(this);
		input = btn.closest('.number-spinner').find('input');
		btn.closest('.number-spinner').find('button').prop("disabled", false);

		if (btn.attr('data-dir') == 'up') {
			action = setInterval(function() {
				if (input.attr('max') == undefined || parseInt(input.val()) < parseInt(input.attr('max'))) {
					input.val(parseInt(input.val()) + 1);
				} else {
					btn.prop("disabled", true);
					clearInterval(action);
				}
			}, 50);
		} else {
			action = setInterval(function() {
				if (input.attr('min') == undefined || parseInt(input.val()) > parseInt(input.attr('min'))) {
					input.val(parseInt(input.val()) - 1);
				} else {
					btn.prop("disabled", true);
					clearInterval(action);
				}
			}, 50);
		}
	}).mouseup(function() {
		clearInterval(action);
	});

});

/* Code change start by sumit */

/*
 * $(document).on("click",'#generateOTPForRegis', function(e){
 * e.preventDefault(); if(document.getElementById('phone').value == ""){
 * alert("Please enter your 10 digt mobile number"); return false; }
 * generateOTPForRegistration(); $("#myModal1").modal() });
 * $(document).on("click",'.otpConfirmbtnForRegis', function(e){ alert("2");
 * validateUserEnteredOTPForRegistration(); }); function
 * generateOTPForRegistration(){ var
 * mobilenum=document.getElementById('phone').value
 * url=$(".js-registeration-otp").data("url"); alert("url:::"+url); $.ajax({
 * url:url, data:{"mobilenum" : mobilenum}, type: "get", success:function(res){ }
 * }); } function validateUserEnteredOTPForRegistration(){ var
 * userenteredotp=document.getElementById('userOTP').value
 * url=$(".js-registeration-otp").data("url"); $.ajax({ url:url, data:{"userOTP" :
 * userenteredotp}, type: "get", success:function(res){ } }); }
 */

/* Code change end here */

function myFunction() {
	location.reload();
}

function validation() {
	var isValid = true;
	// perform your validation on form and update isValid variable accordingly.
	if (isValid) {
		window.location = ''; // your desired location
	}

	return false; // always return false to prevent form from submission.
}

function initialize() {
	var input = document.getElementById('locArea');
	var autocomplete = new google.maps.places.Autocomplete(input);
}

function toggleIcon(e) {
	$(e.target).prev('.panel-heading').find('.more-less').toggleClass('fa-plus fa-minus');
}

/* Change start by pallavi for OTP implementation */

function generateOTP() {
	var mobilenum = document.getElementById('address.phone').value
	url = $(".js-otpgenerate-otpgenerate").data("url");
	$.ajax({
	  url : url,
	  data : {
		  "mobilenum" : mobilenum
	  },
	  type : "get",
	  success : function(res) {
		  if (res) {
			  window.location.reload();
		  } else {
			  localStorage.setItem('addressform', 'false');
			  localStorage.removeItem("optionalDetailsPage");
			  alertModal("Your Mobile Number has already been used. Please use another Number.");
		  }
	  }
	});
}

function generateCNCOTP() {

	var mobilenum = document.getElementById('cnc.phone').value
	url = $(".js-otpgenerate-otpgenerate").data("url");
	$.ajax({
	  url : url,
	  data : {
		  "mobilenum" : mobilenum
	  },
	  type : "get",
	  success : function(res) {
		  window.location.reload();
	  }
	});
}

function validateCncUserEnteredOTP() {

	var cncuserenteredotp = document.getElementById('cncUserOTP').value;
	if (cncuserenteredotp == "") {
		alertModal('Please enter OTP code.');
		$('#userOTP').focus();
		return false;
	}
	if ((cncuserenteredotp != $('#thisField').text())) {
		alertModal('Please enter valid OTP code.');
		$('#userOTP').focus();
		return false;
	}
	url = $(".js-otpgenerate-otpgenerate").data("url");
	$.ajax({
	  url : url,
	  data : {
		  "userenteredotp" : cncuserenteredotp
	  },
	  type : "get",
	  success : function(res) {
		  localStorage.setItem("CNCMobile", "true");
		  $("#cncModal").modal("toggle")
	  }
	});

}

function validateUserEnteredOTP() {
	var userenteredotp = document.getElementById('userOTP').value;
	var lrOptStatus = $('#lrOptStatus').val();
	var otpNumber=$('#thisField').text().trim();
	if (userenteredotp == "") {
		alertModal('Please enter OTP code.');
		$('#userOTP').focus();
		return false;
	}
	if (userenteredotp != otpNumber){
		alertModal('Please enter valid OTP code.');
		$('#userOTP').focus();
		return false;
	}
	url = $(".js-otpgenerate-otpgenerate").data("url");
	$.ajax({
	  url : url,
	  data : {
	    "lrOptStatus" : lrOptStatus,
	    "userenteredotp" : userenteredotp
	  },
	  type : "get",
	  success : function(res) {
		  $("#myModal").modal("toggle");
		  $("#landMarkMyAccountOtp").modal("toggle");
	  }
	});

}

function alertModal(data) {
	$('#alertModal').modal().find('.modal-body').html(data);
}
function confirmModal(data, event) {
	$('#confirmModal').modal().find('.modal-body').html(data)
	$('.confirmModal').attr('onClick', event);
}

/* Change end here */

/* Code to search location on press of enter key */
$(document).on("keydown", "#storelocator-query", function(e) {
	if (e.keyCode == 13) {
		e.stopPropagation();
		e.preventDefault();
		enterPress = true;
		$("#findStoresNearMe").trigger("click");
	}
});
// Tab Index for Register form
$(function() {
	$("#sparRegisterForm select[name='titleCode']").attr('tabindex', "1");
	$("#sparRegisterForm input[name='firstName']").attr('tabindex', "2");
	$("#sparRegisterForm input[name='lastName']").attr('tabindex', "3");
	$("#sparRegisterForm input[name='email']").attr('tabindex', "4");
	$("#sparRegisterForm input[name='pwd']").attr('tabindex', "5");
	$("#sparRegisterForm input[name='checkPwd']").attr('tabindex', "6");
	$("#sparRegisterForm input[name='whetherEmployee']").attr('tabindex', "7");
	$("#sparRegisterForm input[name='whetherSubscribedToPromotion']").attr('tabindex', "8");
	$("#sparRegisterForm input[name='whetherSubscribedToLandmark']").attr('tabindex', "9");
	$("#sparRegisterForm button[type='submit']").attr('tabindex', "10");
	$("#sparRegisterForm button[type='button']").attr('tabindex', "11");
	$("#updatePasswordForm input[name='currentPassword']").attr('placeholder', "Current Password");
	$("#updatePasswordForm input[name='newPassword']").attr('placeholder', "New Password");
	$("#updatePasswordForm input[name='checkNewPassword']").attr('placeholder', "Confirm Password");
	$("#updatePwdForm input[name='pwd']").attr('placeholder', "New Password");
	$("#updatePwdForm input[name='checkPwd']").attr('placeholder', "Confirm Password");
	$("#forgottenPwdForm input[name='email']").attr('placeholder', "Enter your Email");		
});

// Set Loader for Cart Page
$(".page-cartPage .updateForm").submit(function() {
	$('#loading-image').show();
	$('#cboxOverlay').show();
});

/* Responsive Stuff */
var Main, deviceTarget = {
  customStyle : function() {
	  $(".block-vertical-menu-list").show();
	  $('.category-products').find('.panel-collapse').hide();
	  $(".vertical-menu-list").find('span.rightArow').remove();
	  $(".block-vertical-menu-list li").find('a').after("<span class='fa fa-plus-circle'></div>");

	  // $(".top-navigation .btn-group").find('a').append("<span class='fa
	  // fa-plus'></div>");
	  // console.log($('.top-navigation.nav .category .btn-group
	  // .btn-group').children().length);

	  // $('.top-navigation').insertAfter(".banner");
	  $('.product-menu .category-products').find('.fa-caret-down').removeClass().addClass('fa fa-plus');
	  $('.product-menu .category-products .panel-heading').find('i').removeClass('fa-caret-down');
	  $('.product-menu .category-products .panel-heading').find('i').addClass('fa-plus');

	  // Hamburger Navigation Setup
	  $('.top-navigation').clone().insertAfter('header').addClass('nav');
	  $(".top-navigation.nav .naviRight")
	      .prepend(
	          "<div class='btn-group shopby'><a href='#' role='button' class='btn btn-green'><span>Shop by Category</span></a></div>");
	  $('.top-navigation.nav').find('.block-vertical-menu').appendTo('.shopby');
	  $('.top-navigation.nav .v-menu-Sublist').find('.fa-plus-circle').remove();

	  $(function() {
		  $('header .fa-bars').click(function(e) {
			  $(this).parents('header').next().show();
			  $('.top-navigation.nav .title-block').hide();
			  var category = $('.category');
			  if (category.hasClass('visible')) {
				  category.animate({
					  "left" : "-1000px"
				  }, 300).removeClass('visible').hide();
				  $(this).removeClass('active');
			  } else {
				  category.animate({
					  "left" : "0px"
				  }, 200).addClass('visible');
				  $('#cboxOverlay').show();
				  $(this).toggleClass('active');
			  }

		  });
		  $('body').on('click', function(e) {
			  var category = $('.category');
			  if (!$(e.target).closest('header .fa-bars,.category').length) {
				  if (category.hasClass('visible')) {
					  category.animate({
						  "left" : "-1000px"
					  }, 500).removeClass('visible');
					  $('#cboxOverlay').hide();
					  $('header .fa-bars').removeClass('active');
				  }
			  }
		  });

	  });

	  $('.top-navigation.nav .category .btn-group').find('.btn-group').each(function() {
		  if ($(this).children().length > 1) {
			  $(this).find('a.btn').append("<span class='fa fa-plus-circle'></div>")
		  }
	  });
	  $(".top-navigation .btn-group-justified .btn-group").on('click', '.btn', function() {
		  $(this).next().toggle('slide', {
			  direction : 'up'
		  }, 200);
		  $(this).find('.fa-plus-circle').toggleClass('fa-minus-circle');
	  });

	  // Product Category Listing Left Pannel Accordion
	  $('.product-menu .category-products .panel-heading').click(function() {
		  if (!$(this).hasClass('on')) {
			  $(this).find('i').removeClass('fa-plus-circle');
			  $(this).find('i').addClass('fa-minus-circle');
		  } else if ($(this).hasClass('on')) {
			  $(this).find('i').removeClass('fa-minus-circle');
			  $(this).find('i').addClass('fa-plus-circle');
		  }
	  });
	  $('.block-vertical-menu').find('.vertical-head').hide();

	  /*
	   * $('.block-vertical-menu').find('.vertical-head').click(function() {
	   * $(this).next().toggle('slide', { direction : 'up' }, 200); });
	   */
	  $('.toggle-menu').click(function() {
		  $(this).next().slideToggle('fast');
	  });
	  $(".btn-group-justified .dropdown-menu").hide();
	  $(".btn-group-justified .dropdown-toggle").click(function() {
		  $(this).find('.dropdown-menu').show();
	  });
	  if ($('.top-navigation .btn-group p.btn').text() == "") {
		  $('.top-navigation .btn-group p.btn').parent().hide();
	  } else {
		  $('.top-navigation .btn-group p.btn').parent().show();
	  }

	  // Menu Styling End here

	  // Register Last Name Postion change
	  $(function() {
		  $('#sparRegisterForm').find('.row.lname').insertBefore('.row.email');
	  });
	  $('.middle-banners').insertBefore(".footerBlock").removeClass('hidden-xs');
	  $("#product-facet h2").removeClass('hidden-xs');
	  $("#product-facet h2").next().removeClass('hidden-xs');
	  $('.top-navigation.home').remove();

  },
  settings : {
	  menuHandler_myaccount : $('.my-account ul li')
  },

  init : function() {
	  Main = this.settings;
	  this.menu_1();
	  this.miniCart();
  },

  miniCart : function() {
	  $('.mini-cart-count').click(function() {
		  var l = window.location;
		  var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
		  location.href = base_url + '/cart';
	  });
  },

  menu_1 : function() {
	  $(".block-vertical-menu-list li").on('click', '.fa', function() {
		  $(this).parents('li').addClass('active');
		  if ($(this).parents('li').hasClass('active') && $(this).hasClass('fa-plus-circle')) {
			  $(this).parent().next().show();
			  $(this).parent().parent().next().show();
			  $(this).addClass('fa-minus-circle');
			  $(this).removeClass('fa-plus-circle');
		  } else if ($(this).parents('li').hasClass('active') && $(this).hasClass('fa-minus-circle')) {
			  $(this).parent().next().hide();
			  $(this).parent().parent().next().hide();
			  $(this).addClass('fa-plus-circle');
			  $(this).removeClass('fa-minus-circle');
			  $(this).parents('li').removeClass('active');
		  }
	  });
  }
};
if ($(window).width() < 480) {
	deviceTarget.customStyle();
	deviceTarget.init();
	$(document).ready(function() {
		$('body').css('touch-action', 'auto');
		$('body').click(function (event) 
		  {
		     if(!$(event.target).closest('.shop-menu').length && !$(event.target).is('.shop-menu')) {
		       $(".shop-menu ul").slideUp('fast');
		     }     
		  });
	});
}
if (/iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
	deviceTarget.miniCart();
}
// Location Search fixes
$(function() {
	$('#storeFinderForm #cityName').change(function() {
		$(this).parents('form').next().find('.flyout2').addClass('hidden2');
		var cityVal = $(this).val();
		$("#storelocator-query").val('');
		$('#locality_error').hide();
		// Check the Current City and Disable Area box
		if (cityVal == "SelectValue") {
			$('#storelocator-query').attr("disabled", "disabled");
			$('#storelocator-query').val("");
		} else {
			$('#storelocator-query').removeAttr('disabled');
		}
		if ($(window).width() < 480) {
			if ($('body').hasClass('page-productGrid') || $('body').hasClass('page-search')) {
				$("body").focus();
			}
		}
	});
});

// Restrict Cart Maximum Quanity
var cartQuantityChk = {
	maxQtnInput : function() {
		$('.update-entry-quantity-input').each(function() {
			var quantity = $(this).val();
			var maxQuantity = $(this).parents('form').find('.productMaxOrderQuantity').val();
			if (quantity == maxQuantity) {
				$(this).parents('form').find('.update-entry-quantity-input').val(maxQuantity)
				$(this).parents('form').find('.js-qty-selector-plus').attr("disabled", "disabled");
				return false;
			} else {
				$(this).parents('form').find('.js-qty-selector-plus').removeAttr("disabled");
			}

		});
	}
}
$(function() {
	cartQuantityChk.maxQtnInput();
	clearFormFeilds();
	var selectedCity = $("#cityName").val();
	$("#deliveryCityName").val(selectedCity);
	if (selectedCity == "SelectValue") {
		selectedCity = 'Bengaluru';
	}
	var storeSession = JSON.parse(localStorage.getItem("storeLocator"));
	if (storeSession) {
		$("#deliveryCityName").val(storeSession.city);
		$("#cityName").val(storeSession.city);
	} else {
		$("#deliveryCityName").val(selectedCity);
	}
	// Home page Navigation Freeze
	if ($('div').is('.top-navigation.home')) {
		var homeNav = $('.top-navigation .block-vertical-menu').clone();
		$('.top-navigation.home').html(homeNav);
		$('.top-navigation.home').find('.vertical-head').remove();
		mainMenu.menusubCatAnimate();
	}

	// Landmark Reward
	var hash = window.location.hash;
	var requestedPanel = $(hash);

	function landmark() {
		requestedPanel.prev(".panel-heading").find('a').click();
	}

	function resetScroll() {
		if ($(requestedPanel).length) {
			$('html, body').animate({
				scrollTop : requestedPanel.prev(".panel-heading").offset().top - 155
			}, 1000);
		}
	}

	if (hash) {
		// Get panel header element
		if (requestedPanel.length) {
			landmark();
			resetScroll();
		}
		$('.footerBlock li').on('click', 'a', function() {
			if (requestedPanel.is(':hidden')) {
				landmark();
			}
			resetScroll();
		});
	} else {
		$('.footerBlock li').each(function() {
			$(this).on('click', 'a', function() {
				var url = $(this).attr('href');
				var requestedPanel = url.substring(url.lastIndexOf('#'));
				var pathname = window.location.href;
				if (this.href == pathname) {
					if ($(requestedPanel).is(':hidden')) {
						$(requestedPanel).prev(".panel-heading").find('a').click();
					}
					$('html, body').animate({
						scrollTop : $(requestedPanel).prev(".panel-heading").offset().top - 155
					}, 1000);
				} else {
					if ($(requestedPanel).is(':hidden')) {
						$(requestedPanel).prev(".panel-heading").find('a').click();
					}
					$('html, body').animate({
						scrollTop : $(requestedPanel).prev(".panel-heading").offset().top - 155
					}, 1000);
				}
			});
		});
	}
	$('.profile-update-form .checkbox').find('.control-label').click(function() {
		if ($('input[name="whetherEmployee"][type="checkbox"]').is(":checked")) {
			$(this).parent().parent().next().removeClass('hidden');
		} else {
			$(this).parent().parent().next().addClass('hidden');
		}
	});

});
function clearFormFeilds() {
	$("#sparAddressForm").on('change', 'input[name="deliveryType"]', function() {
		var SelectedCity = $(".i18nAddressForm").find("input[name='townCity']").val();
		var SelectedEmail = $(".i18nAddressForm").find("input[name='email']").val();
		$('#nwAddressBtn').click(function() {
			$(".i18nAddressForm").find("input[type='text']").val("");
			$(".i18nAddressForm").find("input[name='townCity']").val(SelectedCity);
			$(".i18nAddressForm").find("input[name='email']").val(SelectedEmail);
			$('.i18nAddressForm option').removeAttr('selected').filter('[value=al1]').attr('selected', true)
			$(".i18nAddressForm select[title='titleCode']").attr('selected', 'selected');
			$(".i18nAddressForm .form-group").removeClass('has-error');
			$(".i18nAddressForm .form-group").find('.help-block').hide();
		});
	});
	
	
}

$(function() {
	if ($("#deliveryCityName").val() != "Bengaluru") {
		$('.first-col-slots').toggleClass('first-col-slots first-col');
	}
	if ($('body').hasClass('page-multiStepCheckoutSummaryPage')) {
		$("#deliveryCityName,.locatorBtn #menu1").attr('disabled', true);
		$('.cart-block .mini-cart-link').css({
		  'opacity' : '0.5',
		  'pointer-events' : 'none'
		});
	}

	$('#gSContactForm').on(
	    'submit',
	    function(e) {
		    e.preventDefault();
		    var requestSent = false;
		    var csName = $("#gsCustomerName").val();
		    var csEmail = $("#gsCustomerEmail").val();
		    var csContact = $("#gsCustomerContact").val();
		    var csCompanyName = $("#gsCustomerCompany").val();
		    var csCity = $("#gsCustomerCity").val();
		    var csRemark = $("#gscustomerRemarks").val();

		    var csNameflag = false;
		    var csContactflag = false;
		    var csEmailflag = false;
		    var csCityflag = false;

		    if (csName == "" || csName == null) {
			    $("#gsCustomerName").addClass('has-error');
			    $("#gsErrorName").show();
			    csNameflag = true;
		    } else {
			    $("#gsCustomerName").removeClass('has-error');
			    $("#gsErrorName").hide();
		    }
		    if (csEmail == "" || csEmail == null) {
			    $("#gsCustomerEmail").addClass('has-error');
			    $("#gsErrorEmail").show();
			    $(".gsEmailError-invalid").hide();
			    csEmailflag = true;
		    } else {
			    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			    if (csEmail != "" && regex.test(csEmail) == false) {
				    $("#gsCustomerEmail").addClass('has-error');
				    $(".gsEmailError-invalid").show();
				    $("#gsErrorEmail").hide();
				    csEmailflag = true;
			    } else {
				    $("#gsCustomerEmail").removeClass('has-error');
				    $("#gsErrorEmail").hide();
				    $(".gsEmailError-invalid").hide();
			    }
		    }

		    var regEx = /^\d{10}$/;
		    if (csContact == "" || csContact == null) {
			    $("#gsCustomerContact").addClass('has-error');
			    $("#gsErrorContact").show();
			    $(".gsError-invalid").hide();
			    csContactflag = true;
		    } else {
			    if (csContact != "" && regEx.test(csContact) == false) {
				    $("#gsCustomerContact").addClass('has-error');
				    $(".gsError-invalid").show();
				    $("#gsErrorContact").hide();
				    csContactflag = true;
			    } else {
				    $("#gsCustomerContact").removeClass('has-error');
				    $("#gsErrorContact").hide();
				    $(".gsError-invalid").hide();
			    }
		    }
		    if (csCity == "" || csCity == null) {
			    $("#gsCustomerCity").addClass('has-error');
			    $("#gsErrorCity").show();
			    csEmailflag = true;
		    } else {
			    $("#gsCustomerCity").removeClass('has-error');
			    $("#gsErrorCity").hide();
		    }

		    if (csNameflag == true || csEmailflag == true || csContactflag == true || csCityflag == true) {
			    return;
		    }
		    $.ajax({
		      type : 'post',
		      url : $('#gSContactForm').attr("action"),
		      data : {
		        'gsCustomerName' : csName,
		        'gsCustomerEmail' : csEmail,
		        'gsCustomerContact' : csContact,
		        'gsCompanyName' : csCompanyName,
		        'gsCustomerCity' : csCity,
		        'customerRemarksh' : csRemark,
		      },
		      async : true,
		      cache : false,
		      success : function(data) {
			      $('#gSContactForm').html("<div id='message'></div>");
			      $('#message').html("<h3>Thanks for writing to us!</h3>").append(
			          "<p>Customer Care Team will get back to you shortly.</p>").hide().fadeIn(1500);
		      }
		    });
		    setTimeout(function() {
			    $("#giftSolutionForm").fadeOut('fast');
			    $('.modal-backdrop').fadeOut('fast');
			    window.location.reload();
		    }, 4000);
	    });
	$('<span class="mandatory">*</span>')
	    .insertAfter(
	        '.page-update-profile input[name="dateOfBirth"],.page-update-profile input[name="employeeCode"],.page-update-profile input[name="dateOfJoining"]');
	$('#alertModal,#confirmModal,#imageGiftSlider,#giftSolutionForm,#landMarkMyAccountOtp,#lrProgram,#codConfirmation').modal({
	  show : false,
	  backdrop : 'static',
	  keyboard : false
	// to prevent closing with Esc button (if you want this too)
	});
	$(function() {
		var maxImgCount = 7;
		var imgUrl = $('#imagepath').val();
		var imgSrc = imgUrl + "/images/gift-solution/";
		for (var i = 1; i < maxImgCount; i++) {
			$('#collapse3 .category').append(
			    "<div class='col-xs-6 col-lg-2'><a class='thumbnail' data='wrapper" + i
			        + "' href='#' data-toggle='modal' data-target='#imageGiftSlider'><img src='" + imgSrc + "category_0" + i
			        + ".jpg'/></a></div>");

		}

		$('.category').find('div').on(
		    'click',
		    'a',
		    function() {
			    var id = $(this).attr('data');
			    $("lrgImg img").each(function() {
				    if ($(this).attr('id') == id) {
					    $(this).removeClass('hide');
				    } else {
					    $(this).addClass('hide');
				    }
			    });
			    $("div.gallery-thumbnails-main > div").each(
			        function() {
				        if ($(this).attr('id') == id) {
					        $(this).removeClass('hide');
					        $('.js-owl-carousel').owlCarousel(
					            {
					              navigation : true,
					              navigationText : [ "<span class='glyphicon glyphicon-chevron-left'></span>",
					                  "<span class='glyphicon glyphicon-chevron-right'></span>" ],
					              pagination : false,
					              items : 2,
					              itemsDesktopSmall : [ 1200, 5 ],
					            });
				        } else {
					        $(this).addClass('hide');
				        }
			        });
		    });
		// Current Item selected owl Carousel
		var selector = '.page-productDetails .image-gallery .carousel.gallery-carousel .owl-item';
		$(selector).first().addClass('active');
		$(selector).on('click', function() {
			$(selector).removeClass('active');
			$(this).addClass('active');
		});
	});
});

// Default Area populate at checkout address page
$(function() {
	mainMenu.showMoreCat();	
	if ($('body').hasClass('page-registeroptional')) {
		var stlocadd= "";
		var storeSession = JSON.parse(localStorage.getItem("storeLocator"));
		if($("#sparAddressForm input[name='longAddress']").val() == '' && null != storeSession){
			$("#sparAddressForm input[name='longAddress']").val(storeSession.area); 
			stlocadd = $("#sparAddressForm input[name='longAddress']").val();
		}
		ACC.storefinder.getAreaDetails(stlocadd);
	}
	
	$('#nwAddressBtn').on('click', function() {
		var storeSession = JSON.parse(localStorage.getItem("storeLocator"));
		if (storeSession) {
			var abc = $("#storelocator-query1").val(storeSession.area);
			$("#sparAddressForm input[name='townCity']").val(storeSession.city);
		} else {
			$("#sparAddressForm input[name='townCity']").val('Bengaluru');
			$("#sparAddressForm input[name='longAddress']").val('Bannerughatta, Bengaluru, Karnataka, India');
			$("#sparAddressForm input[name='area']").val('Bannerughatta');
			$("#sparAddressForm input[name='postcode']").val('');
		}

		var address = $("#sparAddressForm input[name='townCity']").val();
		var stlocadd = $("#storelocator-query1").val();
		ACC.storefinder.getAreaDetails(stlocadd);
		
		
	});
	
	
	/* -----------------OLD GOOGLE API CODE(Commented out to add MapMyIndia code)-------------------*/
	// User Location
	$(document).on("click", '#findStoresNearMe', function(e) {
		e.preventDefault();
		$('#loading-image').show();
		$('#cboxOverlay').show();
		$('.flyout2').addClass('hidden2')
		$('#locality_error').hide();		
		var stlocadd = document.getElementById("storelocator-query").value;
		var addressData = document.getElementById('cityName');
		var address = addressData.options[addressData.selectedIndex].value;		
		if (ACC.storefinder.storeAreaValidation(address, stlocadd)) {			
			ACC.storefinder.googleAddressStatusCheck(stlocadd);
		}
	});
	
	 
	
	// LR Program Start Here	
	
	/******* LR Popup Display ********/
	if (localStorage.getItem('popState') != 'shown') {
		if ($('.shop-menu .navbar-nav').find('li').is('.logged_in') && $('#otpLRSessionValue').val()!="null") {
			$('#lrProgram').modal('show');
			localStorage.setItem('popState', 'shown');
			$('#otpLRSessionValue').val('');
		} else {
			localStorage.removeItem("popState");
		}
	}
	if($('body').hasClass('page-landmarkReward') || $('body').hasClass('page-registeroptional') || $('body').hasClass('page-multiStepCheckoutSummaryPage')){
		$('#lrProgram').remove();		
	}
		
	/****** LR Registration from my Account *****/
	  $('#generateOTPForMyAccount').on('click',function(){		 
		  var url=$("#landmarkMyaccountOTPGeneratorUrl").val();
		  $.ajax({
			    url : url,
			    type : "get",					    
			    success : function(response){	 
			    	var arr = response.split(",");
			    	$('#landMarkMyAccountOtp').modal('show');
			    	$('#landMarkMyAccountOtp .mobile-number').find('span').text(arr[0]);
			    	var requireOTP=$('#landMarkMyAccountOtp').find('#thisField').text(arr[1]);			    	
			    }
			});
	  });
	  
	/******* LR User Status ********/
	$(".checkbox input").on('change', function() {
		if ($(this).is(':checked')) {
			$(this).attr('value', 'true');
			if ($(this).attr('value', true)) {
				if ($(this).attr('id') == 'lrOptStatus') {
					$('#disableLRPrompt').attr('disabled', true);
					$("label[for='disableLRPrompt']").addClass('disabled');
					$('.otp').show();
					$('#confirmLROtp').attr('disabled', false);
					disableLrStatus = false;
				} else if ($(this).attr('id') == 'disableLRPrompt') {
					$('#lrOptStatus').attr('disabled', true);
					$("label[for='lrOptStatus']").addClass('disabled');
					$('.otp').hide();
					$('#confirmLROtp').attr('disabled', false);
					disableLrStatus = true;
				} else {
					$("label[for='disableLRPrompt']").removeClass('disabled');
					$('.otp').hide();
				}
			}
		} else {
			if ($(this).attr('id') == 'disableLRPrompt') {
				$('#lrOptStatus').attr('disabled', false);
				$("label[for='lrOptStatus']").removeClass('disabled');
				disableLrStatus = true;
			} else {
				$(this).attr('value', 'false');
				$('#disableLRPrompt').attr('disabled', false);
				$("label[for='disableLRPrompt']").removeClass('disabled');
				$('#confirmLROtp').attr('disabled', true);
				$('.otp').hide();
			}
			$('#confirmLROtp').attr('disabled', true);
		}

	});
	
	/******* LR User OTP Check ********/
	$("#confirmLROtp").on('click', function() {		
		var lrOptStatus = $('#lrOptStatus').val();
		var reg = /^(?!0\d)\d*(\.\d+)?$/mg;
		var disableLRPrompt = $('#disableLRPrompt').val();
		var userenteredotp = $('#otpCheck').val();
		var url = baseURL +"/en/registeroptional/landmarkRewardRegistration";
		var otpChk = $('#thisField').text();
		
		if(disableLrStatus == true){
			if (disableLRPrompt == true) {
				userenteredotp = "";
			}
			$.ajax({
				  url : url,
				  data : {
				    "lrOptStatus" : lrOptStatus,
				    "disableLRPrompt" : disableLRPrompt,
				    "userenteredotp" : userenteredotp
				  },
				  type : "get",
				  success : function(res) {
					  $("#landMarkMyAccountOtp").modal("toggle");
					  disableLrStatus = false;
				  }
			});			
		}else{
			if (disableLRPrompt == true) {
				userenteredotp = "";
			}

			if ($('#otpCheck').val() == "" || $('#otpCheck').val() == null) {
				$('.otp .error').removeClass('hide');
				$('.otp .text-center').addClass('has-error');
				return false;
			} else {
				if (!reg.test($('#otpCheck').val())) {
					$('.otp .error').removeClass('hide');
					$('.otp .text-center').addClass('has-error');
					return false;
				} else {
					if (userenteredotp === otpChk) {
						$('.otp .error').addClass('hide');
						$.ajax({
						  url : url,
						  data : {
						    "lrOptStatus" : lrOptStatus,
						    "disableLRPrompt" : disableLRPrompt,
						    "userenteredotp" : userenteredotp
						  },
						  type : "get",
						  success : function(res) {
							  $("#landMarkMyAccountOtp").modal("toggle");
						  }
						});
					} else {
						$('.otp .error').removeClass('hide');
						$('.otp .text-center').addClass('has-error');
						return false;
					}
				}
			}
			
		}		
	});
	
	/******* LR Points Redemption  ********/	
	var lRTotalPoints = $('#landmarkAmount').val();
	var totalPaidAmount = $('.pay-summary .sub-total').find('.pay-items').next().text().slice(2);
	totalPaidAmount = totalPaidAmount.replace(/,/g, "");
	
	if(lRTotalPoints == null || lRTotalPoints ==""){
		$('#landmarkRewardAmount').attr({
			  'placeholder' : "0.00",
			  'value' : "0.00"
			});	
		  $(".lr-program .input-group-btn").find('button').attr('disabled',true);
		  $('#landmarkRewardAmount').attr('disabled',true);
	 }	
	else if(parseFloat(lRTotalPoints) > parseFloat(totalPaidAmount)){		
		$('#landmarkRewardAmount').attr({
			  'placeholder' : totalPaidAmount,
			  'value' : totalPaidAmount
			});
	}else if(parseFloat(lRTotalPoints) < parseFloat(totalPaidAmount)){
		$('#landmarkRewardAmount').attr({
			  'placeholder' : lRTotalPoints,
			  'value' : lRTotalPoints
			});
		
	}else{		
		$('#landmarkRewardAmount').attr({
			  'placeholder' : totalPaidAmount,
			  'value' : totalPaidAmount
		});
	}	
	/******* User Restriction after decimal 2 points  ********/	
	$(function() {
		$('#landmarkRewardAmount').on('input',function() {
			match = (/(\d{0,9})[^.]*((?:\.\d{0,2})?)/g).exec(this.value
					.replace(/[^\d.]/g, ''));
			this.value = match[1] + match[2];
		});
	});

	$(".lr-program .input-group-btn").on('click', 'button', function() {
		var balanceDue = $('.paymentBxs .amt-paid>span').text().slice(2);
		var userLRAmount = $('#landmarkRewardAmount').val();
		userLRAmount = userLRAmount.replace(/,/g, "");		
		var reg = /^[1-9]\d*(\.\d+)?$/; 
		var url = $('#doHandleLRPaymentUrl').val();		
		var remainingLRTotalBalance = lRTotalPoints - userLRAmount;	
		if (userLRAmount == "" || userLRAmount == null) {
			alertModal('Please enter valid value');
			return false;
		}			
		
		if (reg.test(userLRAmount)) {			
			if (parseFloat(userLRAmount) <= 0 || parseFloat(totalPaidAmount) < parseFloat(userLRAmount)) {
				alertModal("The value of Landmark Rewards points entered is more than the BALANCE DUE amount. Please enter an amount less than or equal to the BALANCE DUE amount.");
				if(parseFloat(userLRAmount) >= lRTotalPoints && lRTotalPoints < 100){
					$('#landmarkRewardAmount').val(lRTotalPoints);
				}else if(lRTotalPoints >= totalPaidAmount && lRTotalPoints > 100 ){
					$('#landmarkRewardAmount').val(totalPaidAmount);
				}				
				return false;
			}
		} else {			
			alertModal('Please enter valid value');
			return false;
		}
		
		$('#loading-image').show();
		$('#cboxOverlay').show();
		
		$.ajax({
		  url : url,
		  type : "get",
		  data : {
			  "landmarkRewardAmount" : userLRAmount
		  },
		  success : function(response) {
			  var userLRAmountFormatted = parseFloat(userLRAmount).toFixed(2);			  
			  $('#landmarkRewardAmount').val('');
			  $(".lr-program .input-group-btn").find('button').attr('disabled',true);
			  $('#landmarkRewardAmount').attr('disabled',true);
			  	$('#loading-image').hide();
				$('#cboxOverlay').hide();			 
			  if(response.validationMessage != null){
					  $(".lr-program .input-group-btn").find('button').attr('disabled',false);					 
					  if(lRTotalPoints <= 100){
						  $('#landmarkRewardAmount').val(lRTotalPoints);
					  }else{
							$('#landmarkRewardAmount').val(totalPaidAmount);
					  }
					  $('#landmarkRewardAmount').attr('disabled',false);
					 alertModal(response.validationMessage);					 
					 return false;
			  	}			 
			  $('.pay-summary .landmark-reward').removeClass('hidden');
			  $('.pay-summary .landmark-reward').find('.pay-items').next().find('.amt').text("Rs" + " " + userLRAmountFormatted);			  
			  $('.paymentBxs .amt-paid>span').text("Rs" + " " + response.remainingBalanceDue.value);
			 
			  if (response.remainingBalanceDue.value == 0) {				  
				  $('#landmarkRewardAmount').attr({
				    'placeholder' : remainingLRTotalBalance,
				    'value' : remainingLRTotalBalance
				  });				
				  $('.paymentBxs .amt-paid>span').text("Rs" + response.remainingBalanceDue.value);				 
				  $('.pay-summary .landmark-reward').find('.pay-items').next().find('.amt').text("Rs" + " " + userLRAmountFormatted);				  
				  $('#payment_button,#payment_button1').attr("disabled", "disabled").css('opacity', '.5');
				  $('#landmark_payment_confirm_button').removeClass("hidden").prop("disabled", false);				 
				  
			  } else {				  
				  $('#payment_button,#payment_button1').removeAttr("disabled").css('opacity', '1');
				  $('#landmark_payment_confirm_button').attr("disabled", "disabled")
				 
			  }
			  
			  if(lRTotalPoints >= response.subTotal.value){	
				  $('#landmarkRewardAmount').attr({
					    'placeholder' : parseFloat(remainingLRTotalBalance).toFixed(2),
					    'value' : parseFloat(remainingLRTotalBalance).toFixed(2)
				});
			  }else if(lRTotalPoints <= response.subTotal.value){
				  $('#landmarkRewardAmount').attr({
					    'placeholder' : parseFloat(remainingLRTotalBalance).toFixed(2),
					    'value' : parseFloat(remainingLRTotalBalance).toFixed(2)
				});
			  }			  
			  
			  /******* LR Points Redemption Cancel  ********/			  
			  $('.pay-detail span.remove-entry-button').click(function() {		
				  $('#landmarkRewardAmount').val("");	
				  $(".lr-program .input-group-btn").find('button').attr('disabled',false);
				  $('#landmarkRewardAmount').attr('disabled',false);
				  $('#landmark_payment_confirm_button').addClass("hidden");
				  $('.pay-summary .landmark-reward').addClass('hidden');
				  $('.paymentBxs .amt-paid>span').html(totalPaidAmount);
				  if(lRTotalPoints <= response.subTotal.value){
					  $('#landmarkRewardAmount').val(lRTotalPoints);	
					  $('#landmarkRewardAmount').attr({
						  'placeholder' : lRTotalPoints
					  });					  
				  }	else{
					  $('#landmarkRewardAmount').val(totalPaidAmount);				  
					  $('#landmarkRewardAmount').attr({
						  'placeholder' : totalPaidAmount
					  });
				  }				  
				 
				  $('#payment_wallet_button').prop("disabled", false);
				  $('#payment_button,#payment_button1').prop("disabled", false).css('opacity', '1');
				  
				  var cancelUrl = $('#cancelLandmarkRewardPaymentUrl').val();
				  $.ajax({
				    url : cancelUrl,
				    type : "post",				    
				    success : function(response) {
					   //console.log(response);
				    }
				  });
			  });
			  
			  $('#payment_button,#payment_button1').on('click',function(){				  
				 if($('#alertModal').find('modal-body').text()='There seems to be a system error and we are unable to redeem your Landmark Rewards Points at the moment. Please pay via another payment options.'){
					 alertModal('There seems to be a system error and we are unable to redeem your Landmark Rewards Points at the moment. Please pay via another payment options.');
					 return false;
				 }
			  });
		  }
		});	
		//LR Program End Here
	});
	$(document).ready(function(){		
		$("a[href='#myModal']").on('click',function(){			
			$('#myModal').modal({
				  show : false,
				  backdrop : 'static',
				  keyboard : false
			});
		});		
		
		//Login Register hide/show forms
		$(".link_login").removeClass('hide');
		$(document).on('click', '.check-top-bar a',function(e){				
			if($(this).attr('class')=='link2'){
				$(".link_login").addClass('hide');
		        $(".link_reg").show();		
		        $('#registerHeaderBtn').addClass('active');
  				$('#loginHeaderBtn').removeClass('active');  				
			}else{
				$(".link_login").removeClass('hide');
		        $(".link_reg").hide();
		        $('#registerHeaderBtn').removeClass('active');
  				$('#loginHeaderBtn').addClass('active');
  				loadSocialMediaScript();
			}
		});	
		$("#createAccount").text("SIGN UP");
		$(document).on('click','#createAccount',function(){
			var currentForm = $(this).parents('.loginRaginCntr').prev('.check-top-bar');
			if(currentForm.find('a').attr('class')=='link1'){
				$(".link_login").addClass('hide');				
		        $(".link_reg").show();	
		        $('#registerHeaderBtn').addClass('active');
  				$('#loginHeaderBtn').removeClass('active');		        
			}else{
				$(".link_login").removeClass('hide');
		        $(".link_reg").hide();
		        $('#registerHeaderBtn').removeClass('active');
  				$('#loginHeaderBtn').addClass('active');
			}
		});
		
		if(window.location.href.split('register').length > 1){
			 $(".link_reg").show();	
			 $(".link_login").addClass('hide');
			
		}
	});	
	$('#homepage_slider').find('.banner-min img').attr({
		'title':'',
		'alt':''
	});
	$('.comboMultiProducts').on('click',function(){
		var url=document.location.origin + $('#comboMultiProductsUrl').val();
		window.location.href= url;		
	});	
	/*Mini Cart Click Behaviour*/	
	  $('.mini-cart-link').click(function() {
		  var l = window.location;
		  var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
		  location.href = base_url + '/cart';
	  });
	  $( ":text" ).each(function( index ) {
		    $( this ).focusout(function() {
		      var text = $(this).val();      
		      text = $.trim(text);
		      $(this).val(text);
		    });
		});	
	      if($('#cartSavingValue').val() > 0 && $('#cartSavingValue').val() == '0.0'){
	    	  $('.page-cartPage .cart-item-details').removeClass('no-saving');
	      }else if($('#cartSavingValue').val() == '0.0' && $('#combiOfferStatus').val() == 'true'){
	    	  $('.page-cartPage .cart-item-details').removeClass('no-saving');	    	  
	      }else{
	    	  $('.page-cartPage .cart-item-details').addClass('no-saving');
	    	  $('.page-multiStepCheckoutSummaryPage .cart-item-details').addClass('no-saving');
	      }
	    //Saved Address
	      if ($('header .shop-menu  li').hasClass('logged_in')){
	      $('#savedAddress').on('click',function(){
	    	  var savedURL =baseURL + "/checkout/multi/delivery-address/getdeladdresses"; 	    	  
	    	  $('#location-loader').show();    		 
	    		  $.ajax({
			    	  url : savedURL,
			    	  type : "get",			 
			    	  success:function(data){			    		 
			    		  var sessionExpired = $(data).find('.loginRaginCntr').html();			    		  
			    		  var addressData = $(data).find('#addressbook').find('.col-sm-4').html();	
			    		  if(sessionExpired){
			    			  $('.addressViewMsg').removeClass('hide');
			    			  $('#userLoginSession').on('click',function(){
		    			  			window.location.href = baseURL + "/login";
		    			  		});
			    			  $('#chkSavedAddress').prop('disabled', true);
			    			  $('#location-loader').hide(); 
				    		 }	 
			    		  else if(addressData){
			    		  $("#savedaddressbook").find('.savedAddress').html($(data).find('#addressbook').html());
			    		  $('#location-loader').hide(); 
			    		  
			    		    $('#savedaddressbook input[type="radio"][name="address"]').click(function() {			    		    	  
	    			           $('#savedaddressbook input[type="radio"][name="address"]').removeAttr("checked").parent().removeClass('on');
	    			            $(this).prop("checked", "checked");	    			            
	    		            });
			    		    $('#savedaddressbook .savedAddress .col-sm-4').each(function(){			    		    	
			    		    	var lastDeliveryAddressHouse = $('#lastDeliveryAddress').val()+',';
			    		    	
			    		    	if(localStorage.getItem('currentAddress') != null){			    		    		
			    		    		if($(this).find('#editaddress').val() === localStorage.getItem('currentAddress')){			    		    			
			    		    			$(this).find('#deliveryAddressRadio').prop("checked", true);			    		    			
			    		    			$(this).addClass('defaultAddress');
				    		    		var firstAddress = $('#savedaddressbook .row .col-sm-4').first();
					    				$('#savedaddressbook .row .defaultAddress').insertBefore(firstAddress);
			    		    		}			    		    			    		    		
			    		    	}else{	
			    		    		var lastDeliveryAddressHouse = $('#lastDeliveryAddress').val()+',';	
				    		    	var addressHouseNo = $(this).find('.addressParaOne').find('.addressHouse').text();				    		    	
				    		    	if(addressHouseNo.trim() === lastDeliveryAddressHouse){
				    		    		if($("#deliveryAddressRadio").is(':checked') == false){
				    		    			$(this).find('#deliveryAddressRadio').prop("checked", true);				    		    			
					    		    		$(this).addClass('defaultAddress');
					    		    		var firstAddress = $('#savedaddressbook .row .col-sm-4').first();
						    				$('#savedaddressbook .row .defaultAddress').insertBefore(firstAddress);
						    				if($("#deliveryAddressRadio").is(':checked') == false){
						    					$("#deliveryAddressRadio").trigger("click");
						    				}
				    		    		}
				    		    	}
			    		    	}
			    		    });
			    		  
			    		  $('#savedaddressbook').on('click', 'input[type="radio"][name="address"]', function(e){		alert("1");
			    			  $('#location-loader').show();     	
			    			  	 $this = $(this); 
					              $('input:radio[name='+ $this.attr('name')+']').parents('form').removeClass('currentAddress');
					              $this.parents('#deliveryFormSelect').addClass('currentAddress');
					              var addressTown = $this.parent().next().find('.addressParaOne').find('.addressTown').text();
					               addressTown = addressTown.trim();					               
					               $this.data('requestRunning', false);
			    			  		$(this).parents('#savedaddressbook').find('#chkSavedAddress').on('click', function(event){			    			  			
			    			  			event.preventDefault();  			  			
			    			  			 $('.page-loader #loader').show();    	
			    			  			$('#cboxOverlay').show();
			    			  			if ($this.data('requestRunning') ) {
			    			  		        return;
			    			  		    }
			    			  			$this.data('requestRunning', true);	
			    			  			var addressId = $this.parents('#deliveryFormSelect').find('#editaddress').val();			    			  			
			    			  		$.ajax({
					                	url: baseURL + "/store-finder/mappedStore",	
					                	cache: false,
					                    data: {
					                    	"addressId":addressId
					                    },
					                	 type : "get",		
					                	 success:function(data){
					                		data = $.parseJSON(data);					                		 
					                		if(jQuery.isEmptyObject(data) === false){
					                			$('#homedelivery').val(data.name);	
					                			ACC.storemapped.storeData = "";
					                			ACC.storemapped.confirmStoreMapping(data);
					                			$('#savedaddressbook').modal('hide');				                			
					                		}else{
					                			 if ($this.parents('#deliveryFormSelect').find('.addressParaOne span').hasClass('addressLongAddress')) { 
				                				 var deliveryForm = $this.parents('#deliveryFormSelect'),     			              
									              deliveryArea = $this.parents('#deliveryFormSelect').find('.addressParaOne').find('.addressLongAddress').text(),
									              postalCode = $('#deliveryPostalCode').val();
				                				  var addressTown = $this.parent().next().find('.addressParaOne').find('.addressTown').text();
									               addressTown = addressTown.trim();
				                				 
					     	    		            $('#deliveryAddressTown').val(addressTown);    			                		                
					     			                var longFormatted_address = deliveryArea;
					     			                var regPattern = deliveryArea.replace(/[^\x20-\x7E]/gmi, "");
					     			                var localArea = encodeURI(regPattern);
					     			               
					     			                var address = addressTown;

					     			                if (localArea.indexOf('&') > -1) {
					     				               localArea = localArea.replace("&", "%26");
					     			                }

					     			                ACC.storemapped.googleAddressStatusCheck(address, postalCode, deliveryArea, longFormatted_address, addressId, localArea);
					     			                $('form.currentAddress').find('#deliveryAddressRadio').prop("checked", "checked");
					     			                return false;
					     			              } else {
					     			            	 // Old Addresses Without Long Address
										              var savedArea = $this.parents('#deliveryFormSelect').find('.addressParaOne').find('.addressArea').text(), 
										              savedCityCode = $this.parents('#deliveryFormSelect').find('.addressParaTwo').text(),
										              savedDeliveryArea = savedArea + savedCityCode;
										              var postalCode="";
										              var addressTown = $this.parent().next().find('.addressParaOne').find('.addressTown').text();
										               addressTown = addressTown.trim();
										               
					    			                   $('#deliveryAddressTown').val(addressTown);    			             
					     			                var longFormatted_address = savedDeliveryArea;
					     			                var regPattern = longFormatted_address.replace(/[^\x20-\x7E]/gmi, "");
					     			                var geocoder = new google.maps.Geocoder();
					     			                var localArea = encodeURI(regPattern);

					     			                var address = addressTown;

					     			                if (localArea.indexOf('&') > -1) {
					     				               localArea = localArea.replace("&", "%26");
					     			                }
					     			                ACC.storemapped.googleAddressStatusCheck(address, postalCode, savedDeliveryArea,
					     			                    longFormatted_address, addressId, localArea);
					     			               $('form.currentAddress').find('#deliveryAddressRadio').prop("checked", "checked");
					     			               return false;
					     			              }	
					                		}					                		
					                		 $('#location-loader').hide(); 	
					    			  		 $('#cboxOverlay').hide();  
					                	 },complete: function() {
					                		 if(localStorage.getItem('currentAddress') != addressId) {
								            	  localStorage.setItem('currentAddress',addressId);
								              }else{
								            	  localStorage.removeItem('currentAddress');
								              }	
					                		 $this.data('requestRunning', false);				                		 
					                		 
					                     }
				    			    });
			    			  		
			    			  	});		    			    
			    		  });	
			    		  $('#savedaddressbook .savedAddress .col-sm-4').each(function(){
			    			  if($(this).find('#deliveryAddressRadio').is(':checked')){
			    				  $(this).find('#deliveryAddressRadio').trigger('click');
			    			  }
			    		  });
			    	  }
			    		else{			    		
			    		  $('.no-address-msg').removeClass('hide');
			    		  $('#location-loader').hide(); 
			    		  $('#chkSavedAddress').prop('disabled', true);
			    	  }
			    		  
			    	  }
			      });
	      	 }); 
	 }
	  else{		  
		  $('.addressViewMsg').html('Please' + " " + '<span id="userLoginAddress">login</span>' + " " + " to view existing addresses");
		  $('.addressViewMsg').removeClass('hide');
		  $('#userLoginAddress').on('click',function(){
	  			window.location.href = baseURL + "/login";
	  		});
		  $('#location-loader').hide();    	
		  $('#chkSavedAddress').prop('disabled', true);
	  } 
	      
	  // Home Page Saved Address End
	      $('#addressbook .row .col-sm-4').each(function(){	    	     	  
	    	var currentAddress = $(this).find('#editaddress').val();		    	
	    	if(currentAddress == localStorage.getItem('currentAddress')){
	    		$(this).find('#deliveryAddressRadio').prop("checked", true);		    		
	    		if ($(this).find('#deliveryAddressRadio').is(':checked')) {
	    			$(this).find('#deliveryAddressRadio').click();
	    			$(this).addClass('defaultAddress');
	    			if($(this).hasClass('defaultAddress')){
	    				var firstAddress = $('#addressbook .row .col-sm-4').first();
	    				$('#addressbook .row .defaultAddress').insertBefore(firstAddress);
	    			}
	    		}
	    	}
		}); 
	      
	      $('.shop-menu li').each(function(){	    	 
	    	 var selectedTxt = $(this).find('a').text();
	    	 	 selectedTxt= selectedTxt.trim();
	    	 	 $('.shop-menu ul li').css('opacity','1');
	    	 	 if(selectedTxt == "LOGIN/REGISTER"){
	    	 		var loginTxt = "LOGIN",
	    	 			registerLink ="<a href='#' id='registerHeaderBtn' title='Register'>" + "Sign Up"+ "</a>",
	    	 			loginIcon ="<span class='hidden-sm hidden-xs img-icons login-icon'></span>";
	    	 		$(this).find('a').attr({
	    	 			'title':'Login',
	    	 			'href' :"#",
	    	 			'id' :"loginHeaderBtn"    	 			
	    	 		});
	    	 		$(this).find('a').text(loginTxt).append(loginIcon);
	    	 		$(this).find('a').after(registerLink);	   
	    	 		
	    	 	 	$(this).find('a').on('click',function(e){
		    	 		e.preventDefault();
		    	 		var pageUrl = baseURL + "/login";
		    	 		var $this = $(this);	    	 		
		   				if(window.location.href != pageUrl){	   					
				              $('#cboxOverlay').show();
				              $('#loading-image').show();			              
		   					$.ajax({
		   						url : baseURL + "/login",
		   						cache:false,
		   						type : "get",
		   						dataType: 'html',
		   						success:function(data){
		   							var url = baseURL + '/en/login';
		   							var temp = $('<div>').append($.parseHTML(data));	   							
		   							$('.content-wrapper.container').html($(temp).find('.content-wrapper').html());		   							
		   						 		   							
		   							if($this.attr('title') == 'Register'){	   						
		   			    	 			$(".link_login").addClass('hide');	
		   			   					$(".link_reg").show();  
		   			   					$('#registerHeaderBtn').addClass('active');			   			   				 
		   			   				    $('#loginHeaderBtn').removeClass('active');		   			   				    
		   			   				
		   			    	 		}else if($this.attr('title') == 'Login'){		    	 			
		   				   				$(".link_login").removeClass('hide');	
		   			   					$(".link_reg").hide();
		   			   					$('#registerHeaderBtn').removeClass('active');
		   			   					$('#loginHeaderBtn').addClass('active');	   			   				
		   			   				  // Social Intergration Script
		   			   				 loadSocialMediaScript();
		   			    	 		}	   				
		   							history.pushState(null, '', url);
		   							if ($('input[name="whetherEmployee"][type="checkbox"]').is(":checked")) {
			   			 		        $('#test').fadeToggle();
			   			 		        $('.profile-update-form .form-group').next().removeClass('hidden');
		   			   				}		
			   			 	        $('input[name="whetherEmployee"][type="checkbox"]').click(function() {
			   			 		        $('#test').fadeToggle();
			   			 	        });
									 $('input[name="dateOfBirth"]').datepicker({
									  maxDate : new Date(),
									  changeYear : true,
									  yearRange : "1900:+nn"
									});
									$(".dateOfBirth").click(function() {
										$('input[name="dateOfBirth"]').datepicker("show");
									});

									$('input[name="dateOfJoining"]').datepicker({
									  maxDate : new Date(),
									  changeYear : true,
									  yearRange : "1900:+nn"
									});
									$(".dateOfJoining").click(function() {
										$('input[name="dateOfJoining"]').datepicker("show");
									});
		   			              $('#cboxOverlay').hide();
		   			              $('#loading-image').hide();	   			        
		   						}
		   					})
		   				}
		   				else{		   					
		   					if($(this).attr('title') == 'Register'){	   						
			    	 			$(".link_login").addClass('hide');	
			   					$(".link_reg").show();   		
			   					$('#registerHeaderBtn').addClass('active');
   			   				    $('#loginHeaderBtn').removeClass('active');	
			    	 		}else if($(this).attr('title') == 'Login'){		    	 			
				   				$(".link_login").removeClass('hide');	
			   					$(".link_reg").hide();
			   					$('#registerHeaderBtn').removeClass('active');
   			   					$('#loginHeaderBtn').addClass('active');
			    	 		}
		   				}		
		   		});
	    	}
	  });
	  $(document).ajaxStop( function() {
		//Password Hint Messages
		  SignUpPasswordHint();	          	
	      $('[data-toggle="tooltip"]').tooltip();
		});
      // Social Media Integration
       var baseLoginUrl = baseURL + '/en/login';
       if(window.location.href == baseLoginUrl || window.location.href == baseLoginUrl + "/checkout"){    	 
    	   loadSocialMediaScript();
       }	     
       if(baseURL || baseURL + "/cart"){
   	    var msg = localStorage.getItem('msg'); 			
			$(msg).insertBefore('header').fadeIn(5000);
   	   setTimeout(function() {
   		   localStorage.removeItem('msg');
    		  $('.alert.alert-success').fadeOut(5000);
    		  $('.alert.alert-success').remove();
   		},10000);
      }
       var $accordionStoreLocator = $('#accordionStoreLocator');      
           $accordionStoreLocator.on('show.bs.collapse','.collapse', function() {
    	   $accordionStoreLocator.find('.collapse.in').collapse('hide');    	  
       });
           $('#accordionStoreLocator').on('hidden.bs.collapse', toggleIcon);
	        $('#accordionStoreLocator').on('shown.bs.collapse', toggleIcon);
         
        //Password Hint Messages
        
        var passwordMsgs={
        		passwordValidateMsg : "Password must contain at least 8 characters. Please use complex password.",
        		passwordCurrentMsg : "Please enter the current password.",
        		passwordConfirmMsg :  "Please enter the same password as above."       		
        }
        
        function SignUpPasswordHint(){        	
	        	var signUpPwd=$("form input[name='pwd']"),
	        		signUpcheckPwd=$("form input[name='checkPwd']");
		        	$(signUpPwd).next().next('.passwordInfo').attr('data-original-title',passwordMsgs.passwordValidateMsg);
		          	$(signUpcheckPwd).next().next('.passwordInfo').attr('data-original-title',passwordMsgs.passwordConfirmMsg); 
        }
        function UpdatePasswordHint(){
        	var updateCurrentPwd=$("form input[name='currentPassword']"),
	        	updateNewPwd=$("form input[name='newPassword']"),	        	
	        	updateChkNewPwd=$("form input[name='checkNewPassword']");        	
	        	$(updateCurrentPwd).next().next('.passwordInfo').attr('data-original-title',passwordMsgs.passwordCurrentMsg);
	        	$(updateNewPwd).next().next('.passwordInfo').attr('data-original-title',passwordMsgs.passwordValidateMsg);
	          	$(updateChkNewPwd).next().next('.passwordInfo').attr('data-original-title',passwordMsgs.passwordConfirmMsg);	        	       	
        }
        SignUpPasswordHint();
        UpdatePasswordHint();        
        
        $(".row.lname").next().find('.help-block').each(function() {
    	  var content = $(this).find('span').text();   
    	  if(content.includes('Please use complex password') == true){
    		  $(this).find('span').text("Password must contain at least 8 characters. Please use complex password.");    		  
    	  }
    	});
        
       
        
        //Reset Password Form Validation
        $('#updatePwdForm').on('submit',function(e){        	 
        	 var errorMsgPwd = "Password must contain at least 8 characters. Please use complex password.",
        	 	 errorMsgConfirmPwd = "Password and confirm password must be same.",
        	 	 errorMsgNewPwd ="Please enter your new password",
        	 	 newPwd = $('#updatePwdForm input[name="pwd"]'),
        	 	 confirmPwd = $('#updatePwdForm input[name="checkPwd"]');
        	 
        	 	if(newPwd.val() === ""){
        	 		if(!newPwd.parents('.form-group').hasClass('has-error')){
        	 			newPwd.parents('.form-group').addClass('has-error');
        	 			newPwd.next().after('<div class="help-block">' +"<span>"+ errorMsgNewPwd +"</span" + "</div>");
        	 		}
        	 		return false;
        	 	}else if(newPwd.val().length < 8){
        	 		if(!newPwd.parents('.form-group').hasClass('has-error')){
        	 			newPwd.parents('.form-group').addClass('has-error');
        	 			newPwd.next().after('<div class="help-block">' +"<span>"+ errorMsgPwd +"</span" + "</div>");
        	 		}else{
        	 			newPwd.parents('.form-group').find('.help-block').remove(); 
        	 			newPwd.next().after('<div class="help-block">' +"<span>"+ errorMsgPwd +"</span" + "</div>");
        	 		}
        	 		return false;
        	 		
        	 	}else{        	 		
        	 		$('#updatePwdForm input[name="pwd"]').parents('.form-group').removeClass('has-error');
        	 		$('#updatePwdForm input[name="pwd"]').parents('.form-group').find('.help-block').remove();
        	 		if(newPwd.val() !== confirmPwd.val()){        	 		
            	 		if(!confirmPwd.parents('.form-group').hasClass('has-error')){
                	 		confirmPwd.parents('.form-group').addClass('has-error');
                	 		confirmPwd.next().after('<div class="help-block">' +"<span>" + errorMsgConfirmPwd +"</span" + "</div>");            			
            	 		} 
            	 		e.preventDefault(); // stop form submission
            	 	}else{        	 		
            	 		confirmPwd.parent().parent().parent().removeClass('has-error');
            	 		confirmPwd.parent().parent().parent().find('.help-block').remove();
            	 		return true;
            	 	}        	 		
        	 	}        	 	    	
        }); 
        $(function(){
        	$(".slot-table .slots").hide();
        	$(".slot-table .slots").first().show();
        	$(".slot-table .timeslots").first().addClass('active');      
        	$('.timeslots').each(function(){
        		$(this).on('click mouseover',function(){ 
        			$('.timeslots').removeClass('active');
        			var className = $(this).text(),
						className = className.trim(),
						className =	"slots" + " " + className;        				
        			if($(this).attr('class') != 'active'){        				
        				$(this).addClass('active');
        				$(this).parents('.slot-table').find('.slots').each(function(){
            				if($(this).attr('class') == className){
            					$(this).fadeIn('slow');            					
            				}else{
            					$(this).fadeOut('fast');            					
            				}
            			});
        			}else{
        				$(this).removeClass('active');        				
        			}       			
        			
        		});
        	});        	
        });
});
