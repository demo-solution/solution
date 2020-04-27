<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url value="/cart/add" var="addToCartUrl"/>
<form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
	<input type="hidden" maxlength="3" size="1" id="qty_${productIndex}" name="qty" class="qty plp-js-qty-selector-input" value="1">
	<ycommerce:testId code="addToCartButton">
		<input type="hidden" name="productCodePost" value="${product.code}"/>
		<input type="hidden" name="productNamePost" value="${product.name}"/>
		<input type="hidden" name="productPostPrice" value="${product.price.value}"/>
		
		<button type="submit" class="btn btn-primary btn-block  add-btn <c:if test="
			${product.stock.stockLevelStatus.code eq 'outOfStock' }">out-of-stock</c:if>"
				<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>> <i class="fa fa-shopping-cart"></i>${addToCartText}</button>
	</ycommerce:testId>
</form:form>

