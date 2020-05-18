<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>




<div class="row appouterbox">
	  <div class="col-lg-12">
	   		<div class="col-lg-5 col-md-5 col-sm-5">
	   			<div class="appHeading">
	     			<%--<h3>Download Our App</h3>--%>
	     		</div>
	     	</div>
	      	<div class="col-lg-7 col-md-7 col-sm-7">
	     		 <div class="appButton">
	    			 <%--<input class="googleplaybutton" type="image" src="${commonResourcePath}/images/googlePlay.png" alt="googleplay" onclick="window.open('https://play.google.com/store/apps/details?id=com.spar.india&hl=en')">--%>
         			 <%--<input class="appstorebutton" type="image" src="${commonResourcePath}/images/appStore.png" alt="appstore" onclick="window.open('https://itunes.apple.com/lb/app/id1336384894#?platform=iphone')">--%>
	  			 </div>
	 		 </div>
	  	</div>
</div>
<div class="container-fluid footerBlock">
<ul>
<cms:pageSlot position="Footer" var="component">
                              
		<cms:component component="${component}"/>
		
		</cms:pageSlot></ul>
		
	<%-- Common Code for Alert -- tanveers -- Start --%>
	
	  
	
		<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">Alert</h4>
		      </div>
		      <div class="modal-body">
		        ...
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
		<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">Alert</h4>
		      </div>
		      <div class="modal-body">
		        ...
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-default confirmModal" data-dismiss="alert();">Confirm</button>
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
	<%-- Common Code for Alert -- tanveers -- End --%>	
		<%-- 
          <div class="row">
                    <div class="container">
                        <div class="col-sm-3">
                           <!--  <img alt="download Link" src="img/download_footer.jpg"> -->
                        </div>
                        <div class="col-sm-7 footer-nav">
                           <nav class="navbar">
                            <ul class="nav list">
                            <cms:pageSlot position="Footer" var="component">
                              
		<cms:component component="${component}"/>
		
		</cms:pageSlot>
		</ul>
                                  <!--   <ul class="nav list">
                                        <li class="list navbar-text"> <a href="#" class="txtCol"> About Us </a></li>
                                        <li class="list navbar-text"> <a href="#" a="" class="txtCol"> Media &amp; News </a></li>
                                        <li class="list navbar-text"> <a href="#" class="txtCol"> Contact US</a></li>
                                        <li class="list navbar-text"> <a href="#" class="txtCol"> FAQ</a></li>
                                        <li class="list navbar-text"> <a href="#" class="txtCol"> Policy</a></li>
                                    </ul> -->
                             </nav> 
                        </div>
                    <!--     <div class="col-sm-3 ">
                                 <div class="icon1 pull-right">
                                    <ul>
                                        <li class="socialIcons"><img alt="facebook" src="img/FB.png"> </li>
                                        <li class="socialIcons"><img alt="Instagram" src="img/Insta.png"></li>
                                        <li class="socialIcons"> <img alt="Youtube" src="img/Youtube.png"></li>
                                        <li class="socialIcons"> <img alt="blogger" src="img/blogger.png"></li>
                                        <li class="socialIcons"> <img alt="twitter" src="img/twitter.png"></li>
                                    </ul>
                                </div> 
                        </div> -->
                        
                     </div>
                   
         
</div> --%>


</div>

	
	
