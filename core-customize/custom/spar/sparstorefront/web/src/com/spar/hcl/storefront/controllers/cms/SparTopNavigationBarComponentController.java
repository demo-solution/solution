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
 *
 *
 */
package com.spar.hcl.storefront.controllers.cms;

import de.hybris.platform.acceleratorcms.enums.NavigationBarMenuLayout;
import de.hybris.platform.acceleratorcms.model.components.SparTopNavigationBarComponentModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spar.hcl.storefront.controllers.ControllerConstants;


/**
 *
 */
@Controller("SparTopNavigationBarComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SparTopNavigationBarComponent)
public class SparTopNavigationBarComponentController extends AbstractCMSComponentController<SparTopNavigationBarComponentModel>
{
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final SparTopNavigationBarComponentModel component)
	{
		if (component.getDropDownLayout() != null)
		{
			model.addAttribute("dropDownLayout", component.getDropDownLayout().getCode().toLowerCase());
		}
		else if (component.getNavigationNode() != null && component.getNavigationNode().getChildren() != null
				&& !component.getNavigationNode().getChildren().isEmpty())
		{
			// Component has children but not drop down layout, default to auto
			model.addAttribute("dropDownLayout", NavigationBarMenuLayout.AUTO.getCode().toLowerCase());
		}
	}
}
