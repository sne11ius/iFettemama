package de.fefe.ifettemama.web.restfettemama;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author sne11ius
 */
@Path("/")
public class IndexResource {
    
    @Context
    UriInfo uriInfo;
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document listShit() {
        Element rootElement = new Element("index");
        String postsPath = uriInfo.getAbsolutePathBuilder().path("posts").build().toString();
        rootElement.addContent(XMLUtil.createLinkElement("posts", postsPath));
        String userPath = uriInfo.getAbsolutePathBuilder().path("user").build().toString();
        rootElement.addContent(XMLUtil.createLinkElement("user", userPath));
        return new Document(rootElement);
    }
}
