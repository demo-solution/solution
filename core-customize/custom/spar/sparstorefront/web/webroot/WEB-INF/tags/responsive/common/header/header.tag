<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header"	tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<%-- Test if the UiExperience is currently overriden and we should show the UiExperience prompt --%>
<c:if
	test="${uiExperienceOverride and not sessionScope.hideUiExperienceLevelOverridePrompt}">
	<c:url value="/_s/ui-experience?level="
		var="clearUiExperienceLevelOverrideUrl" />
	<c:url value="/_s/ui-experience-level-prompt?hide=true"
		var="stayOnDesktopStoreUrl" />
	<div class="backToMobileStore">
		<a href="${clearUiExperienceLevelOverrideUrl}"><span
			class="greyDot">&lt;</span> <spring:theme
				code="text.swithToMobileStore" /></a> <span class="greyDot closeDot"><a
			href="${stayOnDesktopStoreUrl}">x</a></span>
	</div>
</c:if>

<div id="loader">
	<img id="loading-image"
		src="${commonResourcePath}/images/loader_blue.gif"
		style="display: none;">
</div>
<div class="container">
	<!--container-->

	<div class="row mt10 header_top">
		<div class="col-md-12 col-sm-12 col-xs-12 nopadding mb24">
			<div class="fa fa-bars visible-xs hidden-sm hidden-md col-xs-2"></div>
			<!--header_top-->
			<div class="col-md-3 col-sm-3 col-xs-5 nopadding">
				<div class="logo pull-left">
					<cms:pageSlot position="SiteLogo" var="logo" limit="1">
						<cms:component component="${logo}" class="siteLogo" element="div" />
					</cms:pageSlot>
				</div>
			</div>

			<div class="col-md-9 col-sm-9 col-xs-4 nopadding top-links">
				<div class="shop-menu pull-right">
					<a href="#" class="toggle-menu hidden-sm hidden-md hidden-lg">
						<span class="hidden-sm img-icons login-icon"></span>
					</a>					
					<ul class="nav navbar-nav">
						<!-- <li class="hidden-xs"><a href="http://www.sparindia.org.in/corporategifting" target="_blank">Corporate Gifting </a></li> -->
						<cms:pageSlot position="HeaderLinks" var="link">
							<cms:component component="${link}" element="li" class="contact">
								<span class="img-icons call-icon"> </span>
							</cms:component>
						</cms:pageSlot>
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
							<c:set var="maxNumberChars" value="25" />
							<c:if test="${fn:length(user.firstName) gt maxNumberChars}">
								<c:set target="${user}" property="firstName"
									value="${fn:substring(user.firstName, 0, maxNumberChars)}..." />
							</c:if>
							<li class="logged_in">
								<div class="img-icons login-icon">
									<ycommerce:testId code="header_LoggedUser">
										<spring:theme code="header.welcome"
											arguments="${user.firstName},${user.lastName}"
											htmlEscape="true" />
									</ycommerce:testId>
								</div>
							</li>
						</sec:authorize>
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
							<li><ycommerce:testId code="header_myAccount">
									<a href="<c:url value='/my-account'/>"><span class="myaccount"></span><spring:theme
											code="header.link.account" /></a>
								</ycommerce:testId></li>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
							<li><ycommerce:testId code="header_Login_link">
									<a href="<c:url value="/login"/>"> <span
										class="hidden-sm img-icons login-icon"></span> <spring:theme
											code="header.link.login" /></a>
								</ycommerce:testId></li>
						</sec:authorize>
							<c:if test="${empty hideHeaderLinks}">
							<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
								<li class="signOutBtn"><ycommerce:testId
										code="header_signOut">
										<a href="<c:url value='/logout'/>"><spring:theme
												code="header.link.logout" /></a>
									</ycommerce:testId></li>
							</sec:authorize>
						</c:if>						
					</ul>
					<!-- <a href="http://www.sparindia.org.in/corporategifting" class="hidden-sm hidden-md hidden-lg" target="_blank">
						<span class="hidden-sm img-icons giftzone-icon"></span>
					</a> -->
					<!-- <div class="help-support">
						<a href="#" class="hidden-sm hidden-md hidden-lg"
							data-toggle="modal" data-target="#helpSupport"> <span
							class="need-help-icon"></span>
						</a>
					</div> -->
				</div>
			</div>
		</div>
	</div>
	<!--/container-->