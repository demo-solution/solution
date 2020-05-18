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

<c:if test="${not empty product.variantOptions}">
	<c:set var="variantOptions" value="${product.variantOptions}" />
</c:if>

<c:if test="${not empty product.variantOptions}">
 <c:set var="count"  value="0"/>
	<c:forEach items="${product.variantOptions}" var="baseVariant" varStatus="status">
 	 <c:set var="found"  value="new"/>
	 <c:if test="${count eq '0'}">
	<c:set var="baseVariantOption" value="${product.variantOptions[0]}" /> 
	</c:if>
	<c:if test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' && baseVariant.stock.stockLevelStatus.code eq 'inStock' && found ne 'stock' && found ne 'offer'}">
	<c:set var="found"  value="stock"/>
	<c:set var="baseVariantOption" value="${baseVariant}" /> 
	</c:if>	
	<c:if test="${baseVariant.stock.stockLevelStatus.code eq 'inStock' && found ne 'offer' &&(baseVariant.offerType eq 'combiOffer' || baseVariant.offerType eq 'regularOffer' || baseVariant.offerType eq 'bestDeal') }">
	<c:set var="found"  value="offer"/>
	<c:set var="baseVariantOption" value="${baseVariant}" /> 
	</c:if>
	<c:set var="count"  value="${count+1 }"/>
	
	</c:forEach>
</c:if>


