package controller;

import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.ProductBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@WebServlet(name = "EditProductServlet", urlPatterns = {"/product/update", "/product/edit", "/product/delete" , "/product/resurrect"})
@MultipartConfig(
        fileSizeThreshold = 1_000_000,    // 1MB buffer
        maxFileSize = 5_000_000L,         // 5MB
        maxRequestSize = 6_000_000L
)

public class EditProductServlet extends HttpServlet {
    private Path imagesDir(HttpServletRequest req) {
        // <app>/WEB-INF/images
        return Paths.get(getServletContext().getRealPath("/WEB-INF/images"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean is_edit =   "/product/edit".equals(request.getServletPath());
        boolean is_delete = "/product/delete".equals(request.getServletPath());
        boolean is_update = "/product/update".equals(request.getServletPath());
        boolean is_resurrect = "/product/resurrect".equals(request.getServletPath());

        if (!is_edit && !is_delete && !is_update && !is_resurrect) { return; }



        System.out.println("Called product edit servlet " + " edit: " + is_edit + " delete: " + is_delete + " update: " + is_update + " resurrect: " + is_resurrect);

        if (is_resurrect) {
            String[] product_ids = request.getParameterValues("resurrect_id");
            if (product_ids != null && product_ids.length > 0) {
                ProductDao prod = new ProductDao();
                if (prod.resurrect(product_ids)) {
                    for (String product_id : product_ids) {
                        response.getWriter().println("Prodotto Resuscitato: " + product_id);
                    }
                }
            }
        }

        if (is_edit) {
            String product_id = request.getParameter("prod_id");
            ProductBean prod = new ProductDao().doRetrieveById(Integer.parseInt(product_id));

            System.out.println("Called product edit servlet isEdit");
            if (prod.getId() > 0) {
                System.out.println("Called product edit servlet prod: " + prod.getName());
                request.setAttribute("prod", prod);
                request.getRequestDispatcher("/editproducts.jsp").forward(request, response);
            } else {
                System.out.println("Called product edit servlet prod is null");
            }
        }



        if (is_delete) {
            String product_id = request.getParameter("prod_id");
            ProductBean prod = new ProductDao().doRetrieveById(Integer.parseInt(product_id));
            ProductDao productDao = new ProductDao();
            System.out.println("Called product delete servlet isDelete: " +  prod.getName() + " | " + prod.getId());
            if (productDao.remove(prod) ) {
                response.getWriter().println("Prodotto Rimosso: " + prod.getName());
            } else {
                response.getWriter().println("Prodotto NON Rimosso: " + prod.getName());
            }
        }

        if (is_update) {
            HttpSession session = request.getSession(true);
            Integer product_id = (Integer) session.getAttribute("prod_id");
            System.out.println("Called product edit servlet prod: " + product_id);
            ProductDao productDao = new ProductDao();
            ProductBean prod = productDao.doRetrieveById(product_id);

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

            prod.setName(name);
            prod.setDescription(description);
            prod.setOrigin(origin);
            prod.setManufacturer(manufacturer);
            prod.setPrice(price);
            prod.setStock(stock);
            prod.setImagePath(storedFileName); // solo nome f

            System.out.println("Called product update servlet isUpdate");
            if (productDao.update(prod)) {
                response.getWriter().println("Prodotto Modificato: " + prod.getName());
            } else {
                response.getWriter().println("Prodotto NON Modificato: " + prod.getName());
            }
        }
    }
}
