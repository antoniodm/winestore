package model;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.UserBean;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class Owner implements Filter {

    private static final String COOKIE = "user_cookie";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(true);
        UserBean u = (UserBean) session.getAttribute("authUser");

        if (u != null) {
            // usa wrapper
            request.setAttribute("logged", Boolean.TRUE);
            // FORZA Long per coerenza ovunque
            request.setAttribute("ownerUserId", Long.valueOf(u.getId()));
            session.removeAttribute("user_token");
        } else {
            request.setAttribute("logged", Boolean.FALSE);

            String token = (String) session.getAttribute("user_token");
            if (token == null) token = readCookie(request, COOKIE);
            if (token == null || token.isBlank()) {
                token = java.util.UUID.randomUUID().toString();
                Cookie c = new Cookie(COOKIE, token);
                String ctx = request.getContextPath();
                c.setPath((ctx == null || ctx.isEmpty()) ? "/" : ctx);
                c.setHttpOnly(true);
                c.setMaxAge(60 * 60 * 24 * 30);
                // c.setSecure(true); // se sei in HTTPS
                response.addCookie(c);
            }
            session.setAttribute("user_token", token);
            request.setAttribute("ownerToken", token);
        }

        chain.doFilter(request, response);
    }


    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cs = req.getCookies(); if (cs == null) return null;
        for (Cookie c : cs) if (name.equals(c.getName())) return c.getValue();
        return null;
    }
}
