/**
 *
 */
package com.spar.hcl.url.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * @author manish_ku
 *
 */
public class SparDefaultProductModelUrlResolver extends DefaultProductModelUrlResolver
{
	@Override
	protected String resolveInternal(final ProductModel source)
	{
		final ProductModel variantProduct = source;

		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		String url = getPattern();

		if (currentBaseSite != null && url.contains("{baseSite-uid}"))
		{
			url = url.replace("{baseSite-uid}", currentBaseSite.getUid());
		}
		if (url.contains("{category-path}"))
		{
			url = url.replace("{category-path}", buildPathString(getCategoryPath(variantProduct)));
		}
		if (url.contains("{product-name}"))
		{
			url = url.replace("{product-name}", urlSafe(variantProduct.getName()));
		}
		if (url.contains("{product-code}"))
		{
			url = url.replace("{product-code}", source.getCode());
		}

		return url;
	}

}
