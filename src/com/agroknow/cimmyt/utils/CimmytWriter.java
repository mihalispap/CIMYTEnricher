package com.agroknow.cimmyt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.agroknow.cimmyt.parser.CimmytRecord;
import com.agroknow.cimmyt.parser.CimmytRecordInterface;

public class CimmytWriter 
{
	public static void write2File(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println(record.getSubject().toString());
		
		//CimmytRecord record=new C
		writeObject(record,folder);
		
		 
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
		
		/*
		writer.println("\t<about>");

			writer.println("\t\t<handler>"+handler+"</handler>");
			
			writer.println("\t\t<sets>");
				List<String> sets=record.getCset();
				List<String> setsid=record.getSetid();
				
				for(int i=0;i<sets.size();i++)
				{
					writer.println("\t\t\t<set>");
					
						writer.println("\t\t\t\t<value>"+sets.get(i)+"</value>");
						writer.println("\t\t\t\t<id>"+setsid.get(i)+"</id>");
						writer.println("\t\t\t\t<type>collection</type>");
						writer.println("\t\t\t\t<uri>/cimmyt/collection/"+setsid.get(i)+"</uri>");
					
					writer.println("\t\t\t</set>");
				}
			
			writer.println("\t\t</sets>");
			
			List<String> domainid=record.getDomainid();
			
			for(int i=0;i<domainid.size();i++)
			{
				writer.println("\t\t<cimmytDomainId>"+domainid.get(i)+"</cimmytDomainId>");
			}

			List<String> docid=record.getCdocid();
			
			for(int i=0;i<docid.size();i++)
			{
				writer.println("\t\t<cimmytDocId>"+docid.get(i)+"</cimmytDocId>");
			}
		
		writer.println("\t</about>");
		*/
		
		
		writer.println("</object>");
		writer.close();

		//record.getListOfFields();
		
	}
	
}
















