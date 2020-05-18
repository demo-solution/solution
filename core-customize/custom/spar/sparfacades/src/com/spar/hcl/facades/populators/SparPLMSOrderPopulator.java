package com.spar.hcl.facades.populators;
import de.hybris.platform.catalog.impl.DefaultCatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.plano.model.PlanoGramModel;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.facades.plms.SparOMSOrderData;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;

public class SparPLMSOrderPopulator implements Populator<AbstractOrderEntryModel, SparOMSOrderData>
{
	private static final Logger LOGGER = Logger.getLogger(SparPLMSOrderPopulator.class);
	private ModelService modelService;
	private PromotionsService promotionsService;
	private PriceService priceService;
	private SparCartService sparCartService;
	private StoreFinderServiceInterface storeFinderServiceInterface;
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@Resource
	private DefaultCatalogVersionService catalogVersionService;




	@Override
	public void populate(final AbstractOrderEntryModel source, final SparOMSOrderData target) throws ConversionException
	{
		populateOrderLevelDetails(source, target);
		populateProductDetails(source, target);
		populateOnlineMarketDetail(source, target);
		populatePlanogramDetail(source, target);
		populateProductPriceAndQuantity(source, target);
		populateProductPromotions(source, target);
	}

	/**
	 * This method populates order number and delivery information
	 *
	 * @param source
	 * @param target
	 */
	private void populateOrderLevelDetails(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{
		final AbstractOrderModel orderModel = source.getOrder();
		if (StringUtils.isNotBlank(orderModel.getCode()))
		{
			target.setOrderNo(orderModel.getCode());
		}
		if (StringUtils.isNotBlank(orderModel.getOrderWarehouse().getCode()))
		{
			target.setStoreId(orderModel.getOrderWarehouse().getCode());
		}
		if (StringUtils.isNotBlank(orderModel.getDeliverySlot()))
		{
			target.setDeliverySlot(orderModel.getDeliverySlot());
		}
		if (StringUtils.isNotBlank(orderModel.getDeliveryDate()))
		{
			final DateFormat format = new SimpleDateFormat("EEE MMM dd yyy");
			try
			{
				final Date date = format.parse(orderModel.getDeliveryDate());
				final DateFormat ddformat = new SimpleDateFormat("MMM dd,yyy");
				target.setDeliveryDate(ddformat.format(date));
			}
			catch (final ParseException e)
			{
				LOGGER.error("Error in parsing the date format populateOrderLevelDetails::::"+ e);
			}

		}
	}



	/**
	 * This method populates marketing details
	 *
	 * @param source
	 * @param target
	 */
	private void populateOnlineMarketDetail(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{
		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");

		final CatalogVersionModel CatalogVersionModel = catalogVersionService
				.getSessionCatalogVersionForCatalog("sparProductCatalog");

		final CategoryModel categoryModel = sparCartService.getSecondLevelCategoryOnProduct(CatalogVersionModel, source
				.getProduct().getCode());
		
		final ItemModelContextImpl itemModelContext = (ItemModelContextImpl) categoryModel.getItemModelContext();
		if (null == itemModelContext.getLocaleProvider(false))
		{
			final LocaleProvider localeProvider = new StubLocaleProvider(Locale.ENGLISH);
			itemModelContext.setLocaleProvider(localeProvider);
		}

		if (StringUtils.isNotBlank(categoryModel.getCode()))
		{
			target.setOnlineMarketCode(categoryModel.getCode());
		}
		if (StringUtils.isNotBlank(categoryModel.getName()))
		{
			target.setOnlineMarketDesc(categoryModel.getName());
		}
	}



	/**
	 * This method used to display the planogram detail for each product in order level entry
	 *
	 * @param source
	 * @param target
	 */
	private void populatePlanogramDetail(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{
		final String storeId = source.getOrder().getOrderWarehouse().getCode();
		final ProductModel productModel = source.getProduct();

		final List<PlanoGramModel> PlanoGramModelList = storeFinderServiceInterface.getPlanogramDetail(productModel.getCode(),
				storeId);
		if (CollectionUtils.isNotEmpty(PlanoGramModelList))
		{
			if (null != PlanoGramModelList.get(0))
			{
				final PlanoGramModel planoGramModel = PlanoGramModelList.get(0);
				target.setAisleNo(planoGramModel.getAisleNo());
				target.setBay(planoGramModel.getBay());
				target.setShelf(planoGramModel.getShelf());
				target.setPosition(planoGramModel.getPosition());
				target.setFacing(planoGramModel.getFacing());
			}
		}
	}



	/**
	 * This method is used to display the price and quantity information
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductPriceAndQuantity(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{
		final ProductModel productModel = source.getProduct();

		target.setLevelThreeCategory((productModel.getSupercategories().iterator().next()).getName());
		target.setCsp(source.getBasePrice().intValue());
		target.setQty(source.getQuantity());

		final Collection<PriceRowModel> priceRowModels = productModel.getEurope1Prices();
		for (final PriceRowModel priceRowModel : priceRowModels)
		{
			final String warehouseCode = ((SparPriceRowModel) priceRowModel).getWarehouse().getCode();
			if (warehouseCode.equalsIgnoreCase(source.getOrder().getOrderWarehouse().getCode()))
			{
				target.setPromoCheck(((SparPriceRowModel) priceRowModel).getCheckForPromotion());
			}
		}

		target.setMrp(getMaxMRP(source));
	}

	private Double getMaxMRP(final AbstractOrderEntryModel source)
	{
		Double unitMRP=null;
		final ProductModel productModel = source.getProduct();
		if (null != productModel.getEurope1Prices() && !productModel.getEurope1Prices().isEmpty()
				&& productModel.getEurope1Prices().iterator().hasNext())
		{
			final Collection<PriceRowModel> sparPriceCollection = productModel.getEurope1Prices();
			for (final PriceRowModel price : sparPriceCollection)
			{
				final String priceWareHouseCode = ((SparPriceRowModel) price).getWarehouse().getCode();
				if (StringUtils.equalsIgnoreCase(priceWareHouseCode, source.getOrder().getOrderWarehouse().getCode()))
				{
					unitMRP = ((SparPriceRowModel) price).getUnitMRP();
				}
			}
		}
		return unitMRP;
	}

	/**
	 * This method is used to display the promotion related information in product level
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductPromotions(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{

		final ProductModel productModel = source.getProduct();
		Collection<ProductPromotionModel> promotions= productModel.getPromotions();
		if(null != promotions && !promotions.isEmpty())
		{
			ProductPromotionModel productPromotionModel = promotions.iterator().next();
			target.setPromoCode(productPromotionModel.getCode());
			target.setPromoDesc(productPromotionModel.getDescription());
		}
	}


/*	private List<PromotionResultData> getPromotions(final List<PromotionResult> promotionsResults)
	{
		final ArrayList<PromotionResultModel> promotionResultModels = modelService.getAll(promotionsResults,
				new ArrayList<PromotionResultModel>());
		return Converters.convertAll(promotionResultModels, getPromotionResultConverter());
	}*/


	/**
	 * This method is used to display the item desc related information
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductDetails(final AbstractOrderEntryModel source, final SparOMSOrderData target)
	{
		final ProductModel productModel = source.getProduct();
		if (null != productModel.getStorageType())
		{
			target.setStorageType(productModel.getStorageType());
		}
		if (StringUtils.isNotBlank(productModel.getProductType()))
		{
			target.setProductType(productModel.getProductType());
		}
		if (StringUtils.isNotBlank(productModel.getItemPickingTime()))
		{
			target.setItemPickingTime(productModel.getItemPickingTime());
		}

		target.setItemCode(productModel.getCode());
		target.setItemDesc(productModel.getName());
	}

	/**
	 * @return the promotionResultConverter
	 */
	public Converter<PromotionResultModel, PromotionResultData> getPromotionResultConverter()
	{
		return promotionResultConverter;
	}

	/**
	 * @param promotionResultConverter
	 *           the promotionResultConverter to set
	 */
	public void setPromotionResultConverter(final Converter<PromotionResultModel, PromotionResultData> promotionResultConverter)
	{
		this.promotionResultConverter = promotionResultConverter;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	public PriceService getPriceService()
	{
		return priceService;
	}

	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	public SparCartService getSparCartService()
	{
		return sparCartService;
	}

	public void setSparCartService(final SparCartService sparCartService)
	{
		this.sparCartService = sparCartService;
	}

	public StoreFinderServiceInterface getStoreFinderServiceInterface()
	{
		return storeFinderServiceInterface;
	}

	public void setStoreFinderServiceInterface(final StoreFinderServiceInterface storeFinderServiceInterface)
	{
		this.storeFinderServiceInterface = storeFinderServiceInterface;
	}
}
