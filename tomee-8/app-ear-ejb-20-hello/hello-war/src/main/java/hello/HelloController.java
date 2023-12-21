package hello;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("rest")
@Produces({APPLICATION_JSON})
public class HelloController {

    @GET
    @Path("hello")
    public String hello() {

        try {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.OpenEJBInitialContextFactory");
            Context context = new InitialContext(properties);
            PersonEjb personOne = (PersonEjb) context.lookup("global/hello-ear-1.0/jndihello/PersonOneEjbImpl");
            PersonEjb personTwo = (PersonEjb) context.lookup("global/hello-ear-1.0/jndihello/PersonTwoEjbImpl");

            Map<String, String> map = new HashMap<>();
            map.put(
                    "Person " + personOne.getName(),
                    personOne.hello());
            map.put(
                    "Person " + personTwo.getName(),
                    personTwo.hello());
            map.put(
                    "Hello from person " + personOne.getName() + "'s friend",
                    personOne.getFriend().hello());
            map.put(
                    "Hello from person " + personTwo.getName() + "'s friend",
                    personTwo.getFriend().hello());

            return map
                    .entrySet()
                    .stream()
                    .map(e -> String.format("\"%s\": \"%s\"", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));

        } catch (NamingException e) {
            e.printStackTrace();
            return String.format("\"exception\": \"%s\"", e.getMessage());
        }
    }
}
