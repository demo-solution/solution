<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="row container-fluid" style="margin-top:10px;"><!--mainrow-->
<div class="col-lg-12 nopadding greyBGpwd">
<div class="forgot-Pad">

	<section>
        <div class="wizard forgotten-password">
            
			<!-- <div class="check-top-bar col-lg-12 col-xs-12 shadowBarPad">
            <div class="col-lg-12 col-xs-12"><h4><i class="fa fa-list-alt" style="padding-right:10px;"></i>FORGOT PASSWORD </div>
           
            </div> -->
                        
			<form:form method="post" modelAttribute="forgottenPwdForm">
              <div class="tab-content">
                <div class="step-content" role="tabpanel" id="step1"> 
                <h3>FORGOT PASSWORD</h3>                     
                 <div class="col-sm-12 col-xs-12">                            
                     <div class="control-group">
						<ycommerce:testId code="login_forgotPasswordEmail_input">
							<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" mandatory="true"/>
						</ycommerce:testId>						
					</div>
                      <div class="col-md-12 col-xs-12">
                      	<button id="closeBtn" class=" text-right btn btn-danger btn-sm btn-length" type="button"> CLOSE </button>
                        <!-- <button class=" text-right btn btn-danger btn-sm btn-length " type="button"> SUBMIT </button> -->
                        <ycommerce:testId code="login_forgotPasswordSubmit_button">
							<button class="btn text-right btn-sm btn-length default-btnC mt10 " type="submit">
								<spring:theme code="forgottenPwd.title"/>
							</button>
						</ycommerce:testId>                        
                      </div>                                                                
                    <div class="clearfix"></div>
                </div>	
			</div>
         </div>		 
		 </form:form>
		 
		 
        </div>
    </section>
</div>
</div>
</div>
</div>