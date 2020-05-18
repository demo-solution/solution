/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.constants;

/**
 * Global class for all Sparbmecat constants.
 *
 * @author rohan_c
 */
@SuppressWarnings("deprecation")
public final class SparbmecatConstants extends GeneratedSparbmecatConstants
{
	//Extension Name
	public static final String EXTENSIONNAME = "sparbmecat";

	//Spar classification System Name
	public static final String CLASSIFICATION_SYSTEM_ID = "SparClassification";

	//Field Name Value for Article number / Part Number
	public static final String PARTNUMBER_ATTR_ID = "GTIN";

	//Delimiters
	public static final String VALUE_DELIMITER = ",";
	public static final String LINE_DELIMITER = ". ";
	public static final String CSV_DELIMITER = ";";
	public static final String NEW_LINE_DELIMITER = "\n";
	public static final String CARRIAGE_RETURN_LINE_DELIMITER = "\r";
	public static final String SPACE = " ";
	public static final String EMPTY_SPACE = "";
	public static final String UNDERSCORE_DELIMETER = "_";
	public static final String HYPHEN_DELIMETER = " - ";

	// SPARBMECAT configurations in properties file
	public static final String COPY_PRODUCTCATEGORYFEATURE_TO_HOTFOLDER = "sparbmecat.copy.productcategoryfeature.to.hotfolder";
	public static final String COPY_MEDIA_TO_HOTFOLDER = "sparbmecat.copy.media.to.hotfolder";
	public static final String IMAGE_DOWNLOAD_RETRY = "sparbmecat.image.download.retry";
	public static final String IMAGES_PATH = "sparbmecat.images.path";
	public static final String IMAGES_PATH_VALUE = "\\sparinitialdata\\import\\sampledata\\productCatalogs\\sparProductCatalog\\images\\";
	public static final String IMAGE_DOWNLOAD = "sparbmecat.image.download";
	public static final String OVERRIDE_MEDIA_FILE = "sparbmecat.override.media.file";

	//Default value for impex column if the BB xml does'not have any value for the attribute
	public static final String EMPTY_VALUE_CSV = "<ignore>";

	//File target locations
	public static final String ZIPPED_CSV = "zippedCSVs";
	public static final String BRAND_BANK = "brandbank";
	public static final String TENANT_ID = "master";
	public static final String MIME = "mime";
	public static final String RESOURCES = "resources";
	public static final String ZIP_2_CSV = "bb2csv.zip";
	public static final String IMAGES_MEDIA_FOLDER = "images";

	//File Formats
	public static final String MEDIA_TYPE = ".zip";
	public static final String MIME_TYPE = "jpg";

	//Resolution Characters
	public static final String WIDTH = "W";
	public static final String HEIGHT = "H";
	public static final String CROSS = "x";

	//Location for base folder
	public static final String BASE_FOLDER = "acceleratorservices.batch.impex.basefolder";

	//Files
	public static final String MEDIA_LOG = "SparMediaURLError";
	public static final String STATISTICS_PROPERTIES = "statistics.properties";

	// Id that has an exception for which Main Element has to be taken as an attribute rather than it's sub-element
	public static final String NAME_TEXT_LOOKUPS_ID = "sparbmecat.nametextlookups.id";
	public static final String NAME_TEXT_LOOKUPS_ID_VALUE = "24";
	public static final String NAME_TEXT_LOOKUPS_ID_ATTRIBUTE = "Id";

	public static final String UPDATE_TYPE_VALUE = "AddOrUpdate";

	//Product Attribute configuration
	public static final String PRODUCT_ATTRIBUTE_HEADERS = "sparbmecat.productattribute.headers";
	public static final String PRODUCT_ATTRIBUTE_HEADERS_VALUE = "##CODE,PRODUCT TYPE,STORAGE TYPE,REGULATED PRODUCT NAME,ABOUT PRODUCT DESCRIPTION,INGREDIENT DESCRIPTION,MODEL NUMBER,DISCLAIMER,PICKING AND PACKAGING,BRAND,SUB BRAND";
	public static final String COPY_PRODUCTATTRIBUTE_TO_HOTFOLDER = "sparbmecat.copy.productattribute.to.hotfolder";

