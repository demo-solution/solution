ACC.checkout = {

	_autoload: [
		"bindCheckO",
		"bindForms",
		"bindSavedPayments"
	],


	bindForms:function(){

		$(document).on("click","#addressSubmit",function(e){
			e.preventDefault();
				$('#loading-image').show();
			  	$('#cboxOverlay').show();
			localStorage.setItem("deliveryMode",''+$('input[type="radio"][name="deliveryType"]:checked').attr("id")+'')
			if($("#addressbook").is(":visible")){
				if($('#addressbook input[type="radio"][name="address"]').is(":checked")){					
					var curRIndx=$('#addressbook input[type="radio"][name="address"]:checked').index('#addressbook input[type="radio"][name="address"]');
					setTimeout(function() {
						$('#loading-image').hide();
					  	$('#cboxOverlay').hide();
						$('#addressbook form').eq(curRIndx).submit();
						  },1500);
				}else{
					alertModal('Please Select Delivery Address');
					$('#loading-image').hide();
				  	$('#cboxOverlay').hide();
				}
			}
			else if($("#addressform").is(":visible")){
				localStorage.setItem('addressform', 'true');
				$('#addressform #sparAddressForm').eq(0).submit();	
			}
			/*else if($('#clickNCollect').is(":visible")){
				if($('#clickNCollect input[type="radio"]').is(":checked")){
					var curRIndx=$('#clickNCollect input[type="radio"]:checked').index('#clickNCollect input[type="radio"]');
					var CNCphone=$('.collectNcenterPhoneBx input').val();
					if ((CNCphone == "") || (CNCphone.length != 10) || isNaN(CNCphone)) {
					  alertModal("Please enter your 10 digit mobile number");
					 
					  return false;
					}
					if($('#cncPhoneVerify').val()=="true"){
						if(localStorage.getItem("CNCMobile")!="true"){
							localStorage.removeItem("CNCMobile");
						
							alertModal("Please verify mobile number");
							return false;
						}
					}
					
					$('.clickCollectPhone').val(CNCphone)
					$('#clickNCollect>form').eq(curRIndx).submit();
				}else{
					alertModal('Please Select Collection Center')
				}
			}*/
		})
		
		$(document).on("click","#deliveryMethodSubmit",function(e){
			e.preventDefault();
			$('#selectDeliveryMethodForm').submit();	
		})

	},

	bindSavedPayments:function(){
		$(document).on("click",".js-saved-payments",function(e){
			e.preventDefault();

			ACC.colorbox.open("",{
				href: "#savedpayments",
				inline:true,
				width:"320px",
			});
		})
	},

	bindCheckO: function ()
	{
		var cartEntriesError = false;
		
		// Alternative checkout flows options
		$('.doFlowSelectedChange').change(function ()
		{
			if ('multistep-pci' == $('#selectAltCheckoutFlow').attr('value'))
			{
				$('#selectPciOption').css('display', '');
			}
			else
			{
				$('#selectPciOption').css('display', 'none');

			}
		});



		$('#viewcartLayout').on("click",'.continueShoppingButton',function ()
		{
			var checkoutUrl = $(this).data("continueShoppingUrl");
			window.location = checkoutUrl;
		});

		
		$('.expressCheckoutButton').click(function()
				{
					document.getElementById("expressCheckoutCheckbox").checked = true;
		});
		
		$(document).on("change",".confirmGuestEmail,.guestEmail",function(){
			  
			  var orginalEmail = $(".guestEmail").val();
			  var confirmationEmail = $(".confirmGuestEmail").val();
			  
			  if(orginalEmail === confirmationEmail){
			    $(".guestCheckoutBtn").removeAttr("disabled");
			  }else{
			     $(".guestCheckoutBtn").attr("disabled","disabled");
			  }
		});
		
		$('#viewcartLayout').on("click",'.checkoutButton',function ()
		{
			var checkoutUrl = $(this).data("checkoutUrl");
			$('#loading-image').show();
			$('#cboxOverlay').show();
			cartEntriesError = ACC.pickupinstore.validatePickupinStoreCartEntires();
			if (!cartEntriesError)
			{
				var expressCheckoutObject = $('.express-checkout-checkbox');
				if(expressCheckoutObject.is(":checked"))
				{
					window.location = expressCheckoutObject.data("expressCheckoutUrl");
				}
				else
				{
					var flow = $('#selectAltCheckoutFlow').attr('value');
					if ( flow == undefined || flow == '')
					{
						// No alternate flow specified, fallback to default behaviour
						window.location = checkoutUrl;
					}
					else
					{
						// Fix multistep-pci flow
						if ('multistep-pci' == flow)
						{
						flow = 'multistep';
						}
						var pci = $('#selectPciOption').attr('value');

						// Build up the redirect URL
						var redirectUrl = checkoutUrl + '/select-flow?flow=' + flow + '&pci=' + pci;
						window.location = redirectUrl;
					}
				}
			}
			return false;
		});

	}

};
