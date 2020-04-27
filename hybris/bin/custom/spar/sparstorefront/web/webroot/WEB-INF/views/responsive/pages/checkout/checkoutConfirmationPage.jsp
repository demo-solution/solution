<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%-- <%@ page import="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%> --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

<div class="row mt20"><!--container--> 
   
<!-- Google Code for SPAR Sale Conversion Page -->
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 846392487;
var google_conversion_language = "en";
var google_conversion_format = "3";
var google_conversion_color = "ffffff";
var google_conversion_label = "EoyqCJCH6HIQp9nLkwM";
var google_remarketing_only = false;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/846392487/?label=EoyqCJCH6HIQp9nLkwM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>


   <!--breadcrums-->
<!-- <ul class="breadcrumb breadcrumbText">
    <li><a href="#"> >> Home</a></li>
    <li><a href="#">Contact Us</a></li>      
  </ul> ---> 

 <div class="col-md-12 bg-staticOrder nopadding">
   
   		<div class="row"><!--mainrow-->    
       			
                <div class="check-top-bar1 col-md-12">
                    <h4><spring:theme code="checkout.orderConfirmation.success" /> </h4>
                </div>
                <div class="col-md-9 margTop OrderMsg pl50">
                	<p> <spring:theme code="checkout.orderConfirmation.thankYouForOrder" /> </p> 
                    
                </div>
                <div class="col-md-3 margTop">
                	<span><b>For Further assistance:</b></span> 
                    <h6> 18605009418 </br>
                    <spring:theme code="checkout.orderConfirmation.assistanceemailid" /> </h6>
                </div>
         </div>   <!--mainrow--> 
          
           
           <div class=" row bg-staticInside order-details" >
                
                    <div class="check-top-bar1 col-md-12 ">
                        <h4 class="color1">ORDER DETAILS</h4>
                    </div>
                    <div class="col-sm-2 margTop">
                    <p class="txtUnderline">ORDER NO </p>
                    <h5 class="order-no">${orderData.code}</h5>
                    </div>
                	<div class="col-sm-3 margTop">
                    <p class="txtUnderline">Delivery Date & Time</p>
                     <h5 class="order-date"><fmt:formatDate type="date" value="${orderData.slotDeliveryDate}" /> 
                     <br> Btw ${orderData.orderDeliverySlot.slotDescription}</h5>
                  
                    </div>
                    <div class="col-sm-2 margTop">
                    <p class="txtUnderline">TOTAL ITEM </p>
                    
                    <h5 class="order-item">
                   <c:if test="${not empty orderData.deliveryOrderGroups }"> 
	                   <c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
							<order:orderTotalItem order="${orderData}" orderGroup="${orderGroup}" />
						</c:forEach>
					</c:if>
					<c:if test="${not empty orderData.pickupOrderGroups }">
						<c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
							<order:orderTotalItem order="${orderData}" orderGroup="${orderGroup}" />
						</c:forEach> 
					</c:if>
					</h5>
							
                    </div>
                    <c:if test="${not empty orderData.voucherValue && orderData.voucherValue.value ne '0.0'}">	
	                    <div class="col-sm-3 margTop">
		                    <p class="txtUnderline">Voucher Description</p>                 	
		                    <h5 class="order-voucher"><%-- <c:forEach items="${orderData.appliedVoucherTotal}" var="voucherValue"> --%>                      				 
									Amount:- <format:price priceData="${orderData.voucherValue}"/>					
								<%-- </c:forEach> --%>
								<br>Voucher Code:- ${orderData.voucherCode}</h5>
	                    </div>
                    </c:if> 
                    
                    <div class="col-sm-2 margTop">
                    <p class="txtUnderline">TOTAL AMOUNT </p>
                    <h5 class="order-amount">	<order:OrderTotalPrice order="${orderData}"/> </h5>
                    </div>
                    
                    </div>
                    
                    
                    <!-- <div class="col-sm-2 margTop">
                    <p class="txtUnderline">YOU SAVED </p>
                    <h5> </h5>
                    </div> -->
            
            <c:set value="0.0" var="priceValue"/>
           <div class=" row bg-staticInside" >    
                    <div class="check-top-bar1 col-md-12 ">
                        <h4 class="color1">PAYMENT & ADDRESS</h4>
                    </div>
                	<div class="col-sm-3 margTop">
                    	<p class="txtUnderline">MODE OF PAYMENT </p>
                    	<%-- <c:choose>
                    		<c:when test="${orderData.totalPrice.value eq priceValue and not empty orderData.voucherCode}">
                    			<h5>NA</h5>
                    		</c:when>
                    		<c:otherwise> --%>
                    			<h5><order:paymentMethodItem order="${orderData}"/> </h5>
                    		<%-- </c:otherwise>
                    	</c:choose> --%>
                    </div>
                	<div class="col-sm-3 margTop">
                    	<p class="txtUnderline">TOTAL PAYABLE AMOUNT </p>
                    		<input type="hidden" value="${orderData.totalPrice.value}" id="orderTotalValue">  
                    	<h5><order:OrderTotalPrice order="${orderData}"/> </h5>
                    </div>
                    <div class="col-sm-3 margTop">
                    <p class="txtUnderline">DELIVERY ADDRESS </p>
                    	<h5><order:deliveryAddressItem order="${orderData}"/></h5>
                   
                    </div>
            </div>
           
           <!-- <div class="col-sm-9 marg-btm">
           <p>Forgot to order something? Place an ADD-ON order. <a href="#"> click here </a> </p>
           </div> -->
           <div class="row"> <div class="col-sm-3 pull-right marg-btm">
           <!-- <a  href="#" class="btn btn-danger pull-right">CONTINUE SHOPPING</a> -->
           <a href="${request.contextPath}" class="btn btn-danger pull-right"><spring:theme code="checkout.orderConfirmation.continueShopping" /></a>
           </div></div>
           
           
    </div>  <!--bg-staticOrder-->
       
 
<!-------------------------------- footer -------------------------------------->


<!-- footer1 End -->

</div>



</body>
</html>
</template:page>