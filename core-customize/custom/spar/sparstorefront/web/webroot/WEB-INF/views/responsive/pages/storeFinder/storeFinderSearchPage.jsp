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

  <c:if test="${POS ne null and !empty POS.results}">
	{"total":${POS.pagination.totalNumberOfResults},"data":[
	<c:forEach items="${POS.results}" var="pos" varStatus="loopStatus">
		<c:set value="${ycommerce:storeImage(pos, 'cartIcon')}" var="storeImage"/>
		<c:url value="${pos.url}" var="storeUrl" scope="request"/>
		{
			"displayName" : "${pos.displayName}",
			"firstName" : "${pos.address.firstName}",
			"name" : "${pos.name}",
			"url" : "${pos.url}",
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
				<c:forEach items="${DSSLOTSPOS}" var="entry" varStatus="loop">
					"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}",
				</c:forEach>
			</c:if>
			<c:if test="${empty DSSLOTSPOS}">
					"dslots":"",
			</c:if>
			"userEmailId" : "${USEREMAILID}",
			<c:if test="${not empty pos.features}">
				"features" :[
						<c:forEach items="${pos.features}" var="feature" varStatus="featureNumber">
							"${feature.value}"<c:if test="${!featureNumber.last}">,</c:if>
						</c:forEach>
						],
					
			</c:if>
			"image" : "${storeImage.url}"
		}<c:if test="${!loopStatus.last}">,</c:if>
	</c:forEach>
	]
</c:if>


 <c:if test="${POS eq null or empty POS.results}">
	{"total":${POS.pagination.totalNumberOfResults},"data":[
	<c:forEach items="${POS.results}" var="pos" varStatus="loopStatus">
		<c:set value="${ycommerce:storeImage(pos, 'cartIcon')}" var="storeImage"/>
		<c:url value="${pos.url}" var="storeUrl" scope="request"/>
		{
			"displayName" : "${pos.displayName}",
			"firstName" : "${pos.address.firstName}",
			"name" : "${pos.name}",
			"url" : "${pos.url}",
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
				<c:forEach items="${DSSLOTSPOS}" var="entry" varStatus="loop">
					"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}",
				</c:forEach>
			</c:if>
			<c:if test="${empty DSSLOTSPOS}">
					"dslots":"",
			</c:if>
			"userEmailId" : "${USEREMAILID}",
			<c:if test="${not empty pos.features}">
				"features" :[
						<c:forEach items="${pos.features}" var="feature" varStatus="featureNumber">
							"${feature.value}"<c:if test="${!featureNumber.last}">,</c:if>
						</c:forEach>
						],
					
			</c:if>
			"image" : "${storeImage.url}"
		}<c:if test="${!loopStatus.last}">,</c:if>
	</c:forEach>
	]
</c:if>

 <c:if test="${STORE ne null and !empty STORE.results}">	
	,"total1":${STORE.pagination.totalNumberOfResults},"data1":[
	<c:forEach items="${STORE.results}" var="store" varStatus="loopStatus">
		<c:set value="${ycommerce:storeImage(store, 'cartIcon')}" var="storeImage"/>
		<c:url value="${store.url}" var="storeUrl" scope="request"/>
		{
			<c:if test="${not empty DSSLOTSSTORES}">
				<c:forEach items="${DSSLOTSSTORES}" var="entry" varStatus="loop">
					"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}",
				</c:forEach>
			</c:if>
			<c:if test="${empty DSSLOTSSTORES}">
					"dslots":"",
			</c:if>
			"name" : "${store.name}"			
		}<c:if test="${!loopStatus.last}">,</c:if>
	</c:forEach>

	]
}
</c:if>

 
  <c:if test="${STORE eq null or empty STORE.results}">
	,"total1":${0},"data1":[
	<c:forEach items="${STORE.results}" var="store" varStatus="loopStatus">
		<c:set value="${ycommerce:storeImage(store, 'cartIcon')}" var="storeImage"/>
		<c:url value="${store.url}" var="storeUrl" scope="request"/>
		{
			<c:if test="${not empty DSSLOTSSTORES}">
				<c:forEach items="${DSSLOTSSTORES}" var="entry" varStatus="loop">
					"dslots":"<fmt:formatDate value="${entry.key}" pattern="EEE - dd MMM"/>${space}${entry.value.slotDescription}",
				</c:forEach>
			</c:if>
			<c:if test="${empty DSSLOTSSTORES}">
					"dslots":"",
			</c:if>
				
			"name" : "${store.name}"			
		}<c:if test="${!loopStatus.last}">,</c:if>
	</c:forEach>
	]
}
</c:if>
 
