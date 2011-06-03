/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fefe.ifettemama.web.restfettemama;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author sne11ius
 */
@Provider
@Produces(MediaType.APPLICATION_XML)
public class DocumentMessageBodyWriter implements MessageBodyWriter<Document> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(Document.class);
    }

    @Override
    public long getSize(Document t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return new XMLOutputter().outputString(t).length();
    }

    @Override
    public void writeTo(Document t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        new XMLOutputter().output(t, entityStream);
    }
    
}
