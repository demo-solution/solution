/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.spar.hcl.constants;

/**
 * Global class for all Ybackoffice constants. You can add global constants for your extension into this class.
 */
public final class SparbackofficeConstants extends GeneratedSparbackofficeConstants
{
	public static final String EXTENSIONNAME = "sparbackoffice";

	public SparbackofficeConstants()
	{
		//empty to avoid instantiating this constant class
	}

	//Pick list Configuration for CSV. These configurations are done in SparBackOfficeConfiguration.properties
	public static final String MODEL_PAGEABLE = "pageable";
	public static final String MODEL_COLUMNS_CONFIG = "columnConfig";
	public static final String CONFIRMATION_THRESHOLD = "confirmation.threshold";
	public static final String EXPORT_CSV_CONFIRMATION = "export.csv.confirmation";
	public static final String BEGIN_SPREADSHEET_FORMULA_CHARACTER = "=";
	public static final String PICKLIST_THRESHOLD = "sparbackoffice.picklist.threshold";
	public static final String PICK_LIST_FILE_NAME = "PickList.csv";
	public static final String FILE_NAME_CONFIG = "text/comma-separated-values;charset=UTF-8";
	public static final String PICK_LIST_HEADER = "sparbackoffice.picklist.header";
	public static final String PICK_LIST_QUALIFIER = "sparbackoffice.picklist.qualifier";
	public static final String PICK_LIST_HEADER_COUNT = "sparbackoffice.picklist.header.count";
	public static final String SUCCESS = "success";
	public static final String SCLASS_ACTIVE_MOLD_SELECTOR = "yw-coll-browser-mold-sel-btn-%s-active";
	public static final String SCLASS_INACTIVE_MOLD_SELECTOR = "yw-coll-browser-mold-sel-btn-%s-inactive";
	public static final String CONSIGNMENT_TYPE_CODE = "Consignment";
	public static final String POS_TYPE_CODE = "PointOfService";
	public static final String ACTION_SLOT_COMPONENET_ID = "actionSlotComponentId";
	public static final String SPAR_ACTION_SLOT_COMPONENET_ID = "sparActionSlotComponentId";
	public static final String ACTION_CONF = "component=%s,type=%s";
	public static final String PAGE_SIZE = "pageSize";
	public static final String PAGEABLE = "pageable";
	public static final String SELECTED_OBJECTS = "selectedObjects";
	public static final String GET_LIST_TITLE = "getListTitle()";
	public static final String GET_LIST_SUB_TITLE = "getListSubtitle()";
	public static final String DATA_TYPE_CODE = "dataTypeCode";
	public static final String DATA_TYPE = "dataType";
	public static final String ACTIVE_MODEL_NAME = "activeMoldName";
	public static final String ORDER_CODE = "order.code";

	public static final String PLANOGRAM_QUALIFIER = "warehousingbackoffice.consignment.planogram.";
	public static final String CONSIGNMENT_QUALIFIER = "warehousingbackoffice.consignment.orderEntry.";
	public static final String PAYMENT_QUALIFIER = "warehousingbackoffice.payment.";
	public static final String SNO_QUALIFIER = "warehousingbackoffice.consignment.pickList.item.sno";
	public static final String WAREHOUSE = "warehouse";
	public static final String WAREHOUSINGBACKOFFICE = "warehousingbackoffice";
	public static final String CATEGORY_QUALIFIER = "warehousingbackoffice.consignment.orderEntry.product.category";
	public static final String ORDER_DELIVERY_DATE_QUALIFIER = "slotDeliveryDate";
	public static final String ORDER_DELIVERY_SLOT_QUALIFIER = "orderDeliverySlot?.slotDescription";
	public static final String ORDER_POINT_OF_SERVICE = "orderPointOfService";
	public static final String CHECK_FOR_PROMO_QUALIFIER = "warehousingbackoffice.consignment.orderEntry.product.europe1Prices.promoCheck";
	public static final String UNIT_MRP_QUALIFIER = "warehousingbackoffice.consignment.orderEntry.product.europe1Prices.unitMRP";
	public static final String ORDER_NO_SHOW_RETRY_QUALIFIER = "oderRetryNoShow";
	//CSV Delimeter used for CSV export of PickList and AssetTracker
	public static final String CSV_DELIMETER = "sparbackoffice.csv.delimieter";
	public static final String ORDER_VOUCHERCODE = "voucherCode";
	public static final String ORDER_VOUCHERVALUE = "voucherValue";
	public static final String EMPLOYEE_CODE = "user.employeeCode";
	public static final String CUSTOMER_NAME = "user.displayName";
	public static final String PRODUCT_NAME = "warehousingbackoffice.consignment.orderEntry.product.name";
	public static final String CAPTURE_CHANNEL = "captureChannel";
	public static final String ORDER_DELIVERY_COST = "deliveryCost";
	public static final String PRODUCT_CODE = "warehousingbackoffice.consignment.orderEntry.product.code";
	public static final String QUANTITY = "warehousingbackoffice.consignment.orderEntry.quantity";
	public static final String PRODUCT_PROMOTION = "warehousingbackoffice.consignment.orderEntry.orderEntryPromotion";
	public static final String PRODUCT_TOTALPRICE = "warehousingbackoffice.consignment.orderEntry.totalPrice";
}
