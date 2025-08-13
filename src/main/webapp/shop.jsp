<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">
</head>
<body>
<%@ include file="/WEB-INF/fragments/header.jsp" %>
<%@ include file="/WEB-INF/fragments/navbar.jsp" %>
<div class="content">
    <section id="left_bar">
        <h3>"left_bar"</h3>
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
                    <button type="button" class="add_to_cart" data-id="<%= product.getId() %>">Add to cart</button>
                </li>
                <% } %>
            </ul>
            <% } %>
        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/aside.jsp" %>
    </aside>
</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>