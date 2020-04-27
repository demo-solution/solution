/**
 *
 */
package com.spar.hcl.storefront.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;




/**
 * @author pramod-kuma
 *
 */
@Component
public class MMIUtil
{
	AuthKey authKey = new AuthKey();

	public Object getAutoSuggest(final String keywords, final String reflocation)
	{

		Object result = null;
		HttpURLConnection con = null;
		try
		{

			if (authKey.getLicKey() == null)
			{
				authKey = authenticateUser(authKey);
			}
			final StringBuilder str = new StringBuilder();
			str.append("https://atlas.mapmyindia.com/api/places/search/json?query=");
			str.append(URLEncoder.encode(keywords, "UTF-8"));
			str.append("&tokenizeAddress=");
			//str.append("&location=").append(URLEncoder.encode(reflocation, "UTF-8"));

			final URL urls = new URL(str.toString());

			con = (HttpURLConnection) urls.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", authKey.getLicKey());

			result = prepareAutoSuggestData(con);

		}
		catch (final Exception e)
		{
			if (e.getMessage().contains("401"))
			{
				authKey = authenticateUser(authKey);
				try
				{
					result = prepareAutoSuggestData(con);
				}
				catch (final Exception ex)
				{
					System.out.println("second attempt : " + ex.getMessage());
				}

			}
		}
		return result;
	}

	private String prepareAutoSuggestData(final HttpURLConnection con)
	{

		final List<AutoSuggest> autoSuggestResponse = new LinkedList<AutoSuggest>();
		final Gson gson = new Gson();
		try
		{

			final JSONObject resp = (JSONObject) getPOI(con);
			if (resp != null)
			{
				final JSONArray ja = (JSONArray) resp.get("suggestedLocations");// jp.parse(sb.toString());
				if (ja.size() == 0)
				{
					System.out.println("[cityAutoSuggest] No result returned.");
					return gson.toJson(autoSuggestResponse);
				}


				final Iterator it = ja.iterator();
				while (it.hasNext())
				{
					final JSONObject o = (JSONObject) it.next();
					final JSONObject jsObj = (JSONObject) o.get("addressTokens");

					final AutoSuggest autoSuggest = new AutoSuggest();
					autoSuggest.setAddr(String.valueOf(o.get("placeName")));
					autoSuggest.setX(String.valueOf(o.get("longitude")));
					autoSuggest.setY(String.valueOf(o.get("latitude")));
					autoSuggest.setPostalCode(String.valueOf(jsObj.get("pincode")));
					autoSuggest.setfAddress(String.valueOf(o.get("placeAddress")));
					autoSuggest.setCity(String.valueOf(jsObj.get("city")));
					autoSuggest.setState(String.valueOf(jsObj.get("state")));
					autoSuggest.setLocality(String.valueOf(jsObj.get("locality")));
					autoSuggestResponse.add(autoSuggest);
				}
			}

		}
		catch (final Exception ex)
		{
			System.out.println("[cityAutoSuggest] Exception : " + ex.getMessage());
			ex.printStackTrace();
		}
		return gson.toJson(autoSuggestResponse);

	}

	public static AuthKey authenticateUser(final AuthKey authKey)
	{
		String keys = "";

		try
		{
			final String url = "https://outpost.mapmyindia.com/api/security/oauth/token?grant_type=client_credentials"
					+ "&client_id=bcC3ERYz9pfse2yhmbdasXQ006bYsY2azBycQfrFqFKDfqUjyyUOrUjH-pYTPbQ3" + "&client_secret="
					+ URLEncoder.encode("yesLYGtFPg19oA-PDmRGkOlXplTIgpx9gfd0IlncCd8vh9F5klv_NCPr3sS07l64", "UTF-8");

			//System.out.println("Auth API URL : " + url.toString());

			/*
			 * final HttpURLConnection conn = (HttpURLConnection) url.openConnection(); conn.setDoOutput(true);
			 * conn.setRequestMethod("POST"); conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 * conn.setRequestProperty("Content-Length", "500000"); final BufferedReader br = new BufferedReader(new
			 * InputStreamReader((conn.getInputStream())));
			 */
			final String output = sendPostRequest(url, new HashMap<String, String>());
			/* while ((output = br.readLine()) != null) */
			//{
			//System.out.println("output : " + output);
			final JSONParser jsparser = new JSONParser();
			final JSONObject jsonObject = (JSONObject) jsparser.parse(output);
			keys = (String) jsonObject.get("token_type") + " " + (String) jsonObject.get("access_token");
			authKey.setLicKey(keys);
			//}
			//System.out.println("keys :::::::: " + authKey.getLicKey());

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			keys = null;
		}

		return authKey;
	}

	private Object getPOI(final HttpURLConnection con) throws Exception
	{
		final StringBuilder content = new StringBuilder();
		try
		{

			final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
			{
				content.append(inputLine);
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return JSONValue.parse(content.toString());
	}

	public static String sendPostRequest(final String requestURL, final HashMap<String, String> postDataParams)
	{

		URL url;
		String response = "";
		try
		{
			url = new URL(requestURL);

			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);


			final OutputStream os = conn.getOutputStream();
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			final int responseCode = conn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK)
			{
				final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				response = br.readLine();
			}
			else
			{
				response = "Error Registering";
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return response;
	}


	private static String getPostDataString(final HashMap<String, String> params) throws UnsupportedEncodingException
	{
		final StringBuilder result = new StringBuilder();
		boolean first = true;
		for (final Map.Entry<String, String> entry : params.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				result.append("&");
			}

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}
}
