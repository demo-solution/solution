<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses"/>
<span>


	
     <span class="sideMenuIcon"><img src="${component.navigationNode.previewIcon.url}" /></span>

    <cms:component component="${component.link}" evaluateRestriction="true" class="parent" element="div"/> 
    <span class="rightArow hidden-xs"></span>
    

<c:if test="${not empty component.navigationNode.children}">
	<div class="menu-back-containter">       
         <div class="vertical-dropdown-menu">
			<div class="row">
				<div class="col-sm-12" >
						<ul class="v-menu-list">
							<c:forEach items="${component.navigationNode.children}" var="child">
							<c:if test="${child.visible}">
							<c:forEach items="${child.links}" step="${component.wrapAfter}" varStatus="i"><li>
										<ul class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}">
										<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
		          						<cms:component component="${childlink}" evaluateRestriction="true" element="li"/>
		          						</c:forEach>
		          						</ul>
		          					</c:forEach>    
							<c:if test="${not empty child.children}">
						<ul class="v-menu-Sublist">
						<c:forEach items="${child.children}" var="child1">
						<li>
								<span class="rightborderblocker"></span>					
								<div class="vertical-dropdown-submenu">         							
									<c:forEach items="${child1.links}" step="${component.wrapAfter}" varStatus="j">
										<ul class="accordion Lc ${j.count < 2 ? 'left_col' : 'right_col'}">
										<c:forEach items="${child1.links}" var="childlink1" begin="${j.index}" end="${j.index + component.wrapAfter - 1}">
		          						<cms:component component="${childlink1}" evaluateRestriction="true" element="li"/>
		          						</c:forEach>
		          						</ul>
		          					</c:forEach>                           				
		          				</div>
		          				
		          			</li>
		          			
						</c:forEach>
						</li>
						</ul>
							</c:if>
							<%--  <h4 class= sub-menu-head"> ${child.title}</h4> --%>
							<%-- <li><a href="#"><span class="nav-submenu-title">${child.title}</span></a>
								<span class="rightborderblocker"></span>					
								<div class="vertical-dropdown-submenu">         							
									<c:forEach items="${child.links}" step="${component.wrapAfter}" varStatus="i">
										<ul class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}">
										<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
		          						<cms:component component="${childlink}" evaluateRestriction="true" element="li"/>
		          						</c:forEach>
		          						</ul>
		          					</c:forEach>                           				
		          				</div>
		          			</li> --%>
		          			</c:if>
		             		</c:forEach>
		             	</ul>
		             </div>
				</div>
			</div>
		</div>
    </c:if>
      
<%-- <c:if test="${not empty component.navigationNode.children}">
	<div class="menu-back-containter">       
         <div style="width:200px;" class="vertical-dropdown-menu">
			<div class="row">
				<div class="col-sm-12" >
					<div class="box">
						<ul class="v-menu-list">
							<c:forEach items="${component.navigationNode.children}" var="child">
							<c:if test="${child.visible}">
							
							 <h4 class="sub-menu-head"> ${child.title}</h4>
							<li><a href="#"><span class="nav-submenu-title">${child.title}</span></a>
								<span class="rightborderblocker"></span>					
								<div class="vertical-dropdown-submenu">         							
									<c:forEach items="${child.links}" step="${component.wrapAfter}" varStatus="i">
										<ul class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}">
										<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
		          						<cms:component component="${childlink}" evaluateRestriction="true" element="li"/>
		          						</c:forEach>
		          						</ul>
		          					</c:forEach>                           				
		          				</div>
		          			</li>
		          			</c:if>
		             		</c:forEach>
		             	</ul>
		             </div>
				</div>
			</div>
		</div>
    </c:if> --%>