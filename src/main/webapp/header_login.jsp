<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@ page import="java.util.UUID" %>
<%
    model.UserBean u = (model.UserBean) session.getAttribute("authUser");
    boolean logged = Boolean.TRUE.equals(request.getAttribute("logged"));
  if (logged && u != null) {
%>
Ciao <%= u.getName() %> (<%= u.getUsername() %>)
<a href="${pageContext.request.contextPath}/logout">Logout</a>
<%
} else {
%>
<a href="login.jsp">Login</a> | <a href="signin.jsp">Registrati</a>
<%
  }
%>
