package com.spar.hcl.v2.controller;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cms2lib.model.components.RotatingImagesComponentModel;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.dto.SparBannerComponentListWsDTO;
import com.spar.hcl.dto.SparBannerComponentWsDTO;
import com.spar.hcl.dto.SparContentPageWsDTO;
import com.spar.hcl.model.cms.SparBannerWarehouseRestrictionModel;


@Controller
@RequestMapping(value = "/{baseSiteId}/page")
public class SparContentPageController extends BaseController
{
	protected static final Logger LOG = Logger.getLogger(SparContentPageController.class);
	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";
	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;
	@Resource(name = "imagePopulator")
	private ImagePopulator imagePopulator;
	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;
	@Autowired
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CatalogVersionService catalogVersionService;
	private static final String GET_WAREHOUSE_BANNERS = "select {p.pk} from {SparBannerWarehouseRestriction as p} where {p.catalogversion} in ({{ select {pk} from {catalogversion} where {catalog} in ({{select {PK} from {catalog} where {ID} = 'sparContentCatalog'}}) and {version}= 'Online' }})";

	@RequestMapping(value = "/getHomePageBanners", method = RequestMethod.GET)
	@ResponseBody
	public SparBannerComponentListWsDTO getHomePageBanner(@RequestParam(required = false) final String pointOfServiceName)
			throws CMSItemNotFoundException, Exception
	{
		final SparBannerComponentListWsDTO sparBannerComponentListWsDTO = new SparBannerComponentListWsDTO();
		sparBannerComponentListWsDTO.setBanners(new ArrayList<SparBannerComponentWsDTO>());
		getAllHomePageBanners(sparBannerComponentListWsDTO, pointOfServiceName);
		if (CollectionUtils.isNotEmpty(sparBannerComponentListWsDTO.getBanners()))
		{
			sparBannerComponentListWsDTO.setMessage("success");
		}
		else
		{
			sparBannerComponentListWsDTO.setMessage("Home page banners doesn't exist");
			sparBannerComponentListWsDTO.setTimeout(null);
		}
		return sparBannerComponentListWsDTO;
	}

