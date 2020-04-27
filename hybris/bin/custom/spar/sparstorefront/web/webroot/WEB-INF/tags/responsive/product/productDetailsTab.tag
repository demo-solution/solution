<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="productAttribute" required="false" type="java.lang.String" %> 

<div class="tab-details">
	<ycommerce:testId code="productDetails_content_label">
		<div class="product-attributes">
			<c:if test="${not empty product.description}">
				<div class="headline">About Product description</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.description}</td>
							</tr>
						</tbody>
					</table>
			</c:if>		
			<c:if test="${not empty product.ingredientDescription}">	
				<div class="headline">Ingredient Description</div>
					<table class="table">	
						<tbody>
							<tr>
								<td class="attrib">${product.ingredientDescription}</td>
							</tr>
						</tbody>	
					</table>
			</c:if>	
			<c:if test="${not empty product.modelNumber}">	
				<div class="headline">Model Number</div>
					<table class="table">	
						<tbody>
							<tr>
								<td class="attrib">${product.modelNumber}</td>
							</tr>
						</tbody>	
					</table>	
			</c:if>		
		</div>
	</ycommerce:testId>
</div>