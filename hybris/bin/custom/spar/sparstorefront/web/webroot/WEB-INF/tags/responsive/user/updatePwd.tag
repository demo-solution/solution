<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

	<div class="account-section">
			<div class="account-section-header"><spring:theme code="resetPwd.title"/></div>
			<div class="account-section-content	 account-section-content-small">
				<form:form method="post" modelAttribute="updatePwdForm">					
					<div class="row">
						<div class="col-sm-12 col-md-12">
							<h3><spring:theme code="resetPwd.title"/></h3>
							<formElement:formPasswordBox idKey="updatePwd.pwd" labelKey="updatePwd.pwd" path="pwd" inputCSS="form-control" mandatory="true" />
							<formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="updatePwd.checkPwd" path="checkPwd" inputCSS="form-control" mandatory="true" />					
						</div>					
					</div>
					<div class="row">
						<div class="col-sm-12 col-md-12">
							<button type="submit" class="btn btn-primary btn-block">
								<spring:theme code="text.account.profile.resetPassword" text="Reset Password" />
							</button>
						</div>
					</div>
				</form:form>
			</div>
		</div>
