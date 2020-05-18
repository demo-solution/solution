/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.v2.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.paytm.pg.merchant.CheckSumServiceHelper;
import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.dto.ChecksumRequestParamWsDTO;
import com.spar.hcl.dto.ChecksumWsDTO;
import com.spar.hcl.dto.SparOrderTrackingWsDTO;
import com.spar.hcl.exceptions.NoCheckoutCartException;
import com.spar.hcl.exceptions.PaymentAuthorizationException;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.order.data.PaymentModeData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.plms.DTO.PLMSShortPickResponseDTO;
import com.spar.hcl.plms.DTO.SparPlmsOrderWsDTO;
import com.spar.hcl.plms.DTO.SparPlmsProductWsDTO;
import com.spar.hcl.stock.CommerceStockFacade;
import com.spar.hcl.strategies.OrderCodeIdentificationStrategy;
import com.spar.hcl.v2.helper.OrdersHelper;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;


/**
 * Web Service Controller for the ORDERS resource. Most methods check orders of the user. Methods require authentication
 * and are restricted to https channel.
 *
 * @pathparam code Order GUID (Globally Unique Identifier) or order CODE
 * @pathparam userId User identifier or one of the literals below :
 *            <ul>
 *            <li>'current' for currently authenticated user</li>
 *            <li>'anonymous' for anonymous user</li>
 *            </ul>
 */
@Controller
@RequestMapping(value = "/{baseSiteId}")
public class OrdersController extends BaseCommerceController
{
	private final static Logger LOG = Logger.getLogger(OrdersController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "orderCodeIdentificationStrategy")
	private OrderCodeIdentificationStrategy orderCodeIdentificationStrategy;
	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;
	//TODO avoid using following services
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;
	@Resource(name = "cartService")
	private CartService cartService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Resource(name = "ordersHelper")
	private OrdersHelper ordersHelper;
	@Resource(name = "pointOfServiceValidator")
	private Validator pointOfServiceValidator;
	@Autowired
	private ModelService modelService;
	@Autowired
	PriceDataFactory priceDataFactory;
	@Autowired
	CommonI18NService commonI18NService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private SparPlmsService sparPlmsService;

	@Autowired
	private CalculationService calculationService;
	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;
	//@Resource(name = "sparCheckoutFacade")
	//private SparDefaultCheckoutFacade sparCheckoutFacade;
	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparPaymentFacade;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;
	@Resource(name = "voucherService")
	private VoucherService voucherService;
	@Resource(name = "voucherModelService")
	private VoucherModelService voucherModelService;
	@Resource(name = "checkoutFlowFacade")
	private CheckoutFlowFacade checkoutFlowFacade;

	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String IPG_REDIRECT_URL_ERROR = REDIRECT_PREFIX + "/checkout/multi/ipg/error";
	private static final String ORDER_NO_INVALID = "Order No is Invalid";
	private static final String STATUS_INVALID = "Status is Invalid";
	private static final String PRODUCT_INVALID = "Product ID  is Invalid";
	private static final String ORDER_SUCCESSFUL = "Order Update is successfull";
	private static final String ORDER_UNSUCCESSFUL_MESSAGE = "ORDERNOERROR";
	private static final String STATUS_UNSUCCESSFUL_MESSAGE = "STATUSERROR";
	private static final String SHP_CSP_INVALID = "Shortpick or CSP is Invalid for Product ID :";
	private static final String CSP_INVALID = "Invalid value for CSP";
	private static final String SHP_INVALID = "Invalid value for Shortpick";
	private static final String LIMIT_CSP_CHANGE = "LIMIT_CSP_CHANGE";
	private static final String JSON_INVALID = "INVALIDJSON";

	private static final String JSON_INVALID_MESSAGE = "JSON Parameter is invalid";
	private static final String NO_ERROR = "NOERROR";
	private static final String PRODUCT_ERROR = "PRODUCTERROR";
	private static final String CSP_ERROR = "CSPERROR";
	private static final String SHORTPICK_ERROR = "SHORTPICKERROR";
	private static final String SHP_CSP_INVALID_REASON = "SHORTPICKORCSPERROR";
	private static final String ORDER_CALCULATION = "Order Calculation Exception";
	private static final String ORDER_CALCULATION_REASON = "ORDER_CALCULATION_ERROR";
	private static final long ZERO = 0;
	private static final long ONE = 1;

	boolean flag = false;
	boolean giftProductFlag = false;
	String giftProductCode = "";
	private static String PAYTM_PAYMENT_MODE = "PayTM";


	/**
	 *
	 * Returns details of a specific order based on order GUID (Globally Unique Identifier) or order CODE. The response
	 * contains a detailed order information.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Order data
	 */
	//@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getOrder',#code,#fields)")
	@ResponseBody
	public OrderWsDTO getOrder(@PathVariable final String code, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		OrderData orderData;
		if (orderCodeIdentificationStrategy.isID(code))
		{
			orderData = orderFacade.getOrderDetailsForGUID(code);
		}
		else
		{
			//orderData = orderFacade.getOrderDetailsForCodeWithoutUser(code);
			final Errors errors = new BeanPropertyBindingResult(code, "code");
			errors.reject("guid.invalid");
			throw new WebserviceValidationException(errors);
		}

		return dataMapper.map(orderData, OrderWsDTO.class, fields);
	}

