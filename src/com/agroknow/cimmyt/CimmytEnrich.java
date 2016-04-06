package com.agroknow.cimmyt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jettison.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.agroknow.cimmyt.external.Freme;
import com.agroknow.cimmyt.parser.CimmytRecord;
import com.agroknow.cimmyt.parser.CimmytRecordInterface;

public class CimmytEnrich 
{
	
	void enrich(CimmytRecord record) throws Exception
	{
		if(record.getHandler().contains("repository"))
			this.enrichDSpace(record);
		else if(record.getHandler().contains("data.cimmyt"))
			this.enrichDVN(record);
		
		this.enrichFreme(record);
		this.enrichGeographical(record);
		
		
		//this.cleanse(record);
		
	}

	void enrichFreme(CimmytRecord record)
	{
		Freme freme_enricher=new Freme();
		List<CimmytSubject> subjects=new ArrayList<CimmytSubject>();
		
		try 
		{
			for(int i=0;i<record.getDescription().size();i++)
				subjects.addAll(freme_enricher.enrichSubjects(record.getDescription().get(i).getValue()));
			for(int i=0;i<record.getSubject().size();i++)
				subjects.addAll(freme_enricher.enrichSubjects(record.getSubject().get(i).getValue()));
			for(int i=0;i<record.getTitle().size();i++)
				subjects.addAll(freme_enricher.enrichSubjects(record.getTitle().get(i).getValue()));
			
			for(int i=0;i<subjects.size();i++)
				record.addSubject(subjects.get(i));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (java.lang.NullPointerException e)
		{
			e.printStackTrace();
		}
		
	}
	
	void enrichGeographical(CimmytRecord record) throws IOException
	{
		for(int i=0;i<record.getRegion().size();i++)
		{
		
			List<String> value = new ArrayList<String>();//
			value=record.getRegion();
			
			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
					+ "assets"+System.getProperty("file.separator");
			
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities1000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities15000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities5000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"null.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
		}

		if(true)
			return;
		
		for(int i=0;i<record.getPlace().size();i++)
		{
		
			List<String> value = new ArrayList<String>();//
			value=record.getRegion();
			
			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
					+ "assets"+System.getProperty("file.separator");
			
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities1000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities15000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities5000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"null.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
		}
		
		if(true)
			return;
		
		
		
		for(int i=0;i<record.getDescription().size();i++)
		{
		
			String[] value=record.getDescription().get(i).getValue().split(" ");
			
			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
					+ "assets"+System.getProperty("file.separator");
			
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities1000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities15000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";

					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities5000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";

					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"null.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
	
					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
		}
		
		for(int i=0;i<record.getSubject().size();i++)
		{
		
			String[] value=record.getSubject().get(i).getValue().split(" ");
			
			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
					+ "assets"+System.getProperty("file.separator");
			
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities1000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities15000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";

					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"cities5000.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";

					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
			for(int j=0;j<value.length;j++)
			{
				value[j]=value[j].replace(",", "");
				value[j]=value[j].replace("(", "");
				value[j]=value[j].replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"null.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					System.out.println("Comparing:"+value[j]+" with:"+geonames[1]);
	
					if(value[j].equalsIgnoreCase(geonames[1]))
					{
							found=true;
							geonames_id=geonames[0];
					}
					if(found)
					{
						record.addLocation(value[j]);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}
		}
	}
	
	void enrichDVN(CimmytRecord record) throws Exception
	{
		/*
		 * 
		 * http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10201&metadataPrefix=ddi
		 * 
		 * */	

		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/otherMat");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("URI"))
        				record.addResourceLink(attributes.item(j).getTextContent());
        		}
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/otherMat/labl");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addLabel(nl.item(i).getTextContent());        		
        	}
        }


        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/otherMat/notes");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addCategory(nl.item(i).getTextContent());        		
        	}
        }

	}
	
	void enrichDSpace(CimmytRecord record) throws Exception
	{
		/*
		 * 	SAMPLE:
		 * 		http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier=oai:repository.cimmyt.org:10883/538&metadataPrefix=didl
		 * 
		 * */
		
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier="
				+ "oai:repository.cimmyt.org:"+domain_id+"/"+doc_id+"&metadataPrefix=didl";
		
		
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/DIDL/Item/Component/Resource");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			String value=attributes.item(j).getTextContent();
        			String name=attributes.item(j).getNodeName();
        			System.out.println(j+")"+value+", name:"+name);
        			
        			if(name.equals("mimeType"))
        				record.addResourceType(value);
        			if(name.equals("ref"))
        				record.addResourceLink(value);
        			
        			/*TODO: perhaps on everything?*/
        			if(value.endsWith(".pdf"))
        			{
        				System.out.println("In here..");
        				URL urltopdf = new URL(value);
        				int size=getFileSize(urltopdf);
        				
        				if(size!=-1)
        					record.addResourceLinkSize(String.valueOf(size));
        				System.out.println(size);
        			}
        			
        		}
        	}
        }
        
        
        /*
         * TODO:
         * 	view-source:http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier=
         * 		oai:repository.cimmyt.org:10883/538&metadataPrefix=xoai
         * 
         * */

		url="http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier="
				+ "oai:repository.cimmyt.org:"+domain_id+"/"+doc_id+"&metadataPrefix=xoai";
		
		url2 = new URL(url);
        connection = url2.openConnection();

        doc = parseXML(connection.getInputStream());
        descNodes = doc.getLastChild().getChildNodes();

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='country']"
        		+ "/element[@name='focus']/element[@name='en_US']/field[@name='value']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addRegion(nl.item(i).getTextContent());        		
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='region']"
        		+ "/element[@name='focus']/element[@name='en_US']/field[@name='value']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addRegion(nl.item(i).getTextContent());        		
        	}
        }
		
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='series']"
        		+ "/element[@name='number']/element[@name='en_US']/field[@name='value']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addSeries(nl.item(i).getTextContent());
        		//System.out.println("asdasdasda:"+record.getExtent().get(0));
        	}
        }
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='place']"
        		+ "/element[@name='en_US']/field[@name='value']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addPlace(nl.item(i).getTextContent());        		
        	}
        }
		
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='pages']"
        		+ "/element[@name='en_US']/field[@name='value']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addPages(nl.item(i).getTextContent());        		
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='bundles']"
        		+ "/element[@name='bundle']/element[@name='bitstreams']/element[@name='bitstream']"
        		+ "/field[@name='format']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addResourceType(nl.item(i).getTextContent());
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='bundles']"
        		+ "/element[@name='bundle']/element[@name='bitstreams']/element[@name='bitstream']"
        		+ "/field[@name='url']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addResourceLink(nl.item(i).getTextContent());
        		
        	}
        }
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='bundles']"
        		+ "/element[@name='bundle']/element[@name='bitstreams']/element[@name='bitstream']"
        		+ "/field[@name='size']");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addResourceLinkSize(nl.item(i).getTextContent());
        		//System.out.println("added:"+nl.item(i).getTextContent());
        	}
        }
		
	}
	
	public void enrichCollection(CimmytCollection collection) throws Exception
	{
		String url=collection.handler+"?verb=Identify";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/Identify/repositoryName");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		collection.repoName=nl.item(i).getTextContent();
        	}
        }
        
        url=collection.handler+"?verb=ListMetadataFormats";
		
		url2 = new URL(url);
        connection = url2.openConnection();

        doc = parseXML(connection.getInputStream());
        
        
        descNodes = doc.getLastChild().getChildNodes();

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/ListMetadataFormats/metadataFormat/metadataPrefix");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		collection.metadataNames.add(nl.item(i).getTextContent());        		
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/ListMetadataFormats/metadataFormat/metadataNamespace");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		collection.metadataURIs.add(nl.item(i).getTextContent());        		
        	}
        }

        url=collection.handler+"?verb=ListSets";
		
		url2 = new URL(url);
        connection = url2.openConnection();
        
        doc = parseXML(connection.getInputStream());
        descNodes = doc.getLastChild().getChildNodes();

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/ListSets/set/setName");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        //System.out.println("My Length:"+nl.getLength());
        List<String> set_names=new ArrayList<String>();
        for(int i=0;i<nl.getLength();i++)
        	set_names.add(nl.item(i).getTextContent());
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/ListSets/set/setSpec");
        
        NodeList nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        //System.out.println("LENGTH:::"+nl2.getLength()+"|"+set_names.size());
        if(nl2.getLength()!=0)
        {
        	
        	for(int i=0;i<nl2.getLength();i++)
        	{
        		//System.out.println("SET("+i+"): "+nl2.item(i).getTextContent());
        		if(nl2.item(i).getTextContent().equals(collection.spec))
        			collection.name=set_names.get(i);        		
        	}
        }
	}
	
	private int getFileSize(URL url) {
	    HttpURLConnection conn = null;
	    try {
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("HEAD");
	        conn.getInputStream();
	        
	        System.out.println(conn.getResponseCode());
	        if(conn.getResponseCode()==200)
	        	return conn.getContentLength();
	        return -1;
	    } catch (IOException e) {
	        return -1;
	    } finally {
	        conn.disconnect();
	    }
	}
	
	/*
	private void cleanse(CimmytRecord record)
	{
		List<CimmytRecord.Subject> subjects=new ArrayList<CimmytRecord.Subject>();
		subjects=record.getSubject();
		//System.out.println(subjects.size());
		for(int i=0;i<subjects.size();i++)
		{
			for(int j=i+1;j<subjects.size();j++)
			{
				if(subjects.get(i).getValue().equals(subjects.get(j).getValue()))
				{
					
				}
			}
		}
	}
	*/
	
	private Document parseXML(InputStream stream)
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




















