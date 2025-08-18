<%@ page import="model.UserBean, model.ProductBean" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%
        String ctx = request.getContextPath();
        UserBean userNav = (UserBean) session.getAttribute("authUser");
        ProductBean product = (ProductBean) request.getAttribute("prod");
        boolean isEdit = (product != null);
    %>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="<%= ctx %>/winestore.css">
    <script> window.CTX = '<%= ctx %>'; </script>
    <script src="<%= ctx %>/scripts.js" defer></script>
</head>
<body>
<%@ include file="/WEB-INF/fragments/header.jsp" %>
<%@ include file="/WEB-INF/fragments/navbar.jsp" %>
<div class="content">
    <section id="left_bar">
        <%@ include file="/WEB-INF/fragments/user_menu.jsp" %>
    </section>

    <section id="main">
        <%
            if (userNav != null && "admin".equals(userNav.getUsername())) {
        %>
        <div id="content">
            <h3><%= isEdit ? "Modifica prodotto" : "Nuovo prodotto" %></h3>

            <form action="<%= isEdit ? (ctx + "/product/edit") : (ctx + "/admin/product/insert") %>"
                  method="post" enctype="multipart/form-data">

                <% if (isEdit) { %>
                <input type="hidden" name="id" value="<%= product.getId() %>">
                <input type="hidden" name="current_image" value="<%= product.getImagePath() == null ? "" : product.getImagePath() %>">
                <% } %>

                <label>Nome</label>
                <input type="text" name="name" required
                       value="<%= isEdit ? product.getName() : "" %>"><br>

                <label>Descrizione</label>
                <textarea name="description" required><%= isEdit ? product.getDescription() : "" %></textarea><br>

                <label>Origine</label>
                <input type="text" name="origin" required
                       value="<%= isEdit ? product.getOrigin() : "" %>"><br>

                <label>Produttore</label>
                <input type="text" name="manufacturer" required
                       value="<%= isEdit ? product.getManufacturer() : "" %>"><br>

                <label>Prezzo (centesimi)</label>
                <input type="number" name="price_cents" required min="0"
                       value="<%= isEdit ? product.getPrice() : "" %>"><br>

                <label>Stock</label>
                <input type="number" name="stock" required min="0"
                       value="<%= isEdit ? product.getStock() : "" %>"><br>

                <% if (isEdit && product.getImagePath() != null && !product.getImagePath().isEmpty()) { %>
                <p>Immagine attuale:</p>
                <img src="<%= ctx %>/image/<%= product.getImagePath() %>" alt="<%= product.getName() %>" width="160"><br>
                <% } %>

                <label><%= isEdit ? "Sostituisci immagine (opzionale)" : "Immagine (opzionale)" %></label>
                <input type="file" name="image" accept="image/*"><br><br>

                <button type="submit"><%= isEdit ? "Salva modifiche" : "Crea prodotto" %></button>
                <a href="<%= ctx %>/admin/products">Annulla</a>
            </form>
        </div>
        <div id="responseMessage"></div>
        <%
        } else {
        %>
        <div id="content"><h3>NON SEI AUTORIZZATO</h3></div>
        <%
            }
        %>
    </section>

    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>
</div>

<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>