<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${baseVariantOption.url}" var="productUrl"/>
<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>
<c:set var="appendOnProductName" value="..."/>
<div class="col-md-3 col-sm-4 col-xs-6 separator">
	<div class="product-image-wrapper">
		<div class="single-products"> 
		<div class="wishlist">
			<i class="fa fa-heart-o"></i>
		</div>
		<div class="row product-img">		
		<span id="variantPromoLogo_${index}" class="over">
			<div id="logoImg_${index}">			
				 <c:choose>
				 	<c:when test="${baseVariantOption.offerType eq 'bestDeal' }">
				 		<img class="best-deal" src="${commonResourcePath}/images/BestDeal.jpg">
				 	</c:when>
				 	<c:when test="${baseVariantOption.offerType eq 'combiOffer' }">
				 		<img class="combi-offer" src="${commonResourcePath}/images/NewCombiOffer.jpg">
				 	</c:when>
				 	<c:when test="${baseVariantOption.offerType eq 'regularOffer' }">
				 		<img class="regular-offer" src="${commonResourcePath}/images/NewRegularOffer.jpg">
				 	</c:when>
				</c:choose> 
			</div> 
		</span>
		<a  id="variantImageAnchor_${index}" href="${productUrl}" title="${baseVariantOption.description}">
			<product:productPrimaryImage product="${product}" index="${index}" format="plpProduct"/>
		</a>
		</div>
		<div class="row details">

			<ycommerce:testId code="product_productName">
			<%-- <a class="name" href="${productUrl}">${product.name}</a> --%>
			<div  class=" product-name-list h3">${baseVariantOption.brand}</div>
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
		
			
			<%-- SPAR Variants. This needs to be dynamic.  START --%>
				
				<c:if test="${not empty variantOptions}">
					<div class="form-group var-selector">
						<c:choose>
						<c:when test="${fn:length(variantOptions)==1}">
							<c:set var="optionsString">${baseVariantOption.variantOptionQualifiers[0].value}</c:set>
								<c:choose>
							 			<c:when test="${null == baseVariantOption.offerType || empty baseVariantOption.offerType }">
							 				<c:choose>
										 		<c:when test="${null == baseVariantOption.priceData || empty baseVariantOption.priceData }">
										 			${optionsString}&nbsp;-&nbsp;<spring:theme code="product.price.unavailable" text="Price Unavailable"/>
										 		</c:when>
											 	<c:otherwise>											 													 		
													<div class="item-description"><span class="qty">${optionsString}</span>&nbsp;-&nbsp;<format:price priceData="${baseVariantOption.priceData}"/></div>
												</c:otherwise>
											</c:choose>
							 			</c:when>
							 			<c:otherwise>
							 				<div class="item-description"><span class="qty">${optionsString}</span>&nbsp;-&nbsp;<format:price priceData="${baseVariantOption.unitMRP}"/></div>
										</c:otherwise>
								</c:choose>
						</c:when>
						<c:otherwise>
				
						  <select id="selectedpart_${index}" class="form-control">
							<%-- <option selected="selected" disabled="disabled"><spring:theme code="product.variants.select.variant"/></option> --%>
								
								<c:set var="optionsString" value="" />
									<c:forEach items="${baseVariantOption.variantOptionQualifiers}" var="variantOptionQualifier">
										<c:set var="optionsString">${optionsString}${variantOptionQualifier.value}</c:set>
									</c:forEach>
								
									<c:url value="${baseVariantOption.url}" var="variantOptionUrl"/>
									<option value="${baseVariantOption.code}" >
									<c:choose>
							 			<c:when test="${null == baseVariantOption.offerType || empty baseVariantOption.offerType }">
											<c:choose>
											 	<c:when test="${null == baseVariantOption.priceData || empty baseVariantOption.priceData }">
											 		${optionsString}&nbsp;-&nbsp;<spring:theme code="product.price.unavailable" text="Price Unavailable"/>
											 	</c:when>
											 	<c:otherwise>
													${optionsString}&nbsp;-&nbsp;<format:price priceData="${baseVariantOption.priceData}"/>	
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											${optionsString}&nbsp;-&nbsp;<format:price priceData="${baseVariantOption.unitMRP}"/>
										</c:otherwise>
									</c:choose>									
									</option>									
								<c:forEach items="${variantOptions}" var="variantOption">
								<c:if test="${baseVariantOption.code ne variantOption.code }">
									<c:set var="optionsString" value="" />
									<c:forEach items="${variantOption.variantOptionQualifiers}" var="variantOptionQualifier">
										<c:set var="optionsString">${optionsString}${variantOptionQualifier.value}</c:set>
									</c:forEach>
								
									<c:url value="${variantOption.url}" var="variantOptionUrl"/>
									<option value="${variantOption.code}" >
									<c:choose>
							 			<c:when test="${null == variantOption.offerType || empty variantOption.offerType }">
											<c:choose>
											 	<c:when test="${null == variantOption.priceData || empty variantOption.priceData }">
											 		${optionsString}&nbsp;-&nbsp;<spring:theme code="product.price.unavailable" text="Price Unavailable"/>
											 	</c:when>
											 	<c:otherwise>
													${optionsString}&nbsp;-&nbsp;<format:price priceData="${variantOption.priceData}"/>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											${optionsString}&nbsp;-&nbsp;<format:price priceData="${variantOption.unitMRP}"/>
										</c:otherwise>
									</c:choose>
									</option>
									</c:if>
								</c:forEach>								
							</select>							
						</c:otherwise>
						</c:choose>
					</div>
	              </c:if>
				<%-- SPAR Variants. This needs to be dynamic.  End --%>
			
			<div id="variantPrice_${index}" class="priceBx row">
			<ycommerce:testId code="product_productPrice">
				<!-- 
					These are the business rules
					
					CSP < MRP, strike the MRP
					CSP = MRP, display both CSP & MRP but donot strike
					CSP > MRP, display MRP in both places
				 -->
				 
				<c:choose>
				 	<c:when test="${null == baseVariantOption.priceData || empty baseVariantOption.priceData }">
						<c:choose>
				 			<c:when test="${product.isProductShownMP}">
				 				<span class="prevnolinethrough-price">MP &nbsp; <spring:theme code="product.price.unavailable" text="Unavailable"/></span>
				 			</c:when>
				 			<c:otherwise>
				 				<span class="prevnolinethrough-price">MRP &nbsp; <spring:theme code="product.price.unavailable" text="Unavailable"/></span>
				 			</c:otherwise>
						</c:choose>
				 	</c:when>
				 	<c:otherwise>
					 	<c:choose>
							<c:when test="${baseVariantOption.priceData.value < baseVariantOption.unitMRP.value}">							
								<c:choose>
									<c:when test="${product.isProductShownMP}">
										<span class="prev-price">
										MP &nbsp; <format:price priceData="${baseVariantOption.unitMRP}"/>										
										</span>
									</c:when>
									<c:otherwise>
										<span class="prev-price">
											MRP &nbsp; <format:price priceData="${baseVariantOption.unitMRP}"/>											
										</span>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
							<!--  There will not be any CSP > MRP. In case MRP==CSP, MRP is not striked-off-->
								<c:choose>
									<c:when test="${product.isProductShownMP}">
										<span class="prevnolinethrough-price">MP &nbsp; <format:price priceData="${baseVariantOption.unitMRP}"/></span>
									</c:when>
									<c:otherwise>
										<span class="prevnolinethrough-price">MRP &nbsp; <format:price priceData="${baseVariantOption.unitMRP}"/></span>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
					</c:choose>
						<span class="orignal-price">					
							<format:price priceData="${baseVariantOption.priceData}"/>																		
						</span>					
					<c:choose>
				 	<c:when test="${null == baseVariantOption.promoMessage || empty baseVariantOption.promoMessage }">
					
						<c:choose>
						 	<c:when test="${null == baseVariantOption.priceData || empty baseVariantOption.priceData }">
						 	
						 		<span class="standard-price">Save <spring:theme code="product.price.unavailable " text="Unavailable"/></span>
						 	</c:when>
						 	<c:otherwise>
						 	
								<span class="standard-price">Save <format:price priceData="${baseVariantOption.savings}"/></span>
							</c:otherwise>
						</c:choose>
					</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${fn:length(baseVariantOption.promoMessage)>50}">									
									<span data-toggle="tooltip" data-placement="top" title="${baseVariantOption.promoMessage}" class="standard-price">${fn:substring(baseVariantOption.promoMessage,0,50)}${appendOnProductName}</span>
								</c:when>
								<c:otherwise>
									<span data-toggle="tooltip" data-placement="top" title="${baseVariantOption.promoMessage}" class="standard-price">${baseVariantOption.promoMessage}</span>
								</c:otherwise>
							</c:choose>
					
					
								
							<%-- <span data-toggle="tooltip" data-placement="top" title="${baseVariantOption.promoMessage}" class="standard-price">${baseVariantOption.promoMessage}</span> --%>
							<%-- <span class="var-message" > <spring:theme code="plp.promotion.message" text="Please add item to the cart, to avail offers."/> </span> --%>
					</c:otherwise>
					</c:choose>
					
			</ycommerce:testId>
			</div>	
			
		<%-- Place Holder for Promotion Discount START --%>
		<input id="productDiscountStatusChk" type="hidden" value="${baseVariantOption.promotionDiscount}"/>	
		 <c:choose>
			<c:when test="${baseVariantOption.promotionDiscount == 'null'}">	
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${(baseVariantOption.offerType eq 'bestDeal') or (baseVariantOption.offerType eq 'regularOffer')}">											
						<div id="productDiscount">					
							<span>
								<fmt:formatNumber value="${baseVariantOption.promotionDiscount}" maxFractionDigits="0"/>				
							</span>%&nbsp;OFF				
						</div>
					</c:when>											
				</c:choose>	
			</c:otherwise>
		</c:choose>
		
		<%-- Place Holder for Bogo Check START --%>
		<input id="bogoMsgStatusChk" type="hidden" value="${baseVariantOption.productOnBogo}"/>
		<c:choose>
			<c:when test="${baseVariantOption.productOnBogo == true}">	
				<div id="productBogoMsg">					
					<c:choose>
						<c:when test="${fn:length(baseVariantOption.promoMessage)>50}">									
							<span>${fn:substring(baseVariantOption.promoMessage,0,50)}${appendOnProductName}</span>
						</c:when>
						<c:otherwise>
							<span>${baseVariantOption.promoMessage}</span>
						</c:otherwise>
					</c:choose>
				</div>			
			</c:when>	
			<c:otherwise>
				<div id="productBogoMsg"></div>
			</c:otherwise>		
		</c:choose>
		
		
		
		</div>
		<product:productCombosOrMultiPackSKUs product="${product}"/>
	<%-- Place Holder for Quantity Component START --%>
	<div class="row quantity-updater">
		<div class="input-group  js-qty-selector">
            <span class="input-group-btn data-dwn spinner">
                <button class=" btn btn-default btn-info plp-js-qty-selector-minus" data-dir="dwn" 
                	<c:if test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' || null == baseVariantOption.priceData || empty baseVariantOption.priceData }"> disabled="disabled" aria-disabled="true"</c:if>>
                <span class="glyphicon glyphicon-minus"></span></button>
            </span>
            
            <input id="variantStockLevel_${index}" type="text" maxlength="3" class="form-control text-center plp-js-qty-selector-input" 
            <c:if test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' || null == baseVariantOption.priceData || empty baseVariantOption.priceData }"> disabled="disabled"</c:if> size="1" value='1' data-max="${baseVariantOption.stock.stockLevel}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput"   /> 

            <%-- Input Hidden Parameter for Product Code Start--%>
			<input id="variantPartNumber_${index}"  type="hidden" name="partNumber" class="plp-js-partnumber" value="${baseVariantOption.code}"/>
			<input id="product_${index}"  type="hidden" name="productIndex" class="productIndex" value="${index}"/>
			<%-- Input Hidden Parameter for Product Code End--%>
            <span class="input-group-btn data-up product-btn spinner">
                <button class="btn btn-default btn-info plp-js-qty-selector-plus" data-dir="up" 
                <c:if test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' || null == baseVariantOption.priceData || empty baseVariantOption.priceData}"> disabled="disabled" aria-disabled="true"</c:if>>
                <span class="glyphicon glyphicon-plus"></span></button>
            </span>
       	</div>
                     
	<%-- Place Holder for Quantity Component END --%>

		<c:set var="addToCartText" value="${addToCartText}" scope="request"/>
		<c:set var="addToCartUrl" value="${addToCartUrl}" scope="request"/>
		<c:set var="isGrid" value="true" scope="request"/>
		
		<div class="cart pull-right spinner">
			<div class="actions-container-for-${component.uid} plp-addtocart-component">
				<c:url value="/cart/add" var="addToCartUrl"/>
				<form:form id="addToCartForm${baseVariantOption.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
					<input type="hidden" maxlength="3" size="1" id="qty_${index}" name="qty" class="qty plp-js-qty-selector-input" value="1">
					<ycommerce:testId code="addToCartButton">
						<input id="variantProductCode_${index}" type="hidden" name="productCodePost" value="${baseVariantOption.code}"/>
						<input id="variantProductName_${index}" type="hidden" name="productNamePost" value="${baseVariantOption.name}"/>
						<input id="variantProductPrice_${index}" type="hidden" name="productPostPrice" value="${baseVariantOption.priceData.value}"/>
						
						<c:choose> 
							<c:when test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' || null == baseVariantOption.priceData || empty baseVariantOption.priceData}">
								<button id="variantAdd_${index}" type="button" class="btn btn-primary btn-block out-of-stock"  disabled="disabled" aria-disabled="true">
							<spring:theme code="product.variants.out.of.stock" text="OUT OF STOCK"/></button></c:when>
							<c:otherwise>
								<button id="variantAdd_${index}" type="submit" class="btn btn-primary btn-block  add-btn">
								<i class="fa fa-shopping-cart"></i>${addToCartText}</button></c:otherwise>
						</c:choose>
						
						<%-- <button id="variantAdd_${index}" type="submit" class="btn btn-primary btn-block  add-btn <c:if test="
							${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' }">out-of-stock</c:if>"
								<c:if test="${baseVariantOption.stock.stockLevelStatus.code eq 'outOfStock' || null == baseVariantOption.priceData || empty baseVariantOption.priceData }"> disabled="disabled" aria-disabled="true"</c:if>> <i class="fa fa-shopping-cart"></i>${addToCartText}</button>
								--%>
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

