/**
 *
 */
package com.spar.hcl.core.onlineOfflineSKU.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.spar.hcl.core.onlineOfflineSKU.dao.SparOnlineOfflineSKUDAO;
import com.spar.hcl.core.onlineOfflineSKU.service.SparOnlineOfflineSKUService;
import com.spar.hcl.model.SparOnlineOfflineSKUModel;
/**
 * @author ravindra.kr
 *
 */
public class SparOnlineOfflineSKUServiceImpl implements SparOnlineOfflineSKUService
{
	@Autowired
	private SparOnlineOfflineSKUDAO sparOnlineOfflineSKUDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.onlineOfflineSKU.service.SparOnlineOfflineSKUService#getOfflineSKU(int)
	 */
	@Override
	public SparOnlineOfflineSKUModel getOfflineSKU(final Integer onlineSKU)
	{
		final List<SparOnlineOfflineSKUModel> list = sparOnlineOfflineSKUDAO.getOfflineSKU(onlineSKU);
		if (list.size() > 0)
		{
			return list.get(0);
		}
		return null;
	}
}
