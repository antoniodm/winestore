package controller;
import dao.*;
import jakarta.servlet.http.HttpSession;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ShopServlet", urlPatterns = {"/shop"})
public class ShopServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("authUser") == null) {
            // nessuna sessione => utente non loggato
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        } else {
            System.out.println("Utente: " + session.getAttribute("authUser"));
        }

        ProductDao service = new ProductDao();
        List<ProductBean> products = service.doRetrieveAll();

        for (ProductBean product : products) {
            System.out.println(product.getName());
        }

        request.setAttribute("products", products);
        request.getRequestDispatcher("/shop.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