	/**
	 * CSV Class
	 *
	 * @author rohan_c
	 *
	 */
	public static final class SPAR_BME_CAT_2_CSV
	{
		public static final String PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME = "ProductMediaFormat";
		public static final String PRODUCT_MEDIAS_OBJECT_FILENAME = "ProductMedias";
		public static final String CATEGORY_FEATURE_OBJECT_FILENAME = "ProductCategoryFeatures";
		public static final String PRODUCT_ATTRIBUTES_OBJECT_FILENAME = "ProductAttributes";
		public static final String FILE_EXTENSION = ".csv";
		//This list holds the sequence to copy the files in Hot-Folder location for BrandBank
		public static String[] fileSequenceArray = new String[]
		{ PRODUCT_MEDIA_FORMAT_OBJECT_FILENAME, PRODUCT_MEDIAS_OBJECT_FILENAME, CATEGORY_FEATURE_OBJECT_FILENAME,
				PRODUCT_ATTRIBUTES_OBJECT_FILENAME, STATISTICS_PROPERTIES };

		/**
		 * CSV Headers for Product Media and Media Container
		 *
		 * @author rohan_c
		 *
		 */
		public static final class SPAR_PRODUCT_MIME_HEADERS
		{
			public static final String PRODUCT_MIME_HEADERS = "sparbmecat.productmime.headers";
			public static final String PRODUCT_MIME_HEADERS_VALUE = "ProductNumber,ImageName,Medias";
		}


		/**
		 * CSV Headers for Product Media Format
		 *
		 * @author rohan_c
		 *
		 */
		public static final class SPAR_PRODUCT_MIME_FORMAT_HEADERS
		{
			public static final String PRODUCT_MIME_FORMAT_HEADERS = "sparbmecat.productmimeformat.headers";
			public static final String PRODUCT_MIME_FORMAT_HEADERS_VALUE = "MediaFormat,MediaCode,URL,ImageName";
		}

