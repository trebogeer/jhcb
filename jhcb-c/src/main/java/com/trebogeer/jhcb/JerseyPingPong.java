package com.trebogeer.jhcb;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 1:31 PM
 */
@Path("a/b/c/ping")
@Produces(MediaType.TEXT_PLAIN)
@Singleton
public class JerseyPingPong {

    @GET
    @Path("{id}")
    public String ping(@PathParam("id") String id) {
        return "pong" + id;
    }

}
