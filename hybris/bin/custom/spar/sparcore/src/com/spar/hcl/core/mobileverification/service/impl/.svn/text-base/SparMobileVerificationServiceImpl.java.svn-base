/**
 *
 */
package com.spar.hcl.core.mobileverification.service.impl;

import com.spar.hcl.core.mobileverification.dao.SparMobileVerificationDAO;
import com.spar.hcl.core.mobileverification.service.SparMobileVerificationService;


/**
 * @author jitendriya.m
 *
 */
public class SparMobileVerificationServiceImpl implements SparMobileVerificationService
{

	private SparMobileVerificationDAO sparMobileVerificationDao;


	/**
	 * @return the sparMobileVerificationDao
	 */
	public SparMobileVerificationDAO getSparMobileVerificationDao()
	{
		return sparMobileVerificationDao;
	}


	/**
	 * @param sparMobileVerificationDao
	 *           the sparMobileVerificationDao to set
	 */
	public void setSparMobileVerificationDao(final SparMobileVerificationDAO sparMobileVerificationDao)
	{
		this.sparMobileVerificationDao = sparMobileVerificationDao;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.mobileverification.service.SparMobileVerificationService#countMobileNo(java.lang.String)
	 */
	@Override
	public Boolean countMobileNo(final String mobileNO)
	{
		return sparMobileVerificationDao.countMobileNo(mobileNO);
	}

}
