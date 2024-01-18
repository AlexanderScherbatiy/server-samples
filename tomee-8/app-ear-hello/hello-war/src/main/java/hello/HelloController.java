package hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("rest")
@Produces({APPLICATION_JSON})
public class HelloController {

    @GET
    @Path("hello")
    public String hello() {

        Map<String, String> map = new HashMap<>();
        map.put("greeting", "Hello, World!");

        return toJson(map);
    }

    private static String toJson(Map<?, ?> map) {
        return map
                .entrySet()
                .stream()
                .map(e -> String.format("\"%s\": \"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }
}
