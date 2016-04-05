package com.agroknow.cimmyt.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Freme {

	
	public void enrichSubjects(String value) throws IOException
	{
		String uri = "http://api-dev.freme-project.eu/current/e-terminology/tilde?"
				+ "input="+URLEncoder.encode(value,"UTF-8")+"&informat=text&outformat=json-ld&source-lang=en&target-lang=en&domain=TaaS-1001";
		
		URL url = new URL(uri);
		//logger.info("Calling FREME e-terminology: "+uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/ld+json;charset=UTF-8");
		
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")); 
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
		    responseStrBuilder.append(inputStr);
		
	    int responseCode = connection.getResponseCode();
	    
	    System.out.println(responseStrBuilder);
	    
	}
	
	
}
