<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="row" style="margin-top:10px;"><!--mainrow-->
<div class="col-md-12 nopadding">
<div class="row register-section" >
	<section>
        <div class="wizard">
        	<div class="row check-top-bar">            	
				<ul class="list-inline">
		       	 	<li>
			       	 	<h4>
				       	 	<i class="fa fa-list-alt" style="padding-right:10px;"></i>
				       	 	<a href="javascript:void(0)" class="link1">LOGIN</a>
			       	 	</h4>
		       	 	</li>
		       	 	<li>|</li>
		       	 	<li>
		       	 		<h4>
		       	 			<a href="javascript:void(0)" class="link2"><b>SIGN UP</b></a>
		       	 		</h4>
		       	 	</li>
	       	 	</ul>
	          </div>         
            </div>
            <div class="col-md-12 col-xs-12 loginRaginCntr">
            <form:form method="post" modelAttribute="sparRegisterForm" action="${action}" role="form">
               <div class="form_field-elements js-recaptcha-captchaaddon">
                <div class="tab-content">
                    <div class="" role="tabpanel" id="step1">                 
                        <div class="col-sm-12 col-xs-12 step-content" id="address2" style="display:block; margin-top:20px;">
                        <h5 class="add-det-head">SIGN UP HERE...</h5>
                       <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 pad-top">
                          <div class="row fname"> 	
                          		<div class="row">
                          			<div class="col-xs-3 hidden-md hidden-sm hidden-lg"><label>Prefix</label></div>
                          			<div class="col-xs-9 hidden-md hidden-sm hidden-lg"><label>First Name</label></div>
                          		</div>						
	                            <div class="hidden-xs col-sm-4 col-md-4 col-lg-4 text-right nopadding">
									<label class="fnameLabel">First Name</label>
								</div>
								<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 pr0">
			                         	 <formElement:formTitleSelectBox idKey="register.title"   labelKey="" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}"/>
			                       </div>
		                          <div class="col-xs-9 col-sm-5 col-md-5 col-lg-5">
		                          	<formElement:formFirstNameInputBox idKey="register.firstName"	labelKey="register.firstName" path="firstName"	mandatory="true" />
		                          </div>
	                           
                          </div>   
							<!-- <div class="col-sm-4 text-right">Email</div> -->
                            <div class="row email"><formElement:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text" mandatory="true"/></div>
                          
                           <div class="col-sm-12 text-right" style="font-size:12px;">
							<!--To Verify Your Mobile Number 
                            <a class="pad-right danger-text btn" href="#myModal" role="button" data-toggle="modal">Click Here</a>-->        
                            <div id="myModal" class="modal fade" role="dialog" data-backdrop="static">
							  <div class="modal-dialog">						
							    <!-- Modal content-->
							    <div class="modal-content">
							      <div class="modal-header">
							        <button type="button" class="close" data-dismiss="modal">&times;</button>
							        <h4 class="modal-title text-left">One time account verification</h4>
							      </div>
							      <div class="modal-body">
							        <p class="text-center">Enter OTP <span class="form-group" style="width:100px; margin:0 auto;"><input class="form-control" type="text"/></span></p>
							      </div>
							      <div class="modal-footer">
							      <p class="pull-left green-text"><input type="checkbox"> Sign Up for Landmark Rewards Point</p>
							        <button type="button" class="btn btn-primary" data-dismiss="modal">Confirm</button>
							      </div>
							    </div>							
							  </div>
							</div>                
                           </div>
                           
                       </div>
                       <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 pad-top pr30">
                       	<div class="row lname">
	                        <div class="col-sm-12 col-md-12 col-lg-12">
	                       		<formElement:formInputBox idKey="register.lastName" labelKey="register.lastName" path="lastName" mandatory="true" /> 
	                        </div>
	                      </div>
	                        <div class="row">
		                        <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                        	<formElement:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd" mandatory="true" />
		                        </div>
	                        </div>
	                        <div class="row">
		                        <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		                        	<formElement:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd" mandatory="true" />
		                        </div>  
		                    </div>   
                       </div>
						 <div class="brd-bottom clearfix"></div>
                        <div class="col-sm-12 amt-paid-reg">
                       <!--  <i class="fa fa-check-square-o" style="padding-right:10px"></i>Are you SPAR Employee?  
                       -->
                       <formElement:formCheckbox idKey="register.whetherEmployee" labelKey="register.whetherEmployee" path="whetherEmployee" inputCSS="text" mandatory="false"/>
                       
                        </div>
                            
    
                       <div id="test">
                        <div class="col-sm-6 pad-top pad-bottom ">
                       	 <div class="row">
                   
							<!-- <div class="col-sm-4 text-right">Last Name</div>
                            <div class="col-sm-12"><div class="form-group"><input type="text" class="form-control panel-danger" value="" style="width:100%;"/></div>
                            </div>  --> 
                           <formElement:formInputBox idKey="register.employeeCode"	labelKey="register.employeeCode" path="employeeCode"	mandatory="true" />
                                                     
       
               <div class="form-group"><formElement:formInputBox idKey="register.dateOfJoining" labelKey="register.dateOfJoining" path="dateOfJoining" inputCSS="text" mandatory="true" placeholder="mm/dd/yyyy"/>
		</div>
                     
                         
                        </div>    
                       </div>
                     
                           <div class="col-sm-6 pad-top pad-bottom pr30">
                            	<div class="row dob">
                           			 <formElement:formInputBox idKey="register.dateOfBirth" labelKey="register.dateOfBirth" path="dateOfBirth"  inputCSS="text" mandatory="true" placeholder="mm/dd/yyyy"/>
				  			  </div>                     
                         
                       </div>
                       </div>
                     
                        <div class="brd-bottom clearfix"></div>
                       <div class="col-sm-9 spart-c nopadding"> 
                       <!-- <p class="col-lg-12"><input type="checkbox"> Subscribe to SPAR Offers, Promotions &amp; Newsletter</p>
                       <p class="col-lg-12"><input type="checkbox"> I agree to SPAR T&Cs</p> -->
                       
                      <div class="col-lg-12"><formElement:formCheckbox idKey="register.whetherSubscribedToPromotion" labelKey="register.whetherSubscribedToPromotion" path="whetherSubscribedToPromotion" inputCSS="text" mandatory="false"/></div>
                       <div class="col-lg-12 landmark-grp"><formElement:formCheckbox idKey="register.whetherSubscribedToLandmark" labelKey="register.whetherSubscribedToLandmark" path="whetherSubscribedToLandmark" inputCSS="text" mandatory="false"/></div>
                       
                       </div>
                       <div class="col-sm-6" style="display:none;"> 
                       <div class="row">
							<!-- <div class="col-sm-4 text-right">Enter Text</div>
                            <div class="col-sm-8"><div class="form-group"><input type="text" class="form-control panel-danger" style="width:100%;"/></div>
                            </div>  -->
                            <div class="col-sm-4 text-right pr0">Enter Text</div>
                            <div class="col-sm-8"><div class="form-group"><input  id="recaptchaChallangeAnswered" class="form-control panel-danger" style="width:50%;" value="${requestScope.recaptchaChallangeAnswered}" /></div>
                            </div>                          
                           </div>
                       
                       </div>
                    
                      <div class="col-sm-3 col-xs-12 pull-right mt15 sign-up-btn">
                       <button type="button" onclick="myFunction()" class="btn btnRed" >CANCEL</button>
                       <!-- <a  href="2B_Register-Optional-Personal-Details.html" class="btn btn-primary next-step">REGISTER NOW</a> -->
                        
         <ycommerce:testId code="register_Register_button">
     
		<button type="submit"  class="btn btnRed"  onsubmit="return validation()" >SIGN UP</button>
		
		</ycommerce:testId>
		
                       </div>
                        </div>
                     
                        </div>
                    <div class="tab-pane" role="tabpanel" id="step2">
                        <h3>Step 2</h3>
                        <p>This is step 2</p>
                        <ul class="list-inline pull-right">
                            <li><button type="button" class="btn btn-primary next-step">Save and continue</button></li>
                        </ul>
                    </div>
                    <div class="tab-pane" role="tabpanel" id="step3">
                        <h3>Step 3</h3>
                        <p>This is step 3</p>
                        <ul class="list-inline pull-right">
                            <li><button type="button" class="btn btn-default prev-step">Previous</button></li>
                            <li><button type="button" class="btn btn-primary btn-info-full next">CONTINUE</button></li>
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
                </div>
            </form:form>
          </div>
        </div>
    </section>

</div>
</div>
</div>