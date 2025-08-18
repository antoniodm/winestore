<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">
    <script> window.CTX = '<%= request.getContextPath() %>'; </script>
    <script src="scripts.js" defer></script>

</head>
<body>
<%@ include file="/WEB-INF/fragments/header.jsp" %>
<%@ include file="/WEB-INF/fragments/navbar.jsp" %>
<div class="content">
    <section id="left_bar">
        <%@ include file="/WEB-INF/fragments/user_menu.jsp" %>
    </section>
    <section id="main">
        <div id="content">
            <h3>SHOP</h3>
            <%@ page import="java.util.List, model.ProductBean"%>
            <% List<ProductBean> products = (List<ProductBean>) request.getAttribute("products"); %>
            <% if (products != null) { %>
            <ul class="products">
                <% for (ProductBean product : products) { %>
                <li>
                    Id: <%= product.getId() %><br>
                    Name: <%= product.getName() %><br>
                    Price: <%= product.getPrice() %><br>
                    Stock: <%= product.getStock() %><br>
                    <% if (product.getImagePath() != null && !product.getImagePath().isEmpty()) { %>
                    <img src="<%= request.getContextPath() %>/image/<%= product.getImagePath() %>" alt="<%= product.getName() %>" width="160">
                    <% } %>
                    <button type="button" class="add_to_cart" data-id="<%= product.getId() %>">Add to cart</button>
                    <%
                        u = (model.UserBean) session.getAttribute("authUser");
                        if (u != null && u.getUsername().equals("admin")) {
                    %>
                        <button type="button" class="del_prod_btn" data-id="<%= product.getId() %>">Edit</button>
                        <button type="button" class="edit_prod_btn" data-id="<%= product.getId() %>">Delete</button>
                    <%}%>
                </li>
                <% } %>
            </ul>
            <% } %>
        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>
</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>