/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.CategorySource;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.util.Config; 
import de.hybris.platform.variants.model.VariantProductModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import com.spar.hcl.core.service.cart.SparCartService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
/**
 * @author ravindra.kr
 *
 */
public class SparIsAvailableForFoodCouponValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private SparCartService sparCartService;
	private CategorySource categorySource;
	private FieldNameProvider fieldNameProvider;
	
	private static final Logger LOG = Logger.getLogger(SparIsAvailableForFoodCouponValueProvider.class);

	protected CategorySource getCategorySource()
	{
		return categorySource;
	}

	@Required
	public void setCategorySource(final CategorySource categorySource)
	{
		this.categorySource = categorySource;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			ProductModel product = (ProductModel) model;
			final Collection<VariantProductModel> variants = product.getVariants();
			if (!CollectionUtils.isEmpty(variants))
			{
				product = variants.iterator().next();
			}
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			final Collection<CategoryModel> categories = getCategorySource().getCategoriesForConfigAndProperty(indexConfig,
					indexedProperty, model);
			if (null != categories && categories.size() > 0)
			{
				LOG.debug("Supercategories list for " + product.getCode() + ", :::::  Supercategories :" + categories);
				//final CategoryModel firstLevelCategory = getSparCartService().getFirstLevelCategory(categories);
				final CategoryModel firstLevelCategory = getFirstLevelCategory(categories);
				if(null != firstLevelCategory) {
					LOG.debug("First level category for " + product.getCode() + ", :::::  firstLevelCategory :" + firstLevelCategory.getCode());
					LOG.debug("Adding First level category for Indexing " + firstLevelCategory.getIsAvailableForFoodCoupon().booleanValue() + ", :::::  firstLevelCategory :" + firstLevelCategory.getIsAvailableForFoodCoupon().getClass());
					fieldValues.addAll(createFieldValue(firstLevelCategory.getIsAvailableForFoodCoupon().booleanValue(), indexedProperty));
				}
			}

			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot get isAvailableForFoodCouponValue of non-product item");
		}
	}
	
	
	private CategoryModel getFirstLevelCategory(final Collection<CategoryModel> categories)
	{
		for (final CategoryModel categoryModel : categories)
		{
			String categoryKey = Config.getString("category."+categoryModel.getCode(), "category."+categoryModel.getCode());
			LOG.debug("categoryModel.getCode ::11:: " + categoryModel.getCode()+", :::::::::::::categoryKey = "+categoryKey);
		
			if (categoryKey.equalsIgnoreCase("BASE_CATEGORY"))
			{
				LOG.debug("Inside if :: categoryModel.getCode ::22:: " + categoryModel.getCode());
				return categoryModel;
			}
			else
			{
				LOG.debug("first level category not found. " + categoryModel.getCode());
			}
		}
		return null;
	}

	protected List<FieldValue> createFieldValue(final Boolean value, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		addFieldValues(fieldValues, indexedProperty, value);

		return fieldValues;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	/**
	 * @return the fieldNameProvider
	 */
	public FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * @return the sparCartService
	 */
	public SparCartService getSparCartService()
	{
		return sparCartService;
	}

	/**
	 * @param sparCartService
	 *           the sparCartService to set
	 */
	public void setSparCartService(final SparCartService sparCartService)
	{
		this.sparCartService = sparCartService;
	}

}
