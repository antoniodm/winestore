package controller;

import dao.CartDao;
import dao.UserDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.CartBean;
import model.UserBean;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("loginError", "Username e password sono obbligatori.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserDao userDao = new UserDao();
        UserBean user = userDao.doRetrieveByUsername(username);


        // Hashing della password
        String password_hash = password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes("UTF-8"));
            password_hash = Base64.getEncoder().encodeToString(hashBytes);
            //System.out.println("Password hash: " + password_hash);
        } catch (Exception e) {
            System.out.println("Errore nella SHA-256");
        }

        if (user == null || !password_hash.equals(user.getPasswordHash())) {
            request.setAttribute("loginError", "Credenziali non valide.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // autenticazione OK
        request.changeSessionId();
        HttpSession session = request.getSession(true);
        session.setAttribute("authUser", user);
        session.setAttribute("logged", Boolean.TRUE);

        session.setMaxInactiveInterval(30 * 60);

        // token anonimo eventuale (da sessione o cookie)
        String anonToken = (String) session.getAttribute("guestToken");
        if (anonToken == null || anonToken.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("guestToken".equals(c.getName())) {
                        anonToken = c.getValue();
                        break;
                    }
                }
            }
        }

        // merge carrello anonimo -> utente
        CartDao cartDao = new CartDao();
        try {
            cartDao.mergeAnonymousIntoUser(user.getId(), anonToken);
        } catch (SQLException e) {
            throw new ServletException("Errore merge carrello in login", e);
        }

        // carica cart utente aggiornato e cache in sessione
        try {
            CartBean updated = cartDao.loadOpenCart(user.getId(), null);
            session.setAttribute("cart", updated);
            List<CartBean> closed_carts= cartDao.loadCloseCart(user.getId());
            session.setAttribute("closed_carts", closed_carts);
        } catch (SQLException e) {
            session.setAttribute("cart", null); // non bloccare login per UI
        }

        // opzionale: pulisci token anonimo (la navigazione ora è utente)
        session.removeAttribute("user_token");
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    // Per gestire anche le richieste GET (facoltativo)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
