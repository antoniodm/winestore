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
        <div id="user_menu">
            <%@ page import="model.UserBean" %>
            <%@ page import="model.CartBean" %>
            <%@ page import="java.util.List" %>
            <%@ page import="dao.CartDao" %>
            <%@ page import="model.CartItem" %>

            <% if (is_logged) {%>
            <% UserBean user = (UserBean) session.getAttribute("authUser"); %>
            <% CartBean cart = (CartBean) session.getAttribute("cart"); %>
            <%CartDao cartDao= new CartDao();%>
            <%List<CartBean> closed_carts= cartDao.loadCloseCart(user.getId());%>
            <% if (closed_carts != null && !closed_carts.isEmpty()) { %>
            <ul>
                <% for (CartBean oldcart : closed_carts) { %>
                <li>
                    <div class="product_div">
                    Carrello #<%= oldcart.getId() %><br>
                    Prodotti:
                    <ul class="old_cart_item_menu">
                        <% for (CartItem item : oldcart.getProducts()) { %>
                        <li>
                            <%= item.getProduct().getName() %> -
                            Quantità: <%= item.getQuantity() %> -
                            Prezzo unitario: <%= item.getProduct().getPrice() %>
                        </li>
                        <% } %>
                    </ul>
                    </div>
                </li>
                <% } %>
            </ul>
            <% } else { %>
            <p>Nessun carrello chiuso trovato.</p>
            <% } %>
            <%} else {%>
            <h2>User is not logged</h2>
            <%}%>

        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>
</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>