<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="index" required="false" type="Integer" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<c:if test="${null != product.childSKUs}">
	<c:url value="/search/?text=${product.childSKUs}" var="seeItsContentsUrl"/>
	<input type='hidden' id="comboMultiProductsUrl" value="${seeItsContentsUrl}"/>
	<a href="#" class="comboMultiProducts" data-toggle="tooltip" data-placement="top" title="See the items included in Combo\Multi pack. To avail the discount on combo pack add the combo pack itself">See its contents</a>
</c:if>	
