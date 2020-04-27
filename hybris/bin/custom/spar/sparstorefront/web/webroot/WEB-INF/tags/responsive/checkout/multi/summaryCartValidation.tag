<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<c:if test="${not empty validationData}">
	<c:set var="productLinkValidationTextDecoration" value="style=\"text-decoration: underline\""/>
	<div class="cartalert">
	<c:forEach items="${validationData}" var="modification">
		<div class="cart-flash-text alert-warning alert-dismissable">
				
				<c:url value="${modification.entry.product.url}" var="entryUrl"/>
				<i class="fa fa-caret-right"></i>
				<spring:theme code="basket.validation.${modification.statusCode}"
					arguments="${modification.entry.product.name}###${entryUrl}###${modification.quantity}###
							${modification.quantityAdded}###${productLinkValidationTextDecoration}" argumentSeparator="###"/><br>
			
		</div>
	</c:forEach>
	</div>
</c:if>
<c:if test="${not empty isOrderValueChanged and isOrderValueChanged==true }">
<div class="cartalert">
	<div class="cart-flash-text alert-warning alert-dismissable">
			<i class="fa fa-caret-right"></i>
				<spring:theme code="basket.validation.isOrderValueChanged"/><br>
	</div>
</div>
</c:if>