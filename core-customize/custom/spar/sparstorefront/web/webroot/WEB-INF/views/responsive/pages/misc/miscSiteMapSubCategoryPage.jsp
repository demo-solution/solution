<%@ page contentType="text/plain" language="java"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"> 
<c:forEach items="${siteMapData}" var="siteMap">
<url>
<loc>${siteMap.location}</loc>
<lastmod><fmt:formatDate pattern="yyyy-MM-dd" value="${siteMap.modifiedTime}" /></lastmod>
<changefreq>${siteMap.changefreq}</changefreq>
<priority>${siteMap.priority}</priority>
</url>
</c:forEach> 
</urlset>


