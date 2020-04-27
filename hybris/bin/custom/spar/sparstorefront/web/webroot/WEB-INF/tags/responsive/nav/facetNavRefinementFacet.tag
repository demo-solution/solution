<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true"
	type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="hidden-xs">
	<c:if test="${not empty facetData.values}">
		<ycommerce:testId code="facetNav_title_${facetData.name}">
			<div class="brands_products js-facet"
				style="background-color: #e9e9f2;">
				<div class="facet-name js-facet-name">
					<%-- <spring:theme code="search.nav.facetTitle" arguments="${facetData.name}"/> --%>
				</div>
				<div class="item-name js-facet-values js-facet-form">

					<c:if test="${not empty facetData.topValues}">
						<ul
							class="nav nav-pills nav-stacked js-facet-list js-facet-top-values">
							<c:forEach items="${facetData.topValues}" var="facetValue">
								<li><c:if test="${facetData.multiSelect}">
										<form action="#" method="get">
											<input type="hidden" name="q"
												value="${facetValue.query.query.value}" /> <input
												type="hidden" name="text"
												value="${searchPageData.freeTextSearch}" /> <label>
												<input class="facet-checkbox" type="checkbox"
												${facetValue.selected ? 'checked="checked"' : ''}
												class="facet-checkbox" /> <span class="facet-label">
													<span class="facet-mark"></span> <span class="facet-text">${facetValue.name}</span>
											</span> <span class="facet-value"><ycommerce:testId
														code="facetNav_count">
														<span class="facet-value-count"><spring:theme
																code="search.nav.facetValueCount"
																arguments="${facetValue.count}" /></span>
													</ycommerce:testId></span>
											</label>
										</form>
									</c:if> <c:if test="${not facetData.multiSelect}">
										<c:url value="${facetValue.query.url}"
											var="facetValueQueryUrl" />
										<a
											href="${facetValueQueryUrl}&amp;text=${searchPageData.freeTextSearch}">
											<i class="fa fa-caret-right" style="padding-right: 10px;"></i>
											<span class="pull-right"> <ycommerce:testId
													code="facetNav_count">
													<span class="facet-value-count"><spring:theme
															code="search.nav.facetValueCount"
															arguments="${facetValue.count}" /></span>
												</ycommerce:testId>
										</span> <span class="facetName">${facetValue.name}</span>
										</a>
									</c:if></li>
							</c:forEach>
						</ul>
					</c:if>


					<c:choose>
						<c:when
							test="${facetData.multiSelect and facetData.name != 'Show In-Stock Items only'}">
							<div id="" class="category-products ">
								<!--accordian-->
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="jqHeadingbx">
											<span class=" pull-right"> <i
												class="icon fa fa-caret-down"></i>
											</span>
											<spring:theme code="search.nav.facetTitle"
												arguments="${facetData.name}" />
										</h4>
									</div>
									<div class="panel-collapse collapse in">
										<div class="row">
											<ul
												class=" js-facet-list <c:if test="${not empty facetData.topValues}">facet-list-hidden js-facet-list-hidden</c:if>">
												<c:forEach items="${facetData.values}" var="facetValue">
													<li><ycommerce:testId code="facetNav_selectForm">
															<form action="#" method="get">
																<input type="hidden" name="q"
																	value="${facetValue.query.query.value}" /> <input
																	type="hidden" name="text"
																	value="${searchPageData.freeTextSearch}" /> <label>
																	<input type="checkbox"
																	${facetValue.selected ? 'checked="checked"' : ''}
																	class="facet-checkbox js-facet-checkbox " /> <span
																	class="facet-label"> <span class="facet-mark"></span>
																		<span class="facet-text">${facetValue.name}&nbsp;</span>
																</span> <span class="facet-value"><ycommerce:testId
																			code="facetNav_count">
																			<span class="facet-value-count"><spring:theme
																					code="search.nav.facetValueCount"
																					arguments="${facetValue.count}" /></span>
																		</ycommerce:testId>
																		</sapn>
																</label>
															</form>
														</ycommerce:testId></li>
												</c:forEach>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</c:when>

						<c:otherwise>
							<c:if test="${facetData.multiSelect}">
								<div id="" class="category-products ">
									<!--accordian-->
									<div class="panel panel-default">
										<ul
											class="facetbottombx js-facet-list<c:if test="${not empty facetData.topValues}">facet-list-hidden js-facet-list-hidden</c:if>">
											<c:forEach items="${facetData.values}" var="facetValue">
												<li><ycommerce:testId code="facetNav_selectForm">
														<form action="#" method="get">
															<input type="hidden" name="q"
																value="${facetValue.query.query.value}" /> <input
																type="hidden" name="text"
																value="${searchPageData.freeTextSearch}" /> <label>
																<input type="checkbox"
																${facetValue.selected ? 'checked="checked"' : ''}
																class="facet-checkbox js-facet-checkbox " /> <span
																class="facet-label"> <span class="facet-mark"></span>
																	<span class="facet-text"> ${facetValue.name} </span>
															</span>
															</label>
														</form>
													</ycommerce:testId></li>
											</c:forEach>
										</ul>
									</div>
								</div>
							</c:if>
						</c:otherwise>
					</c:choose>

					<c:if test="${not facetData.multiSelect}">
						<ul
							class="nav nav-pills nav-stacked  <c:if test="${not empty facetData.topValues}">facet-list-hidden js-facet-list-hidden</c:if>"
							style="display: none;">
							<c:forEach items="${facetData.values}" var="facetValue">
								<li><c:url value="${facetValue.query.url}"
										var="facetValueQueryUrl" /> <a
									href="${facetValueQueryUrl}&amp;text=${searchPageData.freeTextSearch}">
										<i class="fa fa-caret-right" style="padding-right: 10px;"></i>
										<span class="facet-value pull-right"> <ycommerce:testId
												code="facetNav_count">
												<span class="facet-value-count"><spring:theme
														code="search.nav.facetValueCount"
														arguments="${facetValue.count}" /></span>
											</ycommerce:testId>
									</span> <span class="facetName">${facetValue.name}</span>
								</a></li>
							</c:forEach>
						</ul>
					</c:if>

					<c:if
						test="${not empty facetData.topValues && not empty facetData.values && facetData.values.size()>5}">
						<div class="more-cat moreCateFacet">
							<i class="fa fa-plus" aria-hidden="true"></i> <span
								class="js-more-facet-values-link vimor"><spring:theme
									code="search.nav.facetShowMore_${facetData.code}" /></span> <span
								class="js-less-facet-values-link vimor" style="display: none;"><spring:theme
									code="search.nav.facetShowLess_${facetData.code}" /></span>
						</div>
					</c:if>
				</div>
			</div>
		</ycommerce:testId>
	</c:if>
</div>
