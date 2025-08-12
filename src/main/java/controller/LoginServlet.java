package controller;

import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Imposta il content type della risposta
        // response.setContentType("text/html;charset=UTF-8");
        // request.setCharacterEncoding("UTF-8");

        // Recupera i parametri dal form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validazione di base (esempio)
        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder();

        if (username == null || username.isEmpty()) {
            hasError = true;
            errorMessage.append("Il campo Username è obbligatorio.<br>");
        }
        if (password == null || password.isEmpty()) {
            hasError = true;
            errorMessage.append("Il campo Password è obbligatorio.<br>");
        }

        PrintWriter out = response.getWriter();
            if (hasError) {
                out.println("<h3>Errori nella registrazione:</h3>");
                out.println("<p>" + errorMessage + "</p>");
                out.println("<a href='register.html'>Torna al form</a>");
            } else {
                // Qui potresti salvare i dati su DB o in sessione
                UserDao service = new UserDao();
                UserBean user = service.doRetrieveByUsername(username);
                if (user != null) {
                    if (password.equals(user.getPasswordHash())) {
                        // out.println("<h2>Login effettuato!</h2>");
                        request.changeSessionId();
                        HttpSession session = request.getSession(true);
                        session.setAttribute("authUser", user);
                        session.setMaxInactiveInterval(30*60); // 30 min
                        response.sendRedirect(request.getContextPath() + "/index.jsp");
                        // response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        out.println("<html><body>");
                        out.println("<h2>Password errata!</h2>");
                        out.println("</html></body>");
                    }

                } else {
                    out.println("<html><body>");
                    out.println("<h2>L'utente non esiste!</h2>");
                    out.println("</html></body>");
                }

            }
    }

    // Per gestire anche le richieste GET (facoltativo)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
