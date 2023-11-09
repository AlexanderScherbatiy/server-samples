package appservlethello;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.http.HttpServlet;

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
