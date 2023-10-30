package appwebhello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/hello")
public class Hello {

    @GET
    public String hello() {
        return "Hello, World!";
    }

    @GET
    @Path("/{name}")
    public String hello(@PathParam("name") String name) {
        return String.format("Hello, %s!%n", name);
    }
}
