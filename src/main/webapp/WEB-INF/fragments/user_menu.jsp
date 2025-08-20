
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
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
    <h3 id="cartsHeader" class="carts-header" aria-expanded="false">
        <a href="${pageContext.request.contextPath}/oldcart.jsp" title="CarrelliChiusi">Carrelli chiusi (<%= closed_carts != null ? closed_carts.size() : 0 %>)</a>
    </h3>
<%} else {%>
    <h2>User is not logged</h2>
<%}%>

</div>