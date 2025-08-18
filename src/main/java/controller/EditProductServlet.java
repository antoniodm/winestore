package controller;

import dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProductBean;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(name = "EditProductServlet", urlPatterns = {"/product/update", "/product/edit", "/product/delete"})
@MultipartConfig(
        fileSizeThreshold = 1_000_000,    // 1MB buffer
        maxFileSize = 5_000_000L,         // 5MB
        maxRequestSize = 6_000_000L
)

public class EditProductServlet extends HttpServlet {
    private Path imagesDir(HttpServletRequest req) {
        // <app>/WEB-INF/images
        return Paths.get(getServletContext().getRealPath("/WEB-INF/images"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean is_edit =   "/product/edit".equals(request.getServletPath());
        boolean is_delete = "/product/delete".equals(request.getServletPath());
        boolean is_update = "/product/update".equals(request.getServletPath());

        System.out.println("Called product edit servlet " + " edit: " + is_edit + " delete: " + is_delete + " update: " + is_update);

        if (is_edit) {
            System.out.println("Called product edit servlet isEdit");
            String product_id = request.getParameter("prod_id");
            ProductBean prod = new ProductDao().doRetrieveById(Integer.parseInt(product_id));
            if (prod != null) {
                System.out.println("Called product edit servlet prod: " + prod.getName());
                request.setAttribute("prod", prod);
                request.getRequestDispatcher("/editproducts.jsp").forward(request, response);
            } else {
                System.out.println("Called product edit servlet prod is null");
            }
        }

        if (is_delete) {
            System.out.println("Called product delete servlet isDelete");
        }

        if (is_update) {
            System.out.println("Called product update servlet isUpdate");
        }
    }
}
