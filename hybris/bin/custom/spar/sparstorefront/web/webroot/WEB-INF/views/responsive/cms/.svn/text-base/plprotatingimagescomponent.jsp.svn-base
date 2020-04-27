<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

 <div class="row slider_component simple-responsive-banner-component mt10">
	<div id="homepage_slider2" class="banner-main">
		<div data-u="slides" class="banner-min" >
			<c:forEach items="${banners}" var="banner" varStatus="status">

				<c:if test="${ycommerce:evaluateRestrictions(banner)}">
					<c:url value="${banner.urlLink}" var="encodedUrl" />
					<div data-p="112.50">
						<img data-u="image"
							src="${banner.media.url}"
							alt="${not empty banner.headline ? banner.headline : banner.media.altText}"
							title="${not empty banner.headline ? banner.headline : banner.media.altText}" />
							</div>
                		</c:if>
			</c:forEach>
		</div>
		<div u="navigator" class="jssorb13" style="position: absolute; bottom: 16px; left: 15px;">
        <!-- bullet navigator item prototype -->
        	<div u="prototype" style="POSITION: absolute; WIDTH:15px; HEIGHT: 15px;"></div>
    	</div>
		
    
		
	</div>
</div>