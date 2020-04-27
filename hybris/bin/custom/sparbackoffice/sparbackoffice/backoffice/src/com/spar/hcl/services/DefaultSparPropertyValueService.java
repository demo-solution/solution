/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.spar.hcl.services;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.core.expression.*;
import com.hybris.cockpitng.core.expression.impl.DefaultEvaluationContextFactory;
import com.hybris.cockpitng.core.expression.impl.DefaultExpressionResolverFactory;
import com.hybris.cockpitng.dataaccess.services.PropertyReadResult;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.dataaccess.services.impl.DefaultPropertyValueService;
import com.hybris.cockpitng.dataaccess.services.impl.expression.RestrictedAccessException;
import com.spar.hcl.widgets.listview.actions.export.csv.SparPickListAction;

import java.util.*;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.*;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DefaultSparPropertyValueService extends DefaultPropertyValueService
{
	private static final Logger LOG = LoggerFactory.getLogger(SparPickListAction.class);
	
	@Override
	public Map<String, Object> readValues(Object sourceObject, List<String> qualifiers, List<Locale> locales)
         throws IllegalStateException
     {
		 ExpressionResolver resolver = getResolverFactory().createResolver();
	    
	    Map<String, Object> values = new LinkedHashMap();
	    for (String qualifier : qualifiers) {
	      try
	      {
	        Object value = resolver.getValue(sourceObject, qualifier, Collections.singletonMap("locales", locales));
	        values.put(qualifier, value);
	      }
	      catch (ParseException|SpelEvaluationException e)
	      {
	      	if(LOG.isDebugEnabled())
	      	{
	      		LOG.error("error parsing expression '" + qualifier + "' on object " + sourceObject);
	      	}
	      }
	    }
	    return values;
     }
}
