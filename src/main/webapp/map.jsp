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
        <div id="dynamic_content">
            <h3>MAP</h3>
        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>