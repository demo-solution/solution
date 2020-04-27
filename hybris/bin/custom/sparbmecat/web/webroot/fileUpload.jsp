<!-- This JSP is used for uploading BB XML to generate CSV -->

<%@ include file="./inc/head.jspf"%>
<%@ taglib prefix="c" uri="/WEB-INF/tlds/c-rt.tld"%>

<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.xml.sax.InputSource"%>

<%@page import="de.hybris.platform.jalo.media.Media"%>
<%@page import="de.hybris.platform.impex.jalo.*"%>
<%@page import="de.hybris.platform.impex.constants.*"%>
<%@page import="de.hybris.platform.impex.jalo.media.*"%>
<%@page import="de.hybris.platform.impex.jalo.ImpExManager"%>
<%@page import="com.spar.hcl.parser.SparBMECatParser"%>
<%@page import="com.spar.hcl.jalo.bmecat2csv.SparBMECat2CSVObjectProcessor"%>

<div class="absatz"><a href="<%=response.encodeURL("/sparbmecat")%>">[Back
to SparBMECat import]</a></div>
<div class="absatz">&nbsp;</div>
<div class="absatz">


<h1>SPARBMECat transforming</h1>
<%
	String encoding = "utf-16"; //request.getParameter("encoding");
	String script = "";
	String mediaPK = "";
	String resourceMediaPK = "";
	Map<String, String> bmecatHeader = new HashMap<String, String>();
	String scriptConstants = "";

	JspContext jspc = new JspContext( out, request, response );

	System.out.println( "Content Type =" + request.getContentType() );

	ServletFileUpload upload = new ServletFileUpload();
	FileItemIterator iter = upload.getItemIterator( request );

	//only one xml file and one zip file are allowed.
	FileItemStream[] fis = new FileItemStream[2];
	int i = 0;
	User u = null;
	
	if(i == 0)
	{
		fis[0] = iter.next();
		i++;
		if( !fis[0].isFormField() )
		{
			if( !jaloSession.getUser().isAdmin() )
			{
				u = jaloSession.getUser();
				jaloSession.setUser( UserManager.getInstance().getAdminEmployee() );
			}
			MediaDataTranslator.setMediaDataHandler( new DefaultMediaDataHandler() );
			String importFileName = fis[0].getName();

			//only *.xml files can be transformed to .csv files
			if(importFileName.toLowerCase().endsWith(".xml"))
			{
				//parsing BB.xml
				SparBMECat2CSVObjectProcessor proc = new SparBMECat2CSVObjectProcessor();
				SparBMECatParser bmecatParser = new SparBMECatParser(proc);
				try
				{
					bmecatParser.parse(new InputSource(fis[0].openStream()));
					proc.finish();
					Media media = proc.getResultZipMedia();
					if(media == null)
					{
						out.print("media is null.");
					}
					else
					{
						out.print("<a href=\"" + media.getDownloadURL()+ "\">zip_file </a><br/>");
					}
					out.print("<br/>Transformation of BB XML: Successful<br/>");
				}
				catch (Exception e) 
				{
					System.out.println("import exception in fileUpload.jsp -->  " + e.getMessage());
					e.printStackTrace();
				}
				out.print("<br/>File [" + importFileName + "] is transformed to csv files successfully.<br/>");
			}
			else
			{
				out.print("<br/><font color='red'>The first file [" + importFileName + "] is not valid.</font><br/>");
				out.print("<font color='red'>Only the Brand Bank file with extension (.xml) can be imported.</font><br/>");
			}
		}
	}
	MediaDataTranslator.unsetMediaDataHandler( );	
	if( u != null )
	{
		jaloSession.setUser( u );
	}

%>
<p><br />
<b>Note: Please also check the application server output if errors occured.</b>
<p>

<%@ include file="./inc/tail.jspf"%>
