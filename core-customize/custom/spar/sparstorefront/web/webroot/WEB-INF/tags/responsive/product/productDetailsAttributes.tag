<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="productAttribute" required="false" type="java.lang.String" %> 


	<div class="product-attributes">
		<c:if test="${not empty product.disclaimer}">
			<div class="headline">Disclaimer</div>
				<table class="table">
					<tbody>
						<tr>
							<td class="attrib">${product.disclaimer}</td>
						</tr>
					</tbody>
				</table>
		</c:if>
		<c:if test="${not empty product.pickingAndPacking}">
			<div class="headline">Picking And Packing</div>
				<table class="table">	
					<tbody>
						<tr>
							<td class="attrib">${product.pickingAndPacking}</td>
						</tr>
					</tbody>	
				</table>
		</c:if>
	</div>