package com.agroknow.cimmyt.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.agroknow.cimmyt.CimmytPerson;
import com.agroknow.cimmyt.CimmytSubject;

public class Freme 
{

	
	public List<CimmytSubject> enrichSubjects(String value) throws JSONException, UnsupportedEncodingException, MalformedURLException, ProtocolException
	{
		String uri = "http://api-dev.freme-project.eu/current/e-terminology/tilde?"
				+ "input="+URLEncoder.encode(value,"UTF-8")+"&informat=text&outformat=json-ld"
						+ "&source-lang=en&target-lang=en&domain=TaaS-1001";
	
		List<CimmytSubject> subjects=new ArrayList<CimmytSubject>();
		
		URL url = new URL(uri);
		//logger.info("Calling FREME e-terminology: "+uri);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/ld+json;charset=UTF-8");
		
		BufferedReader streamReader;
		try {
			streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} 
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		try {
			while ((inputStr = streamReader.readLine()) != null)
			    responseStrBuilder.append(inputStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	    int responseCode;
		try {
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	    
	    System.out.println(responseStrBuilder);
	    
	    switch (responseCode) {
			case 200:
				JSONObject root = new JSONObject(responseStrBuilder.toString());
				JSONArray annotations = (JSONArray) root.get("@graph");
				if ( annotations !=null ) 
				{
					for(int i=0;i<annotations.length();i++)
					{
						JSONObject annotation = (JSONObject) annotations.get(i);
						if (annotation.has("taConfidence"))
						{
							//System.out.println(annotation+" Confidence:"+Double.parseDouble(annotation.get("taConfidence").toString()));
							
							if (Double.parseDouble(annotation.get("taConfidence").toString()) >= 0.60)
							{
								CimmytSubject subject=new CimmytSubject();
								subject.value=annotation.getJSONObject("label").getString("@value");
								subject.vocabulary="agrovoc";
								subject.score=Double.parseDouble(annotation.get("taConfidence").toString());
								subject.uri=null;
								
								String id=annotation.getString("@id");
								
								for(int j=0;j<annotations.length();j++)
								{
									JSONObject searcher=(JSONObject) annotations.get(j);
									if (searcher.has("annotationUnit"))
									{
										if(searcher.getString("annotationUnit").equals(id))
										{	
											//subject.uri=searcher.getString("termInfoRef");
											URL refUrl = new URL( searcher.get("termInfoRef").toString() );
											subject.uri = searcher.get("termInfoRef").toString();
											break;
										}
									}
								}
								if(!subject.uri.isEmpty())
								{
									subjects.add(subject);
								}
								//System.out.println(subject.value);
							}
						}
						else if(annotation.has("itsrdf:taConfidence"))
						{
							try
							{
								
								if(Integer.parseInt(annotation.getString("itsrdf:taConfidence"))==1)
								{
									CimmytSubject subject=new CimmytSubject();
									subject.value=annotation.getJSONObject("label").getString("@value");
									subject.vocabulary="agrovoc";
									subject.score=1.0;//Double.parseDouble(annotation.get("itsrdf:taConfidence").toString());
									subject.uri=null;
									
									String id=annotation.getString("@id");
									
									for(int j=0;j<annotations.length();j++)
									{
										JSONObject searcher=(JSONObject) annotations.get(j);
										if (searcher.has("annotationUnit"))
										{
											if(searcher.getString("annotationUnit").equals(id))
											{	
												//subject.uri=searcher.getString("termInfoRef");
												URL refUrl = new URL( searcher.get("termInfoRef").toString() );
												subject.uri = searcher.get("termInfoRef").toString();
												break;
											}
										}
									}
									if(!subject.uri.isEmpty())
									{
										int k;
										for(k=0;k<subjects.size();k++)
										{
											if(subjects.get(k).value.equalsIgnoreCase(subject.value))
											{
												break;
											}
										}
										
										if(k==subjects.size())
											subjects.add(subject);
										System.out.println(subject.value+", score:"+subject.score);
									}
								}
							}
							catch(java.lang.NumberFormatException e)
							{
								e.printStackTrace();
								continue;
							}
							catch(org.codehaus.jettison.json.JSONException e)
							{
								e.printStackTrace();
								continue;
							}
						}
					}
				
				}
				
				
				
	    }
	    
	    return subjects;
	}
	
	
	public String enrichPersons(String value) throws JSONException, UnsupportedEncodingException, MalformedURLException, ProtocolException
	{
		String dataset="orcid";
		String uri = "http://api-dev.freme-project.eu/current/e-entity/freme-ner/documents?"
				+ "input="+URLEncoder.encode(value,"UTF-8")+"&informat=text&outformat=json-ld&language=en&"
						+ "dataset="+dataset+"&mode=all";
		
		
		/*String uri = "http://api-dev.freme-project.eu/current/e-terminology/tilde?"
				+ "input="+URLEncoder.encode(value,"UTF-8")+"&informat=text&outformat=json-ld"
						+ "&source-lang=en&target-lang=en&domain=TaaS-1001";
		*/
		List<CimmytPerson> persons=new ArrayList<CimmytPerson>();
		
		URL url = new URL(uri);
		//logger.info("Calling FREME e-terminology: "+uri);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/ld+json;charset=UTF-8");
		
		BufferedReader streamReader;
		try {
			streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} 
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		try {
			while ((inputStr = streamReader.readLine()) != null)
			    responseStrBuilder.append(inputStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	    int responseCode;
		try {
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	    
	    System.out.println(responseStrBuilder);
	    
	    switch (responseCode) 
	    {
			case 200:
				JSONObject root = new JSONObject(responseStrBuilder.toString());
				//JSONArray annotations = root;
				if ( root !=null ) 
				{
					if (root.has("itsrdf:taConfidence"))
					{
						double score=Double.valueOf(root.getString("itsrdf:taConfidence"));
						
						if(score>=0.6)
						{
							String orcid=root.getString("taIdentRef");
							return orcid;
						}
						
					}
					
				}
	    }
	    return null;
	}
	
}












