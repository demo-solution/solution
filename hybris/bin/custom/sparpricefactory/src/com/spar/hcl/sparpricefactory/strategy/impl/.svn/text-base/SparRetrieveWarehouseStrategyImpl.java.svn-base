/**
 *
 */
package com.spar.hcl.sparpricefactory.strategy.impl;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import org.apache.log4j.Logger;

import com.spar.hcl.sparpricefactory.strategy.SparRetrieveWarehouseStrategy;
import com.spar.hcl.sparpricefactory.warehouse.dao.SparRetrieveWarehouseDao;


/**
 * This class is used to get the warehouse details from the POS selected by the customer.
 *
 * @author rohan_c
 *
 */
public class SparRetrieveWarehouseStrategyImpl implements SparRetrieveWarehouseStrategy
{
	private static final Logger LOG = Logger.getLogger(SparRetrieveWarehouseStrategyImpl.class.getName());

	SparRetrieveWarehouseDao retrieveWarehouseDao;

	@Override
	public WarehouseModel getWarehouse(final SessionContext ctx)
	{
		WarehouseModel warehouse = null;
		return (null == (warehouse = getWarehouseFromStoredSessionWarehouse(ctx)) ? (null == (warehouse = getWarehouseFromSessionPOS(ctx)) ? null
				: warehouse)
				: warehouse);
	}

	/**
	 * This method is used to get the POS from session variable selectedStore
	 *
	 * @param ctx
	 * @return WarehouseModel
	 */
	private WarehouseModel getWarehouseFromSessionPOS(final SessionContext ctx)
	{
		WarehouseModel warehouseModel = null;
		String pointOfService = "";
		try
		{
			pointOfService = ctx.getAttribute("selectedStore");
			// no POS means that the flow is not of storefront, it could be through HMC,BO,Solr etc.
			if (null == pointOfService || "".equals(pointOfService))
			{
				LOG.debug("No POS was found in the session");
				return warehouseModel;
			}
			warehouseModel = getRetrieveWarehouseDao().findWarehouseByPOS(pointOfService);
		}
		catch (final Exception e)
		{
			LOG.debug("No warehouse found for POS:  " + pointOfService);
			LOG.debug("Error while retrieving the warehouse " + e.getMessage());
		}
		return warehouseModel;
	}


	/**
	 * This method is used get the Warehouse from selectedWarehouseCode in session
	 *
	 * @param ctx
	 * @return WarehouseModel
	 */
	private WarehouseModel getWarehouseFromStoredSessionWarehouse(final SessionContext ctx)
	{
		WarehouseModel warehouseModel = null;
		String warehouse = "";
		try
		{
			warehouse = ctx.getAttribute("selectedWarehouseCode");
			if (null == warehouse || "".equals(warehouse))
			{
				LOG.debug("No Warehouse was found in the session");
				return warehouseModel;
			}
			warehouseModel = getRetrieveWarehouseDao().findWarehouseByCode(warehouse);
		}
		catch (final Exception e)
		{
			LOG.debug("No warehouse found. Error while retrieving the warehouse " + e.getMessage());
		}
		return warehouseModel;
	}

	/**
	 * Getter
	 *
	 * @return the retrieveWarehouseDao
	 */
	public SparRetrieveWarehouseDao getRetrieveWarehouseDao()
	{
		return retrieveWarehouseDao;
	}

	/**
	 * Setter
	 *
	 * @param retrieveWarehouseDao
	 *           the retrieveWarehouseDao to set
	 */
	public void setRetrieveWarehouseDao(final SparRetrieveWarehouseDao retrieveWarehouseDao)
	{
		this.retrieveWarehouseDao = retrieveWarehouseDao;
	}


}
