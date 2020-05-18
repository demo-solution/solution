/**
 *
 */
package com.spar.hcl.core.inclusionexclusion.service;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExRequest;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;


/**
 * @author nikhil-ku This interface has method that decide whether the particular location comes under inclusion list or
 *         exclusion list
 *
 */
public interface InclusionExclusionAreaListService
{
	SparInExResponse isLocationInAreaListOnAreaType(SparInExRequest request, InclusionExclusionTypeEnum areaType);


}
