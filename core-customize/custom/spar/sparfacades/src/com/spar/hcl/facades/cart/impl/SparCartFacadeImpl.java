/**
 *
 */
package com.spar.hcl.facades.cart.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.util.Config;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.core.stock.impl.SparDefaultCommerceStockService;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.populators.SparCategoryData;
import com.spar.hcl.facades.populators.SparOrderEntryPopulator;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;


/**
 * @author nikhil-ku
 *
 */
public class SparCartFacadeImpl extends DefaultCartFacade implements SparCartFacade
{
	private static final Logger LOG = Logger.getLogger(SparCartFacadeImpl.class);
	final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
	private Converter<CategoryModel, SparCategoryData> categoryConverter;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	private static final String LOGIN_VIA = "loginVia";
	private static final String ALL = "ALL";
	private static final String DELIVERYSLOT_DISABLE_DATETIME_FORMAT_KEY = "yyyy-MM-dd";
	private static final String DELIVERYSLOT_DETAILS_TO_DISABLE = "_spar.deliveryslot.details.disable";
	
	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Autowired
	private ModelService modelService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private SparDefaultCommerceStockService sparCommerceStockService;

	@Autowired
	private CommonI18NService commonI18NService;

	/**
	 * @return the categoryConverter
	 */
	public Converter<CategoryModel, SparCategoryData> getCategoryConverter()
	{
		return categoryConverter;
	}

	/**
	 * @param categoryConverter
	 *           the categoryConverter to set
	 */
	public void setCategoryConverter(final Converter<CategoryModel, SparCategoryData> categoryConverter)
	{
		this.categoryConverter = categoryConverter;
	}


	/*
	 * Author nikhil-ku (non-Javadoc)
	 *
	 * this method call the sparCartService getLastLevelCategoryOnProduct method to group the cart.
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#groupCartOnCategory(de.hybris.platform.commercefacades.order.data.
	 * OrderEntryData, java.util.Map)
	 */
	@Override
	public Map<SparCategoryData, List<OrderEntryData>> groupCartOnCategory(final OrderEntryData entry,
			final Map<SparCategoryData, List<OrderEntryData>> catToOrdrEntryMap)
	{
		final ProductData product = entry.getProduct();

		final CategoryModel categoryL1 = getCartService().getLastLevelCategoryOnProduct(product.getCode());
		final SparCategoryData categoryData = getCategoryConverter().convert(categoryL1);
		if (!catToOrdrEntryMap.containsKey(categoryData))
		{
			catToOrdrEntryMap.put(categoryData, new ArrayList<OrderEntryData>());
		}


		catToOrdrEntryMap.get(categoryData).add(entry);

		return catToOrdrEntryMap;
	}

