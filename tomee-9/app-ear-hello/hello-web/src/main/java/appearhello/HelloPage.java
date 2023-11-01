package appearhello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/hello")
public class HelloPage {

    static final String VERSION = "VER 1.0";

    @GET
    public String hello() {
        return String.format("Hello, World! Version: %s.", VERSION);
    }

    @GET
    @Path("/{name}")
    public String hello(@PathParam("name") String name) {
        return String.format("Hello, %s! Version: %s%n", name, VERSION);
    }
}
