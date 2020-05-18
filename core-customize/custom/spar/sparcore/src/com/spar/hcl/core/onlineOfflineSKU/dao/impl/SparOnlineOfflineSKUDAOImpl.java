/**
 *
 */
package com.spar.hcl.core.onlineOfflineSKU.dao.impl;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.spar.hcl.core.onlineOfflineSKU.dao.SparOnlineOfflineSKUDAO;
import com.spar.hcl.model.SparOnlineOfflineSKUModel;
/**
 * @author ravindra.kr
 *
 */
public class SparOnlineOfflineSKUDAOImpl implements SparOnlineOfflineSKUDAO
{
	private static final String ONLINE_SKU = "onlineSKU";
	private static final String GET_OFFLINE_SKU_QUERY = "select {off.pk} from {SparOnlineOfflineSKU as off} where {off.onlineSKU}=?onlineSKU";

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.onlineOfflineSKU.dao.SparOnlineOfflineSKUDAO#getOfflineSKU(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public List<SparOnlineOfflineSKUModel> getOfflineSKU(final Integer onlineSKU)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_OFFLINE_SKU_QUERY);
		final Map<String, Object> params = new HashMap<String, Object>();
		query.addQueryParameter(ONLINE_SKU, onlineSKU.intValue());
		query.addQueryParameters(params);
		final SearchResult searchResult = flexibleSearchService.search(query);
		return searchResult.getResult();
	}
}
