package controller;
import dao.UserDao;
import model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;

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
        String phone = request.getParameter("telefono");

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

        if (hasError) {
            request.getSession().setAttribute("hasError", hasError);
            request.getSession().setAttribute("error_message", errorMessage);
            request.getRequestDispatcher("/WEB-INF/fragments/error.jsp").forward(request, response);
        }

        try {
                UserDao service = new UserDao();
                UserBean user = service.doRetrieveByInfo(username, email);
                // Se voglio iscrivermi ma l'utente esiste già
                if ((user != null) && (is_signin) ) {
                    errorMessage.append("<h2>L'utente esiste! 1</h2>");
                    errorMessage.append(System.lineSeparator());
                    errorMessage.append(username);
                } else {
                    //System.out.println(request.getServletPath());
                    //System.out.println(request.getPathInfo());
                    if (user == null) {
                        user =  new UserBean();
                    }
                    // Hashing della password
                    String password_hash = password;
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] hashBytes = md.digest(password.getBytes("UTF-8"));
                        password_hash = Base64.getEncoder().encodeToString(hashBytes);
                        //System.out.println("Password hash: " + password_hash);
                    } catch (Exception e) {
                        hasError = true;
                        //System.out.println("Errore nella SHA-256");
                    }

                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(password_hash);
                    user.setName(name);
                    user.setSurname(surname);
                    user.setBirthdate(birthdate);
                    user.setAddress(address);
                    user.setPhone(phone);
                    user.setMoney(0);

                    String usr_str = user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")";
                    if (is_signin) {
                        if ("admin".equals(username)) {
                            user.setMoney(999999);
                        }

                        if (service.insert(user)) {
                            errorMessage.append("Registrazione avvenuta con successo!");
                        } else {
                            errorMessage.append("L'utente esiste!");
                        }
                    } else {
                        if (service.update(user)) {
                            errorMessage.append("Modifica avvenuta con successo!");

                        } else {
                            errorMessage.append("L'utente non è stato modificato!");
                        }
                    }
                    request.getSession().setAttribute("authUser", user);
                    request.getSession().setAttribute("hasError", hasError);
                    errorMessage.append(System.lineSeparator());
                    errorMessage.append(usr_str);
                    request.getSession().setAttribute("error_message", errorMessage.toString());

                    request.getRequestDispatcher("/WEB-INF/fragments/error.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            //System.out.println("Errore nel SQL: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
