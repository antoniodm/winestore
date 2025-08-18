package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.nio.file.*;

@WebServlet("/image/*")
public class ImageServlet extends HttpServlet {

    private Path imagesDir() {
        return Paths.get(getServletContext().getRealPath("/WEB-INF/images"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // es: "/1723990000_abc.png"
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // normalizza ed evita path traversal
        String fileName = Paths.get(pathInfo).getFileName().toString();

        Path file = imagesDir().resolve(fileName);
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(fileName);
        if (mime == null) mime = "application/octet-stream";
        resp.setContentType(mime);
        resp.setHeader("Cache-Control", "public, max-age=86400");

        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
