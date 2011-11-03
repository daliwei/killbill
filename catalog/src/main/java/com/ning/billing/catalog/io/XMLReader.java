/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.catalog.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ning.billing.catalog.Catalog;
import com.ning.billing.catalog.ValidatingConfig.ValidationErrors;
import com.ning.billing.catalog.api.InvalidConfigException;

public class XMLReader {
	public static Logger log = LoggerFactory.getLogger(XMLReader.class);

    public static Catalog getCatalogFromName(URL url) throws SAXException, InvalidConfigException, JAXBException, IOException, TransformerException {
        Object o = unmarshaller().unmarshal(url);
        if(o instanceof Catalog) {
            Catalog c = (Catalog)o;
            c.setCatalogURL(url.toString());
            validate(c);
            return (Catalog) o;
        } else {
            return null;
        }
    }
    
    public static Catalog getCatalogFromName(InputStream stream) throws SAXException, InvalidConfigException, JAXBException, IOException, TransformerException {
        Object o = unmarshaller().unmarshal(stream);
        if(o instanceof Catalog) {
            Catalog c = (Catalog)o;
            c.setCatalogURL("embedded catalog");
            validate(c);
            return (Catalog) o;
        } else {
            return null;
        }
    }

    
    public static void validate(Catalog c) {
            c.initialize(c);
            ValidationErrors errs = c.validate();
            System.out.println("Errors: " + errs.size() + " for " + c.getCatalogURL());       
    }
    
    public static Unmarshaller unmarshaller() throws JAXBException, SAXException, IOException, TransformerException {
    	 JAXBContext context =JAXBContext.newInstance(Catalog.class);

         SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI );
         Unmarshaller um = context.createUnmarshaller();

         Schema schema = factory.newSchema(XMLSchemaGenerator.xmlSchema());
         um.setSchema(schema);
         
         return um;
    }
	
}
