<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/responsive/store" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="space" value=" "/>
<c:set var="isSelectedHDDelSlot" value="false" scope="page"/>
<c:set var="isSelectedCNCDelSlot" value="false" scope="page"/>

	{"dataPos":{
		"data":[
	<c:forEach items="${POS}" var="pos" varStatus="loopStatus">
		{
			"displayName" : "${pos.displayName}",
			"firstName" : "${pos.address.firstName}",
			"name" : "${pos.name}",
			"phone" : "${pos.address.phone}",
			"formattedDistance" : "${pos.formattedDistance}",
			"line1" : "${pos.address.line1}",
			"line2" : "${pos.address.line2}",
			"town" : "${pos.address.town}",
			"postalCode" : "${pos.address.postalCode}",
			"regionName" : "${pos.address.region.name}",
			"countryName" :"${pos.address.country.name}",
			"latitude" : "${pos.geoPoint.latitude}",
			"longitude" : "${pos.geoPoint.longitude}",
			<c:if test="${not empty pos.openingHours}">
				"openings":<store:openingSchedule openingSchedule="${pos.openingHours}" />
			</c:if>
			<c:if test="${not empty DSSLOTSPOS}">
				<%-- <c:forEach items="${DSSLOTSPOS}" var="entry" varStatus="loop">
					<c:forEach items="${entry.value}" var="radioValue">	
						<c:if test="${isSelectedCNCDelSlot eq false}">
							<c:if test="${radioValue.availableSlot ne false}">
								"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${radioValue.slotDescription}",
								<c:set var="isSelectedCNCDelSlot" value="true" scope="page"/>
							</c:if>
						</c:if>
					</c:forEach>
				</c:forEach> --%>
				<c:forEach items="${DSSLOTSPOS}" var="entry" varStatus="loop">
					"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}",
				</c:forEach>
			</c:if>
			<c:if test="${empty DSSLOTSPOS}">
					"dslots":"",
			</c:if>
			"userEmailId" : "${USEREMAILID}"
			<c:if test="${not empty pos.features}">
				,"features" :[
						<c:forEach items="${pos.features}" var="feature" varStatus="featureNumber">
							"${feature.value}"<c:if test="${!featureNumber.last}">,</c:if>
						</c:forEach>
						]
					
			</c:if>
		}<c:if test="${!loopStatus.last}">,</c:if>
		<c:set var="isSelectedCNCDelSlot" value="false" scope="page"/>
	</c:forEach>
	
	],"data1":[	
	<c:if test="${not empty DSSLOTSSTORES}">
		<%-- <c:forEach items="${DSSLOTSSTORES}" var="entry" varStatus="status">
			<c:forEach items="${entry.value}" var="radioValue" varStatus="radio">
				<c:if test="${isSelectedHDDelSlot eq false}">
					<c:if test="${radioValue.availableSlot ne false}">	
						{"name":"","dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${radioValue.slotDescription}"}						
						<c:set var="isSelectedHDDelSlot" value="true" scope="page"/>
					</c:if>
				</c:if>
			</c:forEach>
		</c:forEach> --%>
		<c:forEach items="${DSSLOTSSTORES}" var="entry" varStatus="loop">
			{"active":"${hdActive}","name":"${defaultStore}","dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}"}
		</c:forEach>
	</c:if>
	<c:if test="${empty DSSLOTSSTORES}">
			{"name":"","dslots":""}
	</c:if>
	
	]
	}
}