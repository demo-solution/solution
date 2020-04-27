<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true"
	type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>


<div class="row" style="margin-top: 10px;">
	<!--mainrow-->

	<div class="col-md-12 nopadding">
		<div class="row login-section">
			<section>
				<div class="wizard">
					<div class="row check-top-bar">
						<ul class="list-inline">
							<li>
								<h4>
									<i class="fa fa-list-alt" style="padding-right: 10px;"></i> <a
										href="javascript:void(0)" class="link1"> <b>LOGIN </b></a>
								</h4>
							</li>
							<li>|</li>
							<li>
								<h4>
									<a href="javascript:void(0)" class="link2"> SIGN UP</a>
								</h4>
							</li>
						</ul>
					</div>
					<div class="col-md-12 loginRaginCntr">					
						<form:form method="post" modelAttribute="loginForm"
							action="${action}" role="form">

							<div class="tab-content">							
								<div class="" role="tabpanel" id="step1">
									<div class="step-content" style="display: block;">
										<i class="fa fa-user"></i>		
										<h3>Login to Spar</h3>															
										<div class="col-sm-12 col-md-12 col-lg-12">
											<formElement:formInputBox idKey="j_username"
												labelKey="login.email" path="j_username" mandatory="true" />
										</div>
										<div class="col-sm-12 col-md-5 col-lg-12">
											<formElement:formPasswordBox idKey="j_password"
												labelKey="login.password" path="j_password"
												inputCSS="form-control" mandatory="true" />

											<div class="col-sm-12">
												<ycommerce:testId code="login_forgotPassword_link">
													<a href="<c:url value='/login/pw/request'/>"
														class="js-password-forgotten" style="font-size: 12px;"
														data-cbox-title="<spring:theme code="forgottenPwd.title"/>">
														<spring:theme code="login.link.forgottenPwd" />
													</a>
												</ycommerce:testId>
											</div>											
										</div>

										<div class="col-sm-12 col-md-12 col-lg-12 mt15 login-btn">
											<ycommerce:testId code="loginAndCheckoutButton">
												<button type="submit" class="btn btnRed">
													<spring:theme code="${actionNameKey}" />
												</button>
											</ycommerce:testId>
											 <div class="social-media-wrapper">											
												<div class="col-lg-6 col-xs-12">													
													<div id="gSignInWrapper">											    
													    <div id="customBtn" class="btn-signin btn-google">
													      <span class="fa fa-google"></span>
													      <span class="buttonText">Login with Google</span>
													    </div>
												  	</div>											   										
												</div>
												<div class="col-lg-6 col-xs-12">													
													<div class="btn-signin fb-btn" onclick="fbLogin()" id="fbLink">
														<span class="fa fa-facebook"></span>
													    <span class="buttonText">Login with Facebook</span>
													</div>
												</div>
											</div> 
											<div class="clearfix"></div>
											<div class="divider divider-brk"><h5>New to SPAR?</h5></div>
											<div class="mt10"><button type="button" id="createAccount" class="btn">SIGN UP</button></div>											
										</div>
									</div>
								</div>
								<div class="tab-pane" role="tabpanel" id="step2">
									<h3>Step 2</h3>
									<p>This is step 2</p>
									<ul class="list-inline pull-right">
										<li><button type="button"
												class="btn btn-primary next-step">Save and continue</button></li>
									</ul>
								</div>
								<div class="tab-pane" role="tabpanel" id="step3">
									<h3>Step 3</h3>
									<p>This is step 3</p>
									<ul class="list-inline pull-right">
										<li><button type="button"
												class="btn btn-default prev-step">Previous</button></li>
										<li><button type="button"
												class="btn btn-primary btn-info-full next">CONTINUE</button></li>
									</ul>
								</div>
								<div class="tab-pane" role="tabpanel" id="step4">
									<h3>Complete</h3>
									<p>You have successfully completed all steps.</p>
									<ul class="list-inline pull-right">
										<li><button type="button" class="btn btn-default first">Previous</button></li>

									</ul>
								</div>
								<div class="clearfix"></div>
							</div>
						</form:form>
					</div>
				</div>
			</section>

		</div>
	</div>
</div>
