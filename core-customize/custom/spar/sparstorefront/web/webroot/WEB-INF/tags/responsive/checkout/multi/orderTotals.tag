<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>
<%@ attribute name="subtotalsCssClasses" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>



<c:if test="${not empty cartData.deliveryCost}">
		<div class="col-sm-3">
			<strong> 
				<spring:theme code="checkout.multi.summary.deliveryCharges" text="Delivery Charges" /> 
				<span class="delivery-charge">
					<ycommerce:testId code="Order_Totals_Delivery">
							<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="FALSE"/>
						</ycommerce:testId>
				</span>
			</strong>
		</div>
</c:if>

<c:if test="${cartData.totalDiscounts.value > 0}">
	<div class="col-sm-3">
		<strong class="danger-text">
			<spring:theme code="checkout.multi.summary.total.discount" text="Total Discount"/>
			<span class="delivery-charge">
				<ycommerce:testId code="Order_Totals_Savings">
					<format:price priceData="${cartData.totalDiscounts}"/>
				</ycommerce:testId>
			</span>
		</strong>
	</div>
</c:if>
		
<%-- <div class="subtotals ${subtotalsCssClasses}"> --%>
<%-- <div class="subtotal-headline"><spring:theme code="order.order.totals"/></div> --%>	
	<!-- Not required in S1 -->
	<%-- <div class="subtotal">
		<spring:theme code="basket.page.totals.subtotal"/> 
		<span>
			<ycommerce:testId code="Order_Totals_Subtotal">
				<format:price priceData="${cartData.subTotal}"/>
			</ycommerce:testId>
		</span>
	</div>
	 --%>
	
	<!--  Not required in S1 -->
	<%-- <c:if test="${cartData.net && cartData.totalTax.value > 0 && showTax}">
		<div class="tax">
			<spring:theme code="basket.page.totals.netTax"/>
			<span>
				<format:price priceData="${cartData.totalTax}"/>
			</span>
		</div>
	</c:if> --%>
	<div class="col-sm-3"><strong class="danger-text"><spring:theme code="checkout.multi.summary.total.savings" text="You Saved"/>&nbsp; <span class="delivery-charge"><format:price priceData="${cartData.savings}" displayFreeForZero="false"/></span></strong></div>
	
	<div class="col-sm-3">
		<strong class="pull-right">
			<spring:theme code="checkout.multi.summary.order.order.total" text="Total"/>&nbsp;
			<span class="delivery-charge">
				<ycommerce:testId code="cart_totalPrice_label">
						<c:choose>
							<c:when test="${showTax}">
								<format:price priceData="${cartData.totalPriceWithTax}"/>
							</c:when>
							<c:otherwise>
								<format:price priceData="${cartData.totalPrice}"/>
							</c:otherwise>
						</c:choose>
		
				</ycommerce:testId>
			</span>
		</strong>
	    <span class="pull-right total-amt"><spring:theme code="checkout.multi.summary.order.order.totals" text="TOTAL AMOUNT"/>&nbsp;
		    <span class="tot-charge">
				<ycommerce:testId code="cart_totalPrice_label">
						<c:choose>
							<c:when test="${showTax}">
								<format:price priceData="${cartData.totalPriceWithTax}"/>
							</c:when>
							<c:otherwise>
								<format:price priceData="${cartData.totalPrice}"/>
							</c:otherwise>
						</c:choose>
				</ycommerce:testId>
			</span>
		</span>
	</div>
	
	<!--  Not in scope of S1 -->	                        
	<%-- <c:if test="${not cartData.net}">
		<div class="realTotals">
			<ycommerce:testId code="cart_taxes_label">
				<p>
					<spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!"/>
				</p>
			</ycommerce:testId>
		</div>
	</c:if>
	<c:if test="${cartData.net && not showTax }"> 
		<div class="realTotals">
			<ycommerce:testId code="cart_taxes_label">
				<p>
					<spring:theme code="basket.page.totals.noNetTax"/>
				</p>
			</ycommerce:testId>
		</div>
	</c:if> --%>
	<!-- 
		<cart:taxExtimate cartData="${cartData}" showTaxEstimate="${showTaxEstimate}"/>
     --> 
</div>
	
