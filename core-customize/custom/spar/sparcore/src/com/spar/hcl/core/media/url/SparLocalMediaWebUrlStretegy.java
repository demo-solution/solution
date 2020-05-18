/**
 *
 */
package com.spar.hcl.core.media.url;

import de.hybris.platform.core.Registry;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.url.impl.LocalMediaWebURLStrategy;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.MediaUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Preconditions;


/**
 * @author pavan.sr
 *
 */
public class SparLocalMediaWebUrlStretegy extends LocalMediaWebURLStrategy
{

	public static final String CONTEXT_PARAM_DELIM = "|";
	public static final String NO_CTX_PART_MARKER = "-";
	public static final String CONTEXT_PARAM = "context";
	public static final String ATTACHEMENT_PARAM = "attachment";
	public static final String MEDIA_LEGACY_PRETTY_URL = "media.legacy.prettyURL";
	@Value("${mediaweb.webroot:/medias}")
	private String mediaWebRoot;
	private final String CUSTOM_MEDIA_WEBROOT = "custom.mediaweb.webroot";
	private boolean prettyUrlEnabled;

	@Override
	public String getUrlForMedia(final MediaStorageConfigService.MediaFolderConfig config, final MediaSource mediaSource)
	{
		Preconditions.checkArgument(config != null, "Folder config is required to perform this operation");
		Preconditions.checkArgument(mediaSource != null, "MediaSource is required to perform this operation");

		return assembleUrl(config.getFolderQualifier(), mediaSource);
	}

	private String assembleUrl(final String folderQualifier, final MediaSource mediaSource)
	{
		String result = "";
		if ((!GenericValidator.isBlankOrNull(folderQualifier)) && (!GenericValidator.isBlankOrNull(mediaSource.getLocation())))
		{
			if (this.prettyUrlEnabled)
			{
				result = assembleLegacyURL(folderQualifier, mediaSource);
			}
			else
			{
				result = assembleURLWithMediaContext(folderQualifier, mediaSource);
			}
		}
		return result;
	}

	private String assembleLegacyURL(final String folderQualifier, final MediaSource mediaSource)
	{
		final StringBuilder sb = new StringBuilder(MediaUtil.addTrailingFileSepIfNeeded(getMediaWebRootContext()));
		sb.append("sys_").append(getTenantId()).append("/");
		sb.append(folderQualifier).append("/");
		String realFileName = getRealFileNameForMedia(mediaSource);
		if (realFileName == null)
		{
			sb.append(mediaSource.getLocation());
		}
		else
		{
			final String location = mediaSource.getLocation();
			final int lastDotIndex = location.lastIndexOf('.');
			final String basePath = location.substring(0, lastDotIndex);
			final String fileExtension = location.substring(lastDotIndex);
			final int lastDotIndexForRealFileName = realFileName.lastIndexOf('.');
			if (lastDotIndexForRealFileName != -1)
			{
				realFileName = realFileName.substring(0, lastDotIndexForRealFileName);
			}
			sb.append(basePath).append("/").append(realFileName).append(fileExtension);
		}
		return sb.toString();
	}

	private String assembleURLWithMediaContext(final String folderQualifier, final MediaSource mediaSource)
	{
		final StringBuilder builder = new StringBuilder(MediaUtil.addTrailingFileSepIfNeeded(getMediaWebRootContext()));

		final String realFilename = getRealFileNameForMedia(mediaSource);
		if (realFilename != null)
		{
			builder.append(realFilename);
		}
		builder.append("?").append("context").append("=");
		builder.append(assembleMediaLocationContext(folderQualifier, mediaSource));
		return builder.toString();
	}

	private String getRealFileNameForMedia(final MediaSource mediaSource)
	{
		final String realFileName = mediaSource.getRealFileName();
		return StringUtils.isNotBlank(realFileName) ? MediaUtil.normalizeRealFileName(realFileName) : null;
	}


	@Override
	public String getMediaWebRootContext()
	{
		final String mediaCustomUri = Config.getString("custom.mediaweb.webroot", "custom.mediaweb.webroot");
		return StringUtils.isBlank(this.mediaWebRoot) ? "/medias" : mediaCustomUri;
	}

	private String assembleMediaLocationContext(final String folderQualifier, final MediaSource mediaSource)
	{
		final StringBuilder builder = new StringBuilder(getTenantId());
		builder.append("|").append(folderQualifier.replace("|", ""));
		builder.append("|").append(mediaSource.getSize());
		builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getMime()));
		builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getLocation()));

		builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getLocationHash()));

		return new Base64(-1, null, true).encodeAsString(builder.toString().getBytes());
	}

	private String getCtxPartOrNullMarker(final String ctxPart)
	{
		return StringUtils.isNotBlank(ctxPart) ? ctxPart : "-";
	}

	@Override
	protected String getTenantId()
	{
		return Registry.getCurrentTenantNoFallback().getTenantID();
	}


	@Override
	public String getDownloadUrlForMedia(final MediaStorageConfigService.MediaFolderConfig config, final MediaSource mediaSource)
	{
		final StringBuilder url = new StringBuilder(getUrlForMedia(config, mediaSource));
		if (this.prettyUrlEnabled)
		{
			url.append("?");
		}
		else
		{
			url.append("&");
		}
		url.append("attachment").append("=").append("true");
		return url.toString();
	}


	@Override
	public void setPrettyUrlEnabled(final boolean prettyUrlEnabled)
	{
		this.prettyUrlEnabled = prettyUrlEnabled;
	}
}
