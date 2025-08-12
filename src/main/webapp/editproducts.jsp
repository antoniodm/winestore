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
        <%
            u = (model.UserBean) session.getAttribute("authUser");
            if (u != null && u.getName().equals("admin")) {
        %>
        <div id="content">
            <h3>EDIT PRODUCTS</h3>
        </div>
        <%
            } else {
        %>
        <div id="content">
            <h3>NON SEI AUTORIZZATO</h3>
        </div>
        <%
            }
        %>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/aside.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>