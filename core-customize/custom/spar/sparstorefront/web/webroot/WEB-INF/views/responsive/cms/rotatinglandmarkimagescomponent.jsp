<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

 
<div class="row slider_component simple-banner">

<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 nopadding ">
<div class="row homeSliPdg">
	<div id="homepage_slider1" class="banner-main">
		<div data-u="slides" class="banner-min" >
			<c:forEach items="${banners}" var="banner" varStatus="status">

				<c:if test="${ycommerce:evaluateRestrictions(banner)}">
					<c:url value="${banner.urlLink}" var="encodedUrl" />
					<div class="banner-wrapper" data-p="112.50"><a tabindex="-1" href="${encodedUrl}"
						<c:if test="${banner.external}"> target="_blank"</c:if>>
						<img data-u="image"
							src="${banner.media.url}"
							alt="${not empty banner.headline ? banner.headline : banner.media.altText}"
							title="${not empty banner.headline ? banner.headline : banner.media.altText}" /></a>
							<div data-u="thumb" >								
								<!--img src="img/20off.png" /-->
								<div class="title_back"></div>
								<%-- <div class="title" style="line-height:50px;" ><img src="${banner.bottomImage.url}"></div> --%>
								<div class="title"></div>
                			</div></div>
                		</c:if>
			</c:forEach>
		</div>
		<!-- Thumbnail Navigator -->
        <div data-u="thumbnavigator" class="thumbnavigator jssort16 bullets" data-autocenter="1">
            <!-- Thumbnail Item Skin Begin -->
            <div data-u="slides" style="cursor: pointer;">
                <div data-u="prototype" class="p">
                    <div data-u="thumbnailtemplate" class="t"></div>
                </div>
            </div>
            <!-- Thumbnail Item Skin End -->
        </div>
        <!-- Bullet Navigator -->
        <div data-u="navigator" class="jssorb03" style="bottom:116px;right:16px;">
            <!-- bullet navigator item prototype -->
            <div data-u="prototype" style="width:21px;height:21px;">
                <div data-u="numbertemplate"></div>
            </div>
        </div>
	</div>
</div>
</div>
</div>
