<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
 

	<div class="col-md-12" ><h3>SPAR PROMISE </h3></div>     
	 <div class="col-md-12 spar-promise">	 				
		<nav><ul id="tabs">
			<c:forEach items="${banners}" var="banner" varStatus="status">
				<c:if test="${ycommerce:evaluateRestrictions(banner)}">
					<c:url value="${banner.urlLink}" var="encodedUrl" />					
					<li class="col-md-3 col-sm-6 col-xs-6 text-center"><a class="current" href="javascript:void(0)">
						<img data-u="image"	src="${banner.media.url}"	alt="${not empty banner.headline ? banner.headline : banner.media.altText}"	title="${not empty banner.headline ? banner.headline : banner.media.altText}" />
					</a></li>
                </c:if>
			</c:forEach>
			
			</ul></nav>
		<span id="indicator"></span></nav>
		<div id="content" class="mt10">
			<c:forEach items="${banners}" var="banner" varStatus="status">
				<section><p><c:out value="${banner.content}"/></p></section>				
			</c:forEach>
         </div>
     </div>