		/**
		 * CSV Headers for Category Feature
		 *
		 * @author rohan_c
		 *
		 */
		public static final class SPAR_CATEGORY_FEATURE_HEADER
		{
			public static final String CATEGORY_FEATURE_HEADERS = "sparbmecat.productcategoryfeature.headers";
			public static final String CATEGORY_FEATURE_HEADERS_VALUE = "##CODE,FUNCTIONAL NAME,VARIANT,FURTHER DESCRPTION,DESCRIPTION,REGIONAL NAME,PRODUCT MARKETING,BRAND MARKETING,MANUFACTURER MARKETING,COMPANY NAME,COMPANY ADDRESS,TELEPHONE HELPLINE,FAX NUMBER,EMAIL HELPLINE,WEB ADDRESS,MANUFACTURERS ADDRESS,IMPORTER ADDRESS,DISTRIBUTOR ADDRESS,RETURN TO,NUMERIC SIZE,DRAINED WEIGHT,NUMBER OF UNITS,UNIT TYPE,WEIGHT,OTHER INFORMATION,NUMBER OF USES,USAGE OTHER TEXT,OCCASION,NEW ARRIVALS,BHA/BHT (ANTIOXIDANTS),AZO COLOURS,MSG (GLUTAMATE),BENZOATE,ADDITIVES,ASPARTAME,YEAST,GENETICALLY MODIFIED INGREDIENTS,ARTIFICIAL COLOURS,ARTIFICIAL SWEETENERS,ARTIFICIAL FLAVOURS,ARTIFICIAL PRESERVATIVES,TARTRAZINE,HVP (HYDROLISED VEGETABLE PROTEIN),SUCROSE,GM PROTEIN/DNA,ARTIFICIAL ANTIOXIDANTS,FLAVOUR ENHANCERS,PRESERVATIVES,BRAND DESCRIPTION,BENEFIT DESCRIPTION,LOWER AGE LIMIT,UPPER AGE LIMIT,ALLERGEN TAG FORMAT,MIN TEMP °C,MAX TEMP °C,MIN HUMIDITY %,MAX HUMIDITY %,OVEN COOK,MICROWAVE,GRILL,BARBECUE,SHALLOW FRY,DEEP FRY,STIR FRY,STEAM,POACH,HOB,BOIL IN THE BAG,OTHER,COOKING INSTRUCTIONS,COUNTRY OF ORIGIN,PACKED IN,PLACE OF PROVENANCE,FISH CATCH AREA,ORIGIN FREE TEXT,SOURCED (WATER),HARVEST TIME,AVAILABILITY PERIOD,LEAD TIME FROM FARM,PEAK SEASON,AGING,LOOSE,BUSINESS,DIVISION,MARKET,CATEGORY,FAMILY,ONLINE BUSINESS,ONLINE MARKET,ONLINE FAMILY,TITLE TAG,META DESCRIPTION,META KEYWORDS,PAGE URL,SEARCH TAG,CANONICAL TAG,HEADING TAG,MATERIAL DESCRIPTION,MATERIAL FREE TEXT,DESIGN,DESIGN DESCRIPTION,COLOUR,LOOK/PATTERN DESCRIPTION,STYLE,SHAPE,PACK SIZE,SCREEN SIZE,PRODUCT DIMENSIONS,BOX CONTENTS,TECHNOLOGY DESCRIPTION,TECHNICAL DETAILS DESCRIPTION,BENEFITS DESCRIPTION,FEATURES,UTILITY DESCRIPTION,LIFESTYLE,LIFESTYLE OTHER TEXT,SAFETY WARNING,ALLERGY OTHER TEXT,TAGGABLE ALLERGY TEXT,RECIPES,PREPARATION AND USAGE,COOKING INSTRUCTIONS / GUIDELINES,TYPE,STORAGE,WASH CARE DESCRIPTION,WARRANTY,HEADINGS,ENERGY (KJ),ENERGY (KCAL),PROTEIN (G),CARBOHYDRATE (G),OF WHICH SUGAR (G),OF WHICH POLYOLS (G),OF WHICH STARCH (G),FAT (G),OF WHICH SATURATED FAT (G),OF WHICH MONO-UNSATURATES (G),OF WHICH POLYUNSATURATES (G),OF WHICH CHOLESTEROL (MG),FIBRE (G),SODIUM (MG),VITAMIN A (µg),VITAMIN D (µg),VITAMIN E (MG),VITAMIN C (MG),THIAMIN (B1) (MG),RIBOFLAVIN (B2) (MG),NIACIN (MG),VITAMIN B6 (MG),FOLIC ACID (µg),VITAMIN B12 (µg),BIOTIN (MG),PANTOTHENIC ACID (MG),CALCIUM (MG),PHOSPHORUS (MG),IRON (MG),MAGNESIUM (MG),ZINC (MG),IODINE (µg),SALT (G),SALT EQUIVALENT (G),VITAMIN K (µg),BIOTIN (µg),POTASSIUM (MG),CHLORIDE (MG),COPPER (MG),MANGANESE (MG),FLUORIDE (MG),SELENIUM (µg),CHROMIUM (µg),MOLYBDENUM (µg),TRANS FAT (G),TAGGABLE INGREDIENTS,ADDITIVES OTHER TEXT";
		}
	}

	/**
	 * Constructor
	 */
	private SparbmecatConstants()
	{

	}

