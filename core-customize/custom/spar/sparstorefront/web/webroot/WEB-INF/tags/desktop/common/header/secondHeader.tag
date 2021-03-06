<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>

<%-- Test if the UiExperience is currently overriden and we should show the UiExperience prompt --%>
<c:if
	test="${uiExperienceOverride and not sessionScope.hideUiExperienceLevelOverridePrompt}">
	<c:url value="/_s/ui-experience?level="
		var="clearUiExperienceLevelOverrideUrl" />
	<c:url value="/_s/ui-experience-level-prompt?hide=true"
		var="stayOnDesktopStoreUrl" />
	<div class="backToMobileStore">
		<a href="${clearUiExperienceLevelOverrideUrl}"><span
			class="greyDot">&lt;</span> <spring:theme
				code="text.swithToMobileStore" /></a> <span class="greyDot closeDot"><a
			href="${stayOnDesktopStoreUrl}">x</a></span>
	</div>
</c:if>

<script type="text/javascript">
	function setStore() {
		$('#storeForm').submit();
	}
</script>

<div id="header" class="clearfix">
	<form:form id="storeForm" name="storeForm"
		action="${request.contextPath}/setStore" method="post"
		modelAttribute="homePageForm">
	Store Selection:
	<form:select path="store" onchange="javascript:setStore();">
			<form:option  value="Please select a Store"></form:option>
			<form:options items="${pointOfServiceDatas}" itemValue="name"
				itemLabel="displayName" />
		</form:select>
		<%-- Delivery Type:
		<form:select path="deliveryType" onchange="deliveryslot.bindAll();">
			<form:option value="Please select Delivery Type"></form:option>
			<form:option value="HD" onclick="deliveryslot.bindAll();" >Home Delivery</form:option>
			<form:options items="${deliveryTypeEnums}" itemValue="code"
				itemLabel="name" />
		</form:select>
		Delivery Slot:
		<form:select path="deliverySlot" onchange="deliveryslot.bindAll();">
			<form:option value="Please select Delivery Slot"></form:option>
			<form:options items="${deliverySlots}" itemValue="slotDescription"
				itemLabel="slotDescription" />
		</form:select>
		<br></br> --%>
	</form:form>
</div>
