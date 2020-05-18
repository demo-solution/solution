<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="orderGroup" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ attribute name="index" required="false" type="Integer" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<c:url value="/cart/add" var="addToCartUrl"/>

<c:if test="${not empty product.variantOptions}">
	<c:set var="variantOptions" value="${product.variantOptions}" />
</c:if>
<spring:theme code="text.addToCart" var="addToCartText"/>
<table class="table ">
	<thead class="tableHead1"> 
	     <tr class="tableHeadText">
	       
	     <th class="name"><spring:theme code="text.account.orderHistory.itemName"/></th>
	     <th class="price" class="leftPadUnit"><spring:theme code="text.account.orderHistory.itemUnitPrice" text="SPAR Price"/></th>
	     <%-- <th class="saving"><spring:theme code="text.account.orderHistory.Savings"/></th> --%>
	     <th class="quantity"><spring:theme code="order.quantity" text="Quantity"/></th>
	     <th class="total"><spring:theme code="text.account.order.total" text="Quantity"/></th>
	      <th class="saving"><spring:theme code="text.account.orderHistory.Savings"/></th>
	     <th class="add-cart">&nbsp;  </th>
	   </tr>
	 </thead>
	 <tbody>
	 	<c:forEach items="${orderGroup.entries}" var="entry" varStatus="status">
	 	<c:url value="${entry.product.url}" var="productUrl"/>
		<tr class="item">
		   <td class="name">
		   <a class="thumb pull-left" href="${productUrl}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
			
				<a class="product-name" href="${entry.product.purchasable ? productUrl : ''}" title="${entry.product.name}">${entry.product.name}</a>
		
			
		   </td>
		   <td class="price" headers="header4">
			<ycommerce:testId code="orderDetails_productItemPrice_label"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></ycommerce:testId>
		   </td>
			   <%-- <td class="saving"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></td> --%>
		   <td  class="input-group input-group-widthcart js-qty-selector" headers="header5" class="quantity">
			
				<div class="input-group  js-qty-selector">
		           <span class="input-group-btn data-dwn spinner">
		               <button class=" btn btn-default btn-info plp-js-qty-selector-minus" data-type="minus"><span class="glyphicon glyphicon-minus"></span></button>
		           </span>
	     		   <input type="text" maxlength="3" class="form-control text-center plp-js-qty-selector-input" size="1" value='${entry.quantity}' data-max="${entry.product.stock.stockLevel}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput"   /> 
	         	   <%-- Input Hidden Parameter for Product Code Start--%>
				   <input id="product_${status.index}"  type="hidden" name="productIndex" class="productIndex" value="${status.index}"/>
				   <%-- Input Hidden Parameter for Product Code End--%>
		           <span class="input-group-btn data-up product-btn spinner">
		               <button class="btn btn-default btn-info plp-js-qty-selector-plus" data-type="plus"><span class="glyphicon glyphicon-plus"></span></button>
		           </span>
	      	   </div>
			</td>
			<td class="total" headers="header6" class="total">
				<ycommerce:testId code="orderDetails_productTotalPrice_label"><format:price priceData="${entry.totalPrice}" displayFreeForZero="false" /></ycommerce:testId>
			</td>
			<td class="saving">
				<c:choose>
				 <c:when test="${entry.combiOfferApplied}">
				 	<span class="redColr"><spring:theme code="text.combi.offer" text="Combi Offer"/></span>
				 </c:when>
				 <c:otherwise>
				 	<span class="redColr"><format:price priceData="${entry.savings}" displayFreeForZero="false"/></span>
				 </c:otherwise>
	 			</c:choose>
			</td>
			<td class="add-cart">
				<form:form method="post" id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}">
					<c:if test="${entry.product.purchasable}">
						<input type="hidden" maxlength="3" size="1" id="qty_${status.index}" name="qty" class="qty plp-js-qty-selector-input" value="${entry.quantity}">
					</c:if>
					<input type="hidden" name="productCodePost" value="${entry.product.code}"/>
					
					<c:if test="${empty showAddToCart ? true : showAddToCart}">
						<c:set var="buttonType">button</c:set>
						<c:if test="${entry.product.purchasable and entry.product.stock.stockLevelStatus.code ne 'outOfStock' }">
							<c:set var="buttonType">submit</c:set>
						</c:if>
						<c:choose>
							<c:when test="${fn:contains(buttonType, 'button')}">
								<button type="${buttonType}" class="btn btn-primary btn-block js-add-to-cart outOfStock" disabled="disabled">
									<spring:theme code="product.variants.out.of.stock"/>
								</button>
							</c:when>
							<c:otherwise>
								<ycommerce:testId code="addToCartButton">
									<button id="addToCartButton" type="${buttonType}" class="btn btn-primary btn-sm js-add-to-cart" disabled="disabled">
										<img  src="${commonResourcePath}/images/cart-icon-18x18.png" style="padding-right:1em"/><spring:theme code="basket.add.to.cart"/>
									</button>
								</ycommerce:testId>
							</c:otherwise>
						</c:choose>
					</c:if>
				</form:form>
			</td> 
		</tr>
	</c:forEach>
    
    <tr class="borderTopDown">
    <td colspan="6">                                        	  	
		<div class="row"> 
			<div class="col-sm-9 col-md-9 col-lg-9 col-xs-12">
			<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right sub-total"><b><spring:theme code="text.account.orderHistory.subTotal"/>&nbsp;<format:price priceData="${order.subTotal}"/></b> </div>				
				
				<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right"><b>
				<c:choose><c:when test="${order.totalPrice.value eq '0.0'}"></c:when>
					<c:otherwise>
					<c:choose>
						<c:when test="${not empty order.voucherValue && order.voucherValue.value ne '0.0'}">
							<spring:theme code="basket.page.totals.discount.voucher" text="PRODUCT NAME"/>&nbsp; -<format:price priceData="${order.totalDiscounts}" />
						</c:when>
						<c:otherwise>
							<spring:theme code="basket.page.totals.discount" text="PRODUCT NAME"/>&nbsp; -<format:price priceData="${order.totalDiscounts}" />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
				</c:choose></b></div>
				
				
				<%-- <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right">
					 <c:if test="${order.appliedEmployeeDiscountPromotionTotal.value > 0}"> 
							<b><spring:theme code="basket.page.employee.discount.promotions"
										text="Employee Discount:" />&nbsp; -&nbsp;<format:price
										priceData="${order.appliedEmployeeDiscountPromotionTotal}" /></b>
					 </c:if> 
				</div> --%>
				<%-- 
				<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right">
					<c:if test="${order.appliedOrderPromotionTotal.value > 0}">
							<b><spring:theme code="basket.page.totals.order.promotions"
									text="Bill Buster Discount:" />&nbsp; -&nbsp;<format:price
									priceData="${order.appliedOrderPromotionTotal}" />
							</b>
					</c:if>
				</div> --%>
				<!-- Code change start for voucher story -->
				
				
				 	<%-- <c:if test="${not empty order.appliedVoucherTotal}">
                    	<c:forEach items="${order.appliedVoucherTotal}" var="voucherValue">
								<b><spring:theme code="basket.page.cart.order.vouchers " text="Voucher Discount:"/>&nbsp;-
									<format:price priceData="${voucherValue.appliedValue}"/>
								</b>					
						</c:forEach>
						<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right">
							<b><spring:theme code="Voucher Code" text="Voucher Code: "/>&nbsp;-&nbsp;${order.voucherCode}</b>
						</div>
					</c:if> --%>
				<%-- 	 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right">
					<c:if test="${not empty order.voucherValue && order.voucherValue.value ne '0.0'}">
						 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right">				 
							<div class="cartSaved saved-rs text-right"><b><spring:theme code="basket.page.totals.order.vouchers" text="Voucher Discount:"/>&nbsp; -&nbsp;<format:price priceData="${order.voucherValue}"/></b></div>								
						</div>
						<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding txtCol-red text-right">
							<b><spring:theme code="Voucher Code" text="Voucher Code: "/>&nbsp; -&nbsp;${order.voucherCode}</b>
						</div>
					</c:if>
				 </div>	 --%>
				 <!-- Change end here -->	
				<div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right"><b><spring:theme code="text.account.orderHistory.deliveryCharges"/>&nbsp;<format:price priceData="${order.deliveryCost}" displayFreeForZero="FALSE"/> </b></div>		 
				
				 <div class="col-sm-12 col-md-12 col-lg-12 col-xs-12 nopadding text-right"><b><spring:theme code="text.account.orderHistory.total"/> &nbsp;<span class="totalPrice"><format:price priceData="${order.totalPrice}"/> </span> </b></div>
				
			
			<%--  <multi-checkout:orderTotals cartData="${order}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" /> --%>
			</div>
			<div class="col-sm-2 col-md-2 col-lg-2 col-xs-12 nopadding  text-center txtCol-red fs11">				
					<b><spring:theme code="cart.page.savesubTotal"/><br/><format:price priceData="${order.savings}" displayFreeForZero="false"/></b>
			</div>
			<div class="col-sm-1 col-md-1 col-lg-1">
				&nbsp;
			</div>
		</div>
		
	</td>
	</tr>
	<tr >
		<td class=" pull-left"> <div class="btnCartPad">
 
 	<a href="${request.contextPath}">
    	<button  class=" btn-block continueShoppingButton btn btnRed btn-sm"><spring:theme code="cart.page.continueShoping" text="Continue Shopping"/></button>
	</a>
		</div> </td>
	
	</tr>
	</tbody>
</table>


