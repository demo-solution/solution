<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"  %>

<ul class="languagecurrencycomponent">
	<li><footer:languageSelector languages="${languages}" currentLanguage="${currentLanguage}" /></li>
	<li><footer:currencySelector currencies="${currencies}" currentCurrency="${currentCurrency}" /></li>
</ul>

