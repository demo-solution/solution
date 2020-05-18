/**
 *
 */
package com.spar.hcl.storefront.checkout.steps.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.spar.hcl.storefront.controllers.pages.DeliverySlotForm;


/**
 *
 */
public class DeliverySlotValidator implements Validator
{
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return DeliverySlotForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final DeliverySlotForm deliverySlotForm = (DeliverySlotForm) object;
		validateDeliverySlotField(deliverySlotForm.getDeliverySlot(), DeliverySlotField.DELIVERYSLOT, errors);
	}

	protected static void validateDeliverySlotField(final String deliverySlotData, final DeliverySlotField fieldType,
			final Errors errors)
	{
		if (StringUtils.isEmpty(deliverySlotData) || deliverySlotData == null)
		{
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
	}

	protected enum DeliverySlotField
	{
		DELIVERYSLOT("deliverySlot", "checkout.summary.deliveryMode.deliverySlot");

		private final String fieldKey;
		private final String errorKey;

		private DeliverySlotField(final String fieldKey, final String errorKey)
		{
			this.fieldKey = fieldKey;
			this.errorKey = errorKey;
		}

		public String getFieldKey()
		{
			return fieldKey;
		}

		public String getErrorKey()
		{
			return errorKey;
		}
	}
}
