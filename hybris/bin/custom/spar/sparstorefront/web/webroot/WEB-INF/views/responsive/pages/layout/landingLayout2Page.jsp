<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart"%>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-12 col-sm-12 nopadding">
			<!--top navigation -->
			<div class="banner">
				<!-- #banner -->
				<!-- #endregion Jssor Slider End -->

				<cms:pageSlot position="Section1" var="feature">
					<cms:component component="${feature}" element="div"
						class="span-24 section1 cms_disp-img_slot" />
				</cms:pageSlot>
				<!-- #endregion Jssor Slider End -->
			</div>
		</div>
		<!-- #banner-end -->
	</div>
	<!-- added for payment banner component by pallavi-->
	<cms:pageSlot position="HomePageSparPaymentBanner" var="feature"
		element="div" class="middle-banners hidden-xs">
		<cms:component component="${feature}" />
	</cms:pageSlot>

	<div class="row">
		<cms:pageSlot position="Section3" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>

	</div>
	<div class="row">
		<cms:pageSlot position="Section3A" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>

	</div>

<!----------------------------------------SPAR PROMISE------------------------------->
	<%-- <div class="row">
   <cms:pageSlot position="HomePageSparPromiseBanner" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>
 </div> --%>
<!----------------------------------------SPAR PROMISE end----------------------->
<!----------------------------------------SPAR lANDMARK------------------------------->
	<div class="row">
		<cms:pageSlot position="HomePageSparLandmarkBanner" var="feature"
			element="div" class="landmark-wrapper ">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>
<!----------------------------------------SPAR LANDMARK end----------------------->
</template:page>