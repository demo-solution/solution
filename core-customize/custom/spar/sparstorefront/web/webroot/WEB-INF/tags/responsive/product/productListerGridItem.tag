<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="index" required="false" type="Integer" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>
<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>

<div class="col-md-3 col-sm-4 col-xs-6 separator">
<div class="product-image-wrapper">
<div class="single-products"> 
<div class="wishlist">
	<i class="fa fa-heart-o"></i>
</div>
		<a  class="img-150" href="${productUrl}" title="${product.name}">
			<product:productPrimaryImage product="${product}" format="store"/>
		</a>
		<div class="details">

			<ycommerce:testId code="product_productName">
			<%-- <a class="name" href="${productUrl}">${product.name}</a> --%>
			<div class="product-name-list h3">${product.brand}</div>
			<div data-toggle="tooltip" data-placement="top" title="${product.ERPDescription}"><h5> ${product.ERPDescription}</h5></div>
			</ycommerce:testId>
		
			<div class="form-group var-selector">
				${product.ERPShortDescription}
			</div>
			<c:if test="${not empty product.potentialPromotions}">
				<div class="promo">
					<c:forEach items="${product.potentialPromotions}" var="promotion">
						${promotion.description}
					</c:forEach>
				</div>
			</c:if>

			<ycommerce:testId code="product_productPrice">
			<div class="price">
				<span class="prev-price">MRP &nbsp; <format:price priceData="${product.unitMRP}"/></span>
				<p class="orignal-price"><format:price priceData="${product.price}"/></p>
				<span class="standard-price">Save <format:price priceData="${product.savings}"/></span>
			</div><!--price-->
			</ycommerce:testId>

		</div>

	<%-- Place Holder for Quantity Component START --%>
		<div class="input-group js-qty-selector" style="width:50%; padding-top:10px; float:left;">
            <span class="input-group-btn data-dwn spinner">
                <button class=" btn btn-default btn-info plp-js-qty-selector-minus" data-dir="dwn"><span class="glyphicon glyphicon-minus"></span></button>
            </span>
            
            <input type="text" maxlength="3" class="form-control text-center plp-js-qty-selector-input" size="1" value='1' style="padding:2px; height:22px;" data-max="${product.stock.stockLevel}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput"   /> 

            <%-- Input Hidden Parameter for Product Code Start--%>
			<input id="product_${index}"  type="hidden" name="productIndex" class="productIndex" value="${index}"/>
			<%-- Input Hidden Parameter for Product Code End--%>
            <span class="input-group-btn data-up product-btn spinner">
                <button class="btn btn-default btn-info plp-js-qty-selector-plus" data-dir="up"><span class="glyphicon glyphicon-plus"></span></button>
            </span>
       	</div>
                     
	<%-- Place Holder for Quantity Component END --%>

		<c:set var="product" value="${product}" scope="request"/>
		<c:set var="productIndex" value="${index}" scope="request"/>
		<c:set var="addToCartText" value="${addToCartText}" scope="request"/>
		<c:set var="addToCartUrl" value="${addToCartUrl}" scope="request"/>
		<c:set var="isGrid" value="true" scope="request"/>
		
		<div class="cart pull-right spinner " style="padding-top:10px;">
			<div class="actions-container-for-${component.uid} plp-addtocart-component">
				<action:actions element="div" parentComponent="${component}"/>
			</div>
		</div>
		
		<%-- Place Holder for Lead Time messaging START --%>
	 	<!-- <div class="standrd-delivery"> Standard Delivery. Tomorrow Evening</div> -->
	 	<%-- Place Holder for Lead Time messaging END --%>
</div>
</div>
</div>
