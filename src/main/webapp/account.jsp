<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Wine Store</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"> <!-- responsiveness -->
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
            <h3>ACCOUNT</h3>

            <!-- user viene messo dal controller in request (o session) -->
            Ciao <span>${authUser.name}</span> (<span>${authUser.username}</span>)

            <form id="account_form"
                  action="${pageContext.request.contextPath}/account/edit"
                  method="post" target="_self">
                <fieldset>
                    <legend>Modifica utente</legend>

                    <!-- Dati personali -->
                    <div>
                        <label for="nome">Nome</label>
                        <input type="text" id="nome" name="nome"
                               required minlength="2" autocomplete="given-name"
                               value="${authUser.name}">
                    </div>

                    <div>
                        <label for="cognome">Cognome</label>
                        <input type="text" id="cognome" name="cognome"
                               required minlength="2" autocomplete="family-name"
                               value="${authUser.surname}">
                    </div>

                    <div>
                        <label for="indirizzo">Indirizzo</label>
                        <input type="text" id="indirizzo" name="indirizzo"
                               required minlength="2" autocomplete="address-line1"
                               value="${authUser.address}">
                    </div>

                    <div>
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email"
                               required autocomplete="email"
                               value="${authUser.email}">
                    </div>

                    <!-- Credenziali -->
                    <div>
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username"
                               required minlength="4" maxlength="20" autocomplete="username"
                               value="${authUser.username}">
                    </div>

                    <div>
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password"
                               required minlength="8" autocomplete="new-password"
                               aria-describedby="pwd-hint">
                        <small id="pwd-hint">Min 8 caratteri, usa lettere e numeri.</small>
                    </div>

                    <div>
                        <label for="confirmPassword">Conferma password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword"
                               required minlength="8" autocomplete="new-password">
                    </div>

                    <!-- Opzionali -->
                    <div>
                        <label for="telefono">Telefono (opzionale)</label>
                        <input type="tel" id="telefono" name="telefono"
                               autocomplete="tel" pattern="^[0-9+\\s()-]{6,}$" inputmode="tel"
                               value="${authUser.phone}">
                    </div>

                    <div>
                        <label for="dataNascita">Data di nascita (opzionale)</label>
                        <!-- IMPORTANTE: birthdateString deve essere già formattata yyyy-MM-dd dal controller -->
                        <input type="date" id="dataNascita" name="dataNascita"
                               autocomplete="bday"
                               value="${authUser.birthdate}">
                    </div>

                    <!-- Invio -->
                    <button type="submit" id="editAccount">Modifica account</button>
                </fieldset>
            </form>

            <div id="responseMessage"></div>
        </div>
    </section>

    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>
</div>

<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>