	/**
	 * Returns specific order details based on a specific order code. The response contains detailed order information.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Order data
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'getOrderForUserByCode',#code,#fields)")
	@ResponseBody
	public OrderWsDTO getOrderForUserByCode(@PathVariable final String code,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		final OrderWsDTO dto = dataMapper.map(orderData, OrderWsDTO.class, fields);
		return dto;
	}

	/**
	 * Returns order history data for all orders placed by the specific user for the specific base store. Response
	 * contains orders search result displayed in several pages if needed.
	 *
	 * @queryparam statuses Filters only certain order statuses. It means: statuses=CANCELLED,CHECKED_VALID would only
	 *             return orders with status CANCELLED or CHECKED_VALID.
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam sort Sorting method applied to the return results.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Order history data.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.GET)
	@ResponseBody
	public OrderHistoryListWsDTO getOrdersForUser(@RequestParam(required = false) final String statuses,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false) final String sort, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			final HttpServletResponse response)
	{
		validateStatusesEnumValue(statuses);
		final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(statuses, currentPage, pageSize, sort,
				addPaginationField(fields));

		// X-Total-Count header
		setTotalCountHeader(response, orderHistoryList.getPagination());

		return orderHistoryList;
	}

	/**
	 * Returns {@value com.spar.hcl.v2.controller.BaseController#HEADER_TOTAL_COUNT} header with a total number of
	 * results (orders history for all orders placed by the specific user for the specific base store).
	 *
	 * @queryparam statuses Filters only certain order statuses. It means: statuses=CANCELLED,CHECKED_VALID would only
	 *             return orders with status CANCELLED or CHECKED_VALID.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.HEAD)
	@ResponseBody
	public void getCountOrdersForUser(@RequestParam(required = false) final String statuses, final HttpServletResponse response)
	{
		final OrderHistoriesData orderHistoriesData = ordersHelper.searchOrderHistory(statuses, 0, 1, null);

		setTotalCountHeader(response, orderHistoriesData.getPagination());
	}

	/**
	 * Authorizes cart and places the order. Response contains the new order data.
	 *
	 * @formparam cartId Cart code for logged in user, cart GUID for guest checkout
	 * @formparam securityCode CCV security code.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Created order data
	 * @throws PaymentAuthorizationException
	 *            When there are problems with the payment authorization. For example: there is no session cart or no
	 *            payment information set for the cart.
	 * @throws InvalidCartException
	 * @throws WebserviceValidationException
	 *            When the cart is not filled properly (e. g. delivery mode is not set, payment method is not set)
	 * @throws ProductLowStockException
	 *            When product is out of stock in store
	 * @throws StockSystemException
	 *            When there is no information about stock for stores.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public OrderWsDTO placeOrder(@PathVariable final String userId, @PathVariable final String baseSiteId,
			@RequestParam(required = true) final String orderId, @RequestParam(required = false) final String securityCode,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletRequest request)
			throws PaymentAuthorizationException, InvalidCartException, WebserviceValidationException, NoCheckoutCartException
	{

		/* Payment custom spar code - Start */
		final Map<String, String> resultMap = getRequestParameterMap(request);
		final CartData cartData = fetchCartSessionCart(userId, baseSiteId, orderId);
		PaymentSubscriptionResultData paymentSubscriptionResultData = new PaymentSubscriptionResultData();
		String paymentMode = resultMap.get("paymentMode");
		if(null != paymentMode && paymentMode.equalsIgnoreCase(PAYTM_PAYMENT_MODE))
		{
			paymentSubscriptionResultData = this.getSparDefaultPaymentFacade().completePayTMCreateSubscription(resultMap, cartData);
			if (paymentSubscriptionResultData.isSuccess())
			{
				doHandlePayTMSuccessResponse(paymentSubscriptionResultData, cartData);
			}
			else
			{
				// PayTM Error
				LOG.error("Failed to create IPG subscription. Error occurred while contacting external payment services.");
				final Errors errors = new BeanPropertyBindingResult(paymentSubscriptionResultData.getDecision(), "Decision");
				errors.reject("error.order.placed");
				throw new WebserviceValidationException(errors);
			}
		}
		else
		{
			paymentSubscriptionResultData = this.getSparDefaultPaymentFacade().completeIPGCreateSubscription(resultMap, cartData);
			if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null
					&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
			{
				doHandleIPGSuccessResponse(paymentSubscriptionResultData, cartData);
			}

			else
			{
				// IPG Error
				LOG.error("Failed to create IPG subscription. Error occurred while contacting external payment services.");
				final Errors errors = new BeanPropertyBindingResult(paymentSubscriptionResultData.getDecision(), "Decision");
				errors.reject("error.order.placed");
				throw new WebserviceValidationException(errors);
				//	return IPG_REDIRECT_URL_ERROR + "/?decision=" + paymentSubscriptionResultData.getDecision() + "&reasonCode="
				//			+ paymentSubscriptionResultData.getResultCode();

			}
		}

