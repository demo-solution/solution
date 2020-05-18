/**
 *
 */
package com.spar.hcl.core.cms2lib.components;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

import org.apache.log4j.Logger;


/**
 * @author kumari-p
 created for new banner component for landmark in homepage
 *
 */
public class HomePageLandmarkBannerImageComponent extends GeneratedHomePageLandmarkBannerImageComponent
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HomePageLandmarkBannerImageComponent.class.getName());

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem(ctx, type, allAttributes);
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

}
