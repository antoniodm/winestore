package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        AuthUtil.Owner owner = AuthUtil.resolveOwner(request, response);
        boolean logged = owner.logged;
        Long   userId = owner.userId;
        String token  = owner.guestToken;

        StringBuilder error_message = new StringBuilder();
        HttpSession session = request.getSession(true);
        UserBean user = (UserBean) session.getAttribute("authUser");;

        int user_money = 0;

        if (logged) {
            user_money = user.getMoney();
            System.out.println("Logged");
        } else {
            System.out.println("Not logged");
        }

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
                    if (!logged) {
                        error_message.append("Devi essere loggato");
                        request.setAttribute("cart_message", error_message.toString());
                        request.getRequestDispatcher("/WEB-INF/fragments/cart.jsp").forward(request, response);
                        return;
                    }
                    CartBean before = dao.loadOpenCart(userId, token);

                    boolean ok = dao.checkout(userId, user_money, token); // dentro fa COMMIT prima di tornare
                    response.setHeader("X-Checkout-Done", "1");

                    if (ok) {
                            UserDao userDao = new UserDao();
                            if (userDao.updateCredit(userId, (user_money - before.getTotalCents()))) {
                                user.setMoney(user_money - before.getTotalCents());
                            }
                            error_message.append("Ordine completato");


                        LocalDateTime now = LocalDateTime.now();
                        String when = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                        java.util.Map<String,Object> last = new java.util.HashMap<>();
                        last.put("name", user.getName());
                        last.put("date", when);
                        var ctx = request.getServletContext();
                        synchronized (ctx) {
                            ctx.setAttribute("lastPurchase", last);
                        }

                    } else {
                            response.setStatus(409);
                            error_message.append("Disponibilità insufficiente.");
                            error_message.append(System.lineSeparator());
                    }
                    break;
                default:
                    // no-op
            }

            // ricarica dal DB e aggiorna cache UI di sessione
            CartBean updated = dao.loadOpenCart(userId, token);
            request.getSession(true).setAttribute("cart", updated);

            List<CartBean> closed_carts = null;
            if (logged) {
                closed_carts = dao.loadCloseCart(user.getId());
            }
            session.setAttribute("closed_carts", closed_carts);
            request.setAttribute("cart_message", error_message.toString());

            request.getRequestDispatcher("/WEB-INF/fragments/cart.jsp").forward(request, response);

        } catch (SQLIntegrityConstraintViolationException e) {
            // p.es. conflitti di vincoli/duplicati
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
            request.setAttribute("cart_message", "Operazione non valida");
            request.getRequestDispatcher("/WEB-INF/fragments/cart.jsp").forward(request, response);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            request.setAttribute("cart_message", "Errore interno carrello");
            request.getRequestDispatcher("/WEB-INF/fragments/cart.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        AuthUtil.Owner owner = AuthUtil.resolveOwner(request, response);
        boolean logged = owner.logged;
        Long   userId = owner.userId;
        String token  = owner.guestToken;

        try {
            HttpSession session = request.getSession(true);
            CartBean cart = (CartBean) session.getAttribute("cart");
            List<CartBean> closed_carts = (List<CartBean>) session.getAttribute("closed_carts");

            CartDao cartdao = new CartDao();

            if (cart == null) {
                cart = cartdao.loadOpenCart(userId, token);
                session.setAttribute("cart", cart); // cache UI
            }

            if (logged && (closed_carts == null)) {
                closed_carts = cartdao.loadCloseCart(userId);
            }

            String closed_cart = request.getHeader("cart");
            session.setAttribute("closed_carts", closed_carts); // cache UI

            if ("close".equals(closed_cart)) {
                if (closed_carts == null || closed_carts.isEmpty()) {
                    request.setAttribute("cart_message", "Non hai vecchi carrelli");
                }
                request.getRequestDispatcher("/WEB-INF/fragments/closed_carts.jsp").forward(request, response);
            } else {
                System.out.println("5");
                request.getRequestDispatcher("/WEB-INF/fragments/cart.jsp").forward(request, response);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            // p.es. conflitti di vincoli/duplicati
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
            request.setAttribute("cart_message", "Operazione non valida: " + e.getMessage());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            request.setAttribute("cart_message", "Errore interno carrello: " + e.getMessage());
        }
    }
}
