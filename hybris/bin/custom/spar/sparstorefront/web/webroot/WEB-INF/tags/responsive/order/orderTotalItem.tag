<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="orderGroup" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

	<c:set var="lastCount" value="0"></c:set>
	
			<c:forEach items="${orderGroup.entries}" var="entry" varStatus="count">
			<c:set var="lastCount" value=" ${count.index+1}"></c:set>
			</c:forEach>

<c:out value="${lastCount}"></c:out>