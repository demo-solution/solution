<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ attribute name="checkoutSteps" required="true" type="java.util.List" %>
<%@ attribute name="progressBarId" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<!-- <div class="col-sm-6 col-md-7 col-lg-5"> -->
<div class="col-lg-12 nopadding">

<div id="pincodeAlert" class="alert alert-warning alert-dismissible" style="display:none">
  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
  Please enter the valid pin code and continue.
</div>

	<div class="container checkout-block">
		<section>
			<div class="wizard">
				<div class="wizard-inner">
				<div class="connecting-line"></div>
					<ycommerce:testId code="checkoutSteps">
						<div class="checkout-steps">
							<ul class="nav nav-tabs" role="tablist">
								<c:forEach items="${checkoutSteps}" var="checkoutStep" varStatus="status">
									<c:url value="${checkoutStep.url}" var="stepUrl"/>
									<c:choose>
										<c:when test="${progressBarId eq checkoutStep.progressBarId}">
											<c:set scope="page"  var="activeCheckoutStepNumber"  value="${checkoutStep.stepNumber}"/>
											<li role="presentation" class="active">
											<span class="tab-band"></span>
												<a href="${stepUrl}" class="step-head js-checkout-step active">
													<span class="checktab"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></span>
													<span class="round-tab">
                                				<i class="glyphicon">${checkoutStep.stepNumber}</i>
                            				</span>
												</a>
											</li>
											
										</c:when>
										<c:when test="${checkoutStep.stepNumber > activeCheckoutStepNumber}">
											<li role="presentation" class="disabled">
												<!-- <span class="tab-band"></span> -->
												<a href="${stepUrl}" class="step-head js-checkout-step ">
													<span class="checktab"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></span>
													<span class="round-tab">
                                				<i class="glyphicon">${checkoutStep.stepNumber}</i>
                            				</span>
												</a>
											</li>
										</c:when>
										<c:otherwise>
											<li role="presentation" class="disabled">
											<span class="tab-band"></span>
												<a href="${stepUrl}" class="step-head js-checkout-step ">
													<span class="checktab"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></span>
													<span class="round-tab">
                                				<i class="glyphicon">${checkoutStep.stepNumber}</i>
                            				</span>
													<!-- <div class="edit">
														<span class="glyphicon glyphicon-pencil"></span>
													</div> -->
												</a>
											</li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</ul>
							<div class="step-body"><jsp:doBody/></div>
						</div>
					</ycommerce:testId>
				<!-- </div> -->
				</div>
			</div>
		</section>
	</div>
</div>





					
					
					
