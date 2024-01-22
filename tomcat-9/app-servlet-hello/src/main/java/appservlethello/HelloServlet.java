package appservlethello;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;

@WebServlet("/helloservlet")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        ServletOutputStream out = resp.getOutputStream();
        out.println("Hello, World!");
        out.println();
        out.println("Date: " + new Date());
    }
}
