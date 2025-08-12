<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">
</head>
<body>
<%@ include file="/WEB-INF/fragments/header.jsp" %>
<%@ include file="/WEB-INF/fragments/navbar.jsp" %>
<div class="content">
    <section id="left_bar">
        <h3>"left_bar"</h3>
    </section>
    <section id="main">
        <div id="content">
            <h3>INDEX</h3>
            <% model.UserBean user = (model.UserBean) session.getAttribute("authUser"); %>
            <% if (user != null)  { %>
                Ciao <%= user.getName() %> (<%= user.getUsername() %>)
            <% } %>
        </div>
    </section>
    <aside>
        <h3>"aside"</h3>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>