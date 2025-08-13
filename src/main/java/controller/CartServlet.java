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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    // Chiamata per aggiungere/rimuovere un prodotto al carrello o resettarlo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        boolean logged = Boolean.TRUE.equals(request.getAttribute("logged"));
        Long userId = (Long) request.getAttribute("ownerUserId");
        String token = (String) request.getAttribute("ownerToken");

        String action = request.getParameter("action");
        int productId = -1;
        try { productId = Integer.parseInt(request.getParameter("id")); } catch (Exception ignored) {}

        CartDao dao = new CartDao();
        try {
            switch (action == null ? "" : action.toLowerCase()) {
                case "add":
                    if (productId <= 0) { response.sendError(400, "id prodotto mancante"); return; }
                    dao.addOneToOpenCart(userId, token, productId);
                    break;
                case "remove":
                    if (productId <= 0) { response.sendError(400, "id prodotto mancante"); return; }
                    dao.decrementOrRemoveFromOpenCart(userId, token, productId);
                    break;
                case "clear":
                case "reset":
                    dao.clearOpenCart(userId, token);
                    break;
                case "checkout":
                    dao.checkout(userId, token);
                    break;
                default:
                    // no-op
            }

            // ricarica dal DB e aggiorna cache UI
            CartBean updated = dao.loadOpenCart(userId, token);
            request.getSession(true).setAttribute("cart", updated);

            try (PrintWriter out = response.getWriter()) {
                out.print((updated == null || updated.getProducts().isEmpty())
                        ? "<div class='cart-empty'>Carrello vuoto</div>"
                        : updated.printCart());
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Carica il carrello dalla sessione
        HttpSession session = request.getSession(true);
        CartBean cart = (CartBean) session.getAttribute("cart");

        boolean logged = Boolean.TRUE.equals(request.getAttribute("logged"));
        Long userId = (Long) request.getAttribute("ownerUserId"); // se loggato
        String  token  = (String)  request.getAttribute("ownerToken");  // se anonimo

        // se il carrello non è stato ancora caricato in sessione
        if (cart == null) {
            CartDao dao = new CartDao();
            try {
                cart = dao.loadOpenCart(userId, token);
                if (cart != null) {
                    session.setAttribute("cart", cart);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            out.print("<div class='cart-empty'>Carrello vuoto</div>");
        } else {
            out.print(cart.printCart());
        }
    }

}
