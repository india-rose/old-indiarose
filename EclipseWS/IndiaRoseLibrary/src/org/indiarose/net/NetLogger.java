package org.indiarose.net;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.indiarose.lib.ActionLog;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class NetLogger {

	private static String convertTimestamp(String time)
	{
		long timestamp = Long.parseLong(time);
		
		Calendar calendar = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();
		calendar.add(Calendar.MILLISECOND, tz.getOffset(timestamp));
		
		Date date = calendar.getTime();
		
		//2015-02-05T15:47:13.965+01:00
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat timeZoneFormater = new SimpleDateFormat("ZZZ");
		
		String timeZoneResult = timeZoneFormater.format(date);
		String timeZoneFirst = timeZoneResult.substring(0, 3);
		String timeZoneSecond = timeZoneResult.substring(3, 5);
		
		String resultString = formater.format(date) + timeZoneFirst + ":" + timeZoneSecond;
		return resultString;
	}
	
	public static String envoyer(List<ActionLog> logs){ 

		if(logs.isEmpty())
		{
			return "No data to send";
		}
		
		JSONArray data = new JSONArray();
		
		try
		{
			for(int i = 0 ; i < logs.size(); ++i)
			{
				ActionLog log = logs.get(i);
				
				JSONObject obj = new JSONObject();
				obj.put("description", log.getDescription());
				obj.put("type", log.getType());
				obj.put("time", convertTimestamp(log.getTime()));
				
				data.put(obj);
			}
		}
		catch(Exception e)
		{
			Log.wtf("India Log", "Exception while serializing request", e);
		}
		
		String emailData = logs.get(0).getEmail();
		
		
		try
		{
			HttpClient client = new DefaultHttpClient();
			
			String baseUri = "https://orleans.miage.fr/indiarose/rest/logs/";
			//baseUri = "http://julienmialon.com/india/logs/";
			HttpUriRequest request = new HttpPost(baseUri + emailData);
			
			StringEntity content = new StringEntity(data.toString());
			content.setContentType("application/json");
			content.setContentEncoding("UTF-8");
			Log.wtf("India Log", "Content-Length: " + content.getContentLength());
			((HttpEntityEnclosingRequest) request).setEntity(content);
			
			HttpResponse response = client.execute(request);
			Log.wtf("India Log", "Http response : " + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				
				String resultData = out.toString();
				
				Log.wtf("India Log", "Http content : " + resultData);
				return resultData;
			}
			else
			{
				Log.wtf("RequestExecuter", "Error : " + response.getStatusLine().getStatusCode() + " # " + response.getStatusLine().getReasonPhrase());
				Log.wtf("ERROR", response.toString());
				Log.wtf("ERROR", "content length = " + response.getEntity().getContentLength());
				Header [] hs = response.getAllHeaders();
				for(Header h : hs)
				{
					Log.wtf("ERROR", "Name : " + h.getName() + " Value : " + h.getValue());
				}
				Header [] in = request.getAllHeaders();
				for(Header h : in)
				{
					Log.wtf("INIT", "Name : " + h.getName() + " Value : " + h.getValue());
				}
				HttpParams pa = request.getParams();
				Log.wtf("PARAM", pa.toString());
				
				response.getEntity().getContent().close();
			}
		}
		catch(Exception e)
		{
			Log.wtf("India Log", "Exception while sending request", e);
		}
		return "ERROR";
		/*
		
		CRest crest = CRest.getInstance();
		LoggerService logservice = crest.build(LoggerService.class);
		String resultData = logservice.envoyer(emailData, data.toString());
		
		Log.wtf("Log result", "result get from rest ws : " + resultData);
		
		return resultData;
		*/
	}		
		
	
}
