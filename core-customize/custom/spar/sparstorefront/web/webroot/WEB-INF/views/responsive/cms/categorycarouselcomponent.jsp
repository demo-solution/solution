<!-- Author: Shaurya Gupta-->


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="category" tagdir="/WEB-INF/tags/responsive/category" %> 
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:choose>
	<c:when test="${not empty categoryData}">
		<div class="carousel-component">
			<div class="headline">
				<h2>${title}</h2>
			</div>

			<c:choose>
				<c:when test="${component.popup}">
					<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
						<c:forEach items="${categoryData}" var="category">
						

							<c:url value="${category.url}" var="categoryQuickViewUrl"/>
							<div class="item">
								<div class="thumb">
									<a href="${categoryQuickViewUrl}" class=""><img src="${category.image.url}" /></a>
									<%-- <span><category:categoryImage category="${category}" format="thumbnail"/></span>  --%>
									<%-- <div class="item-name">${category.name}</div> --%>								
								 </div>
							</div>
						</c:forEach>
					</div>
				</c:when>
				<c:otherwise>
					<div class="carousel js-owl-carousel js-owl-default">
						<c:forEach items="${categoryData}" var="category">

							<c:url value="${category.url}" var="categoryUrl"/>
							<div class="item">
								<a href="${categoryUrl}">
									<span class="thumb">
								<img src="${category.picture.url}" format="thumbnail" />
									<%-- <span>
										<category:categoryPrimaryImage product="${product}" format="thumbnail"/>
									</span> --%>
									</span>
									<span class="item-name">${category.name}</span>
									<%-- <p>
										<format:fromPrice priceData="${product.price}"/>
									</p> --%>
								</a>
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

