<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.AbstractOrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="hasShippedItems" value="${order.deliveryItemsQuantity > 0}" />
<div class="orderBox address">
	<%-- <div class="txtUnderline"><spring:theme code="text.deliveryAddress" text="Delivery Address"/></div> --%>
	<c:if test="${not hasShippedItems}">
		<spring:theme code="checkout.pickup.no.delivery.required"/>
	</c:if>
	<c:if test="${hasShippedItems}">		
		<ul class="deliveryAddress nopadding nomargin">
			<li>${fn:escapeXml(order.deliveryAddress.title)}&nbsp;${fn:escapeXml(order.deliveryAddress.firstName)}&nbsp;${fn:escapeXml(order.deliveryAddress.lastName)}</li>
		</ul>
		    <c:if test="${empty deliveryAddress.longAddress}">
			<p>${fn:escapeXml(order.deliveryAddress.line2)}, ${fn:escapeXml(deliveryAddress.buildingName)}<br/>		
			${fn:escapeXml(order.deliveryAddress.line1)}, ${fn:escapeXml(deliveryAddress.area)},<br>
			${fn:escapeXml(deliveryAddress.landmark)},&nbsp;<c:if test="${not empty order.deliveryAddress.region.name}"> ${fn:escapeXml(order.deliveryAddress.region.name)},</c:if>
			${fn:escapeXml(order.deliveryAddress.town)}, ${fn:escapeXml(order.deliveryAddress.postalCode)}</p>
			</c:if>
			
			<c:if test="${not empty deliveryAddress.longAddress}">
			<p>${fn:escapeXml(order.deliveryAddress.line2)}, ${fn:escapeXml(deliveryAddress.buildingName)}<br/>		
			${fn:escapeXml(order.deliveryAddress.line1)}, ${fn:escapeXml(deliveryAddress.landmark)},<br>
			${fn:escapeXml(deliveryAddress.longAddress)}<br/>
			<c:if test="${not empty deliveryAddress.postalCode && deliveryAddress.postalCode ne 'NA'}">
				&nbsp;${fn:escapeXml(deliveryAddress.postalCode)}
			</c:if>
			</p>
			</c:if>
						
			<%-- <li>${fn:escapeXml(order.deliveryAddress.country.name)}</li> --%>			
		</ul>
		<span>${fn:escapeXml(order.deliveryAddress.phone)}  email: ${fn:escapeXml(order.deliveryAddress.email)}</span>
	</c:if>
</div>
