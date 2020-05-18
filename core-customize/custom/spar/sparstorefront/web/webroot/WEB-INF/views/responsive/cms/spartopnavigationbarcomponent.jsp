<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses"/>
			
    <%-- <cms:component component="${component.link}" evaluateRestriction="true" class="btn btn-red" /> --%> 
<c:choose>
    
	<c:when test="${not empty component.navigationNode.children}">
		<div class="btn-group ">
			<c:choose>
				<c:when test="${component.link.name eq 'SdayTopLink2'}">
					<a  href="${component.link.url}" class="btn btn-red"><span>${component.link.linkName}</span></a>
				</c:when>
				<c:otherwise>
					<a data-toggle="dropdown"
						class="btn btn-red dropdown-toggle downArrow"><span>${component.link.linkName}</span></a>
					<ul role="menu" class="dropdown-menu">
						<c:forEach items="${component.navigationNode.children}" var="child">
							<c:forEach items="${child.links}" var="childlink"
								begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
								<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
							</c:forEach>
						</c:forEach>
					</ul>
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>        
	              
	<c:when test="${empty component.navigationNode.children}">            	  
		<div class="btn-group ">
			<c:choose>
				<c:when test="${empty component.link.linkName}">
						<p role="button" class="btn btn-red ${component.link.linkName}" style="cursor:default;"></p>
				</c:when>	
				<c:otherwise>
						<a href="${component.link.url}" role="button" class="btn btn-red ${component.link.linkName}">
					<span>  ${component.link.linkName}</span></a>
				</c:otherwise>	
			</c:choose>
		</div>
	</c:when>
</c:choose>