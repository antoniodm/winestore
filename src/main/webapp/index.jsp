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
            <% String loginError = (String) request.getAttribute("loginError"); %>
            <% if (loginError != null) { %>
                <%= loginError %>
            <% } else {%>
                <% model.UserBean user = (model.UserBean) session.getAttribute("authUser"); %>
                <% if (user != null)  { %>
                    <h2>Benvenuto <%= user.getName() %> (<%= user.getUsername() %>) su Wine Store</h2>
                <% } else {%>
                    <h2>Benvenuto su Wine Store</h2>
                <%}%>
            <% } %>

            <p>
                Scopri la nostra selezione dei migliori vini italiani e internazionali, scelti con cura
                per offrirti solo la massima qualità. Dal Chianti al Barolo, dal Prosecco alle etichette più ricercate,
                su Wine Store trovi la bottiglia perfetta per ogni occasione.
            </p>
            <p>
                Ordina in pochi click e ricevi direttamente a casa tua, con la garanzia di un servizio rapido
                e sicuro. Che tu sia un intenditore o un appassionato alle prime armi,
                qui potrai trovare il vino giusto da condividere con chi ami.
            </p>
            <h3>Le nostre promesse</h3>
            <ul>
                <li>Vini certificati e garantiti</li>
                <li>Prezzi trasparenti e convenienti</li>
                <li>Spedizioni veloci in tutta Italia</li>
                <li>Assistenza clienti sempre disponibile</li>
            </ul>


        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>