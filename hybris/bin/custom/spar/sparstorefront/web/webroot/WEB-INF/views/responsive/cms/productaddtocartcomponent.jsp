<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>

<!-- <div class="addtocart-component"> -->
		<%-- <c:if test="${product.stock.stockLevel gt 0}">
			<c:set var="productStockLevel">${product.stock.stockLevel}&nbsp;
				<spring:theme code="product.variants.in.stock"/>
			</c:set>
		</c:if>
		<c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
			<c:set var="productStockLevel">
				<spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
			</c:set>
		</c:if>
		<c:if test="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}">
			<c:set var="productStockLevel">
				<spring:theme code="product.variants.available"/>
			</c:set>
		</c:if>
		<div class="stock-status">
			${productStockLevel}
		</div> --%>
		<!-- <div class="row"> -->
			<action:actions element="div"  parentComponent="${component}"/>
		<!-- </div> -->
<!-- </div> -->