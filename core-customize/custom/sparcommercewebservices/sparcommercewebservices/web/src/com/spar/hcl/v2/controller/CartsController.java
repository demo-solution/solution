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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spar.hcl.cart.impl.CommerceWebServicesCartFacade;
import com.spar.hcl.core.inclusionexclusion.service.InclusionExclusionAreaListService;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.exceptions.InvalidPaymentInfoException;
import com.spar.hcl.exceptions.NoCheckoutCartException;
import com.spar.hcl.exceptions.UnsupportedDeliveryModeException;
import com.spar.hcl.exceptions.UnsupportedRequestException;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.deliverySlots.data.AllDeliverySlotListWsDTO;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotWsDTO;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotsListWsDTO;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.order.data.CartDataList;
import com.spar.hcl.order.data.OrderEntryDataList;
import com.spar.hcl.product.data.PromotionResultDataList;
import com.spar.hcl.request.support.impl.PaymentProviderRequestSupportedStrategy;
import com.spar.hcl.stock.CommerceStockFacade;
import com.spar.hcl.voucher.data.VoucherDataList;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionResultListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.voucher.VoucherListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartAddressException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.OrderRestrictionModel;
import de.hybris.platform.voucher.model.RestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;



/**
 *
 * @pathparam userId User identifier or one of the literals below :
 *            <ul>
 *            <li>'current' for currently authenticated user</li>
 *            <li>'anonymous' for anonymous user</li>
 *            </ul>
 * @pathparam cartId Cart identifier
 *            <ul>
 *            <li>cart code for logged in user</li>
 *            <li>cart guid for anonymous user</li>
 *            <li>'current' for the last modified cart</li>
 *            </ul>
 * @pathparam entryNumber Entry number. Zero-based numbering.
 * @pathparam promotionId Promotion identifier (code)
 * @pathparam voucherId Voucher identifier (code)
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class CartsController extends BaseCommerceController
{
	private final static Logger LOG = Logger.getLogger(BaseCommerceController.class);
	private final static long DEFAULT_PRODUCT_QUANTITY = 1;
	private final static String SPAR_HO_WAREHOUSE = "SPAR";
	private final static String SPAR_LMS_WAREHOUSE = "LMG";
	private final static String SPAR_HO_WAREHOUSE_CODE = "20001";
	private final static String SPAR_LMS_WAREHOUSE_CODE = "20007";
	private final static String DELIVERY_TYPE = "HD";
	private final static String DEFAULT_DELIVERY_MODE = "free-standard-shipping";

	@Resource(name = "commercePromotionRestrictionFacade")
	private CommercePromotionRestrictionFacade commercePromotionRestrictionFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "pointOfServiceValidator")
	private Validator pointOfServiceValidator;
	@Resource(name = "orderEntryCreateValidator")
	private Validator orderEntryCreateValidator;
	@Resource(name = "orderEntryUpdateValidator")
	private Validator orderEntryUpdateValidator;
	@Resource(name = "orderEntryReplaceValidator")
	private Validator orderEntryReplaceValidator;
	@Resource(name = "greaterThanZeroValidator")
	private Validator greaterThanZeroValidator;
	@Resource(name = "paymentProviderRequestSupportedStrategy")
	private PaymentProviderRequestSupportedStrategy paymentProviderRequestSupportedStrategy;
	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;
	@Autowired
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;

	@Autowired
	private SparDefaultCheckoutFacade sparCheckoutFacade;

	@Autowired
	private FlexibleSearchService flexibleSearchService;
	@Resource(name = "sparInclusionExclusionService")
	private InclusionExclusionAreaListService sparInclusionExclusionService;
	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;
	public static final int MAX_PAGE_LIMIT = 100;
	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Autowired
	private ModelService modelService;
	@Resource(name = "cartService")
	private CartService cartService;
	@Resource(name = "voucherService")
	private VoucherService voucherService;

	private static CartModificationData mergeCartModificationData(final CartModificationData cmd1, final CartModificationData cmd2)
	{
		if ((cmd1 == null) && (cmd2 == null))
		{
			return new CartModificationData();
		}
		if (cmd1 == null)
		{
			return cmd2;
		}
		if (cmd2 == null)
		{
			return cmd1;
		}
		final CartModificationData cmd = new CartModificationData();
		cmd.setDeliveryModeChanged(Boolean.valueOf(Boolean.TRUE.equals(cmd1.getDeliveryModeChanged())
				|| Boolean.TRUE.equals(cmd2.getDeliveryModeChanged())));
		cmd.setEntry(cmd2.getEntry());
		cmd.setQuantity(cmd2.getQuantity());
		cmd.setQuantityAdded(cmd1.getQuantityAdded() + cmd2.getQuantityAdded());
		cmd.setStatusCode(cmd2.getStatusCode());
		return cmd;
	}

	private static OrderEntryData getCartEntryForNumber(final CartData cart, final long number) throws CartEntryException
	{
		final List<OrderEntryData> entries = cart.getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf((int) number);
			for (final OrderEntryData entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		throw new CartEntryException("Entry not found", CartEntryException.NOT_FOUND, String.valueOf(number));
	}

	private static OrderEntryData getCartEntry(final CartData cart, final String productCode, final String pickupStore)
	{
		for (final OrderEntryData oed : cart.getEntries())
		{
			if (oed.getProduct().getCode().equals(productCode))
			{
				if (pickupStore == null && oed.getDeliveryPointOfService() == null)
				{
					return oed;
				}
				else if (pickupStore != null && oed.getDeliveryPointOfService() != null
						&& oed.getDeliveryPointOfService().getName().equals(pickupStore))
				{
					return oed;
				}
			}
		}
		return null;
	}

	private static void validateForAmbiguousPositions(final CartData currentCart, final OrderEntryData currentEntry,
			final String newPickupStore) throws CommerceCartModificationException
	{
		final OrderEntryData entryToBeModified = getCartEntry(currentCart, currentEntry.getProduct().getCode(), newPickupStore);
		if (entryToBeModified != null && !entryToBeModified.equals(currentEntry))
		{
			throw new CartEntryException("Ambiguous cart entries! Entry number " + currentEntry.getEntryNumber()
					+ " after change would be the same as entry " + entryToBeModified.getEntryNumber(),
					CartEntryException.AMBIGIOUS_ENTRY, entryToBeModified.getEntryNumber().toString());
		}
	}

	/**
	 * Lists all customer carts. Allowed only for non-anonymous users.
	 *
	 * @formparam savedCartsOnly optional parameter. If the parameter is provided and its value is true only saved carts
	 *            are returned.
	 * @formparam currentPage optional pagination parameter in case of savedCartsOnly == true. Default value 0.
	 * @formparam pageSize optional {@link PaginationData} parameter in case of savedCartsOnly == true. Default value 20.
	 * @formparam sort optional sort criterion in case of savedCartsOnly == true. No default value.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response).
	 * @return All customer carts
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CartListWsDTO getCarts(@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = false, defaultValue = "false") final boolean savedCartsOnly,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false) final String sort,
			@PathVariable(value="userId") String userId)
	{
		if (userFacade.isAnonymousUser())
		{
			throw new AccessDeniedException("Access is denied");
		}

		final CartDataList cartDataList = new CartDataList();

		if (savedCartsOnly)
		{
			final PageableData pageableData = new PageableData();
			pageableData.setCurrentPage(currentPage);
			pageableData.setPageSize(pageSize);
			pageableData.setSort(sort);
			cartDataList.setCarts(saveCartFacade.getSavedCartsForCurrentUser(pageableData, null).getResults());
		}
		else
		{
			cartDataList.setCarts(cartFacade.getCartsForCurrentUser());
		}
		
		if (CollectionUtils.isEmpty(cartDataList.getCarts()))
		{
			final List<CartData> carts = new ArrayList<CartData>();
			final CartData cartData = getSessionCart();
			carts.add(cartData);
			cartDataList.setCarts(carts);
			return dataMapper.map(cartDataList, CartListWsDTO.class, fields);
		}
		else
		{
			final CartDataList cartDataList1 = new CartDataList();
			final List<CartData> carts = new ArrayList<CartData>();
			CartData cartData = cartDataList.getCarts().get(0);
			final UserModel userModel = userService.getUserForUID(StringUtils.lowerCase(userId));
			for (final CartModel cartModel : userModel.getCarts())
			{
				if (cartModel.getCode().equals(cartData.getCode()))
				{
					cartService.setSessionCart(cartModel);
					if(null != cartData.getOrderPointOfService())
					{
						sessionService.setAttribute("selectedStore", cartData.getOrderPointOfService().getName());
					}
				}
			}
			final CartData cartData1 = sparCartFacade.getSessionCart();
			carts.add(cartData1);
			cartDataList1.setCarts(carts);
			return dataMapper.map(cartDataList1, CartListWsDTO.class, fields);
		}
	}

	/**
	 * Returns the cart with a given identifier.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Details of cart and it's entries
	 */
	@RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
	@ResponseBody
	public CartWsDTO getCart(@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = true) final String pickupStore,
			@PathVariable(value="userId") String userId,
			@PathVariable(value="cartId") String cartId)
	{
		ObjectMapper mapper = new ObjectMapper();
		LOG.info("In getCart Method : "+pickupStore);
		// CartMatchingFilter sets current cart based on cartId, so we can return cart from the session
		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedStore", pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
			sparCartFacade.setOrderPointOfService(pickupStore);
			sparCartFacade.setOrderWarehouse(pickupStore, true);
			
			final UserModel userModel = userService.getUserForUID(StringUtils.lowerCase(userId));
			for (final CartModel cartModel : userModel.getCarts())
			{
				if (cartModel.getCode().equals(cartId))
				{
					cartService.setSessionCart(cartModel);
				}
			}
		}
	   sparCartFacade.getSessionCart();
		final CartData cartData = getSessionCart();
		CartWsDTO dto = dataMapper.map(cartData, CartWsDTO.class, fields);
		try{
			String jsonInString = mapper.writeValueAsString(dto);
			LOG.info("In getCart Method , CartWsDTO : "+jsonInString);
			}
			catch(JsonProcessingException ex)
			{
				LOG.error("Exception : "+ex);
			}
		return dto;
	}

	/**
	 * Creates a new cart or restores an anonymous cart as a user's cart (if an old Cart Id is given in the request)
	 *
	 * @formparam oldCartId Anonymous cart GUID
	 * @formparam toMergeCartGuid User's cart GUID to merge anonymous cart to
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Created cart data
	 */
	/*
	 * @RequestMapping(method = RequestMethod.POST)
	 *
	 * @ResponseStatus(HttpStatus.CREATED)
	 *
	 * @ResponseBody public CartWsDTO createCart(@RequestParam(required = false) final String oldCartId,
	 *
	 * @RequestParam(required = false) String toMergeCartGuid,
	 *
	 * @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) { if (LOG.isDebugEnabled())
	 * { LOG.debug("createCart"); }
	 *
	 * if (StringUtils.isNotEmpty(oldCartId)) { if (userFacade.isAnonymousUser()) { throw new
	 * RuntimeException("Anonymous user is not allowed to copy cart!"); }
	 *
	 * if (!isCartAnonymous(oldCartId)) { throw new CartException("Cart is not anonymous", CartException.CANNOT_RESTORE,
	 * oldCartId); }
	 *
	 * if (StringUtils.isEmpty(toMergeCartGuid)) { toMergeCartGuid = getSessionCart().getGuid(); } else { if
	 * (!isUserCart(toMergeCartGuid)) { throw new CartException("Cart is not current user's cart",
	 * CartException.CANNOT_RESTORE, toMergeCartGuid); } }
	 *
	 * try { cartFacade.restoreAnonymousCartAndMerge(oldCartId, toMergeCartGuid); return dataMapper.map(getSessionCart(),
	 * CartWsDTO.class, fields); } catch (final CommerceCartMergingException e) { throw new
	 * CartException("Couldn't merge carts", CartException.CANNOT_MERGE, e); } catch (final
	 * CommerceCartRestorationException e) { throw new CartException("Couldn't restore cart",
	 * CartException.CANNOT_RESTORE, e); } } else { if (StringUtils.isNotEmpty(toMergeCartGuid)) { if
	 * (!isUserCart(toMergeCartGuid)) { throw new CartException("Cart is not current user's cart",
	 * CartException.CANNOT_RESTORE, toMergeCartGuid); }
	 *
	 * try { cartFacade.restoreSavedCart(toMergeCartGuid); return dataMapper.map(getSessionCart(), CartWsDTO.class,
	 * fields); } catch (final CommerceCartRestorationException e) { throw new CartException("Couldn't restore cart",
	 * CartException.CANNOT_RESTORE, oldCartId, e); }
	 *
	 * } return dataMapper.map(getSessionCart(), CartWsDTO.class, fields); } }
	 */

	private boolean isUserCart(final String toMergeCartGuid)
	{
		if (cartFacade instanceof CommerceWebServicesCartFacade)
		{
			final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) cartFacade;
			return commerceWebServicesCartFacade.isCurrentUserCart(toMergeCartGuid);
		}
		return true;
	}

	private boolean isCartAnonymous(final String cartGuid)
	{
		if (cartFacade instanceof CommerceWebServicesCartFacade)
		{
			final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) cartFacade;
			return commerceWebServicesCartFacade.isAnonymousUserCart(cartGuid);
		}
		return true;
	}

	/**
	 * Deletes a cart with a given cart id.
	 */
	@RequestMapping(value = "/{cartId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteCart()
	{
		cartFacade.removeSessionCart();
	}

	/**
	 * Assigns an email to the cart. This step is required to make a guest checkout.
	 *
	 * @formparam email Email of the guest user. It will be used during checkout process
	 * @throws de.hybris.platform.commerceservices.customer.DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/email", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void guestLogin(@RequestParam final String email) throws DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createGuestUserForAnonymousCheckout: email=" + sanitize(email));
		}

		if (!EmailValidator.getInstance().isValid(email))
		{
			throw new RequestParameterException("Email [" + sanitize(email) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "login");
		}

		customerFacade.createGuestUserForAnonymousCheckout(email, "guest");
	}

	/**
	 * Returns cart entries.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Cart entries list
	 */
	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.GET)
	@ResponseBody
	public OrderEntryListWsDTO getCartEntries(
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = true) final String pickupStore)
	{
		// Logs in getCartEntries 
		ObjectMapper mapper = new ObjectMapper();
		LOG.info("pickupStore : "+pickupStore);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartEntries");
		}
		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedStore", pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
			sparCartFacade.setOrderPointOfService(pickupStore);
			sparCartFacade.setOrderWarehouse(pickupStore, true);
		}
		final OrderEntryDataList dataList = new OrderEntryDataList();
		dataList.setOrderEntries(getSessionCart().getEntries());
		sparCartFacade.updateCartSavings(getSessionCart());
		final String fieldSet = FieldSetLevelHelper.FULL_LEVEL;
		final OrderEntryListWsDTO dto = dataMapper.map(dataList, OrderEntryListWsDTO.class, fieldSet);
		if (CollectionUtils.isEmpty(dto.getOrderEntries()))
		{
			dto.setMessage("Cart is Empty");
		}
	// Logs in getCartEntries 
		try{
			String jsonInString = mapper.writeValueAsString(dto);
			LOG.info("OrderEntryListWsDTO : "+jsonInString);
			}
			catch(JsonProcessingException ex)
			{
				LOG.error(ex);
			}
		return dto;

	}

	/**
	 * Adds a product to the cart.
	 *
	 * @formparam code Code of the product to be added to cart. Product look-up is performed for the current product
	 *            catalog version.
	 * @formparam qty Quantity of product.
	 * @formparam pickupStore Name of the store where product will be picked. Set only if want to pick up from a store.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about cart modification.
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws WebserviceValidationException
	 *            When store given in pickupStore parameter doesn't exist
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST)
	@ResponseBody
	public CartModificationWsDTO addCartEntry(@PathVariable final String baseSiteId,
			@RequestParam(required = true) final String code, @RequestParam(required = true) final Long qty,
			@RequestParam(required = true) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException, WebserviceValidationException, ProductLowStockException, StockSystemException
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("addCartEntry: " + logParam("code", code) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}

		if (qty != null)
		{
			validate(qty, "quantity", greaterThanZeroValidator);
		}

		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedStore", pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
			sparCartFacade.setOrderPointOfService(pickupStore);
			sparCartFacade.setOrderWarehouse(pickupStore);
		}

		return addCartEntryInternal(baseSiteId, code, qty.longValue(), pickupStore, fields);
	}

	private CartModificationWsDTO addCartEntryInternal(final String baseSiteId, final String code, final long qty,
			final String pickupStore, final String fields) throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData;

		/*
		 * if (StringUtils.isNotEmpty(pickupStore)) { validateIfProductIsInStockInPOS(baseSiteId, code, pickupStore,
		 * null); cartModificationData = cartFacade.addToCart(code, qty, pickupStore); } else {
		 * validateIfProductIsInStockOnline(baseSiteId, code, null); cartModificationData = cartFacade.addToCart(code,
		 * qty); }
		 */
		validateIfProductIsInStockInPOS(baseSiteId, code, pickupStore, null);
		//checkoutFacade.setDeliveryMode("standard-gross");
		sparCartFacade.setDeliveryMode("standard-gross");
		cartModificationData = sparCartFacade.addToCart(code, qty);
		return dataMapper.map(cartModificationData, CartModificationWsDTO.class, fields);
	}

	/**
	 * Adds a product to the cart.
	 *
	 * @param entry
	 *           Request body parameter (DTO in xml or json format) which contains details like : product code
	 *           (product.code), quantity of product (quantity), pickup store name (deliveryPointOfService.name)
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams entry,quantity,deliveryPointOfService.name,product.code
	 * @return Information about cart modification.
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws WebserviceValidationException
	 *            When there is no product code value When store given in pickupStore parameter doesn't exist
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public CartModificationWsDTO addCartEntry(@PathVariable final String baseSiteId, @RequestBody final OrderEntryWsDTO entry,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException, WebserviceValidationException, ProductLowStockException, StockSystemException
	{
		if (entry.getQuantity() == null)
		{
			entry.setQuantity(Long.valueOf(DEFAULT_PRODUCT_QUANTITY));
		}

		validate(entry, "entry", orderEntryCreateValidator);

		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();
		return addCartEntryInternal(baseSiteId, entry.getProduct().getCode(), entry.getQuantity().longValue(), pickupStore, fields);
	}



	/**
	 * Returns the details of the cart entries.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Cart entry data
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.GET)
	@ResponseBody
	public OrderEntryWsDTO getCartEntry(@PathVariable final long entryNumber,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartEntry: " + logParam("entryNumber", entryNumber));
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(getSessionCart(), entryNumber);
		return dataMapper.map(orderEntry, OrderEntryWsDTO.class, fields);
	}

	/**
	 * Updates the quantity of a single cart entry and details of the store where the cart entry will be picked.
	 * Attributes not provided in request will be defined again (set to null or default)
	 *
	 * @formparam qty Quantity of product.
	 * @formparam pickupStore Name of the store where product will be picked. Set only if want to pick up from a store.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about cart modification
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist or in case of ambiguity of cart entries
	 * @throws WebserviceValidationException
	 *            When store given in pickupStore parameter doesn't exist
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT)
	@ResponseBody
	public CartModificationWsDTO setCartEntry(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestParam(required = true) final Long qty, @RequestParam(required = true) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartEntry: " + logParam("entryNumber", entryNumber) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}

		if (qty != null)
		{
			validate(qty, "quantity", greaterThanZeroValidator);
		}

		if (!StringUtils.isEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
		}
		final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
		sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, qty, pickupStore, fields, true);
	}

	private CartModificationWsDTO updateCartEntryInternal(final String baseSiteId, final CartData cart,
			final OrderEntryData orderEntry, final Long qty, final String pickupStore, final String fields, final boolean putMode)
			throws CommerceCartModificationException
	{
		final long entryNumber = orderEntry.getEntryNumber().longValue();
		final String productCode = orderEntry.getProduct().getCode();
		//final PointOfServiceData currentPointOfService = orderEntry.getDeliveryPointOfService();

		CartModificationData cartModificationData1 = null;
		CartModificationData cartModificationData2 = null;
		//This piece of code is commented due to Pick up in disabled in SPAR as of now. this start here
		/*if (!StringUtils.isEmpty(pickupStore))
		{
			validateForAmbiguousPositions(cart, orderEntry, pickupStore);
			validateIfProductIsInStockInPOS(baseSiteId, productCode, pickupStore, Long.valueOf(entryNumber));
			cartModificationData1 = sparCartFacade.updateCartEntry(entryNumber, pickupStore);
		}*/
		//This piece of code is commented due to Pick up in disabled in SPAR as of now. this end here
		
		/*
		 * if (currentPointOfService == null || !currentPointOfService.getName().equals(pickupStore)) { //was 'shipping
		 * mode' or store is changed validateForAmbiguousPositions(cart, orderEntry, pickupStore);
		 * validateIfProductIsInStockInPOS(baseSiteId, productCode, pickupStore, Long.valueOf(entryNumber));
		 * cartModificationData1 = cartFacade.updateCartEntry(entryNumber, pickupStore); } } else if (putMode &&
		 * currentPointOfService != null) { //was 'pickup in store', now switch to 'shipping mode'
		 * validateForAmbiguousPositions(cart, orderEntry, pickupStore); validateIfProductIsInStockOnline(baseSiteId,
		 * productCode, Long.valueOf(entryNumber)); cartModificationData1 = cartFacade.updateCartEntry(entryNumber,
		 * pickupStore); }
		 */

		if (qty != null)
		{
			cartModificationData2 = sparCartFacade.updateCartEntry(entryNumber, qty.longValue());
		}

		return dataMapper.map(mergeCartModificationData(cartModificationData1, cartModificationData2), CartModificationWsDTO.class,
				fields);
	}

	/**
	 * Updates the quantity of a single cart entry and details of the store where the cart entry will be picked.
	 * Attributes not provided in request will be defined again (set to null or default)
	 *
	 * @param entry
	 *           Request body parameter (DTO in xml or json format) which contains details like : quantity of product
	 *           (quantity), pickup store name (deliveryPointOfService.name)
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams entry,quantity,deliveryPointOfService.name,product.code
	 * @return Information about cart modification
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist or in case of ambiguity of cart entries
	 * @throws WebserviceValidationException
	 *            When store given in pickupStore parameter doesn't exist
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public CartModificationWsDTO setCartEntry(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestBody final OrderEntryWsDTO entry,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);
		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();

		validateCartEntryForReplace(orderEntry, entry);

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, entry.getQuantity(), pickupStore, fields, true);
	}

	private void validateCartEntryForReplace(final OrderEntryData oryginalEntry, final OrderEntryWsDTO entry)
	{
		final String productCode = oryginalEntry.getProduct().getCode();
		final Errors errors = new BeanPropertyBindingResult(entry, "entry");
		if (entry.getProduct() != null && entry.getProduct().getCode() != null && !entry.getProduct().getCode().equals(productCode))
		{
			errors.reject("cartEntry.productCodeNotMatch");
			throw new WebserviceValidationException(errors);
		}

		validate(entry, "entry", orderEntryReplaceValidator);
	}

	/**
	 * Updates the quantity of a single cart entry and details of the store where the cart entry will be picked.
	 *
	 * @formparam qty Quantity of product.
	 * @formparam pickupStore Name of the store where product will be picked. Set only if want to pick up from a store.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about cart modification
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist or in case of ambiguity of cart entries
	 * @throws WebserviceValidationException
	 *            When store given in pickupStore parameter doesn't exist
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH)
	@ResponseBody
	public CartModificationWsDTO updateCartEntry(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestParam(required = false) final Long qty, @RequestParam(required = false) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry: " + logParam("entryNumber", entryNumber) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}

		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);

		if (qty == null && StringUtils.isEmpty(pickupStore))
		{
			throw new RequestParameterException("At least one parameter (qty,pickupStore) should be set!",
					RequestParameterException.MISSING);
		}

		if (qty != null)
		{
			validate(qty, "quantity", greaterThanZeroValidator);
		}

		if (pickupStore != null)
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
		}

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, qty, pickupStore, fields, false);
	}

	/**
	 * Updates the quantity of a single cart entry and details of the store where the cart entry will be picked.
	 *
	 * @param entry
	 *           Request body parameter (DTO in xml or json format) which contains details like : quantity of product
	 *           (quantity), pickup store name (deliveryPointOfService.name)
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams entry,quantity,deliveryPointOfService.name,product.code
	 * @return Information about cart modification
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist or in case of ambiguity of cart entries
	 * @throws WebserviceValidationException
	 *            When store given in pickupStore parameter doesn't exist
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 * @throws ProductLowStockException
	 *            When product is out of stock in store (when pickupStore parameter is filled)
	 * @throws StockSystemException
	 *            When there is no information about stock for stores (when pickupStore parameter is filled).
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public CartModificationWsDTO updateCartEntry(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestBody final OrderEntryWsDTO entry,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);

		final String productCode = orderEntry.getProduct().getCode();
		final Errors errors = new BeanPropertyBindingResult(entry, "entry");
		if (entry.getProduct() != null && entry.getProduct().getCode() != null && !entry.getProduct().getCode().equals(productCode))
		{
			errors.reject("cartEntry.productCodeNotMatch");
			throw new WebserviceValidationException(errors);
		}

		if (entry.getQuantity() == null)
		{
			entry.setQuantity(orderEntry.getQuantity());
		}

		validate(entry, "entry", orderEntryUpdateValidator);

		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();
		return updateCartEntryInternal(baseSiteId, cart, orderEntry, entry.getQuantity(), pickupStore, fields, false);
	}

	/**
	 * Deletes cart entry.
	 *
	 * @throws CartEntryException
	 *            When entry with given number doesn't exist
	 * @throws CommerceCartModificationException
	 *            When there are some problems with cart modification
	 */
	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.DELETE)
	@ResponseBody
	public OrderEntryListWsDTO removeCartEntry(@PathVariable final long entryNumber) throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeCartEntry: " + logParam("entryNumber", entryNumber));
		}

		final CartData cart = getSessionCart();
		getCartEntryForNumber(cart, entryNumber);
		//cartFacade.updateCartEntry(entryNumber, 0);
		sparCartFacade.updateCartEntry(entryNumber, 0);

		final OrderEntryDataList dataList = new OrderEntryDataList();
		dataList.setOrderEntries(getSessionCart().getEntries());

		final String fieldSet = FieldSetLevelHelper.FULL_LEVEL;
		final OrderEntryListWsDTO dto = dataMapper.map(dataList, OrderEntryListWsDTO.class, fieldSet);
		dto.setMessage("Item deleted sucessfully");
		return dto;

	}

	/**
	 * Creates an address and assigns it to the cart as the delivery address.
	 *
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode Isocode for region. If this parameter is required depends on country.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Created address
	 * @throws WebserviceValidationException
	 *            When address parameters are incorrect
	 */
	@Secured(
	{ "ROLE_GUEST", "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public AddressWsDTO createAndSetAddress(@RequestParam(required = false) final String titleCode,
			@RequestParam(required = false) final String firstName, @RequestParam(required = false) final String lastName,
			@RequestParam(required = false) final String line1, @RequestParam(required = false) final String line2,
			@RequestParam(required = false) final String town, @RequestParam(required = false) final String postalCode,
			@RequestParam(required = false) final String landmark, @RequestParam(required = true) final String country,
			@RequestParam(required = false, defaultValue = "false") final boolean defaultAddress,
			@RequestParam(required = false) final String phone, @RequestParam(required = false) final String email,
			@RequestParam(required = false) final String area, @RequestParam(required = false) final String buildingName,
			final HttpServletRequest request, @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, NoCheckoutCartException
	{

		final AddressData addressData = super.createAddressInternal(request);
		//	final AddressData addressData = new AddressData();
		addressData.setTitleCode(titleCode);
		addressData.setFirstName(firstName);
		addressData.setLastName(lastName);
		addressData.setLine1(line1);
		addressData.setLine2(line2);
		addressData.setTown(town);
		addressData.setPostalCode(postalCode);
		addressData.setBillingAddress(false);
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);
		addressData.setLandmark(landmark);
		if (country != null && !StringUtils.isEmpty(country))
		{
			addressData.setCountry(i18NFacade.getCountryForIsocode(country));
		}
		addressData.setPhone(phone);
		addressData.setEmail(email);
		addressData.setArea(area);
		addressData.setBuildingName(buildingName);
		if (userFacade.isAddressBookEmpty())
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		else
		{
			addressData.setDefaultAddress(defaultAddress);
		}

		//validate(addressData, "address", sparAddressDataValidator);
		final String addressId = addressData.getId();
		super.setCartDeliveryAddressInternal(addressId);
		return dataMapper.map(addressData, AddressWsDTO.class, fields);
	}



	/**
	 * Creates an address and assigns it to the cart as the delivery address.
	 *
	 * @param address
	 *           Request body parameter (DTO in xml or json format) which contains details like : Customer's first
	 *           name(firstName), Customer's last name(lastName), Customer's title code(titleCode),
	 *           country(country.isocode), first part of address(line1) , second part of address(line2), town (town),
	 *           postal code(postalCode), region (region.isocode)
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams
	 *             titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress
	 * @return Created address
	 * @throws WebserviceValidationException
	 *            When address parameters are incorrect
	 */
	@Secured(
	{ "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public AddressWsDTO createAndSetAddress(@RequestBody final AddressWsDTO address,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createAddress");
		}
		validate(address, "address", addressDTOValidator);
		AddressData addressData = dataMapper.map(address, AddressData.class,
				"titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress,landmark");
		addressData = createAddressInternal(addressData);
		setCartDeliveryAddressInternal(addressData.getId());
		return dataMapper.map(addressData, AddressWsDTO.class, fields);
	}

	/**
	 * Sets a delivery address for the cart. The address country must be placed among the delivery countries of the
	 * current base store.
	 *
	 * @formparam addressId Address identifier
	 * @throws CartAddressException
	 *            When address with given id is not valid or doesn't exists
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void setCartDeliveryAddress(@RequestParam(required = true) final String addressId,
			@RequestParam(required = false) final String pickupStore) throws NoCheckoutCartException
	{
		if(StringUtils.isNotEmpty(pickupStore))
		{
			LOG.info("In setCartDeliveryAddress method : " + pickupStore);
   		final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
   		sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		}
		super.setCartDeliveryAddressInternal(addressId);
	}

	/**
	 * Removes the delivery address from the cart.
	 *
	 * @throws CartException
	 *            When removing delivery address failed
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void removeCartDeliveryAddress()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryAddress");
		}
		if (!checkoutFacade.removeDeliveryAddress())
		{
			throw new CartException("Cannot reset address!", CartException.CANNOT_RESET_ADDRESS);
		}
	}

	/**
	 * Returns the delivery mode selected for the cart.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Delivery mode selected for the cart
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.GET)
	@ResponseBody
	public DeliveryModeWsDTO getCartDeliveryMode(
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartDeliveryMode");
		}
		return dataMapper.map(getSessionCart().getDeliveryMode(), DeliveryModeWsDTO.class, fields);
	}

	/**
	 * Sets the delivery mode with a given identifier for the cart.
	 *
	 * @formparam deliveryModeId Delivery mode identifier (code)
	 * @throws UnsupportedDeliveryModeException
	 *            When the delivery mode does not exist or when the delivery address is not set for the cart
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void setCartDeliveryMode(@RequestParam(required = true) final String deliveryModeId)
			throws UnsupportedDeliveryModeException
	{
		if (deliveryModeId.isEmpty())
		{
			super.setCartDeliveryModeInternal(DEFAULT_DELIVERY_MODE);
		}
		else
		{
			super.setCartDeliveryModeInternal(deliveryModeId);
		}
	}

	/**
	 * Removes the delivery mode from the cart.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void removeDeliveryMode()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryMode");
		}
		if (!checkoutFacade.removeDeliveryMode())
		{
			throw new CartException("Cannot reset delivery mode!", CartException.CANNOT_RESET_DELIVERYMODE);
		}
	}

	/**
	 * Returns all delivery modes supported for the current base store and cart delivery address. A delivery address must
	 * be set for the cart, otherwise an empty list will be returned.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return All supported delivery modes
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymodes", method = RequestMethod.GET)
	@ResponseBody
	public DeliveryModeListWsDTO getSupportedDeliveryModes(
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSupportedDeliveryModes");
		}
		final DeliveryModesData deliveryModesData = new DeliveryModesData();
		deliveryModesData.setDeliveryModes(checkoutFacade.getSupportedDeliveryModes());
		final DeliveryModeListWsDTO dto = dataMapper.map(deliveryModesData, DeliveryModeListWsDTO.class, fields);
		return dto;
	}

	/**
	 * Defines details of a new credit card payment details and assigns the payment to the cart.
	 *
	 * @formparam accountHolderName Name on card. This parameter is required.
	 * @formparam cardNumber Card number. This parameter is required.
	 * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
	 *            card types are supported
	 * @formparam expiryMonth Month of expiry date. This parameter is required.
	 * @formparam expiryYear Year of expiry date. This parameter is required.
	 * @formparam issueNumber
	 * @formparam startMonth
	 * @formparam startYear
	 * @formparam subscriptionId
	 * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
	 *            reused for future orders.
	 * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
	 * @formparam billingAddress.firstName Customer's first name. This parameter is required.
	 * @formparam billingAddress.lastName Customer's last name. This parameter is required.
	 * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
	 *            rest of address parameters are validated (e.g. if parameters are required :
	 *            line1,line2,town,postalCode,region.isocode)
	 * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
	 *            it is required).
	 * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
	 *            it is not required)
	 * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Created payment details
	 * @throws WebserviceValidationException
	 * @throws InvalidPaymentInfoException
	 * @throws NoCheckoutCartException
	 * @throws UnsupportedRequestException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PaymentDetailsWsDTO addPaymentDetails(final HttpServletRequest request,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		final CCPaymentInfoData paymentInfoData = super.addPaymentDetailsInternal(request).getPaymentInfo();
		return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	/**
	 * Defines details of a new credit card payment details and assigns the payment to the cart.
	 *
	 * @param paymentDetails
	 *           Request body parameter (DTO in xml or json format) which contains details like : Name on card
	 *           (accountHolderName), card number(cardNumber), card type (cardType.code), Month of expiry date
	 *           (expiryMonth), Year of expiry date (expiryYear), if payment details should be saved (saved), if if the
	 *           payment details should be used as default (defaultPaymentInfo), billing address (
	 *           billingAddress.firstName,billingAddress.lastName, billingAddress.titleCode,
	 *           billingAddress.country.isocode, billingAddress.line1, billingAddress.line2, billingAddress.town,
	 *           billingAddress.postalCode, billingAddress.region.isocode)
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams
	 *             accountHolderName,cardNumber,cardType,cardTypeData(code),expiryMonth,expiryYear,issueNumber,startMonth,
	 *             startYear
	 *             ,subscriptionId,defaultPaymentInfo,saved,billingAddress(titleCode,firstName,lastName,line1,line2
	 *             ,town,postalCode,country(isocode),region(isocode),defaultAddress)
	 * @return Created payment details
	 * @throws WebserviceValidationException
	 * @throws InvalidPaymentInfoException
	 * @throws NoCheckoutCartException
	 * @throws UnsupportedRequestException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PaymentDetailsWsDTO addPaymentDetails(@RequestBody final PaymentDetailsWsDTO paymentDetails,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		validatePayment(paymentDetails);
		final String copiedfields = "accountHolderName,cardNumber,cardType,cardTypeData(code),expiryMonth,expiryYear,issueNumber,startMonth,startYear,subscriptionId,defaultPaymentInfo,saved,"
				+ "billingAddress(titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress)";
		CCPaymentInfoData paymentInfoData = dataMapper.map(paymentDetails, CCPaymentInfoData.class, copiedfields);
		paymentInfoData = addPaymentDetailsInternal(paymentInfoData).getPaymentInfo();
		return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	private void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException
	{
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}
		validate(paymentDetails, "paymentDetails", paymentDetailsDTOValidator);
	}

	/**
	 * Sets credit card payment details for the cart.
	 *
	 * @formparam paymentDetailsId Payment details identifier
	 * @throws InvalidPaymentInfoException
	 *            When payment details with given id doesn't exists or belong to another user
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void setPaymentDetails(@RequestParam(required = true) final String paymentDetailsId) throws InvalidPaymentInfoException
	{
		super.setPaymentDetailsInternal(paymentDetailsId);
	}

	/**
	 * Return information about promotions applied on cart
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about promotions applied on cart
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.GET)
	@ResponseBody
	public PromotionResultListWsDTO getPromotions(
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPromotions");
		}
		final List<PromotionResultData> appliedPromotions = new ArrayList<>();
		final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
		final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
		appliedPromotions.addAll(orderPromotions);
		appliedPromotions.addAll(productPromotions);

		final PromotionResultDataList dataList = new PromotionResultDataList();
		dataList.setPromotions(appliedPromotions);
		return dataMapper.map(dataList, PromotionResultListWsDTO.class, fields);
	}

	/**
	 * Return information about promotion with given id, applied on cart.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about promotion with given id, applied on cart
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.GET)
	@ResponseBody
	public PromotionResultListWsDTO getPromotion(@PathVariable final String promotionId,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPromotion: promotionId = " + sanitize(promotionId));
		}
		final List<PromotionResultData> appliedPromotions = new ArrayList<PromotionResultData>();
		final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
		final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
		for (final PromotionResultData prd : orderPromotions)
		{
			if (prd.getPromotionData().getCode().equals(promotionId))
			{
				appliedPromotions.add(prd);
			}
		}
		for (final PromotionResultData prd : productPromotions)
		{
			if (prd.getPromotionData().getCode().equals(promotionId))
			{
				appliedPromotions.add(prd);
			}
		}

		final PromotionResultDataList dataList = new PromotionResultDataList();
		dataList.setPromotions(appliedPromotions);
		return dataMapper.map(dataList, PromotionResultListWsDTO.class, fields);
	}

	/**
	 * Enables the promotion for the order based on the promotionId defined for the cart.
	 *
	 * @formparam promotionId Promotion identifier
	 * @throws CommercePromotionRestrictionException
	 *            When there is no PromotionOrderRestriction for the promotion
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void applyPromotion(@RequestParam(required = true) final String promotionId)
			throws CommercePromotionRestrictionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("applyPromotion: promotionId = " + sanitize(promotionId));
		}
		commercePromotionRestrictionFacade.enablePromotionForCurrentCart(promotionId);
	}

	/**
	 * Disables the promotion for the order based on the promotionId defined for the cart.
	 *
	 * @throws CommercePromotionRestrictionException
	 *            When there is no PromotionOrderRestriction for the promotion
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void removePromotion(@PathVariable final String promotionId) throws CommercePromotionRestrictionException,
			NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removePromotion: promotionId = " + sanitize(promotionId));
		}
		commercePromotionRestrictionFacade.disablePromotionForCurrentCart(promotionId);
	}

	/**
	 * Returns list of vouchers applied to the cart.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of vouchers applied to the cart.
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.GET)
	@ResponseBody
	public VoucherListWsDTO getVouchers(@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getVouchers");
		}
		final VoucherDataList dataList = new VoucherDataList();
		fields = FieldSetLevelHelper.FULL_LEVEL;
		dataList.setVouchers(getSessionCart().getAppliedVoucherTotal());
		return dataMapper.map(dataList, VoucherListWsDTO.class, fields);
	}

	/**
	 * Applies a voucher based on the voucherId defined for the cart.
	 *
	 * @formparam voucherId Voucher identifier
	 * @throws VoucherOperationException
	 *            When trying to apply a non-existent voucher or other error occurs during the voucher-application
	 *            process.
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void applyVoucherForCart(@RequestParam(required = true) final String voucherId,
			@RequestParam(required = false) final String pickupStore)
			throws NoCheckoutCartException, VoucherOperationException, CMSItemNotFoundException
	{
		//super.applyVoucherForCartInternal(voucherId);
		if (StringUtils.isNotEmpty(pickupStore))
		{
			LOG.info("In applyVoucherForCart method : " + pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		}
		if (getSessionCart().getAppliedVouchers().isEmpty())
		{
			/*
			 * if (!sparVoucherFacade.validateSparVoucherCodeParameter(voucherId)) { throw new VoucherOperationException(
			 * "Voucher Code is not valid "); } if (!sparVoucherFacade.isVoucherCodeValid(voucherId)) { throw new
			 * VoucherOperationException("Voucher not found: " + voucherId); }
			 */
			final String confirmation = getUserConfirmationForVoucherApply(voucherId);
			if (confirmation.equals("false"))
			{
				LOG.info("Voucher ready to apply ########:");
				final Map canVoucherBeApply = sparVoucherFacade.applyVoucherCode(voucherId);
				if (!canVoucherBeApply.isEmpty())
				{
					throw new VoucherOperationException("Coupon code " + voucherId + " is invalid. Please enter a valid coupon code");
				}
				sparVoucherFacade.updateVoucherValue(getSessionCart(), true);
			}
			else if (confirmation.equals("true"))
			{
				throw new VoucherOperationException("Coupon code " + voucherId + " is invalid. Please enter a valid coupon code");
			}
			else
			{
				throw new VoucherOperationException(confirmation);
			}

		}
		else
		{
			throw new VoucherOperationException("Voucher is already Applied");
		}
	}

	private String getUserConfirmationForVoucherApply(final String voucherCode)
			throws CMSItemNotFoundException, VoucherOperationException
	{
		if (StringUtils.isNotEmpty(voucherCode))
		{
			if (sparVoucherFacade.isVoucherCodeValid(voucherCode))
			{
				final VoucherModel voucherModel = voucherService.getVoucher(voucherCode);

				LOG.info("getSessionCart().getSaving().getValue() before ########:" + cartService.getSessionCart().getSavings());
				LOG.info("getSessionCart().getSubTotal().getValue() before ########:" + cartService.getSessionCart().getSubtotal());
				LOG.info("getSessionCart().getTotalPrice().getValue() before ########:"
						+ cartService.getSessionCart().getTotalEqvPrice());
				sparVoucherFacade.doCartCalculation(getSessionCart(), voucherCode);
				sparVoucherFacade.updateTotalEquivalentPrice();

				LOG.info("getSessionCart().getSaving().getValue() after ########:" + cartService.getSessionCart().getSavings());
				LOG.info("getSessionCart().getSubTotal().getValue() after ########:" + cartService.getSessionCart().getSubtotal());
				LOG.info("getSessionCart().getTotalPrice().getValue() after ########:"
						+ cartService.getSessionCart().getTotalEqvPrice());

				if (!sparVoucherFacade.checkVoucherCanBeRedeemed(voucherModel, voucherCode))
				{
					String violationMessage = "true";
					final Set<RestrictionModel> restrictions = voucherModel.getRestrictions();
					for (final RestrictionModel restriction : restrictions)
					{
						if (restriction.getItemtype().equalsIgnoreCase("OrderRestriction"))
						{
							OrderRestrictionModel orderRestriction=(OrderRestrictionModel)restriction;
							if(null != getSessionCart().getSubTotal() && null != orderRestriction.getTotal() && getSessionCart().getSubTotal().getValue() .doubleValue() < orderRestriction.getTotal().doubleValue())
							{
							violationMessage = restriction.getViolationMessage();
							}
						}

					}
					return violationMessage;
				}
				else
				{
					final boolean getConfirmationForVoucherApply = sparVoucherFacade.isVoucherValueExceedingOrdertotal(voucherCode);
					sessionService.setAttribute("voucherValueExceedingOrdertotal", getConfirmationForVoucherApply);
					return String.valueOf(getConfirmationForVoucherApply);
				}
			}
			else
			{
				return "true";
			}
		}
		else
		{
			return "true";
		}
	}

	/**
	 * Removes a voucher based on the voucherId defined for the current cart.
	 *
	 * @throws VoucherOperationException
	 *            When an error occurs during the release voucher process.
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers/{voucherId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void releaseVoucherFromCart(@PathVariable final String voucherId,
			@RequestParam(required = false) final String pickupStore) throws NoCheckoutCartException,
			VoucherOperationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("release voucher : voucherCode = " + sanitize(voucherId));
		}
		if(StringUtils.isNotEmpty(pickupStore))
		{
			LOG.info("In releaseVoucherFromCart method : " + pickupStore);
   		final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
   		sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot realese voucher. There was no checkout cart created yet!");
		}
		//voucherFacade.releaseVoucher(voucherId);
		sparVoucherFacade.releaseSparVoucher(voucherId);
		sparVoucherFacade.doCartCalculation(getSessionCart(), voucherId);
		//sparVoucherFacade.updateVoucherValue(getSessionCart(), false);

	}

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
		/*
		 * else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK)) { if (entryNumber !=
		 * null) { throw new LowStockException("Not enough product quantity in stock. Only " + stock.getStockLevelStatus()
		 * + "quantity is available in stock.", LowStockException.LOW_STOCK, productCode); } else { throw new
		 * ProductLowStockException("Not enough product quantity in stock", LowStockException.LOW_STOCK, productCode); } }
		 */
	}

	protected void validateIfProductIsInStockOnline(final String baseSiteId, final String productCode, final Long entryNumber)
	{
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = commerceStockFacade.getStockDataForProductAndBaseSite(productCode, baseSiteId);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, productCode);
			}
		}
	}

	/**
	 * Returns delivery Slots for the given store.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Delivery Slots Array for the cart
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverySlots/{storeName}", method = RequestMethod.GET)
	@ResponseBody
	public AllDeliverySlotListWsDTO getDeliverySlots(@PathVariable final String storeName,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getDeliverySlots");
		}
		sessionService.setAttribute("selectedStore", storeName);
		final boolean whetherEmployee = sparCustomerFacade.isCustomerEmployee();
		final CartData cart = getSessionCart();
		//String orderWarehouse = cart.getOrderWarehouse().getCode();
		final Map<Date, List<DeliverySlotData>> map = sparCartFacade.getDeliverySlotsMap(DELIVERY_TYPE);

		final List<DeliverySlotsListWsDTO> deliverySlotsListWsDTOList = new ArrayList<DeliverySlotsListWsDTO>();
		final AllDeliverySlotListWsDTO allDeliverySlotListWsDTO = new AllDeliverySlotListWsDTO();
		for (final Map.Entry<Date, List<DeliverySlotData>> entry : map.entrySet())
		{
			final DeliverySlotsListWsDTO deliverySlotsListWsDTO = new DeliverySlotsListWsDTO();
			deliverySlotsListWsDTO.setDeliveryDay(entry.getKey().toString());
			final List<DeliverySlotWsDTO> list = new ArrayList<DeliverySlotWsDTO>();

			for (final DeliverySlotData deliverySlotData : entry.getValue())
			{
				if (whetherEmployee
						&& (deliverySlotData.getSlotDescription().contains(SPAR_HO_WAREHOUSE) || deliverySlotData.getSlotDescription()
								.contains(SPAR_LMS_WAREHOUSE)))
				{
					if (cart.getOrderWarehouse().getCode().equals(SPAR_HO_WAREHOUSE_CODE)
							&& deliverySlotData.getSlotDescription().contains(SPAR_HO_WAREHOUSE))
					{
						final DeliverySlotWsDTO deliverySlotWsDTO = dataMapper.map(deliverySlotData, DeliverySlotWsDTO.class, fields);
						list.add(deliverySlotWsDTO);
					}
					else if (cart.getOrderWarehouse().getCode().equals(SPAR_LMS_WAREHOUSE_CODE)
							&& deliverySlotData.getSlotDescription().contains(SPAR_LMS_WAREHOUSE))
					{
						final DeliverySlotWsDTO deliverySlotWsDTO = dataMapper.map(deliverySlotData, DeliverySlotWsDTO.class, fields);
						list.add(deliverySlotWsDTO);
					}
				}
				else if (deliverySlotData.getSlotDescription().contains(SPAR_HO_WAREHOUSE)
						|| deliverySlotData.getSlotDescription().contains(SPAR_LMS_WAREHOUSE))
				{
					continue;
				}
				else
				{
					final DeliverySlotWsDTO deliverySlotWsDTO = dataMapper.map(deliverySlotData, DeliverySlotWsDTO.class, fields);
					list.add(deliverySlotWsDTO);
				}
			}
			deliverySlotsListWsDTO.setDeliverySlots(list);
			deliverySlotsListWsDTOList.add(deliverySlotsListWsDTO);
		}
		allDeliverySlotListWsDTO.setAllDeliverySlots(deliverySlotsListWsDTOList);
		return allDeliverySlotListWsDTO;
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/delivery/{deliverydate}/{deliveryslotID}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void setDeliverySlot(@PathVariable final String deliverydate, @PathVariable final String deliveryslotID)
	{
		try
		{
			final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			final Date orderDeliveryDate = dateFormatter.parse(deliverydate);
			final DeliverySlotModel orderDeliverySlot = storeFinderServiceInterface.getOrderDeliverySLot(deliveryslotID);
			getSparCheckoutFacade().saveDeliverySlotAndDate(orderDeliveryDate, orderDeliverySlot);
		}
		catch (final ParseException e)
		{
			LOG.error("Not able to parse String to Date " + e);
		}
	}

	/**
	 * @return the sparCheckoutFacade
	 */
	public SparDefaultCheckoutFacade getSparCheckoutFacade()
	{
		return sparCheckoutFacade;
	}

	public void setCartByCode(final String code)
	{
		final CartModel cartModel = new CartModel();
		cartModel.setCode(code);
		final CartModel cart = flexibleSearchService.getModelByExample(cartModel);

		if (cart != null)
		{
			sessionService.setAttribute("cart", cart);
		}
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/validateCart", method = RequestMethod.GET)
	@ResponseBody
	public void validateCart(@PathVariable final String userId, @PathVariable final String cartId,
			@PathVariable final String baseSiteId, @RequestParam(required = true) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		LOG.info("In validateCart");
		LOG.info("userId :"+userId);
		LOG.info("cartId :"+cartId);
		LOG.info("baseSiteId :"+baseSiteId);
		LOG.info("pickupStore :"+pickupStore);
		//cartService.setSessionCart(null);
		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedStore", pickupStore);
			final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(pickupStore);
			sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
			sparCartFacade.setOrderPointOfService(pickupStore);
			sparCartFacade.setOrderWarehouse(pickupStore, true);

		}
		BaseStoreModel baseStore = getBaseStoreService().getBaseStoreForUid(baseSiteId);

		baseStore = getBaseStoreService().getCurrentBaseStore();
		final UserModel userModel = userService.getUserForUID(StringUtils.lowerCase(userId));
		cartService.getSessionCart().getTotalPrice();
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID(baseSiteId);
		sessionService.setAttribute("currentSite", site);
		sessionService.setAttribute("user", userModel);
		for (final CartModel cartModel : userModel.getCarts())
		{
			cartModel.getTotalPrice();
			if (cartModel.getCode().equals(cartId))
			{
				//	modelService.refresh(cartModel);
				cartService.setSessionCart(cartModel);


			}

		}
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		if (baseStore != null)
		{
			final Double minOrderLimit = baseStore.getMinOrderLimit();
			if (null != cartData.getTotalPrice() && null != minOrderLimit)
			{
				if (cartData.getTotalPrice().getValue().doubleValue() < minOrderLimit.doubleValue())
				{
					final Errors errors = new BeanPropertyBindingResult(Boolean.valueOf(getSparCheckoutFacade().hasCheckoutCart()),
							"DeliveryMode");
					errors.reject("error.cart.amount");
					throw new WebserviceValidationException(errors);
				}
			}
			else
			{
				final Errors errors = new BeanPropertyBindingResult(Boolean.valueOf(getSparCheckoutFacade().hasCheckoutCart()),
						"DeliveryMode");
				errors.reject("error.cart.amount");
				throw new WebserviceValidationException(errors);
			}

		}
		for (final OrderEntryData orderEntry : cartData.getEntries())
		{
			if (null != orderEntry.getProduct() && null != orderEntry.getEntryNumber())
			{
				validateIfProductIsInStockInPOS(baseSiteId, orderEntry.getProduct().getCode(), pickupStore,
						Long.valueOf(orderEntry.getEntryNumber().longValue()));
			}
		}
		//	return dataMapper.map(getSessionCart(), CartWsDTO.class, fields);
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
