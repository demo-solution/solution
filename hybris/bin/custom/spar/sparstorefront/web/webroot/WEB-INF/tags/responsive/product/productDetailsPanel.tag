<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<c:if
	test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'ApparelSizeVariantProduct')}">
	<c:set var="variantStyles" value="${product.baseOptions[1].options}" />
	<c:set var="variantSizes" value="${product.baseOptions[0].options}" />
	<c:set var="currentStyleUrl"
		value="${product.baseOptions[1].selected.url}" />
</c:if>
<c:url value="${currentStyleUrl}" var="currentStyledProductUrl" />
<%-- Determine if product is other variant --%>
<c:if test="${empty variantStyles}">
	<c:if test="${not empty product.variantOptions}">
		<c:set var="variantOptions" value="${product.variantOptions}" />
	</c:if>
	<c:if test="${not empty product.baseOptions[0].options}">
		<c:set var="variantOptions" value="${product.baseOptions[0].options}" />
	</c:if>
</c:if>
<div id="productdetaillist" class="row">
	<div class="product-menu col-md-3 ">
		<cms:pageSlot position="LeftNavBreadcrumb" var="comp" element="div">
			<cms:component component="${comp}" />
		</cms:pageSlot>
	</div>
	<div class="product-list col-md-9 nopadding">
		<c:if test="${fn:length(breadcrumbs) > 0}">
			<div class="h5" style="margin-left: 15px;">
				<div class="breadcrumb-section">
					<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
				</div>
			</div>
		</c:if>

		<%-- <div class="product-details">
	<ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
		<div class="name">${product.name} <span class="sku">ID ${product.code}</span></div>
	</ycommerce:testId>
	<product:productReviewSummary product="${product}" showLinks="true"/>

