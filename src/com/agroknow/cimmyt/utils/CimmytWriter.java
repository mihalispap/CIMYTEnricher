package com.agroknow.cimmyt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.agroknow.cimmyt.parser.CimmytRecord;

public class CimmytWriter 
{
	public static void write2File(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println(record.getSubject().toString());
		
		
		writeObject(record,folder);
		
		 
	}
	
	protected static void writeObject(CimmytRecord record, String folder) throws FileNotFoundException, UnsupportedEncodingException
	{

		PrintWriter writer = new PrintWriter(folder+File.separator+record.getApiid()+".object.xml", "UTF-8");
		writer.println("<object>\n\t");


		List<CimmytRecord.Title> titles=new ArrayList<CimmytRecord.Title>();
				
		String handler=record.getHandler();
		String type=null;
		
		if(handler.contains("repository.cimmyt"))
			type="resource";
		else if(handler.contains("data.cimmyt"))
			type="dataset/software";
		
		writer.println("\t<type>"+type+"</type>");
		
		titles=record.getTitle();

		for(int i=0;i<titles.size();i++)
		{
			writer.println("\t<title>");
				writer.println("\t\t<value>"+titles.get(i).getValue()+"</value>");
				writer.println("\t\t<lang>"+titles.get(i).getLang()+"</lang>");
			writer.println("\t</title>\n");
		}


		if(1==1)
			return;
		
		String uri=record.getApiid();
		writer.println("\t<uri>"+uri+"</uri>");
		
		if(1==1)
			return;
		
		List<String> langs=new ArrayList<String>();
		List<String> lexvos=new ArrayList<String>();
		
		langs=record.getLanguage();
		lexvos=record.getLexvo();
		
		for(int i=0;i<langs.size();i++)
		{
			writer.println("\t<language>");
				writer.println("\t\t<value>"+langs.get(i)+"</value>");
				writer.println("\t\t<uri>"+lexvos.get(i)+"</uri>");
			writer.println("\t</language>");
		}
		
		writer.println("</object>");
		writer.close();
	}
	
}
















