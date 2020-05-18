<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="lr" tagdir="/WEB-INF/tags/responsive/landmarkReward"%>
<%@ taglib prefix="savedAddress" tagdir="/WEB-INF/tags/responsive/location-existing-address"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>

	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>

	<jsp:body> 
		<lr:landmarkrewardpopup />
		<savedAddress:location-exiting-address />
		<header id="header">
			<header:header hideHeaderLinks="${hideHeaderLinks}" />
         <header:secondHeader hideHeaderLinks="${hideHeaderLinks}"/>
		</header>
			
			<a id="skip-to-content"></a>
		
			<div class="content-wrapper container overflow">
				<common:globalMessages />
				<cart:cartRestoration />
				<jsp:doBody />				
				<footer:footer />
			</div>

		</main>

	</jsp:body>

</template:master>
