<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${ServiceArea ne null}">{"ServiceAreaData":[
	<c:forEach items="${ServiceArea}" var="serviceArea" varStatus="loopStatus">
		{
			"areaId" : "${serviceArea.areaId}",
			"displayName" : "${serviceArea.displayName}",
			"area" : "${serviceArea.area}",
			"pincode" : "${serviceArea.pincode}",
			"defaultStore" : "${serviceArea.defaultStore}",
			"defaultCncCenter" : "${serviceArea.defaultCncCenter}",
			"city" : "${serviceArea.city}",
			"active" : "${serviceArea.active}"
		}<c:if test="${!loopStatus.last}">,</c:if>
	</c:forEach>
	]
</c:if>
}