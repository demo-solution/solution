package com.spar.hcl.jalo.bmecat2csv;

import de.hybris.bootstrap.xml.AbstractValueObject;
import de.hybris.bootstrap.xml.ParseAbortException;
import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.constants.BMECatConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.servicelayer.config.impl.DefaultConfigurationService;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVWriter;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.MediaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparBMECatObjectProcessor;
import com.spar.hcl.parser.SparMime;
import com.spar.hcl.parser.SparProductCategoryFeature;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * This Class is used to parse the BrandBank XML and processes each elements using the TagListners via Handlers
 *
 * @author rohan_c
 *
 */

/**
 * This Class is used to parse the BrandBank XML and processes each elements using the TagListners via Handlers
 *
 * @author rohan_c
 *
 */
public class SparBMECat2CSVObjectProcessor implements SparBMECatObjectProcessor
{
	private static final Logger LOG = Logger.getLogger(SparBMECat2CSVObjectProcessor.class);

	// stores all CSVWriter, output map
	private final Map<String, MyCSVWriter> outputWriters = new HashMap<String, MyCSVWriter>();

	// the return object, a zip file which contains all creates csv files
	private Media resultZipMedia = null;

	// for formating double values
	private final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	// number of transformed items of bmecat.xml
	private final Long ZERO_LONG = Long.valueOf(0);
	private Long mimeSize = ZERO_LONG;
	private Long productMediaFormatSize = ZERO_LONG;

	private int filesNumber = 0;
	private final StringBuilder buf = new StringBuilder();

	// CSVWriter settings
	private static char commentchar = CSVConstants.DEFAULT_COMMENT_CHAR;
	private static char fieldseperator = CSVConstants.DEFAULT_FIELD_SEPARATOR;
	private static char textseperator = CSVConstants.DEFAULT_QUOTE_CHARACTER;

	private Long categoryFeatureSize = ZERO_LONG;
	private Long productAttributeSize = ZERO_LONG;

	public String fileSuffix = "";
	public long startTime = 0;
	private String tempFolderPath = "";
	private String hotFolderPath = "";


	/**
	 * Extended CSVWriter which keeps the given file in a link.
	 *
	 * @author rohan_c
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

	/**
	 * Default constructor used to initialize the baseFolder Path for the import of CSV using Impex
	 */
	public SparBMECat2CSVObjectProcessor()
	{
		init();

	}

	/**
	 * This method is used to initialize instance variables at the start of BB xml parsing
	 */
	private void init()
	{
		// This is to reload the map before every BB load.
		SparBMECatUtils.setClassAttributeMap(null);
		SparBMECatUtils.setProductAttributesList(null);

		if (fileSuffix.isEmpty())
		{
			startTime = System.currentTimeMillis();
			final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			final Calendar cal = Calendar.getInstance();
			fileSuffix = dateFormat.format(cal.getTime());
		}
		decimalFormat.applyPattern(BMECatConstants.BMECat2CSV.NUMBERFORMAT);
		final String path = (String) Registry.getApplicationContext().getBean(DefaultConfigurationService.class).getConfiguration()
				.getProperty(SparbmecatConstants.BASE_FOLDER);
		hotFolderPath = FilenameUtils.separatorsToSystem(path + File.separator + SparbmecatConstants.TENANT_ID + File.separator
				+ SparbmecatConstants.BRAND_BANK);
		tempFolderPath = FilenameUtils.separatorsToSystem(hotFolderPath + File.separator + SparbmecatConstants.ZIPPED_CSV);
		SparBMECatUtils.createFolder(hotFolderPath);
		SparBMECatUtils.createFolder(tempFolderPath);
	}

	/**
	 * This method has to be called after {@link #process(TagListener, AbstractValueObject)}! It creates a zip file of
	 * the generated csv temp files and delete all temp files after that.
	 *
	 * @throws IOException
	 * @throws JaloBusinessException
	 */
	public void finish() throws IOException, JaloBusinessException
	{
		collectStatisticsInfo();
		editStatisticsFile();
		finish_process();
		deleteTempFiles();
		startTime = 0;
		fileSuffix = "";
	}

