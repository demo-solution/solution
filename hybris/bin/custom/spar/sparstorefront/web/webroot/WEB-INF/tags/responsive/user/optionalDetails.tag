<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true"
	type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="otpgen" value="${otpgennum}"/>

<!--top navigation -->
<div class=row>
	

	<div class="row mt10">
		<!--mainrow-->
		<div class="col-lg-12 nopadding">
			<div class="container checkout-block">

		<section>
			<div class="wizard">
			<div class="check-top-bar col-lg-12">
				<div class="col-lg-12">
					<h4><i class="fa fa-list-alt" style="padding-right: 10px;"></i>REGISTER</h4>
				</div>
			</div>
			
		<form:form method="post" id="sparAddressForm" modelAttribute="sparAddressForm" action="${action}" role="form" style="background:#F4F4F4; padding:15px 15px 0 15px;">
			
	 <div class="tab-content">
                    <form:hidden path="countryIso" value="IN"/>
                   <%--  <input type="hidden" name="defaultStore" id="address.defaultStore" />
					<input type="hidden" name="defaultCncCenter" id="address.defaultCncCenter" />
					<input type="hidden" name="sparServiceAreaId" id="address.sparServiceArea.areaId" value="${sparServiceAreaId}"/> --%>
                    <div class="tab-pane active" role="tabpanel" id="step1">
                    
                       
                        <div class="col-xs-12 col-sm-12 step-content" id="address2" style="display:block; margin-top:20px;">
                        <h5 class="add-det-head">PERSONAL DETAILS</h5>
                        <div class="row">
                       	<div class="col-xs-12 col-sm-6 pad-top">                           
                           <div class="col-xs-12 col-sm-12 mobile-verify">
                           <formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true" /> 
	                           	<!-- <div class="col-sm-4 text-right">
									<label class="control-label" for="address.phone">Mobile No </label>
								</div>	 -->
								<div class="col-xs-12 col-md-8 pull-right">
									<%-- <div class="form-group">
										<input type="text" id="address.phone" path="phone" value="${PRIMARYMOBNUM}" class="form-control"/>							
									</div> --%>
									
									<div class="row">
										<c:url value="/registeroptional/otp-generator" var="storeFinderFormAction" />
										<div class="otpgenerate-finder js-otpgenerate-otpgenerate" data-url="${storeFinderFormAction}"></div>
										<div class="col-sm-12 nopadding validate-mobile text-left">
								 			To Verify Your Mobile Number <a class="pad-right danger-text" href="#myModal" id="generateOTPForCheckout" role="button" >Click Here</a>
								 			<div id="myModal" class="modal fade" role="dialog" data-backdrop="static">
											 <div class="modal-dialog">
											 	
											 	<div class="modal-content">
											 		<div class="modal-header">
											 			<button type="button" class="close" data-dismiss="modal">&times;</button>
											 			<h4 class="modal-title text-left">One time account verification</h4>
												    </div>
												    <div class="modal-body">
												    	<div class="row">
												    		<div class="col-lg-12">						
																<!-- <div class="checkbox">
																	 <label for="disableLRPrompt" class="disabled"><input id="disableLRPrompt" name="disableLRPrompt" type="checkbox" value="false" disabled>NOT Opt for LR	program.</label>							
																</div>	 -->	
																<div class="checkbox">
																	 <label for="lrOptStatus"><input id="lrOptStatus" name="lrOptStatus" type="checkbox" value="true" checked />Dear Customer, would you like to get rewarded for all your purchases?
																		Enrol for Landmark Rewards & earn valuable points.</label>							
																</div>				
															</div>
												    	
												    	</div>
												       <p class="text-center">Enter OTP <div class="form-group" style="width:100px; margin:0 auto;"><input class="form-control" id="userOTP" type="text"/></div></p>
												    </div>
												    <div class="modal-footer">												       
												        <button type="button" id="otpconfirmbtn" class="btn btn-primary" >Confirm</button>
												         <div id="thisField" class="hidden"><c:out value="${SysGenOTP}"></c:out></div>
												    </div>
											    </div>						
											</div>
										</div>
										        
					                </div>
									</div>
								</div>               
                           </div>       
                           
                       </div>
                       <div class="col-xs-12 col-sm-6 pad-top">
                         <c:if test="${isdobSet==false}"> 
                            <div class="col-sm-12">
								<formElement:formInputBox idKey="address.dateOfBirth" labelKey="address.dateOfBirth" path="dateOfBirth" inputCSS="form-control" mandatory="true" />                                      
                           </div>
                           </c:if>
                       </div>
                       
                     </div>
                     <div style="margin-top: 5px;font-size: 11px;">
                     	<span>If you are Landmark Rewards member, please use same mobile number while registering to earn points.</span>
                     </div>
						<h5 class="add-det-head nopadding mar-bottom">&nbsp;</h5>
                        <div class="col-xs-12 col-sm-6 pad-top pad-bottom">  
                        	<div class="col-sm-12 mb10">
				<formElement:formInputBox idKey="storelocator-query-reg"
					labelKey="address.longAddress" path="longAddress"
					inputCSS="form-control" mandatory="true"
					placeholder="Enter order delivery area" autocomplete="on" />

				<!-- <input type="text" id="storelocator-query1"	placeholder="Enter order delivery area" 
							autocomplete="on"	class="form-control" size="30" /> -->

				<button type="button" id="areaSearch" class="hide" />
			</div>
                        	                		
                           	<div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="form-control" mandatory="true" />                          
                           </div>
                           	<div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.line1" labelKey="address.line2" path="line2" inputCSS="form-control" mandatory="true" />                           
                           </div>
                           	<div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />                           
                           </div>
                           
                       </div>
                       <div class="col-xs-12 col-sm-6 pad-top pad-bottom">
                       		<div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />                           
                           </div>
                      		<div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.area" labelKey="address.area" path="area" inputCSS="form-control" mandatory="true" />                           
                           </div>
                           <div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.buildingName" labelKey="address.buildingName" path="buildingName" inputCSS="form-control" mandatory="true" />                          
                           </div>
                           <div class="col-xs-12 col-sm-12">
							<formElement:formInputBox idKey="address.landmark" labelKey="address.landmark" path="landmark" inputCSS="form-control" mandatory="false" />                         
                           </div>
                       </div>
                       <!-- <h5 class="add-det-head mar-bottom" style="float:left; width:100%; padding:0;">&nbsp;</h5>
                      <p class="col-lg-12"><input type="checkbox"> Subscribe to SPAR Offers, Promotions &amp; Newsletter</p>
                       <p class="col-lg-12"><input type="checkbox"> I agree to SPAR T&Cs</p>-->
                        </div>
                        <div class="col-sm-12 pad-top">
                      <!-- <div class="form-group pull-left" style="width:340px;"><span class="pull-left pad-right pad-top">Where did you here about us </span>
                        <select class="form-control pull-right" style="width:130px;"><option></option></select></div> -->
                       <ul class="list-inline pull-right row">
                            <li class="col-xs-12 col-lg-6"><a href="${request.contextPath}" class="btn btn-danger pull-right">SKIP NOW</a></li>
                            <li class="col-xs-12 col-lg-6"><!-- <a  href="checkout-loggedin-HD.html" class="btn btn-primary next-step">SUBMIT</a> -->
                            <ycommerce:testId code="register_Register_button">
										<button type="submit" class="btn btnRed" onsubmit="return validation()" style="float:left;">SUBMIT</button>
							</ycommerce:testId></li>
                        </ul></div>
                        </div>
                    
                    <div class="clearfix"></div>
                </div>
		</form:form>
		</div>
		</section>
		</div>
	</div>
</div>
</div>
<!--mainrow-->
