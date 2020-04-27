/**
 *
 */
package com.spar.hcl.core.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spar.hcl.core.enums.PaymentModeEnum;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVWriter;
import de.hybris.platform.util.Config;


/**
 * @author nileshkumar.c
 *
 */
public class SparAvailStockExportJob extends AbstractJobPerformable
{
	private static final Logger LOG = Logger.getLogger(SparExportProductsJob.class);

	StockLevelModel StockLevelModel = null;
	Date date = new Date();
	Date datecal;
	FlexibleSearchService flexibleSearchService;




	// CSVWriter settings
	private static char commentchar = CSVConstants.DEFAULT_COMMENT_CHAR;
	private static char fieldseperator = ',';
	private static char textseperator = CSVConstants.DEFAULT_QUOTE_CHARACTER;

	// stores all CSVWriter, output map
	private Map<String, MyCSVWriter> outputWriters;

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	public static final int SPAR_ORDER_HEADER_COUNT = 11;

	public static final String SPAR_ORDER_FILENAME = "SparOrderInfo";

	//Location for base folder
	public static final String BASE_FOLDER = "acceleratorservices.export.basefolder";
	public static final String orderDateFmt = "dd.MM.yyyy HH:mm:ss";

	final DecimalFormat df = new DecimalFormat("#.00");



	public static final String timeInterval = Config.getParameter("sparavailstockexportjob.order.timeInterval");
	//Delimiters
	public static final String UNDERSCORE_DELIMETER = "_";
	public static final String FILE_EXTENSION = ".csv";

