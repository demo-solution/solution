package com.spar.hcl.jalo.promotions;

import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.jalo.AbstractPromotionRestriction;
import de.hybris.platform.promotions.jalo.PromotionPriceRow;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


public class SparCategoryItemPromotionRestriction extends GeneratedSparCategoryItemPromotionRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparCategoryItemPromotionRestriction.class.getName());

	private final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
	
	private final CommerceCategoryService commerceCategoryService = (CommerceCategoryService) Registry.getApplicationContext().getBean("commerceCategoryService");
	

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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.promotions.jalo.AbstractPromotionRestriction#evaluate(de.hybris.platform.jalo.SessionContext,
	 * java.util.Collection, java.util.Date, de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@Override
	public RestrictionResult evaluate(final SessionContext ctx, final Collection<Product> arg1, final Date arg2,
			final AbstractOrder order)
	{

		if (order != null)
		{
			final Collection<AbstractPromotionRestriction> collection = this.getPromotion().getRestrictions(ctx);
			for (final AbstractPromotionRestriction promotionRestriction : collection)
			{
				if (promotionRestriction instanceof SparCategoryItemPromotionRestriction)
				{
					double totalCategoryItemPrice = 0.0;
					final Collection<Category> categories = ((SparCategoryItemPromotionRestriction) promotionRestriction).getCategories();
					for (final AbstractOrderEntry orderEntry : order.getEntries())
					{
						final List<String> parentCategoryIdsHierarchy = getParentCategoryIdsHierarchy(orderEntry);
						for (final Category category : categories)
						{
							final CategoryModel restrictedCategoryModel = modelService.get(category);
							if (parentCategoryIdsHierarchy.contains(restrictedCategoryModel.getCode()))
							{
								final AbstractOrderEntryModel entryModel = modelService.get(orderEntry);
								totalCategoryItemPrice = totalCategoryItemPrice + entryModel.getTotalPrice().doubleValue();
							}
						}
					}
					LOG.debug("SparCategoryPromotionRestriction ::: totalCategoryItemPrice = " + totalCategoryItemPrice
							+ " for promotion = " + this.getPromotion().getCode());
					try
					{
						double promotionThresholdPrice = 0.0;
						final Collection<PromotionPriceRow> priceRowModels = (Collection<PromotionPriceRow>) this.getPromotion(ctx)
								.getAllProperties().get("thresholdTotals");
						for (final PromotionPriceRow priceRow : priceRowModels)
						{
							promotionThresholdPrice = priceRow.getPrice().doubleValue();
						}
						LOG.debug("SparCategoryPromotionRestriction ::: promotionThresholdPrice = " + promotionThresholdPrice
								+ " for promotion = " + this.getPromotion().getCode());
						if (totalCategoryItemPrice >= promotionThresholdPrice)
						{
							LOG.debug("SparCategoryPromotionRestriction applied for = " + this.getPromotion().getCode());
							return RestrictionResult.ALLOW;
						}
					}
					catch (final JaloInvalidParameterException e)
					{
						LOG.error("Exception occured during evaluating promotion " + this.getPromotion().getCode()
								+ ", exception message = " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		return RestrictionResult.DENY;

	}

	private List<String> getParentCategoryIdsHierarchy(final AbstractOrderEntry orderEntry)
	{
		final List<String> parentCategoryIdsHierarchy = new ArrayList<String>();
		final AbstractOrderEntryModel entryModel = modelService.get(orderEntry);
		final CommerceCategoryService commerceCategoryService = (CommerceCategoryService) Registry.getApplicationContext().getBean(
				"commerceCategoryService");

		for (final CategoryModel categoryModel : entryModel.getProduct().getSupercategories())
		{
			final Collection<List<CategoryModel>> lists = commerceCategoryService.getPathsForCategory(categoryModel);
			for (final List<CategoryModel> lstCategoryModels : lists)
			{
				for (final CategoryModel model : lstCategoryModels)
				{
					parentCategoryIdsHierarchy.add(model.getCode());
				}
			}
		}
		return parentCategoryIdsHierarchy;
	}

}
