/**
 *
 */
package com.spar.hcl.widgets.listview.actions.export.csv;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PayTMPaymentInfoModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.plano.jalo.PlanoGram;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.product.ProductService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Filedownload;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.search.data.pageable.Pageable;
//import com.hybris.cockpitng.widgets.listview.util.ListViewUtil;
import com.spar.hcl.constants.SparbackofficeConstants;
import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.services.DefaultSparPropertyValueService;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;
import org.springframework.beans.factory.annotation.Autowired;
import com.spar.hcl.core.onlineOfflineSKU.service.SparOnlineOfflineSKUService;
import com.spar.hcl.model.SparOnlineOfflineSKUModel;


/**
 * This class is used for exporting Pick List CSV.
 *
 * @author rohan_c
 *
 */
public class SparPickListAction implements CockpitAction<Map, Object>
{

	private static final String PROMO_CHECK = ".promoCheck";
	private static final String UNIT_MRP = ".unitMRP";
	public static final String OFFER_TOTAL = "offertotal";
	public static final String BASE_PRICE = "basePrice";
	private static final Logger LOG = LoggerFactory.getLogger(SparPickListAction.class);

	@Resource
	private CategoryService categoryService;
	@Resource
	private LabelService labelService;
	@Resource
	private PermissionFacade permissionFacade;
	@Resource
	private TypeFacade typeFacade;
	@Resource
	private PropertyValueService propertyValueService;
	@Resource
	private CockpitLocaleService cockpitLocaleService;
	@Resource
	private ModelService modelService;
	@Resource
	DefaultSparPropertyValueService defaultSparPropertyValueService;
	@Resource
	private SparOnlineOfflineSKUService sparOnlineOfflineSKUService;
	@Resource
	private ProductService productService;
	@Autowired
	private CatalogVersionService catalogVersionService;
	private String csvDelimiter = ",";
	private String voucher_value = " ";
	private String voucher_code = " ";

	/**
	 * This method performs the task of Pick List CSV generation.
	 */
	@Override
	public ActionResult<Object> perform(final ActionContext<Map> ctx)
	{
		csvDelimiter = Config.getString(SparbackofficeConstants.CSV_DELIMETER, csvDelimiter);
		final Pageable pageable = (Pageable) ctx.getData().get(SparbackofficeConstants.MODEL_PAGEABLE);
		final String csvContent = this.createCsv(pageable);
		this.writeBinaryResponse(csvContent);
		return new ActionResult(SparbackofficeConstants.SUCCESS);
	}

	/**
	 * This method writes he CSV and makes it available for download
	 *
	 * @param csvContent
	 */
	protected void writeBinaryResponse(final String csvContent)
	{
		Filedownload.save(csvContent, SparbackofficeConstants.FILE_NAME_CONFIG, SparbackofficeConstants.PICK_LIST_FILE_NAME);
	}

	/**
	 * This methods creates the CSV including header and contents.
	 *
	 * @param pageable
	 * @return String
	 */
	private String createCsv(final Pageable pageable)
	{
		final StringBuilder builder = new StringBuilder();
		final List<ListColumn> columnsToRender = findColumnsPrintableInCSV(getPickListColumns());
		this.createCsvHeader(builder, pageable, columnsToRender);
		this.createCsvContent(builder, pageable, columnsToRender);
		return builder.toString();
	}

	/**
	 * This method creates a CSV Header
	 *
	 * @param builder
	 * @param pageable
	 * @param columnList
	 */
	private void createCsvHeader(final StringBuilder builder, final Pageable pageable, final List<ListColumn> columnList)
	{
		for (final ListColumn listColumn : columnList)
		{
			//final String columnHeaderLabel = ListViewUtil
				//	.getColumnHeaderLabel(listColumn, pageable.getTypeCode(), this.labelService);
			//builder.append(this.wrapHeaderForCSV(this.escapeForCSV(this.securityEscape(columnHeaderLabel)))).append(csvDelimiter);
		}
		builder.append('\n');
	}

	/**
	 * This methods finds the value of the CSV headers
	 *
	 * @param columns
	 * @return List<ListColumn>
	 */
	private List<ListColumn> findColumnsPrintableInCSV(final List<ListColumn> columns)
	{
		return (columns.stream().filter(
				listColumn -> StringUtils.isBlank(listColumn.getSpringBean()) && StringUtils.isBlank(listColumn.getClazz()))
				.collect(Collectors.toList()));
	}

