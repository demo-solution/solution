/*
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package de.hybris.platform.plano.jalo;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.plano.model.PlanoGramModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class PlanoGram extends GeneratedPlanoGram
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(PlanoGram.class.getName());

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

	/**
	 * Finder method to get the result for a productId and warehouse location in PlanoGram
	 * 
	 * @param productId
	 * @param warehouse
	 * @return List<PlanoGram>
	 */
	@SuppressWarnings("deprecation")
	public List<PlanoGram> findPlanoGramByProductAndPos(final String productId, final String warehouse)
	{

		final StringBuilder builder = new StringBuilder();
		builder.append("SELECT {m:").append(PlanoGram.PK).append("} ");
		builder.append("FROM {").append(PlanoGramModel._TYPECODE).append(" AS m} ");
		builder.append("WHERE ").append("{m:").append(PlanoGram.PRODUCTID).append("}").append("=?" + PlanoGram.PRODUCTID + " ");
		builder.append("AND ").append("{m:").append(PlanoGram.WAREHOUSE).append("}").append("=?" + PlanoGram.WAREHOUSE + " ");

		final Map values = new HashMap();
		values.put(PlanoGram.PRODUCTID, productId);
		values.put(PlanoGram.WAREHOUSE, warehouse);

		return getSession().getFlexibleSearch().search(builder.toString(), values, PlanoGram.class).getResult();
	}


}
