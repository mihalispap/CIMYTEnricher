package com.agroknow.cimmyt.external;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class DBPedia 
{
	public String queryDBPedia(String value)
	{
		String sparqlQuery;
		
		sparqlQuery=""
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
					+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
					+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
					+"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
					+"PREFIX dc: <http://purl.org/dc/elements/1.1/> "
					+"PREFIX : <http://dbpedia.org/resource/> "
					+"PREFIX dbpedia2: <http://dbpedia.org/property/> "
					+"PREFIX dbpedia: <http://dbpedia.org/> "
					+"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
				+ "	SELECT ?x WHERE { "
					//+"?x foaf:name ?t. "
					//+"FILTER ( regex (str(?t), \"International Maize and Wheat Improvement Center\", \"i\") ) "
					+"?x <http://dbpedia.org/property/name> ?t. "
					+"FILTER ( regex (str(?t), \"CIMMYT\", \"i\") ) "
				+"} "
						+"LIMIT 100";
		
		Query query = QueryFactory.create(sparqlQuery); //s2 = the query above
		//QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
		QueryExecution qExe = QueryExecutionFactory.sparqlService(
					"http://dbpedia.org/sparql", query 
				);
		
		
		ResultSet results = qExe.execSelect();
		while(results.hasNext())
	    {
	        QuerySolution sol = results.nextSolution();
	        RDFNode geouri = sol.get("x"); 

	        return geouri.toString();
	        //System.out.println("geouri:"+geouri);
	    }
		
		return "";
	}
}






