package de.fefe.ifettemama.web.restfettemama;

import de.fefe.ifettemama.web.util.Util;
import de.fefe.runderpapa.shared.BlogPost;
import de.fefe.runderpapa.shared.BlogPostComment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.TreeSet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author sne11ius
 */
@Path("/user")
public class UserResource {
    
    private static final Log LOG = LogFactory.getLog(UserResource.class);
        
    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document getUsers() throws MalformedURLException, IOException {
        FetteMamaScraper fetteMamaScraper = new FetteMamaScraper();
        List<BlogPost> blogPosts = fetteMamaScraper.getBlogPosts();
        TreeSet<String> usernames = new TreeSet<String>() {};
        for (BlogPost blogPost : blogPosts) {
            for (BlogPostComment blogPostComment : blogPost.getComments()) {
                LOG.info("adding user `" + blogPostComment.getUsername() + "'");
                usernames.add(blogPostComment.getUsername());
            }
        }
        LOG.info("building doc...");
        Element rootElement = new Element("users");
        for (String username : usernames) {
            Element userElement = new Element("user");
            userElement.setAttribute("username", Util.sanitize(username));
            rootElement.addContent(userElement);
        }
        LOG.info("sending response...");
        return new Document(rootElement);
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_XML)
    public Document getCommentsForUser(@PathParam("username") String username) throws MalformedURLException, IOException {
        Element rootElement = new Element("comments");
        
        FetteMamaScraper fetteMamaScraper = new FetteMamaScraper();
        List<BlogPost> blogPosts = fetteMamaScraper.getBlogPosts();
        for (BlogPost blogPost : blogPosts) {
            int commentId = 1;
            for (BlogPostComment blogPostComment : blogPost.getComments()) {
                if (blogPostComment.getUsername().equals(username)) {
                    Element blogPostCommentElement = new Element("comment");
                    blogPostCommentElement.setAttribute("postId", "" + blogPost.getPostId());
                    blogPostCommentElement.setAttribute("commentId", "" + commentId);
                    blogPostCommentElement.setText(Util.sanitize(blogPostComment.getComment()));
                    rootElement.addContent(blogPostCommentElement);
                }
                ++commentId;
            }
        }
        
        return new Document(rootElement);
    }
}
