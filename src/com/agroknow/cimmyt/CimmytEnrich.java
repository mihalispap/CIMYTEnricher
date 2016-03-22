package com.agroknow.cimmyt;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	}

	void enrichDVN(CimmytRecord record)
	{
		/*TODO*/
	}
	
	void enrichDSpace(CimmytRecord record) throws Exception
	{
		/*
		 * 	SAMPLE:
		 * 		http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier=oai:repository.cimmyt.org:10883/538&metadataPrefix=didl
		 * 
		 * */
		
		String url="http://repository.cimmyt.org/oai/request?verb=GetRecord&identifier=oai:repository.cimmyt.org:10883/538&metadataPrefix=didl";
		
		
		
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




















