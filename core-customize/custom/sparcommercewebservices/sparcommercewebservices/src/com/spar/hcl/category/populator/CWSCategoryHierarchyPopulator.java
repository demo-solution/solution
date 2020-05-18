package com.spar.hcl.category.populator;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CategoryHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.category.service.SparCategoryService;
import com.spar.hcl.stock.CommerceStockFacade;


public class CWSCategoryHierarchyPopulator extends CategoryHierarchyPopulator
{
	private ImagePopulator ImagePopulator;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Autowired
	private FlexibleSearchService flexibleSearchService;
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "sparCategoryService")
	private SparCategoryService sparCategoryService;
	private AbstractUrlResolver<CategoryModel> categoryUrlResolver;

	@Override
	public void populate(final CategoryModel source, final CategoryHierarchyData target,
			final Collection<? extends CatalogOption> options, final PageOption page) throws ConversionException
	{
		target.setId(source.getCode());
		target.setName(source.getName());
		target.setLastModified(source.getModifiedtime());
		target.setUrl(categoryUrlResolver.resolve(source));
		target.setProducts(new ArrayList<ProductData>());
		target.setSubcategories(new ArrayList<CategoryHierarchyData>());

		if (options.contains(CatalogOption.PRODUCTS))
		{
			final List<ProductModel> products = getProductService().getProductsForCategory(source, page.getPageStart(),
					page.getPageSize());
			for (final ProductModel product : products)
			{
				final ProductData productData = getProductConverter().convert(product);
				target.getProducts().add(productData);
			}
		}

		if (page.includeInformationAboutPages())
		{
			final Integer totalNumber = getProductService().getAllProductsCountForCategory(source);
			final Integer numberOfPages = Integer.valueOf((int) (Math.ceil(totalNumber.doubleValue() / page.getPageSize())));
			target.setTotalNumber(totalNumber);
			target.setCurrentPage(Integer.valueOf(page.getPageNumber()));
			target.setPageSize(Integer.valueOf(page.getPageSize()));
			target.setNumberOfPages(numberOfPages);
		}

		if (options.contains(CatalogOption.SUBCATEGORIES))
		{
			recursive(target, source, true, options);
		}
		if (source.getThumbnail() != null)
		{
			final ImageData imageData = new ImageData();
			getImagePopulator().populate(source.getThumbnail(), imageData);
			target.setCategoryImage(imageData);
		}

		if (source.getPicture() != null)
		{
			final ImageData defaultImage = new ImageData();
			getImagePopulator().populate(source.getPicture(), defaultImage);
			target.setDefaultImage(defaultImage);
		}
		final MediaModel fallBackImage = getFallBackImage();
		if (fallBackImage != null)
		{
			final ImageData fallBackImageData = new ImageData();
			getImagePopulator().populate(fallBackImage, fallBackImageData);
			target.setFallbackImage(fallBackImageData);
		}

		final Boolean sparProductListWS = (Boolean) ((sessionService.getAttribute("sparProductListWS") == null) ? Boolean.FALSE
				: sessionService.getAttribute("sparProductListWS"));
		final String selectedPickUpStore = (String) ((sessionService.getAttribute("selectedPickUpStore") == null) ? StringUtils.EMPTY
				: sessionService.getAttribute("selectedPickUpStore"));

		if (options.contains(CatalogOption.PRODUCTS) || sparProductListWS.booleanValue())
		{
			List<ProductData> productList = target.getProducts();
			productList = new ArrayList<ProductData>();
			target.setProducts(productList);

			final List<ProductModel> products = getProductService().getProductsForCategory(source, page.getPageStart(),
					page.getPageSize());
			//			final List<ProductModel> products = sparCategoryService.findProductsByCategory(source, page.getPageStart(),
			//					page.getPageSize());

			for (final ProductModel product : products)
			{
				final ProductData productData = getProductConverter().convert(product);
				StockData stock = new StockData();
				if (StringUtils.isNotEmpty(selectedPickUpStore))
				{
					stock = commerceStockFacade.getStockDataForProductAndPointOfService(product.getCode(), selectedPickUpStore);

				}
				productData.setStock(stock);
				if (CollectionUtils.isNotEmpty(product.getOthers()))
				{
					for (final MediaModel media : product.getOthers())
					{
						if (media.getMediaFormat() != null && media.getMediaFormat().getQualifier().equals("150Wx150H"))
						{
							final List<ImageData> images = new ArrayList<ImageData>();
							final ImageData defaultImage = new ImageData();
							getImagePopulator().populate(media, defaultImage);
							images.add(defaultImage);
							productData.setImages(images);
						}
					}
				}
				target.getProducts().add(productData);
			}
			sessionService.setAttribute("sparProductListWS", Boolean.FALSE);
		}
	}

	@Override
	protected void recursive(final CategoryHierarchyData categoryData2, final CategoryModel category, final boolean root,
			final Collection<? extends CatalogOption> options)
	{
		final String selectedPickUpStore = (String) ((sessionService.getAttribute("selectedPickUpStore") == null) ? StringUtils.EMPTY
				: sessionService.getAttribute("selectedPickUpStore"));

		if (root)
		{
			if (!category.getLinkComponents().isEmpty())
			{
				if (!category.getLinkComponents().get(0).getNavigationNodes().isEmpty())
				{
					for (final CategoryModel subc : category.getCategories())
					{
						recursive(categoryData2, subc, false, options);
					}
				}
			}
		}
		else
		{
			if (!category.getLinkComponents().isEmpty())
			{
				if (!category.getLinkComponents().get(0).getNavigationNodes().isEmpty())
				{
					final CategoryHierarchyData categoryData = new CategoryHierarchyData();
					categoryData.setId(category.getCode());
					categoryData.setName(category.getName());
					categoryData.setLastModified(category.getModifiedtime());
					categoryData.setUrl(categoryUrlResolver.resolve(category));
					categoryData.setProducts(new ArrayList<ProductData>());
					categoryData.setSubcategories(new ArrayList<CategoryHierarchyData>());

					if (category.getThumbnail() != null)
					{
						final ImageData imageData = new ImageData();
						getImagePopulator().populate(category.getThumbnail(), imageData);
						categoryData.setCategoryImage(imageData);
					}
					if (category.getPicture() != null)
					{
						final ImageData defaultImage = new ImageData();
						getImagePopulator().populate(category.getPicture(), defaultImage);
						categoryData.setDefaultImage(defaultImage);
					}
					final MediaModel fallBackImage = getFallBackImage();
					if (fallBackImage != null)
					{
						final ImageData fallBackImageData = new ImageData();
						getImagePopulator().populate(fallBackImage, fallBackImageData);
						categoryData.setFallbackImage(fallBackImageData);
					}

					if (options.contains(CatalogOption.PRODUCTS))
					{
						final List<ProductModel> products = category.getProducts();
						for (final ProductModel product : products)
						{
							final ProductData productData = getProductConverter().convert(product);
							StockData stock = new StockData();
							if (StringUtils.isNotEmpty(selectedPickUpStore))
							{
								stock = commerceStockFacade.getStockDataForProductAndPointOfService(product.getCode(),
										selectedPickUpStore);

							}
							productData.setStock(stock);
							categoryData.getProducts().add(productData);
						}
					}
					categoryData2.getSubcategories().add(categoryData);
					for (final CategoryModel subc : category.getCategories())
					{
						recursive(categoryData, subc, false, options);
					}
				}
			}
		}
	}

	protected MediaModel getFallBackImage()
	{
		MediaModel fallBackImage = null;

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("sparProductCatalog", "Online");
		final MediaModel media = new MediaModel();
		media.setCode("/img/missing_product_150x150.jpg");
		media.setCatalogVersion(catalogVersionModel);
		try
		{
			fallBackImage = flexibleSearchService.getModelByExample(media);
			return fallBackImage;
		}
		catch (final Exception e)
		{
			return fallBackImage;
		}
	}

	/**
	 * @return the imagePopulator
	 */
	public ImagePopulator getImagePopulator()
	{
		return ImagePopulator;
	}

	/**
	 * @param imagePopulator
	 *           the imagePopulator to set
	 */
	@Required
	public void setImagePopulator(final ImagePopulator imagePopulator)
	{
		ImagePopulator = imagePopulator;
	}

	@Override
	@Required
	public void setCategoryUrlResolver(final AbstractUrlResolver<CategoryModel> categoryUrlResolver)
	{
		this.categoryUrlResolver = categoryUrlResolver;
	}

	@Override
	public AbstractUrlResolver<CategoryModel> getCategoryUrlResolver()
	{
		return categoryUrlResolver;
	}
}