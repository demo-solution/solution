package com.spar.hcl.jalo.promotions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.remoting.soap.SoapFaultException;

import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;

import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;


public class SparEmployeeDiscountPromotion extends GeneratedSparEmployeeDiscountPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparEmployeeDiscountPromotion.class.getName());

	/*
	 * @Autowired private ModelService modelService;
	 */

	boolean isemployeedsicountenabled = Config.getBoolean("enableEmployeeDiscount", false);

	private final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

	private static final String Invalid_Categories = "spar.employeediscount.invalid.categories";

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem(ctx, type, allAttributes);
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<PromotionResult> evaluate(final SessionContext sessioncontext,
			final PromotionEvaluationContext promotionevaluationcontext)
	{
		final Logger LOG = Logger.getLogger(SparEmployeeDiscountPromotion.class);
		final AbstractOrder order = promotionevaluationcontext.getOrder();
		final List<AbstractOrderEntry> entries = order.getAllEntries();
		//for each order entry- category & promotion will be checked.Orderentry should not be in the invalid category and no promotion should be applied on it.
		final List<PromotionResult> promotionResults = new ArrayList<PromotionResult>();

		final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(sessioncontext, this,
				promotionevaluationcontext.getOrder(), 1.0F);
		final CustomerModel customer = modelService.get(getSession().getUser());
		final Double percentageDiscount = getDiscountpercentage();
		Double totalPrice = Double.valueOf(0.0);
		Double DiscountAmount = Double.valueOf(0.0);
		final Double GraceAmount = getGraceamount();
		LOG.info("SparEmployeeDiscountPromotion");
		LOG.info("SparEmployeeDiscountPromotion");

		LOG.info("SparEmployeeDiscountPromotion Started");
		if (customer.getWhetherEmployee() != null && customer.getWhetherEmployee().booleanValue()
				&& customer.getEmployeeCode() != null && customer.getCOemployeediscountamount() != null
				&& customer.getCOemployeediscountamount() > 0)
		{
			LOG.info("SparEmployeeDiscountPromotion Conditions Satisfied");
			LOG.info("Reading from Local property File");
			LOG.info(isemployeedsicountenabled);
			if (isemployeedsicountenabled)
			{

				try
				{
					if (entries != null)
					{
						for (final AbstractOrderEntry entry : entries)
						{
							if (isValidProductCategory(entry).booleanValue() && isNOPromotionApplied(entry).booleanValue())
							{
								totalPrice = totalPrice.doubleValue() + entry.getBasePrice().doubleValue()
										* entry.getQuantity().longValue();
							}
						}
						if (customer.getCOemployeediscountamount().doubleValue() < 0)
						{
							return promotionResults;
						}
						if (totalPrice.doubleValue() > customer.getCOemployeediscountamount().doubleValue())
						{
							if (totalPrice.doubleValue() > customer.getCOemployeediscountamount().doubleValue()
									+ GraceAmount.doubleValue())
							{
								totalPrice = customer.getCOemployeediscountamount().doubleValue() + GraceAmount.doubleValue();
							}
						}
						DiscountAmount = (totalPrice.doubleValue()) * (percentageDiscount.doubleValue() / 100.00d);
						result.addAction(
								sessioncontext,
								PromotionsManager.getInstance().createPromotionOrderAdjustTotalAction(sessioncontext,
										-DiscountAmount.doubleValue()));
						promotionResults.add(result);
						LOG.info("employeedsicountenabled is fired");
					}
					return promotionResults;
				}
				catch (final SoapFaultException s)
				{
					LOG.info("Soap Fault Exception");
					return promotionResults;
				}

				catch (final WebServiceException w)
				{
					LOG.info("Exception While Accessing WebService");
					return promotionResults;
				}
			}
		}
		return promotionResults;
	}

	Boolean isValidProductCategory(final AbstractOrderEntry entry)
	{
		final CategoryManager catManager = CategoryManager.getInstance();
		//GIVING US THIRD LEVEL category
		final Collection<Category> categories = catManager.getSupercategories(entry.getProduct());
		final Collection<Category> invalidCategories = getCategories();

		if (invalidCategories.contains(categories.iterator().next()))
		{
			LOG.info("false");
			return false;
		}
		LOG.info("true");
		return true;
	}

	// Should send true in case any promotion is applied on the product
	Boolean isNOPromotionApplied(final AbstractOrderEntry entry)
	{
		final AbstractOrderEntryModel entryModel = modelService.get(entry);
		final String currentWarehouseCode = (String) getSession().getAttribute("selectedWarehouseCode");
		final Collection<PriceRowModel> priceRow = entryModel.getProduct().getEurope1Prices();
		LOG.info(currentWarehouseCode);
		for (final PriceRowModel price : priceRow)
		{
			final SparPriceRowModel sparPrice = (SparPriceRowModel) price;

			LOG.info(sparPrice.getWarehouse().getCode());
			if (sparPrice.getWarehouse().getCode().equals(currentWarehouseCode))
			{
				LOG.info("sparPrice");
				LOG.info(sparPrice.getCheckForPromotion().booleanValue());
				return !sparPrice.getCheckForPromotion().booleanValue();
			}
		}
		return false;
	}

	@Override
	public String getResultDescription(final SessionContext sessioncontext, final PromotionResult promotionresult,
			final Locale locale)
	{
		// YTODO Auto-generated method stub

		return null;
	}

}
