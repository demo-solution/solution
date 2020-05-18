<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb" %>



<c:if test="${component.visible}">
	<div class="vertical-menu-content">
		<ul class="vertical-menu-list">
			<c:forEach items="${components}" var="component" >
				<c:if test="${component.navigationNode.visible}">
					<c:set var="count_var" value="${count_var + 1}" scope="page"/>
					
					<c:if test="${count_var <=15}">
				<li class="vertical-menu${count_var}">
					 	<cms:component component="${component}" evaluateRestriction="true"/>
						</li>
						</c:if>
				</c:if>
			</c:forEach>
		</ul>				
	</div>
</c:if>