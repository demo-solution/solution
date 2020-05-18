<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url value="/search/" var="searchUrl" />
<c:url value="/search/autocomplete/${component.uid}"
	var="autocompleteUrl" />
<form name="search_form_${component.uid}" method="get"
	action="${searchUrl}" class="search_form">
	<div class="col-sm-10 col-xs-11 nopadding">
		<spring:theme code="search.placeholder" var="searchPlaceholder" />
		<ycommerce:testId code="header_search_input">
			<!-- <i class="fa fa-bars"></i> -->
			<input type="text" id="js-site-search-input"
				class="form-control js-site-search-input" name="text" value=""
				maxlength="100" placeholder="${searchPlaceholder}"
				data-options='{"autocompleteUrl" : "${autocompleteUrl}","minCharactersBeforeRequest" : "${component.minCharactersBeforeRequest}","waitTimeBeforeRequest" : "${component.waitTimeBeforeRequest}","displayProductImages" : ${component.displayProductImages}}'>
		</ycommerce:testId>
	</div>
	<div class="col-sm-2 col-xs-1 nopadding">
		<span class="input-group-btn"> <ycommerce:testId
				code="header_search_button">
				<button class="btn btn-danger collapsed nomargin" type="submit">Search &nbsp;<span class="glyphicon glyphicon-search"></span></button>
			</ycommerce:testId>
		</span>
	</div>
</form>



