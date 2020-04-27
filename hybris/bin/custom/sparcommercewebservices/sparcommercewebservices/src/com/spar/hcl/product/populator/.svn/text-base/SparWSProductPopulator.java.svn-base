package com.spar.hcl.product.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.spar.hcl.stock.CommerceStockFacade;


public class SparWSProductPopulator implements Populator<ProductModel, ProductData>
{
	private ImagePopulator ImagePopulator;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "sessionService")
	private SessionService sessionService;


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
				else if (media.getMediaFormat() != null && media.getMediaFormat().getQualifier().equals("150Wx150H"))
				{
					final ImageData defaultImage = new ImageData();
					getImagePopulator().populate(media, defaultImage);
					target.getImages().add(defaultImage);
				}
			}
		}

		final String selectedStoreForPDPWS = (String) ((sessionService.getAttribute("selectedStoreForPDPWS") == null)
				? StringUtils.EMPTY : sessionService.getAttribute("selectedStoreForPDPWS"));
		StockData stock = new StockData();
		if (StringUtils.isNotEmpty(selectedStoreForPDPWS))
		{
			stock = commerceStockFacade.getStockDataForProductAndPointOfService(source.getCode(), selectedStoreForPDPWS);

		}
		target.setStock(stock);
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