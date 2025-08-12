<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%
  model.UserBean u = (model.UserBean) session.getAttribute("authUser");
  if (u != null) {
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
