/**
 *
 */
package com.spar.hcl.core.service.interceptor.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.LoadInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;


/**
 *
 *
 */
public class FetchDeliverySlotsInterceptor implements LoadInterceptor
{

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;
	@Resource(name = "sessionService")
	SessionService sessionService;
	@Resource(name = "modelService")
	ModelService modelService;

	//final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
	protected static final Logger LOG = Logger.getLogger(FetchDeliverySlotsInterceptor.class);

	private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>()
	{
		@Override
		protected SimpleDateFormat initialValue()
		{
			return new SimpleDateFormat("EEE MMM dd yyyy");
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.LoadInterceptor#onLoad(java .lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onLoad(final Object model, final InterceptorContext ctx) throws InterceptorException
	{

		if (model instanceof OrderModel)
		{
			final OrderModel orderModel = (OrderModel) model;
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Entered in Instance of Fetch Order interceptor....");
			}
			//			if (null != orderModel.getTotalPrice() && orderModel.getTotalPrice().doubleValue() == 0.0D
			//					&& null != orderModel.getSubtotal() && orderModel.getSubtotal().doubleValue() != 0.0D)
			//			{
			//				orderModel.setSubtotal(orderModel.getTotalPrice());
			//				modelService.save(orderModel);
			//			}

			final DeliverySlotModel deliverySlotModel = orderModel.getOrderDeliverySlot();
			if (null != orderModel.getOrderPointOfService())
			{

				final WarehouseModel warehouseModel = storeFinderServiceInterface.getWarehouse(orderModel.getOrderPointOfService()
						.getName());

				sessionService.setAttribute("backofficeSelectedStore", orderModel.getOrderPointOfService().getName());
				if (null != deliverySlotModel && null != deliverySlotModel.getDeliveryType()
						&& StringUtils.isNotEmpty(deliverySlotModel.getDeliveryType().getCode()))
				{
					final List<DeliverySlotModel> dSlots = storeFinderServiceInterface.getDeliverySlots(deliverySlotModel
							.getDeliveryType().getCode());
					if (null != dSlots && !dSlots.isEmpty())
					{
						final Map<Date, List<DeliverySlotModel>> map = new TreeMap<Date, List<DeliverySlotModel>>();
						final List<String> allDeliverySlots = new ArrayList<String>();
						final Calendar cal = Calendar.getInstance();
						final List<Date> dateTest = new ArrayList<Date>();
						final List<Integer> slotId = new ArrayList<Integer>();

						for (int i = 0; i < dSlots.size(); i++)
						{
							cal.add(Calendar.DATE, dSlots.get(i).getOrderingDay().intValue());
							if (!dateTest.contains(cal.getTime()))
							{
								//final String allDate = dateFormat.format(cal.getTime());
								final String allDate = formatter.get().format(cal.getTime());
								Date allDateAvailable;
								try
								{
									//allDateAvailable = dateFormat.parse(allDate);
									allDateAvailable = formatter.get().parse(allDate);
									dateTest.add(allDateAvailable);
									for (int j = 0; j < dSlots.size(); j++)
									{
										if (Integer.valueOf(j) == dSlots.get(i).getOrderingDay())
										{
											slotId.add(dSlots.get(i).getSlotId());
										}
									}
								}
								catch (final NumberFormatException e)
								{
									LOG.error("Number Format exception " + e.getMessage());
								}
								catch (final ParseException e)
								{
									LOG.error("Not able to parse String to Date " + e);
								}
							}
							cal.add(Calendar.DATE, -dSlots.get(i).getOrderingDay().intValue());



						}
						if (!dateTest.isEmpty() && !slotId.isEmpty())
						{
							final List<ArrayList> totalOrder = storeFinderServiceInterface.getTotalOrderPerSlot(dateTest, slotId);

							/* final Integer maxOrdersPerSlot = warehouseModel.getMaxOrdersPerDeliverySlot(); */
							for (final DeliverySlotModel slotData : dSlots)
							{
								try
								{
									calculateSlot(slotData, cal, map, totalOrder, orderModel, allDeliverySlots);
									final List<String> allDeliverySlotsNew = new ArrayList<String>();

									for (final Map.Entry<Date, List<DeliverySlotModel>> entry : map.entrySet())
									{
										final String deliveryDateNew = formatter.get().format(entry.getKey());
										//final String deliveryDateNew = Long.valueOf((entry.getKey()).getTime()).toString();
										for (final DeliverySlotModel deliverySlotNew : entry.getValue())
										{

											final String deliveryDescNew = deliverySlotNew.getSlotDescription();
											allDeliverySlotsNew.add(deliveryDateNew + " - " + deliveryDescNew);
										}


									}
									/*
									 * LOG.info("allDeliverySlotsNew::::::::::::::::" + orderModel.getCode() + "::::::: " +
									 * allDeliverySlotsNew.size());
									 */
									if (orderModel.getOderRetryNoShow() != null && orderModel.getOderRetryNoShow().equals(Boolean.TRUE))
									{
										final List<String> allDeliverySlotsNewBlank = new ArrayList<String>();
										orderModel.setAllDeliverySlots(allDeliverySlotsNewBlank);
									}
									else
									{
										orderModel.setAllDeliverySlots(allDeliverySlotsNew);
									}
								}
								catch (final NumberFormatException e)
								{
									LOG.error("Number Format exception " + e.getMessage());
								}
								catch (final ParseException e)
								{
									LOG.error("Not able to parse String to Date " + e);
								}
							}
						}
					}
				}
			}
		}
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
	private void calculateSlot(final DeliverySlotModel dSlot, final Calendar cal, final Map<Date, List<DeliverySlotModel>> map,
			final List<ArrayList> totalOrder, final OrderModel orderModel, final List<String> allDeliverySlots)
			throws ParseException
	{

		cal.add(Calendar.DATE, dSlot.getOrderingDay());
		createDeliverySlotMap(cal, map, dSlot, totalOrder, orderModel, allDeliverySlots);
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
	private void createDeliverySlotMap(final Calendar cal, final Map<Date, List<DeliverySlotModel>> map,
			final DeliverySlotModel deliverySlot, final List<ArrayList> totalOrder, final OrderModel orderModel,
			final List<String> allDeliverySlots) throws ParseException
	{
		final Integer slotId = deliverySlot.getSlotId();
		//LOG.info("Entered in createDeliverySlotMap method :: slotId ::::::::::: " + slotId);
		//final String deliveryDate = dateFormat.format(cal.getTime());
		//final Date orderDateAvailable = dateFormat.parse(deliveryDate);
		final String deliveryDate = formatter.get().format(cal.getTime());
		final Date orderDateAvailable = formatter.get().parse(deliveryDate);
		String deliverySlotDescription = null;
		//LOG.info("Entered in createDeliverySlotMap:::::::::::::::: " + deliveryDate + ", --  " + orderDateAvailable);
		final Boolean whetherEmployee = isCustomerEmployee(orderModel);
		final List<DeliverySlotModel> deliverySlotlist = (map.get(orderDateAvailable) == null ? new ArrayList<DeliverySlotModel>()
				: map.get(orderDateAvailable));
		Boolean availableSlot = Boolean.TRUE;

		if (null != totalOrder && !totalOrder.isEmpty())
		{
			//LOG.info("Entered in createDeliverySlotMap method :: inside IF ::::::::::: " + totalOrder.size());
			for (int position = 0; position < totalOrder.size(); position++)
			{
				if (totalOrder.get(position).size() == 3 && (totalOrder.get(position)).get(1) == slotId
						&& (totalOrder.get(position)).get(2).equals(orderDateAvailable))
				{
					if (null != deliverySlot.getSlotOrdersCapacity() 
							&&((Integer) (totalOrder.get(position)).get(0)) < deliverySlot.getSlotOrdersCapacity())
					{
						deliverySlot.setAvailableSlot(Boolean.TRUE);
						availableSlot = Boolean.TRUE;
						deliverySlotDescription = deliverySlot.getSlotDescription();
					}
					else
					{
						deliverySlot.setAvailableSlot(Boolean.FALSE);
						availableSlot = Boolean.FALSE;
						deliverySlotDescription = deliverySlot.getSlotDescription();
						break;
					}
				}
				else
				{
					deliverySlot.setAvailableSlot(Boolean.TRUE);
					availableSlot = Boolean.TRUE;
					deliverySlotDescription = deliverySlot.getSlotDescription();
				}

			}
		}
		deliverySlotDescription = deliverySlot.getSlotDescription();
		//LOG.info("Entered in createDeliverySlotMap method :: outside IF ::::::::::: " + deliverySlotDescription);
		if (availableSlot.equals(Boolean.TRUE))
		{
			if (whetherEmployee.equals(Boolean.FALSE) && deliverySlot.getSlotDescription().contains("LMG")
					|| whetherEmployee.equals(Boolean.FALSE) && deliverySlot.getSlotDescription().contains("SPAR"))
			{
				// Delivery slot is not added to the deliverySlotlist List
			}
			else
			{
				deliverySlotlist.add(deliverySlot);
			}
		}
		Collections.sort(deliverySlotlist, new DeliverySlotComparator());
		map.put(orderDateAvailable, deliverySlotlist);
		//LOG.info("Entered in createDeliverySlotMap method :: map ::::::::::: " + map);
		//final List<String> deliverySlotDescNew = new ArrayList<String>();
		//	String deliverySlotDesc = new String();
		//	deliverySlotDesc = deliveryDate + " - " + deliverySlotDescription;
		//	deliverySlotDescNew.add(deliverySlotDesc);
		//orderModel.getAllDeliverySlots().remove(orderModel.getAllDeliverySlots());
		//	orderModel.setAllDeliverySlots(deliverySlotDescNew);
		//if (null != orderModel && null != orderModel.getAllDeliverySlots())
		///{
		//
		//	for (int status = 0; status < orderModel.getAllDeliverySlots().size(); status++)
		//	{
		//		deliverySlotDescNew.add(orderModel.getAllDeliverySlots().get(status));

		//	}
		//	deliverySlotDescNew.add(deliveryDate + " - " + deliverySlotDescription);
		//	orderModel.setAllDeliverySlots(deliverySlotDescNew);
		//	}

	}

	protected boolean isCustomerEmployee(final OrderModel orderModel)
	{
		final CustomerModel customer = (CustomerModel) orderModel.getUser();
		if (BooleanUtils.isFalse(customer.getWhetherEmployee()))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * This class is a comparator class which sorts the list of DeliverySlotModel on SlotId.
	 *
	 *
	 */
	class DeliverySlotComparator implements Comparator<DeliverySlotModel>
	{

		@Override
		public int compare(final DeliverySlotModel o1, final DeliverySlotModel o2)
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

}