	public List<SparBannerWarehouseRestrictionModel> getBannerWarehouseRestriction(final WarehouseModel warehouse)
	{
		final List<SparBannerWarehouseRestrictionModel> result = new ArrayList<SparBannerWarehouseRestrictionModel>();
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_WAREHOUSE_BANNERS);
		final SearchResult<SparBannerWarehouseRestrictionModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		for (final SparBannerWarehouseRestrictionModel res : searchResult.getResult())
		{
			final List<WarehouseModel> wareHouseList = res.getWarehouses();
			for (final WarehouseModel ware : wareHouseList)
			{
				if (ware.getCode().equals(warehouse.getCode()))
				{
					result.add(res);
				}
			}
		}
		return result;
	}

	public SparBannerComponentListWsDTO getAllHomePageBanners(final SparBannerComponentListWsDTO sparBannerComponentListWsDTO,
			final String pointOfServiceName) throws CMSItemNotFoundException
	{
		final ContentPageModel page = cmsPageService.getPageForLabelOrId("homepage");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("Section1Slot-Homepage"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("Section1Slot-Homepage"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent.getUid().equals("SparHomepageRotatingImagesComponent"))
						{
							final RotatingImagesComponentModel component = (RotatingImagesComponentModel) cmsComponent;
							sparBannerComponentListWsDTO.setTimeout(component.getTimeout());
							for (final BannerComponentModel bannerComponent : component.getBanners())
							{
								if (bannerComponent.getVisible().booleanValue()
										&& CollectionUtils.isEmpty(bannerComponent.getRestrictions()))
								{
									final SparBannerComponentWsDTO sparBannerComponentWsDTO = addHomePageBannger(bannerComponent);
									sparBannerComponentListWsDTO.getBanners().add(sparBannerComponentWsDTO);
								}
								else if (bannerComponent.getVisible().booleanValue()
										&& CollectionUtils.isNotEmpty(bannerComponent.getRestrictions()))
								{
									getBanners(sparBannerComponentListWsDTO, bannerComponent, pointOfServiceName);
								}
							}
						}
					}
				}
			}
		}
		return sparBannerComponentListWsDTO;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	@ResponseBody
	public SparContentPageWsDTO getFAQPageContent() throws CMSItemNotFoundException
	{
		final SparContentPageWsDTO sparContentPageWsDTO = new SparContentPageWsDTO();

		final ContentPageModel page = cmsPageService.getPageForLabelOrId("faq");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("faqparagraphcontentslot"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("faqparagraphcontentslot"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent instanceof CMSParagraphComponentModel && cmsComponent.getUid().equals("faqparagraphcomponent"))
						{
							final CMSParagraphComponentModel component = (CMSParagraphComponentModel) cmsComponent;
							if (StringUtils.isNotEmpty(component.getContent()))
							{
								sparContentPageWsDTO.setContent(component.getContent());
								sparContentPageWsDTO.setMessage("success");
							}

						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(sparContentPageWsDTO.getContent()))
		{
			sparContentPageWsDTO.setMessage("content not found");
		}
		return sparContentPageWsDTO;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/termsandConditions", method = RequestMethod.GET)
	@ResponseBody
	public SparContentPageWsDTO getTermsandConditionsPageContent() throws CMSItemNotFoundException
	{
		final SparContentPageWsDTO sparContentPageWsDTO = new SparContentPageWsDTO();

		final ContentPageModel page = cmsPageService.getPageForLabelOrId("termsAndConditions");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("Section2ASlot-TermsAndConditions"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("Section2ASlot-TermsAndConditions"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent instanceof CMSParagraphComponentModel
								&& cmsComponent.getUid().equals("termsAndConditionsMenuParagraph"))
						{
							final CMSParagraphComponentModel component = (CMSParagraphComponentModel) cmsComponent;
							if (StringUtils.isNotEmpty(component.getContent()))
							{
								sparContentPageWsDTO.setContent(component.getContent());
								sparContentPageWsDTO.setMessage("success");
							}
						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(sparContentPageWsDTO.getContent()))
		{
			sparContentPageWsDTO.setMessage("content not found");
		}
		return sparContentPageWsDTO;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/aboutUs", method = RequestMethod.GET)
	@ResponseBody
	public SparContentPageWsDTO getAboutUsPageContent() throws CMSItemNotFoundException
	{
		final SparContentPageWsDTO sparContentPageWsDTO = new SparContentPageWsDTO();

		final ContentPageModel page = cmsPageService.getPageForLabelOrId("aboutus");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("aboutusparagraphcontentslot"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("aboutusparagraphcontentslot"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent instanceof CMSParagraphComponentModel
								&& cmsComponent.getUid().equals("aboutusparagraphcomponent"))
						{
							final CMSParagraphComponentModel component = (CMSParagraphComponentModel) cmsComponent;
							if (StringUtils.isNotEmpty(component.getContent()))
							{
								sparContentPageWsDTO.setContent(component.getContent());
								sparContentPageWsDTO.setMessage("success");
							}
						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(sparContentPageWsDTO.getContent()))
		{
			sparContentPageWsDTO.setMessage("content not found");
		}
		return sparContentPageWsDTO;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
	@ResponseBody
	public SparContentPageWsDTO getPrivacyPolicyPageContent() throws CMSItemNotFoundException
	{
		final SparContentPageWsDTO sparContentPageWsDTO = new SparContentPageWsDTO();

		final ContentPageModel page = cmsPageService.getPageForLabelOrId("policy");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("policyparagraphcontentslot"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("policyparagraphcontentslot"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent instanceof CMSParagraphComponentModel
								&& cmsComponent.getUid().equals("policyparagraphcomponent"))
						{
							final CMSParagraphComponentModel component = (CMSParagraphComponentModel) cmsComponent;
							if (StringUtils.isNotEmpty(component.getContent()))
							{
								sparContentPageWsDTO.setContent(component.getContent());
								sparContentPageWsDTO.setMessage("success");
							}
						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(sparContentPageWsDTO.getContent()))
		{
			sparContentPageWsDTO.setMessage("content not found");
		}
		return sparContentPageWsDTO;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/contactUs", method = RequestMethod.GET)
	@ResponseBody
	public SparContentPageWsDTO getContactUsPageContent() throws CMSItemNotFoundException
	{
		final SparContentPageWsDTO sparContentPageWsDTO = new SparContentPageWsDTO();

		final ContentPageModel page = cmsPageService.getPageForLabelOrId("contactus");
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
		for (final ContentSlotData contentSlotData : contentSlotsForPage)
		{
			if (contentSlotData.getUid().equals("contactusparagraphcontent"))
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				if (contentSlot.getUid().equals("contactusparagraphcontent"))
				{
					final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
					for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
					{
						if (cmsComponent instanceof CMSParagraphComponentModel
								&& cmsComponent.getUid().equals("linkcontactUsParagraph"))
						{
							final CMSParagraphComponentModel component = (CMSParagraphComponentModel) cmsComponent;
							if (StringUtils.isNotEmpty(component.getContent()))
							{
								sparContentPageWsDTO.setContent(component.getContent());
								sparContentPageWsDTO.setMessage("success");
							}
						}
					}
				}
			}
		}
		if (StringUtils.isEmpty(sparContentPageWsDTO.getContent()))
		{
			sparContentPageWsDTO.setMessage("content not found");
		}
		return sparContentPageWsDTO;
	}

	private Date checkDate(final Date date, final String formater)
	{
		Date startDate = null;
		if (date != null)
		{
			final SimpleDateFormat dateFormat = new SimpleDateFormat(formater);
			try
			{
				startDate = dateFormat.parse(dateFormat.format(date));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
		}
		return startDate;
	}

	private WarehouseModel getWarehouses(String pointOfServiceName)
	{
		if (StringUtils.isNotEmpty(pointOfServiceName))
		{
			final WarehouseModel warehouse = storeFinderServiceInterface.getWarehouse(pointOfServiceName);
			if (warehouse != null)
			{
				return warehouse;
			}
		}
		return null;
	}

	private void getBanners(final SparBannerComponentListWsDTO sparBannerComponentListWsDTO, BannerComponentModel bannerComponent,
			String pointOfServiceName)
	{
		final String maxRestrictionCount = Config.getParameter("spar.banner.restriction.count");
		List<AbstractRestrictionModel> restrictions = bannerComponent.getRestrictions();
		if (restrictions.size() >= Integer.parseInt(maxRestrictionCount) && !bannerComponent.isOnlyOneRestrictionMustApply())
		{
			boolean isTimeRestriction = false;
			boolean isWarehouseRestriction = false;
			for (final AbstractRestrictionModel restriction : restrictions)
			{
				if (restriction instanceof CMSTimeRestrictionModel)
				{
					final CMSTimeRestrictionModel timeRestriction = (CMSTimeRestrictionModel) restriction;
					isTimeRestriction = checkCMSTimeRestriction(timeRestriction);
				}
				else if (restriction instanceof SparBannerWarehouseRestrictionModel)
				{
					SparBannerWarehouseRestrictionModel warehouseRestriction = (SparBannerWarehouseRestrictionModel) restriction;
					isWarehouseRestriction = checkBannerWarehouseRestriction(warehouseRestriction, pointOfServiceName);
				}
			}
			if (isWarehouseRestriction && isTimeRestriction)
			{
				final SparBannerComponentWsDTO sparBannerComponentWsDTO = addHomePageBannger(bannerComponent);
				sparBannerComponentListWsDTO.getBanners().add(sparBannerComponentWsDTO);
			}
		}
		else
		{
			for (final AbstractRestrictionModel restriction : restrictions)
			{
				if (restriction instanceof SparBannerWarehouseRestrictionModel)
				{
					final SparBannerWarehouseRestrictionModel warehouseRestriction = (SparBannerWarehouseRestrictionModel) restriction;
					boolean isWarehouseRestriction = checkBannerWarehouseRestriction(warehouseRestriction, pointOfServiceName);
					if (isWarehouseRestriction)
					{
						final SparBannerComponentWsDTO sparBannerComponentWsDTO = addHomePageBannger(bannerComponent);
						sparBannerComponentListWsDTO.getBanners().add(sparBannerComponentWsDTO);
					}
				}
				else if (restriction instanceof CMSTimeRestrictionModel)
				{
					final CMSTimeRestrictionModel timeRestriction = (CMSTimeRestrictionModel) restriction;
					boolean isTimeRestriction = checkCMSTimeRestriction(timeRestriction);
					if (isTimeRestriction)
					{
						final SparBannerComponentWsDTO sparBannerComponentWsDTO = addHomePageBannger(bannerComponent);
						sparBannerComponentListWsDTO.getBanners().add(sparBannerComponentWsDTO);
					}
				}
			}
		}
	}

	private boolean checkCMSTimeRestriction(final CMSTimeRestrictionModel timeRestriction)
	{
		boolean isTimeRestriction = false;
		if (timeRestriction != null)
		{
			final Calendar calender = Calendar.getInstance();
			final Date date = calender.getTime();
			final Date startDate = checkDate(timeRestriction.getActiveFrom(), DATE_FORMAT);
			final Date endDate = checkDate(timeRestriction.getActiveUntil(), DATE_FORMAT);
			final Date currentDate = checkDate(date, DATE_FORMAT);
			if (startDate != null && endDate != null && currentDate != null)
			{
				if (currentDate.getTime() >= startDate.getTime() && endDate.getTime() >= currentDate.getTime())
				{
					isTimeRestriction = true;
				}
			}
		}
		return isTimeRestriction;
	}

	private boolean checkBannerWarehouseRestriction(final SparBannerWarehouseRestrictionModel warehouseRestriction,
			String pointOfServiceName)
	{
		boolean isWarehouseRestriction = false;
		if (warehouseRestriction != null)
		{
			List<WarehouseModel> warehouseList = warehouseRestriction.getWarehouses();
			WarehouseModel warehouse = getWarehouses(pointOfServiceName);
			if (CollectionUtils.isNotEmpty(warehouseList) && warehouse != null)
			{
				if (warehouseList.contains(warehouse))
				{
					isWarehouseRestriction = true;
				}
			}
		}
		return isWarehouseRestriction;
	}

	private SparBannerComponentWsDTO addHomePageBannger(BannerComponentModel bannerComponent)
	{
		final SparBannerComponentWsDTO sparBannerComponentWsDTO = new SparBannerComponentWsDTO();
		if(bannerComponent != null){
			sparBannerComponentWsDTO.setName(bannerComponent.getName());
			sparBannerComponentWsDTO.setHeadline(bannerComponent.getHeadline());
			sparBannerComponentWsDTO.setContent(bannerComponent.getContent());
			sparBannerComponentWsDTO.setUrlLink(bannerComponent.getUrlLink());
			if (bannerComponent.getMedia() != null)
			{
				final ImageData imageData = new ImageData();
				imagePopulator.populate(bannerComponent.getMedia(), imageData);
				sparBannerComponentWsDTO.setImage(imageData);
			}
		}
		return sparBannerComponentWsDTO;
	}
}