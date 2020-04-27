<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>



<div class="register-section">
	<c:url value="/registeroptional/optionalDetails" var="registerActionUrl" />
	<user:optionalDetails actionNameKey="register.submit"
		action="${registerActionUrl}" />
</div>

	
	
