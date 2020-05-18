/**
 *
 */
package com.spar.hcl.facades.storelocator.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import com.spar.hcl.facades.storelocator.data.WarehouseData;


/**
 * This method is used to get the Warehouse Data using warehouseModel.
 *
 * @author rohan_c
 *
 */
public class SparWarehousePopulator implements Populator<WarehouseModel, WarehouseData>
{

	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;

	/**
	 * This method is used to populate the warehouse data using warehousemodel
	 */
	@Override
	public void populate(final WarehouseModel source, final WarehouseData target) throws ConversionException
	{
		if (null != source)
		{
			target.setName(source.getName());
			target.setCode(source.getCode());
			target.setPointOfService(Converters.convertAll(source.getPointsOfService(), getPointOfServiceConverter()));
		}
	}

	/**
	 * Getter
	 *
	 * @return the pointOfServiceConverter
	 */
	public Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	/**
	 * Setter
	 *
	 * @param pointOfServiceConverter
	 *           the pointOfServiceConverter to set
	 */
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}


}
