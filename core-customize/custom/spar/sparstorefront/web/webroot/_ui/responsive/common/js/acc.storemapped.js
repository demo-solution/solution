ACC.storemapped = {
  storeData:"",
  storeSearchData : {},
  googleAddressStatusCheck : function( address, postalCode, deliveryArea, longFormatted_address, addressId, localArea){      
	  
	 var geocoder = new google.maps.Geocoder();
	 geocoder
		  .geocode(
		      {
		        'address' : deliveryArea
		      },
		      function(results, status){			 
		        if (status == google.maps.GeocoderStatus.OK) {alert("inside google");
		           var googleApiKey = $('#googleApiKey').val();
			       $.ajax({
			    	    url : "https://maps.googleapis.com/maps/api/geocode/json?address=" + localArea +"&key="+googleApiKey+"&sensor=false",
			             type : "get",
			             success : function(res){
				            if (res.status == "OK") {				            	
				              ACC.storemapped.getInitStoreData(address, res.results[0].geometry.location.lat,
				                  res.results[0].geometry.location.lng, postalCode, longFormatted_address, addressId);
				              $('#loading-image').hide();
				              $('#cboxOverlay').hide();
				            } else {
				              alertModal("There is an issue wth the locality address you have selected. Please choose another locality address.");
				              $('#loading-image').hide();
				              $('#cboxOverlay').hide();
				            }
				            return false;
			             }
			           });
		        } else {
			       alertModal("Invalid delivery address. Please enter valid address details.");
			       $('#addressSubmit').attr("disabled", "disabled");
			       $('#loading-image').hide();
			       $('#cboxOverlay').hide();
			       return false;
		        }
		      });
  },
 getInitStoreData: function (q, latitude, longitude, postalCode, longFormatted_address,addressId) {	
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
    data.isCheckoutCall = true;   
	if($('body').hasClass('page-multiStepCheckoutSummaryPage')){
		ACC.storemapped.storeMappingDataValidation(data);	
		return false;
	}     
	ACC.storemapped.storeSearchData = data;
	ACC.storemapped.getStoreData(data.page);
	
 },
getStoreData: function(page){
   ACC.storemapped.storeSearchData.page = page;
   url = $(".checkoutFindStore").data("url");      
   $.ajax({
     url : url,   
     cache:false,
     data : ACC.storemapped.storeSearchData,
     type : "get",
     success : function(response){
    	 ACC.storemapped.storeData = $.parseJSON(response);     	
    	 if(jQuery.isEmptyObject(ACC.storemapped.storeData['data1']) === false){    		
    		 ACC.storemapped.refreshNavigation();
	 	    if (data["data1"] != "") { 	    	
	 	    	ACC.storemapped.confirmStoreMapping(data);
	 	    } 
    	 } else{
    		 alertModal('Sorry!!! Currently, we do not deliver in this area. Please select another area.');  
    		 return false;
    	 }    	 
         
 	    return false;
     }
   });
 },
 refreshNavigation:function(){
	 /* Code Change by sumit to loop the collection center data */
	  var listitems1 = "";
	  var noOfColCenter = 0;
	  data = ACC.storemapped.storeData;	  		  
	  $('.noStoreData').hide();
	  $('.bottonColumn').show();
	  $('.clickCollect  .noAvailable, #homedeltextchng>.noAvailable').hide().siblings().show();	
	  if (data != "") {		
		  $('#homedelivery').removeAttr("disabled").val(data["data1"][0].name).trigger("click");
		  $('#hdSlot').html(data["data1"][0].dslots)
		  // localStorage.setItem("cncCenter","true");
	  } 
 },
storeMappingDataValidation: function(data){
   var gurgaonName = "Gurgaon";
   var gurugramName = "Gurugram";
   var bengaluruName = "Bengaluru";
   var bangaloreName = "Bangalore";
   var shivamogga = "Shivamogga";
   var shimoga = "Shimoga";
   var mangaloreName = "Mangalore";
   var mangaluruName = "Mangaluru";
   var secunderabad = "Secunderabad";
   var hyderabad = "Hyderabad";
   var delhiName = "Delhi";
   var newDelhiName = "New Delhi";    
   var addressTown = $('form.currentAddress').find('.addressParaOne').find('.addressTown').text();
   
     
   if ($('#deliveryCityName').val().toUpperCase() == bengaluruName.toUpperCase()) {	  
     if (!(addressTown.toUpperCase() == bengaluruName.toUpperCase() || addressTown.toUpperCase() == bangaloreName
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else if ($('#deliveryCityName').val().toUpperCase() == shivamogga.toUpperCase()) {
     if (!(addressTown.toUpperCase() == shivamogga.toUpperCase() || addressTown.toUpperCase() == shimoga
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else if ($('#deliveryCityName').val().toUpperCase() == gurugramName.toUpperCase()) {
     if (!(addressTown.toUpperCase() == gurugramName.toUpperCase() || addressTown.toUpperCase() == gurgaonName
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else if ($('#deliveryCityName').val().toUpperCase() == mangaluruName.toUpperCase()) {
     if (!(addressTown.toUpperCase() == mangaloreName.toUpperCase() || addressTown.toUpperCase() == mangaluruName
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else if ($('#deliveryCityName').val().toUpperCase() == hyderabad.toUpperCase()) {
     if (!(addressTown.toUpperCase() == secunderabad.toUpperCase() || addressTown.toUpperCase() == hyderabad
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else if ($('#deliveryCityName').val().toUpperCase() == delhiName.toUpperCase()) {
     if (!(addressTown.toUpperCase() == delhiName.toUpperCase() || addressTown.toUpperCase() == newDelhiName
         .toUpperCase())) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   } else {	   
     if ($('#deliveryCityName').val().toUpperCase() != addressTown.toUpperCase()) {
       alertModal("Selected address is not within the selected city.");
       $('#addressSubmit').attr("disabled", "disabled");
       return;
     }
   }    
   if($('body').hasClass('page-multiStepCheckoutSummaryPage')){	  
	   url = $(".checkoutFindStore").data("url");
       $.ajax({
         url : url,
         data : data,
         type : "get",
         success : function(response) {
        	 if (!response){        		
        		 data = $.parseJSON(response);        	
                 if (data["data1"] != "") {                	 
                	 $('form.currentAddress').find('#addressFindStore').val(data["data1"][0].name);
    		        $('#addressSubmit').removeAttr("disabled", "disabled");
    		        $('#loading-image').hide();
    				  $('#cboxOverlay').hide();
    	        }else if(data != ""){    	        	
    	        	  $('.global-alerts').hide();
                 }
    	        else {
    		        alertModal("Home Delivery is not available for this location.");
    		        $('#addressSubmit').attr("disabled", "disabled");
    	        } 
        	 }        	
         }
       });
   }
   return true; 
 },
 confirmStoreMapping:function(data){		 
	  var addressArea =  $('form.currentAddress').find('.addressParaOne').find('.addressArea').text();
	  var addressTown = $('form.currentAddress').find('.addressParaOne').find('.addressTown').text();
	  var longAddress = addressArea + addressTown + ", India" // Custom Long Address for old addresses
	  
	  if(addressTown == 'New Delhi'){
		  addressTown = 'Delhi';
	  }
	  
	  var cityName =$('form.currentAddress').find('#deliveryAddressTown').val();
	  localStorage.setItem("defaultStoreLocation", $('.locContainer input[type="h"]').attr("data"));
	  var storeLocatorData = {};
	  storeLocatorData.city = addressTown;
	  
	  if($('form.currentAddress').find('.addressParaOne span').hasClass('addressLongAddress')){		 
		  storeLocatorData.area = $('form.currentAddress').find('.addressParaOne').find('.addressLongAddress').text();
			  if(ACC.storemapped.storeData != ""){
				  $('form.currentAddress').find('#addressFindStore').val(ACC.storemapped.storeData["data1"][0].name); 
			  }else{
				  $('form.currentAddress').find('#addressFindStore').val(data.name);			  
			  }		
	  	  }else{			 
			  storeLocatorData.area = longAddress;		  
			  $('form.currentAddress').find('#addressFindStore').val(data.name);
		 }
		  storeLocatorData.data = data;
		  storeLocatorData.radio = [];	  
		  $('.locContainer input[type="hidden"]').each(function() {
			  storeLocatorData.radio.push($(this).attr("id"))
		  });
		  localStorage.setItem("storeLocator", JSON.stringify(storeLocatorData));
		  $('#homePageForm input[name="deliveryCityName"]').val(cityName);
		  var confirmStoreFormUrl=$('#homePageForm').attr("action");	
		  $("#homePageForm").ajaxSubmit({url: confirmStoreFormUrl, type: 'post',success:function(){
			  $('.flyout1').addClass('hidden1');		
			  $('#deliveryCityName').val(cityName);	
			  $('#loading-image').hide();
			  $('#cboxOverlay').hide();
		  }});
	  $('#homePageForm input[name="deliveryCityName"]').attr('value' ,addressTown);
	  window.location.reload(true);
 }
}
