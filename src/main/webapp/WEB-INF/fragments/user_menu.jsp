
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<h3>USER MENU</h3>
<div id="user_menu">
<%@ page import="model.UserBean" %>
<%@ page import="model.CartBean" %>
<% boolean is_logged = Boolean.TRUE.equals(session.getAttribute("logged")); %>
<% if (is_logged) {%>
<% UserBean user = (UserBean) session.getAttribute("authUser"); %>
<% CartBean cart = (CartBean) session.getAttribute("cart"); %>
    <h2>User is logged: <%= user.getName() %></h2>
    <h2>Credit: ${sessionScope.authUser.money}</h2>
<%} else {%>
    <h2>User is not logged</h2>
<%}%>
</div>