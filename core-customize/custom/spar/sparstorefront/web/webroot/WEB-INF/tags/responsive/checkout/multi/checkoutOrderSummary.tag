<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:url value="/checkout/multi/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>
<spring:url value="/checkout/multi/payment-method/add" var="paymentFormUrl"/>
<input id="cartSavingValue" type='hidden' value="${cartData.savings.value}"/>
<c:forEach items="${cartData.combiOfferAppliedProductPromotion}" var="promotion">									
	<c:set var="promotionDiscount" value="${promotion.totalDiscount}"></c:set>	
</c:forEach>

<!-- <div class="col-sm-6 col-md-5 col-lg-4"> -->
<div class="tab-pane" role="tabpanel" id="complete">
	<div class="check-top-bar1 col-md-12 col-xs-12">
		<h4><spring:theme code="checkout.multi.summary.reviewYourOrder" text="REVIEW YOUR ORDER" /></h4>
	</div>
	<div class="col-sm-12 col-xs-12 cartItemsBx p5 mt10" id="address2">
			<div class="col-xs-12 col-md-12 tableWidthPad"> <!--Table of Cart items---->
              <table class="table table-hover tableWidthPad table-striped ">
                <thead class="tableHead">
                  <tr class="tableHeadText">
					<th id="header2" class="thumb"><spring:theme code="checkout.multi.summary.productName" text="PRODUCT NAME" /></th>
					<th id="header3" class="itemPrice leftPadUnit"><spring:theme code="checkout.multi.summary.unitPrice" text="UNIT PRICE" /></th>
			<!-- Code hide by sumit -->
					<%-- <th id="header4" style="width:13%"><spring:theme code="checkout.multi.summary.savings" text="SAVINGS" /></th> --%>
					<th id="header5" class="quantity text-center"><spring:theme code="checkout.multi.summary.quantity" text="QUANTITY" /></th>
					<th id="header6" class="total"><spring:theme code="checkout.multi.summary.total" text="TOTAL" /></th>					
					<c:choose>
							<c:when test="${cartData.savings.value == '0.0' && empty promotionDiscount.value}">							
							</c:when>
							<c:when test="${not empty promotion.totalDiscount.value}">
								<th id="header4" class="itemSaving yellowColr"><spring:theme code="checkout.multi.summary.savings" text="SAVINGS" /></th>
							</c:when>
							<c:otherwise>
								<th id="header4" class="itemSaving yellowColr"><spring:theme code="checkout.multi.summary.savings" text="SAVINGS" /></th>
							</c:otherwise>							
						</c:choose>	
					
					
					
					
 	<%--    <th scope="col" style="text-align:center; background:#484848;" align="center" width="10%"><spring:theme code="checkout.multi.summary.cancel" text="CANCEL" /></th> --%>
                 </tr>
			</thead>
            <tbody>       
               <multi-checkout:deliveryCartItems cartData="${cartData}" showDeliveryAddress="${showDeliveryAddress}" showCartitems="true" showAddress="false"/>

						<!--  this has been commented out as Pick Up functionality is not in scope of S1- Rohan_C -->
						<%-- <c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
							<multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" index="${status.index}" showHead="true" />
						</c:forEach> 
						<!--  Payment is moved after review page-->	
						<multi-checkout:paymentInfo cartData="${cartData}" paymentInfo="${cartData.paymentInfo}" showPaymentInfo="${showPaymentInfo}" />
						--%>
                   	</tbody>	
				</table>
			</div>
			
			
			<%-- <c:if test="${not empty cartData.totalFoodCouponAmount and cartData.totalFoodCouponAmount ne 0}"> <spring:theme code="cart.page.foodCouponMsg"  arguments="${cartData.totalFoodCouponAmount}"/> </c:if>
			<div class="col-sm-12 final-rev">
				<!--  this div is for CMS Content- START -->
	           <div class="col-sm-3 nopadding">
	           	<div class="offer-block">
	               	<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
						<cms:component component="${feature}"/>
					</cms:pageSlot>
	               </div>
	           </div>
	           <!--  this div is for CMS Content- END  -->  
	            <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" />
	         </div> --%>
	         
	         <div class="col-md-12 col-xs-12 nopadding"> 
          		  <div class="row lineTopBottom ptb10">
          		  	<c:choose>
						<c:when test="${cartData.savings.value == '0.0'}">
							 <div class="col-md-3 col-xs-12 nopadding"> <!--left Block----->
			                    <div class="offerBlock">
			                        <!-- <p> 20% OFF: Buy 40 kg rice get additional 20% off </p>
			                        <p> 40% OFF: Buy 50 kg rice get additional 20% off </p> -->
			                    </div>
	                   		 </div>
	                   		 <div class="col-sm-9 col-md-9 col-lg-9 col-xs-12 priceBlock"><!--Right Block----->                            
                            <div class="row"> 
                                <div class="col-xs-12 text-right col-sm-12 col-md-12 col-lg-12 grand-total nopadding">                                
                                	<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="cart.page.cartTotal" text="Total"/>
										</div>
										<div class="price-total-value">
											<ycommerce:testId code="cart_totalPrice_label">
												<format:price priceData="${cartData.subTotal}" />
											</ycommerce:testId>
										</div>							
								  </div>	                                
								</div>
								<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
                           			<c:forEach items="${cartData.combiOfferAppliedProductPromotion}" var="promotion">
                          				<%-- <div  class="desc-selector" data-toggle="tooltip" data-placement="top" title="${promotion.description}"> --%> 
										<div class="cartSaved saved-rs text-right clearfix">
											<div class="price-total-wrapper">
												<div class="price-total-label">
													<spring:theme code="text.combi.offer.promotion" text="Combi Offer:"/>
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
									         	<spring:theme code="basket.page.totals.order.promotions" text="Bill Buster Discount:"/>
									         </div>
									         <div class="price-total-value">
									         	 <format:price priceData="${cartData.appliedOrderPromotionTotal}" />
									         </div>
								         </div>
							        </div>         
							     </c:if>
								     
								      <!-- Code change start for voucher story -->
	                           	<%-- <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
	                          		<c:forEach items="${cartData.appliedVoucherTotal}" var="voucherValue">                       				 
										<div class="cartSaved saved-rs text-right clearfix"><b><spring:theme code="basket.page.cart.order.vouchers" text="Voucher Discount :"/>&nbsp;-<format:price priceData="${voucherValue.appliedValue}"/></b></div>								
									</c:forEach>
								</div> --%>  
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
								<!-- Change end here -->
							     
                                		
								
                            </div>
                           <c:if test="${not empty cartData.deliveryCost}">
                                	<div class="cartDelivery text-right">
                               			<div class="price-total-wrapper">
												<div class="price-total-label">
													<spring:theme code="checkout.multi.summary.page.deliveryCharges" text="Delivery Charges"/>
												</div>
												<div class="price-total-value">
													<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE"/>
												</div>							
										  </div>
                                	</div>
								</c:if>		
                           
                           
                           
                             <div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 nopadding text-right"> 
                                 <div class="totalPrice">  
                                 	<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="cart.page.amountPay" text="Amount Payable"/>
										</div>
										<div class="price-total-value">
											 <ycommerce:testId code="cart_totalPrice_label">
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
								<!--  <div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 nopadding fudCupn text-right mt10"><c:if test="${not empty cartData.totalFoodCouponAmount and cartData.totalFoodCouponAmount ne 0}"> <spring:theme code="cart.page.foodCouponMsg"  arguments="${cartData.totalFoodCouponAmount}"/> </c:if></div>-->
                           </div>
						</c:when>
          		  
          		  		<c:otherwise>
          		  		<div class="col-md-3 col-xs-12 nopadding"> <!--left Block----->
			                    <div class="offerBlock">
			                        <!-- <p> 20% OFF: Buy 40 kg rice get additional 20% off </p>
			                        <p> 40% OFF: Buy 50 kg rice get additional 20% off </p> -->
			                    </div>
	                   		 </div>
	                   		 <div class="col-sm-7 col-md-7 col-lg-7 col-xs-12 priceBlock"><!--Right Block----->                            
                            <div class="row"> 
                                <div class="col-xs-12 text-right col-sm-12 col-md-12 col-lg-12 grand-total nopadding">                                
                                	<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="cart.page.cartTotal" text="Total"/>
										</div>
										<div class="price-total-value">
											<ycommerce:testId code="cart_totalPrice_label">
												<format:price priceData="${cartData.subTotal}" />
											</ycommerce:testId>
										</div>							
								  </div>	                                
								</div>
								<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
                           			<c:forEach items="${cartData.combiOfferAppliedProductPromotion}" var="promotion">
                          				<%-- <div  class="desc-selector" data-toggle="tooltip" data-placement="top" title="${promotion.description}"> --%> 
										<div class="cartSaved saved-rs text-right clearfix">
											<div class="price-total-wrapper">
												<div class="price-total-label">
													<spring:theme code="text.combi.offer.promotion" text="Combi Offer:"/>
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
									         	<spring:theme code="basket.page.totals.order.promotions" text="Bill Buster Discount:"/>
									         </div>
									         <div class="price-total-value">
									         	 <format:price priceData="${cartData.appliedOrderPromotionTotal}" />
									         </div>
								         </div>
							        </div>         
							     </c:if>
								     
								      <!-- Code change start for voucher story -->
	                           	<%-- <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">
	                          		<c:forEach items="${cartData.appliedVoucherTotal}" var="voucherValue">                       				 
										<div class="cartSaved saved-rs text-right clearfix"><b><spring:theme code="basket.page.cart.order.vouchers" text="Voucher Discount :"/>&nbsp;-<format:price priceData="${voucherValue.appliedValue}"/></b></div>								
									</c:forEach>
								</div> --%>  
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
								<!-- Change end here -->
							     
                                		
								
                            </div>
                           <c:if test="${not empty cartData.deliveryCost}">
                                	<div class="cartDelivery text-right">
                               			<div class="price-total-wrapper">
												<div class="price-total-label">
													<spring:theme code="checkout.multi.summary.page.deliveryCharges" text="Delivery Charges"/>
												</div>
												<div class="price-total-value">
													<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE"/>
												</div>							
										  </div>
                                	</div>
								</c:if>		
                           
                           
                           
                             <div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 nopadding text-right"> 
                                 <div class="totalPrice">  
                                 	<div class="price-total-wrapper">
										<div class="price-total-label">
											<spring:theme code="cart.page.amountPay" text="Amount Payable"/>
										</div>
										<div class="price-total-value">
											 <ycommerce:testId code="cart_totalPrice_label">
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
								<!--  <div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 nopadding fudCupn text-right mt10"><c:if test="${not empty cartData.totalFoodCouponAmount and cartData.totalFoodCouponAmount ne 0}"> <spring:theme code="cart.page.foodCouponMsg"  arguments="${cartData.totalFoodCouponAmount}"/> </c:if></div>-->
                           </div>
                           <div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 nopadding savings">
	                             <c:if test="${not empty cartData.savings}">
	                                <div class="cartSaved saved-rs"><b><spring:theme code="cart.page.savesubTotal" text="PRODUCT NAME"/><br/><format:price priceData="${cartData.savings}" /></b></div>
	                                </c:if>
                           </div> 
          		  		
          		  		</c:otherwise>
          		  		</c:choose>            
                    </div><!--Right Block----->                    
                    		                   	
            </div>
            </div>
	</div>
	<%-- <div class="hidden-xs">
		<div class="checkbox">
			<form:form id="summaryPostForm" name="summaryPostForm" action="${paymentFormUrl}" method="GET">
				<button id="payment" type="submit" class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
					<spring:theme code="checkout.multi.summary.continue" text="CONTINUE"/>
				</button>
			</form:form>
		</div>
	</div> --%>
	<div class="row">
		<div class="col-sm-6 col-xs-12">
		<c:if test="${cartData.productDiscounts != null  && not empty cartData.productDiscounts}">
			<span class="saved-rs">
				<spring:theme code="text.cart.discount.message"/>
			</span>
		</c:if>
		</div>
  	</div>