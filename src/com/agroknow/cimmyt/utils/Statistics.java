package com.agroknow.cimmyt.utils;

public class Statistics {

	private int no_enrichments=0;
	
	public void update(int value){no_enrichments+=value;}
	public int getEnrichments(){return no_enrichments;}
}
