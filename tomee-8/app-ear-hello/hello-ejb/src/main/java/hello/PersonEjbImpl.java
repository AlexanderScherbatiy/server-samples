package hello;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateless
public class PersonEjbImpl implements PersonEjb {

    @Override
    public String getName() {
        return "Person";
    }

    @Override
    public String hello() {
        return "Hello from Person!";
    }
}
