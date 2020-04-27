<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<%--  AddOn Common CSS files --%>
<c:forEach items="${addOnCommonCssPaths}" var="addOnCommonCss">
	<link rel="stylesheet" type="text/css" media="all" href="${addOnCommonCss}"/>
</c:forEach>

<%-- SPAR CSS files START--%>
<%-- <link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/style-back.css"/> --%>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/slider.css"/>
<%-- <link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/menu_style.css"/> --%>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/font-awesome.min.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/master.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/miniCart.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/header.css"/>
<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/jquery-ui/jquery-ui.css"/>
<%-- SPAR CSS files END--%>

<link rel="stylesheet" type="text/css" media="all" href="${themeResourcePath}/css/jquery.jscrollpane.css"/>
<%--  AddOn Theme CSS files --%>
<c:forEach items="${addOnThemeCssPaths}" var="addOnThemeCss">
	<link rel="stylesheet" type="text/css" media="all" href="${addOnThemeCss}"/>
</c:forEach>