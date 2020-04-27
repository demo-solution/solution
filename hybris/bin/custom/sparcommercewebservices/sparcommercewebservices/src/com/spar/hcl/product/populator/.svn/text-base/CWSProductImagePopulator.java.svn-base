package com.spar.hcl.product.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;


public class CWSProductImagePopulator implements Populator<ProductModel, ProductData>
{
	private ImagePopulator ImagePopulator;

	@Override
	public void populate(final ProductModel source, final ProductData target)
	{
		target.setImages(new ArrayList<ImageData>());


		if (source.getThumbnail() != null)
		{
			final ImageData imageData = new ImageData();
			getImagePopulator().populate(source.getThumbnail(), imageData);
			target.getImages().add(imageData);
		}
		if (CollectionUtils.isNotEmpty(source.getOthers()))
		{
			for (final MediaModel media : source.getOthers())
			{
				if (media.getMediaFormat() != null && media.getMediaFormat().getQualifier().equals("1000Wx1000H"))
				{
					final ImageData defaultImage = new ImageData();
					getImagePopulator().populate(media, defaultImage);
					target.getImages().add(defaultImage);
				}
			}
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
	public void setImagePopulator(final ImagePopulator imagePopulator)
	{
		ImagePopulator = imagePopulator;
	}


}