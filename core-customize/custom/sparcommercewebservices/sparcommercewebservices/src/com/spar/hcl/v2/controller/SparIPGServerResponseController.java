/**
 *
 */
package com.spar.hcl.v2.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spar.hcl.core.enums.SparIPGDecisionEnum;
import com.spar.hcl.core.order.service.SparOrderService;


/**
 * @author jitendriya.m
 *
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/ipg/server")
public class SparIPGServerResponseController
{
	private static final Logger LOG = Logger.getLogger(SparIPGServerResponseController.class);

	@Resource(name = "sparOrderService")
	private SparOrderService sparOrderService;

	/**
	 * This method is used to handle Server to server IPG Response (Success/failure)
	 *
	 */
	@RequestMapping(value = "/response", method = RequestMethod.POST)
	public void doHandleIpgServerResponse(@RequestParam(required = false) final String oid,
			@RequestParam(required = false) final String status, @RequestParam(required = false) final String chargetotal)
	{
		LOG.info("In doHandleIpgServerResponse() Method :");
		LOG.info("oid : " + oid);
		LOG.info("status : " + status);
		LOG.info("chargetotal : " + chargetotal);
		final Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("oid", oid);
		resultMap.put("status", status);
		resultMap.put("chargetotal", chargetotal);
		if (status.equalsIgnoreCase(SparIPGDecisionEnum.APPROVED.getCode()))
		{
			LOG.info("Start Server to Server response !." + resultMap.get("oid"));
			LOG.info("Start Time " + new DateTime());
			sparOrderService.updateOrder(resultMap);
			LOG.info("Finish Time " + new DateTime());
			LOG.info("Finish Server to Server response !.");
		}
		else
		{
			LOG.info("Set Order Status as Fraud Check.");
			sparOrderService.setOrderStatusAsFraudChecked(resultMap);
			//LOG.error("Failed to create IPG subscription. Error occurred while contacting external payment services.");
		}
	}

	protected Map<String, String> getRequestParameterMap(final HttpServletRequest request)
	{
		final Map<String, String> map = new HashMap<String, String>();

		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			final String paramValue = request.getParameter(paramName);
			map.put(paramName, paramValue);
		}

		return map;
	}
}
