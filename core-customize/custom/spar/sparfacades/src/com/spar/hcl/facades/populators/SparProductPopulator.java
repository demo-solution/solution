/**
 *
 */
package com.spar.hcl.facades.populators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.core.stock.impl.SparDefaultCommerceStockService;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;


/**
 * This class is an extension to the OOTB ProductPopulator to handle UOM and Unit MRP
 *
 * @author rohan_c
 *
 */
public class SparProductPopulator extends ProductPopulator
{

	private CommonI18NService commonI18NService;
	private PriceDataFactory priceDataFactory;
	private CommercePriceService commercePriceService;
	private SparCartService sparCartService;

	@Autowired
	private TypeService typeService;

	@Autowired
	private SparSearchResultProductPopulator defaultSparSearchResultProductPopulator;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private StoreFinderFacadeInterface storeFinderFacade;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "productConverter")
	private Converter<ProductModel, ProductData> productConverter;

	@Autowired
	private VariantsService variantsService;

	@Autowired
	private PointOfServiceService pointOfServiceService;

	@Autowired
	private SparDefaultCommerceStockService sparCommerceStockService;


	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		super.populate(source, target);
		populateProductUOM(source, target);
		populateUnitMRP(source, target);
		populateProductSavings(source, target);
		populateProductDescription(source, target);
		populateProductVariantDescription(source, target);
		populateProductBrand(source, target);
		populateFeaturedProductGrammage(source, target);
		populateFeaturedProductDescription(source, target);
		//order history unapproved product check START
		if (ArticleApprovalStatus.APPROVED.equals(ProductModel.APPROVALSTATUS))

		{
			populateUnitMP(source, target);
		}
		//order history unapproved product check END
		populateOfferType(source, target);
		populatePromoMessage(source, target);
		populateProductMaxOrderQuantity(source, target);
		//PDP changes : Pavan
		/*
		 * final String selectedStoreName = sessionService.getAttribute("selectedStore"); if (null != selectedStoreName) {
		 * populateProductVariantOptions(source, target); }
		 */
		populateSparVariantProductChildSKUs(source, target);
		populatePromotionDiscount(source, target);
		populateproductOnBogo(source, target);
		populateproductLegalMetrologyAttributes(source, target);
	}



	private void populateProductVariantOptions(final ProductModel source, final ProductData target)
	{

		if (source instanceof VariantProductModel)
		{
			final VariantProductModel variantProduct = (VariantProductModel) source;
			final ProductModel baseProductModel = variantProduct.getBaseProduct();
			/*
			 * if (null != baseProductModel.getVariants() && baseProductModel.getVariants().size() > 1) {
			 */
			final List<VariantOptionData> optionDatas = new ArrayList<VariantOptionData>();
			final Collection<VariantProductModel> baseProduct = baseProductModel.getVariants();
			for (final VariantProductModel sparVariant : baseProduct)
			{
				final VariantOptionData optionData = new VariantOptionData();
				if (sparVariant instanceof SparVariantProductModel)
				{
					final String funName = sparVariant.getFunctionalName() != null ? sparVariant.getFunctionalName() : "";
					final String varName = sparVariant.getVariant() != null ? sparVariant.getVariant() : "";
					optionData.setCode(sparVariant.getCode()); // 1
					optionData.setName(funName + " " + varName); // 2
					optionData.setDescription(sparVariant.getDescription()); //3
					optionData.setBrand(sparVariant.getBrand()); //4

					optionData.setPriceData(getVariantPriceData(sparVariant));
					optionData.setUnitMRP(getVariantUnitMRP(sparVariant));
					optionData.setSavings(getVariantSaving(sparVariant));
					optionData.setVariantOptionQualifiers(getVariantOptionQualifierData(
							((SparVariantProductModel) sparVariant).getUnitCondition(), sparVariant.getName()));
					optionData.setStock(getVariantStock(sparVariant));
					optionData.setUnitCondition(((SparVariantProductModel) sparVariant).getUnitCondition());
					optionData.setPromoMessage(getVariantPromoMessage(sparVariant));
					//optionData.set(getVariantPromoMessage(sparVariant));

					optionDatas.add(optionData);
				}
			}

			target.setVariantOptions(optionDatas);
			//}
		}
	}

	public String getVariantFeaturedProductGrammage(final VariantProductModel source)
	{
		String grammage = ((SparVariantProductModel) source).getUnitCondition();
		grammage = grammage == null ? "" : grammage;
		return grammage;
	}

	private String getVariantPromoMessage(final VariantProductModel variantProductModel)
	{
		String promoMessage = null;
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(variantProductModel.getVariants()))
		{
			info = getCommercePriceService().getWebPriceForProduct(variantProductModel);
		}

		if (info != null)
		{
			promoMessage = ((String) info.getQualifiers().get("promoMesssage"));
		}
		return promoMessage;
	}

	private PriceData getVariantPriceData(final VariantProductModel variantProductModel)
	{
		PriceData priceData = null;

		final Collection<PriceRowModel> priceRowModels = variantProductModel.getEurope1Prices();
		for (final PriceRowModel priceRowModel : priceRowModels)
		{
			if (priceRowModel instanceof SparPriceRowModel)
			{
				final WarehouseModel warehouse = ((SparPriceRowModel) priceRowModel).getWarehouse();
				final String selectedStoreName = sessionService.getAttribute("selectedStore");
				if (StringUtils.isNotEmpty(selectedStoreName))
				{
					final WarehouseData warehouseData = storeFinderFacade.getWarehouse(selectedStoreName);
					if (StringUtils.equalsIgnoreCase(warehouse.getName(), warehouseData.getName()))
					{
						priceData = getPriceDataFactory().create(PriceDataType.BUY,
								BigDecimal.valueOf(priceRowModel.getPrice().doubleValue()), getCommonI18NService().getCurrentCurrency());
					}
				}
			}
		}
		return priceData;
	}

	private PriceData getVariantUnitMRP(final VariantProductModel variantProductModel)
	{
		PriceData priceData = null;

		final Collection<PriceRowModel> priceRowModels = variantProductModel.getEurope1Prices();
		for (final PriceRowModel priceRowModel : priceRowModels)
		{
			if (priceRowModel instanceof SparPriceRowModel)
			{
				final WarehouseModel warehouse = ((SparPriceRowModel) priceRowModel).getWarehouse();
				final String selectedStoreName = sessionService.getAttribute("selectedStore");
				if (StringUtils.isNotEmpty(selectedStoreName))
				{
					final WarehouseData warehouseData = storeFinderFacade.getWarehouse(selectedStoreName);
					if (StringUtils.equalsIgnoreCase(warehouse.getName(), warehouseData.getName()))
					{
						priceData = getPriceDataFactory().create(PriceDataType.BUY,
								BigDecimal.valueOf(((SparPriceRowModel) priceRowModel).getUnitMRP().doubleValue()),
								getCommonI18NService().getCurrentCurrency());
					}
				}
			}
		}
		return priceData;
	}

	private PriceData getVariantSaving(final VariantProductModel variantProductModel)
	{
		PriceData priceData = null;

		final Collection<PriceRowModel> priceRowModels = variantProductModel.getEurope1Prices();
		for (final PriceRowModel priceRowModel : priceRowModels)
		{
			if (priceRowModel instanceof SparPriceRowModel)
			{
				final WarehouseModel warehouse = ((SparPriceRowModel) priceRowModel).getWarehouse();
				final String selectedStoreName = sessionService.getAttribute("selectedStore");
				if (StringUtils.isNotEmpty(selectedStoreName))
				{
					final WarehouseData warehouseData = storeFinderFacade.getWarehouse(selectedStoreName);
					if (StringUtils.equalsIgnoreCase(warehouse.getName(), warehouseData.getName()))
					{
						priceData = getPriceDataFactory().create(
								PriceDataType.BUY,
								BigDecimal.valueOf(((SparPriceRowModel) priceRowModel).getUnitMRP().doubleValue()
										- ((SparPriceRowModel) priceRowModel).getPrice().doubleValue()),
								getCommonI18NService().getCurrentCurrency());
					}
				}
			}
		}
		return priceData;
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

	private StockData getVariantStock(final VariantProductModel variantProductModel)
	{
		final String selectedStoreName = sessionService.getAttribute("selectedStore");
		final ProductModel productModel = productService.getProductForCode(variantProductModel.getCode());
		final PointOfServiceModel pointOfServiceModel = pointOfServiceService.getPointOfServiceForName(selectedStoreName);

		return createStockData(
				sparCommerceStockService.getStockLevelStatusForProductAndPointOfService(productModel, pointOfServiceModel),
				sparCommerceStockService.getStockLevelForProductAndPointOfService(productModel, pointOfServiceModel));
	}

	protected StockData createStockData(final StockLevelStatus stockLevelStatus, final Long stockLevel)
	{
		final StockData stockData = new StockData();
		stockData.setStockLevelStatus(stockLevelStatus);
		stockData.setStockLevel(stockLevel);
		return stockData;
	}


	/*
	 * private StockData getVariantStock(final VariantProductModel variantProductModel) { StockData stockData = null;
	 * final Collection<StockLevelModel> stockLevelModels = variantProductModel.getStockLevels(); for (final
	 * StockLevelModel stockLevelModel : stockLevelModels) { final stockLevelModel.get final WarehouseModel warehouse =
	 * stockLevelModel.getWarehouse(); stockLevelModel.getProduct().gets final String selectedStoreName =
	 * sessionService.getAttribute("selectedStore"); if (StringUtils.isNotEmpty(selectedStoreName)) { final WarehouseData
	 * warehouseData = storeFinderFacade.getWarehouse(selectedStoreName); if
	 * (StringUtils.equalsIgnoreCase(warehouse.getName(), warehouseData.getName())) { final String stockLevelStatus =
	 * stockData.getStockLevelStatus().getCode(); stockData = stockLevelModel.get } } } return stockData;
	 * 
	 * 
	 * 
	 * 
	 * StockData stockData = variantData.getStock(); final String warehouseCode = getCurrentWarehouse(); final String
	 * stockLevelStatus = stockData.getStockLevelStatus().getCode(); if (StringUtils.isNotEmpty(stockLevelStatus)) {
	 * final StockLevelStatus stockLevelStatusEnum = StockLevelStatus.valueOf(stockLevelStatus); if
	 * (StockLevelStatus.LOWSTOCK.equals(stockLevelStatusEnum)) { final StockData stockDataFromDB =
	 * getstockFromDB(variantData); stockData = stockDataFromDB == null ?
	 * getStockLevelStatusConverter().convert(StockLevelStatus.OUTOFSTOCK) : stockDataFromDB; } else {
	 * stockData.setStockLevel(Long.valueOf(getStockLevelforSelectedStore((String) stockVariantsList.get(position),
	 * warehouseCode, variantData.getCode()))); } } return stockData; }
	 */

	/**
	 * This method is used to populate Unit MP.
	 *
	 * @param source
	 * @param target
	 */
	private void populateUnitMP(final ProductModel source, final ProductData target)
	{
		final CategoryModel firstLevelCategory = getSparCartService().getLastLevelCategoryOnProduct(source.getCode());

		target.setIsProductShownMP(firstLevelCategory.getIsProductShownMP());
	}

	/**
	 * This method populates the Savings (PriceData Type) in ProductData
	 *
	 * @param target
	 */
	private void populateProductSavings(final ProductModel source, final ProductData target)
	{
		if (null != target.getUnitMRP() && null != target.getPrice())
		{
			final String selectedStoreName = sessionService.getAttribute("selectedStore");
			if (StringUtils.isNotEmpty(selectedStoreName))
			{
				final WarehouseData warehouseData = storeFinderFacade.getWarehouse(selectedStoreName);
				final Collection<PriceRowModel> prices = source.getEurope1Prices();
				for (final PriceRowModel price : prices)
				{
					final SparPriceRowModel sparPrice = (SparPriceRowModel) price;
					if (StringUtils.equalsIgnoreCase(warehouseData.getName(), sparPrice.getWarehouse().getName()))
					{
						target.setSavings(getPriceDataFactory().create(PriceDataType.BUY,
								target.getUnitMRP().getValue().subtract(target.getPrice().getValue()),
								getCommonI18NService().getCurrentCurrency()));
					}
				}
			}
		}
	}


	/**
	 * This method is used to populate the unit MRP for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populateUnitMRP(final ProductModel source, final ProductData target)
	{
		PriceDataType priceType = null;
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			priceType = PriceDataType.BUY;
			info = getCommercePriceService().getWebPriceForProduct(source);
		}

		if (info != null)
		{
			final double unitMRP = ((Double) info.getQualifiers().get("unitMRP")).doubleValue();
			final PriceData priceData = getPriceDataFactory().create(priceType, BigDecimal.valueOf(unitMRP),
					info.getPriceValue().getCurrencyIso());
			target.setUnitMRP(priceData);
		}

	}

	/**
	 * This method is used to populate the Promo Message for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populatePromoMessage(final ProductModel source, final ProductData target)
	{
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			info = getCommercePriceService().getWebPriceForProduct(source);
		}

		if (info != null)
		{
			final String promoMessage = ((String) info.getQualifiers().get("promoMesssage"));
			target.setPromoMessage(promoMessage);
		}
	}

	/**
	 * This method is used to populate the Offer type for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populateOfferType(final ProductModel source, final ProductData target)
	{
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			info = getCommercePriceService().getWebPriceForProduct(source);
		}

		if (info != null)
		{
			final boolean isBestDeal = BooleanUtils.isTrue(((Boolean) info.getQualifiers().get("bestDeal")));
			final boolean isRegularOffer = BooleanUtils.isTrue(((Boolean) info.getQualifiers().get("regularOffer")));
			final boolean isCombiOffer = BooleanUtils.isTrue(((Boolean) info.getQualifiers().get("combiOffer")));
			final String offerType = isBestDeal ? "bestDeal" : (isCombiOffer ? "combiOffer" : (isRegularOffer ? "regularOffer"
					: null));
			target.setOfferType(offerType);
		}
	}

	/**
	 * This method is used to populate the UOM for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductUOM(final ProductModel source, final ProductData target)
	{
		final UnitModel unit = source.getUnit();
		if (null != unit)
		{
			target.setSalesUnit(unit.getName());
		}
	}

	/**
	 * This method is used to populate the description for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductDescription(final ProductModel source, final ProductData target)
	{
		final String description = source.getERPDescription(Locale.ENGLISH);
		target.setERPDescription(description);
	}

	/**
	 * This method is used to populate the ERPVariantDescription for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */
	private void populateProductVariantDescription(final ProductModel source, final ProductData target)
	{
		final String functionalname = source.getFunctionalName(Locale.ENGLISH) == null ? "" : source
				.getFunctionalName(Locale.ENGLISH);
		final String variant = source.getVariant(Locale.ENGLISH) == null ? "" : source.getVariant(Locale.ENGLISH);
		final String description = functionalname + " " + variant;
		target.setERPVariantDescription(description);
	}

	/**
	 * This method is used to populate Brand
	 *
	 * @param source
	 * @param target
	 */
	public void populateProductBrand(final ProductModel source, final ProductData target)
	{
		if (source.getBrand() != null)
		{
			target.setBrand(source.getBrand());
		}
	}

	/**
	 * This method is used to populate FeaturedProduct Grammage
	 *
	 * @param source
	 * @param target
	 */
	public void populateFeaturedProductGrammage(final ProductModel source, final ProductData target)
	{
		if (source instanceof SparVariantProductModel)
		{
			String grammage = ((SparVariantProductModel) source).getUnitCondition();
			grammage = grammage == null ? "" : grammage;
			target.setFeaturedProductGrammage(grammage);
		}
	}

	/**
	 * This method is used to populate FeaturedProduct Description
	 *
	 * @param source
	 * @param target
	 */
	public void populateFeaturedProductDescription(final ProductModel source, final ProductData target)
	{
		final String functionalname = source.getFunctionalName(Locale.ENGLISH) == null ? "" : source
				.getFunctionalName(Locale.ENGLISH);
		final String variant = source.getVariant(Locale.ENGLISH) == null ? "" : source.getVariant(Locale.ENGLISH);
		final String description = functionalname + " " + variant;
		target.setFeaturedProductDesc(description);
	}


	/**
	 * This method is used to populate the Promo Message for a product and set it in the ProductData
	 *
	 * @param source
	 * @param target
	 */



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
	 * Getter for PriceDataFactory
	 *
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Setter for PriceDataFactory
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Getter for commonI18NService
	 *
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Setter for commonI18NService
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Getter CommercePriceService
	 *
	 * @return CommercePriceService
	 */
	protected CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	/**
	 * Setter CommercePriceService
	 *
	 * @param commercePriceService
	 */
	@Required
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	/**
	 * Getter
	 *
	 * @return the sparCartService
	 */
	public SparCartService getSparCartService()
	{
		return sparCartService;
	}

	/**
	 * Setter
	 *
	 * @param sparCartService
	 *           the sparCartService to set
	 */
	public void setSparCartService(final SparCartService sparCartService)
	{
		this.sparCartService = sparCartService;
	}

	public void populateProductMaxOrderQuantity(final ProductModel source, final ProductData target)
	{
		if (source.getMaxOrderQuantity() != null)
		{
			target.setMaxOrderQuantity(source.getMaxOrderQuantity());
		}
	}
	
	/**
	 * @param source
	 * @param target
	 */
	public void populateSparVariantProductChildSKUs(final ProductModel source, final ProductData target)
	{
		if (source instanceof VariantProductModel)
		{
			final SparVariantProductModel sparVariantProductModel = (SparVariantProductModel) source;
			if(null != sparVariantProductModel.getChildSKUs())
			{
				target.setChildSKUs(sparVariantProductModel.getChildSKUs());
			}
		}
	}
	
	private void populatePromotionDiscount(final ProductModel source, final ProductData target)
	{
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			info = getCommercePriceService().getWebPriceForProduct(source);
		}

		if (info != null)
		{
			final Double promotionDiscount = ((Double) info.getQualifiers().get("promotionDiscount"));
			target.setPromotionDiscount(promotionDiscount);
		}
	}
	
	private void populateproductOnBogo(final ProductModel source, final ProductData target)
	{
		PriceInformation info = null;
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			info = getCommercePriceService().getWebPriceForProduct(source);
		}

		if (info != null)
		{
			final Boolean productOnBogo = ((Boolean) info.getQualifiers().get("productOnBogo"));
			target.setProductOnBogo(productOnBogo);
		}
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
	 * @return the productConverter
	 */
	public Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           the productConverter to set
	 */
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}
	
	
	public void populateproductLegalMetrologyAttributes(final ProductModel source, final ProductData target)
	{
		if (source.getProduceEAN() != null)
		{
			target.setProduceEAN(source.getProduceEAN());
		}

		if (source.getProductBrand() != null)
		{
			target.setProductBrand(source.getProductBrand());
		}

		if (source.getManufacture() != null)
		{
			target.setManufacture(source.getManufacture());
		}

		if (source.getImportedBy() != null)
		{
			target.setImportedBy(source.getImportedBy());
		}

		if (source.getNetQuantity() != null)
		{
			target.setNetQuantity(source.getNetQuantity());
		}
		if (source.getProductSize() != null)
		{
			target.setProductSize(source.getProductSize());
		}
		if (source.getBestBefore() != null)
		{
			target.setBestBefore(source.getBestBefore());
		}
		if (source.getUsedByDate() != null)
		{
			target.setUsedByDate(source.getUsedByDate());
		}
		if (source.getCustomercare() != null)
		{
			target.setCustomercare(source.getCustomercare());
		}
	}


}
