package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CartBean;
import model.CartItem;
import model.ProductBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Carica il carrello dalla sessione
        HttpSession session = request.getSession(true);
        CartBean cart = (CartBean) session.getAttribute("cart");
        // Se il carrello non esiste, crealo e mettilo in sessione
        if (cart == null) {
            cart = new CartBean();
            session.setAttribute("cart", cart);
        }

        String token_id = request.getParameter("id");
        String action = request.getParameter("action");

        if ("reset".equals(action)) {
            cart.reset();
            return;
        }
        int wine_id = Integer.parseInt(token_id);

        ProductDao service = new ProductDao();
        ProductBean product = service.doRetrieveById(wine_id);

        // Aggiungo il prodotto nel carrello
        if ("add".equals(action)) {
            cart.addProduct(product);
        } else if ("remove".equals(action)) {
            cart.removeProduct(product);
        }

        ArrayList<CartItem> cart_items = cart.getProducts();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (cart_items == null || cart_items.isEmpty()) {
            System.out.println("cart is empty");
        } else {
            out.print(cart.printCart());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Carica il carrello dalla sessione
        HttpSession session = request.getSession(true);
        CartBean cart = (CartBean) session.getAttribute("cart");

        if (cart == null) {
            return;
        }

        ArrayList<CartItem> cart_items = cart.getProducts();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (cart_items == null || cart_items.isEmpty()) {
            System.out.println("cart is empty");
        } else {
            out.print(cart.printCart());
        }
    }

}
