<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="pagination"
	tagdir="/WEB-INF/tags/responsive/nav/pagination"%>
<%@ page
	import="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%-- <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> --%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%-- <%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%> --%>

<spring:url value="/my-account/order/" var="orderDetailsUrl" />
<c:set var="searchUrl"
	value="/my-account/orders?sort=${searchPageData.pagination.sort}" />
<div class="content-wrapper">

	<div class="row">
		<!--container-->
		<%-- <c:if test="${fn:length(breadcrumbs) > 0}">
	<div class="breadcrumb breadcrumbText">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
	</div>
</c:if> --%>

		<div class="col-sm-12 nopadding">
			<div class="row bg-static">
				<div class="col-sm-12 nopadding">
					<h4>
						<spring:theme code="text.account.order.yourOrder" />
					</h4>
				</div>
				<div class="col-sm-12 p5">
					<c:choose>
						<c:when test="${not empty searchPageData.results}">
							<div class="accounHistory table-responsive p10">
								<table class="table ">
									<thead class="tableHead">
										<tr class="tableHeadText">

											<th style="width: 25%; padding-left: 20px;"
												class="text-uppercase"><spring:theme
													code="text.account.orderHistory.orderNumber" /></th>

											<th style="width: 16%; padding-left: 20px;"
												class="leftPadUnit text-uppercase"><spring:theme
													code="text.account.orderHistory.page.sort.byDate" /></th>
											<th style="width: 20%" class="text-uppercase"><spring:theme
													code="text.account.orderHistory.deliveryDate" /></th>
											<th style="width: 10%" class="text-uppercase"><spring:theme
													code="text.account.orderHistory.Savings" /></th>
											<th style="width: 20%; text-align: center"><spring:theme
													code="text.account.orderHistory.orderStatus" /></th>
											<th style="width: 9%">&nbsp;</th>
										</tr>
									</thead>

									<tbody>



										<c:forEach items="${searchPageData.results}" var="order">
											<tr>
												<td colspan="6" class="accOrdRowBx"><ycommerce:testId
														code="orderHistoryItem_orderDetails_link">
														<div class="table-responsive details">
															<table class="table">
																<tr class="AccrBxP">
																	<td style="width: 25%">
																		<%-- <a href="${orderDetailsUrl}${order.code}"> --%>${order.code}<!-- </a> -->
																	</td>
																	<fmt:parseDate pattern="EEE MMM d HH:mm:ss z yyyy"
																		value="${order.placed}" var="parsedDate" />
																	<td style="width: 16%"><fmt:formatDate
																			value="${parsedDate}" pattern="dd MMM yyyy" /></td>
																	<fmt:parseDate pattern="EEE MMM d HH:mm:ss z yyyy"
																		value="${order.delivered}" var="parsedDate" />
																	<td style="width: 20%"><fmt:formatDate
																			value="${parsedDate}" pattern="dd MMM yyyy" /></td>
																	<td style="width: 10%"><format:price
																			priceData="${order.savings}"
																			displayFreeForZero="false" /></td>


																	<td style="width: 20%" align="center"><spring:theme
																			code="text.account.order.status.display.${order.statusDisplay}" /></td>
																	<td style="width: 9%" class="AccrBx"><span
																		class="icon fa fa-chevron-down"> </span></td>
																</tr>
																<tr class="AccrBxS">
																	<td colspan="6" class=""><c:if
																			test="${not empty order.orderData.deliveryOrderGroups }">
																			<c:forEach
																				items="${order.orderData.deliveryOrderGroups}"
																				var="orderGroup">
																				<order:orderHistoryDetailsItem
																					order="${order.orderData}"
																					orderGroup="${orderGroup}" />
																			</c:forEach>
																		</c:if> <c:if
																			test="${not empty order.orderData.pickupOrderGroups }">
																			<c:forEach
																				items="${order.orderData.pickupOrderGroups}"
																				var="orderGroup">
																				<order:orderHistoryDetailsItem
																					order="${order.orderData}"
																					orderGroup="${orderGroup}" />
																			</c:forEach>
																		</c:if></td>
																</tr>
															</table>
														</div>
													</ycommerce:testId></td>
											</tr>
										</c:forEach>

									</tbody>
								</table>
							</div>
						</c:when>
						<c:otherwise>
							<spring:theme code="text.account.orderHistory.noOrders" />
						</c:otherwise>
					</c:choose>
				</div>



			</div>
		</div>
	</div>
</div>