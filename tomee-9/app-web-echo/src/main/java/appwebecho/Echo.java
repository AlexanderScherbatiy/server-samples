package appwebecho;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/echo")
public class Echo {

    @GET
    public String echo() {
        return "Echo!";
    }

    @GET
    @Path("/{name}")
    public String echo(@PathParam("name") String name) {
        return String.format("Echo: %s!%n", name);
    }
}
