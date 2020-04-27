/**
 *
 */
package com.spar.hcl.facades.product.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.stock.impl.SparDefaultCommerceStockService;
import com.spar.hcl.facades.product.SparProductFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;


/**
 * @author pavan.sr
 *
 */
public class SparDefaultProductFacade extends DefaultProductFacade implements SparProductFacade
{

	private CommercePriceService commercePriceService;
	private PriceDataFactory priceDataFactory;

	@Autowired
	private TypeService typeService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private StoreFinderFacadeInterface storeFinderFacade;

	@Autowired
	private VariantsService variantsService;

	@Autowired
	private PointOfServiceService pointOfServiceService;

	@Autowired
	private SparDefaultCommerceStockService sparCommerceStockService;

	@Resource(name = "productService")
	private ProductService productService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.spar.hcl.facades.product.SparProductFacade#getProductVariantOptionsData(de.hybris.platform.core.model.product
	 * .ProductModel, de.hybris.platform.commercefacades.product.data.ProductData)
	 */
	@Override
	public ProductData getProductVariantOptionsData(final List<ProductReferenceData> productReferences, final ProductData target)
	{
		final List<ProductReferenceData> newProductReferenceData = new ArrayList<ProductReferenceData>();
		for (final ProductReferenceData productReferenceData : productReferences)
		{
			final ProductModel productModel = productService.getProductForCode(productReferenceData.getTarget().getCode());
			final VariantProductModel variantProduct = (VariantProductModel) productModel;
			final ProductModel baseProductModel = variantProduct.getBaseProduct();

			if (null != baseProductModel.getVariants() /* && baseProductModel.getVariants().size() > 1 */)
			{
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

						optionDatas.add(optionData);
					}
				}
				productReferenceData.getTarget().setVariantOptions(optionDatas);
				newProductReferenceData.add(productReferenceData);
			}
		}
		target.setProductReferences(newProductReferenceData);
		return target;



		/*
		 * if (source instanceof VariantProductModel) { final VariantProductModel variantProduct = (VariantProductModel)
		 * source; final ProductModel baseProductModel = variantProduct.getBaseProduct();
		 *
		 * if (null != baseProductModel.getVariants() && baseProductModel.getVariants().size() > 1) {
		 *
		 * final List<VariantOptionData> optionDatas = new ArrayList<VariantOptionData>(); final
		 * Collection<VariantProductModel> baseProduct = baseProductModel.getVariants(); for (final VariantProductModel
		 * sparVariant : baseProduct) { final VariantOptionData optionData = new VariantOptionData(); if (sparVariant
		 * instanceof SparVariantProductModel) { final String funName = sparVariant.getFunctionalName() != null ?
		 * sparVariant.getFunctionalName() : ""; final String varName = sparVariant.getVariant() != null ?
		 * sparVariant.getVariant() : ""; optionData.setCode(sparVariant.getCode()); // 1 optionData.setName(funName + " "
		 * + varName); // 2 optionData.setDescription(sparVariant.getDescription()); //3
		 * optionData.setBrand(sparVariant.getBrand()); //4
		 *
		 * optionData.setPriceData(getVariantPriceData(sparVariant));
		 * optionData.setUnitMRP(getVariantUnitMRP(sparVariant)); optionData.setSavings(getVariantSaving(sparVariant));
		 * optionData.setVariantOptionQualifiers(getVariantOptionQualifierData( ((SparVariantProductModel)
		 * sparVariant).getUnitCondition(), sparVariant.getName())); optionData.setStock(getVariantStock(sparVariant));
		 * optionData.setUnitCondition(((SparVariantProductModel) sparVariant).getUnitCondition());
		 * optionData.setPromoMessage(getVariantPromoMessage(sparVariant));
		 * //optionData.set(getVariantPromoMessage(sparVariant));
		 *
		 * optionDatas.add(optionData); } }
		 *
		 * target.setVariantOptions(optionDatas); //} } return target;
		 */
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
	 * @return the commercePriceService
	 */
	public CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}


	/**
	 * @param commercePriceService
	 *           the commercePriceService to set
	 */
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}


	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}


	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
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
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}


	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	/**
	 * @return the storeFinderFacade
	 */
	public StoreFinderFacadeInterface getStoreFinderFacade()
	{
		return storeFinderFacade;
	}


	/**
	 * @param storeFinderFacade
	 *           the storeFinderFacade to set
	 */
	public void setStoreFinderFacade(final StoreFinderFacadeInterface storeFinderFacade)
	{
		this.storeFinderFacade = storeFinderFacade;
	}


	/**
	 * @return the pointOfServiceService
	 */
	public PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}


	/**
	 * @param pointOfServiceService
	 *           the pointOfServiceService to set
	 */
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}


	/**
	 * @return the sparCommerceStockService
	 */
	public SparDefaultCommerceStockService getSparCommerceStockService()
	{
		return sparCommerceStockService;
	}


	/**
	 * @param sparCommerceStockService
	 *           the sparCommerceStockService to set
	 */
	public void setSparCommerceStockService(final SparDefaultCommerceStockService sparCommerceStockService)
	{
		this.sparCommerceStockService = sparCommerceStockService;
	}


	/**
	 * @return the productService
	 */
	@Override
	public ProductService getProductService()
	{
		return productService;
	}


	/**
	 * @param productService
	 *           the productService to set
	 */
	@Override
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


}
