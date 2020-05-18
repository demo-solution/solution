/**
 *
 */
package com.spar.hcl.storefront.controllers.cms;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.AddOnCategoryCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spar.hcl.storefront.controllers.ControllerConstants;


/**
 * @author Nikhil Kumar
 *
 */

@Controller("AddOnCategoryCarouselComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.AddOnCategoryCarouselComponent)
public class AddOnCategoryCarouselComponentController extends AbstractCMSComponentController<AddOnCategoryCarouselComponentModel>
{
	@Resource(name = "categoryConverter")
	private Converter<CategoryModel, CategoryData> categoryConverter;


	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AddOnCategoryCarouselComponentModel component)
	{
		// YTODO Auto-generated method stub
		final List<CategoryData> categories = new ArrayList<CategoryData>();
		categories.addAll(collectLinkedCategories(component));
		model.addAttribute("title", component.getTitle());
		model.addAttribute("categoryData", categories);

	}

	protected List<CategoryData> collectLinkedCategories(final AddOnCategoryCarouselComponentModel component)
	{
		final List<CategoryData> categories = new ArrayList<CategoryData>();
		for (final CategoryModel categoryModel : component.getCategories())
		{

			//final Collection<MediaModel> thumbnailImagesForCategory = categoryModel.getThumbnails();

			//code for coversion to data
			final CategoryData categoryData = getCategoryConverter().convert(categoryModel);
			categories.add(categoryData);
		}

		return categories;
	}

	/**
	 * @return
	 */
	private Converter<CategoryModel, CategoryData> getCategoryConverter()
	{
		// YTODO Auto-generated method stub
		return categoryConverter;
	}


}
