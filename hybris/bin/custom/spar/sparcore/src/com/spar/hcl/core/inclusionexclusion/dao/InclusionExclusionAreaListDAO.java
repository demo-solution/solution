/**
 *
 */
package com.spar.hcl.core.inclusionexclusion.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.deliveryslot.model.InclusionExclusionEntryModel;


/**
 * @author nikhil-ku
 *
 */
public interface InclusionExclusionAreaListDAO extends Dao
{

	List<InclusionExclusionEntryModel> getAllListOnAreaType(InclusionExclusionTypeEnum areaType);



}
