package com.spar.hcl.mobileAppVersion.service.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.dto.SparMobileAppVersionWsDTO;
import com.spar.hcl.mobileAppVersion.service.SparMobileAppVersionService;
import com.spar.hcl.model.SparMobileAppVersionModel;


/**
 * @author ravindra.kr
 *
 */
public class SparMobileAppVersionServiceImpl implements SparMobileAppVersionService
{
	private static final Logger LOG = Logger.getLogger(SparMobileAppVersionServiceImpl.class);
	private static final String QUERY = "SELECT {m.PK} FROM {SparMobileAppVersion as m} where {m.creationtime} = ({{SELECT MAX({m1.creationtime}) FROM {SparMobileAppVersion as m1} }})";
	private static final String VERSION_NUMBER = "versionNumber";

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private ModelService modelService;

	/** The CatalogVersionService */
	@Autowired
	private CatalogVersionService catalogVersionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.mobileAppVersion.service.SparMobileAppVersionService#updateVersion(com.spar.hcl.dto.
	 * SparMobileAppVersionWsDTO)
	 */
	@Override
	public void updateVersion(final SparMobileAppVersionWsDTO versionWsDTO)
	{
		LOG.info("In updateVersion Method : " + versionWsDTO.getVersionNumber());
		final SparMobileAppVersionModel model = modelService.create(SparMobileAppVersionModel.class);
		model.setVersionNumber(versionWsDTO.getVersionNumber());
		model.setForceUpgrade(versionWsDTO.getForceUpgrade());
		model.setRecommendUpgrade(versionWsDTO.getRecommendUpgrade());
		modelService.save(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.mobileAppVersion.service.SparMobileAppVersionService#versionCheck(java.lang.String)
	 */
	@Override
	public SparMobileAppVersionWsDTO versionCheck(final String appVersion)
	{
		LOG.info("In versionCheck Method : " + appVersion);
		final SparMobileAppVersionWsDTO mobileAppVersionWsDTO = new SparMobileAppVersionWsDTO();
		//final Map<String, Object> params = new HashMap<String, Object>();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY);
		//params.put(VERSION_NUMBER, appVersion);
		//query.addQueryParameters(params);
		LOG.info("query :" + query);
		final SearchResult<SparMobileAppVersionModel> result = flexibleSearchService.search(query);
		if (null != result)
		{
			final SparMobileAppVersionModel versionModel = result.getResult().get(0);
			mobileAppVersionWsDTO.setForceUpgrade(versionModel.getForceUpgrade());
			mobileAppVersionWsDTO.setVersionNumber(versionModel.getVersionNumber());
			mobileAppVersionWsDTO.setRecommendUpgrade(versionModel.getRecommendUpgrade());
			return mobileAppVersionWsDTO;
		}

		return null;
	}

}
