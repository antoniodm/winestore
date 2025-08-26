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

        request.setAttribute("products", products);
        request.setAttribute("isAdmin", isAdmin);

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
}
