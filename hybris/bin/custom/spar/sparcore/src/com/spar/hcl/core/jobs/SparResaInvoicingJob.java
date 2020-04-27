/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.internal.converter.util.ModelUtils;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 *
 *
 */
public class SparResaInvoicingJob extends AbstractJobPerformable
{

	private static final Logger LOG = Logger.getLogger(SparResaInvoicingJob.class);

	FlexibleSearchService flexibleSearchService;
	CatalogVersionService catalogVersionService;

	//private static final String QUERY_FOR_BASE_STORE = "select {bs.PK} from {BaseStore as bs} where {bs.uid}='spar'";


	private static final String QUERY_FOR_PRODUCTS = "select distinct {order.deliveryDate},{order.deliverySlot},{wh.code},{order.date},{cs.code},{ps.code},{order.code},"
			+ "{order.deliveryDate},{entry.basePrice},{cust.uid}"
			+ " from {OrderEntry AS entry  JOIN Order AS order ON {entry.order}={order.PK} join Consignment as cons on {cons.order}={order.pk}"
			+ " join Warehouse as wh on {cons.warehouse}={wh.pk} JOIN  DeliverySlot2WarehouseRel as dsw  on {dsw.source}={wh.pk} "
			+ " join DeliverySlot as ds on {dsw.target}={ds.pk}  join  ConsignmentStatus as cs  on {cons.status}={cs.pk}"
			+ " join PaymentStatus as ps on {order.paymentStatus}={ps.pk} join Product as prod on {prod.pk}={entry.product} "
			+ "join Customer as cust on {order.user}={cust.pk} join Address as addr on {cust.pk}={addr.owner}  }  where {cs.code}='READY_FOR_SHIPPING' ";

	// CSVWriter settings
	private static char commentchar = CSVConstants.DEFAULT_COMMENT_CHAR;
	private static char fieldseperator = ';';
	private static char textseperator = CSVConstants.DEFAULT_QUOTE_CHARACTER;

	// stores all CSVWriter, output map
	private Map<String, MyCSVWriter> outputWriters;

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	public static final int SPAR_PRODUCTS_HEADER_COUNT = 25;
	public static final String SPAR_PRODUCTS_HEADER = "SPAR_PRODUCTS_HEADER_";
	public static final String SPAR_PRODUCTS_OBJECT_FILENAME = "SparInvoice";

	//Location for base folder
	public static final String BASE_FOLDER = "acceleratorservices.export.basefolder";

	//Delimiters
	public static final String UNDERSCORE_DELIMETER = "_";
	public static final String FILE_EXTENSION = ".csv";

	Date date;
	SimpleDateFormat dateFormat;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		date = new Date();
		outputWriters = new HashMap<String, MyCSVWriter>();
		dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try
		{
			final FlexibleSearchQuery queryForProducts = new FlexibleSearchQuery(QUERY_FOR_PRODUCTS);
			queryForProducts.setResultClassList(Arrays.asList(String.class, String.class, String.class, Date.class, String.class,
					String.class, String.class, String.class, Double.class, String.class));
			final SearchResult<List<?>> resultForProducts = flexibleSearchService.search(queryForProducts);
			final List<ArrayList> list1 = (List) ModelUtils.getFieldValue(resultForProducts, "resultList");
			getOut(SPAR_PRODUCTS_OBJECT_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).write(
					addSparProductsComment());

			for (final ArrayList line : list1)
			{
				final Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
				int i = 2;
				final String splitDate[] = line.get(1).toString().split("To");
				final String deliveryDate = (((String) line.get(0)).trim() + " " + splitDate[0].trim()).trim();
				final Calendar todayDate = Calendar.getInstance();
				todayDate.set(Calendar.MILLISECOND, 0);
				todayDate.set(Calendar.SECOND, 0);
				todayDate.set(Calendar.MINUTE, 0);
				//todayDate.add(
				////		Calendar.HOUR_OF_DAY,
				//		Integer.valueOf(
				//				((SparResaInvoicingJobModel) arg0).getOffsetHours() != null ? ((SparResaInvoicingJobModel) arg0)
				//						.getOffsetHours() : "2").intValue());
				final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm aaa");
				final Date parsedDeliveryDate = sqlDateFormat.parse(deliveryDate);
				final Calendar orderDate = Calendar.getInstance();
				orderDate.setTime(parsedDeliveryDate);
				orderDate.set(Calendar.MINUTE, 0);
				if (todayDate.equals(orderDate))
				{
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
					getOut(SPAR_PRODUCTS_OBJECT_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).write(
							mapLine);
				}
			}
			getOut(SPAR_PRODUCTS_OBJECT_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).close();
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Product Export", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
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
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	private Map<Integer, String> addSparProductsComment()
	{
		final Map<Integer, String> mapHeaderLine = new HashMap<Integer, String>();
		final String[] sparProductsLine = new String[]
		{ "###STORE,BUSINESS_DATE,TRANSACTION_DATETIME,CUST_ORDER_NO,CUST_ORDER_DATE,ITEM,QTY,"
				+ "UNIT_RETAIL,MRP,PROMO_TYPE,DISCOUNT_AMT,CUST_ID,NAME,ADDR1,ADDR2,CITY,POSTAL_CODE," + "HOME_PHONE,BIRTHDATE" };
		//for (int i = 0; i < SPAR_PRODUCTS_HEADER_COUNT; i++)
		//{
		mapHeaderLine.put(Integer.valueOf(0), sparProductsLine[0]);
		//}
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

		final String path = (String) Registry.getApplicationContext().getBean(DefaultConfigurationService.class).getConfiguration()
				.getProperty(BASE_FOLDER);
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
}
