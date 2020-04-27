<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="index" required="false" type="Integer" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>


<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>
<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>
<c:set var="appendOnProductName" value="..."/>
				
<div class="col-md-12 separator">
	<div class="product-image-wrapper">
		<div class="single-products"> 
		<div class="row">
		<span class="over">
		 <c:choose>
		 	<c:when test="${product.offerType eq 'bestDeal' }">
		 		<img src="${commonResourcePath}/images/BestDeal.jpg">
		 	</c:when>
		 	<c:when test="${product.offerType eq 'combiOffer' }">
		 		<img src="${commonResourcePath}/images/NewCombiOffer.jpg">
		 	</c:when>
		 	<c:when test="${product.offerType eq 'regularOffer' }">
		 		<img src="${commonResourcePath}/images/NewRegularOffer.jpg">
		 	</c:when>
		</c:choose>  
		</span>
		<a  id="variantImageAnchor_${index}" href="${productUrl}" title="${product.description}">
			<product:productPrimaryImage product="${product}" index="${index}" format="plpProduct"/>
		</a>
		</div>
		<div class="row details">

			<ycommerce:testId code="product_productName">
			<%-- <a class="name" href="${productUrl}">${product.name}</a> --%>
			<div  class=" product-name-list h3">${product.brand}</div>
			<div id="variantDescription_${index}" class="desc-selector" data-toggle="tooltip" data-placement="top" title="${product.name}">
				<h5> 
					<c:choose>
						<c:when test="${fn:length(product.name)>25}">
							${fn:substring(product.name,0,25)}... 
						</c:when>
						<c:otherwise>
							${product.name}
						</c:otherwise>
					</c:choose>
				</h5>
			</div>
			</ycommerce:testId>
		
			<div class="form-group var-selector">
				<c:choose>
			 		<c:when test="${null == product.price || empty product.price }">
			 			${product.featuredProductGrammage}&nbsp;-&nbsp;<spring:theme code="product.plp.price.unavailable" text="Price Unavailable"/>
			 		</c:when>
				 	<c:otherwise>
						${product.featuredProductGrammage}&nbsp;-&nbsp;<format:price priceData="${product.price}"/>
					</c:otherwise>
				</c:choose>
			</div>
			
			<div id="variantPrice_${index}" class="priceBx row">
			<ycommerce:testId code="product_productPrice">
				<!-- 
					These are the business rules
					
					CSP < MRP, strike the MRP
					CSP = MRP, display both CSP & MRP but donot strike
					CSP > MRP, display MRP in both places
				 -->
				 <c:choose>
				 	<c:when test="${product.isProductShownMP}">
						<c:set var="priceLabel" value="MP" scope="request"/>
					</c:when>
					<c:otherwise>
				 		<c:set var="priceLabel" value="MRP" scope="request"/>
					</c:otherwise>
				</c:choose>
				 <c:choose>
				 	<c:when test="${null == product.price || empty product.price }">
				 		<span class="prevnolinethrough-price">${priceLabel} &nbsp; <spring:theme code="product.price.unavailable" text="Unavailable"/></span>
				 	</c:when>
				 	<c:otherwise>
					 	<c:choose>
							<c:when test="${product.price.value < product.unitMRP.value}">
								<span class="prev-price">${priceLabel} &nbsp; <format:price priceData="${product.unitMRP}"/></span>
							</c:when>
							<c:otherwise>
								<!--  There will not be any CSP > MRP. In case MRP==CSP, MRP is not striked-off-->
								<span class="prevnolinethrough-price">${priceLabel} &nbsp; <format:price priceData="${product.unitMRP}"/></span>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
					<span class="orignal-price"><format:price priceData="${product.price}"/></span>
					<c:choose>
				 	<c:when test="${null == product.promoMessage || empty product.promoMessage }">
						<c:choose>
					 	<c:when test="${null == product.price || empty product.price }">
					 		<span class="standard-price">Save <spring:theme code="product.price.unavailable" text="Unavailable"/></span>
					 	</c:when>
					 	<c:otherwise>
							<span class="standard-price">Save <format:price priceData="${product.savings}"/></span>
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
							<span class="standard-price">${product.promoMessage}</span>
							<%-- <span class="var-message" > <spring:theme code="plp.promotion.message" text="Please add item to the cart, to avail offers."/> </span> --%>
					</c:otherwise>
					</c:choose>
			</ycommerce:testId>
			</div>
			
			<%-- Place Holder for Promotion Discount START --%>
		<input id="productDiscountStatusChk" type="hidden" value="${product.promotionDiscount}"/>	
		 <c:choose>
			<c:when test="${product.promotionDiscount == null}">	
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${(product.offerType eq 'bestDeal') or (product.offerType eq 'regularOffer')}">											
						<div id="productDiscount">					
							<span>
								<fmt:formatNumber value="${product.promotionDiscount}" maxFractionDigits="0"/>				
							</span>%&nbsp;OFF				
						</div>
					</c:when>											
				</c:choose>
			</c:otherwise>
		</c:choose>
		
		<%-- Place Holder for Bogo Check START --%>
		<input id="bogoMsgStatusChk" type="hidden" value="${product.productOnBogo}"/>
		<c:choose>
			<c:when test="${product.productOnBogo == true}">	
				<div id="productBogoMsg">					
					<c:choose>
						<c:when test="${fn:length(product.promoMessage)>50}">									
							<span>${fn:substring(product.promoMessage,0,50)}${appendOnProductName}</span>
						</c:when>
						<c:otherwise>
							<span>${product.promoMessage}</span>
						</c:otherwise>
					</c:choose>
				</div>			
			</c:when>			
		</c:choose>
			
		</div>

	<%-- Place Holder for Quantity Component START --%>
	<div class="row">
		<div class="input-group  js-qty-selector" style="width:50%; padding-top:10px; float:left;">
		
            <span class="input-group-btn data-dwn spinner">
                <button class=" btn btn-default btn-info plp-js-qty-selector-minus" data-dir="dwn" <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' || null == product.price || empty product.price }"> disabled="disabled" aria-disabled="true"</c:if>><span class="glyphicon glyphicon-minus"></span></button>
            </span>
            
            <input id="variantStockLevel_${index}" type="text" maxlength="3" class="form-control text-center plp-js-qty-selector-input" size="1" value='1' style="padding:2px; height:22px;" data-max="${product.stock.stockLevel}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput"  <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' || null == product.price || empty product.price }"> disabled="disabled" aria-disabled="true"</c:if>  /> 

            <%-- Input Hidden Parameter for Product Code Start--%>
			<input id="variantPartNumber_${index}"  type="hidden" name="partNumber" class="plp-js-partnumber" value="${product.code}"/>
			<input id="product_${index}"  type="hidden" name="productIndex" class="productIndex" value="${index}"/>
			<%-- Input Hidden Parameter for Product Code End--%>
            <span class="input-group-btn data-up product-btn spinner">
                <button class="btn btn-default btn-info plp-js-qty-selector-plus" data-dir="up" <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' || null == product.price || empty product.price }"> disabled="disabled" aria-disabled="true"</c:if>><span class="glyphicon glyphicon-plus"></span></button>
            </span>
       	</div>
                     
	<%-- Place Holder for Quantity Component END --%>

		<c:set var="addToCartText" value="${addToCartText}" scope="request"/>
		<c:set var="addToCartUrl" value="${addToCartUrl}" scope="request"/>
		<c:set var="isGrid" value="true" scope="request"/>
		
		<div class="cart pull-right spinner " style="padding-top:10px;">
			<div class="actions-container-for-${component.uid} plp-addtocart-component">
				<c:url value="/cart/add" var="addToCartUrl"/>
				<form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
					<input type="hidden" maxlength="3" size="1" id="qty_${index}" name="qty" class="qty plp-js-qty-selector-input" value="1">
					<ycommerce:testId code="addToCartButton">
						<input id="variantProductCode_${index}" type="hidden" name="productCodePost" value="${product.code}"/>
						<input id="variantProductName_${index}" type="hidden" name="productNamePost" value="${product.name}"/>
						<input id="variantProductPrice_${index}" type="hidden" name="productPostPrice" value="${product.price.value}"/>
						
						<c:choose> 
							<c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' || null == product.price || empty product.price}">
								<button id="variantAdd_${index}" type="button" class="btn btn-primary btn-block out-of-stock"  disabled="disabled" aria-disabled="true">
								<spring:theme code="product.variants.out.of.stock" text="OUT OF STOCK"/></button>
							</c:when>
							<c:otherwise>
								<button id="variantAdd_${index}" type="submit" class="btn btn-primary btn-block  add-btn">
								<i class="fa fa-shopping-cart"></i>${addToCartText}</button>
							</c:otherwise>
						</c:choose>
						
						
					</ycommerce:testId>
				</form:form>
			</div>
		</div>
	</div>
		
		<%-- Place Holder for Lead Time messaging START --%>
	 	<!-- <div class="standrd-delivery"> Standard Delivery. Tomorrow Evening</div> -->
	 	<%-- Place Holder for Lead Time messaging END --%>
</div>
</div>
</div>