	/**
	 * XML Nodes and Attributes description
	 *
	 * @author rohan_c
	 *
	 */
	public static final class XML
	{
		/**
		 * XML Tag constants
		 *
		 */
		public static final class TAG
		{
			public static final String GET_UNSENT_PRODUCT_DATA_RESPONSE = "GetUnsentProductDataResponse";
			public static final String GET_UNSENT_PRODUCT_DATA_RESULT = "GetUnsentProductDataResult";
			public static final String MESSAGE = "Message";
			public static final String PRODUCT = "Product";
			public static final String IDENTITY = "Identity";
			public static final String PRODUCT_CODES = "ProductCodes";
			public static final String CODE = "Code";
			public static final String ASSETS = "Assets";
			public static final String IMAGE = "Image";
			public static final String SPECIFICATION = "Specification";
			public static final String THUMBPRINT = "Thumbprint";
			public static final String REQUESTED_DIMENSIONS = "RequestedDimensions";
			public static final String URL = "Url";
			public static final String WIDTH = "Width";
			public static final String HEIGHT = "Height";
			public static final String QUALITY = "Quality";
			public static final String RESOLUTION = "Resolution";
			public static final String ISCROPPED = "IsCropped";
			public static final String CROP_PADDING = "CropPadding";
			public static final String MAX_SIZE_IN_BYTES = "MaxSizeInBytes";
			public static final String DATA = "Data";
			public static final String LANGUAGE = "Language";
			public static final String DESCRIPTION = "Description";
			public static final String CATEGORISATIONS = "Categorisations";
			public static final String CATEGORISATION = "Categorisation";
			public static final String LEVEL = "Level";
			public static final String ITEM_TYPE_GROUP = "ItemTypeGroup";
			public static final String STATEMENTS = "Statements";
			public static final String STATEMENT = "Statement";
			public static final String MEMO = "Memo";
			public static final String NAME_LOOKUPS = "NameLookups";
			public static final String NAME_TEXT_LOOKUPS = "NameTextLookups";
			public static final String NAME_TEXT_ITEMS = "NameTextItems";
			public static final String LONG_TEXT_ITEMS = "LongTextItems";


			public static final String TEXTUAL_NUTRITION = "TextualNutrition";
			public static final String NUMERIC_NUTRITION = "NumericNutrition";
			public static final String FRONT_OF_PACK_GDA = "FrontOfPackGDA";


			public static final String NAME_VALUE = "NameValue";
			public static final String NAME_TEXT = "NameText";
			public static final String NAME_VALUE_TEXT = "NameValueText";
			public static final String NAME = "Name";
			public static final String VALUE = "Value";
			public static final String TEXT = "Text";
			public static final String PER_100_UNIT = "Per100Unit";
			public static final String PER_100_HEADING = "Per100Heading";
			public static final String PER_SERVING_HEADING = "PerServingHeading";
			public static final String NUTRIENT_VALUES = "NutrientValues";
			public static final String PER_100 = "Per100";
			public static final String PER_SERVING = "PerServing";
			public static final String HEADINGS = "Headings";
			public static final String HEADING = "Heading";
			public static final String NUTRIENT = "Nutrient";
			public static final String VALUES = "Values";
			public static final String REFRENCE = "Reference";
			public static final String HEADERS = "Headers";
			public static final String HEADER = "Header";
			public static final String LOZENGES = "Lozenges";
			public static final String LOZENGE = "Lozenge";
			public static final String QUANTITIES = "Quantities";
			public static final String QUANTITY = "Quantity";
			public static final String UNIT = "Unit";
			public static final String PERCENTAGE = "Percentage";
			public static final String FOOTERS = "Footers";
			public static final String FOOTER = "Footer";
		}

		/**
		 * XML Attribute constants
		 *
		 */
		public static final class ATTRIBUTE
		{
			/**
			 * Attribute for Message element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class MESSAGE
			{
				public static final String DATE_TIME = "DateTime";
				public static final String DATE_VERSION = "DataVersion";
				public static final String ID = "Id";
			}

			/**
			 * Attribute for Image element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class IMAGE
			{
				public static final String MIME_TYPE = "MimeType";
			}

			/**
			 * Attribute for requestedDimensions element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class REQUESTED_DIMENSIONS
			{
				public static final String UNITS = "Units";
			}

			/**
			 * Attribute for Statements element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class STATEMENTS
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for Code element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class CODE
			{
				public static final String SCHEME = "Scheme";
			}

			/**
			 * Attribute for Memo element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class MEMO
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for NameLookUps element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class NAME_LOOKUPS
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for NameText element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class NAME_TEXT
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for NameTextLookUps element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class NAME_TEXT_LOOKUPS
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for LongItemText element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class LONG_TEXT_ITEMS
			{
				public static final String NAME = "Name";
			}

			/**
			 * Attribute for LongItemText element
			 *
			 * @author rohan_c
			 *
			 */
			public static final class PRODUCT
			{
				public static final String UPDATE_TYPE = "UpdateType";
			}
		}
	}
}
