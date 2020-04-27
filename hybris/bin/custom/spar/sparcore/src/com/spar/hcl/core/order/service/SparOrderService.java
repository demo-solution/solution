/**
 *
 */
package com.spar.hcl.core.order.service;
import java.util.Map;
/**
 * @author jitendriya.m
 *
 */
public interface SparOrderService
{
	public void updateOrder(Map<String, String> resultMap);
	
	public void setOrderStatusAsFraudChecked(Map<String, String> resultMap);
}
