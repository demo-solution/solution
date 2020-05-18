<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<%-- SPAR Product Container START --%>
<div class="row products-container">	
	<!--products-container-->
	<c:forEach items="${searchPageData.results}" var="product"
		varStatus="status">
		<c:if test="${not empty product.variantOptions}">
			<c:set var="variantOptions" value="${product.variantOptions}" />
		</c:if>
		<c:choose>
			<c:when test="${empty variantOptions}">
				<product:productListerGridItem product="${product}"
					index="${status.index}" />
			</c:when>
			<c:otherwise>
				<product:sparVariantProductListerGridItem product="${product}"
					index="${status.index}" />
			</c:otherwise>
		</c:choose>
		<c:remove var="variantOptions" />
	</c:forEach>
	<div id="addToCartTitle" style="display: none">
		<div class="add-to-cart-header">
			<%-- <div class="headline">
				<span class="headline-text"><spring:theme
						code="basket.added.to.basket" /></span>
			</div> --%>
		</div>
	</div>
</div>
<div class="row mt10 ">
	<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}"
		supportShowAll="${isShowAllAllowed}"
		searchPageData="${searchPageData}"
		searchUrl="${searchPageData.currentQuery.url}"
		numberPagesShown="${numberPagesShown}" />
</div>