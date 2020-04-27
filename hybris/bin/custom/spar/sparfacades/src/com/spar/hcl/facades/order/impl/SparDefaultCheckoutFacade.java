/**
 *
 */
package com.spar.hcl.facades.order.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.order.data.PaymentModeData;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.util.Config;


/**
 * This class is a Facade class used in SPAR for custom logic related to Delivery Slot and Payment modes
 *
 * @author rohan_c
 *
 */
public class SparDefaultCheckoutFacade extends DefaultCheckoutFacade
{

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;
	private SessionService sessionService;
	private PaymentModeService paymentModeService;
	private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;
	private ProductService productService;
	private CommerceCartService commerceCartService;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private PointOfServiceService pointOfServiceService;

	@Autowired
	private StoreFinderFacadeInterface storeFinderFacade;
	@Autowired
	private ModelService modelService;


	private static final Logger LOG = Logger.getLogger(SparDefaultCheckoutFacade.class);

	/**
	 * This method is used to save Delivery Slot and Date in cart model
	 *
	 * @param cartModel
	 */
	public void saveDeliverySlotAndDate(final Date date, final DeliverySlotModel deliverySlotModel)
	{
		final CartModel cartModel = getCart();
		cartModel.setSlotDeliveryDate(date);
		cartModel.setOrderDeliverySlot(deliverySlotModel);
		final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd yyyy");
		final String deliverySlot = dateFormatter.format(date);
		final List<String> allDeliverySlots = new ArrayList<String>();
		allDeliverySlots.add(deliverySlot + " - " + deliverySlotModel.getSlotDescription());
		cartModel.setAllDeliverySlots(allDeliverySlots);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
		final String deliveryDate = dateFormat.format(date);
		cartModel.setDeliveryDate(deliveryDate);
		cartModel.setDeliverySlot(deliverySlotModel.getSlotDescription());
		getModelService().save(cartModel);
		/*
		 * final String[] splitSlot = seletedSlot.split("--"); cartModel.setDeliveryDate(splitSlot[0]);
		 * cartModel.setDeliverySlot(splitSlot[1]);
		 */
	}

	/**
	 * This method is used to get all the supported Payment mode in SPAR
	 *
	 * @return List<PaymentModeData>
	 */
	public List<PaymentModeData> getSupportedPaymentModeTypes()
	{
		final Collection<PaymentModeModel> paymentModeDataList = getPaymentModeService().getAllPaymentModes();
		return Converters.convertAll(paymentModeDataList, getPaymentModeConverter());
	}

	/**
	 * This method is used to get the PaymentModeData for the specific payment mode code
	 *
	 * @param code
	 * @return List<PaymentModeData>
	 */
	public List<PaymentModeData> getPaymentModeDataForCode(final String code)
	{
		final Collection<PaymentModeModel> paymentModeDataList = new ArrayList<PaymentModeModel>();
		paymentModeDataList.add(getPaymentModeService().getPaymentModeForCode(code));
		return Converters.convertAll(paymentModeDataList, getPaymentModeConverter());
	}

	/**
	 * This method is used to get the PaymentModeModel for the specific payment mode code
	 *
	 * @param code
	 * @return PaymentModeModel
	 */
	public PaymentModeModel getPaymentModeForCode(final String code)
	{
		final PaymentModeModel model = getPaymentModeService().getPaymentModeForCode(code);
		return model;
	}

	/*
	 * Getter for CartData with paymentMode
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#getCheckoutCart()
	 */
	@Override
	public CartData getCheckoutCart()
	{
		final CartData cartData = super.getCheckoutCart();

		if (cartData != null)
		{
			cartData.setPaymentMode(getPaymentMode());
		}
		if (null != cartData && null == cartData.getSavings())
		{
			populateCartSavings(cartData);
		}
		cartData.setBalanceDue(cartData.getTotalPrice());
		return cartData;
	}