	/**
	 * This method collects the information of statistics of BB.xml
	 */
	private void collectStatisticsInfo()
	{

		if (mimeSize.longValue() > 0)
		{
			addFileEntry(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIAS_OBJECT_FILENAME, mimeSize,
					SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIAS_OBJECT_FILENAME + SparbmecatConstants.UNDERSCORE_DELIMETER
							+ fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION);
		}

		if (productMediaFormatSize.longValue() > 0)
		{
			addFileEntry(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME, productMediaFormatSize,
					SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME
							+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix
							+ SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION);
		}

		if (categoryFeatureSize.longValue() > 0)
		{
			addFileEntry(SparbmecatConstants.SPAR_BME_CAT_2_CSV.CATEGORY_FEATURE_OBJECT_FILENAME, categoryFeatureSize,
					SparbmecatConstants.SPAR_BME_CAT_2_CSV.CATEGORY_FEATURE_OBJECT_FILENAME + SparbmecatConstants.UNDERSCORE_DELIMETER
							+ fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION);
		}
		if (productAttributeSize.longValue() > 0)
		{
			addFileEntry(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_ATTRIBUTES_OBJECT_FILENAME, productAttributeSize,
					SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_ATTRIBUTES_OBJECT_FILENAME
							+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix
							+ SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION);
		}
	}

	/**
	 * This method add entries to the Statistics.properties.
	 *
	 * @param itemPrefix
	 * @param itemSize
	 * @param fileName
	 */
	private void addFileEntry(final String itemPrefix, final Long itemSize, final String fileName)
	{
		buf.append("file." + itemPrefix + ".name=\"" + fileName + "\"\r\n");
		buf.append("file." + itemPrefix + ".size=" + itemSize + "\r\n");
		this.filesNumber++;
	}

	/**
	 * This method edits the statistics.properties file for the number for CSV generated.
	 */
	private void editStatisticsFile()
	{
		buf.append("\r\nstatistics.file.size=" + this.filesNumber + "\r\n");
		final double timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
		buf.append("\r\nstatitstics.Time taken to parse the BB XML=" + (timeElapsed) + " secs \r\n");
		if (null != SparBMECatUtils.additionalCategoryFeatures && !SparBMECatUtils.additionalCategoryFeatures.isEmpty())
		{
			buf.append("\r\nstatistics.Additional Category features that are not in SPAR Classification for Category Features: \r\n"
					+ String.join(SparbmecatConstants.VALUE_DELIMITER + SparbmecatConstants.SPACE,
							SparBMECatUtils.additionalCategoryFeatures) + "\r\n");
		}
		try
		{
			getOut(SparbmecatConstants.STATISTICS_PROPERTIES).writeSrcLine(buf.toString());
		}
		catch (final IOException ioe)
		{
			LOG.info(ioe.getMessage());
		}

	}

	/**
	 * Deletes all temp files which are generated during {@link #process(TagListener, AbstractValueObject)}
	 *
	 * @throws IOException
	 */
	public void deleteTempFiles() throws IOException
	{
		for (final MyCSVWriter wr : outputWriters.values())
		{
			wr.close();
			wr.deleteFile();
		}
	}

