package hello;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PersonTwoEjbImpl implements PersonEjb {

    @Override
    public String getName() {
        return "Two";
    }

    @Override
    public String hello() {
        return "Hello from Person Two!";
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
