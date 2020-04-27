<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="storepickup"
	tagdir="/WEB-INF/tags/responsive/storepickup"%>


<div class="you-may-like">
	
	<c:choose>
		<c:when
			test="${not empty product.productReferences and component.maximumNumberProducts > 0}">
			<h4 class="headline">${component.title}</h4>
			<div class="carousel-component">
				<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
					<c:forEach items="${product.productReferences}" var="productRef"
						varStatus="status">
						<div class="item">
							<c:set var="product" value="${productRef.target}" />
							<c:if test="${not empty product.variantOptions}">
								<c:set var="variantOptions" value="${product.variantOptions}" />
							</c:if>
							<c:choose>
								<c:when test="${empty variantOptions}">
									<product:productRefListerGridItem product="${product}"
										index="${status.index}" />
								</c:when>
								<c:otherwise>
									<product:sparVariantProductRefListerGridItem
										product="${product}" index="${status.index}" />
								</c:otherwise>
							</c:choose>
						</div>
						<c:remove var="variantOptions" />
					</c:forEach>
				</div>
			</div>
		</c:when>
	</c:choose>
</div>
