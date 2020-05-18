<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

 <div class="row benrMidle">
	<c:forEach items="${banners}" var="banner" varStatus="status">
		<c:if test="${ycommerce:evaluateRestrictions(banner)}" >
			<c:url value="${banner.urlLink}" var="encodedUrl" />
			<c:choose>
				<c:when test="${status.index=='1'}">			
					<div class="bnrMBx bnrMBx1 clr${status.index+1}"><span class="iconsBx"><img width="34" height="24" data-u="image"	src="${banner.media.url}"	alt="${not empty banner.headline ? banner.headline : banner.media.altText}"	title="${not empty banner.headline ? banner.headline : banner.media.altText}" /></span>
			<span class="iconTxt">${banner.title}</span></div>					
				</c:when>
						
				<c:when test="${status.index<='2'}">
					<div class="bnrMBx clr${status.index+1}"><span class="iconsBx"><img width="34" height="24" data-u="image"	src="${banner.media.url}"	alt="${not empty banner.headline ? banner.headline : banner.media.altText}"	title="${not empty banner.headline ? banner.headline : banner.media.altText}" /></span>
			<span class="iconTxt">${banner.title}</span></div>
				</c:when>
				
				<c:when test="${status.index=='3'}">
					<div class="col-md-12 col-sm-12 nopadding cards"><img width="100%" data-u="image"	src="${banner.media.url}"	alt="${not empty banner.headline ? banner.headline : banner.media.altText}"	title="${not empty banner.headline ? banner.headline : banner.media.altText}" /></div>
				</c:when>
				
			</c:choose>
						
		</c:if>
		
	</c:forEach>
	
</div>

 
 