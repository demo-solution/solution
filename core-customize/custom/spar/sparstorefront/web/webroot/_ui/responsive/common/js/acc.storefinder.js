ACC.storefinder = {
  _autoload : [
  // ["init", $(".js-store-finder").length != 0],
  [ "bindStoreChange", $(".js-store-finder").length != 0 ], [ "bindSearch", $(".js-store-finder").length != 0 ], "bindPagination"],
  
  storeData : "",
  storeId : "",
  coords : {},
  storeSearchData : {},  
  initialize:function(id){
	  var input = document.getElementById(id);
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
  } ,  
  createListItemHtml : function(data, id) {
	  var item = "";
	  var name = "";
	  if ((data.name.indexOf('CC') > -1)) {
		  var temp = 'Landmark Group';
		  name = data.name;
		  name = name.replace("CC", "");
		  var url = "https://www.google.com/maps/place/" + temp + "," + data.town + "," + data.postalCode + "/@" + data.latitude + "," + data.longitude;
	  } else {
		  name = data.name;
		  var url = "https://www.google.com/maps/place/Spar Hypermarket" + "," + data.town + "," + data.postalCode + "/@" + data.latitude + ","
		      + data.longitude;
	  }
	  item += '<li class="col-md-4"><input type="radio" name="store" value="' + data.name + '" id="store-filder-entry-' + id
	      + '" class="js-store-finder-input" data-id="' + id + '" dataSlot="' + data.dslots + '" >';
	  item += '<div class="col-md-12 nopadding"><h6>' + data.firstName + '</h6><p>' + data.line1 + '</p>';
	  item += '<p>' + data.line2 + '</p>';
	  item += '<p>' + data.town + '&nbsp-&nbsp;' + data.postalCode + '</p><p>'/* +data.regionName+",&nbsp;"+data.countryName+'</p>' */;
	  item += '<p><i class="fa fa-phone-square" style="font-size:16px; padding-right:10px;"></i>Phone: <b class="phoneClr">' + data.phone + '</b></p>';
	  item += '<a href="'
	      + url
	      + '" target="_blank" class="btn btnLocation" ><span><i class="fa fa-map-marker pad-right" style="padding-right:5px;"></i>SEE DIRECTION</span></a>';
	  item += '</div></li>';
	  /* Code Change end here */
	  return item;
  },
  
  refreshNavigation : function() {
	  /* Code Change by sumit to loop the collection center data */
	  var listitems1 = "";
	  var noOfColCenter = 0;
	  data = ACC.storefinder.storeData;	  
	  $('.noStoreData').hide();
	  $('.bottonColumn').show();
	  $('.clickCollect  .noAvailable, #homedeltextchng>.noAvailable').hide().siblings().show();
	  if (data["data1"] != "") {
		  $('.flyout2').removeClass('hidden2');
		  $('#collectionCenter').attr("disabled", "disabled");
		  $('.clickCollect .noAvailable').show().siblings().hide();
		  $('.clickCollect .whiteBack').hide()
		  $('#homedelivery').removeAttr("disabled").val(data["data1"][0].name).trigger("click");
		  $('#hdSlot').html(ACC.storefinder.storeData["data1"][0].dslots)
		  // localStorage.setItem("cncCenter","true");
	  } else {
		  $('.flyout2').addClass('hidden2');
		  $('.bottonColumn').hide();
		  $('.noStoreData').show();
		  $('#loading-image').hide();
		  $('#cboxOverlay').hide();	
		  localStorage.removeItem("storeLocator");
	  }
	  
	  var page = ACC.storefinder.storeSearchData.page;
	  $(".js-store-finder-pager-item-from").html(page * 10 + 1);
	  
	  var to = ((page * 10 + 10) > ACC.storefinder.storeData.total) ? ACC.storefinder.storeData.total : page * 10 + 10;
	  $(".js-store-finder-pager-item-to").html(to);
	  $(".js-store-finder-pager-item-all").html(ACC.storefinder.storeData.total);
	  $(".js-store-finder").removeClass("show-store");	  
	  
  },
  
  bindPagination : function() {
	  
	  $(document).on("click", ".js-store-finder-details-back", function(e) {
		  e.preventDefault();
		  
		  $(".js-store-finder").removeClass("show-store");
		  
	  })

	  $(document).on("click", ".js-store-finder-pager-prev", function(e) {
		  e.preventDefault();
		  var page = ACC.storefinder.storeSearchData.page;
		  ACC.storefinder.getStoreData(page - 1)
		  checkStatus(page - 1);
	  })

	  $(document).on("click", ".js-store-finder-pager-next", function(e) {
		  e.preventDefault();
		  var page = ACC.storefinder.storeSearchData.page;
		  ACC.storefinder.getStoreData(page + 1)
		  checkStatus(page + 1);
	  })

	  function checkStatus(page) {
		  if (page == 0) {
			  $(".js-store-finder-pager-prev").attr("disabled", "disabled")
		  } else {
			  $(".js-store-finder-pager-prev").removeAttr("disabled")
		  }
		  
		  if (page == Math.floor(ACC.storefinder.storeData.total / 10)) {
			  $(".js-store-finder-pager-next").attr("disabled", "disabled")
		  } else {
			  $(".js-store-finder-pager-next").removeAttr("disabled")
		  }
	  }
	  
  },
  
  bindStoreChange : function() {
	  $(document).on("change", ".js-store-finder-input", function(e) {
		  e.preventDefault();
		  storeData = ACC.storefinder.storeData["data"];
		  var storeId = $(this).data("id");
		  var $ele = $(".js-store-finder-details");
		  $.each(storeData[storeId], function(key, value) {
			  if (key == "image") {
				  if (value != "") {
					  $ele.find(".js-store-image").html('<img src="' + value + '" alt="" />');
				  } else {
					  $ele.find(".js-store-image").html('');
				  }
			  } else if (key == "productcode") {
				  $ele.find(".js-store-productcode").val(value);
			  } else if (key == "openings") {
				  if (value != "") {
					  var $oele = $ele.find(".js-store-" + key);
					  var openings = "";
					  $.each(value, function(key2, value2) {
						  openings += "<dt>" + key2 + "</dt>";
						  openings += "<dd>" + value2 + "</dd>";
					  });
					  
					  $oele.html(openings);
					  
				  } else {
					  $ele.find(".js-store-" + key).html('');
				  }
				  
			  } else if (key == "specialOpenings") {
			  } else if (key == "features") {
				  var features = "";
				  $.each(value, function(key2, value2) {
					  features += "<li>" + value2 + "</li>";
				  });
				  
				  $ele.find(".js-store-" + key).html(features);
				  
			  } else {
				  if (value != "") {
					  $ele.find(".js-store-" + key).html(value);
				  } else {
					  $ele.find(".js-store-" + key).html('');
				  }
			  }
			  
		  })

		  ACC.storefinder.storeId = storeData[storeId];
		  ACC.storefinder.initGoogleMap();
		  
	  })

	  $(document).on("click", ".js-select-store-label", function(e) {
		  $(".js-store-finder").addClass("show-store")
	  })

	  $(document).on("click", ".js-back-to-storelist", function(e) {
		  $(".js-store-finder").removeClass("show-store")
	  })

  },
  
  initGoogleMap : function() {
	  
	  if ($(".js-store-finder-map").length > 0) {
		  ACC.global.addGoogleMapsApi("ACC.storefinder.loadGoogleMap");
	  }
  },
  
  loadGoogleMap : function() {
	  
	  storeInformation = ACC.storefinder.storeId;
	  
	  if ($(".js-store-finder-map").length > 0) {
		  $(".js-store-finder-map").attr("id", "store-finder-map")
		  var centerPoint = new google.maps.LatLng(storeInformation["latitude"], storeInformation["longitude"]);
		  
		  var mapOptions = {
		    zoom : 13,
		    zoomControl : true,
		    panControl : true,
		    streetViewControl : false,
		    mapTypeId : google.maps.MapTypeId.ROADMAP,
		    center : centerPoint
		  }

		  var map = new google.maps.Map(document.getElementById("store-finder-map"), mapOptions);
		  
		  var marker = new google.maps.Marker({
		    position : new google.maps.LatLng(storeInformation["latitude"], storeInformation["longitude"]),
		    map : map,
		    title : storeInformation["name"],
		    icon : "https://maps.google.com/mapfiles/marker" + 'A' + ".png"
		  });
		  var infowindow = new google.maps.InfoWindow({
		    content : storeInformation["name"],
		    disableAutoPan : true
		  });
		  google.maps.event.addListener(marker, 'click', function() {
			  infowindow.open(map, marker);
		  });
	  }
	  
  },
  
  bindSearch : function() {
	  $(document)
	      .on(
	          "submit",
	          '#storeFinderForm',
	          function(e) {
		          e.preventDefault()

		          var q = $(".js-store-finder-search-input").val();
		          
		          // document.getElementById("setCityHomaPage").value=document.getElementById("cityName").value;
		          if (q.length > 0) {
			          /* Code change start by sumit for store locator implementation */
			          // ACC.storefinder.getInitStoreData(q);
			          ACC.storefinder.getStoreLatLng();
		          } else {
			          if ($(".js-storefinder-alert").length < 1) {
				          var emptySearchMessage = $(".btn-primary").data("searchEmpty")
				          $(".js-store-finder").hide();
				          $("#storeFinder")
				              .before(
				                  '<div class="js-storefinder-alert alert alert-danger alert-dismissable" ><button class="close" type="button" data-dismiss="alert" aria-hidden="true">×</button>'
				                      + emptySearchMessage + '</div>');
			          }
		          }
	          });
	  
	  /* Code Change by Sumit and Vishwa for Validation of store locator */

	  $(".js-store-finder").hide();  
  },
  storeAreaValidation : function(address, stlocadd) {	 
	  if (address == "SelectValue") {
		  alertModal('Please Select Your City');
		  $('#loading-image').hide();
		  $('#cboxOverlay').hide();
		  return false;
	  }
	  if (stlocadd == "") {
		  alertModal("Please Enter Your Locality");		  
		  $('#loading-image').hide();
		  $('#cboxOverlay').hide();
		  return false;
	  }

	  else if (address == "Bangalore" || address == "Bengaluru") {		  
		  if (stlocadd.indexOf("Bangalore") == -1 && stlocadd.indexOf("Bengaluru") == -1 ) {			 
			  $('#locality_error').show();
			  $('#locality_error1').show();
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
			  alertModal("Selected address is not within the selected city.");			  
			  return false;
		  }
	  } else if (address == "Shivamogga" || address == "Shimoga") {
		  if (stlocadd.indexOf("Shivamogga") == -1 && stlocadd.indexOf("Shimoga") == -1 ) {
			  $('#locality_error').show();
			  $('#locality_error1').show();
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
			  alertModal("Selected address is not within the selected city.");			  
			  return false;
		  }
	  } else if (address == "Gurugram" || address == 'Gurgaon'){
		  if (stlocadd.indexOf("Gurugram") == -1 && stlocadd.indexOf("Gurgaon") == -1) {
			  $('#locality_error').show();
			  $('#locality_error1').show();
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
			  alertModal("Selected address is not within the selected city.");
			  return false;
		  }
	  } else if (address == "Chennai" || address == 'Tamil Nadu'  ){
		  if (stlocadd.indexOf("Chennai") == -1 && stlocadd.indexOf("Tamil Nadu") == -1) {
			  $('#locality_error').show();
			  $('#locality_error1').show();
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
			  alertModal("Selected address is not within the selected city.");
			  return false;
		  }	  
	  } else if (address == "Hyderabad" || address == 'Secunderabad') {
		  if (stlocadd.indexOf("Hyderabad") == -1 && stlocadd.indexOf("Secunderabad") == -1) {
			  $('#locality_error').show();
			  $('#locality_error1').show();		
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
			  alertModal("Selected address is not within the selected city.");
			  return false;
		  }
	  } else if (stlocadd.indexOf(address) == -1) {		  
		  $('#locality_error').show();
		  $('#locality_error1').show();		
		  $('#loading-image').hide();
		  $('#cboxOverlay').hide();
		  return false;
	}	
	  
	  return true;
  },
  googleAddressStatusCheck : function(stlocadd) {
	  var geocoder = new google.maps.Geocoder();
	  geocoder.geocode({
		  'address' : stlocadd
	  }, function(results, status) {
		  if (status == google.maps.GeocoderStatus.OK) {
			  $('.store-finder-navigation-list').removeClass('on');
			  $('.bottomLinkBx').show();
			  ACC.storefinder.getStoreLatLng(stlocadd);
		  } else {
			  if (status == "ZERO_RESULTS") {
				  alertModal("There is an issue wth the locality address you have selected. Please choose another locality address.");
			  } else {
				  alertModal("Please Enter Your Locality address properly");
			  }
			  return false;
		  }
	  });	  
  },
  
  /* Code change start by sumit */

  getStoreLatLng : function(stlocadd) {
	  var localArea = encodeURI(stlocadd);
	  var addressData = document.getElementById('cityName');
	  var address = addressData.options[addressData.selectedIndex].value;
	  var googleApiKey = $('#googleApiKey').val();	 
	  var addressId = "";
	  if (localArea.indexOf('&') > -1) {
		  localArea = localArea.replace("&", "%26");
	  }
	  $.ajax({
	    url : "https://maps.googleapis.com/maps/api/geocode/json?address=" + localArea +"&key="+googleApiKey+"&sensor=false",
	    type : "get",
	    success : function(res) {	    	
	    	if(res["results"].length !== 0){
	    		var myData = res["results"][0].address_components;
	   		    var myPostalCode = "";
	   		    var longFormatted_address = ""; 	   		  
	   		    $.each(myData, function(i) {	   			    
	   			    longFormatted_address += this.long_name;	   			    
	   			    if (i !== myData.length - 1) {
	   				    longFormatted_address += ',';
	   			    }	   			    
	   			    if (this.types == "postal_code") {
	   				    myPostalCode = this.long_name;	   				    
	   			    }	   			    
	   		    });
	   		    longFormatted_address = $('#storelocator-query').val();	   	   		    
	   		    ACC.storefinder.getInitStoreData(address, res.results[0].geometry.location.lat, res.results[0].geometry.location.lng, myPostalCode,
	   		        longFormatted_address,addressId);	
	    	}else{
	    		alertModal("There is some Technical issue with Google Location Address. Please try again.");
	    		 $('#loading-image').hide();
	   		  	 $('#cboxOverlay').hide();	
	    		return false;
	    	}
		 
	    }
	  });
  },
  getAreaDetails : function(stlocadd) {  
	  var localArea = encodeURI(stlocadd);
	  var googleApiKey = $('#googleApiKey').val();	 
	  if (localArea.indexOf('&') > -1) {
		  localArea = localArea.replace("&", "%26");
	  }
		
	  $.ajax({
		url : "https://maps.googleapis.com/maps/api/geocode/json?address=" + localArea +"&key="+googleApiKey+"&sensor=false",
	    type : "get",
	    success : function(res) {	    
		    var myData = res["results"][0].address_components;	    
		    var mylocalArea = "";
		    var myPostalCode = "";
		    var longFormatted_address = "";
		    var locality="";
		    var formatted_address = res["results"][0].formatted_address;
		    $.each(myData, function(i) {
		    		longFormatted_address += this.long_name;			    
			    if (i !== myData.length - 1) {
				    longFormatted_address += ',';
			    }
			    if (this.types.indexOf("postal_code") > -1) {
				    myPostalCode = this.long_name;	
				    $('#sparAddressForm .info-text').addClass('hide');
			    }else{
			    	myPostalCode = "";
			    	$('#sparAddressForm .info-text').removeClass('hide');
			    }
			    if (this.types.indexOf("locality") > -1) {
			    	if(formatted_address.indexOf(this.long_name) !=-1){
			    		locality = this.long_name;
			    	}
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
		    var storeSession = JSON.parse(localStorage.getItem("storeLocator"));
		    $("#sparAddressForm input[name='postcode']").val(myPostalCode);
		    $("#sparAddressForm input[name='area']").val(mylocalArea); 
		     
		     var longAreaAddress= $("#storelocator-query1").val();	
		     var longAreaAddressReg=$("#storelocator-query-reg").val();		     
		    
		     if((null != longAreaAddress && longAreaAddress.includes("Egatoor"))){ 
		    	 $("#sparAddressForm input[name='townCity']").val("Tamil Nadu");
		     } 	 
		     else if((null != longAreaAddress && longAreaAddress.includes("Shimoga"))){ 
		    	 $("#sparAddressForm input[name='townCity']").val("Shimoga");
		    	 
		     }else if((null != longAreaAddress && longAreaAddress.includes("Shivamogga"))){
		    	 $("#sparAddressForm input[name='townCity']").val("Shivamogga");
		     }else{	
			    if((null != stlocadd && stlocadd.includes(locality))){			    	
			    	$("#sparAddressForm input[name='townCity']").val(locality);		    	 
			    }else{			    	
			    	$("#sparAddressForm input[name='townCity']").val(storeSession.city);
			    }	    
			   }
	    	}
	  });
  }, 
  
  getStoreData : function(page) {
	  ACC.storefinder.storeSearchData.page = page;
	  url = $(".js-store-finder").data("url");
	  $.ajax({
	    cache : false,
	    url : url,
	    data : ACC.storefinder.storeSearchData,
	    type : "get",
	    success : function(response) {
		    ACC.storefinder.storeData = $.parseJSON(response);
		    ACC.storefinder.refreshNavigation();
		    if (data["data1"] != "") {
			     ACC.storefinder.confirmStore();
		    }
	    }
	  });
  },
  
  getInitStoreData : function(q, latitude, longitude, postalCode, longFormatted_address, addressId) {
	  $(".alert").remove();
	  data = {
	    "q" : "",
	    "page" : 0
	  }
	  if (q != null) {
		  data.q = q;
	  }
	  
	  if (latitude != null) {
		  data.latitude = latitude;
	  }
	  
	  if (longitude != null) {
		  data.longitude = longitude;
	  }
	  
	  if (postalCode != null) {
		  data.postalCode = postalCode;
	  }
	  
	  if (longFormatted_address != null) {
		  data.longFormatted_address = longFormatted_address;
	  }
	  
	  if (addressId != null) {
	       data.addressId = addressId;
	   }
	  data.isCheckoutCall = false;	  
	  ACC.storefinder.storeSearchData = data;
	  ACC.storefinder.getStoreData(data.page);
	  $(".js-store-finder").show();
	  $(".js-store-finder-pager-prev").attr("disabled", "disabled")
	  $(".js-store-finder-pager-next").removeAttr("disabled")
  },
  confirmStore : function() {	  
	  var cityName = document.getElementById('cityName').value;
	  localStorage.setItem("defaultStoreLocation", $('.locContainer input[type="h"]').attr("data"));
	  var storeLocatorData = {};
	  storeLocatorData.city = $("#cityName").val();
	  storeLocatorData.area = $("#storelocator-query").val();
	  storeLocatorData.data = ACC.storefinder.storeData;
	  storeLocatorData.radio = [];	  
	  $('.locContainer input[type="hidden"]').each(function() {
		  storeLocatorData.radio.push($(this).attr("id"))
	  })
	  localStorage.setItem("storeLocator", JSON.stringify(storeLocatorData));	  
	  $('#homePageForm input[name="deliveryCityName"]').val(cityName);	  
	  
	  var confirmStoreFormUrl=$('#homePageForm').attr("action");	 
	  $("#homePageForm").ajaxSubmit({url: confirmStoreFormUrl, type: 'post',success:function(){
		  $('.flyout1').addClass('hidden1');		
		  $('#deliveryCityName').val(cityName);	
		  $('#loading-image').hide();
		  $('#cboxOverlay').hide();
		  localStorage.removeItem('currentAddress');
		  window.location.reload(true);
	  }});	  
	  //$('#homePageForm').submit();	  
	  
  },
  
  init : function() {
	  /* $("#findStoresNearMe").attr("disabled","disabled"); */
	  if (navigator.geolocation) {
		  navigator.geolocation.getCurrentPosition(function(position) {
			  ACC.storefinder.coords = position.coords;
			  $('#findStoresNearMe').removeAttr("disabled");
		  }, function(error) {
			  console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
		  });
	  }
  }
};