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
package com.spar.hcl.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePwdForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.EqualAttributes;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;



/**
 * author tayal.m
 *
 * Form object for updating the password.
 */
@EqualAttributes(message = "{validation.checkPwd.equals}", value =
{ "pwd", "checkPwd" })
public class SparUpdatePwdForm extends UpdatePwdForm
{

	/**
	 * @return the pwd
	 */
	@Override
	@NotNull(message = "{updatePwd.pwd.invalid}")
	@Size(min = 8, max = 255, message = "{updatePwd.pwd.invalid}")
	//@Pattern(regexp = "^[a-zA-Z0-9]*$")
	public String getPwd()
	{
		return super.getPwd();
	}


}
