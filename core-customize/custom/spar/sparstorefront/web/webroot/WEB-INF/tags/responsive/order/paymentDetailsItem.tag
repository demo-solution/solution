<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

	<c:choose>
	<c:when test="${null!= order.paymentMode && order.paymentMode.code eq 'cashondelivery' }">
		${fn:escapeXml(order.paymentMode.name)}
	</c:when>
	<c:when test="${null!= order.paymentMode && order.paymentMode.code eq 'voucherPaymentMode' }">
		${fn:escapeXml(order.paymentMode.name)}
	</c:when>
	<c:otherwise>
		${fn:escapeXml(order.paymentMode.name)} &nbsp;
		${fn:escapeXml(order.paymentInfo.cardTypeData.name)}
		${fn:escapeXml(order.paymentInfo.cardNumber)}
	</c:otherwise>
	</c:choose>

