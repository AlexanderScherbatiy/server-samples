package appearhello;

import jakarta.ejb.Remote;

@Remote
public interface Hello {

    public String hello();
}
