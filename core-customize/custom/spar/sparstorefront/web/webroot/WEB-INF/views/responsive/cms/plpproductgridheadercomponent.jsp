<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>
 
 <c:if test="${empty title}">
	<spring:theme code="featuredProduct.name" var="title"/>
</c:if>

<c:choose>
	<c:when test="${not empty productData}">
		<div class="carousel-component plpCarousel hidden-xs">
			<div class="headline">${title}</div>
			
			<c:choose>
				<c:when test="${component.popup}">
					<div class="carousel js-owl-carousel js-owl-PLP-reference js-owl-carousel-reference">
						<%-- <div id="quickViewTitle" class="quickView-header" style="display:none">
							<div class="headline">		
								<span class="headline-text"><spring:theme code="popup.quick.view.select"/></span>
							</div>
						</div> --%>
						<c:forEach items="${productData}" var="product" varStatus="status">

							<c:url value="${product.url}" var="productUrl"/>
							<div class="item" >
								<%-- <a href="${productUrl}" >
										<div class="thumb">
										<product:productPrimaryReferenceImage product="${product}" format="plpFeaturedProduct"/>
										</div>
								</a> --%>
								<a  id="variantImageAnchor_${index}" href="${productUrl}" title="${product.description}">
									<div class="plp-thumb">
										<product:productPrimaryImage product="${product}" index="${index}" format="plpProduct"/>
									</div>
								</a>
								<div class="bottomBorder"></div>
								<%-- <div id= "featuredProducts_${status.index}"  class="row featureProduct">
									<product:featuredProductGridItem product="${product}"  index="${status.index}"/>
								</div> --%>
							</div>
						</c:forEach>
					</div>
					<div class="featureProductBx">
						<c:forEach items="${productData}" var="product" varStatus="status">
						<div id= "featuredProducts_${status.index}"  class="row featureProduct">
							<product:featuredProductGridItem product="${product}"  index="${status.index}"/>
						</div>
						</c:forEach>
					</div>
				</c:when>
				<c:otherwise>
					<div class="carousel js-owl-carousel js-owl-default">
						<c:forEach items="${productData}" var="product" varStatus="status">

							<c:url value="${product.url}" var="productUrl"/>

							<div class="item" >
								<a href="${productUrl}">
										<product:productPrimaryImage product="${product}" format="plpFeaturedProduct"/>
								</a>
								<div id= "featuredProducts_${status.index}" style="display:none" >
									<product:featuredProductGridItem product="${product}"  index="${status.index}"/>
								</div>
							</div>
						</c:forEach>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>

