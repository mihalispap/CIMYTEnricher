//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.22 at 03:07:06 PM EET 
//


package com.agroknow.cimmyt.parser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.agroknow.cimmyt.parser package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Resource_QNAME = new QName("", "resource");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.agroknow.cimmyt.parser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CimmytRecord }
     * 
     */
    public CimmytRecord createCimmytRecord() {
        return new CimmytRecord();
    }

    /**
     * Create an instance of {@link CimmytRecord.Title }
     * 
     */
    public CimmytRecord.Title createCimmytRecordTitle() {
        return new CimmytRecord.Title();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CimmytRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "resource")
    public JAXBElement<CimmytRecord> createResource(CimmytRecord value) {
        return new JAXBElement<CimmytRecord>(_Resource_QNAME, CimmytRecord.class, null, value);
    }

}
