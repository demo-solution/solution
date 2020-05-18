package com.spar.hcl.jalo.vouchers;
import de.hybris.platform.voucher.jalo.SerialVoucher;
import de.hybris.platform.voucher.jalo.Voucher;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
/**
 * @author ravindra.kr
 */
public class SparSerialVoucher extends SerialVoucher
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparSerialVoucher.class.getName());

	@Override
	public String generateVoucherCode() throws NoSuchAlgorithmException
	{
		int voucherNumber = getNextVoucherNumber(getSession().getSessionContext());
		/*if (voucherNumber < 0 || voucherNumber >= 16777216)
		{
			throw new IllegalArgumentException("Given voucherNumber is not in accepted range!");
		}
		else
		{
			final String clearText = (new StringBuilder(String.valueOf(getCode()))).append(threeByteHex(voucherNumber)).toString();
			final String sig = threeByteSig(clearText);
			return insertDividers(clearText.concat(sig));
		}*/
		/*if(voucherNumber == 0)
		{
			voucherNumber++;
		}*/
		if (voucherNumber == 0 || voucherNumber < 1 || voucherNumber > 999)
		{
			//throw new IllegalArgumentException("Given voucherNumber is not in accepted range!");
			return "";
		}
		else
		{
   		final String clearText = (new StringBuilder(String.valueOf(getCode()))).append(voucherNumber).toString();
   		return clearText;
		}
	}
	
	  @Override
	public boolean checkVoucherCode(final String aVoucherCode)
	  {
	      //final int lastVoucherNumber = getLastVoucherNumber(getSession().getSessionContext());
			 try
			 {
			   final int voucherNumber = getVoucherNumber(aVoucherCode);
			   return (voucherNumber >= 1) && (voucherNumber <= 999);
			 }
			 catch (final Voucher.InvalidVoucherKeyException localInvalidVoucherKeyException)
			 {
			   return false;
			 }
	  }
	  
	  /*private int getLastVoucherNumber(final SessionContext ctx)
	  {
	    final Object lastVoucherNumber = getProperty(ctx, "lastVoucherNumber");
	    if (lastVoucherNumber != null) {
	      return ((Integer)lastVoucherNumber).intValue();
	    }
	    return -1;
	  }*/
	  
	  @Override
	  protected int getVoucherNumber(final String voucherCode)
	        throws InvalidVoucherKeyException
	    {
	        final String voucherNumber = voucherCode.substring(3, voucherCode.length());
	        return Integer.valueOf(voucherNumber).intValue();
	    }
}
