<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<!-- <div class="price"> -->
<c:choose>
	<c:when test="${empty product.volumePrices}">
		<li class=""> <!-- <i class="fa fa-rupee"></i> -->
			<c:choose>
			<c:when test="${product.unitMRP == null || empty product.unitMRP }">
			<spring:theme code="product.price.unavailable" text="Unavailable"/>
			</c:when>
			<c:otherwise>
				<c:if test="${product.price.formattedValue != product.unitMRP.formattedValue}">
				<del class="mrp"><b><format:fromPrice priceData="${product.unitMRP}"/></b></del>	
				</c:if>
				<c:if test="${product.price.formattedValue == product.unitMRP.formattedValue}">
					<format:fromPrice priceData="${product.unitMRP}"/>	
				</c:if>			
			</c:otherwise>
			</c:choose>
		</li>
	
	<c:choose>	
		<c:when test="${null == product.promoMessage || empty product.promoMessage }">
			<c:choose>
			<c:when test="${product.price == null || empty product.price }">
				<li>  <span class="priceFont">
				<spring:theme code="product.price.unavailable" text="Unavailable"/>
				</span></li> 
			</c:when>
			<c:otherwise>
				<li> <!-- <i class="fa fa-rupee"></i> --> <span class="priceMain">
				<format:fromPrice priceData="${product.price}" />	
				</span></li> 
			</c:otherwise>
			</c:choose>
			
			<c:choose>
			<c:when test="${product.savings == null || empty product.savings }">
				<li>  <span class="priceFont">
				<spring:theme code="product.price.unavailable" text="Unavailable"/>
				</span></li> 
			</c:when>
			<c:otherwise>
				<li> <p class="saveRed"><!-- <i class="fa fa-rupee faPad"> </i> --><span class="saveText">
				<format:fromPrice priceData="${product.savings}"/>
				</span></p>
				</li>
			</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<span data-placement="top" title="${product.promoMessage}" class="standard-price">${product.promoMessage}</span>
			<%-- <span class="var-message" >&nbsp;&nbsp;&nbsp;<spring:theme code="plp.promotion.message" text="Please add item to the cart, to avail offers."/> </span> --%>
	   </c:otherwise>
	</c:choose>
		
		
	</c:when>
	<c:otherwise>
		<table class="volume-prices" cellpadding="0" cellspacing="0" border="0">
			<thead>
					<th class="volume-prices-quantity"><spring:theme code="product.volumePrices.column.qa"/></th>
					<th class="volume-price-amount"><spring:theme code="product.volumePrices.column.price"/></th>
			</thead>
			<tbody>
				<c:forEach var="volPrice" items="${product.volumePrices}">
					<tr>
						<td class="volume-price-quantity">
							<c:choose>
								<c:when test="${empty volPrice.maxQuantity}">
									${volPrice.minQuantity}+
								</c:when>
								<c:otherwise>
									${volPrice.minQuantity}-${volPrice.maxQuantity}
								</c:otherwise>
							</c:choose>
						</td>
						<td class="volume-price-amount">${volPrice.formattedValue}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>
<!-- </div> -->