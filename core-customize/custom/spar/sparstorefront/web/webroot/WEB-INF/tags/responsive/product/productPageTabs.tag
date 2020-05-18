<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
<div class="col-xs-12 prodDescTabStyle nopadding"> <hr class="nomargin">
<div class="nav nav-tabs">
<div class="tabs js-tabs tabs-responsive p10">

	<c:if
					test="${not empty product.description or not empty product.ingredientDescription or not empty product.modelNumber}">
					<div class="tabhead">
						<a href=""><spring:theme code="product.product.details" /></a> <span
							class="glyphicon"></span>
					</div>
					<div class="tabbody">
						<product:productDetailsTab product="${product}" />
					</div>
				</c:if>


				<c:if test="${not empty product.classifications}">
					<c:forEach items="${product.classifications}" var="classification">
						<c:if test="${classification.name eq 'Nutrition'}">
							<div class="tabhead">
								<a href=""><spring:theme code="product.product.nutrition" /></a>
								<span class="glyphicon"></span>
							</div>

							<div class="tabbody">
								<c:if test="${not empty classification.features}">
									<product:productDetailsClassifications product="${product}"
										classificationName="Nutrition" />
								</c:if>
							</div>
						</c:if>



						<c:if test="${classification.name eq 'Product Specifications'}">
							<div class="tabhead">
								<a href=""><spring:theme code="product.product.spec" /></a> <span
									class="glyphicon"></span>
							</div>
							<div class="tabbody">
								<c:if test="${not empty classification.features}">
									<product:productDetailsClassifications product="${product}"
										classificationName="Product Specifications" />
								</c:if>
							</div>
						</c:if>


						<c:if test="${classification.name eq 'Benefits'}">
							<div class="tabhead">
								<a href=""><spring:theme code="product.product.Benefits" /></a>
								<span class="glyphicon"></span>
							</div>
							<div class="tabbody">
								<c:if test="${not empty classification.features}">
									<product:productDetailsClassifications product="${product}"
										classificationName="Benefits" />
								</c:if>
							</div>
						</c:if>


						<c:if test="${classification.name eq 'Preparation & Usage'}">
							<div class="tabhead">
								<a href=""><spring:theme code="product.product.Preparation" /></a>
								<span class="glyphicon"></span>
							</div>
							<div class="tabbody">
								<c:if test="${not empty classification.features}">
									<product:productDetailsClassifications product="${product}"
										classificationName="Preparation & Usage" />
								</c:if>
							</div>
						</c:if>


						<c:if test="${classification.name eq 'Storage & Care'}">
							<div class="tabhead">
								<a href=""><spring:theme code="product.product.Storage" /></a>
								<span class="glyphicon"></span>
							</div>
							<div class="tabbody">
								<c:if test="${not empty classification.features}">
									<product:productDetailsClassifications product="${product}"
										classificationName="Storage & Care" />
								</c:if>
							</div>
						</c:if>

					</c:forEach>
				</c:if>
				
				<c:if test="${not empty product.produceEAN or not empty product.productBrand or not empty product.manufacture or not empty product.importedBy or not empty product.netQuantity or not empty product.productSize or not empty product.bestBefore or not empty product.usedByDate or not empty product.customercare}">
				<div class="tabhead">
					<a href=""><spring:theme code="product.product.legal" /></a> <span class="glyphicon"></span>
				</div>
				<div class="tabbody">
					<product:productLegalMetrologyAttributes product="${product}" productAttribute="disclaimer"/> 
				</div>
				</c:if>

				<c:if test="${not empty product.disclaimer or not empty product.pickingAndPacking}">
					<div class="tabhead">
						<a href=""><spring:theme code="product.product.Disclaimer" /></a>
						<span class="glyphicon"></span>
					</div>
					<div class="tabbody">
						<product:productDetailsAttributes product="${product}"
							productAttribute="disclaimer" />
					</div>
				</c:if>
	
	<%-- <div id="tabreview" class="tabhead">
		<a href=""><spring:theme code="review.reviews" /></a> <span
			class="glyphicon"></span>
	</div>	
	<div class="tabbody">
		<product:productPageReviewsTab product="${product}" />
	</div>
 --%>
	<%-- <cms:pageSlot position="Tabs" var="tabs">
		<cms:component component="${tabs}" />
	</cms:pageSlot> --%>
</div>
</div>
</div>
</div>