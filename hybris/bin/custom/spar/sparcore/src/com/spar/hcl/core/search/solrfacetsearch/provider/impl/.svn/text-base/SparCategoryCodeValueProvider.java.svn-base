/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.CategoryCodeValueProvider;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
/**
 * This class is used to create facet values for New Item Arrival & Product on offer
 *
 * @author ravindra.kr
 *
 */
public class SparCategoryCodeValueProvider extends CategoryCodeValueProvider
{

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection<CategoryModel> categories = getCategorySource().getCategoriesForConfigAndProperty(indexConfig,
				indexedProperty, model);
		if (categories != null && !categories.isEmpty())
		{
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (indexedProperty.isLocalized())
			{
				final Collection<LanguageModel> languages = indexConfig.getLanguages();
				for (final LanguageModel language : languages)
				{
					for (final CategoryModel category : categories)
					{
						if(!category.getCode().startsWith("9"))
						{
							fieldValues.addAll(createFieldValue(category, language, indexedProperty));
						}
					}
				}
			}
			else
			{
				for (final CategoryModel category : categories)
				{
					if(!category.getCode().startsWith("9"))
					{
						fieldValues.addAll(createFieldValue(category, null, indexedProperty));
					}
				}
			}
			return fieldValues;
		}
		else
		{
			return Collections.emptyList();
		}
	}
}
