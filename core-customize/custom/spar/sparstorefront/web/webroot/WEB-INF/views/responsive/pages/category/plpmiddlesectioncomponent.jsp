<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%>

<c:choose>
	<c:when test="${not empty searchPageData.freeTextSearch}">
		<div class="results">
			<h1>
				<spring:theme code="search.page.searchText"
					arguments="${searchPageData.freeTextSearch}" />
			</h1>
		</div>
		<nav:searchSpellingSuggestion
			spellingSuggestion="${searchPageData.spellingSuggestion}" />
	</c:when>
	<c:otherwise>
		<div class="col-md-12">
			<h4 class="primary-heading">
				<c:forEach items="${breadcrumbs}" var="breadcrumb"
					varStatus="status">
					<c:if test="${status.last}">
				${breadcrumb.name}
			</c:if>
				</c:forEach>
			</h4>
		</div>

	</c:otherwise>
</c:choose>

<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}"
	supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
	searchUrl="${searchPageData.currentQuery.url}"
	numberPagesShown="${numberPagesShown}" />
<nav:facetNavAppliedFilters pageData="${searchPageData}" />
