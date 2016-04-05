package com.agroknow.cimmyt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jettison.json.JSONException;

import com.agroknow.cimmyt.CimmytPerson;
import com.agroknow.cimmyt.external.Freme;
import com.agroknow.cimmyt.parser.CimmytRecord;
import com.agroknow.cimmyt.parser.CimmytRecordInterface;

public class CimmytWriter 
{
	public static void write2File(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println(record.getSubject().toString());
		
		//CimmytRecord record=new C
		writeObject(record,folder);
		writeResource(record,folder);
		//writePersons(record,folder);
		 
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
			type="dataset/software";
		
		writer.println("\t<type>"+type+"</type>");
		
		titles=record.getTitle();
		List<String> langs=new ArrayList<String>();
		List<String> lexvos=new ArrayList<String>();
		langs=record.getLanguage();
		lexvos=record.getLexvo();
		
		//writer.println("\t<titles>");
		for(int i=0;i<titles.size();i++)
		{
			writer.println("\t<title>");
				writer.println("\t\t<value>"+titles.get(i).getValue()+"</value>");
				
				if(titles.get(i).getLang()!=null)
					writer.println("\t\t<lang>"+titles.get(i).getLang()+"</lang>");
				else
				{
					if(langs.size()==1)
						writer.println("\t\t<lang>"+langs.get(i)+"</lang>");
					else
						writer.println("\t\t<lang/>");
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
				writer.println("\t\t<value>"+descrs.get(i).getValue()+"</value>");
				
				if(titles.get(i).getLang()!=null)
					writer.println("\t\t<lang>"+descrs.get(i).getLang()+"</lang>");
				else
				{
					if(langs.size()==1)
						writer.println("\t\t<lang>"+langs.get(i)+"</lang>");
					else
						writer.println("\t\t<lang/>");
				}
			writer.println("\t</description>");
		}
		//writer.println("\t</descriptions>");

		//if(1==1)
		//	return;
		
		String uri=record.getApiid();
		writer.println("\t<id>"+uri+"</id>");
		writer.println("\t<uri>/cimmyt/"+type+"/"+uri+"</uri>");
		//if(1==1)
		//	return;
		
		List<CimmytRecord.Subject> subjects=new ArrayList<CimmytRecord.Subject>();
		subjects=record.getSubject();
		System.out.println(subjects.size());
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
				writer.println("\t\t<value>"+subjects.get(i).getValue()+"</value>");
				
				for(int j=i+1;j<subjects.size();j++)
				{
					if(subjects.get(i).getValue().equalsIgnoreCase(subjects.get(j).getValue()))
					{
						if(!subjects.get(j).getUri().equals(subjects.get(i).getUri()))
						{
							writer.println("\t\t<uri>"+subjects.get(j).getUri()+"</uri>");
							writer.println("\t\t<vocabulary>"+subjects.get(j).getVocabulary()+"</vocabulary>");
							writer.println("\t\t<score>"+subjects.get(j).getScore()+"</score>");
						}
					}
				}
				
				try
				{
					if(!subjects.get(i).getUri().equals("null"))
					{
						writer.println("\t\t<uri>"+subjects.get(i).getUri()+"</uri>");
						writer.println("\t\t<vocabulary>"+subjects.get(i).getVocabulary()+"</vocabulary>");
						writer.println("\t\t<score>"+subjects.get(i).getScore()+"</score>");
					}
				}
				catch(java.lang.NullPointerException e)
				{
					
				}
			writer.println("\t</subject>");
		}
		
		
		
		
		for(int i=0;i<langs.size();i++)
		{
			writer.println("\t<language>");
				writer.println("\t\t<value>"+langs.get(i)+"</value>");
				writer.println("\t\t<uri>"+lexvos.get(i)+"</uri>");
			writer.println("\t</language>");
		}
		
		List<XMLGregorianCalendar> created=record.getDate();
		for(int i=0;i<created.size();i++)
		{
			String date=created.get(i).toString();
			writer.println("\t<created>"+date+"</created>");
		}
		
		XMLGregorianCalendar updated=record.getUpdatedDate();
		writer.println("\t<updated>"+updated+"</updated>");
		
		
		
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
				writer.println("\t<type>"+types.get(i)+"</type>");
			}
		
