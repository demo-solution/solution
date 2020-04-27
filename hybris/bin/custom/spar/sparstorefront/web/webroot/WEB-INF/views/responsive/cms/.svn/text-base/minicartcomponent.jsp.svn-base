
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:url value="/cart/miniCart/${totalDisplay}" var="refreshMiniCartUrl" />
<c:url value="/cart/rollover/${component.uid}" var="rolloverPopupUrl" />
<c:url value="/cart" var="cartUrl" />
<div href="${cartUrl}" class="mini-cart-link js-mini-cart-link miniCart"
	data-mini-cart-url="${rolloverPopupUrl}"
	data-mini-cart-refresh-url="${refreshMiniCartUrl}">

	<div class="mini-cart-icon">
		<span class="glyphicon glyphicon-shopping-cart "></span>
	</div>
	<div class="minicart-Name">CART</div>
	<%-- <ycommerce:testId code="miniCart_items_label"> --%>
	<div class="mini-cart-count js-mini-cart-count">${fn:length(entries)}</div>



	<%-- <div class="mini-cart-price js-mini-cart-price">
			<c:if test="${totalDisplay == 'TOTAL'}">
				<format:price priceData="${totalPrice}" />
			</c:if>
			<c:if test="${totalDisplay == 'SUBTOTAL'}">
				<format:price priceData="${subTotal}" />
			</c:if>
			<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
				<format:price priceData="${totalNoDelivery}" />
			</c:if>
		</div>  --%>

	<%-- </ycommerce:testId> --%>



	<c:if test="${totalItems <= 3}">
		<div class="popup-container">
			<div id="miniCartLayer" class="miniCartPopup"
				data-refreshMiniCartUrl="${refreshMiniCartUrl}/?"
				data-rolloverPopupUrl="${rolloverPopupUrl}">
				<div id="loading-cart">
					<span><img src="${commonResourcePath}/images/loading.gif"/></span>
					<span class="cart-text">Your cart is loading...</span>						
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="${totalItems > 3}">

		<div class="popup-container">
			<div class="popup-height">
				<div id="miniCartLayer" class="miniCartPopup"
					data-refreshMiniCartUrl="${refreshMiniCartUrl}/?"
					data-rolloverPopupUrl="${rolloverPopupUrl}">
					<div id="loading-cart">
						<span><img src="${commonResourcePath}/images/loading.gif"/></span>
						<span class="cart-text">Your cart is loading...</span>						
					</div>
				</div>
			</div>
		</div>
	</c:if>


</div>

