package appwebecho;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/appwebecho")
public class ApplicationConfig extends Application {

    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(Echo.class));
    }
}