			writer.println("\t<id>"+record.getApiid()+"</id>");
			writer.println("\t<uri>/cimmyt/resource/"+record.getApiid()+"</uri>");
			
			List<String> creators=new ArrayList<String>();
			creators=record.getCreator();
			
			for(int i=0;i<creators.size();i++)
			{
				int cid=creators.get(i).hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<creator>");
					writer.println("\t\t<value>"+creators.get(i)+"</value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/person/"+cid+"</uri>");
					writer.println("\t\t<type>person</type>");
				writer.println("\t</creator>");
			}
			
			List<String> contributors=new ArrayList<String>();
			contributors=record.getContributor();
			
			for(int i=0;i<contributors.size();i++)
			{
				int cid=contributors.get(i).hashCode();
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

			List<String> publishers=new ArrayList<String>();
			publishers=record.getPublisher();
			
			for(int i=0;i<publishers.size();i++)
			{
				int cid=publishers.get(i).hashCode();
				if(cid<0)
					cid*=-1;
				
				writer.println("\t<publisher>");
					writer.println("\t\t<value>"+publishers.get(i)+"</value>");
					writer.println("\t\t<id>"+cid+"</id>");
					writer.println("\t\t<uri>/cimmyt/organization/"+cid+"</uri>");
					writer.println("\t\t<type>organization</type>");
				writer.println("\t</publisher>");
			}

			List<XMLGregorianCalendar> dates=new ArrayList<XMLGregorianCalendar>();
			dates=record.getPubDate();
			
			for(int i=0;i<dates.size();i++)
			{
				writer.println("\t<date>"+dates.get(i)+"</date>");
			}
			
			dates=new ArrayList<XMLGregorianCalendar>();
			dates=record.getDate();
			
			for(int i=0;i<dates.size();i++)
			{
				writer.println("\t<updatedDate>"+dates.get(i)+"</updatedDate>");
			}

			List<String> issns=new ArrayList<String>();
			issns=record.getIssn();
			
			for(int i=0;i<issns.size();i++)
			{
				writer.println("\t<issn>"+issns.get(i)+"</issn>");
			}

			List<String> isbns=new ArrayList<String>();
			isbns=record.getIsbn();
			
			for(int i=0;i<isbns.size();i++)
			{
				writer.println("\t<isbn>"+isbns.get(i)+"</isbn>");
			}
			
			List<String> urls=new ArrayList<String>();
			urls=record.getUrl();
			
			for(int i=0;i<urls.size();i++)
			{
				boolean broken=exists(urls.get(i));
				writer.println("\t<url>");
					writer.println("\t\t<value>"+urls.get(i)+"</value>");
					writer.println("\t\t<broken>"+broken+"</id>");
				writer.println("\t</url>");
			}

			writer.println("\t<url>");
				writer.println("\t\t<value>http://repository.cimmyt.org/xmlui/handle/"+record.getDomainid().get(0)+
						"/"+record.getCdocid().get(0)+"</value>");
				writer.println("\t\t<broken>false</id>");
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
						writer.println("\t\t<uri>"+geonames.get(i)+"</uri>");
						writer.println("\t\t<vocabulary>geonames</vocabulary>");
						
						/*
						 * TODO:
						 * 	really parse file...
						 * */
						
						writer.println("\t\t<uri>http://www.fao.org/countryprofiles/geoinfo/geopolitical/resource/"+
								locations.get(i)+"</uri>");
						writer.println("\t\t<vocabulary>faogeopolitical</vocabulary>");
					}
					catch (IndexOutOfBoundsException e) {
						
					}
				writer.println("\t</location>");
			}

			List<String> regions=new ArrayList<String>();
			regions=record.getRegion();
			
			for(int i=0;i<regions.size();i++)
			{
				writer.println("\t<region>"+regions.get(i)+"</region>");
			}

			List<String> places=new ArrayList<String>();
			places=record.getPlace();
			
			for(int i=0;i<places.size();i++)
			{
				writer.println("\t<place>"+places.get(i)+"</place>");
			}

			List<String> pages=new ArrayList<String>();
			pages=record.getPages();
			
			for(int i=0;i<pages.size();i++)
			{
				writer.println("\t<page>"+pages.get(i)+"</page>");
			}

			List<String> series=new ArrayList<String>();
			series=record.getExtent();
			
			for(int i=0;i<series.size();i++)
			{
				writer.println("\t<extent>"+series.get(i)+"</extent>");
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

			List<String> rights=new ArrayList<String>();
			rights=record.getRights();
			
			for(int i=0;i<rights.size();i++)
			{
				writer.println("\t<rights>"+rights.get(i)+"</rights>");
			}

			List<String> citations=new ArrayList<String>();
			citations=record.getCitation();
			
			for(int i=0;i<citations.size();i++)
			{
				writer.println("\t<citation>"+citations.get(i)+"</citation>");
			}

			List<String> dois=new ArrayList<String>();
			dois=record.getDoi();
			
			for(int i=0;i<dois.size();i++)
			{
				writer.println("\t<doi>"+dois.get(i)+"</doi>");
			}

			List<String> formats=new ArrayList<String>();
			formats=record.getFormat();
			
			for(int i=0;i<formats.size();i++)
			{
				writer.println("\t<format>"+formats.get(i)+"</format>");
			}

			List<String> qualities=new ArrayList<String>();
			qualities=record.getQuality();
			
			for(int i=0;i<qualities.size();i++)
			{
				writer.println("\t<quality>"+qualities.get(i)+"</quality>");
			}

			List<String> resource_links=new ArrayList<String>();
			resource_links=record.getLinkToResource();

			List<String> resource_sizes=new ArrayList<String>();
			resource_sizes=record.getLinkToResourceSize();

			List<String> resource_types=new ArrayList<String>();
			resource_types=record.getLinkToResourceType();
			
			writer.println("\t<aggregation>");
				
				writer.println("\t\t<shownAt>");
					writer.println("\t\t\t<value>http://repository.cimmyt.org/xmlui/handle/"+record.getDomainid().get(0)+
						"/"+record.getCdocid().get(0)+"</value>");
					writer.println("\t\t\t<broken>false</broken>");
				writer.println("\t\t</shownAt>");
				
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
					
					writer.println("\t\t<shownBy>");
						writer.println("\t\t\t<value>"+resource_links.get(i)+"</value>");
						writer.println("\t\t\t<broken>false</broken>");
					writer.println("\t\t</shownBy>");
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
						writer.println("\t\t\t<type>"+resource_types.get(i)+"</type>");
						writer.println("\t\t\t<size>"+resource_sizes.get(i)+"</size>");
					writer.println("\t\t</linkToResource>");
				}	
				
				
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
			
			
			
		writer.println("</resource>");
		writer.close();
	}
	
	
	
	protected static void writePersons(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		List<String> persons=new ArrayList<String>();
		persons=record.getCreator();
		for(int i=0;i<persons.size();i++)
		{
			int id=persons.get(i).hashCode();
			if(id<0)
				id*=-1;
			
			CimmytPerson person=new CimmytPerson();
			person.id=String.valueOf(id);
			person.uri="/cimmyt/person/"+id;
			person.name=persons.get(i);

			PrintWriter writer = new PrintWriter(folder+File.separator+id+".object.xml", "UTF-8");
			
			writer.println("<object>");
				writer.println("\t<type>person</type>");
				
				writer.println("\t<title>");
					writer.println("\t\t<value>"+person.name+"</value>");
					writer.println("\t\t<lang/>");
				writer.println("\t</title>");

				writer.println("\t<description/>");
				writer.println("\t<subject/>");
				
				writer.println("\t<id>"+person.id+"</id>");
				writer.println("\t<uri>/cimmyt/person/"+person.id+"</uri>");
				
				writer.println("\t<language/>");
				writer.println("\t<created/>");
				writer.println("\t<updated/>");
				
			writer.println("</object>");
			writer.close();
			
			writer = new PrintWriter(folder+File.separator+id+".person.xml", "UTF-8");

			String[] fn_ln=person.name.split(", ");
			person.first_name=fn_ln[0];
			person.last_name=fn_ln[1];

			Freme freme_enricher=new Freme();
			person.orcid="";
			try {
				person.orcid=freme_enricher.enrichPersons(person.first_name+" "+person.last_name);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.println("<person>");
			
			writer.println("</person>");
			writer.close();
		}
	}
	
	
	public static boolean exists(String URLName){
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
	       e.printStackTrace();
	       return false;
	    }
	  }  
	
}
















