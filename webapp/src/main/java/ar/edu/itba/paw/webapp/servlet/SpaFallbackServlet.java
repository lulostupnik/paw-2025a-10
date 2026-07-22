package ar.edu.itba.paw.webapp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SpaFallbackServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        try (InputStream index = getServletContext().getResourceAsStream("/index.html")) {
            if (index == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("text/html;charset=UTF-8");
            resp.setHeader("Cache-Control", "no-cache");
            try (OutputStream out = resp.getOutputStream()) {
                index.transferTo(out);
            }
        }
    }
}
