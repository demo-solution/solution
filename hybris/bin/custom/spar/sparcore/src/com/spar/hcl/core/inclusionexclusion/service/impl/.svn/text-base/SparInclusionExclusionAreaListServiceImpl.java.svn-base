/**
 *
 */
package com.spar.hcl.core.inclusionexclusion.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.core.inclusionexclusion.dao.InclusionExclusionAreaListDAO;
import com.spar.hcl.core.inclusionexclusion.service.InclusionExclusionAreaListService;
import com.spar.hcl.deliveryslot.model.InclusionExclusionEntryModel;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExRequest;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;


/**
 * @author nikhil-ku
 *
 *         This interface has method implementation that decide whether the particular location come under inclusion
 *         list or exclusion list
 *
 */
public class SparInclusionExclusionAreaListServiceImpl implements InclusionExclusionAreaListService
{


	InclusionExclusionAreaListDAO inclusionExclusionAreaListDao;


	/**
	 * @return the inclusionExclusionAreaListDao
	 */
	public InclusionExclusionAreaListDAO getInclusionExclusionAreaListDao()
	{
		return inclusionExclusionAreaListDao;
	}

	/**
	 * @param inclusionExclusionAreaListDao
	 *           the inclusionExclusionAreaListDao to set
	 */
	public void setInclusionExclusionAreaListDao(final InclusionExclusionAreaListDAO inclusionExclusionAreaListDao)
	{
		this.inclusionExclusionAreaListDao = inclusionExclusionAreaListDao;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.spar.hcl.core.inclusionexclusion.service.InclusionExclusionAreaListService#isLocationInAreaListOnAreaType(
	 * com.spar.hcl.facades.inclusionexclusion.data.SparInExRequest, com.spar.hcl.core.enums.InclusionExclusionTypeEnum)
	 */
	@Override
	public SparInExResponse isLocationInAreaListOnAreaType(final SparInExRequest request, final InclusionExclusionTypeEnum areaType)
	{
		// first get only inclusive data list from sparInclusionExclusionAreaListDao
		final SparInExResponse response = new SparInExResponse();
		List<InclusionExclusionEntryModel> areaList = new ArrayList<InclusionExclusionEntryModel>();
		boolean isAreaNameMatched = false;
		boolean ispostalCodeMatched = false;
		boolean isInArealist = false;
		if (InclusionExclusionTypeEnum.EXL.equals(areaType))
		{
			areaList = getInclusionExclusionAreaListDao().getAllListOnAreaType(InclusionExclusionTypeEnum.EXL);
		}

		else
		{
			areaList = getInclusionExclusionAreaListDao().getAllListOnAreaType(InclusionExclusionTypeEnum.IN);

		}
		// iterate over the list and first match the ignore case of areaname if is found then go with pincode.
		if (CollectionUtils.isNotEmpty(areaList))
		{
			for (final InclusionExclusionEntryModel areaEntry : areaList)
			{ // 1 check for area name.
				isAreaNameMatched = false;
				ispostalCodeMatched = false;
				if (StringUtils.isNotEmpty(request.getLongfullAddress()) && StringUtils.isNotEmpty(areaEntry.getAreaName()))
				{
					// using utitity class divide the string into commas, and iterate to match the string...
					final String concatenated_fullAddress = StringUtils.deleteWhitespace(request.getLongfullAddress());

					final List<String> listAreas = Arrays.asList(concatenated_fullAddress.split(","));
					for (final String area : listAreas)
					{
						if (StringUtils.equalsIgnoreCase(area, StringUtils.deleteWhitespace(areaEntry.getAreaName())))
						{
							isAreaNameMatched = true;
							break;
						}


					}

				}

				// check for postal code..

				if (null != request.getPostalCode())
				{

					if (StringUtils.equals(request.getPostalCode(), areaEntry.getPostalCode()))
					{

						// postal code matched.
						ispostalCodeMatched = true;


					}

				}

				// using and operator to find it is in list or not.


				if (isAreaNameMatched && ispostalCodeMatched)
				{

					isInArealist = true;
					response.setIsInArealist(isInArealist);
					response.setAreaPOSlist(areaEntry.getStores());
					if (InclusionExclusionTypeEnum.EXL.equals(areaType))
					{

						response.setCode("FoundExlu.");


					}

					else if (InclusionExclusionTypeEnum.IN.equals(areaType))
					{

						response.setCode("FoundINC.");
					}

					break;


				}

				else
				{
					response.setIsInArealist(isInArealist);

					if (InclusionExclusionTypeEnum.EXL.equals(areaType))
					{
						response.setCode("NotFoundInExlu.");
					}

					else if (InclusionExclusionTypeEnum.IN.equals(areaType))
					{

						response.setCode("NotFoundInInc.");
					}

				}

			}
		}


		return response;
	}
}
