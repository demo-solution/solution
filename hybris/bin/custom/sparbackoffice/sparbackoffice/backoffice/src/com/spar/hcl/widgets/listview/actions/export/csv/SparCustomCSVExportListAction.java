/**
 *
 */
package com.spar.hcl.widgets.listview.actions.export.csv;

import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.export.csv.ListViewExportCSVAction;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.spar.hcl.constants.SparbackofficeConstants;
import com.spar.hcl.widgets.listview.actions.export.csv.SparCustomCSVExportListAction.AssetTrackerComparator;


/**
 * This class extends the OOTB Action to exporting CSV and extends ListViewExportCSVAction .
 *
 * @author rohan_c
 *
 */
public class SparCustomCSVExportListAction extends com.hybris.cockpitng.actions.export.csv.ListViewExportCSVAction
{

	private static final Logger LOG = LoggerFactory.getLogger(ListViewExportCSVAction.class);

	@Resource
	private CockpitLocaleService cockpitLocaleService;

	private String csvDelimiter = ",";

	/**
	 * This method performs the task of Custom CSV generation.
	 */
	@Override
	public ActionResult<Object> perform(final ActionContext<Map> ctx)
	{
		csvDelimiter = Config.getString(SparbackofficeConstants.CSV_DELIMETER, csvDelimiter);
		final Pageable pageable = (Pageable) ctx.getData().get(SparbackofficeConstants.MODEL_PAGEABLE);
		final ListView listView = (ListView) ctx.getData().get(SparbackofficeConstants.MODEL_COLUMNS_CONFIG);
		final String csvContent = this.createCsv(pageable, listView);
		this.writeBinaryResponse(csvContent);
		return new ActionResult(SparbackofficeConstants.SUCCESS);
	}

	/**
	 * This method writes he CSV and makes it available for download
	 *
	 * @param csvContent
	 */
	@Override
	protected void writeBinaryResponse(final String csvContent)
	{
		super.writeBinaryResponse(csvContent);
	}


	/**
	 * This methods creates the CSV including header and contents.
	 *
	 * @param pageable
	 * @return String
	 */
	private String createCsv(final Pageable pageable, final ListView listView)
	{
		final StringBuilder builder = new StringBuilder();
		final List<ListColumn> columnsToRender = findColumnsPrintableInCSV(listView.getColumn());
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
				//	.getColumnHeaderLabel(listColumn, pageable.getTypeCode(), getLabelService());
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
	 * this method is used to create the content of the Custom CSV.
	 *
	 * @param builder
	 * @param pageable
	 * @param columns
	 */
	private void createCsvContent(final StringBuilder builder, final Pageable pageable, final List<ListColumn> columns)
	{

		for (final Object object : doSort(pageable.getAllResults()))
		{
			try
			{
				final String dataTypeCode = this.getTypeFacade().getType(object);
				final DataType dataType = StringUtils.isBlank(dataTypeCode) ? null : this.getTypeFacade().load(dataTypeCode);
				for (final ListColumn listColumn : columns)
				{
					String stringValue = "";
					final String qualifier = listColumn.getQualifier();
					if (dataType == null || this.canReadProperty(dataTypeCode, qualifier))
					{
						Object value;
						block11:
						{
							value = this.getPropertyValueService().readValue(object, qualifier);
							final DataAttribute attribute = dataType != null ? dataType.getAttribute(qualifier) : null;
							if (attribute == null || attribute.getValueType() == null || !attribute.getValueType().isAtomic())
							{
								try
								{
									stringValue = this.getLabelService().getObjectLabel(value);
								}
								catch (final Exception e)
								{
									if (!LOG.isDebugEnabled())
									{
										break block11;
									}
									LOG.debug("Could not get value for field '" + qualifier + "'. Using string representation instead.");
								}
							}
						}

						if (qualifier.equals(SparbackofficeConstants.ORDER_DELIVERY_DATE_QUALIFIER))
						{
							value = getFormattedDeliverySlotDateNoMillis((AbstractOrderModel) object);
						}
						if (qualifier.equals(SparbackofficeConstants.ORDER_DELIVERY_SLOT_QUALIFIER) && null == value)
						{
							stringValue = ((AbstractOrderModel) object).getDeliverySlot();
						}
						if (qualifier.equals(SparbackofficeConstants.ORDER_POINT_OF_SERVICE) && null == value)
						{
							stringValue = ((AbstractOrderModel) object).getPointOfService();
						}
						if (qualifier.equals(SparbackofficeConstants.EMPLOYEE_CODE) && null == value)
						{
							stringValue = "";
						}
						if (qualifier.equals(SparbackofficeConstants.CUSTOMER_NAME))
						{
							stringValue = ((AbstractOrderModel) object).getUser().getName();
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
			}
			catch (final TypeNotFoundException tnf)
			{
				LOG.warn("Could not find type", tnf);
			}
			builder.append('\n');
		}
	}

	/**
	 * This method is used to sort the list if the typecode = Order
	 *
	 * @param list
	 * @return List
	 */
	private List doSort(final List list)
	{
		if (!list.isEmpty() && list.iterator().next() instanceof AbstractOrderModel)
		{
			try
			{
				final List sortedList = new ArrayList<AbstractOrderModel>(list);
				Collections.sort(sortedList, new AssetTrackerComparator());
				return sortedList;
			}
			catch (final Exception e)
			{
				//in case of any failure
				LOG.warn("Could not sort Asset Tracker list  on Delivery Date - ", e);
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
		value = this.getPropertyValueService().readValue(object, qualifier);
		return value;
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
	public String escapeForCSV(final String value)
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
	 * This class is a comparator class for AbstractOrderModel which sorts the list on the basis of Delivery date and
	 * Delivery Slot
	 *
	 * @author rohan_c
	 *
	 */
	class AssetTrackerComparator implements Comparator<AbstractOrderModel>
	{

		@Override
		public int compare(final AbstractOrderModel order1, final AbstractOrderModel order2)
		{
			try
			{
				final Date date1 = getDeliverySlotDate(order1);
				final Date date2 = getDeliverySlotDate(order2);

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
				LOG.warn("Could not compare Delivery Date ", e);
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
	 * @param order
	 * @return date
	 */
	private Date getDeliverySlotDate(final AbstractOrderModel order)
	{
		final String delimeter = "To";
		if (null != order.getSlotDeliveryDate())
		{
			final String orderTime = order.getOrderDeliverySlot().getSlotDescription().split(delimeter)[0];
			final DateTimeFormatter formatter = ISODateTimeFormat.date();
			final String consignmentDate = formatter.print(order.getSlotDeliveryDate().getTime());
			return getDate(consignmentDate + " " + orderTime, "yyyy-MM-dd hh:mm a");
		}
		else if (null != order.getDeliverySlot())
		{
			final String orderTime = order.getDeliverySlot().split(delimeter)[0];
			return getDate(order.getDeliveryDate() + " " + orderTime, "MMM dd,yyyy hh:mm a");
		}
		return null;
	}

	/**
	 * Get DeliverySlot date with time for an order
	 *
	 * @param order
	 * @return date
	 */
	private String getFormattedDeliverySlotDateNoMillis(final AbstractOrderModel order)
	{
		if (null != order.getSlotDeliveryDate())
		{
			final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
			return formatter.format(order.getSlotDeliveryDate());
		}
		else if (null != order.getDeliverySlot())
		{
			return order.getDeliveryDate();
		}
		return "";
	}

}
