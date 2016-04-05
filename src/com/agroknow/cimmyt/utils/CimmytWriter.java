package com.agroknow.cimmyt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
		writePersons(record,folder);
		 
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
	
}
















