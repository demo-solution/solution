<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement"	tagdir="/WEB-INF/tags/responsive/formElement"%>

<div id="lrProgram" class="modal fade" role="dialog">
			<div class="modal-dialog">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title text-left">Landmark Rewards Program</h4>
					</div>
					<div class="modal-body">
						<div class="row">
							<div class="col-lg-12">						
								<div class="checkbox">
									 <label for="disableLRPrompt" class="disabled"><input id="disableLRPrompt" name="disableLRPrompt" type="checkbox" value="false" disabled>Never Show Again.</label>							
								</div>		
								<div class="checkbox">
									 <label for="lrOptStatus"><input id="lrOptStatus" name="lrOptStatus" type="checkbox" value="true" checked />Dear Customer, would you like to get rewarded for all your purchases?
										Enrol for Landmark Rewards & earn valuable points.</label>							
								</div>				
							</div>
						</div>
						<div class="row otp">
							<div class="col-lg-12">
							<div class="text-center">
								<c:choose>
									<c:when test="${not empty customerData.custPrimaryMobNumber}">	
										<p>Mobile No : ${customerData.custPrimaryMobNumber} </p>
									</c:when>
									<c:otherwise>
										<p>Mobile No : ${mobNumberForLRPopup} </p>
									</c:otherwise>
								</c:choose>
								    <p>
									Enter OTP <span class="form-group"
										style="width: 100px; margin: 0 auto;">
										<input id="otpCheck"	class="form-control" type="text" /></span>
										<div class="error hide text-center txtCol-red">Please Enter valid OTP</div>
								</p>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="confirmLROtp" data-dismiss="modal">Confirm</button>
						<div id="thisField" class="hidden"><c:out value="${otpLR}"></c:out></div>
					</div>
				</div>
			</div>
		</div>	
