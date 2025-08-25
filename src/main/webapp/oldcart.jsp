<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Wine Store</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/winestore.css">
    <script>window.CTX='${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/scripts.js" defer></script>
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

            <!-- Utente NON loggato -->
            <div style="display:${logged ? 'none' : 'block'};">
                <h2>User is not logged</h2>
            </div>

            <!-- Utente loggato -->
            <div style="display:${logged ? 'block' : 'none'};">
                <h3>Storico carrelli</h3>

                <!-- Lista carrelli chiusi (HTML già pronto dalla servlet) -->
                <ul class="old_carts">
                    ${renderedClosedCarts}
                </ul>

                <!-- Stato vuoto -->
                <p class="empty-state" style="display:${hasClosedCarts ? 'none' : 'block'};">
                    ${emptyMessage}
                </p>
            </div>

        </div>
    </section>

    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>
</div>

<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>
