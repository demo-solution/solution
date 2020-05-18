<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
	
<template:page pageTitle="${pageTitle}">

<cms:pageSlot position="Section3" var="comp" element="div">
	<cms:component component="${comp}"/>
</cms:pageSlot>

</template:page>