	/**
	 * Creates the zip file but do not delete the generated temp files.
	 *
	 * @throws IOException
	 * @throws JaloBusinessException
	 */
	protected void finish_process() throws IOException, JaloBusinessException //NOPMD
	{
		if (resultZipMedia == null)
		{
			final File ziptempFile = File.createTempFile(fileSuffix, SparbmecatConstants.ZIP_2_CSV, new File(tempFolderPath));
			ZipOutputStream zos = null;
			try
			{
				zos = new ZipOutputStream(new FileOutputStream(ziptempFile));
				final ArrayList<Map.Entry<String, MyCSVWriter>> sortedCSVWriterList = new ArrayList<Map.Entry<String, MyCSVWriter>>();
				getSequencedFileList(sortedCSVWriterList);
				for (final Map.Entry<String, MyCSVWriter> entry : sortedCSVWriterList)
				{
					final MyCSVWriter csvWriter = entry.getValue();
					final String fileName = entry.getKey();
					csvWriter.close();

					//Copying the CSV to HotFolder location for BrandBank for Impex Processing
					if (canCopy(csvWriter))
					{
						copyCSVtoHotFolder(fileName, csvWriter);
					}

					final File csvTmp = csvWriter.getFile();

					final ZipEntry zipEntry = new ZipEntry(fileName);
					zos.putNextEntry(zipEntry);

					final FileInputStream fis = new FileInputStream(csvTmp);
					MediaUtil.copy(fis, zos, false);
					fis.close();
					zos.flush();
					zos.closeEntry();
				}
				// This is no more required. Earlier medias were zipped along with CSV.
				//	attachingMimeZipToBBZIP(zos);
				zos.close();
			}
			catch (final IOException e1)
			{
				LOG.info(e1.getMessage());
			}
			resultZipMedia = MediaManager.getInstance().createMedia("bb2csv_" + fileSuffix + ".zip");
			resultZipMedia.setFile(ziptempFile);
			if (resultZipMedia.getMime() == null)
			{
				resultZipMedia.setMime("application/zip");
			}
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	/**
	 * This method generates the sequence of list in which csv should be copied in hot-folder location.
	 *
	 * @param sequencedFileList
	 */
	private void getSequencedFileList(final ArrayList<Map.Entry<String, MyCSVWriter>> sequencedFileList)
	{
		for (int i = 0; i < SparbmecatConstants.SPAR_BME_CAT_2_CSV.fileSequenceArray.length; i++)
		{
			for (final Map.Entry<String, MyCSVWriter> entry : outputWriters.entrySet())
			{
				if (entry.getKey().startsWith(SparbmecatConstants.SPAR_BME_CAT_2_CSV.fileSequenceArray[i]))
				{
					sequencedFileList.add(entry);
				}
			}
		}
	}

	/**
	 * This method checks if a file can be copied to HotFolder depending on the configuration in
	 * SparbmecatConfiguration.properties
	 *
	 * @param csvWriter
	 * @return boolean
	 */
	private boolean canCopy(final MyCSVWriter csvWriter)
	{
		// Copy Product Category Feature CSV
		final boolean isCopyProdyctCategoryFeature = Config.getBoolean(
				SparbmecatConstants.COPY_PRODUCTCATEGORYFEATURE_TO_HOTFOLDER, false);
		// Copy Product Media CSV
		final boolean isCopyMedia = Config.getBoolean(SparbmecatConstants.COPY_MEDIA_TO_HOTFOLDER, false);
		// Copy Product Attribute CSV
		final boolean isCopyProductAttribute = Config.getBoolean(SparbmecatConstants.COPY_PRODUCTATTRIBUTE_TO_HOTFOLDER, false);

		return (csvWriter.getFile().getName().startsWith(SparbmecatConstants.SPAR_BME_CAT_2_CSV.CATEGORY_FEATURE_OBJECT_FILENAME) && isCopyProdyctCategoryFeature)
				|| (csvWriter.getFile().getName()
						.startsWith(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_ATTRIBUTES_OBJECT_FILENAME) && isCopyProductAttribute)
				|| (csvWriter.getFile().getName().startsWith(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIAS_OBJECT_FILENAME) && isCopyMedia)
				|| (csvWriter.getFile().getName()
						.startsWith(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME) && isCopyMedia);
	}

	/**
	 * This method attaches the MIME Zip to the BB CSV Zip file
	 *
	 * @param zos
	 * @throws JaloBusinessException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unused")
	private void attachingMimeZipToBBZIP(final ZipOutputStream zos) throws JaloBusinessException, IOException,
			FileNotFoundException
	{
		final Media zippedMedia = zipMimeFiles();
		if (zippedMedia != null)
		{
			final ZipEntry zipEntry = new ZipEntry(zippedMedia.getCode());
			zos.putNextEntry(zipEntry);
			final FileInputStream fis = new FileInputStream(zippedMedia.getFile());
			MediaUtil.copy(fis, zos, false);
			fis.close();
			zos.flush();
			zos.closeEntry();

		}
	}


	/**
	 * This method copies the specified CSV to the Hot Folder location
	 *
	 * @param fileName
	 * @param csvWriter
	 * @throws IOException
	 */
	private void copyCSVtoHotFolder(final String fileName, final MyCSVWriter csvWriter) throws IOException
	{

		final String path = hotFolderPath + File.separator + fileName;
		Files.copy(csvWriter.getFile().toPath(), (new File(path)).toPath(), StandardCopyOption.REPLACE_EXISTING);
	}


	/**
	 * This method creates a Zip file for all the CSVs created
	 *
	 * @return a media which hold a zip file which contains all generated csv files.
	 */
	public Media getResultZipMedia()
	{
		if (resultZipMedia == null)
		{
			throw new IllegalStateException();
		}
		else
		{
			return resultZipMedia;
		}
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
		MyCSVWriter csvWriter = outputWriters.get(fileName);
		if (csvWriter == null)
		{
			try
			{
				csvWriter = new MyCSVWriter(File.createTempFile(fileName, ".tmp", new File(tempFolderPath)), encoding);
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
			catch (final IOException e)
			{
				LOG.info(e.getMessage());
			}
		}
		return csvWriter;
	}

	/**
	 * This method processes the BB xml to generate respective CSVs
	 */
	@Override
	public void process(final TagListener listener, final AbstractValueObject obj) throws ParseAbortException
	{
		try
		{

			if (obj instanceof SparProductCategoryFeature)
			{
				final SparProductCategoryFeature productCategoryFeature = (SparProductCategoryFeature) obj;
				exportProductMediaFormat(productCategoryFeature);
				exportProductMedias(productCategoryFeature);
				exportProductClassificationSystem(productCategoryFeature);
				exportProductAttributes(productCategoryFeature);
			}
			else
			{
				LOG.info("unknown: Object to parse " + obj.getClass().getName());
			}
		}
		catch (final IOException e)
		{
			LOG.info("IOException occured: " + e.getMessage());
		}
		catch (final Exception e)
		{
			LOG.info("Exception occured: " + e.getMessage());
		}
	}

	/**
	 * This method writes the CSV
	 *
	 * @param fileName
	 * @param csvLine
	 */
	private void writeCsvLine(final String fileName, final Map<Integer, String> csvLine)
	{
		try
		{
			getOut(fileName).write(csvLine);
		}
		catch (final IOException ioe)
		{
			LOG.info(ioe.getMessage());
		}
	}

	/**
	 * This method exports the Media Container CSV
	 *
	 * @param productCategoryFeature
	 * @throws IOException
	 */
	private void exportProductMedias(final SparProductCategoryFeature productCategoryFeature) throws IOException
	{
		if (mimeSize.longValue() == 0)
		{
			addProductMediasComment();
		}

		final Map<Integer, String> mediamapline = new HashMap<Integer, String>();
		final String partNumber = productCategoryFeature.getPartNumber();
		//mimeName along with extension
		final String imageNameWithExtn = partNumber + "." + SparbmecatConstants.MIME_TYPE;
		//MediaCode
		mediamapline.put(Integer.valueOf(0), partNumber);
		mediamapline.put(Integer.valueOf(1), imageNameWithExtn);
		mediamapline.put(Integer.valueOf(2), productCategoryFeature.getMediaContainerValue());

		getOut(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIAS_OBJECT_FILENAME + SparbmecatConstants.UNDERSCORE_DELIMETER
						+ fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION).write(mediamapline);
		this.mimeSize = Long.valueOf(mimeSize.longValue() + 1);
	}


	/**
	 * This method exports the Product Media Format CSV
	 *
	 * @param productCategoryFeature
	 * @throws IOException
	 */
	private void exportProductMediaFormat(final SparProductCategoryFeature productCategoryFeature) throws IOException
	{
		for (final SparMime mime : productCategoryFeature.getMimeList())
		{
			if (productMediaFormatSize.longValue() == 0)
			{
				addProductMediaFormatComment();
			}

			final Map<Integer, String> productMediaFormatMapLine = new HashMap<Integer, String>();
			final String partNumber = productCategoryFeature.getPartNumber();
			final String mediaFormat = mime.getSpecification().getMediaFormat();
			//urlSuffix i.e partNumber & media format separated by underscore
			final String urlSuffix = partNumber + SparbmecatConstants.UNDERSCORE_DELIMETER + mediaFormat;
			//mimeName along with extension
			final String fileNameWithExtn = partNumber + "." + SparbmecatConstants.MIME_TYPE;
			//MediaCode
			String mediaCode = File.separator + mime.getSpecification().getMediaFormat() + File.separator + fileNameWithExtn;
			// This is done to support Unix/Linux environment (Higher Environment)
			mediaCode = FilenameUtils.separatorsToUnix(mediaCode);
			final String url = getMimeUrl(mime, urlSuffix);
			productMediaFormatMapLine.put(Integer.valueOf(0), mediaFormat);
			productMediaFormatMapLine.put(Integer.valueOf(1), mediaCode);
			productMediaFormatMapLine.put(Integer.valueOf(2), url);
			productMediaFormatMapLine.put(Integer.valueOf(3), fileNameWithExtn);


			getOut(
					SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME
							+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix
							+ SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION).write(productMediaFormatMapLine);
			this.productMediaFormatSize = Long.valueOf(productMediaFormatSize.longValue() + 1);
		}

	}


	/**
	 * This method returns a Image URL. For first Image, all the URLs for a product is concatenated and returned , so
	 * that the images for a product is loaded in one go while impex load using SparMediaDataTranslator.
	 *
	 * @param mime
	 * @param imageName
	 * @return String
	 */
	private String getMimeUrl(final SparMime mime, final String imageName)
	{
		String url = "";
		//URL coming from BB
		final String identificationURL = mime.getSource();
		url = identificationURL + SparbmecatConstants.UNDERSCORE_DELIMETER + imageName;


		return url;
	}

	/**
	 * This method creates the Media Container Header
	 */
	private void addProductMediasComment()
	{
		final Map<Integer, String> mimeLine = new HashMap<Integer, String>();
		final String headers = Config.getString(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_PRODUCT_MIME_HEADERS.PRODUCT_MIME_HEADERS,
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_PRODUCT_MIME_HEADERS.PRODUCT_MIME_HEADERS_VALUE);

		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 0; i < headerValues.length; i++)
		{
			mimeLine.put(Integer.valueOf(i), headerValues[i]);
		}

		writeCsvLine(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIAS_OBJECT_FILENAME
				+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION,
				mimeLine);
	}

	/**
	 * This method creates the Product Media Header Header
	 */
	private void addProductMediaFormatComment()
	{
		final Map<Integer, String> productMediaFormatLine = new HashMap<Integer, String>();
		final String headers = Config.getString(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_PRODUCT_MIME_FORMAT_HEADERS.PRODUCT_MIME_FORMAT_HEADERS,
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_PRODUCT_MIME_FORMAT_HEADERS.PRODUCT_MIME_FORMAT_HEADERS_VALUE);

		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 0; i < headerValues.length; i++)
		{
			productMediaFormatLine.put(Integer.valueOf(i), headerValues[i]);

		}

		writeCsvLine(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME
				+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION,
				productMediaFormatLine);
	}

	/**
	 * This method exports the Category Feature CSV
	 *
	 * @param categoryFeature
	 * @throws IOException
	 */
	private void exportProductClassificationSystem(final SparProductCategoryFeature categoryFeature) throws IOException
	{
		//creating CSV headers
		if (categoryFeatureSize.longValue() == 0)
		{
			writeCsvLine(SparbmecatConstants.SPAR_BME_CAT_2_CSV.CATEGORY_FEATURE_OBJECT_FILENAME
					+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION,
					addCategoryFeatureComment());
		}
		//Populating values for the headers
		final Map<Integer, String> categoryFeatureMapline = new HashMap<Integer, String>();
		populateCategoryFeatures(categoryFeature, categoryFeatureMapline);
		getOut(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.CATEGORY_FEATURE_OBJECT_FILENAME + SparbmecatConstants.UNDERSCORE_DELIMETER
						+ fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION).write(categoryFeatureMapline);
		this.categoryFeatureSize = Long.valueOf(categoryFeatureSize.longValue() + 1);
	}

	/**
	 * This method exports the ProductAttributes CSV
	 *
	 * @param categoryFeature
	 * @throws IOException
	 */
	private void exportProductAttributes(final SparProductCategoryFeature categoryFeature) throws IOException
	{
		//creating CSV headers
		if (productAttributeSize.longValue() == 0)
		{
			writeCsvLine(SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_ATTRIBUTES_OBJECT_FILENAME
					+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION,
					addProductAttributeComment());
		}
		//Populating values for the headers
		final Map<Integer, String> productAttributesMapLine = new HashMap<Integer, String>();
		populateProductAttributes(categoryFeature, productAttributesMapLine);
		getOut(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.PRODUCT_ATTRIBUTES_OBJECT_FILENAME + SparbmecatConstants.UNDERSCORE_DELIMETER
						+ fileSuffix + SparbmecatConstants.SPAR_BME_CAT_2_CSV.FILE_EXTENSION).write(productAttributesMapLine);
		this.productAttributeSize = Long.valueOf(productAttributeSize.longValue() + 1);
	}


	/**
	 * This method is used to populate the values corresponding to the CSV headers
	 *
	 * @param categoryFeature
	 * @param categoryFeatureMapline
	 */
	private void populateCategoryFeatures(final SparProductCategoryFeature categoryFeature,
			final Map<Integer, String> categoryFeatureMapline)
	{
		categoryFeatureMapline.put(Integer.valueOf(0), categoryFeature.getPartNumber());
		final String headers = Config.getString(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_CATEGORY_FEATURE_HEADER.CATEGORY_FEATURE_HEADERS,
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_CATEGORY_FEATURE_HEADER.CATEGORY_FEATURE_HEADERS_VALUE);
		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 1; i < headerValues.length; i++)
		{
			categoryFeatureMapline.put(Integer.valueOf(i), isValueEmpty(headerValues[i], categoryFeature.getCategoryFeaturesMap()));
		}
	}

