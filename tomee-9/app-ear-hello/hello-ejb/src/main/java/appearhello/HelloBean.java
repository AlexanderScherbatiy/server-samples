package appearhello;

import jakarta.ejb.Stateless;

@Stateless
public class HelloBean implements Hello {

    public String hello() {
        return "Hello, Bean!";
    }
}
