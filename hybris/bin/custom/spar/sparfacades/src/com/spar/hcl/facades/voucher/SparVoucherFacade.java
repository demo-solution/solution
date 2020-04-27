package com.spar.hcl.facades.voucher;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.List;
import java.util.Map;


public interface SparVoucherFacade
{
	Map applyVoucherCode(final String voucherCode) throws VoucherOperationException;

	boolean checkCartAfterApplyVoucher(final String lastVoucherCode, final VoucherModel lastVoucher)
			throws VoucherOperationException;

	boolean validateSparVoucherCodeParameter(final String voucherCode);

	List<String> getSparVouchersForCart();

	boolean isVoucherValueExceedingOrdertotal(final String voucherCode) throws VoucherOperationException;

	void releaseSparVoucher(final String voucherCode) throws VoucherOperationException;

	boolean updateOrderWithVoucher(final CartData cartData);

	void updateVoucherValue(final CartData cartData, final boolean isVoucherApplied);

	double getAppliedVoucherValue();

	void updateTotalEquivalentPrice();

}