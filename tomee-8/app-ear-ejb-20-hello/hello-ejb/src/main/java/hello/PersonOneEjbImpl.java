package hello;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PersonOneEjbImpl implements PersonEjb {

    @Override
    public String getName() {
        return "One";
    }

    @Override
    public String hello() {
        return "Hello from Person One!";
    }

    @Override
    public PersonEjb getFriend() {
        try {
            return (PersonEjb) new InitialContext().lookup("java:comp/env/friend");
        } catch (NamingException e) {
            throw new EJBException(e);
        }
    }
}
