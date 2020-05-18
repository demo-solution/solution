package com.spar.hcl.utils;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.initialdata.constants.SparInitialDataConstants;
import com.spar.hcl.logger.SparLogger;
import com.spar.hcl.parser.SparProductCategoryFeature;


/**
 * This is a Util Class for SPAR BMECat
 *
 * @author rohan_c
 *
 */
final public class SparBMECatUtils
{
	@Resource
	private static ClassificationSystemService classificationSystemService;
	/** This is map used for holding CategoryFeature **/
	private static Map<String, String> classAttributeMap;
	/** This is map used for holding Product Attributes **/
	private static List<String> productAttributesList;
	/** This is map used for holding additional attributes coming from BrandBank **/
	public static List<String> additionalCategoryFeatures = new ArrayList<String>();
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(SparBMECatUtils.class.getName());

	/**
	 * Private Constructor
	 */
	private SparBMECatUtils()
	{
	}

	/**
	 * This method retrieves all the attributes in the classification system of SPAR and returns a HashMap out of it.
	 *
	 * @return HashMap<String, String>
	 */
	private static Map<String, String> createClassAttributeMap()
	{
		classAttributeMap = new HashMap<String, String>();

		//Retrieving the Classification System Service
		classificationSystemService = (ClassificationSystemService) Registry.getApplicationContext().getBean(
				"classificationSystemService");
		//Retrieving the latest Version
		final ClassificationSystemModel classificationSystem = classificationSystemService
				.getSystemForId(SparbmecatConstants.CLASSIFICATION_SYSTEM_ID);
		final Set<CatalogVersionModel> versions = classificationSystem.getCatalogVersions();

		for (final CatalogVersionModel versionModel : versions)
		{

			if (null != versionModel && versionModel.getActive().booleanValue())
			{
				final ClassificationSystemVersionModel version = classificationSystemService.getSystemVersion(
						SparbmecatConstants.CLASSIFICATION_SYSTEM_ID, versionModel.getVersion());
				//retrieve all root ClassificationSystemVersionModels classes
				final Collection<ClassificationClassModel> rootClasses = classificationSystemService
						.getRootClassesForSystemVersion(version);

				for (final ClassificationClassModel rootClass : rootClasses)
				{
					final List<ClassAttributeAssignmentModel> classAttributes = rootClass.getAllClassificationAttributeAssignments();
					for (final ClassAttributeAssignmentModel attribute : classAttributes)
					{
						final ClassificationAttributeModel attributeModel = ((attribute.getClassificationAttribute()));
						if (null != attributeModel.getName())
						{

							classAttributeMap.put(attributeModel.getName().toUpperCase(), rootClass.getName());
						}
					}
				}
			}
		}
		return classAttributeMap;
	}

	/**
	 * Getter for classAttributeMap
	 *
	 * @return the classAttributeMap
	 */
	public static Map<String, String> getClassAttributeMap()
	{
		if (null == classAttributeMap || classAttributeMap.isEmpty())
		{
			classAttributeMap = createClassAttributeMap();
		}
		return classAttributeMap;
	}

	/**
	 * This method retrieves the list of product attributes supported by SPAR
	 *
	 * @param attributeName
	 * @return boolean
	 */
	public static boolean isProductAttributes(final String attributeName)
	{
		return getProductAttributesList().contains(attributeName);
	}

	/**
	 * This method loads the Product Attribute List
	 */
	private static void loadProductAttributesList()
	{
		final String headers = Config.getString(SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS,
				SparbmecatConstants.PRODUCT_ATTRIBUTE_HEADERS_VALUE);
		final String[] headerValues = headers.split(SparbmecatConstants.VALUE_DELIMITER);
		if (null != headerValues && headerValues.length != 0)
		{
			productAttributesList = Arrays.asList(headerValues);
		}
		else
		{
			productAttributesList = Collections.emptyList();
		}
	}

