package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CartBean;
import model.ProductBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("doPost called");

        // Carica il carrello dalla sessione
        HttpSession session = request.getSession(true);
        CartBean cart = (CartBean) session.getAttribute("cart");
        // Se il carrello non esiste, crealo e mettilo in sessione
        if (cart == null) {
            cart = new CartBean();
            session.setAttribute("cart", cart);
        }

        System.out.println("getParameter");

        String token_id = request.getParameter("id");
        int wine_id = Integer.parseInt(token_id);
        System.out.println("addProduct");
        cart.addProduct(wine_id);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        ArrayList<ProductBean> products = cart.getProducts();
        if (cart.getProducts().isEmpty()) {
            System.out.println("cart is empty");
        } else {
            for (ProductBean product : products) {
                System.out.println(product.getId());
                out.println("<h4>" + product.getId() + "</h4><br>");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
