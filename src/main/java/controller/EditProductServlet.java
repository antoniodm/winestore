package controller;

import dao.CartDao;
import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.AuthUtil;
import model.CartBean;
import model.ProductBean;
import model.UserBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet(
        name = "EditProductServlet",
        urlPatterns = {"/product/update", "/product/edit", "/product/delete", "/product/resurrect"}
)
@MultipartConfig(
        fileSizeThreshold = 1_000_000, // 1MB
        maxFileSize = 5_000_000L,      // 5MB
        maxRequestSize = 6_000_000L
)
public class EditProductServlet extends HttpServlet {

    private Path imagesDir(HttpServletRequest req) {
        return Paths.get(getServletContext().getRealPath("/WEB-INF/images"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        boolean isEdit      = "/product/edit".equals(servletPath);
        boolean isDelete    = "/product/delete".equals(servletPath);
        boolean isUpdate    = "/product/update".equals(servletPath);
        boolean isResurrect = "/product/resurrect".equals(servletPath);

        // --- Controllo admin (come da corso: ruoli diversi) ---
        HttpSession session = request.getSession(true);
        UserBean auth = (UserBean) session.getAttribute("authUser");
        boolean isAdmin = (auth != null && "admin".equals(auth.getUsername()));

        AuthUtil.Owner owner = AuthUtil.resolveOwner(request, response);
        boolean logged = owner.logged;
        Long   userId = owner.userId;
        String token  = owner.guestToken;

        // Se non admin: pagina "non autorizzato"
        if ((isEdit || isUpdate || isDelete || isResurrect) && !isAdmin) {
            request.setAttribute("title", "NON SEI AUTORIZZATO");
            request.setAttribute("message", "Solo l’amministratore può gestire i prodotti.");
            request.getRequestDispatcher("/WEB-INF/results/unauthorized.jsp").forward(request, response);
            return;
        } else {
            System.out.println(" isEdit: " +isEdit + " isDelete: " + isDelete + " isResurrect: " + isResurrect + " isUpdate: " + isUpdate);
        }

        ProductDao productDao = new ProductDao();
        CartDao cartDao = new CartDao();

        if (isResurrect) {
            String[] ids = request.getParameterValues("resurrect_id");
            if (ids != null && ids.length > 0) {
                productDao.resurrect(ids);
            }
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        if (isDelete) {
            String productId = request.getParameter("prod_id");
            if (productId != null) {
                ProductBean prod = productDao.doRetrieveById(Integer.parseInt(productId));
                productDao.remove(prod);

                try {
                    cartDao.decrementOrRemoveFromOpenCart(userId, token, Integer.parseInt(productId));
                    CartBean updated = cartDao.loadOpenCart(userId, token);
                    request.getSession(true).setAttribute("cart", updated);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        if (isEdit) {
            // Carica prodotto e prepara form di modifica
            String productId = request.getParameter("prod_id");
            ProductBean prod = productDao.doRetrieveById(Integer.parseInt(productId));
            if (prod != null && prod.getId() > 0) {
                session.setAttribute("prod_id", prod.getId()); // serve per /product/update
                prepareFormAttributes(request, true, prod);
                // Anche la lista “rimossi” per i checkbox (renderizzata lato server)
                request.setAttribute("removedOptionsHtml", buildRemovedCheckboxes(productDao.doRetrieveAllRemoved()));
                request.getRequestDispatcher("/editproducts.jsp").forward(request, response);
                return;
            }
            // fallback
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        if (isUpdate) {
            // Prende id da sessione (come tuo codice)
            Integer productId = (Integer) session.getAttribute("prod_id");
            if (productId == null) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            ProductBean prod = productDao.doRetrieveById(productId);
            if (prod == null) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }

            String name         = request.getParameter("name");
            String description  = request.getParameter("description");
            String origin       = request.getParameter("origin");
            String manufacturer = request.getParameter("manufacturer");
            int price           = Integer.parseInt(request.getParameter("price_cents"));
            int stock           = Integer.parseInt(request.getParameter("stock"));

            // upload opzionale
            Part imagePart = request.getPart("image");
            String storedFileName = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                String mime = imagePart.getContentType();
                if (mime == null || !mime.startsWith("image/")) {
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
            if (storedFileName != null) {
                prod.setImagePath(storedFileName);
            }

            productDao.update(prod);
            response.sendRedirect(request.getContextPath() + "/shop");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Per comodità, supporta GET su /product/edit
        if ("/product/edit".equals(req.getServletPath())) {
            doPost(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void prepareFormAttributes(HttpServletRequest request, boolean isEdit, ProductBean prod) {
        String ctx = request.getContextPath();
        request.setAttribute("isEdit", isEdit);
        request.setAttribute("prod", prod);
        // Prepara action risolta (così in JSP non componi stringhe)
        String formAction = isEdit ? (ctx + "/product/update") : (ctx + "/product/add");
        request.setAttribute("formAction", formAction);
        // Etichetta per upload
        request.setAttribute("imageLabel", isEdit ? "Sostituisci immagine (opzionale)" : "Immagine (opzionale)");
        // URL immagine corrente (se presente)
        String currentImgUrl = (prod != null && prod.getImagePath() != null && !prod.getImagePath().isEmpty())
                ? (ctx + "/image/" + prod.getImagePath())
                : "";
        request.setAttribute("currentImageUrl", currentImgUrl);
    }

    private String buildRemovedCheckboxes(List<ProductBean> removed) {
        if (removed == null || removed.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (ProductBean p : removed) {
            sb.append("<div class=\"resurrect-row\">")
                    .append("<input type=\"checkbox\" id=\"resurrect_").append(p.getId())
                    .append("\" name=\"resurrect_id\" value=\"").append(p.getId()).append("\">")
                    .append("<label for=\"resurrect_").append(p.getId()).append("\">")
                    .append(escapeHtml(p.getName())).append("</label>")
                    .append("</div>");
        }
        sb.append("<button type=\"submit\" id=\"recreate_prod_btn\">Resuscita selezionati</button>");
        return sb.toString();
    }

    // mini-escape HTML giusto per i nomi
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
