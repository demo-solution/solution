/**
 * @author kumari-p
 *
 *         Added for displaying delivery date in order history page
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.OrderHistoryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.spar.hcl.deliveryslot.model.DeliverySlotModel;


public class SparOrderHistoryPopulator extends OrderHistoryPopulator
{
	protected static final Logger LOG = Logger.getLogger(SparOrderHistoryPopulator.class);
	private Converter<AbstractOrderModel, OrderData> orderConverter;
	private CommonI18NService commonI18NService;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "modelService")
	private ModelService modelService;

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(final OrderModel source, final OrderHistoryData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		checkOrderEntries(source);
		super.populate(source, target);
		final Date deliveryDate = source.getSlotDeliveryDate();
		final DeliverySlotModel deliverySlot = source.getOrderDeliverySlot();
		if (deliverySlot != null && deliveryDate != null)
		{
			target.setDelivered(deliveryDate);

		}
		populateOrderData(source, target);
		populateOrderSavings(source, target);
		if (null != source.getPaidByWallet())
		{
			final PriceData paidByWallet = getPriceDataFactory().create(PriceDataType.BUY,
					BigDecimal.valueOf(source.getPaidByWallet().doubleValue()), getCommonI18NService().getCurrentCurrency());
			target.setPaidByWallet(paidByWallet);
		}

	}

	/**
	 * This method is used to set saving in OrderHistoryData
	 *
	 * @param target
	 * @throws ConversionException
	 */
	public void populateOrderSavings(final OrderModel source, final OrderHistoryData target) throws ConversionException
	{
		BigDecimal orderSavings = BigDecimal.ZERO;
		BigDecimal orderEntrySavings = BigDecimal.ZERO;
		for (final OrderEntryData orderItem : target.getOrderData().getEntries())
		{
			for (final AbstractOrderEntryModel entryModel : source.getEntries())
			{
				if (null != entryModel && StringUtils.equals(orderItem.getProduct().getCode(), entryModel.getProduct().getCode())
						&& null != entryModel.getSavings())
				{
					if (orderItem.getBasePrice().getValue().doubleValue() == entryModel.getBasePrice().doubleValue())
					{
						orderSavings = orderSavings.add(BigDecimal.valueOf(entryModel.getSavings().doubleValue()));
						orderEntrySavings = BigDecimal.valueOf(entryModel.getSavings().doubleValue());
						// populating combi offer attribute for order entry data
						orderItem.setCombiOfferApplied(entryModel.isCombiOffer());
					}
				}
				final PriceData priceEntryData = getPriceDataFactory().create(PriceDataType.BUY, orderEntrySavings,
						getCommonI18NService().getCurrentCurrency());
				orderItem.setSavings(priceEntryData);
			}
		}
		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderSavings,
				getCommonI18NService().getCurrentCurrency());
		target.getOrderData().setSavings(priceData);
		target.setSavings(priceData);
	}


	/**
	 * Getter
	 *
	 * @return the priceDataFactory
	 */
	@Override
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Setter
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Override
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}


	public void populateOrderData(final OrderModel source, final OrderHistoryData target) throws ConversionException
	{
		target.setOrderData(getOrderConverter().convert(source));
	}

	/**
	 * @return the orderConverter
	 */
	public Converter<AbstractOrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * @param orderConverter
	 *           the orderConverter to set
	 */
	public void setOrderConverter(final Converter<AbstractOrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	private void checkOrderEntries(final OrderModel orderModel)
	{
		final List<AbstractOrderEntryModel> orderEntries = orderModel.getEntries();
		if (orderEntries != null && CollectionUtils.isNotEmpty(orderEntries))
		{
			for (final AbstractOrderEntryModel orderEntry : orderEntries)
			{
				if (orderEntry != null && Objects.isNull(orderEntry.getProduct()))
				{
					addProduct(orderEntry);
					LOG.debug("Order has been updated ...." + orderModel.getCode());
				}
			}
		}
	}

	private void addProduct(final AbstractOrderEntryModel orderEntry)
	{
		final String productCode = orderEntry.getInfo().split("\"")[1];
		if (StringUtils.isNotEmpty(productCode))
		{
			try
			{
				final ProductModel productModel = productService.getProductForCode(productCode);
				if (productModel != null)
				{
					orderEntry.setProduct(productModel);
					modelService.save(orderEntry);
					LOG.debug("Added Product code in empty OrderEntry...." + productModel.getCode());
				}
			}
			catch (final ModelNotFoundException ex)
			{
				LOG.error("No product found." + ex);
			}
			catch (final Exception ex)
			{
				LOG.error("Exception occured" + ex);
			}
		}
	}
}