	/**
	 * This method is used to populate the values corresponding to the CSV headers
	 *
	 * @param categoryFeature
	 * @param productAttributesMapLine
	 */
	private void populateProductAttributes(final SparProductCategoryFeature categoryFeature,
			final Map<Integer, String> productAttributesMapLine)
	{
		productAttributesMapLine.put(Integer.valueOf(0), categoryFeature.getPartNumber());
		final String headers = Config.getString(SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS,
				SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS_VALUE);

		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 1; i < headerValues.length; i++)
		{
			productAttributesMapLine.put(Integer.valueOf(i),
					isValueEmpty(headerValues[i], categoryFeature.getProductAttributesMap()));
		}
	}

	/**
	 * This method creates the Category Feature CSV Header
	 *
	 * @return Map<Integer, String>
	 */
	private Map<Integer, String> addCategoryFeatureComment()
	{
		final Map<Integer, String> categoryFeatureLine = new HashMap<Integer, String>();
		final String headers = Config.getString(
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_CATEGORY_FEATURE_HEADER.CATEGORY_FEATURE_HEADERS,
				SparbmecatConstants.SPAR_BME_CAT_2_CSV.SPAR_CATEGORY_FEATURE_HEADER.CATEGORY_FEATURE_HEADERS_VALUE);
		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 0; i < headerValues.length; i++)
		{
			categoryFeatureLine.put(Integer.valueOf(i), headerValues[i]);
		}
		return categoryFeatureLine;
	}

	/**
	 * This method creates the Product Attribute CSV Header
	 *
	 * @return Map<Integer, String>
	 */
	private Map<Integer, String> addProductAttributeComment()
	{
		final Map<Integer, String> categoryFeatureLine = new HashMap<Integer, String>();
		final String headers = Config.getString(SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS,
				SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS_VALUE);

		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		for (int i = 0; i < headerValues.length; i++)
		{
			categoryFeatureLine.put(Integer.valueOf(i), headerValues[i]);
		}
		return categoryFeatureLine;
	}

	/**
	 * This method checks if the value in the map is empty or not and then return the corresponding values.
	 *
	 * @param attribute
	 * @param categoryFeatureMap
	 * @return String
	 */
	private String isValueEmpty(final String attribute, final HashMap<String, String> categoryFeatureMap)
	{
		if (null != categoryFeatureMap && categoryFeatureMap.containsKey(attribute))
		{
			return categoryFeatureMap.get(attribute);
		}
		else
		{
			return SparbmecatConstants.EMPTY_VALUE_CSV;
		}
	}

	/**
	 * This method creates a zip file of the MIME downloaded for all the products coming in BB xml
	 *
	 * @return Media
	 * @throws JaloBusinessException
	 */
	private Media zipMimeFiles() throws JaloBusinessException
	{
		final String fileNamePath = tempFolderPath + File.separator + SparbmecatConstants.MIME
				+ SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix;
		File ziptempFile = null;
		ZipOutputStream zos = null;
		File folder = null;
		Media zippedMedia = null;
		try
		{
			folder = new File(fileNamePath);

			if (folder.isDirectory())
			{
				ziptempFile = File.createTempFile(fileSuffix, SparbmecatConstants.MIME + SparbmecatConstants.MEDIA_TYPE, new File(
						tempFolderPath));
				zos = new ZipOutputStream(new FileOutputStream(ziptempFile));
				if (folder.list().length != 0)
				{
					for (final String fileName : folder.list())
					{
						final ZipEntry zipEntry = new ZipEntry(fileName);
						zos.putNextEntry(zipEntry);
						final FileInputStream fis = new FileInputStream(fileNamePath + File.separator + fileName);
						MediaUtil.copy(fis, zos, false);
						fis.close();
						zos.flush();
						zos.closeEntry();
					}

					zos.close();
					zippedMedia = MediaManager.getInstance().createMedia(
							SparbmecatConstants.MIME + SparbmecatConstants.UNDERSCORE_DELIMETER + fileSuffix
									+ SparbmecatConstants.MEDIA_TYPE);
					zippedMedia.setFile(ziptempFile);
					if (zippedMedia.getMime() == null)
					{
						zippedMedia.setMime("application/zip");
					}

					for (final File file : folder.listFiles())
					{
						file.delete();
					}
				}
				folder.delete();
				ziptempFile.delete();
			}
		}
		catch (final IOException e1)
		{
			LOG.info(e1.getMessage());
		}
		return zippedMedia;

	}

	/**
	 * @return the tempFolderPath
	 */
	public String getTempFolderPath()
	{
		return tempFolderPath;
	}

}
