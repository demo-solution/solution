<%@ page contentType="text/plain" language="java"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"> 
<c:forEach items="${siteMapData}" var="siteMap">
<sitemap>
<loc>${siteMap.location}</loc>
<lastmod><fmt:formatDate pattern="yyyy-MM-dd" value="${siteMap.modifiedTime}" /></lastmod>
<changefreq>${siteMap.changefreq}</changefreq>
<priority>${siteMap.priority}</priority>
</sitemap>
</c:forEach> 
</sitemapindex>


