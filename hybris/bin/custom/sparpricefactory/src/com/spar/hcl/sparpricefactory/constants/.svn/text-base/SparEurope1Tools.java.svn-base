/**
 *
 */
package com.spar.hcl.sparpricefactory.constants;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.PriceValue;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spar.hcl.sparpricefactory.jalo.SparPriceRow;



/**
 * This class is used to create a PriceInformation which holds value for custom attributes coming from RMS and saved in
 * SparPriceRow
 *
 * @author rohan_c
 *
 */
public class SparEurope1Tools
{

	private static final Logger LOG = Logger.getLogger(SparEurope1Tools.class.getName());

	/**
	 * This method is used to create the PriceInformation with custom attributes like warehouse etc
	 *
	 * @param row
	 * @param currency
	 * @return PriceInformation
	 */
	@SuppressWarnings("deprecation")
	public static final PriceInformation createPriceInformation(final PriceRow row, final Currency currency)
	{
		final Map qualifiers = new HashMap();
		qualifiers.put("minqtd", Long.valueOf(row.getMinQuantity()));
		qualifiers.put("unit", row.getUnit());
		qualifiers.put("pricerow", row);
		qualifiers.put("unitMRP", ((SparPriceRow) row).getUnitMRP());
		qualifiers.put("checkForPromotion", ((SparPriceRow) row).isCheckForPromotion());
		qualifiers.put("warehouse", ((SparPriceRow) row).getWarehouse());
		qualifiers.put("barcode", ((SparPriceRow) row).getSparBarcode());
		//Added for offers link- Start
		if (null != ((SparPriceRow) row).getBenefitType())
		{
			qualifiers.put("benefitType", ((SparPriceRow) row).getBenefitType().getName());
		}
		qualifiers.put("combiOffer", ((SparPriceRow) row).isCombiOffer());

		//Added for offers link- End
		//Added for popularity sorting
		qualifiers.put("popularity", ((SparPriceRow) row).getPopularity());
		qualifiers.put("discount", ((SparPriceRow) row).getDiscount());
		qualifiers.put("promoMesssage", ((SparPriceRow) row).getPromoMessage());
		qualifiers.put("bestDeal", ((SparPriceRow) row).isBestDeal());
		qualifiers.put("regularOffer", ((SparPriceRow) row).isRegularOffer());

		qualifiers.put("productOnBogo", ((SparPriceRow) row).isProductOnBogo());
		qualifiers.put("promotionDiscount", ((SparPriceRow) row).getPromotionDiscount());
		
		final DateRange dateRange = row.getDateRange();
		if (dateRange != null)
		{
			qualifiers.put("dateRange", dateRange);
		}
		final Currency act_curr = row.getCurrency();

		final double basePrice = (currency.equals(act_curr)) ? row.getPriceAsPrimitive() / row.getUnitFactorAsPrimitive()
				: act_curr.convert(currency, row.getPriceAsPrimitive() / row.getUnitFactorAsPrimitive());

		LOG.debug(" PriceInformation qualifiers: " + qualifiers + " for PriceRow : " + row.PK);

		return new PriceInformation(qualifiers, new PriceValue(currency.getIsoCode(), basePrice, row.isNetAsPrimitive()));
	}
}
