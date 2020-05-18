<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:if test="${not empty pageData.breadcrumbs}">
<c:set var="noExclusiveBreadcrum" value="true"/>
<c:forEach items="${pageData.breadcrumbs}" var="breadcrumb"  varStatus="status">
	<c:if test="${fn:length(pageData.breadcrumbs) eq '1'  && (breadcrumb.facetValueName == 'EXCLUSIVE' || breadcrumb.facetValueName == 'OFFER' || breadcrumb.facetValueName == 'DISCOUNT' || breadcrumb.facetValueName == 'HEALTHANDWELLNESS' || breadcrumb.facetValueName == 'SDAY' 
					|| breadcrumb.facetValueName == 'HOLI' || breadcrumb.facetValueName == 'DEALS')}">
		<c:set var="noExclusiveBreadcrum" value="false"/>
	</c:if>
	<c:if test="${fn:length(pageData.breadcrumbs) eq '2' && (pageData.breadcrumbs[status.first].facetValueName=='OFFER') && (pageData.breadcrumbs[status.first+1].facetValueName=='true') && (pageData.breadcrumbs[status.first+2].facetValueName=='true') }">
		<c:set var="noExclusiveBreadcrum" value="false"/>
	</c:if>
	<c:if test="${fn:length(pageData.breadcrumbs) eq '2' && (pageData.breadcrumbs[status.first].facetValueName=='DEALS')}">
			<c:set var="noExclusiveBreadcrum" value="false"/>
	</c:if>
	 <%-- <c:if test="${fn:length(pageData.breadcrumbs) eq '1' && (breadcrumb.facetValueName =='true')}">
		<c:set var="noExclusiveBreadcrum" value="false"/>
	</c:if> --%>  
	 
</c:forEach>

<c:if test="${noExclusiveBreadcrum}">
<div class="col-md-12 paraHeading">
<div class="row">
<div class="col-md-12 nopadding">
	<div class="facet js-facet">
		<div class="facet-name js-facet-name pull-left ">Filter :</div>
		<div class="facet-values js-facet-values pull-left col-md-11 nopadding">
			<ul class="facet-list"> 
				<c:forEach items="${pageData.breadcrumbs}" var="breadcrumb"  varStatus="status">
				
					<c:if test="${breadcrumb.facetValueName != 'EXCLUSIVE' && breadcrumb.facetValueName != 'OFFER'&& breadcrumb.facetValueName != 'true'&& breadcrumb.facetValueName != 'DISCOUNT' && breadcrumb.facetValueName != 'HEALTHANDWELLNESS' && breadcrumb.facetValueName != 'SDAY'
								&& breadcrumb.facetValueName != 'HOLI' && breadcrumb.facetValueName != 'DEALS'}">
					<li><%-- <c:if test="${status.first}"> ${breadcrumb.facetName}&nbsp;</c:if> --%>
						<c:url value="${breadcrumb.removeQuery.url}" var="removeQueryUrl"/>
						${breadcrumb.facetValueName}<a href="${removeQueryUrl}" ><span class="">x</span></a>
					<c:if test="${status.last}"></c:if></li>
					</c:if>
					
				</c:forEach>
			</ul>
		</div>
	</div>
	</div>
	</div>
</div>
</c:if>
</c:if>



