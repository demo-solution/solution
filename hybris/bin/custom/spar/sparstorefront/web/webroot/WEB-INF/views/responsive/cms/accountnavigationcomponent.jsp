<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<div class="greyBG1 mt10 my-account">
	<c:if test="${navigationNode.visible}">
		<c:choose>
			<c:when test="${navigationNode.title == 'MY ACCOUNT'}">
				<div class="grnBG">
					<h4>
						<a style="color: #fff;" href="${request.contextPath}/account">${navigationNode.title}</a>
					</h4>
				</div>
			</c:when>
			<c:otherwise>
				<div class="headline">
					<h4>${navigationNode.title}</h4>
				</div>
			</c:otherwise>
		</c:choose>
		<nav>
			<ul class="inBlockNav">
				<c:forEach items="${navigationNode.links}" var="link">

					<%-- <c:set value="${ requestScope['javax.servlet.forward.servlet_path'] == link.url ? 'active':'' }" var="selected"/> --%>

					<%--  <span class="sideMenuIcon"><img src="${component.navigationNode.previewIcon.url}" /></span> --%>

					<li class="greyBG">
						<span class="sideMenuIcon"><img src="${link.previewIcon.url}" /></span> 
						<cms:component	component="${link}" evaluateRestriction="true" />
					</li>
					<%-- <c:out value="link:${link.name}"></c:out> --%>
				</c:forEach>
			</ul>
		</nav>
	</c:if>
</div>