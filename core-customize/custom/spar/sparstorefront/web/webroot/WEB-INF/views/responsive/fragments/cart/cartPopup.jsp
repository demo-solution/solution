<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:theme code="text.addToCart" var="addToCartText" />
<spring:theme code="text.popupCartTitle" var="popupCartTitleText" />
<c:url value="/cart" var="cartUrl" />
<c:url value="/cart/checkout" var="checkoutUrl" />


<div class="container-fluid mini-cart-wrapper cartWrapper p10">

	<div class="row">
		<!--Row----->

		<div class="col-xs-12 col-md-12 tableWidthPad ">

			<!--Table of Cart items---->


			<c:choose>
				<c:when test="${numberShowing > 0 }">
					<div class="row cartItemslisting">
						<div class="cart-heading">
							<div class="product-name thumb">PRODUCT NAME</div>
							<div class="leftPadUnit product-price">
								<spring:theme code="text.account.orderHistory.itemUnitPrice"
									text="SPAR Price" />
							</div>
			<!-- Change start by sumit code hide -->
							<%-- <div class="product-saving">
								<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
							</div> --%>
			    <!-- Change end here -->
							<div class="product-quantity quantity">QUANTITY</div>
			<!-- Change start by sumit -->
							<div class="product-saving yellowColr itemSaving">
								<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
							</div>
			<!-- Change end here -->
						</div>
						<ul class="list-inline">
							<c:forEach items="${entries}" var="entry"
								end="${numberShowing - 1}">
								<li>
									<div class="product-name thumb productWidthCart">
										<span class="pull-left"> <c:url
												value="${entry.product.url}" var="entryProductUrl" /> <a
											class="img-thumb" href="${entryProductUrl}"> <product:productPrimaryImage
													product="${entry.product}" format="cartIcon" />
										</a> <a class="name" href="${entryProductUrl}"
											title="${entry.product.name}"> ${entry.product.name} </a>
										</span>
									</div>
									<div class="product-price itemPrice">
										<div class="item-heading hidden-md hidden-lg hidden-sm">
											<spring:theme code="text.account.orderHistory.itemUnitPrice"
												text="SPAR Price" />
										</div>
										<div class="price">
											<format:price priceData="${entry.basePrice}" />
										</div>
									</div>
					<!-- Change start by sumit code hide-->
									<%-- <div class="product-saving itemSaving">
										<div class="item-heading hidden-md hidden-lg hidden-sm">
											<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
										</div>
										<format:price priceData="${entry.savings}"
											displayFreeForZero="false" />
									</div> --%>
					<!-- Change end here -->
									<div class="product-quantity quantity">
										<div class="item-heading hidden-md hidden-lg hidden-sm">QUANTITY</div>
										${entry.quantity}
									</div>
						<!-- Change start by sumit -->
									<div class="product-saving itemSaving">
										<div class="item-heading hidden-md hidden-lg hidden-sm">
											<spring:theme code="cart.page.cartSaving" text="SAVINGS" />
										</div>
										<c:choose>
											 <c:when test="${entry.combiOfferApplied}">
											 	<span class="redColr"><spring:theme code="text.combi.offer" text="Combi Offer"/></span>
											 </c:when>
											 <c:otherwise>
											 	<span class="redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span>
											 </c:otherwise>
							 			</c:choose>
									</div>
						<!-- Change end here -->
								</li>
							</c:forEach>
						</ul>
					</div>
					<div class="container-fluid nopadding">
						<div class="row priceBox ">

							<div class="col-sm-12 col-xs-12 priceBlock nopadding">
								<!--Right Block----->



								<div class="row">
								 <div class="col-lg-9 col-md-9 col-sm-9 col-xs-12">
								 	<div class="row">
								 		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-right">
											<b>Total <format:price priceData="${cartData.subTotal}" /></b>
										</div>
										
											
										<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 txtCol-red text-right">
											<c:if test="${cartData.appliedEmployeeDiscountPromotionTotal.value > 0}">
														<b><spring:theme code="basket.page.employee.discount.promotions.minicart"
										text="Employee Discount:" />&nbsp; -&nbsp;<format:price
										priceData="${cartData.appliedEmployeeDiscountPromotionTotal}" /></b>
											</c:if>
										</div>
										<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 txtCol-red text-right">
											<c:if test="${cartData.appliedOrderPromotionTotal.value > 0}">
													<b><spring:theme code="basket.page.totals.order.promotions.minicart "
															text="Bill Buster Discount" />&nbsp; -&nbsp;<format:price
															priceData="${cartData.appliedOrderPromotionTotal}" />
													</b>
											</c:if>
										</div>
										
										<!-- Code change start for voucher story -->
								 	
								 	<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 text-right txtCol-red">
									 	<%-- <c:if test="${not empty cartData.appliedVoucherTotal }">
		                           			<c:forEach items="${cartData.appliedVoucherTotal}" var="voucherValue">                       				 
												<div class="cartDelivery  ptb5 text-right">
													<b><spring:theme code="basket.page.totals.order.vouchers" text="Voucher Discount"/>&nbsp;
														-<format:price priceData="${voucherValue.appliedValue}"/>
													</b></font>
												</div>								
											</c:forEach>
										</c:if> --%>
										<c:if test="${not empty cartData.voucherValue && cartData.voucherValue.value ne '0.0'}">			 
											<div class="cartDelivery  ptb5 text-right">
												<b><spring:theme code="basket.page.cart.order.vouchers" text="Voucher Discount"/>&nbsp; -&nbsp;<format:price priceData="${cartData.voucherValue}"/></b>
											</div>								
										</c:if>
									</div> 
									<!-- Changes end here -->
								 		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-right">
											<c:if test="${not empty cartData.deliveryCost}">
												<div class="cartDelivery">
													<b><spring:theme code="checkout.multi.summary.deliveryCharges"	text="Delivery Charges" />
													&nbsp;<span class="delivery-charge">
													<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE" /></b>
												</div>
											</c:if>
										</div>									
								 	</div>
									
								 	<div class="col-md-12 text-right">
										<span class="totalPrice"> AMOUNT PAYABLE <ycommerce:testId
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
										</span>
									</div>
								 </div>
								 <div class="col-lg-3 col-md-3 col-sm-3 col-xs-12 text-center">
								 <div class="saved-rs">
							 		<c:if test="${not empty cartData.savings}">
                               			<b> Savings on SUBTOTAL <br/>  <format:price priceData="${cartData.savings}" /></b>
									</c:if>						 
								 </div>
									
								</div>
								<%-- <div class="row">
									<div class="col-md-12 text-right text-uppercase">
                <span class="totalPrice">  <ycommerce:testId code="cart_totalPrice_label">
											<c:choose>
												<c:when test="${showTax}">
													<format:price priceData="${cartData.totalPriceWithTax}" />
												</c:when>
												<c:otherwise>
													<format:price priceData="${cartData.totalPrice}" />
												</c:otherwise>
											</c:choose>
										</ycommerce:testId> </span> 
                    </div>
									<div class="col-md-12 text-right">
										<span class="totalPrice"> AMOUNT PAYABLE <ycommerce:testId
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
										</span>
									</div>
								</div> --%>

							</div>
							<!--Right Block----->
						</div>
					</div>
				</c:when>

				<c:otherwise>
                  Your shopping cart is empty. Start shopping now.

                  </c:otherwise>
			</c:choose>
		</div>

		<div class="row">
			<div class="col-sm-12 col-xs-12"
				style="visibility: hidden; display: none">
				<p class="btnCartPad">
					<span class="delivery-img"><img
						src="${commonResourcePath}/images/lorryDeli.png"
						alt="delivery image"></span> <span>Home delivery 1000 INR or
						more will be free home shipping and <br>less than that will
						be 30 INR shipping charge.
					</span>
				</p>
			</div>

			<div class="col-sm-12 col-xs-12 text-right nopadding">
				<c:choose>
					<c:when test="${numberShowing > 0 }">
						<div class="cartBtn">
							<a href="${checkoutUrl}" class="btn btnRed  minicart-checkout"> <spring:theme
									code="checkout.checkout" />
							</a>
						</div>
						<div class="cartBtn">
							<!-- <button type="button" class="btn btnRed">  CONTINUE SHOPPING </button> -->
							<a href="${cartUrl}" class="btn btn-Green"> <spring:theme
									text="VIEW CART" code="basket.view.basket" />
							</a>
						</div>
					</c:when>
					<c:otherwise>
						<div class="cartBtn">
							<!-- <button type="button" class="btn btnRed">  CONTINUE SHOPPING </button> -->
							<a href="${request.contextPath}" class="btn btnRed"> <spring:theme
									text="Continue Shopping" code="cart.page.continue" />
							</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>


	</div>
	<!--Row End----->


</div>


<script>
  //SPAR CART Pop up //
  $("#menu").hover(function() {
	$('.flyout').removeClass('hidden');
  });
  $('.flyout').mouseleave(function() {
	$('.flyout').addClass('hidden');
  });
</script>

