/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparDailyRefundReportEmailProcessModel;
import com.spar.hcl.model.jobs.SparDailyRefundResultModel;


public class SparDailyRefundReportJob extends AbstractJobPerformable
{
	private static final Logger LOG = Logger.getLogger(SparDailyRefundReportJob.class);

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private ConfigurationService configurationService;

	@SuppressWarnings("deprecation")
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		LOG.debug("SparDailyRefundReportJob :: perform :: start");
		final List<SparDailyRefundResultModel> dailyRefundResultList = new ArrayList<SparDailyRefundResultModel>();

		final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		final Calendar previousDate = Calendar.getInstance();
		previousDate.add(Calendar.DATE, -7);
		previousDate.set(Calendar.MILLISECOND, 0);
		previousDate.set(Calendar.MINUTE, 0);
		previousDate.set(Calendar.HOUR_OF_DAY, 0);
		previousDate.set(Calendar.SECOND, 0);

		final Calendar todayDate = Calendar.getInstance();
		todayDate.set(Calendar.MILLISECOND, 0);
		todayDate.set(Calendar.MINUTE, 0);
		todayDate.set(Calendar.HOUR_OF_DAY, 0);
		todayDate.set(Calendar.SECOND, 0);

		/*
		 * final String DAILY_REFUND_REPORT_QUERY =
		 * "select distinct {o.code} as OrderNumber,{o.creationtime} as OrderDate,round({o.totalprice},2) as TotalOrderValue,"
		 * +
		 * "{c.name} as CustomerName,{o.slotdeliverydate} as DeliveryDate,{ds.slotdescription} as DeliverySlot,{e.code} as OrderStatus,"
		 * +
		 * "{pm.code} as CardType,{p.subscriptionid} as TransactionId,{c.custprimarymobnumber} as MobileNumber,round({o.totaldiscounts},2) as TotalOrderDiscount "
		 * + "from {OrderEntry as oe join Order as o on {oe:order}={o:pk} JOIN customer as c on {o:user}={c:pk} " +
		 * "join orderstatus as e on {o:status}={e:pk} join DeliverySlot as ds on {o:orderdeliveryslot}={ds:pk} " +
		 * "join paymentmode as pm on {o:paymentmode}={pm:pk} join creditcardpaymentinfo as p on {o:paymentinfo}={p:pk}} "
		 * + "where {o." + OrderModel.MODIFIEDTIME + "} >= '" + sqlDateFormat.format(previousDate.getTime()) + "' AND {o."
		 * + OrderModel.MODIFIEDTIME + "} < '" + sqlDateFormat.format(todayDate.getTime()) + "'";
		 */

		final String DAILY_REFUND_REPORT_QUERY = "select OrderNumber,OrderDate, TotalOrderValue,CustomerId, CustomerName, DeliveryDate,DeliverySlot,OrderStatus,CardType,TransactionId, MobileNumber, TotalOrderDiscount, Warehouse, EmployeeCode ,PaidByLandmarkReward from ("
				+ "{{"
				+ "select distinct {o.code} as OrderNumber,{o.creationtime} as OrderDate,round({o.balanceDue},2) as TotalOrderValue, {c.uid} as CustomerId,"
				+ "{c.name} as CustomerName,{o.slotdeliverydate} as DeliveryDate,{ds.slotdescription} as DeliverySlot,{e.code} as OrderStatus,"
				+ "{pm.code} as CardType,'' as TransactionId,{c.custprimarymobnumber} as MobileNumber,round({o.totaldiscounts},2) as TotalOrderDiscount, {w.code} as Warehouse,{c.employeecode} as EmployeeCode,round({o.paidByLandmarkReward},2) as PaidByLandmarkReward "
				+ "from {OrderEntry as oe join Order as o on {oe:order}={o:pk} JOIN customer as c on {o:user}={c:pk} "
				+ "join orderstatus as e on {o:status}={e:pk} join DeliverySlot as ds on {o:orderdeliveryslot}={ds:pk}"
				+ "join CashOnDeliveryPaymentInfo as p on {o:paymentinfo}={p:pk} join warehouse as w on {o:orderwarehouse}={w.pk} join paymentmode as pm on {o:paymentmode}={pm:pk}} where {pm.code} in ('cashondelivery','multiPaymentMode')"
				+ " and {o."
				+ OrderModel.MODIFIEDTIME
				+ "} >= '"
				+ sqlDateFormat.format(previousDate.getTime())
				+ "' AND {o."
				+ OrderModel.MODIFIEDTIME
				+ "} < '"
				+ sqlDateFormat.format(todayDate.getTime())
				+ "'"
				+ "}}"
				+ " union "
				+ "{{"
				+ "select distinct {o.code} as OrderNumber,{o.creationtime} as OrderDate,round({o.balanceDue},2) as TotalOrderValue, {c.uid} as CustomerId,"
				+ "{c.name} as CustomerName,{o.slotdeliverydate} as DeliveryDate,{ds.slotdescription} as DeliverySlot,{e.code} as OrderStatus,"
				+ "{pm.code} as CardType,{p.subscriptionid} as TransactionId,{c.custprimarymobnumber} as MobileNumber,round({o.totaldiscounts},2) as TotalOrderDiscount, {w.code} as Warehouse,{c.employeecode} as EmployeeCode,round({o.paidByLandmarkReward},2) as PaidByLandmarkReward "
				+ "from {OrderEntry as oe join Order as o on {oe:order}={o:pk} JOIN customer as c on {o:user}={c:pk} "
				+ "join orderstatus as e on {o:status}={e:pk} join DeliverySlot as ds on {o:orderdeliveryslot}={ds:pk}"
				+ "join paymentmode as pm on {o:paymentmode}={pm:pk} join creditcardpaymentinfo as p on {o:paymentinfo}={p:pk} Join warehouse as w on {o:orderwarehouse}={w.pk}} where {pm.code} in ('creditcard','multiPaymentMode')"
				+ " and {o."
				+ OrderModel.MODIFIEDTIME
				+ "} >= '"
				+ sqlDateFormat.format(previousDate.getTime())
				+ "' AND {o."
				+ OrderModel.MODIFIEDTIME
				+ "} < '"
				+ sqlDateFormat.format(todayDate.getTime())
				+ "'"
				+ "}}"
				+ " union "
				+ "{{"
				+ "select distinct {o.code} as OrderNumber,{o.creationtime} as OrderDate,round({o.balanceDue},2) as TotalOrderValue, {c.uid} as CustomerId,"
				+ "{c.name} as CustomerName,{o.slotdeliverydate} as DeliveryDate,{ds.slotdescription} as DeliverySlot,{e.code} as OrderStatus,"
				+ "{pm.code} as CardType,'' as TransactionId,{c.custprimarymobnumber} as MobileNumber,round({o.totaldiscounts},2) as TotalOrderDiscount, {w.code} as Warehouse,{c.employeecode} as EmployeeCode,round({o.paidByLandmarkReward},2) as PaidByLandmarkReward "
				+ "from {OrderEntry as oe join Order as o on {oe:order}={o:pk} JOIN customer as c on {o:user}={c:pk} "
				+ "join orderstatus as e on {o:status}={e:pk} join DeliverySlot as ds on {o:orderdeliveryslot}={ds:pk}"
				+ "join warehouse as w on {o:orderwarehouse}={w.pk} join paymentmode as pm on {o:paymentmode}={pm:pk}} where {pm.code} in ('landmarkReward')"
				+ " and {o."
				+ OrderModel.MODIFIEDTIME
				+ "} >= '"
				+ sqlDateFormat.format(previousDate.getTime())
				+ "' AND {o."
				+ OrderModel.MODIFIEDTIME
				+ "} < '"
				+ sqlDateFormat.format(todayDate.getTime())
				+ "'"
				+ "}}"
				+ ") as foo order by OrderDate";

