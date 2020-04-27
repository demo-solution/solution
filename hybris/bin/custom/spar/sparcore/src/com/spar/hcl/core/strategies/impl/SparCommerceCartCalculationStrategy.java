package com.spar.hcl.core.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;

public class SparCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy 
{
	private boolean calculateExternalTaxes = false;
	
	@Override
	public boolean calculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");

		final CalculationService calcService = getCalculationService();
		boolean recalculated = false;
		if (calcService.requiresCalculation(cartModel))
		{
			try
			{
				parameter.setRecalculate(false);
				beforeCalculate(parameter);
				calcService.calculate(cartModel);
			}
			catch (final CalculationException calculationException)
			{
				throw new IllegalStateException("Cart model " + cartModel.getCode() + " was not calculated due to: "
						+ calculationException.getMessage(), calculationException);
			}
			finally
			{
				afterCalculate(parameter);
			}
			recalculated = true;

			if (calculateExternalTaxes)
			{
				getExternalTaxesService().calculateExternalTaxes(cartModel);
			}
		}

		return recalculated;
	}
	public boolean isCalculateExternalTaxes() {
		return calculateExternalTaxes;
	}
	public void setCalculateExternalTaxes(boolean calculateExternalTaxes) {
		this.calculateExternalTaxes = calculateExternalTaxes;
	}
}