	/**
	 * This method is used to parse the value of the element depending on its type (String/ArrayList)
	 *
	 * @param value
	 * @return String value
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String processSubElement(final Object value, final String delimeter)
	{
		String val = null;

		if (value instanceof String)
		{
			val = (String) value;
			val = val.replace(SparbmecatConstants.CARRIAGE_RETURN_LINE_DELIMITER + SparbmecatConstants.NEW_LINE_DELIMITER,
					SparbmecatConstants.EMPTY_SPACE).replace(SparbmecatConstants.NEW_LINE_DELIMITER, SparbmecatConstants.EMPTY_SPACE);
		}
		else if (value instanceof ArrayList)
		{
			val = String.join(delimeter, (ArrayList) value);
			val = val.replace(SparbmecatConstants.CARRIAGE_RETURN_LINE_DELIMITER + SparbmecatConstants.NEW_LINE_DELIMITER,
					SparbmecatConstants.LINE_DELIMITER).replace(SparbmecatConstants.NEW_LINE_DELIMITER,
					SparbmecatConstants.LINE_DELIMITER);
		}

		if (null == val)
		{
			val = "";
		}

		if (val.contains(SparbmecatConstants.CSV_DELIMITER))
		{
			val = val.replace(';', ':');
		}

		return val;
	}

	/**
	 * This method is used to process Category Feature according to the SPAR Classification System
	 *
	 * @param categoryFeature
	 * @param attributeName
	 * @param value
	 */
	public static void processCategoryFeature(final SparProductCategoryFeature categoryFeature, final String attributeName,
			final Object value)
	{
		processCategoryFeature(categoryFeature, attributeName, value, null);
	}

	/**
	 * This method is used to process Category Feature according to the SPAR Classification System
	 *
	 * @param categoryFeature
	 * @param attributeName
	 * @param value
	 * @param delimeter
	 */
	public static void processCategoryFeature(final SparProductCategoryFeature categoryFeature, final String attributeName,
			final Object value, final String delimeter)
	{
		if (null != attributeName)
		{
			final String attributeNameUC = attributeName.toUpperCase();
			// If an attribute is at productLevel then it is mapped to ProductAttribute.csv, otherwise it could be a category feature
			if (isProductAttributes(attributeNameUC))
			{
				// Product Attributes
				categoryFeature.getProductAttributesMap().put(attributeNameUC, processSubElement(value, delimeter));
			}
			else
			{
				if (SparBMECatUtils.getClassAttributeMap().containsKey(attributeNameUC))
				{
					//if attribute is a category feature in SPAR classification system, the keep it for productCategoryFeatures.csv
					categoryFeature.getCategoryFeaturesMap().put(attributeNameUC, processSubElement(value, delimeter));
				}
				else if (!additionalCategoryFeatures.contains(attributeNameUC))
				{
					//Additional Attributes that are not consumed as part of classification system. These attributes are ignored.
					additionalCategoryFeatures.add(attributeNameUC);
				}
			}
		}
	}

	/**
	 * This method is used to process Category Feature according to the SPAR Classification System
	 *
	 * @param categoryFeature
	 * @param attributeName
	 * @param value1
	 * @param value2
	 * @param delimeter
	 */
	public static void processCategoryFeature(final SparProductCategoryFeature categoryFeature, final String attributeName,
			final Object value1, final Object value2, final String delimeter)
	{
		if (null != attributeName)
		{
			final String attributeNameUC = attributeName.toUpperCase();
			if (SparBMECatUtils.getClassAttributeMap().containsKey(attributeNameUC))
			{
				categoryFeature.getCategoryFeaturesMap().put(
						attributeNameUC,
						processSubElement(value2, delimeter) + SparbmecatConstants.HYPHEN_DELIMETER
								+ processSubElement(value1, delimeter));
			}
			else
			{
				if (!additionalCategoryFeatures.contains(attributeNameUC))
				{
					additionalCategoryFeatures.add(attributeNameUC);
				}
			}
		}
	}

	/**
	 * This methods takes the image URL for each product in BB xml and downloads it to the specified folder
	 *
	 * @param imageUrl
	 */
	public static String saveImage(final String imageUrl)
	{
		String file = "";

		file = splitUrl(imageUrl, file);

		return file;
	}

