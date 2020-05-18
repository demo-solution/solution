/**
 *
 */
package com.spar.hcl.core.util;


import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This method is used to convert the IPG request parameters into has using shared secret.
 *
 * @author ravindra.kr
 *
 */

public class SparIPGResponseHashGenerationUtilService
{
	/**
	 * Default constructor
	 */
	public SparIPGResponseHashGenerationUtilService()
	{
		// default constructor
	}

	/**
	 * Parameterized constructor
	 *
	 * @param storeId
	 * @param sharedSecret
	 * @param charge
	 * @param ccy
	 * @param dateNow
	 */
	public SparIPGResponseHashGenerationUtilService(final String sharedSecret, final String approval_code, final String charge, final String ccy,
			final Date dateNow, final String storeId)
	{
		this.storeId = storeId;
		this.sharedSecret = sharedSecret;
		this.charge = charge;
		currency = ccy;
		fmtDate = dateFormat.format(dateNow);
		this.approval_code = approval_code;
	}

	/**
	 * IPG method to create hash using secret key.
	 *
	 * @return String
	 */
	public String createHash()
	{
		final String stringToHash = (new StringBuilder(String.valueOf(sharedSecret))).append(approval_code).append(charge).append(currency)
				.append(fmtDate).append(storeId).toString();
		return calculateHashFromHex(new StringBuffer(stringToHash));
	}

	/**
	 * This method is used to calculate the has frommShared secret key.
	 *
	 * @param buffer
	 * @return String
	 */
	private String calculateHashFromHex(StringBuffer buffer)
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance(algorithm);
		}
		catch (final Exception e)
		{
			throw new IllegalArgumentException((new StringBuilder("Algorithm '")).append(algorithm).append("' not supported")
					.toString());
		}
		final StringBuffer result = new StringBuffer();
		final StringBuffer sb = new StringBuffer();
		final byte bytes[] = buffer.toString().getBytes();
		final int byteLen = bytes.length;
		for (int i = 0; i < byteLen; i++)
		{
			final byte b = bytes[i];
			sb.append(Character.forDigit((b & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(b & 0xf, 16));
		}

		buffer = new StringBuffer(sb.toString());
		messageDigest.update(buffer.toString().getBytes());
		final byte message[] = messageDigest.digest();
		final int messageLen = message.length;
		for (int j = 0; j < messageLen; j++)
		{
			final byte b = message[j];
			String apps = Integer.toHexString(b & 0xff);
			if (apps.length() == 1)
			{
				apps = (new StringBuilder("0")).append(apps).toString();
			}
			result.append(apps);
		}

		return result.toString();
	}

	/**
	 * Getter
	 *
	 * @return String
	 */
	public String getCharge()
	{
		return charge;
	}

	/**
	 * Getter Shared Secret
	 *
	 * @return String
	 */
	public String getSharedSecret()
	{
		return sharedSecret;
	}

	/**
	 * Getter storeId
	 *
	 * @return String
	 */
	public String getStoreId()
	{
		return storeId;
	}

	/**
	 * getter for fmtDate
	 * 
	 * @return String
	 */
	public String getFormattedSysDate()
	{
		return fmtDate;
	}


	/**
	 * Getter
	 *
	 * @return the algorithm
	 */
	public String getAlgorithm()
	{
		return algorithm;
	}

	/**
	 * Setter
	 *
	 * @param algorithm
	 *           the algorithm to set
	 */
	public void setAlgorithm(final String algorithm)
	{
		this.algorithm = algorithm;
	}

	/**
	 * this method is used to set all the parameter, which will be used to generate has using shared secret.
	 * 
	 * @param storeId
	 * @param sharedSecret
	 * @param charge
	 * @param ccy
	 * @param dateNow
	 */
	public void setParameters(final String sharedSecret, final String approval_code, final String charge, final String ccy,
			final Date dateNow, final String storeId)
	{
		this.storeId = storeId;
		this.sharedSecret = sharedSecret;
		this.charge = charge;
		this.approval_code = approval_code;
		currency = ccy;
		fmtDate = dateFormat.format(dateNow);
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
	private String fmtDate;
	private String storeId;
	private String sharedSecret;
	private String charge;
	private String currency;
	private String algorithm;
	private String approval_code;
}