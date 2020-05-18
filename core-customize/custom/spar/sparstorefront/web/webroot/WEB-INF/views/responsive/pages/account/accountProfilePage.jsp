<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="content-wrapper">
	<div class="account-section-header"><h3><spring:theme code="text.account.profile" text="Profile"/></h3></div>
<div class="account-section-content">
<table class="account-profile-data">
	<tr>
		<td><spring:theme code="profile.title" text="Title"/>: </td>
		<td>${fn:escapeXml(title.name)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.firstName" text="First name"/>: </td>
		<td>${fn:escapeXml(customerData.firstName)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.lastName" text="Last name"/>: </td>
		<td>${fn:escapeXml(customerData.lastName)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.email" text="E-mail"/>: </td>
		<td>${fn:escapeXml(customerData.displayUid)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.mobilenumber" text="Mobile Number"/>: </td>
		<td>${fn:escapeXml(customerData.custPrimaryMobNumber)}</td>
	</tr>
	<tr>
	
		 <td><spring:theme code="profile.dateofbirth" text="Date Of Birth"/>: </td>
		<td><fmt:formatDate type="date" pattern="dd/MM/yyyy"  value="${customerData.dateOfBirth}"/></td> 
 	</tr>
 	<c:if test="${customerData.whetherEmployee==true}">
 	<tr>
		 <td><spring:theme code="profile.employeecode" text="Date Of Birth"/>: </td>
		 <td>${fn:escapeXml(customerData.employeeCode)}</td>
 	</tr>
 	<tr>
		 <td><spring:theme code="profile.dateofjoining" text="Date Of Birth"/>: </td>
		 <td><fmt:formatDate type="date" pattern="dd/MM/yyyy"  value="${customerData.dateOfJoining}"/></td>
 	</tr>
 	
 	</c:if>
 	
</table>
<%-- <a class="button" href="update-password"><spring:theme code="text.account.profile.changePassword" text="Change password"/></a>
<a class="button" href="update-profile"><spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/></a> --%>
<div class="row changePasswordsrw">
	<div class="col-md-3 col-xs-12 col-md-push-3">
		<div class="form-group">
			<a class="btn btn-primary btn-block" href="update-password">
				<spring:theme code="text.account.profile.changePassword" text="Change password"/>
			</a>
		</div>
	</div>
	<div class="col-md-4 col-xs-12 col-md-push-3">
		<div class="form-group">		
			<a class="btn btn-primary btn-block" href="update-profile">
				<spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/>
			</a>		
		</div>
	</div>
</div>
</div>
<!-- This has been commented as part of comments given by Spar client for Beta release
<a class="button" href="update-email"><spring:theme code="text.account.profile.updateEmail" text="Update email"/></a>
-->
</div>
