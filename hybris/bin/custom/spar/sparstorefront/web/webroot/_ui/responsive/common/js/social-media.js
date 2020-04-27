// Social Media Integration

// Google Sign In
var googleUser = {};
 var startApp = function() {
    gapi.load('auth2', function(){
  // Retrieve the singleton for the GoogleAuth library and set up the client.
  auth2 = gapi.auth2.init({
    client_id: '886722314335-d1rl211epdi3813oeic001m42etpbvsa.apps.googleusercontent.com',
    cookiepolicy: 'single_host_origin',
    // Request scopes in addition to 'profile' and 'email'
    //scope: 'additional_scope'
  });
  attachSignin(document.getElementById('customBtn'));
    });
  };

  function attachSignin(element) {	    
	    auth2.attachClickHandler(element, {},
	        function(googleUser) {
	    	var id_token = googleUser.getAuthResponse().id_token,
	            profile = googleUser.getBasicProfile(),
	            loginVia = googleUser.getAuthResponse().idpId,
	            userName = profile.getName(),
	            userEmail= profile.getEmail();	  
	    		authCheck(id_token,userEmail,loginVia,userName);	    	
	        });
	  }
  function authCheck(id_token,userEmail,loginVia,userName){	  
      $.ajax({
            url : baseURL + "/socialMediaLogin/checkUser",	                
            data : {
                 "token_id" : id_token,		                   
                 "email_id" : userEmail,
                 'login_media':loginVia
              },
            type : "get",	 
            beforeSend: function(){
            	$('#cboxOverlay').show();
		        $('#loading-image').show();	   		
              },
            success:function(response){
          	  if(response == "New User"){	                		 
          		  registerViaSocialMedia(id_token,userName,userEmail,loginVia);
          	  }else{	                		  
          		  if(response == "/login/checkout") {
          			  window.location = baseURL + "/cart";	
          		}else{
          			 window.location = baseURL;	           			          			 
          		}
          		  var closeBtn = "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>";
          		  var welcomeMsg ="<div class='alert alert-success social' role='alert'><strong>Welcome Back!" + " " + userName + "</strong> to SPAR. Continue Shopping. " + closeBtn + "</div>";
          			
          		
     			localStorage.setItem('msg', welcomeMsg);
          	  }          	  	 
            }            
      });
    }
function registerViaSocialMedia(id_token,userName,userEmail,loginVia){										        	 
	  $.ajax({
        url : baseURL + "/login/register",
          data : {
             "token" : id_token,
             "firstName" : userName,
             "email" : userEmail,
             'loginVia':loginVia
          },
        type : "post",
        beforeSend: function(){
        	$('#cboxOverlay').show();
	        $('#loading-image').show();	   		
          },
        success:function(response){
        	if($('body').hasClass('page-checkout-login')){
        		window.location = baseURL + "/cart";	
        	}else{
        		window.location = baseURL;
        	}        	
        },
        complete:function(){
        	$('#cboxOverlay').hide();
        	$('#loading-image').hide();        	
        }
  }); 
}
startApp();

//Login Via Facebook
window.fbAsyncInit = function() {
    // FB JavaScript SDK configuration and setup
    FB.init({
      appId      : '1045432868955798', // FB App ID
      cookie     : true,  // enable cookies to allow the server to access the session
      xfbml      : true,  // parse social plugins on this page
      version    : 'v2.8' // use graph api version 2.8
    });    
};

// Load the JavaScript SDK asynchronously
(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

// Facebook login with JavaScript SDK
function fbLogin() {
    FB.login(function (response) {
        if (response.authResponse) {
            // Get and display the user profile data
            getFbUserData();
        } 
    }, {scope: 'email'});
}

// Fetch the user profile data from facebook
function getFbUserData(){
    FB.api('/me', {locale: 'en_US', fields: 'id,first_name,last_name,email,link,gender,locale,picture'},
    function (response) {        
        var id_token = response.id,
       		userName = response.first_name+' '+response.last_name,
       		userEmail = response.email,
       		loginVia ="Facebook";
            authCheck(id_token,userEmail,loginVia,userName); 
    });
}