	/**
	 * This method is used to download the all images for a product depending on the concatenated URL and returns the
	 * correct fileName for the Media Impex Load
	 *
	 * @param url
	 * @param imageFile
	 * @return String
	 */
	public static String splitUrl(final String url, String imageFile)
	{
		final String urlParts[] = url.split(SparbmecatConstants.UNDERSCORE_DELIMETER);
		final String mimeUrl = urlParts[0];
		final String partNumber = urlParts[1];
		final String mediaformat = urlParts[2];
		final String imagePath = Config.getString(SparbmecatConstants.IMAGES_PATH, SparbmecatConstants.IMAGES_PATH_VALUE);
		final boolean isDownload = Config.getBoolean(SparbmecatConstants.IMAGE_DOWNLOAD, false);
		final boolean isRetryDownload = Config.getBoolean(SparbmecatConstants.IMAGE_DOWNLOAD_RETRY, false);
		// This is done to support Unix/Linux environment (Higher Environment)
		final String filePath = FilenameUtils.separatorsToUnix(Utilities.getExtensionInfo(SparInitialDataConstants.EXTENSIONNAME)
				.getExtensionDirectory() + File.separator + SparbmecatConstants.RESOURCES + imagePath + mediaformat);
		createFolder(filePath);
		//image name is same as partNumber
		final String fileName = partNumber + "." + SparbmecatConstants.MIME_TYPE;
		final String file = filePath + File.separator + fileName;
		try
		{
			if (isDownload)
			{
				downloadMime(mimeUrl, file);
			}
		}
		catch (final Exception exception)
		{
			try
			{
				//Retry
				if (isRetryDownload)
				{
					downloadMime(mimeUrl, file);
				}
			}
			catch (final IOException e)
			{
				//Log the exception in finally block
				LOG.info("IO Exception while reading " + mimeUrl + " " + e.getMessage());
			}
			catch (final Exception e)
			{
				//Log the exception in finally block
				LOG.info("Exception while reading " + mimeUrl + " " + e.getMessage());
			}
			finally
			{
				final String message = exception + " | Error in downloading Image having URL : " + mimeUrl
						+ " | for the partnumber: " + partNumber;
				SparLogger.logMessage(SparbmecatConstants.MEDIA_LOG, message);
			}

		}
		if (imageFile.isEmpty())
		{
			imageFile = file;
		}

		return imageFile;

	}

	/**
	 * This method is used to download the image
	 *
	 * @param mimeUrl
	 * @param file
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void downloadMime(final String mimeUrl, final String file) throws MalformedURLException, IOException
	{
		final File imageFile = new File(file);
		final boolean isOverRideMedia = Config.getBoolean(SparbmecatConstants.OVERRIDE_MEDIA_FILE, false);
		if (isOverRideMedia || !imageFile.exists())
		{
			BufferedImage image;
			final URL imageurl = new URL(mimeUrl);
			image = ImageIO.read(imageurl);

			ImageIO.write(image, SparbmecatConstants.MIME_TYPE, imageFile);
		}
	}

	/**
	 * This method creates a folder if it does not exist.
	 *
	 * @param filePath
	 */
	public static void createFolder(final String filePath)
	{
		final File file = new File(filePath);
		if (!file.exists())
		{
			file.mkdir();
		}
	}

	/**
	 * This method is used to wrap data by quotes.
	 *
	 * @param data
	 * @return String
	 */
	public static String wrapHeaderForCSV(final String data)
	{
		return String.format("\"%s\"", data);
	}

	/**
	 * Getter for productAttributesList
	 *
	 * @return the productAttributesList
	 *
	 */
	public static List<String> getProductAttributesList()
	{
		if (null == productAttributesList || productAttributesList.isEmpty())
		{
			loadProductAttributesList();
		}
		return productAttributesList;
	}

	/**
	 * Setter for productAttributesList
	 *
	 * @param productAttributesList
	 *           the productAttributesList to set
	 */
	public static void setProductAttributesList(final List<String> productAttributesList)
	{
		SparBMECatUtils.productAttributesList = productAttributesList;
	}

	/**
	 * Setter for classAttributeMap
	 *
	 * @param classAttributeMap
	 *           the classAttributeMap to set
	 */
	public static void setClassAttributeMap(final HashMap<String, String> classAttributeMap)
	{
		SparBMECatUtils.classAttributeMap = classAttributeMap;
	}


}
