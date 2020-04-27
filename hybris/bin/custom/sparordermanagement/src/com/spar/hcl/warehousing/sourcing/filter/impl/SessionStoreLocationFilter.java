package com.spar.hcl.warehousing.sourcing.filter.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.warehousing.sourcing.filter.impl.AbstractBaseSourcingLocationFilter;

public class SessionStoreLocationFilter extends AbstractBaseSourcingLocationFilter {

	@Resource(name = "sessionService")
	SessionService sessionService;
	@Resource(name = "flexibleSearchService")
	FlexibleSearchService flexibleSearchService;

	private static final String GET_WAREHOUSE_FOR_STORE = "select {pos2war.target} from {PoS2WarehouseRel as pos2war join pointofservice as pos "
			+ "on {pos2war.source}={pos.pk}} where {pos.name}=?pos";
	private static Logger LOG = LoggerFactory.getLogger(SessionStoreLocationFilter.class);

	@Override
	public Collection<WarehouseModel> applyFilter(AbstractOrderModel order, Set locations) {
		try {
			if (null != order.getOrderPointOfService()) {
				LOG.info("The store for sourcing is found as : " + order.getOrderPointOfService().getName());
				return getWarehouseForSelectedStore(order.getOrderPointOfService().getName());
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Collection<WarehouseModel> getWarehouseForSelectedStore(String store) {
		FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_WAREHOUSE_FOR_STORE);
		flexibleSearchQuery.addQueryParameter("pos", store);
		SearchResult<WarehouseModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService() {
		return sessionService;
	}

	/**
	 * @param sessionService
	 *            the sessionService to set
	 */
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *            the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

}
