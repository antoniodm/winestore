package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        AuthUtil.Owner owner = AuthUtil.resolveOwner(request, response);
        boolean logged = owner.logged;
        Long   userId = owner.userId;
        String token  = owner.guestToken;

        HttpSession session = request.getSession(true);

        UserBean user = (UserBean) session.getAttribute("authUser");;
        int user_money = 0;
        if (logged) {
            user_money = user.getMoney();
        }

        System.out.println("Cart doPost called");
        System.out.println("logged = " + logged);
        System.out.println("token = " + token);
        System.out.println("userId = " + userId);
        System.out.println("user_money = " + user_money);

        String action = request.getParameter("action");   // add | remove | clear | reset | checkout
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

                    CartBean before = dao.loadOpenCart(userId, token);

                    boolean ok = dao.checkout(userId, user_money, token); // dentro fa COMMIT prima di tornare
                    response.setHeader("X-Checkout-Done", "1");

                    CartBean after = dao.loadOpenCart(userId, token);
                    request.getSession(true).setAttribute("cart", after);

                    try (PrintWriter out = response.getWriter()) {
                        if (ok) {
                            System.out.println("Money: " + user_money + " Price: " + before.getTotalCents());
                            UserDao userDao = new UserDao();
                            if (userDao.updateCredit(userId, (user_money - before.getTotalCents()))) {
                                user.setMoney(user_money - before.getTotalCents());
                            }

                            out.print("<div class='cart-success'>Ordine completato.</div><div class='cart-empty'>Carrello vuoto</div>");
                        } else {
                            response.setStatus(409);
                            out.print("<div class='cart-error'>Disponibilità insufficiente.</div>");
                            out.print((after == null || after.getProducts().isEmpty()) ? "<div class='cart-empty'>Carrello vuoto</div>" : after.printCart());
                        }
                    }
                    return;
                default:
                    // no-op
            }

            // ricarica dal DB e aggiorna cache UI di sessione
            CartBean updated = dao.loadOpenCart(userId, token);
            request.getSession(true).setAttribute("cart", updated);

            try (PrintWriter out = response.getWriter()) {
                out.print((updated == null || updated.getProducts().isEmpty())
                        ? "<div class='cart-empty'>Carrello vuoto</div>"
                        : updated.printCart());
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // p.es. conflitti di vincoli/duplicati
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
            try (PrintWriter out = response.getWriter()) {
                out.print("<div class='cart-error'>Operazione non valida: "
                        + e.getMessage() + "</div>");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            try (PrintWriter out = response.getWriter()) {
                out.print("<div class='cart-error'>Errore interno carrello.</div>");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        AuthUtil.Owner owner = AuthUtil.resolveOwner(request, response);
        boolean logged = owner.logged;
        Long   userId = owner.userId;
        String token  = owner.guestToken;

        System.out.println("Cart doPost called");
        System.out.println("logged = " + logged);
        System.out.println("token = " + token);
        System.out.println("userId = " + userId);

        HttpSession session = request.getSession(true);
        CartBean cart = (CartBean) session.getAttribute("cart");

        if (cart == null) {
            try {
                cart = new CartDao().loadOpenCart(userId, token);
                session.setAttribute("cart", cart); // cache UI
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.print((cart == null || cart.getProducts().isEmpty())
                    ? "<div class='cart-empty'>Carrello vuoto</div>"
                    : cart.printCart());
        }
    }
}
