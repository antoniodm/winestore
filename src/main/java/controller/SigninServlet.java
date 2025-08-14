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
import java.time.LocalDate;
import java.time.Period;

@WebServlet(name = "SigninServlet", urlPatterns = {"/account/edit", "/account/signin"})
public class SigninServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean is_edit = "/account/edit".equals(request.getServletPath());
        boolean is_signin = "/account/signin".equals(request.getServletPath());

        if (!is_signin && !is_edit) return;

        // Imposta il content type della risposta
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // Recupera i parametri dal form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String name = request.getParameter("nome");
        String surname = request.getParameter("cognome");
        String address = request.getParameter("indirizzo");
        String birthdate = request.getParameter("dataNascita");

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

        if (address == null || address.isEmpty()) {
            hasError = true;
            errorMessage.append("Inserire un indirizzo valido.<br>");
        }

        if (birthdate == null || birthdate.isEmpty()) {
            hasError = true;
            errorMessage.append("Inserire una data di nascita valida.<br>");
        } else {
            LocalDate birthDate = LocalDate.parse(birthdate);
            LocalDate today = LocalDate.now();
            int age = Period.between(birthDate, today).getYears();
            if (age < 18) {
                errorMessage.append("Devi avere al meno 18 anni.<br>");
                hasError = true;
            }
        }

        try (PrintWriter out = response.getWriter()) {
            if (hasError) {
                out.println("<h3>Errori nella registrazione:</h3>");
                out.println("<p>" + errorMessage + "</p>");
                out.println("<a href='signin.html'>Torna al form</a>");
            } else {
                // Qui potresti salvare i dati su DB o in sessione
                UserDao service = new UserDao();
                UserBean user = service.doRetrieveByInfo(username, email);
                // Se voglio iscrivermi ma l'utente esiste già
                if ((user != null) && (is_signin) ) {
                    out.println("<h2>L'utente esiste! 1</h2>");
                    out.println("<p>" + name + " " + surname + " (" + username + ")</p>");
                } else {
                    System.out.println(request.getServletPath());
                    System.out.println(request.getPathInfo());
                    if (user == null) {
                        user =  new UserBean();
                    }

                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(password);
                    user.setName(name);
                    user.setSurname(surname);
                    user.setBirthdate(birthdate);
                    user.setAddress(address);
                    // user.setMoney(0);

                    if (is_signin) {
                        if (service.insert(user)) {
                            out.println("<h2>Registrazione avvenuta con successo!</h2>");
                            request.getSession().setAttribute("authUser", user);
                            out.println("<p>Benvenuto, " + user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")</p>");
                        } else {
                            out.println("<h2>L'utente esiste!</h2>");
                            out.println("<p>Benvenuto, " + user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")</p>");
                        }
                    } else if (is_edit) {
                        if (service.update(user)) {
                            out.println("<h2>Modifica avvenuta con successo!</h2>");
                            request.getSession().setAttribute("authUser", user);
                            out.println("<p>Benvenuto, " + user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")</p>");
                        } else {
                            out.println("<h2>L'utente non è stato modificato!</h2>");
                            out.println("<p>Benvenuto, " + user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")</p>");
                        }                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore nel SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
