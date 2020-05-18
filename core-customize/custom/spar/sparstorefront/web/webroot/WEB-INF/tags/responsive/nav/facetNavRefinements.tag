<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>


<c:forEach items="${pageData.facets}" var="facet">

	<c:if test="${not empty facet.values && not empty facet.topValues}">
		<h2 class="hidden-xs"><spring:theme code="search.nav.facetTitle" arguments="${facet.name}"/></h2>
	</c:if>
	
	<c:choose>
		<c:when test="${facet.code eq 'availableInStores'}">
			<nav:facetNavRefinementStoresFacet facetData="${facet}" userLocation="${userLocation}"/>
		</c:when>
		<c:otherwise>
			<nav:facetNavRefinementFacet facetData="${facet}"/>
		</c:otherwise>
	</c:choose>
</c:forEach>


