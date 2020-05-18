package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;


/**
 * This class is an extension to the OOTB ProductPopulator to populate disclaimer,pickingAndPacking, modelNumber and
 * ingredientDescription and storageType
 *
 * @author tanveers
 *
 */
public class SparPDPProductPopulator extends ProductPopulator
{
	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		super.populate(source, target);
		if (null != source.getDisclaimer() && StringUtils.isNotEmpty(source.getDisclaimer()))
		{
			target.setDisclaimer(source.getDisclaimer());
		}
		if (null != source.getPickingAndPacking() && StringUtils.isNotEmpty(source.getPickingAndPacking()))
		{
			target.setPickingAndPacking(source.getPickingAndPacking());
		}
		if (null != source.getModelNumber() && StringUtils.isNotEmpty(source.getModelNumber()))
		{
			target.setModelNumber(source.getModelNumber());
		}
		if (null != source.getIngredientDescription() && StringUtils.isNotEmpty(source.getIngredientDescription()))
		{
			target.setIngredientDescription(source.getIngredientDescription());
		}
		if (null != source.getStorageType() && StringUtils.isNotEmpty(source.getStorageType()))
		{
			target.setStorageType(source.getStorageType());
		}
	}


}