/**
 *
 */
package com.spar.hcl.core.service.interceptor.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.LoadInterceptor;

import com.spar.hcl.core.helper.ContentFormatHelper;


/**
 * @author valechar
 *
 */
public class CMSParagraphMediaURLRewriteInterceptor implements LoadInterceptor
{

	/**
	 * Reference for content format helper
	 */
	private ContentFormatHelper contentFormatHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.LoadInterceptor#onLoad(java .lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onLoad(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		final CMSParagraphComponentModel paragraph = (CMSParagraphComponentModel) model;
		final CatalogVersionModel catalogVersion = paragraph.getCatalogVersion();
		if (catalogVersion.getActive().booleanValue())
		{
			paragraph.setContent(contentFormatHelper.replaceMediaPlaceHolder(paragraph.getContent(), paragraph.getCatalogVersion()));
		}
	}

	/**
	 * @param contentFormatHelper
	 *           the contentFormatHelper to set
	 */
	public void setContentFormatHelper(final ContentFormatHelper contentFormatHelper)
	{
		this.contentFormatHelper = contentFormatHelper;
	}



}
