<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ attribute name="isNotValidEmail" required="false" type="java.lang.Boolean" %>

<c:choose>
<c:when test="${isNotValidEmail}">
<div class="alert positive forgotten-password" id="validEmail" tabindex="0">
	<spring:theme code="account.error.account.not.found"/>
</div>
</c:when>
<c:otherwise>
<div class="alert positive forgotten-password" id="validEmail" tabindex="0">
	<spring:theme code="account.confirmation.forgotten.password.link.sent"/>
</div>
</c:otherwise>
</c:choose>