	//Date date;
	SimpleDateFormat dateFormat;
	String voucherCode = "";
	Double vouchervalue;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		datecal = new Date();
		final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
		final Calendar currentDate = Calendar.getInstance();
		final long timeInMiliSec = currentDate.getTimeInMillis();
		final Date afterSubtractMins = new Date(timeInMiliSec - (Integer.valueOf(timeInterval) * ONE_MINUTE_IN_MILLIS));
		final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String currentDateWithFormat = dateFormat1.format(afterSubtractMins);
		date = new Date();
		outputWriters = new HashMap<String, MyCSVWriter>();
		dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String QUERY_FOR_ORDERS = "SELECT {o.code},{os.code},{p.code},{oe.quantity},{w.code},{o.creationtime}, {offsku.offlineSKU}, {offsku.grammageRatio} "
				+ "from {Order as o JOIN orderEntry as oe on {o:pk}={oe:order}  join product as p on {oe.product} = {p.pk} "
				+ "join stocklevel as sl on {p.code} = {sl.productcode}  and  {o.orderWarehouse}={sl.warehouse} "
				+ "join orderstatus as os on {o.status} = {os.pk} join Warehouse as w on {o.orderWarehouse}={w.pk} "
				+ "join Warehouse as w on {o.orderWarehouse}={w.pk} left join SparOnlineOfflineSKU as offsku on {p.code}={offsku.onlineSKU}} "
				+ "where {o.creationtime} >";
		QUERY_FOR_ORDERS = QUERY_FOR_ORDERS + "'" + currentDateWithFormat + "'";
		QUERY_FOR_ORDERS = QUERY_FOR_ORDERS + " or {o.modifiedtime} > '" + currentDateWithFormat + "'";
		try
		{

			final FlexibleSearchQuery queryForOrders = new FlexibleSearchQuery(QUERY_FOR_ORDERS);
			queryForOrders.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class, String.class,
					Date.class,String.class,String.class));

			final SearchResult<List> resultForOrders = flexibleSearchService.search(queryForOrders);
			if (resultForOrders.getResult().size() != 0)
			{
				final List<List> list2 = resultForOrders.getResult();
				final List<List> listContainer = new ArrayList<>(list2);
				for (int i = 0; i <= listContainer.size() - 1; i++)
				{
					final List row = listContainer.get(i);
					// populating the value of Prepaid Transaction Number
					final String ordderId = (String) row.get(0);
					final OrderModel order = getOrderForCode(ordderId);
					final String paymentSubId = getPaymentSubscriptionId(order);
					// now need to add at 2nd position of list entries for this need to make internal loop toaccess this
					final boolean isAllCancelled = checkOrderLevelCancel(row, order);
					//added for cancel status for order entry level
					checkOrderEntryLevelStatus(row, order, isAllCancelled);
					//doing operation on order creation date  28.06.2016  12:15:35
					final SimpleDateFormat formatter = new SimpleDateFormat(orderDateFmt);
					final String orderDate = formatter.format(order.getCreationtime());
					
					String convertedOfflineItemCode = (String) row.get(6);
					row.remove(6);
					row.set(5, orderDate);
					
					String calculatedQty = null;
					if(null != row.get(6) && null != row.get(3))
					{
						double productQty =  Double.valueOf(row.get(3).toString()).doubleValue();
						double grammageRatio = Double.valueOf(row.get(6).toString()).doubleValue();
						calculatedQty = df.format(productQty * grammageRatio);
					}
					row.remove(6);
					row.add(6,paymentSubId);
					final List<DiscountModel> discountList = order.getDiscounts();
					if (discountList != null)
					{
						for (final DiscountModel d : discountList)
						{
							voucherCode = d.getCode();
							vouchervalue = d.getValue();
						}
					}
					row.add(7, voucherCode);
					row.add(8, vouchervalue);
					if(null != convertedOfflineItemCode)
					{
						row.add(9, convertedOfflineItemCode);
					}
					if(null != calculatedQty)
					{
						row.add(10, calculatedQty);
					}
				}
				getOut(SPAR_ORDER_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).write(
						addSparProductsComment());

				for (final List line : listContainer)
				{
					final Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
					int i = 0;
					for (final Object lineValue : line)
					{
						try
						{
							mapLine.put(Integer.valueOf(i), (lineValue != null ? lineValue.toString() : ""));
						}
						catch (final ArrayIndexOutOfBoundsException exception)
						{
							mapLine.put(Integer.valueOf(i), "");
						}
						i++;
					}
					getOut(SPAR_ORDER_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).write(mapLine);
				}
				getOut(SPAR_ORDER_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).close();
			}

			else
			{

				LOG.info("No order found in current export time interval");
			}

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Order Export", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	/**
	 * This method is used to check order level cancel
	 *
	 * @param row
	 * @param order
	 * @return boolean
	 */
	private boolean checkOrderLevelCancel(final List row, final OrderModel order)
	{
		boolean isAllCancelled = true;
		for (final ConsignmentModel consignmentModel : order.getConsignments())
		{
			if (!ConsignmentStatus.CANCELLED.equals(consignmentModel.getStatus()))
			{
				isAllCancelled = false;
				break;
			}
		}

		if (isAllCancelled && OrderStatus.COMPLETED.equals(order.getStatus()))
		{
			row.set(1, OrderStatus.CANCELLED.getCode());
		}
		return isAllCancelled;
	}

	/**
	 * This method is used to check order entry level cancel
	 *
	 * @param row
	 * @param order
	 * @param isAllCancelled
	 */
	private void checkOrderEntryLevelStatus(final List row, final OrderModel order, final boolean isAllCancelled)
	{
		if (!isAllCancelled)
		{
			for (final AbstractOrderEntryModel orderEntrymodel : order.getEntries())
			{
				if (null != orderEntrymodel.getProduct() && orderEntrymodel.getProduct().getCode().equals(row.get(2)))
				{
					if (((OrderEntryModel)orderEntrymodel).getQuantityCancelled().longValue() > 0)
					{
						row.set(1, OrderStatus.CANCELLED.getCode());
					}
				}
			}
		}
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the catalogVersionService
	 */


	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */


	private Map<Integer, String> addSparProductsComment()
	{
		final Map<Integer, String> mapHeaderLine = new HashMap<Integer, String>();
		final String[] sparProductsLine = new String[]
		{ "ORDER_NUMBER", "STATUS", "PRODUCT_NUMBER", "QUANTITY_SOLD", "STORE_LOCATION", "SUBMITTED_ORDER_DATE",
				"Prepaid Transaction Number", "Voucher Code", "Voucher Value" , "CONVERTED_OFFLINE_ITEM_CODE" , "CONVERTED_OFFLINE_ITEM_QTY" };
		for (int i = 0; i < SPAR_ORDER_HEADER_COUNT; i++)
		{
			mapHeaderLine.put(Integer.valueOf(i), sparProductsLine[i]);
		}
		return mapHeaderLine;
	}

	/**
	 * This method gets the CSV writer with the specified fileName
	 *
	 * @param fileName
	 *           the used filename
	 * @return a CSVWriter (here MyCSVWriter) for the given filename. The Encoding is set to UTF-8 and the file is bound
	 *         to the CSVWriter.
	 */
	protected MyCSVWriter getOut(final String fileName)
	{

		/*
		 * final String path = (String)
		 * Registry.getApplicationContext().getBean(DefaultConfigurationService.class).getConfiguration()
		 * .getProperty(BASE_FOLDER);
		 */
		final String path = Config.getParameter("sparcore.sparOrderExportCronJob.export.path");
		MyCSVWriter csvWriter = outputWriters.get(fileName);
		if (csvWriter == null)
		{
			try
			{
				csvWriter = new MyCSVWriter(new File(path + File.separator + fileName), encoding);
				outputWriters.put(fileName, csvWriter);
			}
			catch (final UnsupportedEncodingException e)
			{
				LOG.info(e.getMessage());
			}
			catch (final FileNotFoundException e)
			{
				LOG.info(e.getMessage());
			}
		}
		return csvWriter;
	}

	/**
	 * Extended CSVWriter which keeps the given file in a link.
	 *
	 */
	protected static class MyCSVWriter extends CSVWriter
	{
		private final File file;

		/**
		 * Parameterized constructor
		 *
		 * @param file
		 * @param encoding
		 * @throws UnsupportedEncodingException
		 * @throws FileNotFoundException
		 */
		MyCSVWriter(final File file, final String encoding) throws UnsupportedEncodingException, FileNotFoundException
		{
			super(file, encoding);
			super.setCommentchar(commentchar);
			super.setFieldseparator(fieldseperator);
			super.setTextseparator(textseperator);
			this.file = file;
		}

		/**
		 * Getter for File
		 *
		 * @return the file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Delete File
		 *
		 * @return boolean
		 */
		public boolean deleteFile()
		{
			return this.file.delete();
		}

	}



	String getPaymentSubscriptionId(final AbstractOrderModel order)
	{
		boolean isIPGTransaction = false;
		String stringValue = "";

		if (null != order)
		{
			if (null != order.getPaymentMode() && PaymentModeEnum.CREDITCARD.getCode().equals(order.getPaymentMode().getCode()))
			{
				isIPGTransaction = true;
			}



			if (isIPGTransaction)
			{

				if (null != order.getPaymentInfo())
				{

					final PaymentInfoModel paymentInfo = order.getPaymentInfo();

					if (paymentInfo instanceof CreditCardPaymentInfoModel)
					{

						stringValue = ((CreditCardPaymentInfoModel) paymentInfo).getSubscriptionId();

					}

				}
			}



		}
		return stringValue;

	}

	protected OrderModel getOrderForCode(final String orderCode)
	{

		final DefaultGenericDao defaultGenericDao = new DefaultGenericDao(OrderModel._TYPECODE);
		defaultGenericDao.setFlexibleSearchService(flexibleSearchService);
		final List<OrderModel> orders = defaultGenericDao.find(Collections.singletonMap(OrderModel.CODE, orderCode));
		return orders.isEmpty() ? null : (OrderModel) orders.get(0);

	}



}
