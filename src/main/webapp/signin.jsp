<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Wine Store</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/winestore.css">
    <script>
        window.CTX = '${pageContext.request.contextPath}';
    </script>
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
            <form id="signin_form"
                  action="${pageContext.request.contextPath}/account/signin"
                  method="post">
                <fieldset>
                    <legend>Registrazione utente</legend>

                    <!-- Dati personali -->
                    <div>
                        <label for="nome">Nome</label>
                        <input type="text" id="nome" name="nome" required minlength="2" autocomplete="given-name">
                    </div>

                    <div>
                        <label for="cognome">Cognome</label>
                        <input type="text" id="cognome" name="cognome" required minlength="2" autocomplete="family-name">
                    </div>

                    <div>
                        <label for="indirizzo">Indirizzo</label>
                        <input type="text" id="indirizzo" name="indirizzo" required minlength="2" autocomplete="address-line1">
                    </div>

                    <div>
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required autocomplete="email">
                    </div>

                    <!-- Credenziali -->
                    <div>
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required minlength="4" maxlength="20" autocomplete="username">
                    </div>

                    <div>
                        <label for="password">Password</label>
                        <input
                                type="password"
                                id="password"
                                name="password"
                                required
                                minlength="8"
                                autocomplete="new-password"
                                aria-describedby="pwd-hint">
                        <small id="pwd-hint">Min 8 caratteri, usa lettere e numeri.</small>
                    </div>

                    <div>
                        <label for="confirmPassword">Conferma password</label>
                        <input
                                type="password"
                                id="confirmPassword"
                                name="confirmPassword"
                                required
                                minlength="8"
                                autocomplete="new-password">
                    </div>

                    <!-- Opzionali -->
                    <div>
                        <label for="telefono">Telefono (opzionale)</label>
                        <input type="tel" id="telefono" name="telefono"
                               autocomplete="tel" pattern="^[0-9()+\s\-]{6,}$" inputmode="tel">
                    </div>

                    <div>
                        <label for="dataNascita">Data di nascita (opzionale)</label>
                        <input type="date" id="dataNascita" name="dataNascita" autocomplete="bday">
                    </div>

                    <!-- Consensi -->
                    <div>
                        <input type="checkbox" id="tos" name="tos" required>
                        <label for="tos">Accetto i Termini di Servizio e l’Informativa Privacy</label>
                    </div>

                    <!-- Invio -->
                    <button type="submit" id="btnRegister">Crea account</button>
                </fieldset>
            </form>

            <div id="responseMessage">
                ${empty errorMessage ? '' : errorMessage}
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
