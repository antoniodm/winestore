package controller;

import dao.CartDao;
import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;
import model.CartBean;
import model.CartItem;
import model.UserBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(name = "LastSellServlet", urlPatterns = {"/last-sell"})

public class LastSellServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        ServletContext ctx = getServletContext();

        Map<String,Object> last;
        synchronized (ctx) {
            last = (Map<String,Object>) ctx.getAttribute("lastPurchase");
        }

        PrintWriter out = response.getWriter();

        if (last == null) {
            CartDao cartDao = new CartDao();
            UserDao userDao = new UserDao();
            try {
                CartBean cart = cartDao.retrieveLastClosedCart();

                UserBean user = userDao.doRetrieveById(cart.getUserId());

                System.out.println("last is null, retrived userid: " + cart.getUserId()
                        + " - " + user.getName()
                        + " - " + cart.getTs()
                        + " - " + cart.getItems().size()
                        + " - " + user.getMoney()
                );

                last = new java.util.HashMap<>();
                System.out.println("last in null 2");
                last.put("name", user.getName());
                last.put("date", cart.getTs());
                synchronized (ctx) {
                    ctx.setAttribute("lastPurchase", last);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        System.out.println("last is null, retrived userid: "
                + " - " + last.get("name")
                + " - " + last.get("date"));


        String json = "{"
                + "\"name\":\"" + escape(last.get("name")) + "\","
                + "\"date\":\"" + escape(last.get("date")) + "\""
                + "}";

        out.print(json);
    }

    private String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("\"", "\\\"");
    }
}
