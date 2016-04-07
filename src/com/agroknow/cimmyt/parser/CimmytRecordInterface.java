package com.agroknow.cimmyt.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.agroknow.cimmyt.CimmytSubject;

public class CimmytRecordInterface extends CimmytRecord
{
	public void updateSubject(String s, String v, String u)
	{
		for(int i=0;i<this.subject.size();i++)
		{
			if(subject.get(i).value.equals(s))
			{
				subject.get(i).vocabulary=v;
				subject.get(i).uri=u;
				return;
			}
		}
	}
	
	public void changeProducer(String from, String to)
	{
		List<String> producers=new ArrayList<String>();
		producers=this.getPublisher();
		
		for(int i=0;i<producers.size();i++)
		{
			if(producers.get(i).equals(from))
			{
				producers.remove(i);
				producers.add(to);
				return;
			}
		}
	}

    public void addGeonames(String geo)
    {
    	if (geonames == null) {
    		geonames = new ArrayList<String>();
        }

    	int i;
    	for(i=0;i<geonames.size();i++)
    	{
    		if(geonames.get(i).equalsIgnoreCase(geo))
    			break;
    	}
    	
    	if(i==geonames.size())
    		geonames.add(geo);
    }

    public void addLocation(String loc)
    {
    	if (location == null) {
    		location = new ArrayList<String>();
        }
    	
    	int i;
    	for(i=0;i<location.size();i++)
    	{
    		if(location.get(i).equalsIgnoreCase(loc))
    			break;
    	}
    	
    	if(i==location.size())
    		location.add(loc);
    }


    public void addSeries(String sr)
    {
    	if (extent == null) {
    		extent = new ArrayList<String>();
        }
    	extent.add(sr);
    }


    public void addPages(String pg)
    {
    	if (pages == null) {
    		pages = new ArrayList<String>();
        }
    	pages.add(pg);
    }

    public void addPlace(String pl)
    {
    	if (place == null) {
    		place = new ArrayList<String>();
        }
    	place.add(pl);
    }

    public void addRegion(String reg)
    {
    	if (region == null) {
    		region = new ArrayList<String>();
        }
    	region.add(reg);
    }

    public void addResourceLink(String link)
    {
    	if (linkToResource == null) {
    		linkToResource = new ArrayList<String>();
        }

    	int i;
    	for(i=0;i<linkToResource.size();i++)
    	{
    		if(linkToResource.get(i).equalsIgnoreCase(link))
    			break;
    	}
    	
    	if(i==linkToResource.size())
    		linkToResource.add(link);
    }

    public void addResourceType(String type)
    {
    	if (linkToResourceType == null) {
    		linkToResourceType = new ArrayList<String>();
        }
    	linkToResourceType.add(type);
    }

    public void addLabel(String l)
    {
    	if (linkToResourceLabel == null) {
    		linkToResourceLabel = new ArrayList<String>();
        }
    	linkToResourceLabel.add(l);
    }


    public void addCategory(String c)
    {
    	if (linkToResourceCategory == null) {
    		linkToResourceSize = new ArrayList<String>();
        }
    	linkToResourceSize.add(c);
    }


    public void addResourceLinkSize(String size)
    {
    	if (linkToResourceSize == null) {
    		linkToResourceSize = new ArrayList<String>();
        }
    	linkToResourceSize.add(size);
    }


    public void addSubject(CimmytSubject subject)
    {
    	CimmytRecord.Subject s=new CimmytRecord.Subject();
    	
    	s.generated=true;
    	s.uri=subject.uri;
    	s.value=subject.value;
    	s.vocabulary=subject.vocabulary;
    	s.score=Double.valueOf(subject.score);
    	
    	this.subject.add(s);
    	System.out.println("---ADD---\n"+this.subject.get(this.subject.size()-1).value+"\n-----\n");
    	//this.subject.add(subject);
    }
    
    public void getListOfFields()
    {
    	Field[] fields = this.getClass().getDeclaredFields();
	    System.out.printf("%d fields:%n", fields.length);
	    for (Field field : fields) {
	        System.out.printf("%s %s %s%n",
	            Modifier.toString(field.getModifiers()),
	            field.getType().getSimpleName(),
	            field.getName()
	        );
	    }
    }
}
