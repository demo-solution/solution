/**
 *
 */
package com.spar.hcl.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 * @author tayal.m
 *
 */
@Component("sparResetPasswordValidator")
public class SparResetPasswordValidator extends PasswordValidator
{
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SparUpdatePwdForm passwordForm = (SparUpdatePwdForm) object;

		final String newPasswd = passwordForm.getPwd();
		final String checkPasswd = passwordForm.getCheckPwd();



		if (StringUtils.isEmpty(newPasswd))
		{
			errors.rejectValue("pwd", "profile.pwd.invalid");
		}

		/*
		 * * override 8 digit validation logic in change password
		 */
		else if (StringUtils.length(newPasswd) < 8 || StringUtils.length(newPasswd) > 255)
		{
			errors.rejectValue("pwd", "updatePwd.pwd.invalid");
		}


		else if (StringUtils.isNotEmpty(checkPasswd) && !newPasswd.equals(checkPasswd))
		{
			errors.rejectValue("checkPwd", "register.password.mismatch");
		}

	}

	public boolean isAlphaNumeric(final String s)
	{
		final String pattern = "^[a-zA-Z0-9]*$";
		if (s.matches(pattern))
		{
			return true;
		}
		return false;
	}
}
