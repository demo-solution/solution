<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/" var="homeUrl" />

<ul class="breadcrumb">	
	<li><i class="fa fa-angle-double-right hidden-xs" aria-hidden="true"></i> <a href="${homeUrl}"><spring:theme code="breadcrumb.home" /></a>
	</li>

	<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
		<c:url value="${breadcrumb.url}" var="breadcrumbUrl" />
		<c:choose>
			<c:when test="${status.last}">
				<li class="active">${breadcrumb.name}<span class="fa fa-caret-up hidden-xs"></span></li>
			</c:when>
			<c:when test="${breadcrumb.url eq '#'}">
				<li>
					<a href="#">${breadcrumb.name}</a>
				</li>
			</c:when>
			<c:otherwise>
				<li>
					<a href="${breadcrumbUrl}">${breadcrumb.name}</a>
				</li>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</ul>
