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
    <div id="content">
        <form id="login-form" name="login-form" method="POST" action="${pageContext.request.contextPath}/login">
          <label for="username">Username</label>
          <input type="text" id="username" name="username" required>
          <label for="password">Password</label>
          <input type="password" id="password" name="password" required>
          <button type="submit">Accedi</button>
        </form>
        <a href="signin.jsp" title="Signin">Registrazione nuovo Utente</a>
      <h3>"main"</h3>
    </div>
  </section>
  <aside>
      <%@ include file="/WEB-INF/fragments/cart.jsp" %>
  </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>