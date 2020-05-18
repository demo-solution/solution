<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="header"
	tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<c:url value="/store-finder" var="storeFinderFormAction" />
<c:url value="/setConfirmedStore" var="setStoreFormAction" />

<%-- <c:url value="/spar-store-finder" var="storeFinderFormAction" />
<c:url value="/spar-store-finder/spar-delivery-slots" var="storeDeliverySlotsAction" /> --%>


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
 <!-- text box to hold LR seesion otp value to open existing user landmark reward popup --> 
<input type="hidden" id ="otpLRSessionValue" name="otpLRSessionValue" value="<%=session.getAttribute("otpLR")%>"/>
<div class="header-bottom">
	<!--header-bottom-->

	<div class="row">
		<div class="store-finder js-store-finder"
			data-url="${storeFinderFormAction}"></div>
		<%-- <div class="store-finder js-deliverySlots-store-finder"
			data-url="${storeDeliverySlotsAction}"></div> --%>
		<!--------------------location contents--------------------------------------------------->
		<form:form action="${storeFinderFormAction}" method="GET"
			modelAttribute="storeFinderForm">
			<div class="flyout1 hidden1 BorderLocation">
				<a href="javascript:void(0)"
					class="hidden-sm hidden-md hidden-lg btn pull-right fa fa-times-circle closeLoc"></a>
				<div class="row location-search">
					<div class="row">
						<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding">
							<div class="form-group">
								<div><a href="javascript:void(0)" id="savedAddress" class="mb5" data-toggle="modal" data-target="#savedaddressbook" title="Select from existing delivery addresses">Select existing delivery addresses</a>
								Select your city to start shopping</div>
							</div>
						</div>
					</div>
					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 mb10 nopadding">
						<%-- <label class="locCityPad">
						<spring:theme code="storeFinder.city" /></label> --%>
						
						<div class="form-group">
							<select id="cityName">
								<option value="SelectValue">
									<spring:theme code="storeFinder.selectyourcity" />
								</option>
								<c:forEach items="${sparCities}" var="SparCities"
									varStatus="status">
									<option value="${SparCities.city}"
										<c:if test="${SparCities.city == 'Bengaluru'}">selected="selected"</c:if>>${SparCities.city}</option>
								</c:forEach>
							</select>
						</div>

					</div>
					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 mb10 nopadding">
						<%-- <label class="locCityPad">
						<spring:theme code="storeFinder.localityarea" /></label>  --%>
						<div class="form-group">
							<input type="text" id="storelocator-query"
								placeholder="Enter order delivery area" autocomplete="on"
								class="form-control" size="30" />

						</div>
						<div class="txtCol-red">
							<span id="locality_error" style="display: none;"> <spring:theme
									code="storefinder.localityarea.error" />
							</span>
						</div>
					</div>
					
					<!-- <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleApiKey}&libraries=places"></script> -->
					<!-- <script type="text/javascript">
						function initialize() {
				            var input = document.getElementById('storelocator-query');
				            var southWest = new google.maps.LatLng(12.975821448855658, 77.59847820229163);
				            var northEast = new google.maps.LatLng(12.977366551144343, 77.60006379770846);
				            var bangaloreBounds = new google.maps.LatLngBounds(southWest, northEast);
				            var options = {
				              bounds : bangaloreBounds,
				              componentRestrictions : {
					              country : "in"
				              }
				            };
	           				 var autocomplete = new google.maps.places.Autocomplete(input, options);
           				 }
           				google.maps.event.addDomListener(window, 'load', initialize);
					</script> -->
					<div class="row noStoreData">
						<div>
							<spring:theme code="storeFinder.nodata.available" />
							<%-- <img class="pull-right"
							src="${commonResourcePath}/images/homeDeli.png"
							alt="home delivery"> --%>
						</div>
					</div>
					<div class="btn-block form-group">
						<!-- <a href="javascript:void(0)" type="button" class="btn btn-danger searchLoc  " > SEARCH</a> -->
						<button type="button" id="findStoresNearMe"
							class="btn  searchLoc ">Submit</button>
						<!-- <input type="submit" class="btn searchLoc" id="confirmStore"
							value="CONFIRM" /> -->
						<!-- <input type="submit" value="search"/> -->
						<a href="javascript:void(0)" "type="button" class="btn closeLoc  ">Skip</a>
					</div>
				</div>
			</div>
		</form:form>
		<!------------Location Search page--------------->
		<form:form action="${setStoreFormAction}" method="POST"
			modelAttribute="homePageForm">
			<input type="hidden" name="deliveryType" value="STORE" />
			<input type="hidden" name="deliveryCityName" value="" />
			<div class="flyout2 hidden2 ">
				<div class="row noshow">
					<div class="col-xs-6 text-right">
						<label class="locCityPad locLabel"><spring:theme
								code="storeFinder.city" /></label> <input type="text" id="selectedCity"
							value="">
					</div>
					<div class="col-xs-6">
						<label class="locAreaPad locLabel"><spring:theme
								code="storeFinder.localityarea" /></label> <input type="text"
							id="locArea" value="">
					</div>
					<button type="button" id="findStoresNearMe" class="btn  searchLoc ">
						<spring:theme code="SEARCH" />
					</button>
				</div>
				<div id="map"></div>
				<div class="locContainer mb10">
					<!--Location contain-->
					<div class="row">
						<div class="col-xs-12 homeDeli p5">
							<span class="locLabel "> <input type="hidden" data="HD"
								name="store" id="homedelivery"> <%-- <spring:theme code="storeFinder.homedelivery" /> --%>
							</span>
							<div id="homedeltextchng">
								<span class="locGreentxt full-width"> <spring:theme
										code="storeFinder.deliverlocality" />
								</span> <span class="locGreytxt"><spring:theme
										code="storeFinder.nextdelivery" /><span id="hdSlot"></span></span> <span
									class="locGreentxt noAvailable"><spring:theme
										code="storeFinder.homedelivery.notpossible" /></span>
							</div>
							<%-- <img class="pull-right"
								src="${commonResourcePath}/images/homeDeli.png"
								alt="home delivery"> --%>

						</div>
					</div>			

				</div>
				<!--Location contain close-->


				<%-- <form:radiobutton path="store" value="" /> --%>
				<div class="row">
					<div
						class="col-xs-12 col-sm-12 col-md-12 col-lg-12 mb10 nopadding bottonColumn hidden">

						<input type="submit" class="btn searchLoc" id="confirmStore" value="CONFIRM" />			

						<a href="javascript:void(0)" type="button"
							class="btn hidden-xs closeLoc  "><spring:theme
								code="storeFinder.close" /></a>

					</div>
					<div class="Note_text">
						<spring:theme code="basket.validation.cartTotalValueChange.text" />
					</div>
				</div>

			</div>
		</form:form>
		<!--------------------location Search end------------------------------------------------------------------------------->
	</div>
	<!-- <div class="container">container  -->
	<div class="row">
		<div class="col-md-12 col-sm-12 label-location">SELECT YOUR CITY</div>
		<div class="search col-md-3 col-sm-4 nopadding">
			<div class="col-md-10 col-sm-10 nopadding">
				<form:form id="storeForm" name="storeForm"
					action="${request.contextPath}/setStore" method="post"
					modelAttribute="homePageForm">
					<%--  <form:input path="store" class="form-control col-sm-10" onchange="javascript:setStore();"/> --%>
					<form:input path="deliveryCityName" class="form-control col-sm-10"
						onchange="javascript:setStore();" />					
				</form:form>
			</div>
			<div class="col-md-2 col-sm-2 nopadding locatorBtn">
				<button id="menu1" data-target="#location" data-toggle="collapse"
					class="btn btn-danger collapsed nomargin pos-search" type="button"
					aria-expanded="false">
					<i class="fa fa-map-marker"></i>
				</button>
			</div>
		</div>
		<!--Location contain close-->
		<!-- Change end here for html pages -->
		<div class="col-md-8 col-sm-6 nopadding">
			<cms:pageSlot position="SearchBox" var="component" element="div"
				class="productsearch col-sm-12 nopadding">
				<cms:component component="${component}" element="div" />
			</cms:pageSlot>
		</div>
		<div class="col-md-1 col-sm-2 cart-block nopadding">
			<!-- <button style="width: 100%;padding: 6px 0px;font-size: 15px;font-weight: normal;text-align: left;" class="btn btn-primary nomargin" type="button"><i style="padding:0 0 0 15px; font-size:17px; padding-right:10px;" class="fa fa-shopping-cart pull-left"></i>CART
                                                  <span style="margin-left:15px" class="badge">7</span></button> -->

			<cms:pageSlot position="MiniCart" var="component" element="div">
				<cms:component component="${component}" element="div" />
			</cms:pageSlot>
		</div>
	</div>
	<!-- 	</div>/container    -->
</div>
<div class="row top-navigation">
	<div class="col-md-3 col-sm-4 col-xs-12 nopadding">
		<div class="block block-vertical-menu">
			<div class="vertical-head">
				<h5 class="vertical-title">SHOP NOW</h5>
			</div>
			<div class="block-vertical-menu-list">
				<nav:topNavigation />
				<div class="more-cat">
					<i class="fa fa-plus" aria-hidden="true"></i><span class="vimor">VIEW
						MORE</span>
				</div>
			</div>
		</div>
	</div>

	<div class="col-md-9 col-sm-8 col-xs-12 nopadding category">
		<cms:pageSlot position="NavigationBar2" var="component">
			<cms:component component="${component}" />
		</cms:pageSlot>
	</div>

</div>
</div>