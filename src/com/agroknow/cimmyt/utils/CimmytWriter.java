package com.agroknow.cimmyt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jettison.json.JSONException;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.agroknow.cimmyt.CimmytCollection;
import com.agroknow.cimmyt.CimmytEnrich;
import com.agroknow.cimmyt.CimmytOrganization;
import com.agroknow.cimmyt.CimmytPerson;
import com.agroknow.cimmyt.external.Freme;
import com.agroknow.cimmyt.parser.CimmytRecord;
import com.agroknow.cimmyt.parser.CimmytRecordInterface;
import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public class CimmytWriter 
{
	public static void write2File(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println(record.getSubject().toString());
		
		//CimmytRecord record=new C
		writeObject(record,folder);
		if(record.getHandler().contains("repository") || record.getHandler().contains("knowledge"))
			writeResource(record,folder);
		else if(record.getHandler().contains("data.cimmyt"))
			writeDatasetSoftware(record, folder);
		writePersons(record,folder);
		writeOrganizations(record,folder);
		writeCollections(record,folder);
		 
	}
	
	protected static void writeObject(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{

		PrintWriter writer = new PrintWriter(folder+File.separator+record.getApiid()+".object.xml", "UTF-8");
		writer.println("<object>");

		List<CimmytRecord.Title> titles=new ArrayList<CimmytRecord.Title>();
				
		String handler=record.getHandler();
		String type=null;
		
		if(handler.contains("repository.cimmyt"))
			type="resource";
		else if(handler.contains("data.cimmyt"))
			type="dataset_software";
		
		writer.println("\t<type><![CDATA["+type+"]]></type>");
		
		titles=record.getTitle();
		List<String> langs=new ArrayList<String>();
		List<String> lexvos=new ArrayList<String>();
		langs=record.getLanguage();
		lexvos=record.getLexvo();
		
		//writer.println("\t<titles>");
		for(int i=0;i<titles.size();i++)
		{
			writer.println("\t<title>");
				writer.println("\t\t<value><![CDATA["+titles.get(i).getValue()+"]]></value>");
				
				if(titles.get(i).getLang()!=null)
					writer.println("\t\t<lang>"+titles.get(i).getLang()+"</lang>");
				else
				{
					try
					{
						
						//load all languages:
						List<LanguageProfile> languageProfiles = null;
						try {
							languageProfiles = new LanguageProfileReader().readAll();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}

						//build language detector:
						LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
						        .withProfiles(languageProfiles)
						        .build();

						//create a text object factory
						TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

						//query:
						TextObject textObject = textObjectFactory.forText(titles.get(i).getValue().toLowerCase());
						Optional<String> lang = languageDetector.detect(textObject);
						
						String iso3=lang.get();
						
						
                			String value=lang.get();
                			
                			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
            						+ "assets"+System.getProperty("file.separator");
                			
                			/*
                			 * 	TODO: 
                			 * 		rethink about case sensitive/insensitive
                			 * 
                			 * */
                			
                				FileInputStream fstream = new FileInputStream(absolute_path+"iso-languagecodes.db");
                				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                				String strLine;
                				while ((strLine = br.readLine()) != null)   
                				{

                					String[] langs_read=strLine.split("\t");
                				  
	                				boolean found=false;

	                				if(value.equalsIgnoreCase(langs_read[2]))
	                				{
	                						found=true;
	                				}
	                				if(found)
	                				{
	                					iso3=langs_read[0];
	                					break;
	                				}
                				}
                				br.close();
           		
						
						//String lang2 = languageDetector.detect(textObject);
						writer.println("\t\t<lang>"+iso3+"</lang>");
						//if(langs.size()==1)
							//writer.println("\t\t<lang>"+langs.get(i)+"</lang>");
						//else
						//	writer.println("\t\t<lang></lang>");
					}
					catch(java.lang.IndexOutOfBoundsException e)
					{
						writer.println("\t\t<lang></lang>");
					}
					catch(java.lang.Exception e)
					{
						writer.println("\t\t<lang></lang>");
					}
				}
			writer.println("\t</title>");
		}
		//writer.println("\t</titles>");

		List<CimmytRecord.Description> descrs=new ArrayList<CimmytRecord.Description>();
		descrs=record.getDescription();
		//writer.println("\t<descriptions>");
		for(int i=0;i<descrs.size();i++)
		{
			writer.println("\t<description>");
				writer.println("\t\t<value><![CDATA["+descrs.get(i).getValue()+"]]></value>");
				
				try
				{
					if(titles.get(i).getLang()!=null)
						writer.println("\t\t<lang>"+descrs.get(i).getLang()+"</lang>");
					else
					{
						/*if(langs.size()==1)
							writer.println("\t\t<lang>"+langs.get(i)+"</lang>");
						else
							writer.println("\t\t<lang></lang>");
							*/
						
						//load all languages:
						List<LanguageProfile> languageProfiles = null;
						try {
							languageProfiles = new LanguageProfileReader().readAll();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}

						//build language detector:
						LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
						        .withProfiles(languageProfiles)
						        .build();

						//create a text object factory
						TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

						//query:
						TextObject textObject = textObjectFactory.forText(descrs.get(i).getValue().toLowerCase());
						Optional<String> lang = languageDetector.detect(textObject);
						
						String iso3=lang.get();
						
						
                			String value=lang.get();
                			
                			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
            						+ "assets"+System.getProperty("file.separator");
                			
                			/*
                			 * 	TODO: 
                			 * 		rethink about case sensitive/insensitive
                			 * 
                			 * */
                			
                				FileInputStream fstream = new FileInputStream(absolute_path+"iso-languagecodes.db");
                				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                				String strLine;
                				while ((strLine = br.readLine()) != null)   
                				{

                					String[] langs_read=strLine.split("\t");
                				  
	                				boolean found=false;

	                				if(value.equalsIgnoreCase(langs_read[2]))
	                				{
	                						found=true;
	                				}
	                				if(found)
	                				{
	                					iso3=langs_read[0];
	                					break;
	                				}
                				}
                				br.close();
           		
                		writer.println("\t\t<lang>"+iso3+"</lang>");
					}
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					//e.printStackTrace();
					writer.println("\t\t<lang></lang>");
				}
				catch(java.lang.Exception e)
				{
					writer.println("\t\t<lang></lang>");
				}
			writer.println("\t</description>");
		}
		
		if(descrs.size()==0)
		{
			writer.println("\t<description>\n\t\t<value></value>\n\t\t<lang></lang></description>");
		}
		
		//writer.println("\t</descriptions>");

		//if(1==1)
		//	return;
		
		String uri=record.getApiid();
		writer.println("\t<appid>"+uri+"</appid>");
		writer.println("\t<appuri>/cimmyt/"+type+"/"+uri+"</appuri>");
		//if(1==1)
		//	return;
		
		List<CimmytRecord.Subject> subjects=new ArrayList<CimmytRecord.Subject>();
		subjects=record.getSubject();
		//System.out.println(subjects.size());
		for(int i=0;i<subjects.size();i++)
		{
			int k;
			for(k=0;k<i;k++)
			{
				if(subjects.get(i).getValue().equalsIgnoreCase(subjects.get(k).getValue()))
				{
					break;
				}
			}
			if(k!=i)
				continue;
			
			writer.println("\t<subject>");
				/*
				 * TODO:
				 * 		perhaps toLowerCase()??
				 * */
				writer.println("\t\t<value><![CDATA["+subjects.get(i).getValue().toLowerCase()+"]]></value>");
				
				for(int j=i+1;j<subjects.size();j++)
				{
					if(subjects.get(i).getValue().equalsIgnoreCase(subjects.get(j).getValue()))
					{
						try
						{
							if(!subjects.get(j).getUri().equals(subjects.get(i).getUri()))
							{
								writer.println("\t\t<uri><![CDATA["+subjects.get(j).getUri()+"]]></uri>");
								writer.println("\t\t<vocabulary>"+subjects.get(j).getVocabulary().toLowerCase()+"</vocabulary>");
								writer.println("\t\t<score>"+subjects.get(j).getScore()+"</score>");
							}
							else
							{
								writer.println("\t\t<uri></uri>");
								writer.println("\t\t<vocabulary></vocabulary>");
								writer.println("\t\t<score></score>");
							}
						}
						catch(java.lang.NullPointerException e)
						{
							//e.printStackTrace();
						}
					}
				}
				
				try
				{
					if(!subjects.get(i).getUri().equals("null"))
					{
						writer.println("\t\t<uri><![CDATA["+subjects.get(i).getUri()+"]]></uri>");
						writer.println("\t\t<vocabulary>"+subjects.get(i).getVocabulary()+"</vocabulary>");
						writer.println("\t\t<score>"+subjects.get(i).getScore()+"</score>");
					}
					else
					{
						writer.println("\t\t<uri></uri>");
						writer.println("\t\t<vocabulary></vocabulary>");
						writer.println("\t\t<score></score>");
					}
				}
				catch(java.lang.NullPointerException e)
				{
					writer.println("\t\t<uri></uri>");
					writer.println("\t\t<vocabulary></vocabulary>");
					writer.println("\t\t<score></score>");
					
				}
			writer.println("\t</subject>");
		}
		

		if(subjects.size()==0)
		{
			writer.println("\t<subject>\n\t\t<value></value>\n\t\t<uri></uri>"
					+ "<vocabulary></vocabulary><score></score></subject>");
		}
		
		
		
		
		for(int i=0;i<langs.size();i++)
		{
			writer.println("\t<language>");
				writer.println("\t\t<value>"+langs.get(i)+"</value>");
				try
				{
					writer.println("\t\t<uri>"+lexvos.get(i)+"</uri>");
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					writer.println("\t\t<uri></uri>");
				}
			writer.println("\t</language>");
		}
		
		if(langs.size()==0)
		{
			writer.println("\t<language>\n\t\t<value></value>\n\t\t<uri></uri>"
					+ "</language>");
		}	
		
		List<XMLGregorianCalendar> created=record.getDate();
		for(int i=0;i<created.size();i++)
		{
			try
			{
				String date=created.get(i).toString();
				writer.println("\t<created>"+date+"</created>");
			}
			catch(java.lang.NullPointerException e)
			{
				writer.println("<created></created>");
			}
		}
		if(created.size()==0)
			writer.println("<created></created>");
		
		
		XMLGregorianCalendar updated=record.getUpdatedDate();
		try
		{
			writer.println("\t<updated>"+updated+"</updated>");
		}
		catch(java.lang.NullPointerException e)
		{
			writer.println("\t<updated></updated>");
		}
		
		
		writer.println("</object>");
		writer.close();

	}
	
	protected static void writeResource(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(folder+File.separator+record.getApiid()+".resource.xml", "UTF-8");
		writer.println("<resource>");
		
			List<String> types=new ArrayList<String>();
			types=record.getType();
		
			for(int i=0;i<types.size();i++)
			{
				writer.println("\t<type><![CDATA["+types.get(i)+"]]></type>");
			}
			
			if(types.size()==0)
			{
				writer.println("\t<type></type>");
			}
		
			writer.println("\t<appid>"+record.getApiid()+"</appid>");
			writer.println("\t<appuri>/cimmyt/resource/"+record.getApiid()+"</appuri>");
			
			List<String> creators=new ArrayList<String>();
			creators=record.getCreator();
			
			for(int i=0;i<creators.size();i++)
			{
				String cr_name=creators.get(i)+" person";
				
				int cid=cr_name.hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<creator>");
					writer.println("\t\t<value>"+creators.get(i)+"</value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/person/"+cid+"</uri>");
					writer.println("\t\t<type>person</type>");
				writer.println("\t</creator>");
			}

			if(creators.size()==0)
			{
				writer.println("\t<creator><value></value><id></id><uri></uri><type></type></creator>");
			}
		
			List<String> contributors=new ArrayList<String>();
			contributors=record.getContributor();
			
			for(int i=0;i<contributors.size();i++)
			{
				String contr_name=contributors.get(i)+" person";
				
				int cid=contr_name.hashCode();
				if(cid<0)
					cid*=-1;
				
				/*
				 * TODO:
				 * 	validate that they are persons!
				 * */
				writer.println("\t<contributor>");
					writer.println("\t\t<value>"+contributors.get(i)+"</value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/person/"+cid+"</uri>");
					writer.println("\t\t<type>person</type>");
				writer.println("\t</contributor>");
			}

			if(contributors.size()==0)
			{
				writer.println("\t<contributor><value></value><id></id><uri></uri><type></type></contributor>");
			}
		
			List<String> publishers=new ArrayList<String>();
			publishers=record.getPublisher();
			
			for(int i=0;i<publishers.size();i++)
			{
				String pub_name=publishers.get(i)+" organization";
				
				int cid=pub_name.hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<publisher>");
					writer.println("\t\t<value><![CDATA["+publishers.get(i)+"]]></value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/organization/"+cid+"</uri>");
					writer.println("\t\t<type>organization</type>");
				writer.println("\t</publisher>");
			}

			if(publishers.size()==0)
			{
				writer.println("\t<publisher><value></value><id></id><uri></uri><type></type></publisher>");
			}
		
			List<XMLGregorianCalendar> dates=new ArrayList<XMLGregorianCalendar>();
			dates=record.getPubDate();
			
			for(int i=0;i<dates.size();i++)
			{
				writer.println("\t<date>"+dates.get(i)+"</date>");
			}

			if(dates.size()==0)
			{
				writer.println("\t<date></date>");
			}
		
			dates=new ArrayList<XMLGregorianCalendar>();
			dates=record.getDate();
			
			for(int i=0;i<dates.size();i++)
			{
				writer.println("\t<updatedDate>"+dates.get(i)+"</updatedDate>");
			}

			if(dates.size()==0)
			{
				writer.println("\t<updatedDate></updatedDate>");
			}
		
			List<String> issns=new ArrayList<String>();
			issns=record.getIssn();
			
			for(int i=0;i<issns.size();i++)
			{
				writer.println("\t<issn>"+issns.get(i)+"</issn>");
			}

			if(issns.size()==0)
			{
				writer.println("\t<issn></issn>");
			}
		
			List<String> isbns=new ArrayList<String>();
			isbns=record.getIsbn();
			
			for(int i=0;i<isbns.size();i++)
			{
				writer.println("\t<isbn>"+isbns.get(i)+"</isbn>");
			}

			if(isbns.size()==0)
			{
				writer.println("\t<isbn></isbn>");
			}
		
			List<String> urls=new ArrayList<String>();
			urls=record.getUrl();
			
			for(int i=0;i<urls.size();i++)
			{
				boolean broken;
				try {
					broken = exists(urls.get(i));
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					broken=true;
				}
				writer.println("\t<url>");
					writer.println("\t\t<value>"+urls.get(i)+"</value>");
					writer.println("\t\t<broken>"+broken+"</broken>");
				writer.println("\t</url>");
			}

			/*if(urls.size()==0)
			{
				writer.println("\t<url><value></value><broken></broken></url>");
			}*/
		
			String base_url="";
			
			if(record.getHandler().contains("repository"))
				base_url="http://repository.cimmyt.org/xmlui/handle/";
			else if(record.getHandler().contains("data.cimmyt"))
				base_url="http://data.cimmyt.org/dvn/study?globalId=hdl:";
			
			writer.println("\t<url>");
			try
			{
				writer.println("\t\t<value>"+base_url+record.getDomainid().get(0)+
						"/"+record.getCdocid().get(0)+"</value>");
				writer.println("\t\t<broken>false</broken>");
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				//e.printStackTrace();
				writer.println("\t\t<value></value><broken></broken>");
				//writer.println("\t\t<broken>false</broken>");
			}
			writer.println("\t</url>");
			
			List<String> locations=new ArrayList<String>();
			locations=record.getLocation();
			List<String> geonames=new ArrayList<String>();
			geonames=record.getGeonames();
			
			for(int i=0;i<locations.size();i++)
			{
				writer.println("\t<location>");
					writer.println("\t\t<value>"+locations.get(i)+"</value>");
					try
					{
						writer.println("\t\t<uri><![CDATA["+geonames.get(i)+"]]></uri>");
						writer.println("\t\t<vocabulary>geonames</vocabulary>");
						
						/*
						 * TODO:
						 * 	really parse file...
						 * */
						
						String fao_geo=locations.get(i);
						
						if(fao_geo.contains(" "))
						{
							//fao_geo.replace(" ", "_");
							//fao_geo.
							String[] fao=fao_geo.split(" ");
							fao_geo=fao[0].toLowerCase()+"_"+fao[1];
						}
						
						writer.println("\t\t<uri><![CDATA[http://www.fao.org/countryprofiles/geoinfo/geopolitical/resource/"+
								fao_geo+"]]></uri>");
						writer.println("\t\t<vocabulary>faogeopolitical</vocabulary>");
					}
					catch (IndexOutOfBoundsException e) {
						
					}
				writer.println("\t</location>");
			}
			
			if(locations.size()==0)
			{
				writer.println("\t<location><value></value><uri></uri><vocabulary></vocabulary></location>");
			}

			List<String> regions=new ArrayList<String>();
			regions=record.getRegion();
			
			for(int i=0;i<regions.size();i++)
			{
				writer.println("\t<coverage>"+regions.get(i)+"</coverage>");
			}

			if(regions.size()==0)
			{
				writer.println("\t<coverage></coverage>");
			}

			List<String> places=new ArrayList<String>();
			places=record.getPlace();
			
			for(int i=0;i<places.size();i++)
			{
				writer.println("\t<place>"+places.get(i)+"</place>");
			}

			if(places.size()==0)
			{
				writer.println("\t<place></place>");
			}

			List<String> pages=new ArrayList<String>();
			pages=record.getPages();
			
			for(int i=0;i<pages.size();i++)
			{
				writer.println("\t<page>"+pages.get(i)+"</page>");
			}

			if(pages.size()==0)
			{
				writer.println("\t<page></page>");
			}

			List<String> series=new ArrayList<String>();
			series=record.getExtent();
			
			for(int i=0;i<series.size();i++)
			{
				writer.println("\t<extent>"+series.get(i)+"</extent>");
			}

			if(series.size()==0)
			{
				writer.println("\t<extent></extent>");
			}

			List<String> relations=new ArrayList<String>();
			relations=record.getRelation();
			
			/*
			 * TODO:
			 * 	check if need be for external link
			 * */
			for(int i=0;i<relations.size();i++)
			{
				writer.println("\t<relation>"+relations.get(i)+"</relation>");
			}

			if(relations.size()==0)
			{
				writer.println("\t<relation></relation>");
			}

			List<String> rights=new ArrayList<String>();
			rights=record.getRights();
			
			for(int i=0;i<rights.size();i++)
			{
				writer.println("\t<rights>"+rights.get(i).replaceAll("\\<.*?>","")+"</rights>");
			}

			if(rights.size()==0)
			{
				writer.println("\t<rights></rights>");
			}

			List<String> citations=new ArrayList<String>();
			citations=record.getCitation();
			
			for(int i=0;i<citations.size();i++)
			{
				writer.println("\t<citation>"+citations.get(i)+"</citation>");
			}

			if(citations.size()==0)
			{
				writer.println("\t<citation></citation>");
			}

			List<String> dois=new ArrayList<String>();
			dois=record.getDoi();
			
			for(int i=0;i<dois.size();i++)
			{
				writer.println("\t<doi>"+dois.get(i)+"</doi>");
			}

			if(dois.size()==0)
			{
				writer.println("\t<doi></doi>");
			}

			List<String> formats=new ArrayList<String>();
			formats=record.getFormat();
			
			for(int i=0;i<formats.size();i++)
			{
				writer.println("\t<format>"+formats.get(i)+"</format>");
			}

			if(formats.size()==0)
			{
				writer.println("\t<format></format>");
			}

			List<String> qualities=new ArrayList<String>();
			qualities=record.getQuality();
			
			for(int i=0;i<qualities.size();i++)
			{
				writer.println("\t<quality>"+qualities.get(i)+"</quality>");
			}

			if(qualities.size()==0)
			{
				writer.println("\t<quality></quality>");
			}

			List<String> resource_links=new ArrayList<String>();
			resource_links=record.getLinkToResource();

			List<String> resource_sizes=new ArrayList<String>();
			resource_sizes=record.getLinkToResourceSize();

			List<String> resource_types=new ArrayList<String>();
			resource_types=record.getLinkToResourceType();

			List<String> resource_labels=new ArrayList<String>();
			resource_labels=record.getLinkToResourceLabel();

			List<String> resource_categories=new ArrayList<String>();
			resource_categories=record.getLinkToResourceCategory();
			
			writer.println("\t<aggregation>");
				
				try
				{
					writer.println("\t\t<shownAt>");
						writer.println("\t\t\t<value>"+base_url+record.getDomainid().get(0)+
								"/"+record.getCdocid().get(0)+"</value>");
						writer.println("\t\t\t<broken>false</broken>");
					writer.println("\t\t</shownAt>");
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					writer.println("\t\t<shownAt><value></value><broken></broken></shownAt>");
				}
				
				for(int i=0;i<resource_links.size();i++)
				{
					int k;
					
					for(k=0;k<i;k++)
					{
						if(resource_links.get(i).equals(resource_links.get(k)))
							break;
					}
					if(k!=i)
						continue;
					boolean broken;
					try {
						broken = exists(resource_links.get(i));
					} catch (ConnectException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						broken=true;
					}
					writer.println("\t\t<shownBy>");
						writer.println("\t\t\t<value>"+resource_links.get(i)+"</value>");
						writer.println("\t\t\t<broken>"+broken+"</broken>");
					writer.println("\t\t</shownBy>");
				}
				
				if(resource_links.size()==0)
				{
					writer.println("\t<shownBy><value></value><broken></broken></shownBy>");
				}
				
				for(int i=0;i<resource_links.size();i++)
				{

					int k;
					
					for(k=0;k<i;k++)
					{
						if(resource_links.get(i).equals(resource_links.get(k)))
							break;
					}
					if(k!=i)
						continue;
					
					writer.println("\t\t<linkToResource>");
						writer.println("\t\t\t<value>"+resource_links.get(i)+"</value>");
						try
						{
							writer.println("\t\t\t<label>"+resource_labels.get(i)+"</label>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<label></label>");
						}
						try
						{
							writer.println("\t\t\t<category>"+resource_categories.get(i)+"</category>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<category></category>");
						}
						try
						{
							writer.println("\t\t\t<type><![CDATA["+resource_types.get(i)+"]]></type>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<type></type>");
						}
						try
						{
							writer.println("\t\t\t<size>"+resource_sizes.get(i)+"</size>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							URL resource_url;
							try {
								resource_url = new URL(resource_links.get(i));
								int size=getFileSize(resource_url);
								
								if(size<=0)
									throw(new MalformedURLException());
								
								writer.println("\t\t\t<size>"+size+"</size>");
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								writer.println("\t\t\t<size></size>");
							}
						}
					writer.println("\t\t</linkToResource>");
				}	

				if(resource_links.size()==0)
				{
					writer.println("\t<linkToResource><value></value><label></label>"
							+ "<category></category><type></type><size></size>"
							+ "</linkToResource>");
				}
				
				
				/*for(int i=0;i<resource_sizes.size();i++)
				{
					System.out.println(i+")"+resource_sizes.get(i));
				}*/
				
			writer.println("\t</aggregation>");
			
						
			List<String> collections=new ArrayList<String>();
			collections=record.getSetid();
				
			for(int i=0;i<collections.size();i++)
			{
				writer.println("\t<collection>");
					writer.println("\t\t<id>"+collections.get(i)+"</id>");
					writer.println("\t\t<uri>/cimmyt/collection/"+collections.get(i)+"</uri>");
					writer.println("\t\t<type>collection</type>");
				writer.println("\t</collection>");
			}
			
			if(resource_links.size()==0)
			{
				writer.println("\t<collection><id></id><uri></uri>"
						+ "<type></type>"
						+ "</collection>");
			}
			
			
		writer.println("</resource>");
		writer.close();
	}
	
	
	
	protected static void writePersons(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		List<String> persons=new ArrayList<String>();
		persons=record.getCreator();
		persons.addAll(record.getContributor());
		
		List<CimmytRecord.Subject> subjs=new ArrayList<CimmytRecord.Subject>();
		subjs=record.getSubject();
		
		if(record.getHandler().contains("data.cimmyt"))
		{
			CimmytEnrich enricher=new CimmytEnrich();
			try {
				persons.addAll(enricher.extractPersonsDVN(record));
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
		for(int i=0;i<persons.size();i++)
		{
			String p_name=persons.get(i)+" person";
			
			int id=p_name.hashCode();
			if(id<0)
				id*=-1;
			
			CimmytPerson person=new CimmytPerson();
			person.id=String.valueOf(id);
			person.uri="/cimmyt/person/"+id;
			person.name=persons.get(i);

			CimmytEnrich enricher=new CimmytEnrich();
			try {
				enricher.enrichPersonDVN(record,person);
				//System.out.println("OUTSIDE:"+person.affiliation_name);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			PrintWriter writer = new PrintWriter(folder+File.separator+id+".object.xml", "UTF-8");
			
			writer.println("<object>");
				writer.println("\t<type>person</type>");
				
				writer.println("\t<title>");
					writer.println("\t\t<value><![CDATA["+person.name+"]]></value>");
					writer.println("\t\t<lang></lang>");
				writer.println("\t</title>");
				
				writer.println("\t<appid>"+String.valueOf(person.id)+"</appid>");
				writer.println("\t<appuri>/cimmyt/person/"+person.id+"</appuri>");
				
				writer.println("\t<description>\n\t\t<value></value>\n\t\t<lang></lang></description>");
				writer.println("\t<subject>\n\t\t<value></value><uri></uri><vocabulary></vocabulary>"
						+ "<score></score></subject>");
				writer.println("\t<language><uri></uri><value></value></language>");
				writer.println("\t<created></created>");
				writer.println("\t<updated></updated>");
				
			writer.println("</object>");
			writer.close();
			
			writer = new PrintWriter(folder+File.separator+id+".person.xml", "UTF-8");

			String[] fn_ln=person.name.split(", ");
			
			try
			{
				if(fn_ln.length<=2)
				{
					person.last_name=fn_ln[0];
					person.first_name=fn_ln[1];
				}
				else
				{
					person.last_name="";
					person.first_name="";
				}
			}
			catch(java.lang.ArrayIndexOutOfBoundsException e)
			{
				fn_ln=person.name.split(" ");
				try
				{
					person.last_name=fn_ln[0];
					person.first_name=fn_ln[1];
				}
				catch(java.lang.ArrayIndexOutOfBoundsException e2)
				{
					person.last_name=person.first_name="";
				}
			}
			
			Freme freme_enricher=new Freme();
			
			
			person.orcid="null";
			List<String> locations=record.getLocation();
			String compact_locs="";
			
			for(int j=0;j<locations.size();j++)
				compact_locs+=locations.get(j)+" ";


			try
			{
				GetConfig config=new GetConfig();
				int enrich=Integer.valueOf(config.getValue("author_enrich"));
				
				int p_enrich=0;
				
				if(enrich==1)
				{
					person.orcid=freme_enricher.enrichPersons(person.first_name+" "
							+person.last_name+" "+person.affiliation_name);
					
					try
					{
						if(person.orcid.isEmpty())
							person.orcid=freme_enricher.enrichPersons(person.first_name+" "
								+person.last_name);
					}
					catch(java.lang.NullPointerException e)
					{
						person.orcid=freme_enricher.enrichPersons(person.first_name+" "
								+person.last_name);
					}
					
					try
					{
						if(person.orcid.isEmpty())
						{
							for(int k=0;k<subjs.size();k++)
							{
								person.orcid="";
								person.orcid=freme_enricher.enrichPersons(person.first_name+" "
										+person.last_name+" "+subjs.get(k).getValue());
								if(!person.orcid.isEmpty())
									break;
							}
						}
					}
					catch(java.lang.NullPointerException e)
					{
						for(int k=0;k<subjs.size();k++)
						{
							person.orcid="";
							person.orcid=freme_enricher.enrichPersons(person.first_name+" "
									+person.last_name+" "+subjs.get(k));
							if(!person.orcid.isEmpty())
								break;
						}
					}
					
					try
					{
						if(!person.orcid.isEmpty())
							p_enrich=1;
					}
					catch(java.lang.NullPointerException e)
					{
						
					}
					
					record.updateStats(p_enrich);
				}
			}
			catch(java.lang.Exception e)
			{
				
			}
			
			/*try(FileWriter fw = new FileWriter("C:\\Users\\Mihalis\\Desktop\\CIMMYT\\outfilename.csv", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				    out.println(";");
				    //System.exit(1);
				} catch (IOException e) {
				    //exception handling left as an exercise for the reader
				}*/
			
			writer.println("<person>");
			
				writer.println("\t<fullName><![CDATA["+person.name+"]]></fullName>");
				writer.println("\t<lastName><![CDATA["+person.last_name+"]]></lastName>");
				writer.println("\t<firstName><![CDATA["+person.first_name+"]]></firstName>");
				
				String person_name=person.name+" person";
				
				int pid=person_name.hashCode();
				if(pid<0)
					pid*=-1;
				writer.println("\t<appid>"+String.valueOf(pid)+"</appid>");
				writer.println("\t<appuri>/cimmyt/person/"+pid+"</appuri>");
				
				writer.println("\t<orcid>"+person.orcid+"</orcid>");
				
				/*
				 * TODO:
				 * 		really enrich...
				 *  
				 * */


				
				writer.println("\t<url><value></value><broken>false</broken></url>");
								
				writer.println("\t<contact>"+person.contact+"</contact>");
				writer.println("\t<location><value></value><uri></uri><vocabulary></vocabulary></location>");
				
				if(!person.affiliation_name.isEmpty())
				{
					writer.println("\t<affiliation>");
						writer.println("\t\t<value>"+person.affiliation_name+"</value>");
						
						String aff_name=person.affiliation_name+" organization";
						
						int aid=aff_name.hashCode();
						if(aid<0)
							aid*=-1;
						writer.println("\t\t<id>"+aid+"</id>");
						writer.println("\t\t<type>organization</type>");
						writer.println("\t\t<uri>/cimmyt/organization/"+aid+"</uri>");
					writer.println("\t</affiliation>");
				}
				else
					writer.println("\t<affiliation><value></value><id></id><type></type><uri></uri></affiliation>");
				writer.println("\t<photo/>");
				writer.println("\t<shortBio/>");
			
			writer.println("</person>");
			writer.close();
		}
	}
	
	protected static void writeOrganizations(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		List<String> organizations=new ArrayList<String>();
		organizations=record.getPublisher();
		
		CimmytEnrich enricher=new CimmytEnrich();
		try {
			organizations.addAll(enricher.extractOrganizationsDVN(record));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		for(int i=0;i<organizations.size();i++)
		{
			String org_name=organizations.get(i)+" organization";
			
			int id=org_name.hashCode();
			if(id<0)
				id*=-1;
			
			CimmytOrganization organization=new CimmytOrganization();
			organization.id=id;
			organization.uri="/cimmyt/organization/"+id;
			organization.name=organizations.get(i);

			try {
				enricher.enrichOrganizationDVN(record,organization);
				//System.out.println("OUTSIDE:"+person.affiliation_name);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						

			PrintWriter writer = new PrintWriter(folder+File.separator+id+".object.xml", "UTF-8");
			
			writer.println("<object>");
				writer.println("\t<type>organization</type>");
				
				writer.println("\t<title>");
					writer.println("\t\t<value><![CDATA["+organization.name+"]]></value>");
					writer.println("\t\t<lang></lang>");
				writer.println("\t</title>");

				
				writer.println("\t<appid>"+String.valueOf(organization.id)+"</appid>");
				writer.println("\t<appuri>/cimmyt/organization/"+organization.id+"</appuri>");
				
				writer.println("\t<description>\n\t\t<value></value>\n\t\t<lang></lang></description>");
				writer.println("\t<subject>\n\t\t<value></value><uri></uri><vocabulary></vocabulary>"
						+ "<score></score></subject>");
				writer.println("\t<language><value></value><uri></uri></language>");
				writer.println("\t<created></created>");
				writer.println("\t<updated></updated>");
				
			writer.println("</object>");
			writer.close();
			
			writer = new PrintWriter(folder+File.separator+id+".organization.xml", "UTF-8");

			Freme freme_enricher=new Freme();
			organization.viaf="";
			
			GetConfig config=new GetConfig();
			int enrich = 0;
			try {
				enrich = Integer.valueOf(config.getValue("organization_enrich"));
			} catch (NumberFormatException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(enrich==1)
			{
				try {
					organization.viaf=freme_enricher.enrichOrganizations(organization.name);
					if(!organization.viaf.isEmpty())
					{
						record.updateStats(1);
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			writer.println("<organization>");
			
				writer.println("\t<fullName>"+organization.full_name+"</fullName>");
				
				String orgz_name=organization.name+" organization";
				int pid=orgz_name.hashCode();
				if(pid<0)
					pid*=-1;
				writer.println("\t<appid>"+String.valueOf(pid)+"</appid>");
				writer.println("\t<appuri>/cimmyt/organization/"+pid+"</appuri>");
				
				writer.println("\t<viaf>"+organization.viaf+"</viaf>");
				
				/*
				 * TODO:
				 * 		really enrich...
				 *  
				 * */
				boolean broken=false;
				try{
					broken=exists(organization.url);
				}
				catch(Exception e)
				{
					broken=false;
				}
				writer.println("\t<url><value>"+organization.url+"</value><broken>"+broken+"</broken></url>");
				writer.println("\t<contact/>");
				writer.println("\t<location><value></value><uri></uri><vocabulary></vocabulary></location>");
				writer.println("\t<address/>");
				writer.println("\t<logo>"+organization.logo+"</logo>");
			
			writer.println("</organization>");
			writer.close();
		}
	}
	
	protected static void writeCollections(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		List<String> collections=new ArrayList<String>();
		collections=record.getCset();

		List<String> collection_ids=new ArrayList<String>();
		collection_ids=record.getSetid();
		
		for(int i=0;i<collections.size();i++)
		{
			int id=Integer.valueOf(collection_ids.get(i));
			if(id<0)
				id*=-1;
			
			CimmytCollection collection=new CimmytCollection();
			collection.id=id;
			collection.uri="/cimmyt/collection/"+id;
			
			collection.spec=collections.get(i);
			collection.handler=record.getHandler();
			
			if(collection.handler.contains("repository"))
				collection.software="DSpace";
			else if(collection.handler.contains("data.cimmyt"))
				collection.software="Dataverse";

			CimmytEnrich cimmyt_enrich=new CimmytEnrich();
			try {
				GetConfig config=new GetConfig();
				int enrich=Integer.valueOf(config.getValue("collection_enrich"));
				
				if(enrich==1)
				{
					cimmyt_enrich.enrichCollection(collection, record);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			/*

			String domain_id=record.getDomainid().get(0);
			String doc_id=record.getCdocid().get(0);
			
			String url=collection.handler+"?verb=ListSets";
			
			
			
			URL url2 = new URL(url);
	        URLConnection connection = url2.openConnection();

	        Document doc = parseXML(connection.getInputStream());
	        
	        
	        NodeList descNodes = 
	        			doc.getLastChild().getChildNodes();
	       
	        XPathFactory xPathfactory = XPathFactory.newInstance();
	        XPath xpath = xPathfactory.newXPath();
	        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/DIDL/Item/Component/Resource");
	        
	        */

			PrintWriter writer = new PrintWriter(folder+File.separator+id+".object.xml", "UTF-8");
			
			writer.println("<object>");
				writer.println("\t<type>collection</type>");
				
				writer.println("\t<title>");
					writer.println("\t\t<value><![CDATA["+collection.name+"]]></value>");
					writer.println("\t\t<lang></lang>");
				writer.println("\t</title>");

				writer.println("\t<description>");
					writer.println("\t\t<value>"+collection.description+"</value>");
					writer.println("\t\t<lang></lang>");
				writer.println("\t</description>");
					
				
				writer.println("\t<appid>"+String.valueOf(collection.id)+"</appid>");
				writer.println("\t<appuri>/cimmyt/collection/"+collection.id+"</appuri>");
				
				writer.println("\t<subject>\n\t\t<value></value><uri></uri><vocabulary></vocabulary>"
						+ "<score></score></subject>");
				writer.println("\t<language><value></value><uri></uri></language>");
				writer.println("\t<created></created>");
				writer.println("\t<updated></updated>");
				
			writer.println("</object>");
			writer.close();
			
			writer = new PrintWriter(folder+File.separator+id+".collection.xml", "UTF-8");

			
			writer.println("<collection>");
			
				writer.println("\t<appid>"+String.valueOf(collection.id)+"</appid>");
				writer.println("\t<appuri>/cimmyt/collection/"+collection.id+"</appuri>");
				
				writer.println("\t<name>"+collection.name+"</name>");
				writer.println("\t<spec>"+collection.spec+"</spec>");
				
				writer.println("\t<about>");
				
					writer.println("\t\t<software>"+collection.software+"</software>");
					writer.println("\t\t<handler>"+collection.handler+"</handler>");
					writer.println("\t\t<repoName>"+collection.repoName+"</repoName>");
					
					for(int j=0;j<collection.metadataNames.size();j++)
					{
						writer.println("\t\t<metadataSchema>");
							writer.println("\t\t\t<name>"+collection.metadataNames.get(j)+"</name>");
							writer.println("\t\t\t<uri>"+collection.metadataURIs.get(j)+"</uri>");
						writer.println("\t\t</metadataSchema>");
					}
				
				writer.println("\t</about>");
			
			writer.println("</collection>");
			writer.close();
		}
	}
	

	protected static void writeDatasetSoftware(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(folder+File.separator+record.getApiid()+".dataset_software.xml", "UTF-8");
		writer.println("<dataset_software>");
		
			List<String> types=new ArrayList<String>();
			types=record.getType();
			
			for(int i=0;i<types.size();i++)
				writer.println("\t<type><![CDATA["+types.get(i)+"]]></type>");
		
			if(types.size()==0)
				writer.println("\t<type></type>");
			
			//int rid=record.getApiid().hashCode();
			//if(rid<0)
			//	rid*=-1;
			
			writer.println("\t<appid>"+record.getApiid()+"</appid>");
			writer.println("\t<appuri>/cimmyt/dataset_software/"+record.getApiid()+"</appuri>");
			
			List<String> creators=new ArrayList<String>();
			creators=record.getCreator();
			
			for(int i=0;i<creators.size();i++)
			{
				String p_name=creators.get(i)+" person";
				int cid=p_name.hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<creator>");
					writer.println("\t\t<value>"+creators.get(i)+"</value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/person/"+cid+"</uri>");
					writer.println("\t\t<type>person</type>");
				writer.println("\t</creator>");
			}

			if(creators.size()==0)
				writer.println("\t<creator><value></value><id></id><uri></uri><type></type></creator>");
			
			List<String> distributors=new ArrayList<String>();
			distributors=record.getPublisher();
			
			for(int i=0;i<distributors.size();i++)
			{
				String dist_name=distributors.get(i)+" organization";
				
				int cid=dist_name.hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<publisher>");
					writer.println("\t\t<value><![CDATA["+distributors.get(i)+"]]></value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/organization/"+cid+"</uri>");
					writer.println("\t\t<type>organization</type>");
				writer.println("\t</publisher>");
			}
			
			if(distributors.size()==0)
				writer.println("\t<publisher><value></value><id></id><uri></uri><type></type></publisher>");
			
			List<XMLGregorianCalendar> dates=new ArrayList<XMLGregorianCalendar>();
			dates=record.getPubDate();
			
			for(int i=0;i<dates.size();i++)
			{
				writer.println("\t<date>"+dates.get(i)+"</date>");
			}

			if(dates.size()==0)
				writer.println("\t<date></date>");
			
			List<String> urls=new ArrayList<String>();
			urls=record.getUrl();
			
			for(int i=0;i<urls.size();i++)
			{
				boolean broken;
				try {
					broken = exists(urls.get(i));
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					broken=true;
				}
				writer.println("\t<url>");
					writer.println("\t\t<value>"+urls.get(i)+"</value>");
					writer.println("\t\t<broken>"+broken+"</broken>");
				writer.println("\t</url>");
			}

			String base_url="";
			
			if(record.getHandler().contains("repository"))
				base_url="http://repository.cimmyt.org/xmlui/handle/";
			else if(record.getHandler().contains("data.cimmyt"))
				base_url="http://data.cimmyt.org/dvn/study?globalId=hdl:";
			
			writer.println("\t<url>");
				writer.println("\t\t<value>"+base_url+record.getDomainid().get(0)+
						"/"+record.getCdocid().get(0)+"</value>");
				writer.println("\t\t<broken>false</broken>");
			writer.println("\t</url>");

			List<String> locations=new ArrayList<String>();
			locations=record.getLocation();
			List<String> geonames=new ArrayList<String>();
			geonames=record.getGeonames();
			
			for(int i=0;i<locations.size();i++)
			{
				writer.println("\t<location>");
					writer.println("\t\t<value>"+locations.get(i)+"</value>");
					try
					{
						writer.println("\t\t<uri><![CDATA["+geonames.get(i)+"]]></uri>");
						writer.println("\t\t<vocabulary>geonames</vocabulary>");
						
						/*
						 * TODO:
						 * 	really parse file...
						 * */
						
						String fao_geo=locations.get(i);
						
						if(fao_geo.contains(" "))
						{
							//fao_geo.replace(" ", "_");
							//fao_geo.
							String[] fao=fao_geo.split(" ");
							fao_geo=fao[0].toLowerCase()+"_"+fao[1];
						}
						
						writer.println("\t\t<uri><![CDATA[http://www.fao.org/countryprofiles/geoinfo/geopolitical/resource/"+
								fao_geo+"]]></uri>");
						writer.println("\t\t<vocabulary>faogeopolitical</vocabulary>");
					}
					catch (IndexOutOfBoundsException e) {
						
					}
				writer.println("\t</location>");
			}

			if(locations.size()==0)
			{
				writer.println("\t<location><value></value><uri></uri><vocabulary></vocabulary></location>");
			}
			
			List<String> regions=new ArrayList<String>();
			regions=record.getRegion();
			
			for(int i=0;i<regions.size();i++)
			{
				writer.println("\t<coverage>"+regions.get(i)+"</coverage>");
			}

			if(regions.size()==0)
				writer.println("\t<coverage></coverage>");
			
			List<String> rights=new ArrayList<String>();
			rights=record.getRights();
			
			for(int i=0;i<rights.size();i++)
			{
				writer.println("\t<rights>"+rights.get(i).replaceAll("\\<.*?>","")+"</rights>");
			}

			if(rights.size()==0)
			{
				writer.println("\t<rights></rights>");
			}
			
			List<String> citations=new ArrayList<String>();
			citations=record.getCitation();
			
			for(int i=0;i<citations.size();i++)
			{
				writer.println("\t<citation>"+citations.get(i)+"</citation>");
			}

			if(citations.size()==0)
				writer.println("\t<citation></citation>");
			
			CimmytEnrich enricher=new CimmytEnrich();
			String kind_of_data="";
			try {
				kind_of_data = enricher.extractKindOfData(record);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			writer.println("\t<kindOfData>"+kind_of_data+"</kindOfData>");

			String time_period="";
			try {
				time_period = enricher.extractTimePeriod(record);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			writer.println("\t<timePeriod>"+time_period+"</timePeriod>");

			String funding="";
			try {
				funding = enricher.extractFunding(record);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			writer.println("\t<funding><![CDATA["+funding+"]]></funding>");
			
			List<String> relation_abbr=new ArrayList<String>();
			List<String> relation_program=new ArrayList<String>();
			List<String> relation_name=new ArrayList<String>();
			try {
				relation_program = enricher.extractProgramDVN(record);
				relation_name = enricher.extractProgramNameDVN(record);
				//relation_abbr = enricher.extractAbbreviationDVN(record);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			for(int i=0;i<relation_program.size();i++)
			{
				String value=relation_program.get(i);
				try
				{
					value+=": "+relation_name.get(i);
					try
					{
						//value+="("+relation_abbr.get(i)+")";
					}
					catch(java.lang.IndexOutOfBoundsException e)
					{
						
					}
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					
				}
				writer.println("\t<relation>"+value+"\t</relation>");
			}
			
			if(relation_program.size()==0)
				writer.println("\t<relation></relation>");
			
			String notes_tag="";
			try
			{
				notes_tag=enricher.extractNotesDVN(record);
			}
			catch(Exception e)
			{
				
			}
			writer.println(notes_tag);
			
			if(notes_tag.equals("") || notes_tag.isEmpty())
				writer.println("\t<notes></notes>");

			List<String> resource_links=new ArrayList<String>();
			resource_links=record.getLinkToResource();

			List<String> resource_sizes=new ArrayList<String>();
			resource_sizes=record.getLinkToResourceSize();

			List<String> resource_types=new ArrayList<String>();
			resource_types=record.getLinkToResourceType();

			List<String> resource_labels=new ArrayList<String>();
			resource_labels=record.getLinkToResourceLabel();

			List<String> resource_categories=new ArrayList<String>();
			resource_categories=record.getLinkToResourceCategory();
			
			writer.println("\t<aggregation>");
				
			try
			{
				writer.println("\t\t<shownAt>");
					writer.println("\t\t\t<value>"+base_url+record.getDomainid().get(0)+
						"/"+record.getCdocid().get(0)+"</value>");
					writer.println("\t\t\t<broken>false</broken>");
				writer.println("\t\t</shownAt>");
			}
			catch(java.lang.Exception e)
			{
				writer.println("\t\t<shownAt><value></value><broken></broken></shownAt>");
			}
				for(int i=0;i<resource_links.size();i++)
				{
					int k;
					
					for(k=0;k<i;k++)
					{
						if(resource_links.get(i).equals(resource_links.get(k)))
							break;
					}
					if(k!=i)
						continue;
					
					boolean broken;
					try
					{
						broken=exists(resource_links.get(i));
					}
					catch(java.net.ConnectException e)
					{
						broken=true;
					}
					
					writer.println("\t\t<shownBy>");
						writer.println("\t\t\t<value>"+resource_links.get(i)+"</value>");
						writer.println("\t\t\t<broken>"+broken+"</broken>");
					writer.println("\t\t</shownBy>");
				}
				
				if(resource_links.size()==0)
					writer.println("\t\t<shownBy><value></value><broken></broken></shownBy>");
				
				for(int i=0;i<resource_links.size();i++)
				{

					int k;
					
					for(k=0;k<i;k++)
					{
						if(resource_links.get(i).equals(resource_links.get(k)))
							break;
					}
					if(k!=i)
						continue;
					
					writer.println("\t\t<linkToResource>");
						writer.println("\t\t\t<value>"+resource_links.get(i)+"</value>");
						try
						{
							writer.println("\t\t\t<label>"+resource_labels.get(i)+"</label>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<label></label>");
						}
						try
						{
							writer.println("\t\t\t<category>"+resource_categories.get(i)+"</category>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<category></category>");
						}
						try
						{
							writer.println("\t\t\t<type><![CDATA["+resource_types.get(i)+"]]></type>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							writer.println("\t\t\t<type></type>");
						}
						try
						{
							writer.println("\t\t\t<size>"+resource_sizes.get(i)+"</size>");
						}
						catch(java.lang.IndexOutOfBoundsException e)
						{
							URL resource_url;
							try {
								resource_url = new URL(resource_links.get(i));
								int size=getFileSize(resource_url);
								
								if(size<=0)
									throw(new MalformedURLException());
								
								writer.println("\t\t\t<size>"+size+"</size>");
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								//e1.printStackTrace();
								writer.println("\t\t\t<size></size>");
							}
						}
					writer.println("\t\t</linkToResource>");
				}	
				
				if(resource_links.size()==0)
					writer.println("\t\t<linkToResource><value></value><label></label><category></category>"
							+ "<type></type><size></size></linkToResource>");
			writer.println("\t</aggregation>");
			
						
			List<String> collections=new ArrayList<String>();
			collections=record.getSetid();
				
			for(int i=0;i<collections.size();i++)
			{
				writer.println("\t<collection>");
					writer.println("\t\t<id>"+collections.get(i)+"</id>");
					writer.println("\t\t<uri>/cimmyt/collection/"+collections.get(i)+"</uri>");
					writer.println("\t\t<type>collection</type>");
				writer.println("\t</collection>");
			}
			
			if(collections.size()==0)
				writer.println("\t<collection><id></id><uri></uri><type></type></collection>");
			
			
		writer.println("</dataset_software>");
		writer.close();
	}
	
	public static boolean exists(String URLName) throws java.net.ConnectException{
	    
		if(URLName.isEmpty())
			return false;
		
		try {
	      HttpURLConnection.setFollowRedirects(false);
	      // note : you may also need
	      //        HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con =
	         (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	       //e.printStackTrace();
	       return false;
	    }
	  }  
	

	private static int getFileSize(URL url) {
	    HttpURLConnection conn = null;
	    try {
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("HEAD");
	        conn.getInputStream();
	        
	        //System.out.println("ResponseCode:"+conn.getResponseCode());
	        if(conn.getResponseCode()==200)
	        	return conn.getContentLength();
	        return -1;
	    } catch (IOException e) {
	        return -1;
	    } finally {
	        conn.disconnect();
	    }
	}
	
	private static Document parseXML(InputStream stream)
		    throws Exception
		    {
		        DocumentBuilderFactory objDocumentBuilderFactory = null;
		        DocumentBuilder objDocumentBuilder = null;
		        Document doc = null;
		        try
		        {
		            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

		            doc = objDocumentBuilder.parse(stream);
		        }
		        catch(Exception ex)
		        {
		            throw ex;
		        }       

		        return doc;
		    }
	
	
}
