	@Override
	public CartData getSessionCart()
	{

		CartData cartData = null;
		if (hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			// code for removing unapproved products from cart model START --PALLAVI
			List<AbstractOrderEntryModel> datalist = null;
			if (cartModel.getEntries() != null && !cartModel.getEntries().isEmpty())
			{
				datalist = new ArrayList<AbstractOrderEntryModel>(cartModel.getEntries());
				for (final java.util.Iterator<AbstractOrderEntryModel> i = datalist.iterator(); i.hasNext();)
				{
					try
					{
						final AbstractOrderEntryModel entry = i.next();
						updateCartForCleanup(cartModel, entry);
						if (ArticleApprovalStatus.UNAPPROVED.equals(entry.getProduct().getApprovalStatus()))
						{
							final Map<Integer, Long> quantities = new HashMap<Integer, Long>();
							quantities.put(entry.getEntryNumber(), Long.valueOf(0));
							getCartService().updateQuantities(cartModel, quantities);

						}
					}
					catch (final Exception e)
					{
						LOG.error("Product not found at cart");
					}
				}

			}
			// code for removing unapproved products from cart model END --PALLAVI
			try
			{
				cartData = getCartConverter().convert(cartModel);
				updateCartSavings(cartData);
			}
			catch (final Exception e)
			{
				LOG.error("Product not found at cart");
			}
			//final CartData cartData = sparCartFacade.getSessionCartWithEntryOrdering(true);

		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	protected void updateCartForCleanup(final CartModel cartModel, final AbstractOrderEntryModel entry)
	{
		boolean isPriceUnavailable = true;
		final String currentPOSName = sessionService.getAttribute("selectedStore");
		final Long stockForPOS = sparCommerceStockService.getStockLevelForProductAndPointOfService(entry.getProduct(),
				storeFinderServiceInterface.getPointOfService());
		final Collection<PriceRowModel> prices = entry.getProduct().getEurope1Prices();
		for (final PriceRowModel price : prices)
		{
			final SparPriceRowModel sparPrice = (SparPriceRowModel) price;
			if (null != currentPOSName && storeFinderServiceInterface.getWarehouse(currentPOSName).getName()
					.equalsIgnoreCase(sparPrice.getWarehouse().getName()))
			{
				isPriceUnavailable = false;
			}
		}

		if (isPriceUnavailable || stockForPOS.longValue() <= 0)
		{
			final Map<Integer, Long> quantities = new HashMap<Integer, Long>();
			quantities.put(entry.getEntryNumber(), Long.valueOf(0));
			getCartService().updateQuantities(cartModel, quantities);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#checkCartQualifyForOrderLimits()
	 */
	@Override
	public List<String> checkCartQualifyForOrderLimits()
	{
		List<String> statusCodes = new ArrayList<String>();
		final CartModel cart = getCartService().getSessionCart();

		if (cart != null)
		{
			statusCodes = getCartService().isCartQualifyForOrderLimits(cart.getTotalPrice(), cart.getDeliveryMode());

		}

		return statusCodes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#setDeliveryMode(java.lang.String)
	 */
	@Override
	public boolean setDeliveryMode(final String deliveryModeCode)
	{

		validateParameterNotNullStandardMessage("deliveryModeCode", deliveryModeCode);

		final CartModel cartModel = getCart();
		if (cartModel != null)
		{

			final DeliveryModeModel deliveryModeModel = getDeliveryService().getDeliveryModeForCode(deliveryModeCode);
			if (deliveryModeModel != null)
			{
				final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(cartModel);
				parameter.setDeliveryMode(deliveryModeModel);
				return getCartService().setDeliveryMode(parameter);
			}

		}
		return false;
	}

	@Override
	public void updateCartSavings(final CartData cartData)
	{
		final CartModel cartModel = getCart();
		if (cartData.getEntries() != null)
		{
			BigDecimal orderSavings = BigDecimal.ZERO;
			double savingsMRP = 0.0;
			for (final OrderEntryData cartDataEntry : cartData.getEntries())
			{
				for (final AbstractOrderEntryModel cartModelEntry : cartModel.getEntries())
				{
					if (StringUtils.equals(cartDataEntry.getProduct().getCode(), cartModelEntry.getProduct().getCode())
							&& cartDataEntry.getSavings() != null)
					{
						if (cartDataEntry.getBasePrice().getValue().doubleValue() == 0.0
								&& cartModelEntry.getBasePrice().doubleValue() == 0.0)
						{
							if (cartDataEntry.getEntryNumber() == cartModelEntry.getEntryNumber())
							{
								final SparVariantProductModel sparVariantProductModel = (SparVariantProductModel) cartModelEntry
										.getProduct();
								savingsMRP = updateSavingsToMRP(sparVariantProductModel, cartModel.getOrderWarehouse().getName());
								cartModelEntry.setSavings(Double.valueOf(savingsMRP * cartDataEntry.getQuantity().longValue()));
								orderSavings = orderSavings.add(BigDecimal.valueOf(savingsMRP * cartDataEntry.getQuantity().longValue()));
								modelService.save(cartModelEntry);
							}
						}
						else
						{
							if (cartDataEntry.getBasePrice().getValue().doubleValue() > 0.0
									&& cartModelEntry.getBasePrice().doubleValue() > 0.0)
							{
								cartModelEntry.setSavings(Double.valueOf(cartDataEntry.getSavings().getValue().doubleValue()));
								orderSavings = orderSavings.add(cartDataEntry.getSavings().getValue());
								modelService.save(cartModelEntry);
							}
						}
					}
				
				}
			}

			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderSavings,
					commonI18NService.getCurrentCurrency());
			cartData.setSavings(priceData);
			cartModel.setSavings(Double.valueOf(priceData.getValue().doubleValue()));
			if (null != sessionService.getAttribute(LOGIN_VIA))
			{
				cartModel.setOrderVia(sessionService.getAttribute(LOGIN_VIA).toString());
				sessionService.setAttribute(LOGIN_VIA, null);
				sessionService.removeAttribute(LOGIN_VIA);
			}
			modelService.save(cartModel);
			modelService.refresh(cartModel);
		}
	}

	private double updateSavingsToMRP(final VariantProductModel variantProductModel, final String selectedStoreName)
	{
		double priceData = 0.0;

		final Collection<PriceRowModel> priceRowModels = variantProductModel.getEurope1Prices();
		for (final PriceRowModel priceRowModel : priceRowModels)
		{
			if (priceRowModel instanceof SparPriceRowModel)
			{
				final WarehouseModel warehouse = ((SparPriceRowModel) priceRowModel).getWarehouse();
				if (StringUtils.equalsIgnoreCase(warehouse.getName(), selectedStoreName))
				{
					priceData = ((SparPriceRowModel) priceRowModel).getUnitMRP().doubleValue();
				}
			}
		}
		return priceData;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#setOrderWarehouse(java.lang.String)
	 */
	@Override
	public void setOrderWarehouse(final String pointOfService)
	{
		setOrderWarehouse(pointOfService, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#setOrderPointOfService(java.lang.String)
	 */
	@Override
	public void setOrderPointOfService(final String pointOfService)
	{
		final CartModel cartModel = getCart();
		if (null != cartModel)
		{
			cartModel.setOrderPointOfService(getStoreFinderServiceInterface().getPosStore(pointOfService));
			getCartService().saveOrder(cartModel);
			LOG.debug("Saving  PointOfService : " + pointOfService + " in Cart : " + cartModel.getCode());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#setOrderWarehouse(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void setOrderWarehouse(final String pointOfService, final boolean isRecaluclationRequired)
	{
		final CartModel cartModel = getCart();
		if (null != cartModel)
		{
			final WarehouseModel warehouse = getStoreFinderServiceInterface().getWarehouse(pointOfService);
			cartModel.setOrderWarehouse(warehouse);
			getCartService().saveOrder(cartModel);
			LOG.debug("Saving warehouse : " + warehouse.getCode() + " in Cart : " + cartModel.getCode());
			if (isRecaluclationRequired)
			{
				for (final AbstractOrderEntryModel entry : cartModel.getEntries())
				{
					updateCartForCleanup(cartModel, entry);
				}
				final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
				commerceCartParameter.setEnableHooks(true);
				commerceCartParameter.setCart(cartModel);
				getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);
			}
		}
	}

	protected CartModel getCart()
	{
		if (hasCheckoutCart())
		{
			return getCartService().getSessionCart();
		}

		return null;
	}

	public boolean hasCheckoutCart()
	{
		return hasSessionCart();
	}

	/**
	 * Getter
	 *
	 * @return the storeFinderServiceInterface
	 */
	public StoreFinderServiceInterface getStoreFinderServiceInterface()
	{
		return storeFinderServiceInterface;
	}

	/**
	 * Setter
	 *
	 * @param storeFinderServiceInterface
	 *           the storeFinderServiceInterface to set
	 */
	public void setStoreFinderServiceInterface(final StoreFinderServiceInterface storeFinderServiceInterface)
	{
		this.storeFinderServiceInterface = storeFinderServiceInterface;
	}

	@Override
	protected SparCartService getCartService()
	{
		return (SparCartService) super.getCartService();
	}

	/**
	 * Getter for CommerceCartCalculationStrategy
	 *
	 * @return the commerceCartCalculationStrategy
	 */
	public CommerceCartCalculationStrategy getCommerceCartCalculationStrategy()
	{
		return commerceCartCalculationStrategy;
	}

	/**
	 * Setter for CommerceCartCalculationStrategy
	 *
	 * @param commerceCartCalculationStrategy
	 *           the commerceCartCalculationStrategy to set
	 */
	public void setCommerceCartCalculationStrategy(final CommerceCartCalculationStrategy commerceCartCalculationStrategy)
	{
		this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.cart.SparCartFacade#setCncPhone(java.lang.String)
	 */
	@Override
	public void setCncPhone(final String cncPhone)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		cartModel.setCncPhone(cncPhone);
		modelService.save(cartModel);
	}

	/**
	 * This method is used to reset the cnc phone number after order confirmation through CNC or changing the order flow
	 * from CNC to HD
	 */
	@Override
	public void resetCncPhoneNumber()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		cartModel.setCncPhone(StringUtils.EMPTY);
		modelService.save(cartModel);
	}

	@Override
	public Map<Date, List<DeliverySlotData>> getDeliverySlotsMap(final String deliveryType)
	{
		final List<DeliverySlotData> dSlots = storeFinderFacadeInterface.getDeliverySlots(deliveryType);
		final Map<Date, List<DeliverySlotData>> map = new TreeMap<Date, List<DeliverySlotData>>();
		final Calendar cal = Calendar.getInstance();
		final List<Date> dateTest = new ArrayList<Date>();
		final List<Integer> slotId = new ArrayList<Integer>();

		for (int i = 0; i < dSlots.size(); i++)
		{
			cal.add(Calendar.DATE, dSlots.get(i).getOrderingDay());
			if (!dateTest.contains(cal.getTime()))
			{
				final String allDate = dateFormat.format(cal.getTime());
				Date allDateAvailable;
				try
				{
					allDateAvailable = dateFormat.parse(allDate);
					dateTest.add(allDateAvailable);
				}
				catch (final ParseException e)
				{
					LOG.error("Not able to parse String to Date " + e);
				}
			}
			cal.add(Calendar.DATE, -dSlots.get(i).getOrderingDay());

			for (int j = 0; j < dSlots.size(); j++)
			{
				if (Integer.valueOf(j) == dSlots.get(i).getOrderingDay())
				{
					slotId.add(dSlots.get(i).getSlotId());
				}
			}
		}
		if(CollectionUtils.isNotEmpty(dateTest))
		{
   		final List<ArrayList> totalOrder = storeFinderServiceInterface.getTotalOrderPerSlot(dateTest, slotId);
   		/* final Integer maxOrdersPerSlot = warehouseModel.getMaxOrdersPerDeliverySlot(); */
   
   		for (final DeliverySlotData slotData : dSlots)
   		{
   			try
   			{
   				calculateSlot(slotData, cal, map, totalOrder);
   			}
   			catch (final ParseException e)
   			{
   				LOG.error("Not able to parse String to Date " + e);
   			}
   		}
		}
		return map;
	}

	/**
	 * @param dSlot
	 * @param cal
	 * @param map
	 * @param maxOrdersPerSlot
	 * @param totalOrder
	 * @throws ParseException
	 *
	 *            This method calculate the date dynamically for each ordering day of delivery slot.
	 *
	 */
	private void calculateSlot(final DeliverySlotData dSlot, final Calendar cal, final Map<Date, List<DeliverySlotData>> map,
			final List<ArrayList> totalOrder) throws ParseException
	{

		cal.add(Calendar.DATE, dSlot.getOrderingDay());
		createDeliverySlotMap(cal, map, dSlot, totalOrder);
		cal.add(Calendar.DATE, -dSlot.getOrderingDay());
	}

	/**
	 * @param cal
	 * @param map
	 * @param maxOrdersPerSlot
	 * @param totalOrder
	 * @throws ParseException
	 *
	 *            This method creates map with Date as key and delivery slots as list to each key. Also checks for the
	 *            max order placed for each slot with max order allowed per slot.
	 *
	 */
	@SuppressWarnings("boxing")
	private void createDeliverySlotMap(final Calendar cal, final Map<Date, List<DeliverySlotData>> map,
			final DeliverySlotData deliverySlot, final List<ArrayList> totalOrder) throws ParseException
	{
		final Integer slotId = deliverySlot.getSlotId();
		final String deliveryDate = dateFormat.format(cal.getTime());
		final Date orderDateAvailable = dateFormat.parse(deliveryDate);
		final List<DeliverySlotData> deliverySlotlist = (map.get(orderDateAvailable) == null ? new ArrayList<DeliverySlotData>()
				: map.get(orderDateAvailable));


		if (null != totalOrder && !totalOrder.isEmpty())
		{
			for (int position = 0; position < totalOrder.size(); position++)
			{
				if (totalOrder.get(position).size() == 3 && (Integer) (totalOrder.get(position)).get(1) == slotId.intValue()
						&& (totalOrder.get(position)).get(2).equals(orderDateAvailable))
				{
					if (null != deliverySlot.getSlotOrdersCapacity()
							&& ((Integer) (totalOrder.get(position)).get(0)) < deliverySlot.getSlotOrdersCapacity())
					{
						deliverySlot.setAvailableSlot(Boolean.TRUE);
					}
					else
					{
						deliverySlot.setAvailableSlot(Boolean.FALSE);
						break;
					}
				}
				else
				{
					deliverySlot.setAvailableSlot(Boolean.TRUE);
				}

			}
		}
		
		//Code change start to disable delivery slot specified in local.properties file 
		try
		{
   		final CartModel cartModel = getCartService().getSessionCart();
   		if(null != cartModel.getOrderWarehouse())
   		{
   			String deliveryslotDetailsToDisable = Config.getString(cartModel.getOrderWarehouse().getCode()+DELIVERYSLOT_DETAILS_TO_DISABLE
   					, cartModel.getOrderWarehouse().getCode()+DELIVERYSLOT_DETAILS_TO_DISABLE);
   			if(null != deliveryslotDetailsToDisable)
   			{
   				String[] deliverySlotDetails = deliveryslotDetailsToDisable.split("\\|");
   				if(null != deliverySlotDetails && deliverySlotDetails.length == 2)
   				{
   					 String deliveryslotDatetimeToDisable = deliverySlotDetails[0];
            		 SimpleDateFormat sdf = new SimpleDateFormat(DELIVERYSLOT_DISABLE_DATETIME_FORMAT_KEY);
                   Date disablingDate = sdf.parse(deliveryslotDatetimeToDisable);
                   String formattedDeliverySlotDate = sdf.format(orderDateAvailable);
                   Date deliverySlotDate = sdf.parse(formattedDeliverySlotDate);
                   
                   String deliverySlotListTemp = deliverySlotDetails[1];
                   if(disablingDate.compareTo(deliverySlotDate) == 0 && null != deliverySlotListTemp)
                   {
               		 if(deliverySlotListTemp.equalsIgnoreCase(ALL))
               		 {
               			 deliverySlot.setAvailableSlot(Boolean.FALSE);
            				 deliverySlot.setActive(Boolean.FALSE);
               		 }
               		 else
               		 {
                  		 for(String slot : deliverySlotListTemp.split(","))
                  		 {
                  			 String slotTemp = slot.replaceAll("\\s", "");
                  			 String slotDescriptionTemp = deliverySlot.getSlotDescription().replaceAll("\\s", "");
                  			 if(slotTemp.equalsIgnoreCase(slotDescriptionTemp))
                  			 {
                  				 deliverySlot.setAvailableSlot(Boolean.FALSE);
                  				 deliverySlot.setActive(Boolean.FALSE);
                  			 }
                  		 }
               		 }
                   }		
   				}
   			}
   		}
		}
		catch(Exception e)
		{
			LOG.error("Not able to process delivery slot disabling " + e);
		}
		//Code change end to disable delivery slot specified in local.properties file 
		
		deliverySlotlist.add(deliverySlot);
		Collections.sort(deliverySlotlist, new DeliverySlotComparator());
		map.put(orderDateAvailable, deliverySlotlist);
	}

	/**
	 * This class is a comparator class which sorts the list of DeliverySlotData on SlotId
	 *
	 * @author tanveers
	 *
	 */
	class DeliverySlotComparator implements Comparator<DeliverySlotData>
	{

		@Override
		public int compare(final DeliverySlotData o1, final DeliverySlotData o2)
		{
			try
			{
				final Integer slotId1 = o1.getSlotId();
				final Integer slotId2 = o2.getSlotId();
				if (null == slotId1 || null == slotId2)
				{
					// In case if Slot Id is null then no sort should be done.
					return 0;
				}
				return slotId1.compareTo(slotId2);
			}
			catch (final Exception e)
			{
				//In case of any failure
				LOG.warn("Could not compare Slot Id", e);
				return 0;
			}
		}
	}


	/**
	 * @param orderVia
	 */
	@Override
	public void updateSocialMediaToCart(final String orderVia)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		cartModel.setOrderVia(orderVia);
		modelService.save(cartModel);
	}
}
