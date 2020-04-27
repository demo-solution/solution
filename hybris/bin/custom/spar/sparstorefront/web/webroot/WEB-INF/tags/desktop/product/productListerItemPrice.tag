<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<ycommerce:testId code="searchPage_price_label_${product.code}">

		<!-- if product is multidimensional with different prices, show range, else, show unique price -->
		<c:choose>
			<c:when test="${product.multidimensional and (product.priceRange.minPrice.value ne product.priceRange.maxPrice.value)}">
				<format:price priceData="${product.priceRange.minPrice}"/> - <format:price priceData="${product.priceRange.maxPrice}"/>
			</c:when>
			<c:otherwise>
<%-- 				<format:price priceData="${product.price}"/> --%>
				<div class="priceContainer">
					<spring:theme code="product.spar.MRP"/>&nbsp;
					<format:fromPrice priceData="${product.price}"/>
					<!-- This change is done to show Unit MRP {Rohan_c} -->
					<c:if test="${product.price.formattedValue != product.unitMRP.formattedValue}">
					<br>
						<small><del>
							<format:fromPrice priceData="${product.unitMRP}"/>&nbsp;MRP
						</del>
						</small>
					</c:if>
					
				</div>

				<br>
			</c:otherwise>
		</c:choose>

</ycommerce:testId>

<c:if test="${product.volumePricesFlag}">
	<br />
	<div class="volumePricesFlag">
		<spring:theme code="text.volumePrices" var="volumePricesText"/>
		<theme:image code="img.volumePrices" alt="${volumePricesText}" title="${volumePricesText}" />
	</div>
</c:if>
