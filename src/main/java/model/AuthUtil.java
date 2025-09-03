package model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

public final class AuthUtil {
    public static final class Owner {
        public final boolean logged;
        public final Long userId;
        public final String guestToken;
        public Owner(boolean logged, Long userId, String guestToken) {
            this.logged = logged; this.userId = userId; this.guestToken = guestToken;
        }
    }

    public static Owner resolveOwner(HttpServletRequest req, HttpServletResponse res) {
        HttpSession s = req.getSession(true);

        // 1) se loggato: prendo l'utente dalla sessione
        Object uObj = s.getAttribute("authUser");
        if (uObj instanceof model.UserBean u) {
            s.setAttribute("logged", Boolean.TRUE);
            return new Owner(true, u.getId(), null);
        }

        // 2) altrimenti è guest: recupero/creo guestToken in sessione
        String token = (String) s.getAttribute("guestToken");
        if (token == null || token.isBlank()) {
            token = java.util.UUID.randomUUID().toString();
            s.setAttribute("guestToken", token);
            // opzionale: cookie per persistenza oltre sessione
            Cookie c = new Cookie("guestToken", token);
            c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            c.setMaxAge(60 * 60 * 24 * 7); // 7 giorni
            c.setHttpOnly(true);
            c.setSecure(true);
            res.addCookie(c);
        }
        s.setAttribute("logged", Boolean.FALSE);
        return new Owner(false, null, token);
    }
}
