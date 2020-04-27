package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ImageValueProvider;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class is used for indexing Image (product/thumbnail) of variants for the corresponding products.
 *
 * @author rohan_c
 *
 */
public class SparVariantImageValueProvider extends ImageValueProvider
{
	private static final Logger LOG = Logger.getLogger(SparVariantImageValueProvider.class);

	/**
	 * This method is used to get the Field value for primary image of Variant and push it into Solr
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			final MediaFormatModel mediaFormatModel = getMediaService().getFormat(getMediaFormat());
			if (mediaFormatModel != null)
			{
				try
				{
					return processVariantImage(((ProductModel) model).getVariants(), mediaFormatModel, indexedProperty);
				}
				finally
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("No [" + mediaFormatModel.getQualifier() + "] image found for variant product ["
								+ ((ProductModel) model).getCode() + "]");
					}
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * This method gets the Image of variant specified and adds in FieldValue
	 *
	 * @param variants
	 * @param mediaFormat
	 * @param indexedProperty
	 * @return Collection<FieldValue>
	 */
	protected Collection<FieldValue> processVariantImage(final Collection<VariantProductModel> variants,
			final MediaFormatModel mediaFormat, final IndexedProperty indexedProperty)
	{
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
		for (final VariantProductModel product : variants)
		{
			MediaModel media = null;
			if (product != null && mediaFormat != null)
			{
				final List<MediaContainerModel> galleryImages = product.getGalleryImages();
				if (galleryImages != null && !galleryImages.isEmpty())
				{
					// Search each media container in the gallery for an image of the right format
					for (final MediaContainerModel container : galleryImages)
					{
						try
						{
							final MediaModel mediaModel = getMediaContainerService().getMediaForFormat(container, mediaFormat);
							if (mediaModel != null)
							{
								final String mediaName = (mediaModel.getCode().substring(mediaModel.getCode().lastIndexOf("/")+1,
										mediaModel.getCode().indexOf(".")));
								if (mediaName.equalsIgnoreCase(product.getCode()))
								{
									media = mediaModel;
								}
							}
						}
						catch (final ModelNotFoundException ignore)
						{
							// ignore
						}
					}
				}
			}
			// Failed to find media in product
			// Look in the base product
			if (null == media)
			{
				media = findMedia(product.getBaseProduct(), mediaFormat);
			}

			if (media != null)
			{
				fieldValues.addAll(createFieldValues(indexedProperty, media));
			}
		}

		return fieldValues;
	}

}
