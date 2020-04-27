/**
 *
 */
package com.spar.hcl.core.helper;

/**
 * @author valechar
 *
 */

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.core.constants.CommonConstants;






/**
 * The Class ContentFormatHelper.
 */
@SuppressWarnings("PMD")
public class ContentFormatHelper
{

	/** Reference for logger instance. */
	protected static final Logger LOG = Logger.getLogger(ContentFormatHelper.class);

	/** Pattern string for non-secure media. */
	private static final Pattern MEDIA_PLACEHOLDER_PATTERN = Pattern.compile(Config
			.getParameter(CommonConstants.PLACE_HOLDER_MEDIA));

	/** Pattern string for secure media. */
	private static final Pattern SECURE_MEDIA_PLACEHOLDER_PATTERN = Pattern.compile(Config
			.getParameter(CommonConstants.PLACE_HOLDER_SECURE_MEDIA));

	/** Place holder for static assets url. */
	private static final String STATIC_ASSETS_URL = Config.getParameter(CommonConstants.CONFIG_SITE_STATIC_ASSETS_URL);

	/** Place holder for secure static assets url. */
	private static final String STATIC_ASSETS_SECURE_URL = Config
			.getParameter(CommonConstants.CONFIG_SITE_STATIC_ASSETS_SECURE_URL);

	/** Pattern matching the translated non secure media path. */
	private static final Pattern MEDIA_PATH_PATTERN = Pattern.compile(STATIC_ASSETS_URL
			+ Config.getParameter(CommonConstants.SITE_MEDIA_PATH_PATTERN));

	/** Pattern matching the non secure media path without the static assets URL. */
	private static final Pattern RELATIVE_MEDIA_PATH_PATTERN = Pattern.compile(Config
			.getParameter(CommonConstants.SITE_MEDIA_PATH_PATTERN));

	/** Pattern matching the translated secure media path. */
	private static final Pattern SECURE_MEDIA_PATH_PATTERN = Pattern.compile(STATIC_ASSETS_SECURE_URL
			+ Config.getParameter(CommonConstants.SITE_MEDIA_PATH_PATTERN));

	/** Reference for ModelService. */
	protected ModelService modelService;

	/** Reference for media service. */
	protected MediaService mediaService;

	/** Reference for media service. */
	protected FlexibleSearchService flexibleSearchService;

	/**
	 * Replace media place holder.
	 *
	 * @param content
	 *           the content
	 * @param catalogVersion
	 *           the catalog version
	 * @return String
	 */
	public String replaceMediaPlaceHolder(final String content, final CatalogVersion catalogVersion)
	{
		return replaceMediaPlaceHolder(content, (CatalogVersionModel) modelService.get(catalogVersion));
	}


	/**
	 * Replace media place holder.
	 *
	 * @param content
	 *           the content
	 * @param catalogVersion
	 *           the catalog version
	 * @return String
	 */
	public String replaceMediaPlaceHolder(final String content, final CatalogVersionModel catalogVersion)
	{
		// return if we don't have content
		if (StringUtils.isBlank(content))
		{
			return content;
		}
		Matcher matcher = MEDIA_PLACEHOLDER_PATTERN.matcher(content);
		boolean secureContent = false;
		boolean result = matcher.find();
		if (!result)
		{
			matcher = SECURE_MEDIA_PLACEHOLDER_PATTERN.matcher(content);
			result = matcher.find();
			secureContent = true;
		}
		// While we have matches, iterate and replace
		if (result)
		{
			final StringBuffer buffer = new StringBuffer();
			do
			{
				final String mediaCode = matcher.group(1);
				try
				{
					final MediaModel media = mediaService.getMedia(catalogVersion, mediaCode);
					if (media != null)
					{
						final StringBuilder builder = new StringBuilder();
						if (secureContent)
						{
							builder.append(STATIC_ASSETS_SECURE_URL);
						}
						else
						{
							builder.append(STATIC_ASSETS_URL);
						}
						builder.append(media.getUrl());
						matcher.appendReplacement(buffer, builder.toString());
					}
				}
				catch (final UnknownIdentifierException e)
				{
					LOG.error("Could not find media with code [" + mediaCode + "]");
				}
				catch (final AmbiguousIdentifierException e)
				{
					LOG.error("Multiple media items found for code [" + mediaCode + "]");
				}
				finally
				{
					result = matcher.find();
				}
			}
			while (result);
			matcher.appendTail(buffer);
			return buffer.toString();
		}
		return content;
	}


	/**
	 * Substitute media place holder.
	 *
	 * @param content
	 *           the content
	 * @return String
	 */
	public String substituteMediaPlaceHolder(final String content)
	{
		// return if we don't have content
		if (StringUtils.isBlank(content))
		{
			return content;
		}
		Matcher matcher = MEDIA_PATH_PATTERN.matcher(content);
		boolean secureContent = false;
		boolean result = matcher.find();
		if (!result)
		{
			matcher = SECURE_MEDIA_PATH_PATTERN.matcher(content);
			result = matcher.find();
			if (result)
			{
				secureContent = true;
			}
			else
			{
				matcher = RELATIVE_MEDIA_PATH_PATTERN.matcher(content);
				result = matcher.find();
			}
		}
		// While we have matches, iterate and replace
		if (result)
		{
			final StringBuffer stringBuffer = new StringBuffer();
			do
			{
				final String mediaPK = matcher.group(2);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("media PK [" + mediaPK + "]");
				}
				try
				{
					final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
							"select {PK} from {Media} where {dataPK} = ?dataPK");
					flexibleSearchQuery.addQueryParameter(MediaModel.DATAPK, mediaPK);
					final SearchResult<MediaModel> mediaResults = flexibleSearchService.search(flexibleSearchQuery);
					// if we have found a media the substitute
					if (mediaResults.getCount() > 0)
					{
						final MediaModel media = mediaResults.getResult().get(0);
						if (secureContent)
						{
							matcher.appendReplacement(stringBuffer, "{secure:media:" + media.getCode() + "}");
						}
						else
						{
							matcher.appendReplacement(stringBuffer, "{media:" + media.getCode() + "}");
						}
					}
				}
				catch (final Exception e)
				{
					LOG.error("Could not find media with code [" + mediaPK + "]");
				}
				finally
				{
					result = matcher.find();
				}
			}
			while (result);
			matcher.appendTail(stringBuffer);
			return stringBuffer.toString();
		}
		// If no matches, nothing to do
		return content;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Sets the media service.
	 *
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * Sets the flexible search service.
	 *
	 * @param flexibleSearchService
	 *           the new flexible search service
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
