/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparDailyFraudReportEmailProcessModel;
import com.spar.hcl.model.jobs.SparDailyFraudResultModel;


/**
 * @author jitendriya.m
 *
 */
public class SparDailyFraudReportJob extends AbstractJobPerformable<CronJobModel>
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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{

		final List<SparDailyFraudResultModel> allDailyFraudReport = new ArrayList<SparDailyFraudResultModel>();

		final String START_TIME = "startTime";
		final String END_TIME = "endTime";

		final String DAILY_FRAUD_REPORT_QUERY = "select distinct {o.code} as OrderNumber,{o.creationtime} as OrderDate,round({o.totalprice},2) as TotalOrderValue, {c.uid} as CustomerId,"
				+ "{c.name} as CustomerName,{o.slotdeliverydate} as DeliveryDate,{ds.slotdescription} as DeliverySlot,{e.code} as OrderStatus,"
				+ "{pm.code} as CardType,{p.subscriptionid} as TransactionId,{c.custprimarymobnumber} as MobileNumber,{w.code} as Warehouse, round({o.paymentCaptureValue},2) as CreditInBank, round({o.balanceDue}-{o.paymentCaptureValue},2) as Diff "
				+ "from {Order as o JOIN customer as c on {o:user}={c:pk} "
				+ "join orderstatus as e on {o:status}={e:pk} join DeliverySlot as ds on {o:orderdeliveryslot}={ds:pk} "
				+ "join paymentmode as pm on {o:paymentmode}={pm:pk} join creditcardpaymentinfo as p on {o:paymentinfo}={p:pk} Join warehouse as w on {o:orderwarehouse}={w.pk}} where {pm.code} in ('creditcard')  and {o.balanceDue}!={o.paymentCaptureValue} and {o.creationtime}>?startTime and {o.creationtime}<?endTime";

		final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		final Date currentDate = new Date();
		final Date daysAgo = new DateTime(currentDate).minusDays(0).toDate();

		try
		{

			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(DAILY_FRAUD_REPORT_QUERY);
			final Map<String, Object> params = new HashMap<String, Object>();
			flexibleSearchQuery.addQueryParameter(START_TIME, sqlDateFormat.format(getStartOfDay(daysAgo)));// it works
			flexibleSearchQuery.addQueryParameter(END_TIME, sqlDateFormat.format(getEndOfDay(daysAgo)));// it works

			flexibleSearchQuery.addQueryParameters(params);

			flexibleSearchQuery.setResultClassList(
					Arrays.asList(String.class, String.class, String.class, String.class, String.class, String.class, String.class,
							String.class, String.class, String.class, String.class, String.class, String.class, String.class));

			final SearchResult<List> dailyFraudReportResult = flexibleSearchService.search(flexibleSearchQuery);

			if (!dailyFraudReportResult.getResult().isEmpty())
			{
				final List dailyFraudReportResultList = dailyFraudReportResult.getResult();
				for (int i = 0; i <= dailyFraudReportResultList.size() - 1; i++)
				{
					final List resultRow = (List) dailyFraudReportResultList.get(i);
					allDailyFraudReport.add(getDailyRraudResults(resultRow));
				}
				sendDailyFraudReportEmail(allDailyFraudReport);
			}
			else
			{
				LOG.info("Today no fraud order found");
			}

			LOG.debug("SparDailyRefundReportJob :: perform :: finished");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Order Export", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}


	/**
	 * @param resultRow
	 * @return SparDailyFraudResultModel
	 */
	private SparDailyFraudResultModel getDailyRraudResults(final List resultRow)
	{
		final SparDailyFraudResultModel dailyFraudResult = modelService.create(SparDailyFraudResultModel.class);

		dailyFraudResult.setOrderNumber((String) resultRow.get(0));
		dailyFraudResult.setOrderDate((String) resultRow.get(1));
		dailyFraudResult.setTotalOrderValue((String) resultRow.get(2));
		dailyFraudResult.setCustomerId((String) resultRow.get(3));
		dailyFraudResult.setCustomerName((String) resultRow.get(4));
		dailyFraudResult.setDeliveryDate((String) resultRow.get(5));
		dailyFraudResult.setDeliverySlot((String) resultRow.get(6));
		dailyFraudResult.setOrderStatus((String) resultRow.get(7));
		dailyFraudResult.setCardType((String) resultRow.get(8));
		dailyFraudResult.setTransactionId((String) resultRow.get(9));
		dailyFraudResult.setMobileNumber((String) resultRow.get(10));
		dailyFraudResult.setWarehouse((String) resultRow.get(11));
		dailyFraudResult.setCreditInBank((String) resultRow.get(12));
		dailyFraudResult.setDiff((String) resultRow.get(13));
		modelService.save(dailyFraudResult);
		return dailyFraudResult;
	}


	/**
	 * @param dailyFraudReportResult
	 */
	private void sendDailyFraudReportEmail(final List<SparDailyFraudResultModel> dailyFraudReportResult)
	{
		final SparDailyFraudReportEmailProcessModel process = (SparDailyFraudReportEmailProcessModel) businessProcessService
				.createProcess("SparDailyFraudReportEmailProcess" + "-" + "-" + System.currentTimeMillis(),
						"SparDailyFraudReportEmailProcess");

		final BaseSiteModel baseSite = baseSiteService
				.getBaseSiteForUID(configurationService.getConfiguration().getString("basesite.uid"));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		process.setSparDailyFraud(dailyFraudReportResult);
		modelService.saveAll();
		businessProcessService.startProcess(process);
		LOG.debug("SparDailyFraudReportJob :: sendDailyFraudReportEmail :: finished");

	}


	public static Date getEndOfDay(final Date date)
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static Date getStartOfDay(final Date date)
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
