<header>
  <div id="banner">
    <h1>Find the best wine ever!!!</h1>
  </div>

  <div id="login">
    <!-- Blocco “utente loggato” -->
    <span style="${sessionScope.logged and not empty sessionScope.authUser ? '' : 'display:none'}">
  Ciao ${sessionScope.authUser.name} (${sessionScope.authUser.username})
  <a href="${pageContext.request.contextPath}/logout">Logout</a> |
  <a href="${pageContext.request.contextPath}/account.jsp">Account</a>
</span>

    <!-- Blocco “ospite” -->
    <span style="${sessionScope.logged and not empty sessionScope.authUser ? 'display:none' : ''}">
  <a href="${pageContext.request.contextPath}/login.jsp">Login</a> |
  <a href="${pageContext.request.contextPath}/signin.jsp">Registrati</a>
</span>  </div>
</header>
