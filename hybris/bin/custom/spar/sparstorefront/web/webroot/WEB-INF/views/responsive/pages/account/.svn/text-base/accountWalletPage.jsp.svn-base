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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
			<div class="row bg-static wallet-wrapper">
				<div class="col-sm-12 nopadding">
					<h4>
						<%-- <spring:theme code="text.account.order.yourOrder" /> --%>
						
						<spring:theme code="text.account.wallet.caps" />
					</h4>
				</div>
				<div class="col-sm-12 p5">
					<c:choose>
						<c:when test="${fn:length(customerData.walletDetails)>0}">
							<div class="accounHistory table-responsive p10">
							
								<!-- My Wallet Start Here  -->
								<div class="balance"><spring:theme code="text.account.wallet.available.balance"/><format:price priceData="${customerData.paidByWallet}"/>  </div>								
								<table class="table wallet">
									<thead class="tableHead">
										<tr class="tableHeadText">
											<th><spring:theme code="text.account.wallet.order" /></th>
											<th>	<spring:theme code="text.account.wallet.refund.amount" /></th>
											<th>	<spring:theme code="text.account.wallet.refund.reason" /></th>
											<th>	<spring:theme code="text.account.wallet.refund.date" /></th>
										</tr>
									</thead>
									<tbody>
							
								<c:forEach items="${customerData.walletDetails}" var="walletDetails" varStatus="idx">
										<tr class="AccrBxP">
											<td class="col">${walletDetails.walletOrder}</td>
											<td class="col"><format:price priceData="${walletDetails.walletAmount}"/></td>
											<td class="col">${walletDetails.walletFundingReason}</td>
											<td class="col">
												<fmt:formatDate value="${walletDetails.refundDate}" pattern="dd MMM yyyy" />
											</td>
										</tr>
								</c:forEach>	
								
								<c:forEach items="${searchPageData.results}" var="order" varStatus="idx">
								
									<c:if test="${order.paidByWallet.value > 0}">
										<tr class="AccrBxP">
											<td class="col">${order.code}</td>
											<td class="col"><format:price priceData="${order.paidByWallet}"/></td>
											<td class="col"><spring:theme code="text.account.wallet.claim" /></td>
											<fmt:parseDate pattern="EEE MMM d HH:mm:ss z yyyy"
																		value="${order.placed}" var="parsedDate" />
											<td class="col"><fmt:formatDate
																			value="${parsedDate}" pattern="dd MMM yyyy" /></td>
											
										</tr>
										</c:if>
								</c:forEach>
								</tbody>
								</table>
								<!-- My Wallet End Here  -->
								
							</div>
						</c:when>
						<c:otherwise>
							<spring:theme code="text.account.wallet.no.transaction" />
						</c:otherwise>
					</c:choose>
				</div>



			</div>
		</div>
	</div>
</div>