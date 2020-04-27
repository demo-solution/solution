/**
 *
 */
package com.spar.hcl.sparpricefactory.warehouse.dao.impl;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spar.hcl.sparpricefactory.warehouse.dao.SparRetrieveWarehouseDao;


/**
 * This class is used to get the warehouseModel using flexible search.
 * 
 * @author rohan_c
 *
 */
public class SparRetrieveWarehouseDaoImpl extends DefaultGenericDao<WarehouseModel> implements SparRetrieveWarehouseDao
{

	private static final Logger LOG = Logger.getLogger(SparRetrieveWarehouseDaoImpl.class.getName());
	// Query to retrieve warehouse from selected POS
	private static final String FIND_WAREHOUSE_BY_POS = "select {wh.PK} from {PointOfService as pos JOIN PoS2WarehouseRel as pwrel on {pos.PK}={pwrel.source}"
			+ " JOIN Warehouse as wh on {wh.PK}={pwrel.target}} where {pos.name}=?pos";
	private static final String FIND_WAREHOUSE_BY_CODE = "SELECT {" + WarehouseModel.PK + "} FROM {" + WarehouseModel._TYPECODE
			+ "} WHERE {" + WarehouseModel.CODE + "} = ?code";


	/**
	 * SparRetriveWarehouseDaoImpl is only usable when typecode is set.
	 */
	public SparRetrieveWarehouseDaoImpl()
	{
		super(WarehouseModel._TYPECODE);
	}


	/**
	 * This method is used to get the Warehouse from Point of Service
	 *
	 * @param pointOfService
	 * @return WarehouseModel
	 */
	@Override
	public WarehouseModel findWarehouseByPOS(final String pointOfService)
	{
		WarehouseModel warehouseModel = null;
		try
		{
			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_WAREHOUSE_BY_POS);
			flexibleSearchQuery.addQueryParameter("pos", pointOfService);
			final SearchResult<WarehouseModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
			warehouseModel = searchResult.getResult().get(0);
			LOG.debug(" Warehouse found : " + warehouseModel);
		}
		catch (final Exception e)
		{
			LOG.debug("No warehouse found for POS:  " + pointOfService);
			LOG.debug("Error while retrieving the warehouse " + e.getMessage());
		}
		return warehouseModel;
	}


	/**
	 * This method is used get the Warehouse from code
	 *
	 * @param warehouse
	 * @return WarehouseModel
	 */
	@Override
	public WarehouseModel findWarehouseByCode(final String warehouse)
	{
		WarehouseModel warehouseModel = null;
		try
		{
			final Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("code", warehouse);
			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_WAREHOUSE_BY_CODE, queryParams);
			warehouseModel = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
			LOG.debug(" Warehouse found : " + warehouseModel);
		}
		catch (final Exception e)
		{
			LOG.debug("No warehouse found. Error while retrieving the warehouse " + e.getMessage());
		}
		return warehouseModel;
	}
}
