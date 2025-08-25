package controller;

import dao.ProductDao;
import model.ProductBean;
import model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "ShopServlet", urlPatterns = {"/shop"})
public class ShopServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDao service = new ProductDao();
        List<ProductBean> products = service.doRetrieveAll();

        // Chi è l'utente? (per bottoni admin)
        UserBean u = (UserBean) request.getSession().getAttribute("authUser");
        boolean isAdmin = (u != null && "admin".equals(u.getUsername()));

        String ctx = request.getContextPath();
        StringBuilder sb = new StringBuilder();

        if (products != null && !products.isEmpty()) {
            for (ProductBean p : products) {
                if (p.is_removed()) continue; // non mostrare prodotti rimossi

                sb.append("<li>")
                        .append("<div class=\"product_div\">")
                        .append("Name: ").append(escape(p.getName())).append("<br>")
                        .append("Description: ").append(escape(p.getDescription())).append("<br>")
                        .append("Manufacturer: ").append(escape(p.getManufacturer())).append("<br>")
                        .append("Price: ").append(p.getPrice()).append("<br>")
                        .append("Stock: ").append(p.getStock()).append("<br>");

                String img = p.getImagePath();
                if (img != null && !img.isEmpty()) {
                    sb.append("<img src=\"").append(ctx).append("/image/")
                            .append(urlEncode(img))
                            .append("\" alt=\"").append(htmlAttr(altText(p.getName())))
                            .append("\" width=\"160\">");
                }

                sb.append("<div class=\"cart_btns\">");
                if (p.getStock() > 0) {
                    sb.append("<button type=\"button\" class=\"add_to_cart\" data-id=\"")
                            .append(p.getId()).append("\">Add to cart</button>");
                } else {
                    sb.append("<button type=\"button\" class=\"empty\" disabled>Out-of-stock</button>");
                }

                if (isAdmin) {
                    sb.append("<form action=\"").append(ctx).append("/product/delete\" method=\"post\">")
                            .append("<input type=\"hidden\" name=\"prod_id\" value=\"").append(p.getId()).append("\">")
                            .append("<button type=\"submit\" id=\"del_prod_btn\">Elimina</button>")
                            .append("</form>");

                    sb.append("<form action=\"").append(ctx).append("/product/edit\" method=\"post\">")
                            .append("<input type=\"hidden\" name=\"prod_id\" value=\"").append(p.getId()).append("\">")
                            .append("<button type=\"submit\" id=\"edit_prod_btn\">Modifica</button>")
                            .append("</form>");
                }

                sb.append("</div>") // .cart_btns
                        .append("</div>") // .product_div
                        .append("</li>");
            }

            request.setAttribute("renderedProducts", sb.toString());
            request.setAttribute("emptyMessage", "");
        } else {
            request.setAttribute("renderedProducts", "");
            request.setAttribute("emptyMessage", "Nessun prodotto disponibile al momento.");
        }

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        if (isAjax) {
            // 🔹 Solo il frammento per aggiornare #dynamic_content via JS
            request.getRequestDispatcher("/WEB-INF/fragments/product_list.jsp").forward(request, response);
        } else {
            // 🔹 Pagina completa con header/navbar/sidebar/footer
            request.getRequestDispatcher("/shop.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // --- helper semplici per sicurezza output ---

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"","&quot;")
                .replace("'", "&#39;");
    }

    private static String htmlAttr(String s) { return escape(s); }

    private static String altText(String s) {
        return (s == null || s.isBlank()) ? "product image" : s;
    }

    private static String urlEncode(String s) {
        return s == null ? "" : URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
