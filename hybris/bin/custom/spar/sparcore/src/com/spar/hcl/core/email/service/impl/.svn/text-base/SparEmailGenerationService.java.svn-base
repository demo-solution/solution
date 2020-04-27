/**
 *
 */
package com.spar.hcl.core.email.service.impl;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class SparEmailGenerationService.
 *
 * method createEmailMessage is overridden for adding attachments of feed error files and customer in CC
 */
public class SparEmailGenerationService extends DefaultEmailGenerationService
{

	/** The Constant CC_ADDRESS. */
	private static final String CC_ADDRESS = "cc_address";
	private static final String CC_ADDRESSES = "cc_addresses";

	/** The Constant ATTACHMENT_FILES. */
	public static final String ATTACHMENT_FILES = "attachementFiles";
	public static final String TOEMAILS = "toEmails";
	public static final String INVENTORYFEED = "INVENTORYFEED";
	public static final String CORRUPTEDPRICEFEED = "CORRUPTEDPRICEFEED";


	@SuppressWarnings("unchecked")
	@Override
	protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody,
			final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		EmailMessageModel emailMessageModel = null;
		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final ArrayList<EmailAddressModel> ccAddresses = new ArrayList<EmailAddressModel>();

		// get the toEmails from context and add it to list on the below condition.
		//  if emailContext is of type sparrmsfeedserormail then  need to add toAddress more in list

		if (emailContext.containsKey(INVENTORYFEED))
		{
			final List<String> listAddresses = (List<String>) emailContext.get(TOEMAILS);

			for (final String addrs : listAddresses)
			{
				toEmails.add(getEmailService().getOrCreateEmailAddressForEmail(addrs, emailContext.getToDisplayName()));
			}
		}

		else
		{
			final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
					emailContext.getToDisplayName());
			toEmails.add(toAddress);
		}

		if (emailContext.containsKey(CORRUPTEDPRICEFEED))
		{
			final List<String> listAddresses = (List<String>) emailContext.get(TOEMAILS);

			for (final String addrs : listAddresses)
			{
				toEmails.add(getEmailService().getOrCreateEmailAddressForEmail(addrs, emailContext.getToDisplayName()));
			}
		}

		else
		{
			final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
					emailContext.getToDisplayName());
			toEmails.add(toAddress);
		}

		final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
				emailContext.getFromDisplayName());

		// Assigning CC addresses
		if (emailContext.get(CC_ADDRESSES) != null)
		{
			final List<String> ccAddressList = (List<String>) emailContext.get(CC_ADDRESSES);

			for (final String cc : ccAddressList)
			{
				final EmailAddressModel ccAddress = getEmailService().getOrCreateEmailAddressForEmail(cc, cc);
				ccAddresses.add(ccAddress);
			}

			emailMessageModel = getEmailService().createEmailMessage(toEmails, ccAddresses, new ArrayList<EmailAddressModel>(),
					fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
		}

		if (emailContext.get(CC_ADDRESS) != null)
		{
			final EmailAddressModel ccAddressModel = (EmailAddressModel) emailContext.get(CC_ADDRESS);
			final EmailAddressModel ccAddress = getEmailService().getOrCreateEmailAddressForEmail(ccAddressModel.getEmailAddress(),
					ccAddressModel.getDisplayName());
			ccAddresses.add(ccAddress);

			emailMessageModel = getEmailService().createEmailMessage(toEmails, ccAddresses, new ArrayList<EmailAddressModel>(),
					fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
		}
		else if (emailContext.get(ATTACHMENT_FILES) != null)
		{
			final List<EmailAttachmentModel> attachments = (List<EmailAttachmentModel>) emailContext.get(ATTACHMENT_FILES);
			emailMessageModel = getEmailService().createEmailMessage(toEmails, ccAddresses, new ArrayList<EmailAddressModel>(),
					fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, attachments);
		}
		else
		{
			emailMessageModel = getEmailService().createEmailMessage(toEmails, ccAddresses, new ArrayList<EmailAddressModel>(),
					fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
		}
		return emailMessageModel;
	}
}
