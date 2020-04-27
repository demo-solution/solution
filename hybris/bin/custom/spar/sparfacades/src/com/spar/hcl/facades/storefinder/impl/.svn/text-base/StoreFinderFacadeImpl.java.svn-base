/**
 *
 */
package com.spar.hcl.facades.storefinder.impl;

import de.hybris.platform.commercefacades.storefinder.impl.DefaultStoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.core.model.service.delivery.SparCitiesModel;
import com.spar.hcl.core.stock.SparCommerceStockService;
import com.spar.hcl.core.storefinder.StoreFinderServiceHelperInterface;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;
import com.spar.hcl.facades.service.delivery.data.SparCitiesData;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;




/**
 * @author valechar/tanveers
 *
 */
public class StoreFinderFacadeImpl extends DefaultStoreFinderFacade implements StoreFinderFacadeInterface
{



	protected static final Logger LOG = Logger.getLogger(StoreFinderFacadeImpl.class);

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Autowired
	private SparCommerceStockService sparCommerceStockService;

	@Autowired
	private SessionService sessionService;

	private StoreFinderServiceInterface storeFinderServiceInterface;
	private StoreFinderServiceHelperInterface storeFinderServiceHelperInterface;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private Converter<DeliverySlotModel, DeliverySlotData> deliverySlotConverter;
	private Converter<WarehouseModel, WarehouseData> warehouseConverter;
	//private Converter<SparServiceAreaModel, SparServiceAreaData> serviceAreaConverter;
	private Converter<SparCitiesModel, SparCitiesData> sparCitiesConverter;
	final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");

	/**
	 * @return the storeFinderServiceInterface
	 */
	public StoreFinderServiceHelperInterface getStoreFinderServiceHelperInterface()
	{
		return storeFinderServiceHelperInterface;
	}

