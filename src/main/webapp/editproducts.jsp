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
        <%
            u = (model.UserBean) session.getAttribute("authUser");
            if (u != null && u.getUsername().equals("admin")) {
        %>
        <div id="content">
            <h3>EDIT PRODUCTS</h3>
            <form action="${pageContext.request.contextPath}/editproducts"
                  method="post" enctype="multipart/form-data">
                <label>Nome</label>
                <input type="text" name="name" required><br>

                <label>Descrizione</label>
                <textarea name="description" required></textarea><br>

                <label>Origine</label>
                <input type="text" name="origin" required><br>

                <label>Produttore</label>
                <input type="text" name="manufacturer" required><br>

                <label>Prezzo (centesimi)</label>
                <input type="number" name="price_cents" required min="0"><br>

                <label>Stock</label>
                <input type="number" name="stock" required min="0"><br>

                <label>Immagine</label>
                <input type="file" name="image" accept="image/*"><br><br>

                <button type="submit" id="add_prod_btn">Crea prodotto</button>
            </form>
        </div>
        <div id="responseMessage"></div>
        <%
            } else {
        %>
        <div id="content">
            <h3>NON SEI AUTORIZZATO</h3>
        </div>
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