<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="account-section-header">
	<h3>
		<spring:theme code="text.account.profile.updatePersonalDetails" />
	</h3>
</div>
<div class="account-section-content	 account-section-content-small profile-update-form ">
	<form:form action="update-profile" method="post"
		modelAttribute="sparUpdateProfileForm">
		<div class="row">
			<div class="col-lg-6">
				<formElement:formSelectBox idKey="profile.title"
					labelKey="profile.title" path="titleCode" mandatory="true"
					skipBlank="false" skipBlankMessageKey="form.select.empty"
					items="${titleData}" selectCSSClass="form-control" />
				<formElement:formInputBox idKey="profile.firstName"
					labelKey="profile.firstName" path="firstName" inputCSS="text"
					mandatory="true" />
					<div class="clearfix"></div>
				<formElement:formInputBox idKey="profile.lastName"
					labelKey="profile.lastName" path="lastName" inputCSS="text"
					mandatory="true" />
				<c:if test="${isdobSet==false}">
					<formElement:formInputBox idKey="profile.dateofbirth"
						labelKey="profile.dateofbirth" path="dateOfBirth"
						placeholder="mm/dd/yyyy" mandatory="true" />
				</c:if>
			</div>
			<div class="col-lg-6">	
			<formElement:formCheckbox idKey="profile.whetherEmployee"
					labelKey="profile.whetherEmployee" path="whetherEmployee"
					inputCSS="text" mandatory="true" />		
				<div class="hidden">
					<formElement:formInputBox idKey="profile.employeeCode"
					labelKey="profile.employeeCode" path="employeeCode" inputCSS="text"
					mandatory="true" />
					<div class="clearfix"></div>
				<formElement:formInputBox idKey="profile.dateOfJoining"
					labelKey="profile.dateOfJoining" path="dateOfJoining"
					placeholder="mm/dd/yyyy" />
				</div>
						
			</div>
		</div>


		<div class="row accountActions">
		<div class="col-xs-12 col-md-3 col-md-push-3 accountButtons">
			<div class="form-group">
				<ycommerce:testId
					code="personalDetails_cancelPersonalDetails_button">
					<button type="submit" class="btn btn-primary btn-block">
						<spring:theme code="text.account.profile.saveUpdates"
							text="Save Updates" />
					</button>
				</ycommerce:testId>
			</div>

		</div>
		<div class="col-xs-12 col-md-3 col-md-push-3 accountButtons">
			<div class="form-group">
				<ycommerce:testId code="personalDetails_savePersonalDetails_button">
					<button type="button" class="btn btn-default btn-block backToHome">
						<spring:theme code="text.account.profile.cancel" text="Cancel" />
					</button>
				</ycommerce:testId>
			</div>
		</div>
		</div>
</div>
</form:form>
</div>
