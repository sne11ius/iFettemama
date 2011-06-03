package de.fefe.ifettemama.web.restfettemama;

import de.fefe.ifettemama.web.util.Util;
import de.fefe.runderpapa.shared.BlogPost;
import de.fefe.runderpapa.shared.BlogPostComment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author sne11ius
 */
@Path("/posts")
public class PostResource {
    
    private static final Log LOG = LogFactory.getLog(PostResource.class);

    @Context
    UriInfo uriInfo;
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document listPosts() throws MalformedURLException, IOException {
        int maxPostId = new FetteMamaScraper().getMaxPostId();
        Element rootElement = new Element("posts");
        for (int i = 1; i <= maxPostId; ++i) {
            String path = uriInfo.getAbsolutePathBuilder().path("" + i).build().toString();
            Element linkElement = XMLUtil.createLinkElement("post", path);
            linkElement.setAttribute("id", "" + i);
            rootElement.addContent(linkElement);
        }
        return new Document(rootElement);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{postId}")
    public Document getPost(@PathParam("postId") int postId) {
        Element rootElement = new Element("post");
        rootElement.setAttribute("id", "" + postId);
        String contentPath = uriInfo.getAbsolutePathBuilder().path("content").build().toString();
        String commentsPath = uriInfo.getAbsolutePathBuilder().path("comments").build().toString();
        rootElement.addContent(XMLUtil.createLinkElement("content", contentPath));
        rootElement.addContent(XMLUtil.createLinkElement("comments", commentsPath));
        return new Document(rootElement);
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{postId}/content")
    public String getPostContent(@PathParam("postId") int postId) throws MalformedURLException, IOException {
        return new FetteMamaScraper().getBlogPost(postId).getText();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{postId}/comments")
    public Document getPostComments(@PathParam("postId") int postId) throws MalformedURLException, IOException {
        List<BlogPostComment> blogPostComments = new FetteMamaScraper().getBlogPost(postId).getComments();
        Element rootElement = new Element("comments");
        int commentId = 1;
        for (BlogPostComment comment : blogPostComments) {
            Element commentElement = new Element("comment");
            commentElement.setAttribute("user", Util.sanitize(comment.getUsername()));
            commentElement.setAttribute("id", "" + commentId++);
            commentElement.setText(Util.sanitize(comment.getComment()));
            rootElement.addContent(commentElement);
        }
        
        return new Document(rootElement);
    }
    
    @POST
    @Path("{postId}/comments")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postComment(@PathParam("postId") int postId, @HeaderParam("X-Username") String username, String comment) throws MalformedURLException, IOException {
        FetteMamaScraper fetteMamaScraper = new FetteMamaScraper();
        if (postId < 1 || postId > fetteMamaScraper.getMaxPostId()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        LOG.info("Posting comment.\n\tpostId: `" + postId + "'\n\tUsername: `" + username + "'\n\tcomment: `" + comment + "'");
        
        URI commentPath = uriInfo.getAbsolutePathBuilder().path("" + (fetteMamaScraper.getBlogPost(postId).getComments().size() + 1)).build();

        new FetteMamaTelnetClient().addComment(postId, new BlogPostComment(username, comment));        
        return Response.created(commentPath).build();
    }

    @GET
    @Path("{postId}/comments/{commentId}")
    public Response getPostComment(@PathParam("postId") int postId, @PathParam("commentId") int commentId) throws MalformedURLException, IOException {
        FetteMamaScraper fetteMamaScraper = new FetteMamaScraper();
        if (postId < 1 || postId > fetteMamaScraper.getMaxPostId()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        BlogPost blogPost = fetteMamaScraper.getBlogPost(postId);
        if ((commentId < 1) || commentId > blogPost.getComments().size()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        Element responseElement = new Element("comment");
        responseElement.setAttribute("user", blogPost.getComments().get(commentId - 1).getUsername());
        responseElement.setText(blogPost.getComments().get(commentId - 1).getComment());
        
        return Response.ok(new Document(responseElement), MediaType.APPLICATION_XML).build();
    }

}
