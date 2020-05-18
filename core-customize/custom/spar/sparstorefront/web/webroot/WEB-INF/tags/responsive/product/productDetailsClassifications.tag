<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="classificationName" required="false" type="java.lang.String" %> 


<div class="product-classifications">
	<c:if test="${not empty product.classifications}">
		<c:forEach items="${product.classifications}" var="classification">
			<c:if test="${classification.name eq classificationName}">
			<c:if test="${classification.name eq 'Storage & Care'}">
				<div class="headline">Storage Type</div>
				<table class="table">
					<tbody>
						<tr>
							<td class="attrib">Type</td>
							<td>${product.storageType}</td>
						</tr>
					</tbody>
				</table>
			</c:if>
			<div class="headline">${classification.name}</div>
				<table class="table">
					<tbody>
						<c:forEach items="${classification.features}" var="feature">
							<tr>
								<td class="attrib">${feature.name}</td>

								<td>
									<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
										<c:choose>
											<c:when test="${feature.name eq 'Nutrition'}">
												<c:set var="valueArray" value="${value.value}"/>  
												<c:set var="valueSplit" value="${fn:split(valueArray , '#&')}"/>
												<c:forEach varStatus="loop" items="${valueSplit}">
           											 ${valueSplit[loop.index]}<br/><br/>
												</c:forEach>
											</c:when>
											<c:otherwise>
												${value.value}
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${feature.range}">
												${not status.last ? '-' : feature.featureUnit.symbol}
											</c:when>
											<c:otherwise>
												${feature.featureUnit.symbol}
												${not status.last ? '<br/>' : ''}
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</c:forEach>
	</c:if>
</div>