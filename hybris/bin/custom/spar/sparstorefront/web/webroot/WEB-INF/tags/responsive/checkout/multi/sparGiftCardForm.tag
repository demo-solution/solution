<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div class="row">
 	<div class="col-lg-4 nopadding mb10">
		<input type="text" class="form-control card-number" placeholder="Card Number">		
   </div>
   <div class="col-lg-4 nopadding mb10">
		<input type="text" class="form-control pin-code" placeholder="Pin Code">		
   </div>
   <div class="col-lg-4 nopadding mb10">
		<input type="text" class="form-control card-amount" placeholder="Amount">		
   </div>
    <span class="input-group-btn">
		<button class="btn btn-green" type="button">Validate e-gift card details and amount</button>								      
   </span>
</div>
