<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true"
	type="de.hybris.platform.commercefacades.order.data.CartData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="storepickup"
	tagdir="/WEB-INF/tags/responsive/storepickup"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add" />


<input id="cartSavingValue" type='hidden' value="${cartData.savings.value}"/>

<c:forEach items="${cartData.combiOfferAppliedProductPromotion}" var="promotion">									
	<c:set var="promotionDiscount" value="${promotion.totalDiscount}"></c:set>	
</c:forEach>
<div class="container-fluid tableBorder nopadding cartItemsBx">
	<div class="row ">
		<!--Row----->

		<div class="col-xs-12 col-md-12 tableWidthPad">
			<!--Table of Cart items---->
			<table class="table table-hover table-striped ">
				<thead class="tableHead">
					<tr class="tableHeadText">
						<th id="header2" class="thumb"><spring:theme
								code="cart.page.productName" text="PRODUCT NAME" /></th>
						<th id="header3" class="itemPrice leftPadUnit"><spring:theme
								code="cart.page.unitPrice" text="UNIT PRICE" /></th>
						<th id="header5" class="quantity"><spring:theme
								code="cart.page.quantity" text="QUANTITY" /></th>
						<th id="header6" class="total"><spring:theme
								code="cart.page.subtotal" text="SUBTOTAL" /></th>													
						<c:choose>
							<c:when test="${cartData.savings.value == '0.0' && empty promotionDiscount.value}">
							
							</c:when>	
							<c:when test="${not empty promotion.totalDiscount.value}">
								<th id="header4" class="itemSaving yellowColr">
									<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
								</th>
							</c:when>							
							
							<c:otherwise>
								<th id="header4" class="itemSaving yellowColr">
									<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
								</th>
							</c:otherwise>							
						</c:choose>			
						
						<!-- Change end here -->

					</tr>
				</thead>
				<tbody>
					<!-- <tr>
                    <td colspan="6" class="tableHeadingText"> Grocery & Staples    </td>
                  </tr> -->

					<%-- <c:choose>
                 <c:when test="${numberShowing > 0 }"> --%>

					<c:forEach var="entryMap" items="${catToOrdrEntryMap}">
						<c:set var="categoryData" value="${entryMap.key}"></c:set>
						<tr class="cartCateHedng">
							<td colspan="6">
								<h5>
									<c:choose>
										<c:when test="${not empty categoryData.name}">${categoryData.name}</c:when>
										<c:otherwise>${categoryData.code}</c:otherwise>
									</c:choose>
								</h5>
							</td>
						</tr>
						<c:forEach var="entry" items="${entryMap.value}">
							<c:url value="${entry.product.url}" var="productUrl" />

							<tr class="cart-item-details">


								<%-- <td class="productWidthCart" >
                     <c:url value="${entry.product.url}" var="entryProductUrl"/>
                     <a href="${entryProductUrl}">
											<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
										</a>
										<a class="name" href="${entryProductUrl}">${entry.product.name}</a>
										</td> --%>
								<td headers="header2" class="thumb"><a href="${productUrl}"
									class="img-60"><product:productPrimaryImage
											product="${entry.product}" format="thumbnail" /></a> <!-- </td>
										
										<td headers="header2" class="details"> --> <ycommerce:testId
										code="cart_product_name">
										<a class="color-black" href="${productUrl}">${entry.product.name}</a>
									</ycommerce:testId> <c:set var="entryStock"
										value="${entry.product.stock.stockLevelStatus.code}" /> <c:forEach
										items="${entry.product.baseOptions}" var="option">
										<c:if
											test="${not empty option.selected and option.selected.url eq entry.product.url}">
											<c:forEach items="${option.selected.variantOptionQualifiers}"
												var="selectedOption">
												<%-- <div>
										<strong>${selectedOption.name}:</strong>
										<span>${selectedOption.value}</span>
									</div> --%>
												<c:set var="entryStock"
													value="${option.selected.stock.stockLevelStatus.code}" />
												<div class="clear"></div>
											</c:forEach>
										</c:if>
									</c:forEach> <c:if
										test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber) && entry.quantity < entry.product.maxOrderQuantity}">
										<ul class="potentialPromotions">
											<c:forEach items="${cartData.potentialProductPromotions}"
												var="promotion">
												<c:set var="displayed" value="false" />
												<c:forEach items="${promotion.consumedEntries}"
													var="consumedEntry">
													<c:if
														test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
														<c:set var="displayed" value="true" />
														<li><ycommerce:testId
																code="cart_potentialPromotion_label">
													${promotion.description}
												</ycommerce:testId></li>
													</c:if>
												</c:forEach>
											</c:forEach>
										</ul>
									</c:if>
									
									 <c:if
										test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
										<ul class="appliedPromotions">
											<c:forEach items="${cartData.appliedProductPromotions}"
												var="promotion">
												<c:set var="displayed" value="false" />
												<c:forEach items="${promotion.consumedEntries}"
													var="consumedEntry">
													<c:if
														test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
														<c:set var="displayed" value="true" />
														<li><ycommerce:testId
																code="cart_appliedPromotion_label">
																<c:choose>
																	<c:when
																		test="${promotion.appliedProductPromotionCount == null  or promotion.appliedProductPromotionCount==1}">
													${promotion.description}
													</c:when>
																	<c:otherwise>
																		<spring:theme code="multiple.promotions.message"
																			arguments="${promotion.appliedProductPromotionCount},${promotion.description}" />
																	</c:otherwise>
																</c:choose>
															</ycommerce:testId></li>
													</c:if>
												</c:forEach>
											</c:forEach>
										</ul>
									</c:if></td>

								<td headers="header3" class="itemPrice">
									<div class="item-heading hidden-sm hidden-md hidden-lg">
										<spring:theme code="cart.page.unitPrice" text="SPAR PRICE" />
									</div> <format:price priceData="${entry.basePrice}"
										displayFreeForZero="true" />
								</td>

								<td headers="header5" class="quantity">
									<div class="qty">
										<div class="item-heading hidden-sm hidden-md hidden-lg">
											<spring:theme code="cart.page.quantity" text="QUANTITY" />
										</div>
										<div class="qty-selector js-qty-selector">
											<div class="input-group input-group-widthcart">
												<c:url value="/cart/update" var="cartUpdateFormAction" />
												<form:form id="updateCartForm${entry.entryNumber}"
													class="updateForm" action="${cartUpdateFormAction}"
													method="post"
													modelAttribute="updateQuantityForm${entry.entryNumber}">
													<div class="input-group input-group-widthcart">
														<input type="hidden" name="entryNumber"
															value="${entry.entryNumber}" /> <input type="hidden"
															name="productCode" value="${entry.product.code}" /> <input
															type="hidden" name="initialQuantity"
															value="${entry.quantity}" />
															<input type="hidden" name="maxOrderQuantity" class="productMaxOrderQuantity" value="${entry.product.maxOrderQuantity}" />
														<ycommerce:testId code="cart_product_quantity">
						                                    <span class="input-group-btn inputMargin">
														         <button id="call" class="btn-number btn-sm inputPad js-qty-selector-minus js-add-to-cart-for-pickup-popup"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
													          </span>
													          <span class="inputMargin">
								                                    <form:input cssClass="form-control width-50 update-entry-quantity-input js-qty-selector-input input-number " disabled="${not entry.updateable}" type="text" size="1" id="quantity_${entry.entryNumber}" path="quantity" />
								                                </span>
								                                <span class="input-group-btn inputMargin">
														            <button class="btn-number btn-sm inputPad js-qty-selector-plus js-add-to-cart-for-pickup-popup"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
													         </span>
						                                </ycommerce:testId>
													</div>
													<td headers="header6" class="total">
														<div class="item-heading hidden-sm hidden-md hidden-lg">
															<spring:theme code="cart.page.subtotal" text="SUBTOTAL" />
														</div>
														<div style="display:inline">
														<ycommerce:testId code="cart_totalProductPrice_label">
															<format:price priceData="${entry.totalPrice}"
																displayFreeForZero="true" />
														</ycommerce:testId>
														</div>
														<div class="remove-item pull-right">
															<c:if test="${entry.updateable}">
															<ycommerce:testId code="cart_product_removeProduct">
																<a href="javascript:void(0)" class="remove-item remove-entry-button" id="removeEntry_${entry.entryNumber}"> </a>
															</ycommerce:testId>
														</c:if>
														</div>
													</td>
												</form:form>
											</div>
										</div>
									</div>
								</td>

								<%-- <td class="remove-item">
					<c:if test="${entry.updateable}" >
                <ycommerce:testId code="cart_product_removeProduct">
		            <a href="#" class="remove-item remove-entry-button" id="removeEntry_${entry.entryNumber}">
		            </a>
                </ycommerce:testId>
            </c:if>
					</td> --%>

								
								<!-- Change start by sumit -->								
								<c:choose>
									<c:when test="${cartData.savings.value =='0.0' && empty promotionDiscount.value}">									
									</c:when>
									<c:when test="${not empty promotionDiscount.value}">
										<td headers="header4" class="itemSaving">
											<div class="item-heading hidden-sm hidden-md hidden-lg">
												<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
											</div> 		
																			
											<c:choose>
												<c:when test="${entry.combiOfferApplied}">
													<span class="redColr"><spring:theme
															code="text.combi.offer" text="Combi Offer" /></span>
												</c:when>
												<c:otherwise>
													<span class="redColr">
													<format:price priceData="${entry.savings}" displayFreeForZero="false" /></span>
												</c:otherwise>
											</c:choose>
										 </td>											
									</c:when>
									<c:when test="${cartData.savings.value =='0.0' && cartData.savings.value > 0}">
										<td headers="header4" class="itemSaving">
												<div class="item-heading hidden-sm hidden-md hidden-lg">
													<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
												</div> 		
																				
												<c:choose>
													<c:when test="${entry.combiOfferApplied}">
														<span class="redColr"><spring:theme
																code="text.combi.offer" text="Combi Offer" /></span>
													</c:when>
													<c:otherwise>
														<span class="redColr">
														<format:price priceData="${entry.savings}" displayFreeForZero="false" /></span>
													</c:otherwise>
												</c:choose>
										 </td>									
									</c:when>
									<c:otherwise>
										<td headers="header4" class="itemSaving">
											<div class="item-heading hidden-sm hidden-md hidden-lg">
												<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
											</div> 		
																			
											<c:choose>
												<c:when test="${entry.combiOfferApplied}">
													<span class="redColr"><spring:theme
															code="text.combi.offer" text="Combi Offer" /></span>
												</c:when>
												<c:otherwise>
													<span class="redColr">
													<format:price priceData="${entry.savings}" displayFreeForZero="false" /></span>
												</c:otherwise>
											</c:choose>
									 </td>
									</c:otherwise>
								</c:choose>		
								<!-- Change end here -->

							</tr>
						</c:forEach>
					</c:forEach>
					<%--  <c:forEach items="${cartData.entries}" var="entry" varStatus="loop">
                             </c:forEach> --%>
				</tbody>
			</table>



		</div>
		<!--Table of Cart items----->




		<div class="col-sm-12 col-md-12  col-lg-12 col-xs-12 nopadding">
			<div class="row lineTopBottom ptb10" style="margin-left: 5px;">	
				<c:choose>
					<c:when test="${cartData.savings.value == '0.0'}">		
						<div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 nopadding">
							<!--left Block----->
							<div class="offerBlock">
								<!-- <p> 20% OFF: Buy 40 kg rice get additional 20% off </p>
		                        <p> 40% OFF: Buy 50 kg rice get additional 20% off </p> -->
							</div>
						</div>				
						<div class="col-sm-9 col-md-9 col-lg-9 col-xs-12 priceBlock">
					<!--Right Block----->
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding grand-total text-right">						
							<div class="price-total-wrapper">
								<div class="price-total-label">
									<spring:theme code="cart.page.cartTotal" text="Total" />
								</div>
								<div class="price-total-value">
									<ycommerce:testId code="cart_totalPrice_label">
										<format:price priceData="${cartData.subTotal}" />
									</ycommerce:testId>
								</div>							
							</div>							
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12 nopadding text-right">
							<c:forEach items="${cartData.combiOfferAppliedProductPromotion}"
								var="promotion">
								<%-- <div  class="desc-selector" data-toggle="tooltip" data-placement="top" title="${promotion.description}">  --%>
								<div class="cartSaved saved-rs text-right clearfix">								
									<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="text.combi.offer.promotion"	text="Combi Offer:" />
										</div>
										<div class="price-total-value">										
											<format:price priceData="${promotion.totalDiscount}" />											
										</div>							
									</div>	
								</div>
							</c:forEach>
						</div>
						<c:if test="${cartData.appliedEmployeeDiscountPromotionTotal.value > 0}">
							<div class="cartSaved saved-rs text-right clearfix">
								<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="basket.page.employee.discount.promotions" text="Employee Discount:" />
										</div>
										<div class="price-total-value">
											<format:price priceData="${cartData.appliedEmployeeDiscountPromotionTotal}" />
										</div>							
									</div>								
							</div>
						</c:if>
						<c:if test="${cartData.appliedOrderPromotionTotal.value > 0}">
							<div class="cartSaved saved-rs text-right clearfix">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="basket.page.totals.order.promotions" text="Bill Buster Discount:" />
									</div>
									<div class="price-total-value">
										<format:price priceData="${cartData.appliedOrderPromotionTotal}" />
									</div>							
								</div>
							</div>
						</c:if>
						
						<!-- Code change start for voucher story -->
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
							<%-- <c:if test="${not empty cartData.appliedVoucherTotal }">
			                    <c:forEach items="${cartData.appliedVoucherTotal}" var="voucherValue">                       				 
									<div class="cartSaved saved-rs text-right clearfix"><b><spring:theme code="basket.page.cart.order.vouchers" text="Voucher Discount:"/>&nbsp;-<format:price priceData="${voucherValue.appliedValue}"/></b></div>								
								</c:forEach>
							</c:if> --%>
							<c:if test="${not empty cartData.voucherValue && cartData.voucherValue.value ne '0.0'}">
								 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">				 
									<div class="cartSaved saved-rs text-right clearfix">
										<div class="price-total-wrapper">
											<div class="price-total-label">
												<spring:theme code="basket.page.totals.order.vouchers" text="Voucher Discount:"/>
											</div>
											<div class="price-total-value">
												<format:price priceData="${cartData.voucherValue}"/>
											</div>							
										</div>
									</div>								
								</div>
							</c:if>
						</div> 
					</div>
					<!-- Changes end here -->
						<c:if test="${not empty cartData.deliveryCost}">
							<div class="cartDelivery text-right">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="checkout.multi.summary.page.deliveryCharges"	text="Delivery Charges" />
									</div>
									<div class="price-total-value">
										<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE" />
									</div>							
								</div>
							</div>
						</c:if>
					</div>
					
					<div class="row">
						<div
							class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding text-right">
							<div class="totalPrice">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="cart.page.amountPay" text="Amount Payable" />
									</div>
									<div class="price-total-value">
										<ycommerce:testId
									code="cart_totalPrice_label">
									<c:choose>
										<c:when test="${showTax}">
											<format:price priceData="${cartData.totalPriceWithTax}" />
										</c:when>
										<c:otherwise>
											<format:price priceData="${cartData.totalPrice}" />
										</c:otherwise>
									</c:choose>
								</ycommerce:testId>
									</div>							
								</div>	
							</div>
						</div>
					</div>
						<!--  <div class="row">
							<div
								class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding fudCupn text-right mt10">
								<c:if
									test="${not empty cartData.totalFoodCouponAmount and cartData.totalFoodCouponAmount ne 0}">
									<spring:theme code="cart.page.foodCouponMsg"
										arguments="${cartData.totalFoodCouponAmount}" />
								</c:if>
							</div>
						</div>-->
				</div>				
					</c:when>
					<c:otherwise>
					
					<div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 nopadding">
					<!--left Block----->
					<div class="offerBlock">
						<!-- <p> 20% OFF: Buy 40 kg rice get additional 20% off </p>
                        <p> 40% OFF: Buy 50 kg rice get additional 20% off </p> -->
					</div>
					</div>
					<div class="col-sm-7 col-md-7 col-lg-7 col-xs-12 priceBlock nopadding">
					<!--Right Block----->
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding grand-total text-right">						
							<div class="price-total-wrapper">
								<div class="price-total-label">
									<spring:theme code="cart.page.cartTotal" text="Total" />
								</div>
								<div class="price-total-value">
									<ycommerce:testId code="cart_totalPrice_label">
										<format:price priceData="${cartData.subTotal}" />
									</ycommerce:testId>
								</div>							
							</div>							
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12 nopadding text-right">
							<c:forEach items="${cartData.combiOfferAppliedProductPromotion}"
								var="promotion">
								<%-- <div  class="desc-selector" data-toggle="tooltip" data-placement="top" title="${promotion.description}">  --%>
								<div class="cartSaved saved-rs text-right clearfix">								
									<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="text.combi.offer.promotion"	text="Combi Offer:" />
										</div>
										<div class="price-total-value">
											<format:price priceData="${promotion.totalDiscount}" />
										</div>							
									</div>	
								</div>
							</c:forEach>
						</div>
						<c:if test="${cartData.appliedEmployeeDiscountPromotionTotal.value > 0}">
							<div class="cartSaved saved-rs text-right clearfix">
								<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="basket.page.employee.discount.promotions" text="Employee Discount:" />
										</div>
										<div class="price-total-value">
											<format:price priceData="${cartData.appliedEmployeeDiscountPromotionTotal}" />
										</div>							
									</div>								
							</div>
						</c:if>
						<c:if test="${cartData.appliedOrderPromotionTotal.value > 0}">
							<div class="cartSaved saved-rs text-right clearfix">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="basket.page.totals.order.promotions" text="Bill Buster Discount:" />
									</div>
									<div class="price-total-value">
										<format:price priceData="${cartData.appliedOrderPromotionTotal}" />
									</div>							
								</div>
							</div>
						</c:if>
						
						<!-- Code change start for voucher story -->
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
							<%-- <c:if test="${not empty cartData.appliedVoucherTotal }">
			                    <c:forEach items="${cartData.appliedVoucherTotal}" var="voucherValue">                       				 
									<div class="cartSaved saved-rs text-right clearfix"><b><spring:theme code="basket.page.cart.order.vouchers" text="Voucher Discount:"/>&nbsp;-<format:price priceData="${voucherValue.appliedValue}"/></b></div>								
								</c:forEach>
							</c:if> --%>
							<c:if test="${not empty cartData.voucherValue && cartData.voucherValue.value ne '0.0'}">
								 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">				 
									<div class="cartSaved saved-rs text-right clearfix">
										<div class="price-total-wrapper">
											<div class="price-total-label">
												<spring:theme code="basket.page.totals.order.vouchers" text="Voucher Discount:"/>
											</div>
											<div class="price-total-value">
												<format:price priceData="${cartData.voucherValue}"/>
											</div>							
										</div>
									</div>								
								</div>
							</c:if>
						</div> 
					</div>
					<!-- Changes end here -->
						<c:if test="${not empty cartData.deliveryCost}">
							<div class="cartDelivery text-right">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="checkout.multi.summary.page.deliveryCharges"	text="Delivery Charges" />
									</div>
									<div class="price-total-value">
										<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE" />
									</div>							
								</div>
							</div>
						</c:if>
					</div>
					
					<div class="row">
						<div
							class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding text-right">
							<div class="totalPrice">
								<div class="price-total-wrapper">
									<div class="price-total-label">
										<spring:theme code="cart.page.amountPay" text="Amount Payable" />
									</div>
									<div class="price-total-value">
										<ycommerce:testId
									code="cart_totalPrice_label">
									<c:choose>
										<c:when test="${showTax}">
											<format:price priceData="${cartData.totalPriceWithTax}" />
										</c:when>
										<c:otherwise>
											<format:price priceData="${cartData.totalPrice}" />
										</c:otherwise>
									</c:choose>
								</ycommerce:testId>
									</div>							
								</div>	
							</div>
						</div>
					</div>
					<!--  <div class="row">
						<div
							class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding fudCupn text-right mt10">
							<c:if
								test="${not empty cartData.totalFoodCouponAmount and cartData.totalFoodCouponAmount ne 0}">
								<spring:theme code="cart.page.foodCouponMsg"
									arguments="${cartData.totalFoodCouponAmount}" />
							</c:if>
						</div>
					</div>-->
				</div>
					<!--Right Block----->
				<div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 nopadding savings">
				<!-- Delivery Charges Rs 0  -->
				<c:if test="${not empty cartData.savings}">
					<div class="cartSaved saved-rs">
						<b><spring:theme code="cart.page.savesubTotal"
								text="PRODUCT NAME" /><br />
						<format:price priceData="${cartData.savings}" /></b>
					</div>
				</c:if>
					</div>					
					</c:otherwise>				
				</c:choose>
		</div>
			</div>
			
		<div class="row">
			<div class="col-sm-6 col-xs-12">
				<c:if test="${not empty cartData.entries}">
					<cms:pageSlot position="CenterLeftContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</c:if>
			</div>
			<div class="col-sm-6 col-xs-12 ">
				<div class="btnCartPad pull-right">
					<button type="button" class="btn-block checkoutButton btn btnRed"
						data-checkout-url="${checkoutUrl}">
						<spring:theme code="checkout.checkout" />
					</button>
					<!-- <a href="2_Register-page.html"  type="button" class="btn btn-Green"> PROCEED TO CHECKOUT  </a> -->
				</div>
				<div class="btnCartPad pull-right">
					<button type="button"
						class=" btn-block continueShoppingButton btn btn-Green"
						data-continue-shopping-url="${request.contextPath}">
						<spring:theme code="cart.page.continueShoping"
							text="CONTINUE SHOPPING" />
					</button>
					<!-- <button type="button" class="btn btnRed">  CONTINUE SHOPPING </button> -->
				</div>
			</div>

		</div>
		<div class="row">
			<div class="col-sm-6 col-xs-12">
				<c:if
					test="${cartData.productDiscounts != null  && not empty cartData.productDiscounts}">
					<span class="saved-rs"> <spring:theme
							code="text.cart.discount.message" />
					</span>
				</c:if>
			</div>
		</div>

	</div>
	<!--Row End----->