		final OrderWsDTO dto = dataMapper.map(cartData, OrderWsDTO.class, fields);
		/*
		 * if(null != dto) { LOG.info("In placeOrder method, OrderWsDTO details : "+ dto.toString()); }
		 */
		return dto;
		/* Payment custom spar code */
	}

	/**
	 * @param userId
	 * @param baseSiteId
	 * @param orderId
	 * @param pickupStore
	 * @return
	 */
	protected void validateIfProductIsInStockInPOS(final String baseSiteId, final String productCode, final String storeName,
			final Long entryNumber)
	{
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product added in Cart is currently out of stock", LowStockException.NO_STOCK,
						productCode);
			}
			else
			{
				throw new ProductLowStockException("Product added in Cart is currently out of stock", LowStockException.NO_STOCK,
						productCode);
			}
		}
		else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Not enough product quantity in stock. Only " + stock.getStockLevelStatus()
						+ "quantity is available in stock.", LowStockException.LOW_STOCK, productCode);
			}
			else
			{
				throw new ProductLowStockException("Not enough product quantity in stock", LowStockException.LOW_STOCK, productCode);
			}
		}
	}

	/**
	 * @param userId
	 * @param baseSiteId
	 * @param orderId
	 * @return
	 */
	private CartData fetchCartSessionCart(final String userId, final String baseSiteId, final String orderId)
	{
		final UserModel userModel = userService.getUserForUID(StringUtils.lowerCase(userId));

		final BaseSiteModel site = baseSiteService.getBaseSiteForUID(baseSiteId);
		sessionService.setAttribute("currentSite", site);
		sessionService.setAttribute("user", userModel);
		for (final CartModel cartModel : userModel.getCarts())
		{
			if (cartModel.getCode().equals(orderId))
			{
				modelService.refresh(cartModel);
				cartService.setSessionCart(cartModel);

			}

		}
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		CartModel cartModel = cartService.getSessionCart();
		BigDecimal orderSavings = BigDecimal.ZERO;
		BigDecimal orderEntrySavings = BigDecimal.ZERO;
		for (final OrderEntryData orderItem : cartData.getEntries())
		{
			for (final AbstractOrderEntryModel entryModel : cartModel.getEntries())
			{
				if (null != entryModel && StringUtils.equals(orderItem.getProduct().getCode(), entryModel.getProduct().getCode())
						&& null != entryModel.getSavings())
				{
					if(orderItem.getBasePrice().getValue().doubleValue() == entryModel.getBasePrice().doubleValue())
					{
   					orderSavings = orderSavings.add(BigDecimal.valueOf(entryModel.getSavings().doubleValue()));
   					orderEntrySavings = BigDecimal.valueOf(entryModel.getSavings().doubleValue());
   					//orderItem.setCombiOfferApplied(entryModel.isCombiOffer());
					}
				}
				final PriceData priceEntryData = priceDataFactory.create(PriceDataType.BUY, orderEntrySavings,
						commonI18NService.getCurrentCurrency());
				orderItem.setSavings(priceEntryData);
			}
		}
		cartData.setBalanceDue(cartData.getTotalPrice());
		return cartData;
	}

	/**
	 * This method is used to handle success response from IPG
	 *
	 * @param paymentSubscriptionResultData
	 */
	public void doHandleIPGSuccessResponse(final PaymentSubscriptionResultData paymentSubscriptionResultData,
			final CartData cartData)
	{
		final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();
		if (getUserFacade().getCCPaymentInfos(true).size() <= 1)
		{
			getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
		}
		if (null != cartData.getBalanceDue() && !(cartData.getBalanceDue().getValue().equals(cartData.getTotalPrice().getValue())))
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.MULTIPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.TRUE);
		}
		else
		{

			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.CREDITCARD.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.TRUE);
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		placeCartOrder();
	}

	public void doHandlePayTMSuccessResponse(final PaymentSubscriptionResultData paymentSubscriptionResultData,
			final CartData cartData)
	{
		if (null != cartData.getBalanceDue() && !(cartData.getBalanceDue().getValue().equals(cartData.getTotalPrice().getValue())))
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.MULTIPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.TRUE);
		}
		else
		{
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.PAYTM.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.TRUE);
		}
		placeCartOrder();
	}

	/**
	 *
	 */
	private void placeCartOrder()
	{
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		OrderData orderData = null;
		// Update Order with voucher details
		if (sparVoucherFacade.updateOrderWithVoucher(cartData))
		{
			sessionService.setAttribute("voucherFlag", Boolean.TRUE);
			//return REDIRECT_URL_SUMMARY;
		}
		try
		{
			//calculationService.calculateTotals(orderModel, true);
			if (null != cartData.getOrderPointOfService())
			{
				sessionService.setAttribute("selectedStore", cartData.getOrderPointOfService().getName());
			}
			orderData = sparDefaultCheckoutFacade.placeOrder();
			sparDefaultCheckoutFacade.triggerSMS(Config.getString("user.order.confirm", "user.order.confirm"), orderData);
			orderData.setDeliveryOrderGroups(null);
			orderData.setPickupOrderGroups(null);
			final OrderModel orderModel = new OrderModel();
			orderModel.setGuid(orderData.getGuid());
			final OrderModel createdOrderModel = flexibleSearchService.getModelByExample(orderModel);
			createdOrderModel.setSalesApplication(SalesApplication.WEBMOBILE);
			modelService.save(createdOrderModel);
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			final Errors errors = new BeanPropertyBindingResult(orderData, "orderData");
			errors.reject("error.order.placed");
			throw new WebserviceValidationException(errors);
		}
	}

	/**
	 * Validates the order form before to filter out invalid order states
	 *
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/carts/{cartId}/validateBeforePayment", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public void validateBeforePayment(@PathVariable final String cartId, @PathVariable final String baseSiteId,
			@RequestParam(required = true) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedStore", pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
			sparCartFacade.setOrderPointOfService(pickupStore);
			sparCartFacade.setOrderWarehouse(pickupStore);
		}
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		// adding validation for order thresholds.
		final Collection<String> voucherCodes = sparVoucherFacade.getAppliedVoucherCodes();
		if (null != voucherCodes && CollectionUtils.isNotEmpty(voucherCodes))
		{
			final String voucherCodeForCart = voucherCodes.iterator().next();

			final String errorMsg = validateBeforePlaceOrder(voucherCodeForCart);

			if (null != errorMsg)
			{
				sessionService.setAttribute("releaseMsg", errorMsg);
				final Errors errors = new BeanPropertyBindingResult(errorMsg, "errorMsg");
				errors.reject("error.voucher.release");
				throw new WebserviceValidationException(errors);
			}
		}
		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			final Errors errors = new BeanPropertyBindingResult(Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()),
					"DeliveryAddress");
			errors.reject("error.delivery.address");
			throw new WebserviceValidationException(errors);
		}
		if (null != cartService.getSessionCart())
		{
			if (null == cartService.getSessionCart().getSlotDeliveryDate()
					|| null == cartService.getSessionCart().getOrderDeliverySlot())
			{
				final Errors errors = new BeanPropertyBindingResult(Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()),
						"DeliverySlot");
				errors.reject("error.delivery.slot");
				throw new WebserviceValidationException(errors);
			}
		}
		//if payment mode is not COD then do payment check
		/*
		 * if (!isCODMode(cartData)) { if (!isWalletMode(cartData) && !isMultiPaymentMode(cartData) &&
		 * !isVoucherPaymentMode(cartData)) { if (getCheckoutFlowFacade().hasNoPaymentInfo()) { final Errors errors = new
		 * BeanPropertyBindingResult(Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()), "PaymentInfo");
		 * errors.reject("error.payment.info"); throw new WebserviceValidationException(errors); }
		 * 
		 * } }
		 */
		if (CollectionUtils.isNotEmpty(sparVoucherFacade.getSparVouchersForCart())
				&& cartData.getTotalPriceWithTax().getValue().equals(BigDecimal.valueOf(0.0)))
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.VOUCHERPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(true));
		}

		//Update savings to cartmodel
		sparCartFacade.updateCartSavings(cartData);
		//validateOrderForm();
		//if payment mode is  COD/Wallet/IPG then skip authorization check
		/*
		 * if (!isSparPaymentModes(cartData) &&
		 * !PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(cartData.getPaymentMode().getCode())) { // authorize, if
		 * failure occurs don't allow to place the order boolean isPaymentUthorized = false; try { isPaymentUthorized =
		 * sparDefaultCheckoutFacade.authorizePayment(null); } catch (final AdapterException ae) { // handle a case where
		 * a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
		 * LOG.error(ae.getMessage(), ae); } if (!isPaymentUthorized) { final Errors errors = new
		 * BeanPropertyBindingResult(Boolean.valueOf(isPaymentUthorized), "paymentAuthorized");
		 * errors.reject("error.checkout.authorize"); throw new WebserviceValidationException(errors); } }
		 */
		sparCartFacade.setOrderWarehouse(pickupStore, true);
		//final CartWsDTO dto = dataMapper.map(cartData, CartWsDTO.class, fields);
		//return dto;
	}

	public String validateBeforePlaceOrder(final String voucherCode)
	{
		String errKey = null;
		if (CollectionUtils.isNotEmpty(sparVoucherFacade.getVouchersForCart()))
		{
			Map voucherCodeStatus = null;
			voucherCodeStatus = isVoucherApplicable(voucherCode);

			if (null != voucherCodeStatus)
			{
				final Iterator itr = voucherCodeStatus.entrySet().iterator();
				while (itr.hasNext())
				{
					final Map.Entry entry = (Entry) itr.next();
					if (entry.getKey() != null)
					{
						errKey = entry.getValue().toString();
						break;
					}
				}
			}
		}
		return errKey;
	}

	public Map isVoucherApplicable(final String voucherCode)
	{
		final Map canVoucherBeApply = new HashMap();
		if (!sparVoucherFacade.validateSparVoucherCodeParameter(voucherCode))
		{
			canVoucherBeApply.put("spar.invalid.voucher.key", "spar.invalid.voucher.value");
			return canVoucherBeApply;
		}

		if (!sparVoucherFacade.isVoucherCodeValid(voucherCode))
		{
			canVoucherBeApply.put("spar.invalid.voucher.key", "spar.invalid.voucher.value");
			return canVoucherBeApply;
		}
		final VoucherModel voucherModel = voucherService.getVoucher(voucherCode);
		final UserModel user = userService.getCurrentUser();
		if (!voucherModelService.isReservable(voucherModel, voucherCode, user))
		{
			canVoucherBeApply.put("spar.invalid.voucher.key", "spar.invalid.voucher.value");
			return canVoucherBeApply;
		}

		final VoucherModel voucher = voucherService.getVoucher(voucherCode);
		if (voucher == null)
		{
			canVoucherBeApply.put("spar.invalid.voucher.key", "spar.invalid.voucher.value");
			return canVoucherBeApply;
		}

		return canVoucherBeApply;
	}

	/**
	 * This method is used to check COD/CreditCard payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isSparPaymentModes(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isCashOnDelivery = (null != paymentModeData && PaymentModeEnum.CASHONDELIVERY.getCode().equals(
				paymentModeData.getCode()));
		final boolean isIPG = (null != paymentModeData && PaymentModeEnum.CREDITCARD.getCode().equals(paymentModeData.getCode()));
		final boolean isWalletApplied = (null != paymentModeData && PaymentModeEnum.WALLET.getCode().equals(
				paymentModeData.getCode()));
		final boolean isMultiPayment = (null != paymentModeData && PaymentModeEnum.MULTIPAYMENTMODE.getCode().equals(
				paymentModeData.getCode()));
		final boolean isVoucherPaymentMode = (null != paymentModeData && PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(
				paymentModeData.getCode()));
		return isCashOnDelivery || isIPG || isWalletApplied || isMultiPayment || isVoucherPaymentMode;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isCODMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isCashOnDelivery = (null != paymentModeData && PaymentModeEnum.CASHONDELIVERY.getCode().equals(
				paymentModeData.getCode()));
		return isCashOnDelivery;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isWalletMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isWalletApplied = (null != paymentModeData && PaymentModeEnum.WALLET.getCode().equals(
				paymentModeData.getCode()));
		return isWalletApplied;
	}

	/**
	 * This method is used to check voucher payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isVoucherPaymentMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isVoucherModeApplied = (null != paymentModeData && PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(
				paymentModeData.getCode()));
		return isVoucherModeApplied;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isMultiPaymentMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isMultiPayment = (null != paymentModeData && PaymentModeEnum.MULTIPAYMENTMODE.getCode().equals(
				paymentModeData.getCode()));
		return isMultiPayment;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/orders/COD", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public OrderWsDTO placeCODOrder(@PathVariable final String userId, @PathVariable final String baseSiteId,
			@RequestParam(required = true) final String orderId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletRequest request)
			throws PaymentAuthorizationException, InvalidCartException, WebserviceValidationException, NoCheckoutCartException
	{


		final Map<String, String> resultMap = getRequestParameterMap(request);
		final CartData cartData = fetchCartSessionCart(userId, baseSiteId, orderId);
		setCashOnDeliveryParam(resultMap);
		final boolean savePaymentInfo = false;
		boolean isCashOnDelivery = PaymentModeEnum.CASHONDELIVERY.getCode().equals(resultMap.get("paymentMode"));
		if (CollectionUtils.isNotEmpty(sparVoucherFacade.getSparVouchersForCart())
				&& cartData.getTotalPriceWithTax().getValue().equals(BigDecimal.valueOf(0.0)))
		{
			isCashOnDelivery = true;
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.VOUCHERPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));

		}
		else
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					sparDefaultCheckoutFacade.getPaymentModeForCode(PaymentModeEnum.CASHONDELIVERY.getCode()), PaymentStatus.NOTPAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));
		}
		this.getSparDefaultPaymentFacade().completeCashOnDeliveryCreateSubscription(resultMap, savePaymentInfo, isCashOnDelivery,
				cartData);
		placeCartOrder();
		final OrderWsDTO dto = dataMapper.map(cartData, OrderWsDTO.class, fields);
		return dto;

	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/orders/checkSumGen", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ChecksumWsDTO checkSumGeneration(@PathVariable final String userId, @PathVariable final String baseSiteId,
			@RequestBody final ChecksumRequestParamWsDTO checksumRequestParamWsDTO) throws PaymentAuthorizationException, InvalidCartException,
			WebserviceValidationException, NoCheckoutCartException
	{
		final TreeMap<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("MID", checksumRequestParamWsDTO.getMid());
		paramMap.put("ORDER_ID", checksumRequestParamWsDTO.getOrderId());
		paramMap.put("CUST_ID", userId);
		paramMap.put("INDUSTRY_TYPE_ID", checksumRequestParamWsDTO.getIndustryTypeId());
		paramMap.put("CHANNEL_ID", checksumRequestParamWsDTO.getChannelId());
		paramMap.put("TXN_AMOUNT", checksumRequestParamWsDTO.getAmount());
		paramMap.put("WEBSITE", checksumRequestParamWsDTO.getWebsite());
		paramMap.put("EMAIL", checksumRequestParamWsDTO.getEmail());
		paramMap.put("MOBILE_NO", checksumRequestParamWsDTO.getMobileNumber());
		paramMap.put("CALLBACK_URL", checksumRequestParamWsDTO.getCallBackURL());
		paramMap.put("REQUEST_TYPE", "DEFAULT");
		try
		{
			final String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(checksumRequestParamWsDTO.getMerchantKey(), paramMap);
			paramMap.put("CHECKSUMHASH", checkSum);
			final ChecksumWsDTO chckSumDto = new ChecksumWsDTO();
			chckSumDto.setCheckSum(checkSum);
			chckSumDto.setRequestParam(paramMap);
			return chckSumDto;
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set COD Parameters
	 *
	 * @param resultMap
	 */
	private void setCashOnDeliveryParam(final Map<String, String> resultMap)
	{
		resultMap.put("reasonCode", "100");
		resultMap.put("decision", "ACCEPT");
	}


	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	protected AcceleratorCheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	/**
	 * Getter
	 *
	 * @return the sparCheckoutFacade
	 */
	/*
	 * public SparDefaultCheckoutFacade getSparCheckoutFacade() { return sparCheckoutFacade; }
	 *//**
	 * Setter
	 *
	 * @param sparCheckoutFacade
	 *           the sparCheckoutFacade to set
	 */
	/*
	 * public void setSparCheckoutFacade(final SparDefaultCheckoutFacade sparCheckoutFacade) { this.sparCheckoutFacade =
	 * sparCheckoutFacade; }
	 */

	/**
	 * Getter
	 *
	 * @return the sparDefaultPaymentFacade
	 */
	public SparDefaultPaymentFacade getSparDefaultPaymentFacade()
	{
		return sparDefaultPaymentFacade;
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	/**
	 * Setter
	 *
	 * @param sparDefaultPaymentFacade
	 *           the sparDefaultPaymentFacade to set
	 */
	public void setSparDefaultPaymentFacade(final SparDefaultPaymentFacade sparDefaultPaymentFacade)
	{
		this.sparDefaultPaymentFacade = sparDefaultPaymentFacade;
	}

	protected Map<String, String> getRequestParameterMap(final HttpServletRequest request)
	{
		final Map<String, String> map = new HashMap<String, String>();

		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			final String paramValue = request.getParameter(paramName);
			map.put(paramName, paramValue);
		}

		return map;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/updateorderDetails", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PLMSShortPickResponseDTO updateOrderDetails(@RequestBody final SparPlmsOrderWsDTO sparPlmsOrderWsDTO)
	{

		boolean flag = false;
		try
		{
			if (sparPlmsOrderWsDTO != null)
			{
				//if Order No is empty return with Error code
				if (sparPlmsOrderWsDTO.getOrderNo().isEmpty() || sparPlmsOrderWsDTO.getOrderNo() == null)
				{
					LOG.info(responseDTO(Boolean.FALSE, -56, ORDER_NO_INVALID, ORDER_UNSUCCESSFUL_MESSAGE));
					return responseDTO(Boolean.FALSE, -56, ORDER_NO_INVALID, ORDER_UNSUCCESSFUL_MESSAGE);
				} //if Order Status is empty return with Error code
				else if (sparPlmsOrderWsDTO.getStatus().charValue() == ' ' || sparPlmsOrderWsDTO.getStatus().charValue() != 'Y'
						|| sparPlmsOrderWsDTO.getStatus() == null)
				{
					LOG.info(responseDTO(Boolean.FALSE, -56, STATUS_INVALID, STATUS_UNSUCCESSFUL_MESSAGE));
					return responseDTO(Boolean.FALSE, -56, STATUS_INVALID, STATUS_UNSUCCESSFUL_MESSAGE);
				}// if Order No and Order Status are provided else
				else
				{
					final List<AbstractOrderEntryModel> savedOrderEntries = new ArrayList<AbstractOrderEntryModel>();
					final OrderModel order = modelService.create(OrderModel.class);
					order.setCode(sparPlmsOrderWsDTO.getOrderNo());

					final OrderModel orderModel = sparPlmsService.findOrderData(sparPlmsOrderWsDTO.getOrderNo().trim());
					//if Product Array is Empty , we will set the Order Status and Consignment Status
					if (orderModel != null)
					{
						if (sparPlmsOrderWsDTO.getProducts() == null)
						{
							orderModel.getConsignments().iterator().next().setStatus(ConsignmentStatus.READY_FOR_SHIPPING);
							//orderModel.setStatus(OrderStatus.COMPLETED);
							modelService.save(orderModel.getConsignments().iterator().next());
							modelService.save(orderModel);
							LOG.info(responseDTO(Boolean.TRUE, 1, ORDER_SUCCESSFUL, "NO ERROR"));
							return responseDTO(Boolean.TRUE, 1, ORDER_SUCCESSFUL, "NO ERROR");
						}
						else
						{
							for (final SparPlmsProductWsDTO SPARproductDTO : sparPlmsOrderWsDTO.getProducts())
							{
								flag = false;
								// if Product Array is in the  request but no Product iD is given
								for (final AbstractOrderEntryModel orderEntry : orderModel.getEntries())
								{
									if (SPARproductDTO.getProductid().isEmpty())
									{
										LOG.info(responseDTO(Boolean.FALSE, -56, PRODUCT_INVALID, PRODUCT_ERROR));
										return responseDTO(Boolean.FALSE, -56, PRODUCT_INVALID, PRODUCT_ERROR);
									}
									if (orderEntry.getProduct().getCode().equalsIgnoreCase(SPARproductDTO.getProductid()))
									{
										if (SPARproductDTO.getShortpickqty() == null)
										{
											if (SPARproductDTO.getCSP() == null)
											{
												LOG.info(responseDTO(Boolean.FALSE, -56,
														SHP_CSP_INVALID + ":" + SPARproductDTO.getProductid(), SHP_CSP_INVALID_REASON));
												return responseDTO(Boolean.FALSE, -56, SHP_CSP_INVALID + ":" + SPARproductDTO.getProductid(),
														SHP_CSP_INVALID_REASON);
											}
										}
										/*
										 * if ordered quantity is 5 and 2 gift promotions are added then first condition checks if
										 * the short pick quantity is not more than 7
										 */
										else if (SPARproductDTO.getShortpickqty().longValue() > countGiftPromotions(orderModel,
												SPARproductDTO.getProductid())
												&& !orderEntry.getProduct().getCode()
														.equals(giftpromotionproductcode(orderModel, SPARproductDTO.getProductid()))
												|| SPARproductDTO.getShortpickqty().longValue() < 0
												|| (SPARproductDTO.getShortpickqty().longValue() > countGiftPromotions(orderModel,
														SPARproductDTO.getProductid()) && orderEntry.getProduct().getCode()
														.equals(giftpromotionproductcode(orderModel, SPARproductDTO.getProductid()))))
										{
											LOG.info(responseDTO(Boolean.FALSE, -56, SHP_INVALID + ":" + SPARproductDTO.getProductid(),
													SHORTPICK_ERROR));
											return responseDTO(Boolean.FALSE, -56, SHP_INVALID + ":" + SPARproductDTO.getProductid(),
													SHORTPICK_ERROR);
										}
										else
										{
											if (isGiftPromotionApplied(orderModel)
													&& orderEntry.getProduct().getCode()
															.equals(giftpromotionproductcode(orderModel, SPARproductDTO.getProductid()))
													&& isProductadded(orderModel, SPARproductDTO.getProductid()))
											{
												if (SPARproductDTO.getShortpickqty().longValue() > orderEntry.getQuantity().longValue())
												{
													setShortPickQuantity(orderModel, orderEntry.getProduct().getCode(),
															SPARproductDTO.getShortpickqty());
												}
												else
												{
													if (null != orderEntry.getBasePrice() && 0 != orderEntry.getBasePrice().doubleValue())
													{
														orderEntry.setQuantity(Long.valueOf(orderEntry.getQuantity().longValue()
																- SPARproductDTO.getShortpickqty().longValue()));
														orderEntry.setShortPickQty(Long.valueOf(orderEntry.getShortPickQty().longValue()
																+ SPARproductDTO.getShortpickqty().longValue()));
													}
												}
											}
											else if (isGiftPromotionApplied(orderModel)
													&& orderEntry.getProduct().getCode()
															.equals(giftpromotionproductcode(orderModel, SPARproductDTO.getProductid()))
													&& !isProductadded(orderModel, SPARproductDTO.getProductid()))
											{
												if (SPARproductDTO.getShortpickqty().longValue() > orderEntry.getQuantity().longValue())
												{
													setShortPickQuantity(orderModel, orderEntry.getProduct().getCode(),
															SPARproductDTO.getShortpickqty());
												}
												else
												{
													if (orderEntry.getQuantity().longValue() != 0)
													{
														orderEntry.setQuantity(Long.valueOf(orderEntry.getQuantity().longValue()
																- SPARproductDTO.getShortpickqty().longValue()));
														orderEntry.setShortPickQty(Long.valueOf(orderEntry.getShortPickQty().longValue()
																+ SPARproductDTO.getShortpickqty().longValue()));
													}
												}

											}
											else
											{
												orderEntry.setQuantity(Long.valueOf(orderEntry.getQuantity().longValue()
														- SPARproductDTO.getShortpickqty().longValue()));
												orderEntry.setShortPickQty(Long.valueOf(orderEntry.getShortPickQty().longValue()
														+ SPARproductDTO.getShortpickqty().longValue()));
											}
										}
										if (SPARproductDTO.getCSP() == null)
										{
											if (SPARproductDTO.getShortpickqty() == null)
											{
												LOG.info(responseDTO(Boolean.FALSE, -56,
														SHP_CSP_INVALID + ":" + SPARproductDTO.getProductid(), SHP_CSP_INVALID_REASON));
												return responseDTO(Boolean.FALSE, -56, SHP_CSP_INVALID + ":" + SPARproductDTO.getProductid(),
														SHP_CSP_INVALID_REASON);
											}
										}
										else if (SPARproductDTO.getCSP().doubleValue() < ((orderEntry.getOriginalCSP().doubleValue() / 100) * (100 - Config
												.getDouble(LIMIT_CSP_CHANGE, 30)))
												|| SPARproductDTO.getCSP().doubleValue() > ((orderEntry.getOriginalCSP().doubleValue() / 100) * (100 + Config
														.getDouble(LIMIT_CSP_CHANGE, 30))))
										{
											// CSP is more than 30% changed then error code
											LOG.info(responseDTO(Boolean.FALSE, -56, CSP_INVALID + ":" + SPARproductDTO.getProductid(),
													CSP_ERROR));
											return responseDTO(Boolean.FALSE, -56, CSP_INVALID + ":" + SPARproductDTO.getProductid(),
													CSP_ERROR);
										}
										else
										{
											orderEntry.setBasePrice(SPARproductDTO.getCSP());
										}
										if (SPARproductDTO.getShortPickReason() != null)
										{

											orderEntry.setShortPickReason(SPARproductDTO.getShortPickReason());
										}
										if (SPARproductDTO.getMRP() != null)
										{
											orderEntry.setUnitMRP(SPARproductDTO.getMRP());
										}
										flag = true;
										savedOrderEntries.add(orderEntry);
										break;
									}

								}
							}
							if (!flag)
							{
								LOG.info(responseDTO(Boolean.FALSE, -56, PRODUCT_INVALID, PRODUCT_ERROR));
								return responseDTO(Boolean.FALSE, -56, PRODUCT_INVALID, PRODUCT_ERROR);
							}
							else
							{
								orderModel.getConsignments().iterator().next().setStatus(ConsignmentStatus.READY_FOR_SHIPPING);
								for (final AbstractOrderEntryModel orderEntry : savedOrderEntries)
								{
									modelService.save(orderEntry);
									modelService.refresh(orderEntry);
								}
								modelService.save(orderModel.getConsignments().iterator().next());
								modelService.save(orderModel);
								modelService.refresh(orderModel);
								calculationService.calculateTotals(orderModel, true);
								LOG.info(responseDTO(Boolean.TRUE, 1, ORDER_SUCCESSFUL + ":" + orderModel.getCode(), NO_ERROR));
								return responseDTO(Boolean.TRUE, 1, ORDER_SUCCESSFUL + ":" + orderModel.getCode(), NO_ERROR);
							}
						}
					}
					else
					{
						LOG.info(responseDTO(Boolean.FALSE, -56, ORDER_NO_INVALID, ORDER_UNSUCCESSFUL_MESSAGE));
						return responseDTO(Boolean.FALSE, -56, ORDER_NO_INVALID, ORDER_UNSUCCESSFUL_MESSAGE);
					}
				}
			}
		}
		catch (final CalculationException s)
		{
			LOG.info(responseDTO(Boolean.FALSE, -56, ORDER_CALCULATION, ORDER_CALCULATION_REASON));
			return responseDTO(Boolean.FALSE, -56, ORDER_CALCULATION, ORDER_CALCULATION_REASON);
		}
		LOG.info(responseDTO(Boolean.FALSE, -57, JSON_INVALID, JSON_INVALID_MESSAGE));
		return responseDTO(Boolean.FALSE, -57, JSON_INVALID, JSON_INVALID_MESSAGE);

	}

	private PLMSShortPickResponseDTO responseDTO(final Boolean status, final int errorcode, final String errormsg,
			final String errorreason)
	{
		final PLMSShortPickResponseDTO plmsShortPickResponseDTO = new PLMSShortPickResponseDTO();
		plmsShortPickResponseDTO.setStatus(status);
		plmsShortPickResponseDTO.setErrorcode(errorcode);
		plmsShortPickResponseDTO.setErrormessage(errormsg);
		plmsShortPickResponseDTO.setErrorreason(errorreason);
		return plmsShortPickResponseDTO;
	}


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}/status", method = RequestMethod.GET)
	@ResponseBody
	public SparOrderTrackingWsDTO getOrderStatusForUserByCode(@PathVariable final String code,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		final SparOrderTrackingWsDTO dto = dataMapper.map(orderData, SparOrderTrackingWsDTO.class, fields);
		if (StringUtils.isNotEmpty(dto.getStatusDisplay()))
		{
			dto.setMessage("success");
		}
		return dto;
	}

	private boolean isGiftPromotionApplied(final OrderModel orderModel)
	{
		boolean giftFlag = false;
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (null != entry.getBasePrice() && 0 == entry.getBasePrice().doubleValue())
			{
				giftFlag = true;
				return giftFlag;
			}
		}

		return giftFlag;

	}

	private String giftpromotionproductcode(final OrderModel orderModel, final String productCode)
	{

		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (null != entry.getBasePrice() && 0 == entry.getBasePrice().doubleValue()
					&& entry.getProduct().getCode().equalsIgnoreCase(productCode))
			{
				giftProductCode = entry.getProduct().getCode();
				break;
			}
		}
		return giftProductCode;
	}

	private void setShortPickQuantity(final OrderModel orderModel, final String productCode, final Long qty)
	{
		Long quantity = qty;
		//final ArrayList<AbstractOrderEntryModel> savedEntries = new ArrayList<AbstractOrderEntryModel>();
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (entry.getProduct().getCode().equals(productCode) && entry.getQuantity().longValue() > 0)
			{
				while (quantity.longValue() > ZERO && entry.getQuantity().longValue() > ZERO)
				{
					quantity = Long.valueOf(quantity.longValue() - entry.getQuantity().longValue());
					entry.setQuantity(Long.valueOf(ZERO));
					entry.setShortPickQty(entry.getOriginalQty());
				}
			}

		}

	}

	//To count the no of gift promotion applied.
	private long countGiftPromotions(final OrderModel orderModel, final String productCode)
	{
		long count = 0;
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (null != entry.getBasePrice() && entry.getProduct().getCode().equalsIgnoreCase(productCode)
					&& entry.getQuantity().longValue() > ZERO)
			{
				count += entry.getQuantity().longValue();
			}
		}
		return count;
	}

	private boolean isProductadded(final OrderModel orderModel, final String productCode)
	{
		boolean isproductadded = false;
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (null != entry.getBasePrice() && entry.getProduct().getCode().equalsIgnoreCase(productCode)
					&& entry.getQuantity().longValue() > ZERO && entry.getBasePrice().doubleValue() != 0)
			{
				isproductadded = true;
				return isproductadded;
			}

		}
		return isproductadded;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