	/**
	 * This method is used to get the columns list (List<ListColumn>) of the Pick list CSV, containing Qualifier and
	 * Label
	 *
	 * @return List<ListColumn>
	 */
	private List<ListColumn> getPickListColumns()
	{
		final List<ListColumn> columnList = new ArrayList<ListColumn>();
		final long headerCount = Long.parseLong(Config.getString(SparbackofficeConstants.PICK_LIST_HEADER_COUNT, "39"));
		for (int i = 1; i <= headerCount; i++)
		{
			final ListColumn column = new ListColumn();
			final String columnHeaderLabel = Config.getString(SparbackofficeConstants.PICK_LIST_HEADER + i, "");
			final String columnQualifier = Config.getString(SparbackofficeConstants.PICK_LIST_QUALIFIER + i, "");
			column.setQualifier(columnQualifier);
			column.setLabel(columnHeaderLabel);
			columnList.add(column);
		}

		return columnList;
	}

	/**
	 * this method is used to create the content of the Pick List CSV.
	 *
	 * @param builder
	 * @param pageable
	 * @param columns
	 */
	private void createCsvContent(final StringBuilder builder, final Pageable pageable, final List<ListColumn> columns)
	{
		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("sparProductCatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		int sno = 1;

		for (final Object object : doSort(pageable.getAllResults()))
		{
			try
			{
				if (object instanceof ConsignmentModel)
				{
					final ConsignmentModel consginment = (ConsignmentModel) object;
					for (final ConsignmentEntryModel consignmentEntry : consginment.getConsignmentEntries())
					{
						String promoCheck = "";
						double grammageRatio = 0.0;
						String productName = null;
						String OfflineSKU = "";
						final String dataTypeCode = this.getTypeFacade().getType(object);
						final DataType dataType = StringUtils.isBlank(dataTypeCode) ? null : this.getTypeFacade().load(dataTypeCode);
						for (final ListColumn listColumn : columns)
						{
							String stringValue = "";
							final String qualifier = listColumn.getQualifier();
							if (dataType == null || this.canReadProperty(dataTypeCode, qualifier))
							{
								Object value = null;
								DataAttribute attribute = null;
								if (!qualifier.startsWith(SparbackofficeConstants.WAREHOUSINGBACKOFFICE))
								{
									value = getValueFromQualifier(object, qualifier);
									attribute = dataType != null ? dataType.getAttribute(qualifier) : null;
								}
								if (attribute == null || attribute.getValueType() == null || !attribute.getValueType().isAtomic())
								{
									try
									{
										if (null == value && SparbackofficeConstants.SNO_QUALIFIER.endsWith(qualifier))
										{
											stringValue = sno + "";
										}
										else if (SparbackofficeConstants.CATEGORY_QUALIFIER.equals(qualifier))
										{
											value = getProductCategoryName(consignmentEntry);
										}
										else if (qualifier.contains(SparbackofficeConstants.PLANOGRAM_QUALIFIER))
										{
											value = processPlanoGram(object, consignmentEntry, qualifier);
										}
										else if (qualifier.startsWith(SparbackofficeConstants.CONSIGNMENT_QUALIFIER))
										{
											if(qualifier.equals(SparbackofficeConstants.PRODUCT_CODE))
											{
												value = processConsginmentEntry(consignmentEntry, qualifier);
												SparOnlineOfflineSKUModel onlineOfflineSKUModel = sparOnlineOfflineSKUService.getOfflineSKU(Integer.valueOf(value.toString()));
												
												if(null != onlineOfflineSKUModel && !onlineOfflineSKUModel.getOfflineSKU().equals(Integer.valueOf(value.toString())))
												{
													if(null != onlineOfflineSKUModel.getGrammageRatio())
													{
														grammageRatio = onlineOfflineSKUModel.getGrammageRatio().doubleValue();
														ProductModel productModel = productService.getProductForCode(version , onlineOfflineSKUModel.getOfflineSKU().toString());
														productName = productModel.getName(Locale.ENGLISH);
														OfflineSKU = onlineOfflineSKUModel.getOfflineSKU().toString();
														value = OfflineSKU;
													}
												}
											}
											else
											{
   											if (qualifier.contains(SparbackofficeConstants.PRODUCT_NAME))
   											{
   												if(null != productName)
   												{
   													stringValue = productName;
   												}
   												else
   												{
   													stringValue = getProductName(consignmentEntry);
   												}
   											}
   											else
   											{
   												value = processConsginmentEntry(consignmentEntry, qualifier);
   												if (qualifier.contains(SparbackofficeConstants.QUANTITY) && grammageRatio > 0.0)
      											{
   													double orderEntryQty = Double.valueOf(value.toString()).doubleValue();
   													double calculatedQty = orderEntryQty * grammageRatio;
      												value =  Double.valueOf(calculatedQty); 
      											}
   												/*if (qualifier.contains(SparbackofficeConstants.PRODUCT_PROMOTION))
   												{
   													value = "N";
   													if (null != consignmentEntry.getOrderEntry() && consignmentEntry.getOrderEntry().getSavings().doubleValue() > 0)
   														{
   															value = "Y";
   														}
   												}*/
												if (qualifier.contains(SparbackofficeConstants.PRODUCT_PROMOTION))
													{
														promoCheck = "N";
														value = "N";
														if (null != consignmentEntry.getOrderEntry())
														{
															final Set<PromotionResultModel> resultModels = consignmentEntry.getOrderEntry().getOrder().getAllPromotionResults();
															
															for (final PromotionResultModel resultModel : resultModels)
															{
																LOG.info("resultModel.getPromotion() instanceof ProductPromotionModel :::::::::::: "+(resultModel.getPromotion() instanceof ProductPromotionModel));
																
																//if(resultModel.getPromotion() instanceof ProductPromotionModel){
   																final Collection<PromotionOrderEntryConsumedModel> models = resultModel.getConsumedEntries();
   																for(PromotionOrderEntryConsumedModel consumedModel : models){
   																	if(consumedModel.getOrderEntry().getProduct().getCode().equalsIgnoreCase(consignmentEntry.getOrderEntry().getProduct().getCode())){
   																		
   																		promoCheck = "Y"; 
   																		value = promoCheck;
   																	}
   																}
															//	}
															}
														}
													}
   												if (qualifier.contains(SparbackofficeConstants.PRODUCT_TOTALPRICE))
   												{
   														if (null != consignmentEntry.getOrderEntry()
   																&& consignmentEntry.getOrderEntry().getTotalPrice().doubleValue() > 0)
   														{
   															value = consignmentEntry.getOrderEntry().getTotalPrice();
   														}
   												}
   											}
											
   											/*if (qualifier.contains(SparbackofficeConstants.PRODUCT_PROMOTION))
											{
												promoCheck = "N";
												if (null != consignmentEntry.getOrderEntry()
														&& null != consignmentEntry.getOrderEntry().getDiscountValues())
												{
													if (consignmentEntry.getOrderEntry().getDiscountValues().size() > 0
															|| consignmentEntry.getOrderEntry().getGiveAway().booleanValue())
													{
														LOG.info("consignmentEntry.getOrderEntry().getDiscountValues().size() :::"
																+ consignmentEntry.getOrderEntry().getDiscountValues().size());
														LOG.info("consignmentEntry.getOrderEntry() :::"
																+ consignmentEntry.getOrderEntry().getProduct().getCode());
														promoCheck = "Y";
														value = promoCheck;
													}
												}
											}*/
   											if ((qualifier.endsWith(BASE_PRICE)) && promoCheck.equals("Y"))
   											{
   												value = null;
   											}
   										}
										}
										else if (qualifier.equals("order." + SparbackofficeConstants.ORDER_NO_SHOW_RETRY_QUALIFIER))
										{
											value = consginment.getOrder().getOderRetryNoShow();
										}
										else if (qualifier.startsWith("order." + SparbackofficeConstants.ORDER_DELIVERY_DATE_QUALIFIER))
										{

											value = getFormattedDeliverySlotDate(consginment);
										}
										else if (qualifier.startsWith("order." + SparbackofficeConstants.ORDER_DELIVERY_SLOT_QUALIFIER))
										{
											stringValue = getDeliverySlot(consginment);
										}

										else if (qualifier.startsWith(SparbackofficeConstants.PAYMENT_QUALIFIER))
										{
											stringValue = processPaymentData(consginment, stringValue, qualifier, value);
										}
										else if (qualifier.startsWith("warehousingbackoffice.consignment."))
										{
											final String qualifierConsignmentEntry = qualifier.replace("warehousingbackoffice.consignment.",
													"");
											value = getValueFromQualifier(consignmentEntry, qualifierConsignmentEntry);
											if (qualifier.contains(OFFER_TOTAL) && "Y".equalsIgnoreCase(promoCheck))
											{
												if (null != consignmentEntry.getOrderEntry().getTotalPrice())
												{
													stringValue = consignmentEntry.getOrderEntry().getTotalPrice().toString();
												}
											}
											if (qualifier.contains(SparbackofficeConstants.ORDER_VOUCHERCODE))
											{
												if(LOG.isDebugEnabled())
												{
   												LOG.info("QUALIFIER");
   												LOG.info(qualifier);
												}
												stringValue = getVoucherCode(consginment);
											}
											if (qualifier.contains(SparbackofficeConstants.ORDER_VOUCHERVALUE))
											{
												if(LOG.isDebugEnabled())
												{
   												LOG.info("QUALIFIER");
   												LOG.info(qualifier);
												}
												stringValue = getVoucherValue(consginment);
											}
										}
										else
										{
											stringValue = value == null ? "" : this.getLabelService().getObjectLabel(value);
										}
									}
									catch (final Exception e)
									{
										LOG.debug("Could not get value for field '" + qualifier + "'. Using string representation instead."
												+ "\n" + e.getMessage());
									}
								}
								if (value instanceof HashMap)
								{
									final Locale currentLocale = this.cockpitLocaleService.getCurrentLocale();
									final String localizedValue = (String) ((HashMap) value).get(currentLocale);
									stringValue = (String) StringUtils.defaultIfBlank((CharSequence) localizedValue, (CharSequence) "");
								}
								else if (StringUtils.isBlank(stringValue))
								{
									stringValue = value == null ? "" : value.toString();
								}
							}
							builder.append(this.escapeForCSV(this.securityEscape(stringValue))).append(csvDelimiter);
						}
						sno++;
						builder.append('\n');
					}
				}
			}
			catch (final TypeNotFoundException tnf)
			{
				LOG.warn("Could not find type", tnf);
			}
		}
	}

	/**
	 * @param consignmentEntry
	 * @param qualifier
	 *
	 */
	private String getProductName(final ConsignmentEntryModel consignmentEntry)
	{
		String productName = null;
		final ProductModel productModel = consignmentEntry.getOrderEntry().getProduct();
		if (null != productModel)
		{
			productName = productModel.getName();
		}
		return productName;
	}

	private String getVoucherCode(final ConsignmentModel consginment)
	{
		voucher_code = "";
		if (CollectionUtils.isNotEmpty(consginment.getOrder().getDiscounts()))
		{
			/*
			 * final List<DiscountModel> discountList = consginment.getOrder().getDiscounts(); if (discountList != null) {
			 * for (final DiscountModel d : discountList) { voucher_code = d.getCode(); } LOG.info(voucher_code); return
			 * voucher_code; }
			 */
			voucher_code = consginment.getOrder().getVoucherDescription();
			LOG.info(voucher_code);
			return voucher_code;
		}
		LOG.info(voucher_code);
		return voucher_code;
	}

	private String getVoucherValue(final ConsignmentModel consginment)
	{
		voucher_value = "";
		if (consginment.getOrder().getDiscounts() != null)
		{
/*			final List<DiscountModel> discountList = consginment.getOrder().getDiscounts();

			if (discountList != null)
			{
				for (final DiscountModel d : discountList)
				{
					LOG.info(voucher_value);
					voucher_value = d.getValue().toString();
				}
			}*/
			if(null != consginment.getOrder().getPaidByVoucher())
			{
				voucher_value = consginment.getOrder().getPaidByVoucher().toString();
			}
		}
		LOG.info(voucher_value);
		return voucher_value;
	}

	/**
	 * @param consginment
	 * @return
	 */

	private String getDeliverySlot(final ConsignmentModel consginment)
	{
		if (null != consginment.getOrder().getOderRetryNoShow() && consginment.getOrder().getOderRetryNoShow().equals(Boolean.TRUE)
				&& null != consginment.getOrder())
		{
			return consginment.getOrder().getDeliverySlot();
		}
		else
		{
			if (null != consginment.getOrder().getOrderDeliverySlot())
			{
				return consginment.getOrder().getOrderDeliverySlot().getSlotDescription();
			}
		}
		return null;
	}

	/**
	 * this method is used to retrieve value for collected amount and transactionId for ICICI/COD flow
	 *
	 * @param consginment
	 * @param stringValue
	 * @param qualifier
	 * @param value
	 * @return String
	 */
	protected String processPaymentData(final ConsignmentModel consginment, String stringValue, String qualifier, Object value)
	{
		//final boolean isIPGTransaction = false;
		qualifier = qualifier.replace(SparbackofficeConstants.PAYMENT_QUALIFIER, "");
		if (null != consginment && null != consginment.getOrder())
		{
			//if (null != consginment.getOrder().getPaymentMode()
			//		&& PaymentModeEnum.CREDITCARD.getCode().equals(consginment.getOrder().getPaymentMode().getCode()))
			//{
			//	isIPGTransaction = true;
			//}

			value = getValueFromQualifier(consginment, qualifier);

			//if (isIPGTransaction)
			//	{

			//	}
			//else
			//{
			if (qualifier.equals("order.paymentStatus"))
			{
				stringValue = consginment.getOrder().getPaymentStatus().toString();
			}
			//	if (qualifier.equals("order.paidByWallet"))
			//	{
			//		if (null != consginment.getOrder().getPaidByWallet())
			//		{
			//			stringValue = consginment.getOrder().getPaidByWallet().toString();
			//		}
			//	}
			if (qualifier.equals("order.balanceDue"))
			{
				if ((consginment.getOrder().getPaymentMode().getCode().equalsIgnoreCase(PaymentModeEnum.MULTIPAYMENTMODE.getCode()) && consginment
						.getOrder().getPaymentStatus().toString().equals(PaymentStatus.PAID.getCode()))
						|| (consginment.getOrder().getPaymentMode().getCode().equalsIgnoreCase(PaymentModeEnum.CREDITCARD.getCode())))
				{
					stringValue = consginment.getOrder().getBalanceDue().toString();
				}
			}
			if (qualifier.equals("order.remainingAmount"))
			{
				if (consginment.getOrder().getPaymentStatus().getCode().equals(PaymentStatus.NOTPAID.getCode())
						|| consginment.getOrder().getPaymentStatus().getCode().equals(PaymentStatus.PARTPAID.getCode()))
				{
					stringValue = consginment.getOrder().getBalanceDue().toString();
				}
			}
			if (qualifier.equals("order.paymentInfo"))
			{
				if (value instanceof CreditCardPaymentInfoModel)
				{
					stringValue = ((CreditCardPaymentInfoModel) value).getSubscriptionId();
				}
				if (null != consginment.getOrder().getPaymentMode()
						&& consginment.getOrder().getPaymentMode().getCode().equalsIgnoreCase("payTM"))
				{
					stringValue = ((PayTMPaymentInfoModel) value).getCode();
				}
			}
			if (qualifier.equals("order.paidByLandmarkReward"))
			{
				if (null != consginment.getOrder().getPaidByLandmarkReward())
				{
					stringValue = consginment.getOrder().getPaidByLandmarkReward().toString();
				}
			}

			if (qualifier.equals("order.paidBypayTm"))
			{
				if (null != consginment.getOrder().getPaymentMode()
						&& consginment.getOrder().getPaymentMode().getCode().equalsIgnoreCase("payTM")
						&& null != consginment.getOrder().getTotalPrice())
				{
					stringValue = consginment.getOrder().getTotalPrice().toString();
				}
			}
			if (qualifier.equals("order.orderDiscount"))
			{
				if (null != consginment.getOrder().getTotalDiscounts())
				{
					stringValue = consginment.getOrder().getTotalDiscounts().toString();
				}
			}

		}
		return stringValue;
	}

	/**
	 * This method is used to sort the list if the typecode = Consignment
	 *
	 * @param list
	 * @return List
	 */
	private List doSort(final List list)
	{
		if (!list.isEmpty() && list.iterator().next() instanceof ConsignmentModel)
		{
			try
			{
				final List sortedList = new ArrayList<ConsignmentModel>(list);
				Collections.sort(sortedList, new PickListComparator());
				return sortedList;
			}
			catch (final Exception e)
			{
				//in case of any failure
				LOG.warn("Could not sort picklist  on Delivery Date - ", e);
				return list;
			}
		}
		else
		{
			return list;
		}
	}


	/**
	 * This method is used to retrieve value from an object using qualifier.
	 *
	 * @param object
	 * @param qualifier
	 * @return Object
	 */
	private Object getValueFromQualifier(final Object object, final String qualifier)
	{
		Object value;
		value = getDefaultSparPropertyValueService().readValue(object, qualifier);
		if (qualifier.equalsIgnoreCase("order.salesApplication"))
		{
			if (null != value && value.toString().equalsIgnoreCase("WebMobile"))
			{
				value = "Mobile App";
			}
		}
		return value;
	}

	/**
	 * This method is used to retrieve the Category for the product
	 *
	 * @param consignmentEntry
	 * @return categoryName
	 */
	private Object getProductCategoryName(final ConsignmentEntryModel consignmentEntry)
	{
		String categoryName = null;
		final Collection<CategoryModel> catgoryModelCollection = consignmentEntry.getOrderEntry().getProduct().getSupercategories();
		if (null != catgoryModelCollection && !catgoryModelCollection.isEmpty() && catgoryModelCollection.iterator().hasNext())
		{
			categoryName = (consignmentEntry.getOrderEntry().getProduct().getSupercategories().iterator().next()).getName();
		}
		return categoryName;
	}


	/**
	 * This method is used to process the ConsignmmentEntry object using the qualifier.
	 *
	 * @param consignmentEntry
	 * @param qualifier
	 * @return Object
	 * @throws TypeNotFoundException
	 */
	private Object processConsginmentEntry(final ConsignmentEntryModel consignmentEntry, String qualifier)
			throws TypeNotFoundException
	{
		Object value;
		final String originalQualifier = qualifier;
		qualifier = qualifier.replace(SparbackofficeConstants.CONSIGNMENT_QUALIFIER, "orderEntry.");
		qualifier = getPriceRowQualifier(qualifier);
		value = getValueFromQualifier(consignmentEntry, qualifier);
		value = getValueFromPriceRow(value, originalQualifier, consignmentEntry);
		return value;
	}

	/**
	 * This method is used to get the priceRow qualifier
	 *
	 * @param qualifier
	 */
	private String getPriceRowQualifier(String qualifier)
	{
		if (qualifier.endsWith(UNIT_MRP))
		{
			qualifier = qualifier.replaceFirst(UNIT_MRP, "");
		}
		else if (qualifier.endsWith(PROMO_CHECK))
		{
			qualifier = qualifier.replaceFirst(PROMO_CHECK, "");
		}
		return qualifier;
	}

	/**
	 * This method retrieves the value from price row for getValueFromPriceRow
	 *
	 * @param value
	 * @param originalQualifier
	 * @return unitMRP/CheckForPromo
	 */
	private Object getValueFromPriceRow(Object value, final String originalQualifier, final ConsignmentEntryModel consignmentEntry)
	{
		if (SparbackofficeConstants.CHECK_FOR_PROMO_QUALIFIER.equals(originalQualifier) && value != null
				&& value instanceof Collection && !((Collection) value).isEmpty())
		{
			Boolean checkForPromotion = null;
			final Iterator itr = ((Collection) value).iterator();
			while (itr.hasNext())
			{
				final SparPriceRowModel sparPriceRowModel = (SparPriceRowModel) itr.next();
				if (null != sparPriceRowModel.getWarehouse()
						&& null != consignmentEntry.getConsignment().getWarehouse()
						&& sparPriceRowModel.getWarehouse().getCode()
								.equalsIgnoreCase(consignmentEntry.getConsignment().getWarehouse().getCode()))
				{
					checkForPromotion = sparPriceRowModel.getCheckForPromotion();
					break;
				}
			}
			value = checkForPromotion != null && checkForPromotion.booleanValue() ? "Y" : "N";
		}
		else if (SparbackofficeConstants.UNIT_MRP_QUALIFIER.equals(originalQualifier) && value != null
				&& value instanceof Collection && !((Collection) value).isEmpty())
		{
			final Collection<SparPriceRowModel> priceRowModels = ((Collection<SparPriceRowModel>) value);
			for (final SparPriceRowModel model : priceRowModels)
			{
				if (consignmentEntry.getOrderEntry().getOrder().getOrderWarehouse().getCode().equals(model.getWarehouse().getCode()))
				{
					value = model.getUnitMRP().toString();
				}
			}
		}
		return value;
	}

	/**
	 * This method is used to process the PlanoGram for getting values against the qualifier.
	 *
	 * @param object
	 * @param consignmentEntry
	 * @param qualifier
	 * @return Object
	 */
	private Object processPlanoGram(final Object object, final ConsignmentEntryModel consignmentEntry, String qualifier)
	{
		final String warehouse = ((WarehouseModel) getValueFromQualifier(object, SparbackofficeConstants.WAREHOUSE)).getCode();
		final String productId = consignmentEntry.getOrderEntry().getProduct().getCode();
		try
		{
			final PlanoGram planogram = getPlanoGramforProductAndPos(warehouse, productId);
			qualifier = qualifier.replace(SparbackofficeConstants.PLANOGRAM_QUALIFIER, "");
			return getValueFromQualifier(planogram, qualifier);
		}
		catch (final Exception e)
		{
			LOG.debug("Could not get Planogram Details for the product: " + productId + " and the Location: " + warehouse + "\n"
					+ e.getMessage());
		}
		return null;
	}

	/**
	 * This method is used to get the PlanoGram on the basis of productId and warehouse.
	 *
	 * @param warehouse
	 * @param productId
	 * @return PlanoGram
	 */
	public PlanoGram getPlanoGramforProductAndPos(final String warehouse, final String productId)
	{
		final PlanoGram planogram = new PlanoGram();
		final List<PlanoGram> result = planogram.findPlanoGramByProductAndPos(productId, warehouse);
		if (null == result || result.isEmpty())
		{
			throw new UnknownIdentifierException("No records found in planogram for productId '" + productId + "'  and Warehouse '"
					+ warehouse + "'");
		}
		else if (result.size() > 1)
		{
			throw new AmbiguousIdentifierException("Multiple records found in planogram for productId '" + productId
					+ "'  and Warehouse '" + warehouse + "'");
		}
		return result.get(0);
	}

	/**
	 * This method is used to wrap Headers by quotes.
	 *
	 * @param header
	 * @return String
	 */
	private String wrapHeaderForCSV(final String header)
	{
		return String.format("\"%s\"", header);
	}

	/**
	 * This method is used to escape characters for CSV
	 *
	 * @param value
	 * @return String
	 */
	private String escapeForCSV(final String value)
	{
		String ret = value;
		if (value.contains(csvDelimiter) || value.contains("\""))
		{
			ret = "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return ret.replace('\n', ' ');
	}

	/**
	 * This method is used to escape the value by enclosing it into single quotes.
	 *
	 * @param value
	 * @return String
	 */
	private String securityEscape(final String value)
	{
		String ret = value;
		if (value.startsWith("="))
		{
			ret = "'" + ret + "'";
		}
		return ret;
	}

	@Override
	public boolean canPerform(final ActionContext<Map> ctx)
	{
		final Map data = ctx.getData();
		if (data == null)
		{
			return false;
		}
		final Object pageable = data.get(SparbackofficeConstants.PAGEABLE);
		final Object listView = data.get(SparbackofficeConstants.MODEL_COLUMNS_CONFIG);
		return pageable instanceof Pageable && listView instanceof ListView;
	}

	@Override
	public boolean needsConfirmation(final ActionContext<Map> ctx)
	{
		final Map data = ctx.getData();
		if (data == null)
		{
			return false;
		}
		final Pageable pageable = (Pageable) data.get(SparbackofficeConstants.PAGEABLE);
		final int confirmationThreshold = this.getConfirmationThreshold(ctx);
		return confirmationThreshold > 0 && pageable.getTotalCount() > confirmationThreshold;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<Map> ctx)
	{
		final Pageable pageable = (Pageable) ctx.getData().get(SparbackofficeConstants.PAGEABLE);
		return ctx.getLabel(SparbackofficeConstants.EXPORT_CSV_CONFIRMATION, new Object[]
		{ Integer.valueOf(pageable.getTotalCount()), Integer.valueOf(this.getConfirmationThreshold(ctx)) });
	}

	/**
	 * This method is used to set the threshHold Value
	 *
	 * @param ctx
	 * @return int
	 */
	private int getConfirmationThreshold(final ActionContext<Map> ctx)
	{
		final Object parameter = ctx.getParameter("confirmation.threshold");
		if (parameter instanceof Integer)
		{
			return ((Integer) parameter).intValue();
		}
		if (parameter instanceof String)
		{
			try
			{
				return Integer.parseInt((String) parameter);
			}
			catch (final NumberFormatException nfe)
			{
				LOG.warn(String.format("Invalid integer [%s]", parameter), nfe);
			}
		}
		return -1;
	}

	/**
	 * This method is used to determine whether the property can be read or not.
	 *
	 * @param type
	 * @param qualifier
	 * @return boolean
	 */
	private boolean canReadProperty(final String type, final String qualifier)
	{
		try
		{
			return this.getPermissionFacade().canReadInstanceProperty(type, qualifier);
		}
		catch (final Exception exc)
		{
			LOG.warn("Could not check assigned permissions", exc);
			return false;
		}
	}

	/**
	 * Getter for LabelService
	 *
	 * @return LabelService
	 */
	public LabelService getLabelService()
	{
		return this.labelService;
	}

	/**
	 * Setter for LabelService
	 *
	 * @param labelService
	 */
	public void setLabelService(final LabelService labelService)
	{
		this.labelService = labelService;
	}

	/**
	 * Getter for PermissionFacade
	 *
	 * @return PermissionFacade
	 */
	public PermissionFacade getPermissionFacade()
	{
		return this.permissionFacade;
	}

	/**
	 * Setter for PermissionFacade
	 *
	 * @param permissionFacade
	 */
	public void setPermissionFacade(final PermissionFacade permissionFacade)
	{
		this.permissionFacade = permissionFacade;
	}

	/**
	 * Getter for PropertyValueService
	 *
	 * @return PropertyValueService
	 */
	public PropertyValueService getPropertyValueService()
	{
		return this.propertyValueService;
	}

	/**
	 * Setter for PropertyValueService
	 *
	 * @param propertyValueService
	 */
	public void setPropertyValueService(final PropertyValueService propertyValueService)
	{
		this.propertyValueService = propertyValueService;
	}

	/**
	 * @return the defaultSparPropertyValueService
	 */
	public DefaultSparPropertyValueService getDefaultSparPropertyValueService()
	{
		return defaultSparPropertyValueService;
	}

	/**
	 * @param defaultSparPropertyValueService the defaultSparPropertyValueService to set
	 */
	public void setDefaultSparPropertyValueService(DefaultSparPropertyValueService defaultSparPropertyValueService)
	{
		this.defaultSparPropertyValueService = defaultSparPropertyValueService;
	}

	/**
	 * Getter for TypeFacade
	 *
	 * @return TypeFacade
	 */
	public TypeFacade getTypeFacade()
	{
		return this.typeFacade;
	}

	/**
	 * Setter for TypeFacade
	 *
	 * @param typeFacade
	 */
	public void setTypeFacade(final TypeFacade typeFacade)
	{
		this.typeFacade = typeFacade;
	}

	/**
	 * This class is a comparator class for ConsignmentModel which sorts the list on the basis of Delivery date and
	 * Delivery Slot
	 *
	 * @author rohan_c
	 *
	 */
	class PickListComparator implements Comparator<ConsignmentModel>
	{
		@Override
		public int compare(final ConsignmentModel consginment1, final ConsignmentModel consginment2)
		{
			try
			{
				final Date date1 = getDeliverySlotDate(consginment1);
				final Date date2 = getDeliverySlotDate(consginment2);
				if (null == date1 || null == date2)
				{
					// in case if Date is null then no sort should be done.
					return 0;
				}
				return date1.compareTo(date2);
			}
			catch (final Exception e)
			{
				//in case of any failure
				LOG.warn("Could not compare Delivery Date due to exception like java.lang.NullPointerException in getDeliverySlotDate() method");
				return 0;
			}
		}
	}

	/**
	 * This method is used to retrieve date from the Delivery Date and Delivery Slot
	 *
	 * @param dateInString
	 * @param format
	 * @return Date
	 */
	private Date getDate(final String dateInString, final String format)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		try
		{
			date = formatter.parse(dateInString);
		}
		catch (final ParseException pe)
		{
			LOG.warn("Could not parse Date " + dateInString, pe);
		}
		return date;
	}

	/**
	 * Get DeliverySlot date with time for an order
	 *
	 * @param consginment
	 * @return date
	 */
	private Date getDeliverySlotDate(final ConsignmentModel consginment)
	{
		final String delimeter = "To";
		if (null !=consginment.getOrder() && null != consginment.getOrder().getSlotDeliveryDate())
		{
			final DateTimeFormatter formatter = ISODateTimeFormat.date();
			final String consignmentDate = formatter.print(consginment.getOrder().getSlotDeliveryDate().getTime());
			final String consignmentTime = consginment.getOrder().getOrderDeliverySlot().getSlotDescription().split(delimeter)[0];
			return getDate(consignmentDate + " " + consignmentTime, "yyyy-MM-dd hh:mm a");
		}
		else if (null != consginment.getOrder() && null != consginment.getOrder().getDeliveryDate())
		{
			final String consignmentTime = consginment.getOrder().getDeliverySlot().split(delimeter)[0];
			return getDate(consginment.getOrder().getDeliveryDate() + " " + consignmentTime, "MMM dd,yyyy hh:mm a");
		}
		return null;
	}

	/**
	 * Get DeliverySlot date with time for an order
	 *
	 * @param consginment
	 * @return date
	 */
	private String getFormattedDeliverySlotDate(final ConsignmentModel consginment)
	{
		if (null != consginment.getOrder().getOderRetryNoShow() && consginment.getOrder().getOderRetryNoShow().equals(Boolean.TRUE)
				&& null != consginment.getOrder().getDeliveryDate())
		{

			//return consginment.getOrder().getDeliveryDate();


			final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");
			final SimpleDateFormat formatterConv = new SimpleDateFormat("MMM dd,yyyy");
			try
			{
				final Date date = formatter.parse(consginment.getOrder().getDeliveryDate());
				return (formatterConv.format(date));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}


		}
		else
		{
			if (null != consginment.getOrder().getSlotDeliveryDate())
			{
				final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
				return formatter.format(consginment.getOrder().getSlotDeliveryDate());
			}
			else if (null != consginment.getOrder().getDeliveryDate())
			{
				return consginment.getOrder().getDeliveryDate();
			}
		}
		return "";
	}
}
