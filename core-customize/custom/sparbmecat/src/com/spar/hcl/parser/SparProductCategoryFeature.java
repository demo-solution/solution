package com.spar.hcl.parser;

import de.hybris.bootstrap.xml.AbstractValueObject;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * This class is used to contain all the Category Features in a Classification System for a product
 *
 * @author rohan_c
 *
 */
public class SparProductCategoryFeature extends AbstractValueObject
{
	/**
	 * This variable will hold all the Category Feature in the BB Xml
	 */
	private final HashMap<String, String> categoryFeaturesMap = new HashMap<String, String>();

	/**
	 * This variable will hold all the Product Attributes in the BB Xml
	 */
	private HashMap<String, String> productAttributesMap = new HashMap<String, String>();

	/**
	 * This attribute will hold the Product Code / PartNumber of the SKU
	 */
	private String partNumber = "";

	/**
	 * Products Description
	 */
	private String description = "";

	/**
	 * Category Level 1
	 */
	private String categoryLevel1 = "";

	/**
	 * Category Level 2
	 */
	private String categoryLevel2 = "";

	/**
	 * Category Level 3
	 */
	private String categoryLevel3 = "";

	/**
	 * This attribute will hold the MIME details of the Product
	 */
	private ArrayList<SparMime> mimeList = new ArrayList<SparMime>();

	/**
	 * Media container for all the images for a product
	 */
	private String mediaContainerValue = "";


	/**
	 * Get all the Category Feature of a Product
	 *
	 * @return the categoryFeaturesMap
	 */
	public HashMap<String, String> getCategoryFeaturesMap()
	{
		return categoryFeaturesMap;
	}

	/**
	 * Get the Product Code/Part Number
	 *
	 * @return the partNumber
	 */
	public String getPartNumber()
	{
		return partNumber;
	}

	/**
	 * Set the Product Code/Part Number
	 *
	 * @param partNumber
	 *           the partNumber to set
	 */
	public void setPartNumber(final String partNumber)
	{
		this.partNumber = partNumber;
	}


	/**
	 * Getter
	 *
	 * @return the mimeList
	 */
	public ArrayList<SparMime> getMimeList()
	{
		return mimeList;
	}

	/**
	 * Setter
	 *
	 * @param mimeList
	 *           the mimeList to set
	 */
	public void setMimeList(final ArrayList<SparMime> mimeList)
	{
		this.mimeList = mimeList;
	}

	/**
	 * Getter for Media Container Vale
	 *
	 * @return the mediaContainers
	 */
	public String getMediaContainerValue()
	{
		return mediaContainerValue;
	}

	/**
	 * Setter for Media Container Value
	 *
	 * @param mediaContainers
	 *           the mediaContainers to set
	 */
	public void setMediaContainerValue(final String mediaContainers)
	{
		this.mediaContainerValue = mediaContainers;
	}

	/**
	 * Getter for Product Desription
	 *
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Setter for Product Desription
	 *
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * Getter for Category Level 1
	 *
	 * @return the categoryLevel1
	 */
	public String getCategoryLevel1()
	{
		return categoryLevel1;
	}

	/**
	 * Setter for Category Level 1
	 *
	 * @param categoryLevel1
	 *           the categoryLevel1 to set
	 */
	public void setCategoryLevel1(final String categoryLevel1)
	{
		this.categoryLevel1 = categoryLevel1;
	}

	/**
	 * Getter for Category Level 2
	 *
	 * @return the categoryLevel2
	 */
	public String getCategoryLevel2()
	{
		return categoryLevel2;
	}

	/**
	 * Setter for Category Level 2
	 *
	 * @param categoryLevel2
	 *           the categoryLevel2 to set
	 */
	public void setCategoryLevel2(final String categoryLevel2)
	{
		this.categoryLevel2 = categoryLevel2;
	}

	/**
	 * Getter for Category Level 3
	 *
	 * @return the categoryLevel3
	 */
	public String getCategoryLevel3()
	{
		return categoryLevel3;
	}

	/**
	 * Setter for Category Level 3
	 *
	 * @param categoryLevel3
	 *           the categoryLevel3 to set
	 */
	public void setCategoryLevel3(final String categoryLevel3)
	{
		this.categoryLevel3 = categoryLevel3;
	}

	/**
	 * Getter
	 * 
	 * @return the productAttributesMap
	 */
	public HashMap<String, String> getProductAttributesMap()
	{
		return productAttributesMap;
	}

	/**
	 * Setter
	 * 
	 * @param productAttributesMap
	 *           the productAttributesMap to set
	 */
	public void setProductAttributesMap(final HashMap<String, String> productAttributesMap)
	{
		this.productAttributesMap = productAttributesMap;
	}



}
