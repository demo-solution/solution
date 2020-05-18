/**
 *
 */
package com.spar.hcl.core.service.voucher;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.voucher.jalo.Voucher;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.List;


/**
 * @author kumarchoubeys
 *
 */
public interface SparVoucherService
{
	List currentVoucherInvalidationCheck(final String voucherCode);

	VoucherValue getAppliedValue(final VoucherModel voucherVal, final AbstractOrderModel order, final CartData cartData);

	VoucherValue getApplicableValue(final AbstractOrder anOrder, final Voucher voucher, final CartData cartData);

	VoucherValue getAppliedValueForOrder(final VoucherModel voucherVal, final AbstractOrderModel order, final OrderData orderData);

	VoucherValue getApplicableValueForOrder(final AbstractOrder anOrder, final Voucher voucher, final OrderData orderData);

	AbstractOrder getDiscountList(final AbstractOrderModel order);
}
