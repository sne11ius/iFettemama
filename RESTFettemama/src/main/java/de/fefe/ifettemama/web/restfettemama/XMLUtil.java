package de.fefe.ifettemama.web.restfettemama;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 *
 * @author sne11ius
 */
public class XMLUtil {
    
    public static Element createLinkElement(String elementName, String url) {
        Element linkElement = new Element(elementName);
        
        linkElement.setAttribute(new Attribute("type", "simple", getXLinkNamespace()));
        linkElement.setAttribute(new Attribute("href", url, getXLinkNamespace()));
        
        return linkElement;
    }
    
    public static Namespace getXLinkNamespace() {
        return Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
    }
    
}
