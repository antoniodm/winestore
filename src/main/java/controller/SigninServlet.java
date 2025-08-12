package controller;
import dao.UserDao;
import model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "SigninServlet", urlPatterns = {"/signin"})
public class SigninServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Imposta il content type della risposta
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // Recupera i parametri dal form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String name = request.getParameter("nome");
        String surname = request.getParameter("cognome");

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
        if (email == null || !email.contains("@")) {
            hasError = true;
            errorMessage.append("Inserire un'email valida.<br>");
        }

        try (PrintWriter out = response.getWriter()) {
            if (hasError) {
                out.println("<h3>Errori nella registrazione:</h3>");
                out.println("<p>" + errorMessage + "</p>");
                out.println("<a href='register.html'>Torna al form</a>");
            } else {
                // Qui potresti salvare i dati su DB o in sessione
                UserDao service = new UserDao();
                UserBean user = service.doRetrieveByInfo(username, email);
                if (user != null) {
                    out.println("<h2>L'utente esiste! 1</h2>");
                    out.println("<p>" + name + " " + surname + " (" + username + ")</p>");
                } else {
                    user =  new UserBean();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(password);
                    user.setName(name);
                    user.setSurname(surname);
                    if (service.insert(user)) {
                        out.println("<h2>Registrazione avvenuta con successo!</h2>");
                        out.println("<p>Benvenuto, " + name + " " + surname + " (" + username + ")</p>");
                    } else {
                        out.println("<h2>L'utente esiste! 2</h2>");
                        out.println("<p>Benvenuto, " + name + " " + surname + " (" + username + ")</p>");
                    }

                }

            }
        } catch (SQLException e) {
            System.out.println("Errore nel SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Per gestire anche le richieste GET (facoltativo)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