	/**
	 * This method is used to set saving in OrderData
	 *
	 * @param cartData
	 * @throws ConversionException
	 */
	public void populateCartSavings(final CartData cartData) throws ConversionException
	{
		final CartModel cart = getCart();
		if (null != cart)
		{
			final String orderPOSName = cart.getPointOfService();
			final WarehouseData cartWarehouseData = storeFinderFacade.getWarehouse(orderPOSName);
			BigDecimal orderSavings = BigDecimal.ZERO;
			for (final AbstractOrderEntryModel model : cart.getEntries())
			{
				final ProductModel product = model.getProduct();
				if (null != product.getEurope1Prices() && !product.getEurope1Prices().isEmpty()
						&& product.getEurope1Prices().iterator().hasNext())
				{
					final Collection<PriceRowModel> sparPriceCollection = product.getEurope1Prices();
					for (final PriceRowModel price : sparPriceCollection)
					{
						final String priceWareHouseCode = ((SparPriceRowModel) price).getWarehouse().getCode();
						if (StringUtils.equalsIgnoreCase(priceWareHouseCode, cartWarehouseData.getCode()))
						{
							final Double unitMRP = ((SparPriceRowModel) price).getUnitMRP();
							final Double unitCSP = price.getPrice();
							if (null != unitMRP && null != unitCSP)
							{
								final BigDecimal mrp = new BigDecimal(unitMRP.toString());
								final BigDecimal csp = new BigDecimal(unitCSP.toString());
								final BigDecimal quantity = new BigDecimal(model.getQuantity().toString());
								orderSavings = orderSavings.add(mrp.subtract(csp).multiply(quantity));
							}
						}
					}
				}
			}
			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderSavings,
					getCommonI18NService().getCurrentCurrency());
			cartData.setSavings(priceData);
		}

	}

	/**
	 * Getter for paymentMode
	 *
	 * @return
	 */
	protected PaymentModeData getPaymentMode()
	{
		final CartModel cart = getCart();
		if (null != cart && null != cart.getPaymentMode())
		{
			final PaymentModeModel paymentMode = cart.getPaymentMode();
			return getPaymentModeConverter().convert(paymentMode);
		}

		return null;
	}

	/**
	 * This method modify the cartentry with POS
	 *
	 * @author tanveers
	 */
	public CartModificationData modifyCart(final String code, final long entryNumber, final PointOfServiceData pointOfService)
			throws CommerceCartModificationException
	{
		if (pointOfService == null)
		{
			return modifyCart(code, entryNumber);
		}
		else
		{
			final CartModel cartModel = getCartService().getSessionCart();
			final ProductModel product = getProductService().getProductForCode(code);
			final PointOfServiceModel pointOfServiceModel = getPointOfServiceService()
					.getPointOfServiceForName(pointOfService.getName());
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setProduct(product);
			parameter.setPointOfService(pointOfServiceModel);
			parameter.setEntryNumber(entryNumber);
			final CommerceCartModification modification = getCommerceCartService().updatePointOfServiceForCartEntry(parameter);

			return getCartModificationConverter().convert(modification);
		}
	}

	/**
	 * This method modify the cartentry with homedelivery
	 *
	 * @author tanveers
	 */
	public CartModificationData modifyCart(final String code, final long entryNumber) throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final CartModel cartModel = getCartService().getSessionCart();

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(product);
		parameter.setEntryNumber(entryNumber);
		final CommerceCartModification modification = getCommerceCartService().updateToShippingModeForCartEntry(parameter);

		return getCartModificationConverter().convert(modification);
	}

	public void triggerSMS(String msg, final OrderData order)
	{
		LOG.info("Entered into triggerSMS :::::::: ");
		URL url = null;
		final String orderId = order.getCode();
		final String store = (String) sessionService.getAttribute("selectedStore");
		String destmobnum = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.name");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");

		if (msg.contains("XXXX"))
		{
			msg = msg.replaceAll("XXXX", orderId);
		}

		if (msg.contains("YYYY"))
		{
			msg = msg.replaceAll("YYYY", store);
		}

		LOG.info("orderId :::: " + orderId + " , store :::: " + store);


		destmobnum = order.getDeliveryAddress().getPhone();
		final String USER_AGENT = "Mozilla/5.0";
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");


		try
		{
			final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
					+ URLEncoder.encode(msg, "UTF-8");

			LOG.info(" SMS URL to Mgage gateway *********** " + mGageURL);
			url = new URL(mGageURL);
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			final int resCode = urlConnection.getResponseCode();
			final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			final StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			LOG.info("response messege for SMS Request" + response);
			LOG.info(" resCode Message *********** " + resCode + "Message" + urlConnection.getResponseMessage());
			urlConnection.disconnect();
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
			LOG.error("Connection Failed", ex);
		}
	}

	/**
	 * Getter
	 *
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Setter
	 *
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Getter
	 *
	 * @return the paymentModeService
	 */
	public PaymentModeService getPaymentModeService()
	{
		return paymentModeService;
	}

	/**
	 * Setter
	 *
	 * @param paymentModeService
	 *           the paymentModeService to set
	 */
	public void setPaymentModeService(final PaymentModeService paymentModeService)
	{
		this.paymentModeService = paymentModeService;
	}

	/**
	 * Getter
	 *
	 * @return the paymentModeConverter
	 */
	public Converter<PaymentModeModel, PaymentModeData> getPaymentModeConverter()
	{
		return paymentModeConverter;
	}

	/**
	 * Setter
	 *
	 * @param paymentModeConverter
	 *           the paymentModeConverter to set
	 */
	public void setPaymentModeConverter(final Converter<PaymentModeModel, PaymentModeData> paymentModeConverter)
	{
		this.paymentModeConverter = paymentModeConverter;
	}

	/**
	 * @return the storeFinderServiceInterface
	 */
	public StoreFinderServiceInterface getStoreFinderServiceInterface()
	{
		return storeFinderServiceInterface;
	}

	/**
	 * @param storeFinderServiceInterface
	 *           the storeFinderServiceInterface to set
	 */
	public void setStoreFinderServiceInterface(final StoreFinderServiceInterface storeFinderServiceInterface)
	{
		this.storeFinderServiceInterface = storeFinderServiceInterface;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the commerceCartService
	 */
	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	/**
	 * @return the cartModificationConverter
	 */
	public Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	/**
	 * @param cartModificationConverter
	 *           the cartModificationConverter to set
	 */
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	/**
	 * @return the pointOfServiceService
	 */
	public PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	/**
	 * @param pointOfServiceService
	 *           the pointOfServiceService to set
	 */
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

}