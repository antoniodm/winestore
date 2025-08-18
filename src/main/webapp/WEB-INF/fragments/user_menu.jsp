
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<h3>USER MENU</h3>
<div id="user_menu">
    <%@ page import="model.UserBean" %>
    <%@ page import="model.CartBean" %>
    <%@ page import="java.util.List" %>
    <%@ page import="dao.CartDao" %>
    <%@ page import="model.CartItem" %>
    <% boolean is_logged = Boolean.TRUE.equals(session.getAttribute("logged")); %>
<% if (is_logged) {%>
    <% UserBean user = (UserBean) session.getAttribute("authUser"); %>
    <% CartBean cart = (CartBean) session.getAttribute("cart"); %>
    <h2>User is logged: <%= user.getName() %></h2>
    <h2>Credit: ${sessionScope.authUser.money}</h2>
    <%CartDao cartDao= new CartDao();%>
    <%List<CartBean> closed_carts= cartDao.loadCloseCart(user.getId());%>
    <h3>Storico carrelli chiusi</h3>
    <% if (closed_carts != null && !closed_carts.isEmpty()) { %>
    <ul>
        <% for (CartBean oldcart : closed_carts) { %>
        <li>
            Carrello #<%= oldcart.getId() %><br>
            Prodotti:
            <ul>
                <% for (CartItem item : oldcart.getProducts()) { %>
                <li>
                    <%= item.getProduct().getName() %> -
                    Quantità: <%= item.getQuantity() %> -
                    Prezzo unitario: <%= item.getProduct().getPrice() %>
                </li>
                <% } %>
            </ul>
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