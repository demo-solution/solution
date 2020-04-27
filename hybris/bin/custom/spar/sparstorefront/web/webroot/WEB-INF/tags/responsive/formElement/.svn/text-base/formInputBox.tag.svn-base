 
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="placeholder" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.String"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<template:errorSpanField path="${path}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
	
<c:choose>
	<c:when test="${idKey == 'register.email'}">
			<div class="col-sm-4 text-right pr0">
				<label for="${idKey}">
					<spring:theme code="${labelKey}" />
					
				</label>
			</div>
				
			<div class="col-sm-8">
				<div class="form-group">
					<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
							tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeHolderMessage}"/>
						<c:if test="${mandatory != null && mandatory == true}">
							<span class="mandatory">*</span>
						</c:if>
				</div>
			</div>
	</c:when>
	<c:when test="${idKey == 'register.lastName'}">
	<div class="col-sm-4 text-right pr0">
			<label for="${idKey}">
				<spring:theme code="${labelKey}" />
			
			</label>
			</div>
				
			<div class="col-sm-8">
			<div class="form-group">
			<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
					tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeHolderMessage}"/>
					<c:if test="${mandatory != null && mandatory == true}">
						<span class="mandatory">*</span>
						</c:if>
			</div></div>
	</c:when>
	<c:when test="${idKey == 'register.employeeCode'}">
		<div class="col-sm-4 text-right pr0">
			<label for="${idKey}">
				<spring:theme code="${labelKey}" />
				
			</label>
			</div>
			
				
			<div class="col-sm-8">
			<div class="form-group">
			<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
					tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeHolderMessage}"/>
					<c:if test="${mandatory != null && mandatory == true}">
						<span class="mandatory">*</span>
						</c:if>
			</div></div>
	</c:when>
	<c:when test="${idKey == 'register.dateOfJoining'}">
		<div class="col-sm-4 text-right pr0">
			<label for="${idKey}">
				<spring:theme code="${labelKey}" />
				
			</label>
			</div>	
				
			<div class="col-sm-8">
			<div class="form-group">
			<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
					tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="mm/dd/yyyy" readonly="true"/>
					<a class="registerDateOfJoining" href="javascript:void(0)">
							<span class="glyphicon glyphicon-calendar dateOfJoining"></span></a>
							<c:if test="${mandatory != null && mandatory == true}">
						<span class="mandatory">*</span>
						</c:if>
			</div></div>
	</c:when>
	<c:when test="${idKey == 'register.dateOfBirth'}">
			<div class="col-sm-4 text-right pr0">
				<label for="${idKey}"><spring:theme code="${labelKey}" /></label>
			</div>
				
			<div class="col-sm-8 dob-grp">
			<div class="form-group">
			<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
					tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="mm/dd/yyyy" readonly="true"/>
					<a class="registerDateBirth" href="javascript:void(0)">
         				 <span class="glyphicon glyphicon-calendar dateOfBirth"></span>
         			</a>

				<c:if test="${mandatory != null && mandatory == true}">
				<span class="mandatory">*</span>
				</c:if>
			</div></div>
	</c:when>
	
	<c:when test="${idKey == 'j_username'}">
		<%-- <div class="col-sm-4 text-right pr0">
			<label for="${idKey}">
				<spring:theme code="${labelKey}" />
			
			</label>
			</div> --%>
			
		
			<div class="col-sm-12">
			<div class="form-group">
			<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
					tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="Please Enter your Email ID"/>
						<c:if test="${mandatory != null && mandatory == true}">
						<span class="mandatory">*</span>
				</c:if>
					
			</div></div>
	</c:when>
		
	
	<c:when test="${idKey == 'address.firstName'}">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}"  style="width:100%;"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
	</c:when>
	
	<c:when test="${idKey == 'address.dateOfBirth'}">
		<div class="col-xs-12 col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-xs-12 col-sm-8">
		<div class="form-group">
		<div class='input-group date' id='datetimepicker1' onclick="javascript">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}"  readonly="true" style="width:100%;"/>
				<span class="input-group-addon dateOfBirth">
                 <span class="fa fa-calendar "></span>
            </span>
		</div>
		<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if></div></div>
	</c:when>
	
	<c:when test="${idKey == 'address.phone'}">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.phone}" style="width:100%;" maxlength="10"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
	
	<c:when test="${idKey == 'address.postcode'}">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
			<div class="form-group">
				<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
						tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.postalCode}" style="width:100%;" minlength="6" maxlength="6"/>
						<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
					<span class="info-text hide"><strong>Note:</strong> You can proceed with this Address without Pincode.</span>
			</div>
		</div>
	</c:when>
	<c:when test="${idKey == 'address.email' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${loggedInUser}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
		<c:when test="${idKey == 'address.townCity' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.town}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
	
	
	<c:when test="${ idKey == 'storelocator-query1' }">			
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding deliveryLoc">
			<div class="col-sm-4 text-right pr0"><label class="control-label " for="address.area">Location</label></div>
			<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.longAddress}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div>
		<div class="txtCol-red">
			<span id="locality_error1" style="display: none;"> 
				<spring:theme	code="storefinder.localityarea.error" />
			</span>
		</div>
		</div>		
		</div>
	</c:when>
	
	
	<c:when test="${ idKey == 'storelocator-query-reg' }">			
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 nopadding deliveryLoc">
			<div class="col-sm-4 text-right pr0"><label class="control-label " for="address.area">Location</label></div>
			<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.longAddress}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div>
		<div class="txtCol-red">
			<span id="locality_error1" style="display: none;"> 
				<spring:theme	code="storefinder.localityarea.error" />
			</span>
		</div>
		</div>		
		</div>
	</c:when>
	
	
		<c:when test="${idKey == 'address.area' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.area}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>	
	
	<c:when test="${ idKey == 'address.buildingName' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.buildingName}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
		
	<c:when test="${ idKey == 'address.line1' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.line1}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>	
	<c:when test="${ idKey == 'address.line2' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.line2}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>	
	<c:when test="${ idKey == 'address.landmark' }">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${custAdd.landmark}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>	
	<c:when test="${idKey == 'address.surname' || idKey == 'address.dateOfBirth'
		||  idKey == 'address.line1' || idKey == 'address.line2'}">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" style="width:100%;" maxlength="255"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
	
	<c:when test="${idKey == 'cnc.phone'}">
		<div class="col-sm-4 text-right pr0">
			<label class="control-label ${labelCSS}" for="${idKey}"><spring:theme code="${labelKey}" /></label>
		</div>	
		<div class="col-sm-8">
		<div class="form-group">
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeholder}" value="${primaryCNCPhone}" style="width:100%;" maxlength="10"/>
				<c:if test="${mandatory != null && mandatory == true}"><span class="mandatory">*</span></c:if>
		</div></div>
	</c:when>
		
	<c:otherwise>
		<label class="control-label ${labelCSS}" for="${idKey}">
			<spring:theme code="${labelKey}" />
			<c:if test="${mandatory != null && mandatory == false}">
				<spring:theme code="login.optional" />
			</c:if>
		</label>
			
		<spring:theme code="${placeholder}" var="placeHolderMessage" />
			
		<form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
				tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeHolderMessage}"/>
	</c:otherwise>
</c:choose>
					
	</ycommerce:testId>
</template:errorSpanField>
 