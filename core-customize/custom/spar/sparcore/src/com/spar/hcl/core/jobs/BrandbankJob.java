/*package com.spar.hcl.core.jobs;


import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.spar.hcl.core.brandbank.ws.DataX0020Extract;
import com.spar.hcl.core.brandbank.ws.DataX0020ExtractSoap;
import com.spar.hcl.core.brandbank.ws.GetUnsentProductDataResponse;
import com.spar.hcl.core.brandbank.ws.GetUnsentProductDataResponse.GetUnsentProductDataResult;
import com.spar.hcl.jalo.bmecat2csv.SparBMECat2CSVObjectProcessor;
import com.spar.hcl.parser.SparBMECatParser;


 *//**
 *
 *
 */
/*
 * 
 * public class BrandbankJob extends AbstractJobPerformable<CronJobModel> {
 * 
 * @Override public PerformResult perform(final CronJobModel cronJob) { // YTODO Auto-generated method stub
 * GetUnsentProductDataResult bbData = null; final GetUnsentProductDataResponse result = new
 * GetUnsentProductDataResponse(); try {
 * 
 * final Date date = new Date();
 * 
 * final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
 * 
 * final SparBMECat2CSVObjectProcessor proc = new SparBMECat2CSVObjectProcessor(); final SparBMECatParser bmecatParser =
 * new SparBMECatParser(proc);
 * 
 * final File file = new File(proc.getTempFolderPath() + File.separator + "Brandbank_" + dateFormat.format(date) +
 * ".xml");
 * 
 * final DataX0020Extract bbService = new DataX0020Extract();
 * 
 * final DataX0020ExtractSoap bbRequest = bbService.getDataX0020ExtractSoap(); bbData =
 * bbRequest.getUnsentProductData();
 * 
 * result.setGetUnsentProductDataResult(bbData);
 * 
 * 
 * final JAXBContext context = JAXBContext.newInstance(GetUnsentProductDataResponse.class); final Marshaller m =
 * context.createMarshaller(); //for pretty-print XML in JAXB m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
 * Boolean.TRUE); // Write to File m.marshal(result, file);
 * 
 * try { bmecatParser.parse(file); proc.finish(); proc.getResultZipMedia(); } catch (final Exception e) {
 * System.out.println("import exception in fileUpload.jsp -->  " + e.getMessage()); e.printStackTrace(); }
 * 
 * 
 * if (bbData != null) { final String messageGuid = "8EB942BC-198F-4E2D-A258-936F0C072301";
 * bbRequest.acknowledgeMessage(messageGuid); }
 * 
 * 
 * } catch (final Exception e) { System.out.println("Exception" + e.getMessage()); }
 * 
 * return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED); } }
 */
