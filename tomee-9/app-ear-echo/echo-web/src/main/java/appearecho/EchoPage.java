package appearecho;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/echo")
public class EchoPage {

    static final String VERSION = "VER 1.0";

    @GET
    public String echo() {
        return String.format("Echo! Version: %s.", VERSION);
    }

    @GET
    @Path("/{name}")
    public String echo(@PathParam("name") String name) {
        return String.format("Echo: %s! Version: %s%n", name, VERSION);
    }
}