</div>
<!--container----->

<%--  <div class="row">
                    <div class="col-sm-6 col-md-3 col-md-push-6">
                        <button class="btn btn-default btn-block continueShoppingButton" data-continue-shopping-url="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></button>
                    </div>
                    <div class="col-sm-6 col-md-3 col-md-push-6">
                        <ycommerce:testId code="checkoutButton">
                            <button class="btn btn-primary btn-block checkoutButton" data-checkout-url="${checkoutUrl}"><spring:theme code="checkout.checkout"/></button>
                        </ycommerce:testId>
                    </div>
                </div> --%>


<!------------End Of View Cart --------------------->


<!--------------------------------------------------------------Cash Options --------------------------------------------->


<div class="row benrMidle">
	<div class="bnrMBx clr1">
		<span class="iconsBx"><img width="34" align="middle"
			height="24" src="${commonResourcePath}/images/OntimeDeli-34x24.png"></span>
		<span class="iconTxt">SAME DAY DELIVERY AVAILABLE</span>
	</div>

	<div class="bnrMBx bnrMBx1 clr2">
		<span class="iconsBx"><img width="34" align="middle"
			height="24" src="${commonResourcePath}/images/Card-34x24.png"></span>
		<span class="iconTxt">WE ACCEPT CARD AND CASH ON DELIVERY</span>
	</div>

	<div class="bnrMBx clr3">
		<span class="iconsBx"><img width="34" height="24"
			src="${commonResourcePath}/images/Free-shipping 34x24.png"></span> <span
			class="iconTxt"><spring:theme code="freeshipping.text" /></span>
	</div>

	<div class="col-md-12 cards">
		<ul class="cards-icons">
			<%-- <li><img width="64" height="22"
				src="${commonResourcePath}/images/ticket-icon.png"></li>
			<li><img width="64" height="22"
				src="${commonResourcePath}/images/sodexo-icon.png"></li> --%>
			<li><img width="50" height="22"
				src="${commonResourcePath}/images/visa-icon.png"></li>
			<li><img width="38" height="22"
				src="${commonResourcePath}/images/mastercard-icon.png"></li>
			<li><img width="36" height="22"
				src="${commonResourcePath}/images/maestro_card.png"></li>
			<li><span class="iconsBx"><img width="24" height="24"
					src="${commonResourcePath}/images/Hassle_Free_Return-24x24.png"></span>
				<span class="iconTxt1">HASSLE FREE RETURNS AND NO QUESTIONS
					ASKED REFUND </span></li>
		</ul>
	</div>
</div>



<!-- footer -------------------------------------------------------------------------------------------------------------------------->



<!-- footer1 End -->
<!-- </div>container End
footer2  -->

<!-- <div class="container-fluid footerBlock">  
          <div class="row">
                    <div class="container">
                     
                        
                     </div>
                   
          </div>
</div>
 -->

<!-- footer2  -->
<!-- footer3  -->

<!-- footer3  -->
