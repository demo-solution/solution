/**
 *
 */
package com.spar.hcl.widgets;


import de.hybris.platform.core.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.AbstractInitAdvancedSearchAdapter;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


/**
 * This class is used for back office asset tracker search for orders on some conditions
 *
 * @author rohan_c
 *
 */
public class MultiConditionSearchController extends AbstractInitAdvancedSearchAdapter
{
	private static final String STATUS = "status";
	private static final String DELIVERY_DATE = "deliveryDate";
	private static final String SLOT_DELIVERY_DATE = "slotDeliveryDate";
	private static final String ORDER = "Order";
	private static final String ASSET_TRACKER_NODE_ID = "ordermanagementbackoffice.typenode.order.open.assettracker";

	@Override
	public void addSearchDataConditions(final AdvancedSearchData searchData, Optional<NavigationNode> arg1)
	{
		final FieldType readyStatus = getFieldType(STATUS);
		final FieldType deliveryDateField = getFieldType(DELIVERY_DATE);
		final FieldType slotDeliveryDateField = getFieldType(SLOT_DELIVERY_DATE);

		final SearchConditionDataList innerConditionList = getInnerCondition(searchData);
		final SearchConditionData searchStatusCondition = new SearchConditionData(readyStatus, OrderStatus.READY,
				ValueComparisonOperator.EQUALS);
		final SearchConditionDataList innerDeliveryDateConditionList = getDeliverySlotConditionList(deliveryDateField,
				slotDeliveryDateField);

		searchData.addConditionList(ValueComparisonOperator.AND,
				Lists.newArrayList(innerDeliveryDateConditionList, innerConditionList, searchStatusCondition));

	}

	/**
	 * This method is used to delivery slot conditions for query
	 *
	 * @param deliveryDateField
	 * @param slotDeliveryDateField
	 * @return SearchConditionDataList
	 */
	private SearchConditionDataList getDeliverySlotConditionList(final FieldType deliveryDateField,
			final FieldType slotDeliveryDateField)
	{
		final SearchConditionData deliverySlotCondition = new SearchConditionData(deliveryDateField, "",
				ValueComparisonOperator.IS_NOT_EMPTY);
		final SearchConditionData orderDeliverySlotCondition = new SearchConditionData(slotDeliveryDateField, "",
				ValueComparisonOperator.IS_NOT_EMPTY);


		final List<SearchConditionData> deliveryDateSearchConditions = Lists.newArrayList();
		deliveryDateSearchConditions.add(orderDeliverySlotCondition);
		deliveryDateSearchConditions.add(deliverySlotCondition);
		final SearchConditionDataList innerDeliveryDateConditionList = SearchConditionDataList.or(deliveryDateSearchConditions);
		return innerDeliveryDateConditionList;
	}

	/**
	 * This method is used to get the inner conditions for query
	 *
	 * @param searchData
	 * @return SearchConditionDataList
	 */
	private SearchConditionDataList getInnerCondition(final AdvancedSearchData searchData)
	{
		final List<SearchConditionData> searchConditions = Lists.newArrayList();
		for (int i = 0; i < searchData.getSearchFields().size(); i++)
		{
			searchData.getCondition(i).getFieldType().setOperator(ValueComparisonOperator.IS_NOT_EMPTY.name());
			searchConditions.add(searchData.getCondition(i));
		}
		final SearchConditionDataList innerConditionList = SearchConditionDataList.or(searchConditions);
		return innerConditionList;
	}

	/**
	 * This method gets the FieldType
	 *
	 * @return FieldType
	 */
	private FieldType getFieldType(final String fieldName)
	{
		final FieldType readyStatus = new FieldType();
		readyStatus.setDisabled(Boolean.TRUE);
		readyStatus.setSelected(Boolean.TRUE);
		readyStatus.setName(fieldName);
		return readyStatus;
	}

	@Override
	public String getNavigationNodeId()
	{
		return ASSET_TRACKER_NODE_ID;
	}

	@Override
	public String getTypeCode()
	{
		return ORDER;
	}

	

	
}