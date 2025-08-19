<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@ page import="java.util.UUID" %>
<%
    model.UserBean u = (model.UserBean) session.getAttribute("authUser");
    boolean logged = Boolean.TRUE.equals(session.getAttribute("logged"));
  if (logged && u != null) {
%>
Ciao <%= u.getName() %> (<%= u.getUsername() %>)
<a href="${pageContext.request.contextPath}/logout">Logout</a> | <a href="${pageContext.request.contextPath}/account.jsp">Account</a>
<%
} else {
%>
<a href="${pageContext.request.contextPath}/login.jsp">Login</a> | <a href="${pageContext.request.contextPath}/signin.jsp">Registrati</a>
<%
  }
%>
