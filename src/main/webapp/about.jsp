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
            <h2>Chi siamo</h2>
            <p>
                Wine Store nasce dalla passione per il buon vino e dall’idea di renderlo accessibile a tutti.
                Il nostro obiettivo è quello di creare un luogo dove gli amanti del vino possano scoprire,
                acquistare e conoscere nuove etichette con facilità.
            </p>
            <p>
                Collaboriamo con cantine rinomate e piccoli produttori locali, selezionando bottiglie
                che raccontano storie di territori, tradizioni e persone.
                Crediamo che ogni calice sia un’esperienza unica, capace di unire cultura e convivialità.
            </p>
            <h3>La nostra filosofia</h3>
            <p>
                Qualità, passione e attenzione al cliente sono i valori che ci guidano ogni giorno.
                Siamo convinti che il vino non sia solo una bevanda, ma un’esperienza da vivere e condividere.
            </p>

        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>