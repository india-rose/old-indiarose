package org.indiarose.net;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.indiarose.R;
import org.indiarose.lib.ActionLog;
import org.indiarose.lib.AppData;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class NetLogger {

	private static String convertTimestamp(String time)
	{
		long timestamp = Long.parseLong(time);
		
		Timestamp ts = new Timestamp(timestamp);
		
		//2015-02-05T15:47:13.965+01:00
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat timeZoneFormater = new SimpleDateFormat("ZZZ");
		
		String timeZoneResult = timeZoneFormater.format(ts);
		String timeZoneFirst = timeZoneResult.substring(0, 3);
		String timeZoneSecond = timeZoneResult.substring(3, 5);
		
		String resultString = formater.format(ts) + timeZoneFirst + ":" + timeZoneSecond;
		return resultString;
	}
	
	private static HttpClient _httpClient = null;
	private static HttpClient CreateHttpClient() throws Exception
	{
		if(_httpClient != null)
		{
			return _httpClient;
		}
		
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		
		InputStream certificateStream = AppData.currentContext.getResources().openRawResource(R.raw.miage_fr_cert);
		Certificate ca;
		
		try
		{
			ca = factory.generateCertificate(certificateStream);
		}
		finally
		{
			certificateStream.close();
		}
		
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);

		SSLSocketFactory sslSocketFactory = new MySSLSocketFactory(keyStore, context);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", sslSocketFactory, 443));
		
		HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
		
		_httpClient = new DefaultHttpClient(connectionManager, params);
		return _httpClient;
	}
	
	public static boolean envoyer(List<ActionLog> logs, Activity activity){ 

		if(logs.isEmpty())
		{
			return true;
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
				obj.put("timestamp", convertTimestamp(log.getTime()));
				
				data.put(obj);
			}
		}
		catch(Exception e)
		{
			Log.wtf("India Log", "Exception while serializing request", e);
			return false;
		}
		
		String emailData = logs.get(0).getEmail();
		
		
		try
		{
			HttpClient client = CreateHttpClient();
			//HttpClient client = new DefaultHttpClient();
			
			String baseUri = "https://orleans.miage.fr/indiarose/rest/logs/";
			//baseUri = "http://julienmialon.com/india/logs/";
			HttpUriRequest request = new HttpPost(baseUri + emailData);
			
			String asciiContent = data.toString();
			
			//Log.wtf("India Log", "Content : " + asciiContent);
			ByteBuffer unicodeContent =Charset.forName("UTF-8").encode(asciiContent);
			ByteArrayEntity content = new ByteArrayEntity(unicodeContent.array());
			
			content.setContentType("application/json");
			content.setContentEncoding("UTF-8");
			((HttpEntityEnclosingRequest) request).setEntity(content);
			
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if(statusCode >= 200 && statusCode < 300)
			{
				response.getEntity().getContent().close();
				return true;
			}
			else
			{
				final String error = "Error : " + response.getStatusLine().getStatusCode() + " # " + response.getStatusLine().getReasonPhrase();
				activity.runOnUiThread(new Runnable(){
					public void run() {
						Toast.makeText(AppData.currentContext, error, Toast.LENGTH_SHORT).show();;
					}
				});
				Log.wtf("India Log", error);
				response.getEntity().getContent().close();
				
				
				return false;
			}
		}
		catch(Exception e)
		{
			Log.wtf("India Log", "Exception while sending request", e);
		}
		return false;
	}		
		
	
}