		LOG.debug("SparDailyRefundReportJob :: perform :: query " + DAILY_REFUND_REPORT_QUERY);

		try
		{
			final FlexibleSearchQuery queryForOrders = new FlexibleSearchQuery(DAILY_REFUND_REPORT_QUERY);

			queryForOrders.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
					String.class,String.class));

			final SearchResult<List> dailyRefundReportResult = flexibleSearchService.search(queryForOrders);

			if (!dailyRefundReportResult.getResult().isEmpty())
			{
				final List dailyRefundReportResultList = dailyRefundReportResult.getResult();
				for (int i = 0; i <= dailyRefundReportResultList.size() - 1; i++)
				{
					final List resultRow = (List) dailyRefundReportResultList.get(i);
					dailyRefundResultList.add(getDailyRefundResults(resultRow));
				}
			}
			else
			{
				LOG.info("Daily Refund no result found");
			}
			sendDailyRefundReportEmail(dailyRefundResultList);
			LOG.debug("SparDailyRefundReportJob :: perform :: finished");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Order Export", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	private SparDailyRefundResultModel getDailyRefundResults(final List resultRow)
	{
		final SparDailyRefundResultModel dailyRefundResult = modelService.create(SparDailyRefundResultModel.class);

		dailyRefundResult.setCardType((String) resultRow.get(8));
		dailyRefundResult.setCustomerId((String) resultRow.get(3));
		dailyRefundResult.setCustomerName((String) resultRow.get(4));
		dailyRefundResult.setDeliveryDate((String) resultRow.get(5));
		dailyRefundResult.setDeliverySlot((String) resultRow.get(6));
		dailyRefundResult.setMobileNumber((String) resultRow.get(10));
		dailyRefundResult.setOrderDate((String) resultRow.get(1));
		dailyRefundResult.setOrderNumber((String) resultRow.get(0));
		dailyRefundResult.setOrderStatus((String) resultRow.get(7));
		dailyRefundResult.setTotalOrderValue((String) resultRow.get(2));
		dailyRefundResult.setTransactionId((String) resultRow.get(9));
		dailyRefundResult.setTotalOrderDiscount((String) resultRow.get(11));

		dailyRefundResult.setWarehouse((String) resultRow.get(12));
		dailyRefundResult.setEmployeeCode((String) resultRow.get(13));
		dailyRefundResult.setPaidByLandmarkReward((String) resultRow.get(14));

		modelService.save(dailyRefundResult);
		LOG.debug("SparDailyRefundReportJob :: getDailyRefundResults :: ends");
		return dailyRefundResult;
	}

	private void sendDailyRefundReportEmail(final List<SparDailyRefundResultModel> dailyRefundResultList)
	{
		final SparDailyRefundReportEmailProcessModel process = (SparDailyRefundReportEmailProcessModel) businessProcessService
				.createProcess("SparDailyRefundReportEmailProcess" + "-" + "-" + System.currentTimeMillis(),
						"SparDailyRefundReportEmailProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				"basesite.uid"));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		process.setDailyRefundResults(dailyRefundResultList);
		modelService.saveAll();
		businessProcessService.startProcess(process);
		LOG.debug("SparDailyRefundReportJob :: sendDailyRefundReportEmail :: finished");
	}

}
