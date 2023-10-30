package appearhello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class HelloPage {

    static final String VERSION = "VER 1.0";

    @GET
    public String hello() {
        return String.format("EJB sample version: %s", VERSION);
    }
}
