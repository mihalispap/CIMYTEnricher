package com.agroknow.cimmyt;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.agroknow.cimmyt.parser.CimmytRecord;
import com.agroknow.cimmyt.parser.CimmytRecordInterface;
import com.agroknow.cimmyt.utils.CimmytWriter;

public class Enrich {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Run"); 


			// TODO Auto-generated method stub

			//System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");

			//System.out.println(JAXBContext.newInstance("com.agroknow.cimmyt.parser",
			//		CimmytRecord.class.getClassLoader()));


			
			//if(true)
			//	return;
			
	        if (args.length != 2) {
	            System.err.println("Usage: param1(inputdir) param2(outputdir)");                
	            System.exit(1);
	        } 
			String output=args[1];
	        File file = new File(output);
			file.mkdirs();
	        
	        String folder_path=args[0];
	        
	        File folder = new File(folder_path);
	        //File[] listOfFiles = folder.listFiles();

	        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
	            public boolean accept(File dir, String name) {
	                return name.toLowerCase().endsWith(".xml");
	            }
	        });
	        
	        //folder.list
	        
	        for (int i = 0; i < listOfFiles.length; i++) 
	        {
	        	if (listOfFiles[i].isFile()) 
	        	{
	        		System.out.println("File " + listOfFiles[i].getName());
	        	} 
	        	else if (listOfFiles[i].isDirectory()) 
	        	{
	        		System.out.println("Directory " + listOfFiles[i].getName());
	            }
	        }
	        

	        JAXBContext jaxbContext=null;
	        try {
				jaxbContext = JAXBContext.newInstance("com.agroknow.cimmyt.parser");
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
	        
	        Unmarshaller unmarshaller=null;
	        try {
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
	        
	        for(int i=0;i<listOfFiles.length;i++)
	        {
	        	JAXBElement<CimmytRecord> record;
	    		try {

	    			record = (JAXBElement<CimmytRecord>) 
	    						unmarshaller.unmarshal(new File(listOfFiles[i].getAbsolutePath()));
	    			
	    			System.out.println(listOfFiles[i].getAbsolutePath());
	    			CimmytRecord crec = (CimmytRecord) record.getValue();
	    			
	    			System.out.println(crec.toString());
	    			
	    		} catch (JAXBException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        }
	        
	         

	        JAXBContext jc=null;
			try {
				jc = JAXBContext.newInstance( "com.agroknow.cimmyt.parser" );
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // create an Unmarshaller
	        Unmarshaller u=null;
			try {
				u = jc.createUnmarshaller();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // unmarshal a FosterHome instance document into a tree of Java content
	        // objects composed of classes from the com.abhi.xml.jaxb.generated 
	        // package.
	        JAXBElement<?> fhElement;
			try {
				
				for(int i=0;i<listOfFiles.length;i++)
				{
				
					fhElement = (JAXBElement<?>)u.unmarshal(new FileInputStream(listOfFiles[i].getAbsolutePath()));
	
					CimmytRecord record = (CimmytRecord) fhElement.getValue();
			        //System.out.println(record.getApiid());
			        
			        CimmytEnrich enricher=new CimmytEnrich();
			        try {
						enricher.enrich(record);
						
						//System.out.println(record.getLinkToResourceSize());
						
						
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
					CimmytWriter writer=new CimmytWriter();
					try {
						CimmytWriter.write2File(record,output);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Finished with:"+record.getApiid());
				}
		        
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		
		
		
	

}
