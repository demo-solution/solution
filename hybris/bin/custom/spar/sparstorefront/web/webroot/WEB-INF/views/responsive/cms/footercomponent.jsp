<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="footer"
	tagdir="/WEB-INF/tags/responsive/common/footer"%>
<c:forEach items="${navigationNodes}" var="node">
	<c:if test="${node.visible}">
		<c:forEach items="${node.links}" step="${component.wrapAfter}"
			varStatus="i">
			<c:forEach items="${node.links}" var="childlink" begin="${i.index}"
				end="${i.index + component.wrapAfter - 1}">
				<cms:component component="${childlink}" evaluateRestriction="true"
					element="li" class="list navbar-text" />
			</c:forEach>
		</c:forEach>
	</c:if>
</c:forEach>
</div>
<c:if test="${showLanguageCurrency}">
	<div class="pull-right">
		<footer:languageSelector languages="${languages}"
			currentLanguage="${currentLanguage}" />
		<footer:currencySelector currencies="${currencies}"
			currentCurrency="${currentCurrency}" />
	</div>
</c:if>
<div class="col-md-12 col-xs-12 col-sm-12 container-fluid blockGreen grayBlock">${notice}</div>
</div>