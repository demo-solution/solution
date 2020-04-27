/**
 *
 */
package com.spar.hcl.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import java.util.regex.Pattern;


/**
 * @author tayal.m
 *
 */
@Component("sparPasswordValidator")
public class SparPasswordValidator extends PasswordValidator
{
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final UpdatePasswordForm passwordForm = (UpdatePasswordForm) object;
		final String currPasswd = passwordForm.getCurrentPassword();
		final String newPasswd = passwordForm.getNewPassword();
		final String checkPasswd = passwordForm.getCheckNewPassword();

		if (StringUtils.isEmpty(currPasswd))
		{
			errors.rejectValue("currentPassword", "profile.currentPassword.invalid");
		}

		if (StringUtils.isEmpty(newPasswd))
		{
			errors.rejectValue("newPassword", "profile.newPassword.invalid");
		}

		/*
		 * * override 8 digit validation logic in change password
		 */
		else if (StringUtils.length(currPasswd) < 8 || StringUtils.length(currPasswd) > 255)
		{
			errors.rejectValue("currentPassword", "updatePwd.pwd.invalid");
		}

		else if (StringUtils.length(newPasswd) < 8 || StringUtils.length(newPasswd) > 255)
		{
			errors.rejectValue("newPassword", "updatePwd.pwd.invalid");
		}

		else if (StringUtils.isNotEmpty(newPasswd) && StringUtils.isNotEmpty(checkPasswd) && !newPasswd.equals(checkPasswd))
		{
			errors.rejectValue("checkNewPassword", "register.password.mismatch");
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
