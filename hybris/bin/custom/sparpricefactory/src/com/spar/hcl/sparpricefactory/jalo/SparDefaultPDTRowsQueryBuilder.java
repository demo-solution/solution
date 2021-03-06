/**
 *
 */
package com.spar.hcl.sparpricefactory.jalo;

import de.hybris.platform.core.PK;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;


/**
 * This class is a custom query builder used for making a custom query for warehouse specific prices
 *
 * @author rohan_c
 *
 */
public class SparDefaultPDTRowsQueryBuilder implements SparPDTRowsQueryBuilder
{

	private final String type;
	private boolean anyProduct;
	private PK productPk;
	private PK productGroupPk;
	private String productId;
	private boolean anyUser;
	private PK userPk;
	private PK userGroupPk;
	private PK warehousePk;

	public SparDefaultPDTRowsQueryBuilder(final String type)
	{
		this.type = Objects.requireNonNull(type);
	}

	@Override
	public PDTRowsQueryBuilder withAnyProduct()
	{
		anyProduct = true;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withProduct(final PK productPk)
	{
		this.productPk = productPk;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withProductGroup(final PK productGroupPk)
	{
		this.productGroupPk = productGroupPk;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withProductId(final String productId)
	{
		this.productId = productId;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withAnyUser()
	{
		anyUser = true;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withUser(final PK userPk)
	{
		this.userPk = userPk;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withUserGroup(final PK userGroupPk)
	{
		this.userGroupPk = userGroupPk;
		return this;
	}

	@Override
	public PDTRowsQueryBuilder withWarehouse(final PK warehousePk)
	{
		this.warehousePk = warehousePk;
		return this;
	}

	/**
	 * This method is used to build a query w.r.t to the params provided along with warehouse
	 */
	@Override
	public PDTRowsQueryBuilder.QueryWithParams build()
	{
		final StringBuilder query = new StringBuilder();
		final com.google.common.collect.ImmutableMap.Builder params = ImmutableMap.builder();
		final Map productParams = getProductRelatedParameters();
		final Map userParams = getUserRelatedParameters();
		final boolean addPricesByProductId = productId != null;
		boolean isUnion = false;
		final boolean matchByProduct = !productParams.isEmpty();
		final boolean matchByUser = !userParams.isEmpty();
		if (!matchByProduct && !matchByUser && !addPricesByProductId)
		{
			return new PDTRowsQueryBuilder.QueryWithParams(
					(new StringBuilder("select {PK} from {")).append(type).append("}").toString(), Collections.EMPTY_MAP, null);
		}
		if (matchByProduct || matchByUser)
		{
			query.append("select {PK} from {").append(type).append("} where ");
			if (matchByProduct)
			{
				query.append("{").append("productMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(productParams.keySet())).append(")");
				params.putAll(productParams);
				if (matchByUser)
				{
					query.append(" and ");
				}
			}
			if (matchByUser)
			{
				query.append("{").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
				params.putAll(userParams);
			}
		}
		if (addPricesByProductId)
		{
			if (matchByProduct || matchByUser)
			{
				query.append("}} UNION {{");
				isUnion = true;
			}
			query.append("select {PK} from {").append(type).append("} where {");
			query.append("productMatchQualifier").append("}=?matchByProductId and {");
			if (null == warehousePk)
			{
				query.append("productId").append("}=?").append("productId");

			}
			else
			{
				query.append("productId").append("}=?").append("productId  and {");
				query.append("warehouse").append("}=?").append("warehouse");
				params.put("warehouse", warehousePk);
			}
			params.put("matchByProductId", Long.valueOf(Europe1PriceFactory.MATCH_BY_PRODUCT_ID));
			params.put("productId", productId);

			if (matchByUser)
			{
				query.append(" and {").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
			}
		}
		StringBuilder resultQuery;
		if (isUnion)
		{
			resultQuery = (new StringBuilder("select x.PK from ({{")).append(query).append("}}) x");
		}
		else
		{
			resultQuery = query;
		}
		return new PDTRowsQueryBuilder.QueryWithParams(resultQuery.toString(), params.build(),null);
	}

	/**
	 * This method is taken up from the decompiled code of Europe1PriceFactory
	 *
	 * @return Map
	 */
	private Map getProductRelatedParameters()
	{
		final com.google.common.collect.ImmutableMap.Builder params = ImmutableMap.builder();
		if (anyProduct)
		{
			params.put("anyProduct", Long.valueOf(Europe1PriceFactory.MATCH_ANY));
		}
		if (productPk != null)
		{
			params.put("product", productPk.getLong());
		}
		if (productGroupPk != null)
		{
			params.put("productGroup", productGroupPk.getLong());
		}
		return params.build();
	}

	/**
	 * This method is taken up from the decompiled code of Europe1PriceFactory
	 *
	 * @return Map
	 */
	private Map getUserRelatedParameters()
	{
		final com.google.common.collect.ImmutableMap.Builder params = ImmutableMap.builder();
		if (anyUser)
		{
			params.put("anyUser", Long.valueOf(Europe1PriceFactory.MATCH_ANY));
		}
		if (userPk != null)
		{
			params.put("user", userPk.getLong());
		}
		if (userGroupPk != null)
		{
			params.put("userGroup", userGroupPk.getLong());
		}
		return params.build();
	}
}
