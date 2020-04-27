<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showCartitems" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showAddress" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<c:set var="hasShippedItems" value="${cartData.deliveryItemsQuantity > 0}" />
<c:set var="deliveryAddress" value="${cartData.deliveryAddress}"/>
<c:forEach items="${cartData.combiOfferAppliedProductPromotion}" var="promotion">									
	<c:set var="promotionDiscount" value="${promotion.totalDiscount}"></c:set>	
</c:forEach>

<%-- <c:if test="${not hasShippedItems}">
	<spring:theme code="checkout.pickup.no.delivery.required"/>
</c:if> --%>

<c:if test="${hasShippedItems and showAddress}">
<!-- 	<li class="section"> -->
		<c:choose>
			<c:when test="${showDeliveryAddress and not empty deliveryAddress}">
			<%-- 	<div class="title"><spring:theme code="checkout.pickup.items.to.be.shipped" text="Ship To:"/></div> --%>
				<!-- <div class="address"> -->
				<c:if test="${empty deliveryAddress.longAddress}">	
					<p><strong>
					${fn:escapeXml(deliveryAddress.title)}&nbsp;${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}
					</strong></p>
					<p>
					<c:if test="${ not empty deliveryAddress.line2 }">
						${fn:escapeXml(deliveryAddress.line2)},&nbsp;
					</c:if>
					${fn:escapeXml(deliveryAddress.buildingName)},&nbsp;
					<c:if test="${ not empty deliveryAddress.landmark }">
						${fn:escapeXml(deliveryAddress.landmark)},&nbsp;
					</c:if>
					${fn:escapeXml(deliveryAddress.area)},&nbsp;
					<c:if test="${ not empty deliveryAddress.line1 }">
						${fn:escapeXml(deliveryAddress.line1)},&nbsp;
					</c:if>
					<c:if test="${not empty deliveryAddress.town }">
						${fn:escapeXml(deliveryAddress.town)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.region.name }">
						${fn:escapeXml(deliveryAddress.region.name)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.postalCode }">
						${fn:escapeXml(deliveryAddress.postalCode)}
					</c:if>
					</p>
				<%-- 	<c:if test="${ not empty deliveryAddress.country.name }">
						${fn:escapeXml(deliveryAddress.country.name)}
					</c:if> --%>
					<p><i class="fa fa-phone-square" style="font-size:16px; padding-right:10px;"></i>
					<c:if test="${ not empty deliveryAddress.phone }">
						${fn:escapeXml(deliveryAddress.phone)}
					</c:if></p>
					<c:if test="${ not empty deliveryAddress.email }">
						<p><i class="fa fa-envelope" style="font-size:16px; padding-right:10px;"></i>${fn:escapeXml(deliveryAddress.email)}</p>
					</c:if>
				</c:if>		
				
				<c:if test="${not empty deliveryAddress.longAddress}">
				
				<p><strong>
					${fn:escapeXml(deliveryAddress.title)}&nbsp;${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}
					</strong></p>
					<p>
					<c:if test="${ not empty deliveryAddress.line2 }">
						${fn:escapeXml(deliveryAddress.line2)},&nbsp;
					</c:if>
					${fn:escapeXml(deliveryAddress.buildingName)},&nbsp;
					<c:if test="${ not empty deliveryAddress.landmark }">
						${fn:escapeXml(deliveryAddress.landmark)},&nbsp;
					</c:if>
					
					<c:if test="${ not empty deliveryAddress.line1 }">
						${fn:escapeXml(deliveryAddress.line1)},&nbsp;
					</c:if>
					
					<c:if test="${ not empty deliveryAddress.longAddress }">
						${fn:escapeXml(deliveryAddress.longAddress)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.postalCode  && deliveryAddress.postalCode ne 'NA'}">
						${fn:escapeXml(deliveryAddress.postalCode)}
					</c:if>
					
					<%-- <c:if test="${not empty deliveryAddress.town }">
						${fn:escapeXml(deliveryAddress.town)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.region.name }">
						${fn:escapeXml(deliveryAddress.region.name)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.postalCode }">
						${fn:escapeXml(deliveryAddress.postalCode)}
					</c:if> --%>
					</p>
				<%-- 	<c:if test="${ not empty deliveryAddress.country.name }">
						${fn:escapeXml(deliveryAddress.country.name)}
					</c:if> --%>
					<p><i class="fa fa-phone-square" style="font-size:16px; padding-right:10px;"></i>
					<c:if test="${ not empty deliveryAddress.phone }">
						${fn:escapeXml(deliveryAddress.phone)}
					</c:if></p>
					<c:if test="${ not empty deliveryAddress.email }">
						<p><i class="fa fa-envelope" style="font-size:16px; padding-right:10px;"></i>${fn:escapeXml(deliveryAddress.email)}</p>
					</c:if>
				
				</c:if>
				<!-- </div> -->
			</c:when>
			<c:otherwise>
				<div class="alternatetitle"><spring:theme code="checkout.pickup.items.to.be.delivered" /></div>
			</c:otherwise>
		</c:choose>
		
	<!-- </li> -->
