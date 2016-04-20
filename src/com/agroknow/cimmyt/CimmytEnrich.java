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
import javax.xml.xpath.XPathExpressionException;
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
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		catch (java.lang.NullPointerException e)
		{
			//e.printStackTrace();
		}

		//System.out.println("Subjecrt size:"+subjects.size());
		
		for(int i=0;i<subjects.size();i++)
			record.addSubject(subjects.get(i));
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
					else if(toCheck.equalsIgnoreCase(geonames[2]))
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
					else if(toCheck.equalsIgnoreCase(geonames[2]))
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
			}for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"countries.db");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
				String strLine;
				while ((strLine = br.readLine()) != null)   
				{
	
					String[] geonames=strLine.split("\t");
				  
					boolean found=false;
					String geonames_id="";
					
					if(toCheck.equalsIgnoreCase(geonames[4]))
					{
							found=true;
							geonames_id=geonames[16];
					}
					if(found)
					{
						record.addLocation(toCheck);
						record.addGeonames("http://sws.geonames.org/"+geonames_id);
						break;
					}
				}
				br.close();
			}for(int j=0;j<value.size();j++)
			{
				String toCheck;
				
				toCheck=value.get(j).replace(",", "");
				toCheck=value.get(j).replace("(", "");
				toCheck=value.get(j).replace(")", "");
				
				FileInputStream fstream = new FileInputStream(absolute_path+"continents.db");
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
					else if(toCheck.equalsIgnoreCase(geonames[2]))
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
					
					//System.out.println("Comparing:"+value[j]+" with:"+geonames[1]);
	
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
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10588&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/otherMat");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			//System.out.println(j+")"+attributes.item(j).getTextContent());
        			if(attributes.item(j).getNodeName().equals("URI"))
        				record.addResourceLink(attributes.item(j).getTextContent());
        		}
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/otherMat/labl");
        
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/otherMat/notes");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		record.addCategory(nl.item(i).getTextContent());        		
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/citation/prodStmt/producer");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		int j;
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		for(j=0;j<attributes.getLength();j++)
        		{
        			//System.out.println("Going to compare:\n------\n"+attributes.item(j).getNodeName()+"\n---");
        			if(attributes.item(j).getNodeName().equals("abbr"))
        				break;
        		}
        		if(j!=attributes.getLength())
        			record.changeProducer(nl.item(i).getTextContent(), attributes.item(j).getTextContent());
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/citation/prodStmt/producer");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		int j;
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		for(j=0;j<attributes.getLength();j++)
        		{
        			//System.out.println("Going to compare:\n------\n"+attributes.item(j).getNodeName()+"\n---");
        			if(attributes.item(j).getNodeName().equals("abbr"))
        				break;
        		}
        		if(j!=attributes.getLength())
        			record.changeProducer(nl.item(i).getTextContent(), attributes.item(j).getTextContent());
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/stdyInfo/subject/keyword");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
       
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		String voc="";
        		String uri="";
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("vocab"))
        				voc=attributes.item(j).getTextContent();
        			if(attributes.item(j).getNodeName().equals("vocabURI"))
        				uri=attributes.item(j).getTextContent();
        		}
        		if(voc.length()>0)
        		{
        			record.updateSubject(nl.item(i).getTextContent(),voc,uri);
        		}
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/stdyInfo/sumDscr/nation");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
       
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		String[] values=nl.item(i).getTextContent().split("; ");
        		
        		for(int j=0;j<values.length;j++)
        			record.addRegion(values[j]);
        	}
        }

        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/stdyInfo/sumDscr/geogCover");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
       
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		String[] values=nl.item(i).getTextContent().split("; ");
        		
        		for(int j=0;j<values.length;j++)
        			record.addRegion(values[j]);
        	}
        }        
        
	}

	public String extractKindOfData(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "stdyInfo/sumDscr/dataKind");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	return nl.item(0).getTextContent().toLowerCase();
        }
        return "";
	}

	public String extractNotesDVN(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        String notes="";
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "notes");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        //System.out.println("SIZE:"+nl.getLength());
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		int j;
        		String type="";
        		String subject="";
        		boolean to_write=true;
        		
        		for(j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("type"))
        			{
        				type=attributes.item(j).getTextContent();
        				if(!attributes.item(j).getTextContent().contains("Program")
        						&& !attributes.item(j).getTextContent().contains("Abbreviation"))
        				{
        					to_write=false;
        				}
        			}
        			if(attributes.item(j).getNodeName().equals("subject"))
        			{
        				subject=attributes.item(j).getTextContent();
        			}
        		}
        		
        		if(to_write)
        			notes+="\n\t<notes>"+type+": "+subject+"</notes>";
        			
        	}
        }
        return notes;
	}

	public String extractTimePeriod(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "stdyInfo/sumDscr/timePrd");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0 && nl.getLength()>1)
        {
        	return nl.item(0).getTextContent()+"-"+nl.item(1).getTextContent();
        }
        return "";
	}

	public String extractFunding(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "/citation/prodStmt/fundAg");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	return nl.item(0).getTextContent();
        }
        return "";
	}

	public List<String> extractProgramDVN(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		List<String> values=new ArrayList<String>();
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "notes");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        //System.out.println("SIZE:"+nl.getLength());
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("type"))
        			{
        				if(attributes.item(j).getTextContent().contains("Program")
        						&& !attributes.item(j).getTextContent().contains("Abbreviation"))
        				{
        					values.add(attributes.item(j).getTextContent());
        				}
        			}
        		}
        	}
        }
        return values;
	}

	public List<String> extractProgramNameDVN(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		List<String> values=new ArrayList<String>();
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "notes");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		String subject="";
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("subject"))
        			{
        				subject=attributes.item(j).getTextContent();
        			}
        			if(attributes.item(j).getNodeName().equals("type"))
        			{
        				if(attributes.item(j).getTextContent().contains("Program")
        						&& !attributes.item(j).getTextContent().contains("Abbreviation"))
        				{
        					values.add(subject);
        				}
        			}
        		}
        	}
        }
        return values;
	}

	public List<String> extractAbbreviationDVN(CimmytRecord record) throws Exception
	{
		String domain_id=record.getDomainid().get(0);
		String doc_id=record.getCdocid().get(0);
		
		String url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:"
				+ ""+domain_id+"/"+doc_id+"&metadataPrefix=ddi";
		
		//url="http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&identifier=hdl:11529/10394&metadataPrefix=ddi";
		
		List<String> values=new ArrayList<String>();
		
		URL url2 = new URL(url);
        URLConnection connection = url2.openConnection();

        Document doc = parseXML(connection.getInputStream());
        
        
        NodeList descNodes = 
        			doc.getLastChild().getChildNodes();
       
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr/"
        		+ "notes");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("subject"))
        			{
        				if(attributes.item(j).getTextContent().contains("Program Abbreviation"))
        				{
        					values.add(attributes.item(j).getTextContent());
        				}
        			}
        		}
        	}
        }

        return values;
	}
	
	public void enrichOrganizationDVN(CimmytRecord record, CimmytOrganization organization) throws Exception
	{
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
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/prodStmt/producer");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		String abbr;
        		
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("abbr"))
        			{
        				organization.full_name=nl.item(i).getTextContent();
        				
        				abbr=attributes.item(j).getTextContent();
        				
        				if(!abbr.equals(organization.name))
        					continue;
        				
                		expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
                        		+ "/citation/prodStmt/producer[@abbr='"+abbr+"']/ExtLink");
                		NodeList nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                        
                		//System.out.println("---\n"+nl2.getLength()+"\n-----");
                		
                        if(nl2.getLength()!=0)
                        {
                        	for(int k=0;k<nl2.getLength();k++)
                        	{
                        		NamedNodeMap attributes_in=nl2.item(k).getAttributes();
                        		
                        		if(attributes_in.getLength()==1 && attributes_in.item(0).getNodeName().equals("URI"))
                        		{
                        			organization.url=attributes_in.item(0).getTextContent();
                        		}
                        		else if(attributes_in.getLength()==2)
                        		{
                        			if(attributes_in.item(0).getNodeName().equals("URI")
                        					&& attributes_in.item(1).getTextContent().equals("image"))
                        			{
                        				organization.logo=attributes_in.item(0).getTextContent();
                        			}
                        		}
                        	}
                        }
        			}
        				
        		}
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/distStmt/distrbtr");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		String abbr;
        		
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("abbr"))
        			{
        				organization.full_name=nl.item(i).getTextContent();
        				
        				abbr=attributes.item(j).getTextContent();
        				
        				if(!abbr.equals(organization.name))
        					continue;
        				
                		expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
                        		+ "/citation/distStmt/distrbtr[@abbr='"+abbr+"']/ExtLink");
                		NodeList nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                        
                		//System.out.println("---\n"+nl2.getLength()+"\n-----");
                		
                        if(nl2.getLength()!=0)
                        {
                        	for(int k=0;k<nl2.getLength();k++)
                        	{
                        		NamedNodeMap attributes_in=nl2.item(k).getAttributes();
                        		
                        		if(attributes_in.getLength()==1 && attributes_in.item(0).getNodeName().equals("URI"))
                        		{
                        			organization.url=attributes_in.item(0).getTextContent();
                        		}
                        		else if(attributes_in.getLength()==2)
                        		{
                        			if(attributes_in.item(0).getNodeName().equals("URI")
                        					&& attributes_in.item(1).getTextContent().equals("image"))
                        			{
                        				organization.logo=attributes_in.item(0).getTextContent();
                        			}
                        		}
                        	}
                        }
        			}
        				
        		}
        	}
        }
        
	}

	public void enrichPersonDVN(CimmytRecord record, CimmytPerson person) throws Exception
	{
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
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/distStmt/contact");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		//System.out.println("COMPARING:"+person.name+", with:"+nl.item(i).getTextContent());
        		if(!person.name.equals(nl.item(i).getTextContent()))
        			continue;
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("email"))
        				person.contact=attributes.item(j).getTextContent();
        			if(attributes.item(j).getNodeName().equals("affiliation"))
        				person.affiliation_name=attributes.item(j).getTextContent();
        		}
        		//System.out.println(person.affiliation_name+", "+person.contact);
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/rspStmt/AuthEnty");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		//System.out.println("COMPARING:"+person.name+", with:"+nl.item(i).getTextContent());
        		if(!person.name.equals(nl.item(i).getTextContent()))
        			continue;
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("affiliation"))
        				person.affiliation_name=attributes.item(j).getTextContent();
        		}
        		//System.out.println(person.affiliation_name+", "+person.contact);
        	}
        }
        
        
	}

	public List<String> extractPersonsDVN(CimmytRecord record) throws XPathExpressionException, Exception
	{
		List<String> persons=new ArrayList<String>();
		
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
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/distStmt/contact");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		String person=nl.item(i).getTextContent();
        		//if(!person.contains(","))
        		//	person=person.replace(" ", ", ");
        		persons.add(person);
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/distStmt/depositr");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		persons.add(nl.item(i).getTextContent());
        	}
        }

		
		return persons;
	}

	public List<String> extractOrganizationsDVN(CimmytRecord record) throws XPathExpressionException, Exception
	{
		List<String> organizations=new ArrayList<String>();
		
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
        XPathExpression expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/rspStmt/AuthEnty");
        
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("affiliation"))
        				organizations.add(attributes.item(j).getTextContent());
        		}
        		//System.out.println(person.affiliation_name+", "+person.contact);
        	}
        }
        
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/codeBook/stdyDscr"
        		+ "/citation/distStmt/contact");
        
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        
        if(nl.getLength()!=0)
        {
        	for(int i=0;i<nl.getLength();i++)
        	{
        		
        		NamedNodeMap attributes=nl.item(i).getAttributes();
        		
        		for(int j=0;j<attributes.getLength();j++)
        		{
        			if(attributes.item(j).getNodeName().equals("affiliation"))
        				organizations.add(attributes.item(j).getTextContent());
        		}
        		//System.out.println(person.affiliation_name+", "+person.contact);
        	}
        }
        
		return organizations;
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
        			//System.out.println(j+")"+value+", name:"+name);
        			
        			if(name.equals("mimeType"))
        				record.addResourceType(value);
        			if(name.equals("ref"))
        				record.addResourceLink(value);
        			
        			/*TODO: perhaps on everything?*/
        			if(value.endsWith(".pdf"))
        			{
        				//System.out.println("In here..");
        				URL urltopdf = new URL(value);
        				int size=getFileSize(urltopdf);
        				
        				if(size!=-1)
        					record.addResourceLinkSize(String.valueOf(size));
        				//System.out.println(size);
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='country']"
        		+ "/element[@name='focus']/element/field[@name='value']");
        
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='region']"
        		+ "/element[@name='focus']/element/field[@name='value']");
        
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='series']"
        		+ "/element[@name='number']/element/field[@name='value']");
        
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='place']"
        		+ "/element/field[@name='value']");
        
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
        expr = xpath.compile("/OAI-PMH/GetRecord/record/metadata/metadata/element[@name='dc']/element[@name='pages']"
        		+ "/element/field[@name='value']");
        
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
	        
	        //System.out.println(conn.getResponseCode());
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




















