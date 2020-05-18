<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header"
	tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<div class="content-wrapper">
	<div class="row">
		<div class="col-sm-12 nopadding">
			<div class="row bg-static wallet-wrapper">
				<div class="col-sm-12 nopadding">
					<h4>
						<spring:theme code="text.account.landmarkreward.caps" />
					</h4>
				</div>
				<div class="col-sm-12 p5">
					<c:choose>
						<c:when test="${customerData.isEnrolledToLR}">		
							<c:choose>
								<c:when test="${sparLRUserDetailDataResult ne null && sparLRUserDetailDataResult.result eq false}">	
									<spring:theme code="text.account.landmarkreward.pending.enroll.message" /> 
								</c:when>
								<c:when test="${sparLRUserDetailDataResult eq null}">	
									<spring:theme code="spar.landmark.serverdown.myaccount.message" /> 
								</c:when>
								<c:otherwise>
									<div class="table-responsive p10">
									<table class="table lr-customer-records">
									<tr>
									<td class="nopadding" style="width:30%;">										
										<table class="table">
											<tr class="tableHead tableHeadText">											
											<th>Member Name</th>											
										</tr>
										<tr class="tableHead tableHeadText">	
											<th><spring:theme	code="text.account.landmarkreward.available.balance" /> </th>											
										</tr>
										<tr class="tableHead tableHeadText">										
											<th>Email Id</th>
										</tr>
										<tr class="tableHead tableHeadText">										
											<th>Mobile Number</th>
										</tr>
										
										<tr class="tableHead tableHeadText">										
											<th>Tier Status</th>
										</tr>
										<!-- <tr class="tableHead tableHeadText">										
											<th>Ok To Accrue</th>
										</tr>
										<tr class="tableHead tableHeadText">										
											<th>Ok To Redeem</th>
										</tr>
										<tr class="tableHead tableHeadText">										
											<th>Active</th>
										</tr> 
										<tr class="tableHead tableHeadText">										
											<th>Equivalent Amount</th>
										</tr> -->
										</table>								
									</td>
										<td class="nopadding">
											<table class="table">
											<tr class="AccrBxP">										
											<td>${sparLRUserDetailDataResult.displayName}</td>											
										</tr>	
										<tr class="AccrBxP">
											<td> ${sparLRUserDetailDataResult.pointsAvailable}</td>										
										</tr>	
										<tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.emailID}</td>
										</tr>	
										<tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.mobileNumber}</td>
										</tr>
										<tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.tierStatus}</td>
										</tr>	
										<%-- <tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.okToAccrue}</td>
										</tr>	
										<tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.okToRedeem}</td>
										</tr>	
										<tr class="AccrBxP">	
											<td>${sparLRUserDetailDataResult.active}</td>
										</tr>
										<tr class="AccrBxP">	
											<td> ${sparLRUserDetailDataResult.balanceAvailable}</td>
										</tr>		 --%>								
											</table>
										</td>
									</tr>
								</table>
							</div>	
								</c:otherwise>
								</c:choose>
						</c:when>
						<c:otherwise>
							<div class="row">
										<c:choose>
											<c:when test="${customerData.custPrimaryMobNumber != null && customerData.lrOptStatus == 'false'}">	
												<a class="danger-text" data-toggle="modal" data-target="#landMarkMyAccountOtp" id="generateOTPForMyAccount" role="button" >Click Here </a><spring:theme code="text.account.landmarkreward.unavailable.message" />
												<c:url value="/registeroptional/landmarkMyaccountOTPGenerator" var="landmarkMyaccountOTPGeneratorVar" />
												<input type="hidden" value="${landmarkMyaccountOTPGeneratorVar}" id="landmarkMyaccountOTPGeneratorUrl" />
												<div class="otpgenerate-finder js-otpgenerate-otpgenerate" data-url="${storeFinderFormAction}"></div>
												
											</c:when>
											<c:when test="${customerData.lrOptStatus == 'true'}">	
												<spring:theme	code="text.account.landmarkreward.pending.enroll.message" /> 
											</c:when>
											<c:otherwise>
												<spring:theme	code="text.account.landmarkreward.mobile.verificationpending.message" /> 
											</c:otherwise>
										</c:choose>
										<div class="col-sm-12 nopadding text-left" style="font-size:12px;">
								 			<div id="landMarkMyAccountOtp" class="modal fade" role="dialog">
											 <div class="modal-dialog">
											 	<div class="modal-content">
											 		<div class="modal-header">
											 			<button type="button" class="close" data-dismiss="modal">&times;</button>
											 			<h4 class="modal-title text-left">One time account verification</h4>
												    </div>
												    <div class="modal-body">
												    	<div class="row">
												    		<div class="col-lg-12">
																<div class="checkbox">
																	 <label for="lrOptStatus">
																	 	<input id="lrOptStatus" name="lrOptStatus" type="checkbox" value="true" checked />Dear Customer, would you like to get rewarded for all your purchases?
																	 	<input id="disableLRPrompt" name="disableLRPrompt" type="hidden" value="false"/>
																		Enrol for Landmark Rewards & earn valuable points.</label>	
																								
																</div>				
															</div>
												    	
												    	</div>
												    	<%-- <div class="row otp">
												    		<div class="mobile-number text-center">Mobile No : <span>${custPrimaryMobNumber}</span></div>
												       		 <div class="text-center">Enter OTP <div class="form-group" style="width:100px; margin:0 auto;"><input class="form-control" id="otpCheck" type="text"/></div></idv>
												      		 <div class="error hide text-center txtCol-red">Please Enter valid OTP</div>
												    	</div> --%>
												    	
												    	<div class="row otp">
															<div class="col-lg-12">
															<div class="text-center">
																<p class="mobile-number">Mobile No : <span>${custPrimaryMobNumber}</span> </p>
																    <p>
																	Enter OTP <span class="form-group"	style="width: 100px; margin: 0 auto;">
																		<input id="otpCheck" class="form-control" type="text" /></span>
																		<div class="error hide text-center txtCol-red">Please Enter valid OTP</div>
																</p>
																</div>
															</div>
														</div>
												    	
												    </div>
												    <div class="modal-footer mt10"> 											       
												    	<c:url value="registeroptional/landmarkRewardRegistration" var="landmarkRegistrationUrl" />
														<input type="hidden" value="${landmarkRegistrationUrl}" id="landmarkRegistrationUrl" />
												        <button type="button" id="confirmLROtp" class="btn btn-primary" >Confirm</button>
												         <div id="thisField" class="hidden"><c:out value="${myAccountLROtp}"></c:out></div>
												    </div>
											    </div>						
											</div>
										</div>
										        
					                </div>
									</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
</div>