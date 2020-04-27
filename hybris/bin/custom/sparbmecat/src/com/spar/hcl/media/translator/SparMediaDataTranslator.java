/**
 *
 */
package com.spar.hcl.media.translator;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.media.MediaDataTranslator;
import de.hybris.platform.jalo.Item;

import com.spar.hcl.utils.SparBMECatUtils;


/**
 * This Class is extended from MediaDataTranslator to override few functionalities to download the MIME to specific SPAR
 * location.
 * 
 * @author rohan_c
 * 
 */
public class SparMediaDataTranslator extends MediaDataTranslator
{
	/*
	 * This method is overridden to download the product image coming from BB
	 * 
	 * @see de.hybris.platform.impex.jalo.media.MediaDataTranslator#performImport(java.lang.String,
	 * de.hybris.platform.jalo.Item)
	 */
	@Override
	public void performImport(final String cellValue, final Item processedItem) throws ImpExException
	{
		final String value = "file:" + SparBMECatUtils.saveImage(cellValue);
		super.performImport(value, processedItem);
	}

}
