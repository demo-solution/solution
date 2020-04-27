package com.spar.hcl.core.order.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

public class SparCalculationService extends DefaultCalculationService {

	@Resource
	private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;

	@Resource
	private CommonI18NService commonI18NService;

	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException {
		final Collection<TaxValue> entryTaxes = findTaxValues(entry);
		entry.setTaxValues(entryTaxes);
		final PriceValue pv = findBasePrice(entry);
		final AbstractOrderModel order = entry.getOrder();
		final PriceValue basePrice = convertPriceIfNecessary(pv, order.getNet().booleanValue(), order.getCurrency(),
				entryTaxes);
		entry.setBasePrice(Double.valueOf(basePrice.getValue()));
	}

	@Override
	protected Map resetAllValues(final AbstractOrderModel order) throws CalculationException {
		
		final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(order, false);
		final Collection<TaxValue> relativeTaxValues = new LinkedList<TaxValue>();
		
		for (final Map.Entry<TaxValue, ?> e : taxValueMap.entrySet()) {
			final TaxValue taxValue = e.getKey();
			if (!taxValue.isAbsolute()) {
				relativeTaxValues.add(taxValue);
			}
		}

		resetAdditionalCosts(order, relativeTaxValues);

		return taxValueMap;
	}

	@Override
	protected void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException {

		if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order)) {
			final CurrencyModel curr = order.getCurrency();
			final int digits = curr.getDigits().intValue();
			final double subtotal = order.getSubtotal().doubleValue();

			final double total = subtotal + order.getPaymentCost().doubleValue()
					+ order.getDeliveryCost().doubleValue();
			final double totalRounded = commonI18NService.roundCurrency(total, digits);
			order.setTotalPrice(Double.valueOf(totalRounded));
			final double totalTaxes = calculateTotalTaxValues(order, recalculate, digits,
					getTaxCorrectionFactor(taxValueMap, subtotal, total, order), taxValueMap);
			final double totalRoundedTaxes = commonI18NService.roundCurrency(totalTaxes, digits);
			order.setTotalTax(Double.valueOf(totalRoundedTaxes));
			setCalculatedStatus(order);
			saveOrder(order);

		}
	}
}
