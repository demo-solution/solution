<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<ul class="productItemList">
	<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
		<c:url value="${breadcrumb.url}" var="breadcrumbUrl" />
		<c:choose>
			<c:when test="${status.last}">
				<li class="active" style="display:none;">${breadcrumb.name}</li>
			</c:when>
			<%-- <c:when test="${breadcrumb.url eq '#'}">
				<li class="productMenuItems">
					<a href="#">${breadcrumb.name}</a>
				</li>
			</c:when> --%>
			<c:otherwise>
				<li class="productMenuItems">
					<a href="${breadcrumbUrl}"><i class="fa fa-caret-right"></i>${breadcrumb.name}</a>
				</li>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</ul>
