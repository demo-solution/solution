package com.spar.hcl.jalo.promotions;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.promotions.jalo.AbstractPromotionRestriction;
import de.hybris.platform.util.Config;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;
/**
 * @author ravindra.kr
 *
 */
public class SparAddressPromotionRestriction extends GeneratedSparAddressPromotionRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger( SparAddressPromotionRestriction.class.getName() );
	private static final String SPAR_HYPERMARKET_INDIA_ADDRESSES = "SPAR Hypermarket India Pvt ltd:Max Hypermarket India Pvt ltd:#39/3 & 44, 2nd Floor:39/3 & 44, 2nd Floor (Above Spar hypermarket) ";
	private static final String SPAR_HO_ADDRESS_PROPERTY_KEY = "spar.office.ho.addresses";
	
	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem( ctx, type, allAttributes );
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.promotions.jalo.AbstractPromotionRestriction#evaluate(de.hybris.platform.jalo.SessionContext, java.util.Collection, java.util.Date, de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@Override
	public RestrictionResult evaluate(final SessionContext ctx, final Collection<Product> arg1, final Date arg2,
			final AbstractOrder order)
	{
		if (order != null && null != order.getDeliveryMode() && !(order.getDeliveryMode().getCode().equalsIgnoreCase("free-standard-shipping")))
		{
			final Collection<AbstractPromotionRestriction> collection = this.getPromotion().getRestrictions(ctx);
			for (final AbstractPromotionRestriction promotionRestriction : collection)
			{
				if (promotionRestriction instanceof SparAddressPromotionRestriction)
				{
					Address address = order.getDeliveryAddress(ctx);
					if(null != address)
					{
   					StringBuffer tempAddress = new StringBuffer();
   					if(null != address.getStreetname())
   					{
   						tempAddress.append(address.getStreetname());
   					}
   					if(null != address.getStreetnumber())
   					{
   						tempAddress.append(address.getStreetnumber());
   					}
   					if(null != address.getAppartment())
   					{
   						tempAddress.append(address.getAppartment());
   					}
   					if(null != address.getBuilding())
   					{
   						tempAddress.append(address.getBuilding());
   					}
   					if(null != address.getDistrict())
   					{
   						tempAddress.append(address.getDistrict());
   					}
   					String deliveryAddress = tempAddress.toString().replaceAll(" ", "").toLowerCase();
   					LOG.info("Delivery Address Associate with Order : "+deliveryAddress);
   					
   					String sparHOAddresses = Config.getString(SPAR_HO_ADDRESS_PROPERTY_KEY , SPAR_HYPERMARKET_INDIA_ADDRESSES);
   					String[] sparHOAddressList = sparHOAddresses.split(":");
   					for(String  sparHOAddressTemp : sparHOAddressList)
   					{
   						String sparHOAddress = sparHOAddressTemp.replaceAll(" ", "").toLowerCase();
   						LOG.info("Address fetched from properties  : "+sparHOAddress);
   						if (deliveryAddress.contains(sparHOAddress))
   						{
   							return RestrictionResult.ALLOW;
   						}
   					}
					}
					/*if (((SparAddressPromotionRestriction) promotionRestriction).getAddresses(ctx).contains(
							order.getDeliveryAddress(ctx)))
					{
						return RestrictionResult.ALLOW;
					}*/
				}
			}
		}
		return RestrictionResult.DENY;
	}
	
	
}