</div> --%>
		<div class="row">
			<div class="container-fluid produtBorder">
				<div class="row mb10">
					<div class="col-md-6 nopadding">
						<product:productImagePanel galleryImages="${galleryImages}"
							product="${product}" />							
						<span id="variantPromoLogo_${index}" class="promoImg">
							<div id="logoImg_${index}">
								<c:choose>
									<c:when test="${product.offerType eq 'bestDeal' }">
										<img  src="${commonResourcePath}/images/best-deal-pdp.jpg">
									</c:when>
									<c:when test="${product.offerType eq 'combiOffer' }">
										<img  src="${commonResourcePath}/images/NewCombiOffer.jpg">
									</c:when>
									<c:when test="${product.offerType eq 'regularOffer' }">
										<img  src="${commonResourcePath}/images/NewRegularOffer.jpg">
									</c:when>
								</c:choose>
							</div>
						</span>
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
							
					<div class="col-md-6 nopadding pdpInfoBx">
						<h4>${product.name}</h4>
						<!-- Change start by sumit -->
						<%-- <c:choose>
							<c:when test="${product.offerType eq 'bestDeal' }">
								<c:if test="${not empty product.promoMessage}">
									<!-- Best Deal LOGO -->
									<!-- <p class="pdpOfferrs txtOverflowElipsis"  data="130"> -->
									<p class="pdpOfferrs" data="130">
										<spring:theme code="promotion.best.deal.text" />${product.promoMessage}</span>
									</p>
								</c:if>
							</c:when>
							<c:when test="${product.offerType eq 'combiOffer' }">
								<c:if test="${not empty product.promoMessage}">
									<!-- Combi Offer Logo -->
									<p class="pdpOfferrs"  data="130">
										<spring:theme code="promotion.combi.offer.text" />${product.promoMessage}</span>
									</p>
								</c:if>
							</c:when>
							<c:when test="${product.offerType eq 'regularOffer' }">
								<c:if test="${not empty product.promoMessage}">
									<!-- Regular Offer Logo -->
									<p class="pdpOfferrs"  data="130">
										<spring:theme code="promotion.regular.offer.text" />${product.promoMessage}</span>
									</p>
								</c:if>
							</c:when>
						</c:choose> --%>

						<!-- Change end here -->
						<div class="priceTextsBG">
							<c:if test="${not empty product.brand}">
								<div class="row">
									<div class="col-md-4 col-xs-6 text-right"
										style="padding-top: 10px;">Brand</div>
									<div class="col-md-8 col-xs-6 text-left brand"
										style="padding-top: 10px;">${product.brand}</div>
								</div>
							</c:if>
							<c:if test="${not empty variantOptions}">
								<div class="row">
									<div class="col-md-4 col-xs-6 text-right"
										style="padding-top: 5px;">Select your pack</div>
									<div class="col-md-8 col-xs-6 text-left">
										<div class="form-group">
											<cms:pageSlot position="VariantSelector" var="component">
												<cms:component component="${component}" />
											</cms:pageSlot>
										</div>
									</div>
								</div>
							</c:if>
							<div class="row">
								<div class="col-md-4 col-xs-6 text-right"
									style="padding-top: 5px;">Quantity</div>
								<div class="col-md-8 col-xs-6 text-left">
									<%-- <c:if test="${empty showAddToCart ? true : showAddToCart}"> --%>
									<div class="input-group input-group-widthcart js-qty-selector">
										<span class="input-group-btn">
											<button type="button"
												class="btn btn-number btn-sm inputPad js-qty-selector-minus"
												data-type="minus" data-field="quant[2]"
												<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>>
												<span class="glyphicon glyphicon-minus glyphiSize"> </span>
											</button>
										</span> <span class="inputMargin ">
										<input type="hidden" name="maxOrderQuantity" class="productMaxOrderQuantity" value="${product.maxOrderQuantity}" />
										<input type="hidden" name="maxProductQuantity" class="maxProductQuantity" value="${product.stock.stockLevel}" />
										<input type="text" class="form-control js-qty-selector-input" maxlength="3"
											size="1" value='1' data-max="${product.stock.stockLevel}"
											data-min="1" name="pdpAddtoCartInput" id="pdpAddtoCartInput"
											<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>></span>
										<span class="input-group-btn">
											<button type="button"
												class="btn btn-number btn-sm inputPad js-qty-selector-plus"
												data-type="plus" data-field="quant[2]"
												<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>>
												<span class="glyphicon glyphicon-plus"></span>
											</button>
										</span>
									</div>
									<%-- </c:if> --%>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-xs-6 text-right nopadding">
									<ul>
										<c:choose>
											<c:when
												test="${null == product.promoMessage || empty product.promoMessage }">
												<c:choose>
													<c:when test="${product.isProductShownMP}">
														<li>MP</li>
														<li>SPAR Price</li>
														<li>Save</li>
													</c:when>
													<c:otherwise>
														<li>MRP</li>
														<li>SPAR Price</li>
														<li>Save</li>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${product.isProductShownMP}">
														<li>MP</li>
													</c:when>
													<c:otherwise>
														<li>MRP</li>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</ul>
								</div>
								<div class="col-md-8 col-xs-6 text-left">
									<product:productPricePanel product="${product}" />
									<product:productCombosOrMultiPackSKUs product="${product}"/>
								</div>
							</div>
							<div class="row nopadding">
								<div class="col-md-4 col-xs-12 text-right"></div>
								<div class="col-md-8 col-xs-12 text-left bottmShadow">
									<cms:pageSlot position="AddToCart" var="component">
										<cms:component component="${component}" />
									</cms:pageSlot>
								</div>
							</div>

						</div>


						<%-- <ul class="priceTexts pull-right">
	            <c:if test="${not empty product.brand}"><li> Brand </li></c:if>
	            <c:if test="${not empty variantOptions}"><li> Select your pack </li></c:if>
	            <li> Quantity </li>
	            <li> MRP </li>
	            <li> SPAR Price </li>
	            <li> Save </li></ul> --%>
					</div>
					<%-- <div class="col-xs-6 col-md-8 col-xs-6 text-left">
       	<ul class="priceTextRight priceTextsBG"> 
               <li>${product.brand}</li>
               <li> <div class="form-group form-group-width">
                        <cms:pageSlot position="VariantSelector" var="component">
									<cms:component component="${component}" />
								</cms:pageSlot>
                   </div> 
               </li>
               <li>
             	   <c:if test="${empty showAddToCart ? true : showAddToCart}">
	                    <div class="input-group input-group-widthcart js-qty-selector">
	                       <span class="input-group-btn">
	                             <button type="button" class="btn btn-number btn-sm inputPad js-qty-selector-minus"  data-type="minus" data-field="quant[2]"> <span class="glyphicon glyphicon-minus glyphiSize"> </span>
	                             </button>
	                        </span>
	                        <span class="inputMargin " ><input type="text" class="form-control js-qty-selector-input" maxlength="3" size="1" value='1' data-max="${product.stock.stockLevel}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput"></span>
	                        <span class="input-group-btn">
	                             <button type="button" class="btn btn-number btn-sm inputPad js-qty-selector-plus" data-type="plus" data-field="quant[2]"> <span class="glyphicon glyphicon-plus"></span>
	                           </button>
	                         </span>
	 					  	</div>
	 					</c:if>
              </li>
              <product:productPricePanel product="${product}" />
               <li class="btnAddCart"> <cms:pageSlot position="AddToCart" var="component">
					<cms:component component="${component}" /></cms:pageSlot> </li>					
           </ul>
    </div> --%>
					<%-- <product:productPromotionSection product="${product}"/> --%>

					<%-- 	<ycommerce:testId
						code="productDetails_productNamePrice_label_${product.code}">
						<product:productPricePanel product="${product}" />
					</ycommerce:testId>

					<div class="description">${product.summary}</div> --%>

					<%-- <div class="col-lg-6">

				<cms:pageSlot position="VariantSelector" var="component">
					<cms:component component="${component}" />
				</cms:pageSlot>

				<cms:pageSlot position="AddToCart" var="component">
					<cms:component component="${component}" />
				</cms:pageSlot>

			</div> --%>
				</div>
				<product:productPageTabs />
				
			</div>
		</div>

	</div>
</div>