</c:if>

<c:if test="${showCartitems}">
<c:forEach var="entryMap" items="${catToOrdrEntryMap}">
  <c:set var="categoryData" value="${entryMap.key}"></c:set>
  <tr class="cartCateHedng"><td colspan="6"> <h5><c:choose><c:when test="${not empty categoryData.name}">${categoryData.name}</c:when> <c:otherwise>${categoryData.code}</c:otherwise></c:choose></h5></td></tr>
     <c:forEach items="${entryMap.value}" var="entry">
     
	<tr class="cart-top-totals cart-item-details checkout-order-summary-list">
		<c:url value="${entry.product.url}" var="productUrl"/>
			<td headers="header2" class="thumb">
					<a href="${productUrl}">
						<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
					</a>				
			
					<a href="${productUrl}">${entry.product.name}</a>
					
					<!-- Change start by sumit -->
							
						<c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
							<ul class="potentialPromotions">
								<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
									<c:set var="displayed" value="false"/>
									<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
										<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
											<c:set var="displayed" value="true"/>
											<li>
												<ycommerce:testId code="cart_potentialPromotion_label">
													${promotion.description}
												</ycommerce:testId>
											</li>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</c:if>
								
						<c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
							<ul class="appliedPromotions">
								<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
									<c:set var="displayed" value="false"/>
									<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
										<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
											<c:set var="displayed" value="true"/>
											<li>
												<ycommerce:testId code="cart_appliedPromotion_label">
													<c:choose>
													<c:when test="${promotion.appliedProductPromotionCount == null  or promotion.appliedProductPromotionCount==1}">
															${promotion.description}
													</c:when>
													<c:otherwise>
														<spring:theme code="multiple.promotions.message"  arguments="${promotion.appliedProductPromotionCount},${promotion.description}"/>
													</c:otherwise>
													</c:choose>
												</ycommerce:testId>
											</li>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</c:if>
							
							<!-- Change end here -->
				<input type="hidden" id="categoryName" value="${categoryData.name}"/>
			</td>
			
			
			<!--  Below Variant drop down is in HTML but not in scope of S1. Therefore, this has been commented out.  leaving the placeholder for dynamic content - Rohan_C -->
			<%--  <td style="text-align:center;">
				<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<div style="width:70px; margin-top:8px;height: 28px; padding: 2px 3px;">${selectedOption.name}: ${selectedOption.value}</div>
							</c:forEach>
						</c:if>
					</c:forEach>
				<!--	
					<select class="form-control" style="width:70px; margin-top:8px;height: 28px; padding: 2px 3px;">
						<option>5 kg</option><option>10 kg</option>
					</select>
					-->
			</td>  --%>
			<!-- Above Variant drop down is in HTML but not in scope of S1. Therefore, this has been commented out.  leaving the placeholder for dynamic content -->
			
			 <td class="itemPrice">
			  <div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.unitPrice" text="SPAR PRICE"/></div>
			 <span class="table-text"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></span></td>
		<!-- Code hide by sumit -->
            <%--  <td class="itemSaving"><span class="table-text"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span></td> --%>
          <!-- Change end here -->
			
			<td class="quantity">
			 <div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.quantity" text="QUANTITY"/></div>
				<div class="row">
	               	<div class="col-xs-5 col-xs-offset-5 col-sm-9 col-sm-offset-3 col-lg-8 col-lg-offset-4" >
	                    <div class="input-group number-spinner">
                            <!--<span class="input-group-btn data-dwn">
	                                <button class="btn btn-default btn-info" data-dir="dwn"><span class="glyphicon glyphicon-minus"></span></button>
	                            </span> 
	                            <input class="form-control text-center" value="${entry.quantity}" min="-10" max="40" style="padding: 5px 0;height: 34px; width: 40px;" type="text">
                            	<span class="input-group-btn data-up">
	                                <button class="btn btn-default btn-info" data-dir="up"><span class="glyphicon glyphicon-plus"></span></button>
	                            </span> -->
                             	<input class="form-control text-center" value="${entry.quantity}" disabled="disabled" min="-10" max="40" style="padding: 5px 0;height: 34px; width: 40px;" type="text">
	                     </div>
	                 </div>
                </div>
       		</td>
         	<td class="total">
         	<div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.subtotal" text="SUBTOTAL"/></div>
         	<span class="table-text"><format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/></span></td>
         <!-- Code change start by sumit -->
         	<%--  <td class="itemSaving"><span class="table-text redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span></td> --%>
         						<c:choose>
									<c:when test="${cartData.savings.value =='0.0' && empty promotionDiscount.value}">									
									</c:when>
									<c:when test="${not empty promotionDiscount.value}">
										<td headers="header4" class="itemSaving">
										 <div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.cartSaving" text="SAVINGS"/></div>
											 <c:choose>
											 <c:when test="${entry.combiOfferApplied}">
											 	<span class="redColr"><spring:theme code="text.combi.offer" text="Combi Offer"/></span>
											 </c:when>
											 <c:otherwise>
											 	<span class="redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span>
											 </c:otherwise>
											 </c:choose>
										 </td>							
									</c:when>
									<c:when test="${cartData.savings.value =='0.0' && cartData.savings.value > 0}">
										<td headers="header4" class="itemSaving">
										 <div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.cartSaving" text="SAVINGS"/></div>
											 <c:choose>
											 <c:when test="${entry.combiOfferApplied}">
											 	<span class="redColr"><spring:theme code="text.combi.offer" text="Combi Offer"/></span>
											 </c:when>
											 <c:otherwise>
											 	<span class="redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span>
											 </c:otherwise>
											 </c:choose>
										 </td>							
									</c:when>
									<c:otherwise>
										<td headers="header4" class="itemSaving">
											 <div class="item-heading hidden-sm hidden-md hidden-lg"><spring:theme code="cart.page.cartSaving" text="SAVINGS"/></div>
												 <c:choose>
												 <c:when test="${entry.combiOfferApplied}">
												 	<span class="redColr"><spring:theme code="text.combi.offer" text="Combi Offer"/></span>
												 </c:when>
												 <c:otherwise>
												 	<span class="redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span>
												 </c:otherwise>
												 </c:choose>
											 </td>														
									</c:otherwise>
								</c:choose>	
         	 
         <!-- Change end here -->	
           <!--  <td style="text-align:center;" align="center"><span class="table-text"><a href="#"><i class="fa fa-times-circle"></i></a></span></td>   -->                 
			
			<!--  This has been commented out so that the below promotion related code can be referenced - Rohan_C -->
			<!-- 
				<div class="details">
					<div class="variants">
						<c:forEach items="${entry.product.baseOptions}" var="option">
							<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
								<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
									<div>${selectedOption.name}: ${selectedOption.value}</div>
								</c:forEach>
							</c:if>
						</c:forEach>
						
						<c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber) && showPotentialPromotions}">
							<ul>
								<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
									<c:set var="displayed" value="false"/>
									<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
										<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
											<c:set var="displayed" value="true"/>
											<span>${promotion.description}</span>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</c:if>
						
						<c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
							<ul>
								<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
									<c:set var="displayed" value="false"/>
									<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
										<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
											<c:set var="displayed" value="true"/>
											<span>${promotion.description}</span>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</c:if>
					</div>
				</div>
			 -->
			<!--  <div class="stock-status">Item In Stock</div> -->
</tr>

</c:forEach> 
</c:forEach>
</c:if>
