/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spar.hcl.core.constants.SparCoreConstants;
import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;


/**
 * This class extends SearchResultProductPopulator. This is used to populate Variant details for the search results.
 *
 * @author Anuj_Mittal
 *
 */
/**
 * @author ravindra.kr
 *
 */
public class SparSearchResultProductPopulator extends SearchResultProductPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(SparSearchResultProductPopulator.class);
	private static final String VARIANT_IMAGE_PLP_TYPE = "plpProduct";
	private static final String VARIANT_IMAGE_THUMBNAIL_PLP_TYPE = "thumbnail";
	private static final String ZERO_PRICE = "0";
	private TypeService typeService;
	private VariantsService variantsService;
	@Resource(name = "sessionService")
	SessionService sessionService;
	private StoreFinderServiceInterface storeFinderService;

	@Override
	public void populate(final SearchResultValueData source, final ProductData target) throws ConversionException
	{
		super.populate(source, target);
		populateVariants(source, target);
		populateUnitMP(source, target);
		populateIsAvailableForFoodCoupon(source, target);
	}

	/**
	 * @param source
	 * @param target
	 */
	private void populateIsAvailableForFoodCoupon(final SearchResultValueData source, final ProductData target)
	{
		target.setIsAvailableForFoodCoupon((Boolean) getValue(source, "isAvailableForFoodCoupon"));
	}	
	
	/**
	 * @param source
	 * @param target
	 */
	private void populateUnitMP(final SearchResultValueData source, final ProductData target)
	{
		target.setIsProductShownMP((Boolean) getValue(source, "isProductShownMP"));
	}

	/**
	 * This method populates Variant data and associates it to the Base product
	 *
	 * @param source
	 * @param target
	 */
	protected void populateVariants(final SearchResultValueData source, final ProductData target)
	{
		final String productMediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(
				VARIANT_IMAGE_PLP_TYPE);
		final String thumbnailMediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(
				VARIANT_IMAGE_THUMBNAIL_PLP_TYPE);

		final List unitConditionVariantsList = getValue(source, "unitConditionVariants");
		final List descriptionVariantsList = getValue(source, "descriptionVariants");
		final List codeVariantsList = getValue(source, "codeVariants");
		final List nameVariantsList = getValue(source, "nameVariants");
		final List brandVariantsList = getValue(source, "brandVariants");
		final List stockVariantsList = getValue(source, "stockVariants");
		final List stockStatusVariantsList = getValue(source, "stockStatusVariants");
		final List priceVariantsList = getValue(source, "priceVariants");
		final List mrpVariantsList = getValue(source, "mrpVariants");
		final List imageVariantsList = getValue(source, "variantsImg-" + productMediaFormatQualifier);
		final List imageVariantsThumbnailList = getValue(source, "variantsImg-" + thumbnailMediaFormatQualifier);
		final List variantsUrlList = getValue(source, "variantsUrl");
		final List variantsOfferTypeList = getValue(source, "offerTypeVariants");
		final List variantsPromoMessageList = getValue(source, "promoMessageVariants");
		final List childSKUsVariantsList = getValue(source, "childSKUsVariants");
		final List variantsPromotionDiscountList = getValue(source, "promotionDiscountVariants");
		final List variantsProductOnBogoList = getValue(source, "productOnBogoVariants");
		
		// Populate the list of available child variant options
		if (codeVariantsList != null && CollectionUtils.isNotEmpty(codeVariantsList))
		{
			final List<VariantOptionData> variantOptions = new ArrayList<VariantOptionData>();
			int position = 0;
			for (final Object code : codeVariantsList)
			{
				final VariantOptionData variantData = new VariantOptionData();
				setVariantCode(code, position, variantData);
				setVariantName(target, nameVariantsList, position, variantData);
				setVariantDescription(target, position, descriptionVariantsList, variantData);
				setVariantBrand(target, brandVariantsList, position, variantData);
				setVariantPriceData(target, priceVariantsList, position, variantData);
				setVariantMRPData(target, mrpVariantsList, position, variantData);
				setVariantStockStatus(target, stockStatusVariantsList, position, variantData);
				setVariantStock(target, stockVariantsList, position, variantData);
				setVariantCondition(unitConditionVariantsList, position, variantData);
				setVariantSavings(target, position, variantData);
				setVariantImage(target, imageVariantsList, imageVariantsThumbnailList, position, variantData);
				setVariantURL(target, variantsUrlList, position, variantData);
				setVariantOfferTypeData(target, variantsOfferTypeList, position, variantData);
				setVariantChildSKUsListData(target, childSKUsVariantsList, position, variantData);
				setVariantPromoMessageData(target, variantsPromoMessageList, position, variantData);
				setVariantPromotionDiscountData(target, variantsPromotionDiscountList, position, variantData);
				setVariantProductOnBogoData(target, variantsProductOnBogoList, position, variantData);
				variantOptions.add(variantData);
				position++;
			}
			target.setVariantOptions(variantOptions);
		}
	}

	/**
	 * This method is used to set the details into VariantOptionData w.r.t name/description/code and brand. Also it sets
	 * the details to the base product for first variant.
	 *
	 * @param target
	 * @param position
	 * @param descriptionVariantsList
	 * @param variantData
	 */
	private void setVariantDescription(final ProductData target, final int position, final List descriptionVariantsList,
			final VariantOptionData variantData)
	{
		if (null != descriptionVariantsList && position < descriptionVariantsList.size())
		{
			final String desc = (String) descriptionVariantsList.get(position);
			variantData.setDescription(desc);

			// setting values of first variant in base product.
			if (0 == position)
			{
				target.setDescription(desc);
			}
		}
	}

	/**
	 * This method is used to set the details into VariantOptionData w.r.t name/description/code and brand.
	 *
	 * @param code
	 * @param position
	 * @param variantData
	 */
	private void setVariantCode(final Object code, final int position, final VariantOptionData variantData)
	{
		final String articleNumber = (String) code;
		variantData.setCode(articleNumber);
	}

	/**
	 * This method is used to set the details into VariantOptionData w.r.t name/description/code and brand. Also it sets
	 * the details to the base product for first variant.
	 *
	 * @param target
	 * @param nameVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantName(final ProductData target, final List nameVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != nameVariantsList && position < nameVariantsList.size())
		{
			final String name = (String) nameVariantsList.get(position);
			variantData.setName(name);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setName(name);
			}

		}
	}

	/**
	 * This method is used to set the details into VariantOptionData w.r.t name/description/code and brand. Also it sets
	 * the details to the base product for first variant.
	 *
	 * @param target
	 * @param brandVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantBrand(final ProductData target, final List brandVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != brandVariantsList && position < brandVariantsList.size())
		{
			final String brand = (String) brandVariantsList.get(position);
			variantData.setBrand(brand);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setBrand(brand);
			}

		}
	}

	/**
	 * This method sets the URL for the variant products
	 *
	 * @param target
	 * @param variantsUrlList
	 * @param position
	 * @param variantData
	 */
	private void setVariantURL(final ProductData target, final List variantsUrlList, final int position,
			final VariantOptionData variantData)
	{
		if (null != variantsUrlList && position < variantsUrlList.size())
		{
			final String url = (String) variantsUrlList.get(position);
			variantData.setUrl(url);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setUrl(url);
			}
		}
	}

	/**
	 * This method sets the product/thumbnail image for the Variant product.
	 *
	 * @param target
	 * @param imageVariantsList
	 * @param thumbnailVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantImage(final ProductData target, final List imageVariantsList, final List thumbnailVariantsList,
			final int position, final VariantOptionData variantData)
	{
		final List<ImageData> imageDataList = new ArrayList<ImageData>();

		if (null != thumbnailVariantsList && position < thumbnailVariantsList.size())
		{
			final String thumbnailImageUrl = (String) thumbnailVariantsList.get(position);
			getVariantImageData(thumbnailImageUrl, VARIANT_IMAGE_THUMBNAIL_PLP_TYPE, variantData.getName(), imageDataList);
		}

		if (null != imageVariantsList && position < imageVariantsList.size())
		{
			final String primaryImageUrl = (String) imageVariantsList.get(position);
			getVariantImageData(primaryImageUrl, VARIANT_IMAGE_PLP_TYPE, variantData.getName(), imageDataList);
		}

		variantData.setImages(imageDataList);
		// setting values of first variant in base product
		if (0 == position && CollectionUtils.isNotEmpty(imageDataList))
		{
			target.setImages(imageDataList);
		}
	}

	/**
	 * This method sets the price (csp) for variants. Also it sets the values of first variant to the base product
	 *
	 * @param target
	 * @param priceVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantPriceData(final ProductData target, final List priceVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != priceVariantsList && position < priceVariantsList.size())
		{
			final PriceData price = getVariantPriceData(priceVariantsList, position, variantData.getCode());
			variantData.setPriceData(price);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setPrice(price);
			}
		}
	}

	private void setVariantPromotionDiscountData(final ProductData target, final List variantsPromotionDiscountList,
			final int position, final VariantOptionData variantData)
	{
		if (null != variantsPromotionDiscountList && position < variantsPromotionDiscountList.size())
		{
			
			final String promotionDiscount = getVariantPromotionDiscountData(variantsPromotionDiscountList, position, variantData.getCode());
			variantData.setPromotionDiscount(promotionDiscount);

			// setting values of first variant in base product
			if (0 == position)
			{
				if("null".equals(promotionDiscount))
				{
					target.setPromotionDiscount(null);
				}
				else
				{
					target.setPromotionDiscount(Double.valueOf(promotionDiscount));
				}
			}
		}
	}
	
	protected String getVariantPromotionDiscountData(final List variantsPromotionDiscountList, final int position, final String productCode)
	{
		final String warehouse = getCurrentWarehouse();
		final String promotionDiscount = getPromotionDiscountforSelectedStore((String) variantsPromotionDiscountList.get(position), warehouse, productCode);
		return promotionDiscount;
	}
	
	protected String getPromotionDiscountforSelectedStore(final String variantsPromotionDiscountList, final String warehouseCode,
			final String productCode)
	{
		try
		{
			final String[] storePromotionDiscounts = variantsPromotionDiscountList
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storePromotionDiscount : storePromotionDiscounts)
			{
				final String[] splitString = storePromotionDiscount.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				final String warehouseFromPromotionDiscount = splitString[0];
				final String promotionDiscount = splitString[1];
				if (null != warehouseCode && warehouseCode.equals(warehouseFromPromotionDiscount))
				{
					return promotionDiscount;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get the store specific PromotionDiscount for product :" + productCode + " because of the following exception"
					+ e.getMessage());
			return null;
		}
		LOG.debug("Could not get the store specific PromotionDiscount. PriceRows not defined for product : " + productCode);
		return null;
	}

	private void setVariantProductOnBogoData(final ProductData target, final List variantsProductOnBogoList, final int position,
			final VariantOptionData variantData)
	{
		Boolean productOnBogoBoolean = null;
		if (null != variantsProductOnBogoList && position < variantsProductOnBogoList.size())
		{
			final String productOnBogo = getVariantProductOnBogoData(variantsProductOnBogoList, position, variantData.getCode());
			if (null != productOnBogo)
			{
				productOnBogoBoolean = Boolean.valueOf(productOnBogo);
			}
			variantData.setProductOnBogo(productOnBogoBoolean);
			// setting values of first variant in base product
			if (0 == position)
			{
				target.setProductOnBogo(productOnBogoBoolean);
			}
		}
	}

	protected String getVariantProductOnBogoData(final List variantsProductOnBogoList, final int position, final String productCode)
	{
		final String warehouse = getCurrentWarehouse();
		final String productOnBogo = getProductOnBogoforSelectedStore((String) variantsProductOnBogoList.get(position), warehouse,
				productCode);
		return productOnBogo;
	}

	protected String getProductOnBogoforSelectedStore(final String productOnBogoForWarehouseVariant, final String warehouseCode,
			final String productCode)
	{
		try
		{
			final String[] storeProductOnBogos = productOnBogoForWarehouseVariant
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storeProductOnBogo : storeProductOnBogos)
			{
				final String[] splitString = storeProductOnBogo
						.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				final String warehouseFromProductOnBogo = splitString[0];
				final String productOnBogo = splitString[1];
				
				  if (null != warehouseCode && warehouseCode.equals(warehouseFromProductOnBogo)) 
				  {
					  return productOnBogo; 
				}
			}
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get the store specific productOnBogo for product :" + productCode + " because of the following exception"
					+ e.getMessage());
			return null;
		}
		LOG.debug("Could not get the store specific productOnBogo. PriceRows not defined for product : " + productCode);
		return null;
	}
	
	
	/**
	 * This method sets the price (mrp) for variants. Also it sets the values of first variant to the base product
	 *
	 * @param target
	 * @param mrpPriceVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantMRPData(final ProductData target, final List mrpPriceVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != mrpPriceVariantsList && position < mrpPriceVariantsList.size())
		{
			final PriceData mrp = getVariantPriceData(mrpPriceVariantsList, position, variantData.getCode());
			variantData.setUnitMRP(mrp);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setUnitMRP(mrp);
			}
		}
	}

	/**
	 * This method sets the promo message for variants. Also it sets the values of first variant to the base product
	 *
	 * @param target
	 * @param variantsOfferTypeList
	 * @param position
	 * @param variantData
	 */
	private void setVariantPromoMessageData(final ProductData target, final List variantsOfferTypeList, final int position,
			final VariantOptionData variantData)
	{
		if (null != variantsOfferTypeList && position < variantsOfferTypeList.size())
		{
			final String promoMessage = getVariantDataFromPriceInfo(variantsOfferTypeList, position, variantData.getCode());
			variantData.setPromoMessage(promoMessage);
		}
	}

	/**
	 * This method sets the offer type for variants. Also it sets the values of first variant to the base product
	 *
	 * @param target
	 * @param variantsOfferTypeList
	 * @param position
	 * @param variantData
	 */
	private void setVariantOfferTypeData(final ProductData target, final List variantsOfferTypeList, final int position,
			final VariantOptionData variantData)
	{
		if (null != variantsOfferTypeList && position < variantsOfferTypeList.size())
		{
			final String offerType = getVariantDataFromPriceInfo(variantsOfferTypeList, position, variantData.getCode());
			variantData.setOfferType(offerType);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setOfferType(offerType);
			}
		}
	}

	/**
	 * @param target
	 * @param childSKUsVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantChildSKUsListData(final ProductData target, final List childSKUsVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != childSKUsVariantsList && position < childSKUsVariantsList.size())
		{
			final String childSKUs = (String) childSKUsVariantsList.get(position);
			//variantData.setChildSKUs(childSKUs);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setChildSKUs(childSKUs);
			}
		}
	}
	
	
	/**
	 * Sets the unitCOndition to Variant product
	 *
	 * @param unitConditionVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantCondition(final List unitConditionVariantsList, final int position, final VariantOptionData variantData)
	{
		String unit = "";
		if (null != unitConditionVariantsList && position < unitConditionVariantsList.size())
		{
			unit = (String) unitConditionVariantsList.get(position);
		}

		variantData.setUnitCondition(unit);
		variantData.setVariantOptionQualifiers(getVariantOptionQualifierData(unit, variantData.getName()));
	}

	/**
	 * This method sets the stockData to the variant. Also it sets the stockData of first variant to the base product.
	 *
	 * @param target
	 * @param stockVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantStock(final ProductData target, final List stockVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != stockVariantsList && position < stockVariantsList.size())
		{
			final StockData stock = getVariantStockData(stockVariantsList, position, variantData);
			variantData.setStock(stock);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setStock(stock);
			}
		}
	}

	/**
	 * This method sets the stockStatus to the variant. Also it sets the stockData of first variant to the base product.
	 *
	 * @param target
	 * @param stockStatusVariantsList
	 * @param position
	 * @param variantData
	 */
	private void setVariantStockStatus(final ProductData target, final List stockStatusVariantsList, final int position,
			final VariantOptionData variantData)
	{
		if (null != stockStatusVariantsList && position < stockStatusVariantsList.size())
		{
			final StockData stock = getVariantStockStatus(stockStatusVariantsList, position, variantData.getCode());
			variantData.setStock(stock);

			// setting values of first variant in base product
			if (0 == position)
			{
				target.setStock(stock);
			}
		}
	}

	/**
	 * Sets the saving in PriceData to the variant product. Also it sets the savings of first variant product in the base
	 * product.
	 *
	 * @param target
	 * @param position
	 * @param variantData
	 */
	private void setVariantSavings(final ProductData target, final int position, final VariantOptionData variantData)
	{
		final PriceData savings = getVariantSavings(variantData);
		variantData.setSavings(savings);

		// setting values of first variant in base product
		if (0 == position)
		{
			target.setSavings(savings);
		}
	}

	/**
	 * This method is used to compute savings using MRP and CSP
	 *
	 * @param variantData
	 * @return PriceData
	 */
	protected PriceData getVariantSavings(final VariantOptionData variantData)
	{
		PriceData savings = null;
		if (null != variantData.getUnitMRP() && null != variantData.getPriceData())
		{
			savings = getPriceDataFactory().create(PriceDataType.BUY,
					variantData.getUnitMRP().getValue().subtract(variantData.getPriceData().getValue()),
					getCommonI18NService().getCurrentCurrency());
		}
		return savings;
	}


	/**
	 * This method is used to create VariantOptionQualifier
	 *
	 * @param value
	 * @param description
	 * @return Collection<VariantOptionQualifierData>
	 */
	protected Collection<VariantOptionQualifierData> getVariantOptionQualifierData(final String value, final String description)
	{
		final List<VariantAttributeDescriptorModel> descriptorModels = getVariantsService().getVariantAttributesForVariantType(
				createVariantType(SparVariantProductModel._TYPECODE));

		final Collection<VariantOptionQualifierData> variantOptionQualifiers = new ArrayList<VariantOptionQualifierData>();
		for (final VariantAttributeDescriptorModel descriptorModel : descriptorModels)
		{
			// Create the variant qualifier
			final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
			final String qualifier = descriptorModel.getQualifier();
			variantOptionQualifier.setQualifier(qualifier);
			variantOptionQualifier.setName(description);
			variantOptionQualifier.setValue(value);
			// Add to list of variants
			variantOptionQualifiers.add(variantOptionQualifier);
		}
		return variantOptionQualifiers;

	}

	/**
	 * This method is used to get the StockData for Variants
	 *
	 * @param stockVariantsList
	 * @param position
	 * @param variantData
	 * @return StockData
	 */
	protected StockData getVariantStockData(final List stockVariantsList, final int position, final VariantOptionData variantData)
	{
		StockData stockData = variantData.getStock();
		final String warehouseCode = getCurrentWarehouse();
		final String stockLevelStatus = stockData.getStockLevelStatus().getCode();
		if (StringUtils.isNotEmpty(stockLevelStatus))
		{
			final StockLevelStatus stockLevelStatusEnum = StockLevelStatus.valueOf(stockLevelStatus);
			if (StockLevelStatus.LOWSTOCK.equals(stockLevelStatusEnum))
			{
				final StockData stockDataFromDB = getstockFromDB(variantData);
				stockData = stockDataFromDB == null ? getStockLevelStatusConverter().convert(StockLevelStatus.OUTOFSTOCK)
						: stockDataFromDB;
			}
			else
			{
				stockData.setStockLevel(Long.valueOf(getStockLevelforSelectedStore((String) stockVariantsList.get(position),
						warehouseCode, variantData.getCode())));
			}
		}
		return stockData;
	}

	/**
	 * This method is used to get the stores specific StockLevel from the solr index
	 *
	 * @param stockVariant
	 * @param warehouseCode
	 * @param productCode
	 * @return String
	 */
	protected String getStockLevelforSelectedStore(final String stockVariant, final String warehouseCode, final String productCode)
	{
		try
		{
			final String[] storeStockLevelArray = stockVariant
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storeStockLevel : storeStockLevelArray)
			{
				final String[] splitString = storeStockLevel
						.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				final String warehouse = splitString[0];
				final String stockLevel = splitString[1];
				if (null != warehouseCode && warehouseCode.equals(warehouse))
				{
					return stockLevel;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("Could not get the store specific StockLevel for product :" + productCode
					+ " because of the following exception" + e.getMessage());
			return "0";
		}
		LOG.debug("Could not get the store specific StockLevel for product: " + productCode);
		return "0";
	}


	/**
	 * In case of lowStock, real time DB check should be done so that PLP products Add to Bag behaviour is not
	 * contradicting with PDP.
	 *
	 * @param variantData
	 * @return StockData
	 */
	protected StockData getstockFromDB(final VariantOptionData variantData)
	{
		try
		{
			// In case of low stock then make a call to the stock service to determine if in or out of stock.
			// In this case (low stock) it is ok to load the product from the DB and do the real stock check
			final ProductModel productModel = getProductService().getProductForCode(variantData.getCode());
			if (productModel != null)
			{
				return getStockConverter().convert(productModel);
			}
		}
		catch (final UnknownIdentifierException ex)
		{
			// If the product is no longer visible to the customergroup then this exception can be thrown

			// We can't remove the product from the results, but we can mark it as out of stock
			return getStockLevelStatusConverter().convert(StockLevelStatus.OUTOFSTOCK);
		}

		return getStockLevelStatusConverter().convert(StockLevelStatus.OUTOFSTOCK);
	}


	/**
	 * This method is used to get the StockStatus for Variants
	 *
	 * @param stockVariantsList
	 * @param position
	 * @param productCode
	 * @return StockData
	 */
	protected StockData getVariantStockStatus(final List stockVariantsList, final int position, final String productCode)
	{
		final String warehouse = getCurrentWarehouse();
		final StockData stockData = new StockData();
		final String storeStockLevelStatus = (String) stockVariantsList.get(position);
		final String stockLevelStatus = getStockStatusforSelectedStore(storeStockLevelStatus, warehouse, productCode);
		if (StockLevelStatus.INSTOCK.getCode().equals(stockLevelStatus))
		{
			stockData.setStockLevelStatus(StockLevelStatus.INSTOCK);
		}
		else if (StockLevelStatus.LOWSTOCK.getCode().equals(stockLevelStatus))
		{
			stockData.setStockLevelStatus(StockLevelStatus.LOWSTOCK);
		}
		else
		{
			stockData.setStockLevelStatus(StockLevelStatus.OUTOFSTOCK);
		}

		return stockData;
	}

	/**
	 * This method is used to get the stores specific StockStatus from the solr index
	 *
	 * @param stockVariant
	 * @param warehouseCode
	 * @param productCode
	 * @return String
	 */
	protected String getStockStatusforSelectedStore(final String stockVariant, final String warehouseCode, final String productCode)
	{
		try
		{
			final String[] storeStockStatusArray = stockVariant
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storeStockStatus : storeStockStatusArray)
			{
				final String[] splitString = storeStockStatus
						.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				final String warehouse = splitString[0];
				final String stockStatus = splitString[1];
				if (null != warehouseCode && warehouseCode.equals(warehouse))
				{
					return stockStatus;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get the store specific stockStatus for product : " + productCode
					+ " because of the following exception" + e.getMessage());
			return StockLevelStatus.OUTOFSTOCK.toString();
		}
		LOG.debug("Could not get the store specific stockStatus  for product : " + productCode);
		return StockLevelStatus.OUTOFSTOCK.toString();
	}

	/**
	 * This method is used to get the PriceData for the Variant
	 *
	 * @param priceVariantsList
	 * @param position
	 * @param productCode
	 * @return PriceData
	 */
	protected PriceData getVariantPriceData(final List priceVariantsList, final int position, final String productCode)
	{
		final String warehouse = getCurrentWarehouse();
		final BigDecimal price = getPriceforSelectedStore((String) priceVariantsList.get(position), warehouse, productCode);
		if (null == price)
		{
			return null;
		}
		return getPriceDataFactory().create(PriceDataType.BUY, price, getCommonI18NService().getCurrentCurrency());
	}

	/**
	 * This method is used to get the Data from Price Info for the Variant
	 *
	 * @param list
	 * @param position
	 * @param productCode
	 * @return String
	 */
	protected String getVariantDataFromPriceInfo(final List list, final int position, final String productCode)
	{
		final String warehouse = getCurrentWarehouse();
		final String data = getDataFromPriceInfo((String) list.get(position), warehouse, productCode);
		if (null == data)
		{
			return null;
		}
		return data;
	}

	/**
	 * This method is used to get the variantImage data
	 *
	 * @param primaryImgValue
	 * @param imageFormat
	 * @param productName
	 * @param result
	 * @return List<ImageData>
	 */
	protected List<ImageData> getVariantImageData(final String primaryImgValue, final String imageFormat,
			final String productName, final List<ImageData> result)
	{
		return createVariantImageData(primaryImgValue, imageFormat, productName, result);
	}

	/**
	 * This method is used to create the variantImage
	 *
	 * @param primaryImgValue
	 * @param imageFormat
	 * @param productName
	 * @param result
	 * @return List<ImageData>
	 */
	protected List<ImageData> createVariantImageData(final String primaryImgValue, final String imageFormat,
			final String productName, final List<ImageData> result)
	{
		addVariantImageData(primaryImgValue, imageFormat, result, productName);

		return result;
	}

	/**
	 * This method is used to add the variantImage
	 *
	 * @param imgValue
	 * @param imageFormat
	 * @param images
	 * @param productName
	 */
	protected void addVariantImageData(final String imgValue, final String imageFormat, final List<ImageData> images,
			final String productName)
	{
		final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
		if (mediaFormatQualifier != null && !mediaFormatQualifier.isEmpty())
		{
			addVariantImageData(imgValue, imageFormat, ImageDataType.PRIMARY, images, productName);
		}
	}

	/**
	 * This method is used to add the variantImage
	 *
	 * @param imgValue
	 * @param imageFormat
	 * @param type
	 * @param images
	 * @param productName
	 */
	protected void addVariantImageData(final String imgValue, final String imageFormat, final ImageDataType type,
			final List<ImageData> images, final String productName)
	{
		if (imgValue != null && !imgValue.isEmpty())
		{
			final ImageData imageData = createImageData();
			imageData.setImageType(type);
			imageData.setFormat(imageFormat);
			imageData.setUrl(imgValue);
			imageData.setAltText(productName);

			images.add(imageData);
		}
	}

	/**
	 * This method is used to get the stores specific price from the solr index
	 *
	 * @param priceForWarehouseVariant
	 * @param warehouseCode
	 * @param productCode
	 * @return BigDecimal
	 */
	protected BigDecimal getPriceforSelectedStore(final String priceForWarehouseVariant, final String warehouseCode,
			final String productCode)
	{
		try
		{
			final String[] storePrices = priceForWarehouseVariant
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storePrice : storePrices)
			{
				final String[] splitString = storePrice.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				final String warehouseFromPrice = splitString[0];
				final String price = splitString[1];
				if (null != warehouseCode && warehouseCode.equals(warehouseFromPrice) && !ZERO_PRICE.equals(price))
				{
					return new BigDecimal(price);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get the store specific price for product :" + productCode + " because of the following exception"
					+ e.getMessage());
			return null;
		}
		LOG.debug("Could not get the store specific price. PriceRows not defined for product : " + productCode);
		return null;
	}

	/**
	 * This method is used to get the stores specific price from the solr index
	 *
	 * @param priceForWarehouseVariant
	 * @param warehouseCode
	 * @param productCode
	 * @return String
	 */
	protected String getDataFromPriceInfo(final String priceForWarehouseVariant, final String warehouseCode,
			final String productCode)
	{
		try
		{
			final String[] storePrices = priceForWarehouseVariant
					.split(SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
			for (final String storePrice : storePrices)
			{
				final String[] splitString = storePrice.split(SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER);
				String warehouseFromPrice = "";
				String promoMessage = "";
				try
				{
					if (null != splitString)
					{
						if (splitString.length > 1)
						{
							warehouseFromPrice = splitString[0];
							promoMessage = splitString[1];
						}
						else
						{
							warehouseFromPrice = splitString[0];
						}
					}
				}
				catch (final Exception msg)
				{
					LOG.debug("Could not get the store specific price info for product :" + productCode
							+ " because of the following exception" + msg.getMessage());
				}
				if (null != warehouseCode && warehouseCode.equals(warehouseFromPrice))
				{
					return promoMessage;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get the store specific price info for product :" + productCode
					+ " because of the following exception" + e.getMessage());
			return null;
		}
		LOG.debug("Could not get the store specific price info. PriceRows not defined for product : " + productCode);
		return null;
	}

	/**
	 * This method is used to get the warehouse from the current POS selected.
	 *
	 * @return WarehouseModel
	 */
	protected String getCurrentWarehouse()
	{
		String warehouseCode = null;
		if (null != sessionService)
		{
			warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		}
		return warehouseCode;
	}

	/**
	 * This method is overridden to populate warehouse specific price from warehouse specific index property like
	 * (pricevalue_20001)
	 *
	 * @see de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator#populatePrices(de.
	 *      hybris.platform.commerceservices.search.resultdata.SearchResultValueData,
	 *      de.hybris.platform.commercefacades.product.data.ProductData)
	 */
	@Override
	protected void populatePrices(final SearchResultValueData source, final ProductData target)
	{
		// Pull the volume prices flag
		final Boolean volumePrices = this.<Boolean> getValue(source, "volumePrices");
		target.setVolumePricesFlag(volumePrices == null ? Boolean.FALSE : volumePrices);

		// Pull the price value for the current currency
		final String warehouseCode = getCurrentWarehouse();
		if (null != warehouseCode)
		{
			final Double priceValue = this.<Double> getValue(source, "priceValue" + "_" + warehouseCode);
			if (priceValue != null)
			{
				final BigDecimal price = new BigDecimal(priceValue.doubleValue());
				final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, price,
						getCommonI18NService().getCurrentCurrency());
				target.setPrice(priceData);
			}
		}
	}


	/**
	 * This method is used to create a VariantTypeModel
	 *
	 * @param variantTypeCode
	 * @return VariantTypeModel
	 */
	public VariantTypeModel createVariantType(final String variantTypeCode)
	{
		return (VariantTypeModel) typeService.getComposedTypeForCode(variantTypeCode);
	}

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * @return the variantsService
	 */
	public VariantsService getVariantsService()
	{
		return variantsService;
	}

	/**
	 * @param variantsService
	 *           the variantsService to set
	 */
	public void setVariantsService(final VariantsService variantsService)
	{
		this.variantsService = variantsService;
	}

	/**
	 * Getter
	 *
	 * @return the storeFinderService
	 */
	public StoreFinderServiceInterface getStoreFinderService()
	{
		return storeFinderService;
	}

	/**
	 * Setter
	 *
	 * @param storeFinderService
	 *           the storeFinderService to set
	 */
	public void setStoreFinderService(final StoreFinderServiceInterface storeFinderService)
	{
		this.storeFinderService = storeFinderService;
	}

}
