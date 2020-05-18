/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
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
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * @author valechar
 *
 */
public class SparExportProductsJob extends AbstractJobPerformable
{

	private static final Logger LOG = Logger.getLogger(SparExportProductsJob.class);

	FlexibleSearchService flexibleSearchService;
	CatalogVersionService catalogVersionService;

	private static final String QUERY_FOR_BASE_STORE = "select {bs.PK} from {BaseStore as bs} where {bs.uid}='spar'";


	private static final String QUERY_FOR_PRODUCTS = "select {pos.name},{c3.code},{c1.code}, {cat.code}, {p.code}, {p.description}, {p.name}, {pr.unitMRP}, {pr.price}, {sl.available},{pr.checkForPromotion},{p.modifiedtime},{p.picture}  from "
			+ " {Product as p join CategoryProductRelation as l on {l.target}={p.PK} join CategoryCategoryRelation as c on {c.target}={l.source} "
			+ " join Category as cat on {cat.pk}={c.target}  join  Category as c1 on {c1.pk}={c.source} join CategoryCategoryRelation as c2 on {c2.target}={c1.pk} "
			+ " JOIN Category as c3 on {c2.source}={c3.PK} JOIN SparPriceRow as pr on {pr.productid}={p.code} "
			+ " JOIN STOCKLEVEL as sl on {sl.productCode}={p.code} JOIN PointOfService as pos ON {pos.baseStore}=?basestore "
			+ " JOIN PoS2WarehouseRel as p2w ON {p2w.source}={pos.pk} AND {p2w.target} = {sl.warehouse}} where {p.catalogversion}=?cv and {c3.catalogversion}<>?ccvm";

	// CSVWriter settings
	private static char commentchar = CSVConstants.DEFAULT_COMMENT_CHAR;
	private static char fieldseperator = ',';
	private static char textseperator = CSVConstants.DEFAULT_QUOTE_CHARACTER;

	// stores all CSVWriter, output map
	private Map<String, MyCSVWriter> outputWriters;

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	public static final int SPAR_PRODUCTS_HEADER_COUNT = 13;
	public static final String SPAR_PRODUCTS_HEADER = "SPAR_PRODUCTS_HEADER_";
	public static final String SPAR_PRODUCTS_OBJECT_FILENAME = "SparProducts";

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
			final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("sparProductCatalog", "Online");
			final CatalogVersionModel classificationCVM = catalogVersionService.getCatalogVersion("SparClassification", "1.0");
			final FlexibleSearchQuery queryForBaseStore = new FlexibleSearchQuery(QUERY_FOR_BASE_STORE);
			final SearchResult<BaseStoreModel> resultForBaseStore = flexibleSearchService.search(queryForBaseStore);
			final String baseStore = resultForBaseStore.getResult().get(0).getPk().toString();

			final FlexibleSearchQuery queryForProducts = new FlexibleSearchQuery(QUERY_FOR_PRODUCTS);
			queryForProducts.addQueryParameter("basestore", baseStore);
			queryForProducts.addQueryParameter("cv", catalogVersionModel);
			queryForProducts.addQueryParameter("ccvm", classificationCVM);
			queryForProducts.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, Double.class, String.class, String.class, Date.class, String.class));
			final SearchResult<List<?>> resultForProducts = flexibleSearchService.search(queryForProducts);
			final List<ArrayList> list1 = (List) ModelUtils.getFieldValue(resultForProducts, "resultList");	
			getOut(SPAR_PRODUCTS_OBJECT_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION).write(
					addSparProductsComment());

			for (final ArrayList line : list1)
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
				getOut(SPAR_PRODUCTS_OBJECT_FILENAME + UNDERSCORE_DELIMETER + dateFormat.format(date) + FILE_EXTENSION)
						.write(mapLine);
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
		{ "##Location", "Hierarchy-1", "Hierarchy-2", "Hierarchy-3", "SKU Code", "RMS Description", "Online Description", "MRP",
				"CSP", "SOH", "Promo Check", "Date & Time of last update", "Photography" };
		for (int i = 0; i < SPAR_PRODUCTS_HEADER_COUNT; i++)
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
