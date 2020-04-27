<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="productAttribute" required="false" type="java.lang.String" %> 

	<div class="product-attributes">
		<c:if test="${not empty product.produceEAN}">
				<div class="headline">Produce - (EAN)</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.produceEAN}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
		
			<c:if test="${not empty product.productBrand}">
				<div class="headline">Brand</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.productBrand}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.manufacture}">
				<div class="headline">Manufacturer (Name & Address)</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.manufacture}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.importedBy}">
				<div class="headline">Imported by (Name and Address)</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.importedBy}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.netQuantity}">
				<div class="headline">Net Quantity</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.netQuantity}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.productSize}">
				<div class="headline">Size</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.productSize}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.bestBefore}">
				<div class="headline">Best Before (Days)</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.bestBefore}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.usedByDate}">
				<div class="headline">Used by Date</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.usedByDate}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
			
			<c:if test="${not empty product.customercare}">
				<div class="headline">Customer Care</div>
					<table class="table">
						<tbody>
							<tr>
								<td class="attrib">${product.customercare}</td>
							</tr>
						</tbody>
					</table>
			</c:if>
	</div>