	@Required
	public void setStoreFinderServiceHelperInterface(final StoreFinderServiceHelperInterface storeFinderServiceHelperInterface)
	{
		this.storeFinderServiceHelperInterface = storeFinderServiceHelperInterface;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getStoresList()
	 */
	@Override
	public List<PointOfServiceData> getStoresList()
	{
		final List<PointOfServiceModel> pointOfServiceModels = storeFinderServiceInterface.getStores();
		return Converters.convertAll(pointOfServiceModels, getPointOfServiceConverter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getStoresList()
	 */
	@Override
	public List<PointOfServiceData> getMappedStore(final String entryCode, final String postalCode)
	{
		final List<PointOfServiceModel> pointOfServiceModels = storeFinderServiceInterface.getMappedStore(entryCode, postalCode);
		return Converters.convertAll(pointOfServiceModels, getPointOfServiceConverter());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface#getPointOfService()
	 */
	@Override
	public PointOfServiceData getPointOfService()
	{
		final PointOfServiceModel pointOfServiceModel = storeFinderServiceInterface.getPointOfService();
		return getPointOfServiceConverter().convert(pointOfServiceModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getDefaultStore()
	 */
	@Override
	public PointOfServiceData getDefaultStore()
	{
		final PointOfServiceModel pointOfServiceModel = storeFinderServiceInterface.getDefaultStore();
		return getPointOfServiceConverter().convert(pointOfServiceModel);
	}

	@Override
	public PointOfServiceData getPosStore(final String pos)
	{
		final PointOfServiceModel pointOfServiceModel = storeFinderServiceInterface.getPosStore(pos);
		return getPointOfServiceConverter().convert(pointOfServiceModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getDeliveryTypes()
	 */
	@Override
	public List<DeliveryTypeEnum> getDeliveryTypes()
	{
		final List<DeliveryTypeEnum> deliveryTypeEnums = storeFinderServiceInterface.getDeliveryTypes();
		return deliveryTypeEnums;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getDeliverySlots(java.lang.String)
	 */
	@Override
	public List<DeliverySlotData> getDeliverySlots(final String deliveryType)
	{
		final List<DeliverySlotModel> deliverySlotModels;
		if (deliveryType.equalsIgnoreCase("pickup"))
		{
			deliverySlotModels = storeFinderServiceInterface.getDeliverySlots("CNC");
		}
		else
		{
			deliverySlotModels = storeFinderServiceInterface.getDeliverySlots("HD");
		}
		return Converters.convertAll(deliverySlotModels, getDeliverySlotConverter());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hcl.spar.facades.storefinder.StoreFinderFacadeInterface#getDeliverySlotsForUserLocation(java.lang.String)
	 */
	@Override
	public List<DeliverySlotData> getDeliverySlotsForUserLocation(final String deliveryType, final String posName)
	{
		final List<DeliverySlotModel> deliverySlotModels;
		if (deliveryType.equalsIgnoreCase("pickup"))
		{
			deliverySlotModels = storeFinderServiceHelperInterface.getDeliverySlotsForUserLocation("CNC", posName);
		}
		else
		{
			deliverySlotModels = storeFinderServiceHelperInterface.getDeliverySlotsForUserLocation("HD", posName);
		}
		return Converters.convertAll(deliverySlotModels, getDeliverySlotConverter());
	}

	/* public List<PointOfServiceData> getPosForCity(final String cityName, final String posType) */
	@Override
	public List<PointOfServiceData> getPosForCity()
	{
		//final List<PointOfServiceModel> pointOfServiceModel = storeFinderServiceInterface.getPosForCity(cityName, posType);
		final List<PointOfServiceModel> pointOfServiceModel = storeFinderServiceInterface.getPosForCity();
		return Converters.convertAll(pointOfServiceModel, getPointOfServiceConverter());
	}

	/**
	 * @return the storeFinderServiceInterface
	 */
	public StoreFinderServiceInterface getStoreFinderServiceInterface()
	{
		return storeFinderServiceInterface;
	}

	/**
	 * @param storeFinderServiceInterface
	 *           the storeFinderServiceInterface to set
	 */
	@Required
	public void setStoreFinderServiceInterface(final StoreFinderServiceInterface storeFinderServiceInterface)
	{
		this.storeFinderServiceInterface = storeFinderServiceInterface;
	}


	/**
	 * @return the pointOfServiceConverter
	 */
	@Override
	public Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	/**
	 * @param pointOfServiceConverter
	 *           the pointOfServiceConverter to set
	 */
	@Override
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	/**
	 * @return the deliverySlotConverter
	 */
	public Converter<DeliverySlotModel, DeliverySlotData> getDeliverySlotConverter()
	{
		return deliverySlotConverter;
	}

	/**
	 * @param deliverySlotConverter
	 *           the deliverySlotConverter to set
	 */
	public void setDeliverySlotConverter(final Converter<DeliverySlotModel, DeliverySlotData> deliverySlotConverter)
	{
		this.deliverySlotConverter = deliverySlotConverter;
	}

	/*
	 * @Override public List<SparServiceAreaData> getSparServiceAreaForCity(final String city, final String area) { final
	 * List<SparServiceAreaModel> sparServiceAreaModel = storeFinderServiceInterface.getSparServiceAreaForCity(city,
	 * area); return Converters.convertAll(sparServiceAreaModel, getServiceAreaConverter()); }
	 */

	/*
	 * @Override public List<SparServiceAreaData> getSparServiceArea() { final List<SparServiceAreaModel>
	 * sparServiceAreaModel = storeFinderServiceInterface.getSparServiceArea(); return
	 * Converters.convertAll(sparServiceAreaModel, getServiceAreaConverter()); }
	 * 
	 * @Override public SparServiceAreaData getAddressServiceArea(final Integer areaId) { final SparServiceAreaModel
	 * sparServiceAreaModel = storeFinderServiceInterface.getAddressServiceArea(areaId); return
	 * getServiceAreaConverter().convert(sparServiceAreaModel); }
	 */

	/*
	 * Method to retrieve SPAR cities in the Spar Cities DB
	 */
	@Override
	public List<SparCitiesData> getSparCities()
	{
		final List<SparCitiesModel> sparCitiesModel = storeFinderServiceInterface.getSparCities();
		final List<SparCitiesModel> sparCitiesModifiableList = new ArrayList<SparCitiesModel>(sparCitiesModel);
		Collections.sort(sparCitiesModifiableList, new SparCityComparator());
		return Converters.convertAll(sparCitiesModifiableList, getSparCitiesConverter());
	}

	public Map<Date, DeliverySlotData> getDeliverySlotsForStorePOS(final String deliveryType, final String posName)
	{
		List<DeliverySlotData> dSlots = null;
		final Map<Date, List<DeliverySlotData>> map = new TreeMap<Date, List<DeliverySlotData>>();
		final Map<Date, DeliverySlotData> finalDeliverySlotResult = new TreeMap<Date, DeliverySlotData>();
		final Calendar cal = Calendar.getInstance();
		final List<Date> dateTest = new ArrayList<Date>();
		final List<Integer> slotId = new ArrayList<Integer>();
		WarehouseModel warehouseModel = null;

		if (deliveryType == "HD")
		{
			dSlots = storeFinderFacadeInterface.getDeliverySlotsForUserLocation("HD", posName);
		}
		else
		{
			dSlots = storeFinderFacadeInterface.getDeliverySlotsForUserLocation("pickup", posName);
		}

		if (!CollectionUtils.isEmpty(dSlots))
		{
			for (int i = 0; i < dSlots.size(); i++)
			{
				cal.add(Calendar.DATE, dSlots.get(i).getOrderingDay().intValue());
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

				cal.add(Calendar.DATE, -dSlots.get(i).getOrderingDay().intValue());

				for (int j = 0; j < dSlots.size(); j++)
				{
					if (Integer.valueOf(j) == dSlots.get(i).getOrderingDay())
					{
						slotId.add(dSlots.get(i).getSlotId());
					}
				}
			}

			try
			{
				if (null != posName)
				{
					warehouseModel = storeFinderServiceInterface.getWarehouse(posName);
					LOG.info("posName for Delivery Slots:::" + posName);
				}
			}
			catch (final Exception e1)
			{
				LOG.error("No Warehouse association found for the Store", e1);
			}

			final List<ArrayList> totalOrder = storeFinderServiceInterface.getTotalOrderPerSlot(dateTest, slotId);
			final Integer maxOrdersPerSlot = warehouseModel.getMaxOrdersPerDeliverySlot();
			for (final DeliverySlotData slotData : dSlots)
			{

				try
				{
					cal.add(Calendar.DATE, slotData.getOrderingDay().intValue());
					createDeliverySlotList(cal, map, slotData, totalOrder);
					cal.add(Calendar.DATE, -slotData.getOrderingDay().intValue());
				}
				catch (final ParseException e)
				{
					LOG.error("Not able to parse String to Date " + e);
				}

				final Iterator itr = map.entrySet().iterator();
				boolean delSlotsAvl = false;
				while (itr.hasNext())
				{
					final Map.Entry<Date, List<DeliverySlotData>> entry = (Entry<Date, List<DeliverySlotData>>) itr.next();
					final List<DeliverySlotData> val = entry.getValue();
					for (int i = 0; i < val.size(); i++)
					{

						if (null == val.get(i).getAvailableSlot() || val.get(i).getAvailableSlot().booleanValue())
						{
							finalDeliverySlotResult.put(entry.getKey(), val.get(i));
							delSlotsAvl = true;
							break;
						}
					}
				}

				if (delSlotsAvl)
				{
					break;
				}
			}
			return finalDeliverySlotResult;
		}
		return null;
	}

	private Map<Date, List<DeliverySlotData>> createDeliverySlotList(final Calendar cal,
			final Map<Date, List<DeliverySlotData>> map, final DeliverySlotData deliverySlot, final List<ArrayList> totalOrder)
			throws ParseException
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
				if (totalOrder.get(position).size() == 3 && (totalOrder.get(position)).get(1) == slotId
						&& (totalOrder.get(position)).get(2).equals(orderDateAvailable))
				{
					if (null != deliverySlot.getSlotOrdersCapacity()
							&& ((Integer) (totalOrder.get(position)).get(0)).intValue() < deliverySlot.getSlotOrdersCapacity()
									.intValue())
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
		deliverySlotlist.add(deliverySlot);
		Collections.sort(deliverySlotlist, new DeliverySlotComparator());
		map.put(orderDateAvailable, deliverySlotlist);
		return map;
	}


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

	/* Change end here */

	@Override
	public StoreFinderSearchPageData<PointOfServiceData> positionSearchStore(final GeoPoint geoPoint,
			final PageableData pageableData, final SparInExResponse inExResponse)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderServiceHelperInterface()
				.positionSearchStore(currentBaseStore, geoPoint, pageableData, inExResponse);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	@Override
	public StoreFinderSearchPageData<PointOfServiceData> positionSearchPOS(final GeoPoint geoPoint,
			final PageableData pageableData, final String sanitizedSearchQuery)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getStoreFinderServiceHelperInterface()
				.positionSearchPOS(currentBaseStore, geoPoint, pageableData, sanitizedSearchQuery);
		return getSearchPagePointOfServiceDistanceConverter().convert(searchPageData);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface#isProductValidForPOS()
	 */
	@Override
	public boolean isProductValidForPOS(final ProductModel product)
	{
		boolean isPriceUnavailable = true;
		final String currentPOSName = sessionService.getAttribute("selectedStore");
		final Long stockForPOS = sparCommerceStockService.getStockLevelForProductAndPointOfService(product,
				storeFinderServiceInterface.getPointOfService());
		final Collection<PriceRowModel> prices = product.getEurope1Prices();
		for (final PriceRowModel price : prices)
		{
			final SparPriceRowModel sparPrice = (SparPriceRowModel) price;
			if (storeFinderServiceInterface.getWarehouse(currentPOSName).getName()
					.equalsIgnoreCase(sparPrice.getWarehouse().getName()))
			{
				isPriceUnavailable = false;
			}
		}

		if (isPriceUnavailable || stockForPOS.longValue() <= 0)
		{
			return false;
		}
		return true;
	}

	/* Change end here */

	/**
	 * This method is used to get the warehouse data from warehousemodel
	 */
	@Override
	public WarehouseData getWarehouse(final String pos)
	{
		return getWarehouseConverter().convert(storeFinderServiceInterface.getWarehouse(pos));

	}

	/**
	 * Getter
	 *
	 * @return the warehouseConverter
	 */
	public Converter<WarehouseModel, WarehouseData> getWarehouseConverter()
	{
		return warehouseConverter;
	}

	/**
	 * Setter
	 *
	 * @param warehouseConverter
	 *           the warehouseConverter to set
	 */
	public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter)
	{
		this.warehouseConverter = warehouseConverter;
	}

	/**
	 * @return the serviceAreaConverter
	 */
	/*
	 * public Converter<SparServiceAreaModel, SparServiceAreaData> getServiceAreaConverter() { return
	 * serviceAreaConverter; }
	 *//**
	 * @param serviceAreaConverter
	 *           the serviceAreaConverter to set
	 */
	/*
	 * public void setServiceAreaConverter(final Converter<SparServiceAreaModel, SparServiceAreaData>
	 * serviceAreaConverter) { this.serviceAreaConverter = serviceAreaConverter; }
	 */

	/**
	 * @return the sparCitiesConverter
	 */
	public Converter<SparCitiesModel, SparCitiesData> getSparCitiesConverter()
	{
		return sparCitiesConverter;
	}

	/**
	 * @param sparCitiesConverter
	 *           the sparCitiesConverter to set
	 */
	public void setSparCitiesConverter(final Converter<SparCitiesModel, SparCitiesData> sparCitiesConverter)
	{
		this.sparCitiesConverter = sparCitiesConverter;
	}

	/**
	 * This class is a comparator class which sorts the list of Spar Cities name
	 *
	 * @author tanveers
	 *
	 */
	class SparCityComparator implements Comparator<SparCitiesModel>
	{

		@Override
		public int compare(final SparCitiesModel o1, final SparCitiesModel o2)
		{
			try
			{
				final String cityName1 = o1.getCity();
				final String cityName2 = o2.getCity();
				if (StringUtils.isEmpty(cityName1) || StringUtils.isEmpty(cityName2))
				{
					// In case if Slot Id is null then no sort should be done.
					return 0;
				}
				return cityName1.compareToIgnoreCase(cityName2);
			}
			catch (final Exception e)
			{
				//In case of any failure
				LOG.warn("Could not city name", e);
				return 0;
			}
		}
	}
}
