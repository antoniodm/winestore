package controller;

import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProductBean;

import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.io.IOException;

@WebServlet(name = "AddProductServlet", urlPatterns = {"/product/add"})
@MultipartConfig(
        fileSizeThreshold = 1_000_000,    // 1MB buffer
        maxFileSize = 5_000_000L,         // 5MB
        maxRequestSize = 6_000_000L
)

public class AddProductServlet extends HttpServlet {
    private Path imagesDir(HttpServletRequest req) {
        // <app>/WEB-INF/images
        return Paths.get(getServletContext().getRealPath("/WEB-INF/images"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String origin = request.getParameter("origin");
        String manufacturer = request.getParameter("manufacturer");
        int price = Integer.parseInt(request.getParameter("price_cents"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        // upload opzionale
        Part imagePart = request.getPart("image");
        String storedFileName = null;

        if (imagePart != null && imagePart.getSize() > 0) {
            String mime = imagePart.getContentType();
            if (mime == null || !(mime.startsWith("image/"))) {
                response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Formato immagine non valido");
                return;
            }

            String original = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
            String base = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String ext = base.contains(".") ? base.substring(base.lastIndexOf('.')) : "";
            storedFileName = System.currentTimeMillis() + "_" + Integer.toHexString(base.hashCode()) + ext;

            Path dir = imagesDir(request);
            Files.createDirectories(dir);
            try (InputStream in = imagePart.getInputStream()) {
                Files.copy(in, dir.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        ProductBean p = new ProductBean();
        p.setName(name);
        p.setDescription(description);
        p.setOrigin(origin);
        p.setManufacturer(manufacturer);
        p.setPrice(price);
        p.setStock(stock);
        p.setImagePath(storedFileName); // solo nome file o null

        System.out.println("NEW PROD name " + name);

        ProductDao productDao = new ProductDao();
        if (productDao.insert(p)) {
            System.out.println("PRODUCT ADDED");
        }

        response.getWriter().println("Nuovo Prodotto Aggiunto");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDao productDao = new ProductDao();
        request.setAttribute("removedProducts", productDao.doRetrieveAllRemoved());
        request.getRequestDispatcher("/editproducts.jsp").forward(request, response);
    }
}
