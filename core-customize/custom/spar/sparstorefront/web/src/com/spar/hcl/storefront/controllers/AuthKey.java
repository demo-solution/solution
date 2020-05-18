/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spar.hcl.storefront.controllers;

/**
 *
 * @author CEINFO
 */
public class AuthKey
{
	private String licKey;

	public void setLicKey(final String licKey)
	{
		this.licKey = licKey;
	}

	public String getLicKey()
	{
		return licKey;
	}

	@Override
	public String toString()
	{
		return "AuthKey{" + "licKey=" + licKey + '}';
	}

}
