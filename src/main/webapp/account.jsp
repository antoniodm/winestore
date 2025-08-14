<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">
    <script>
        const CTX = '<%= request.getContextPath() %>'; // es: "/winestore"

        document.addEventListener('DOMContentLoaded', () => {
            const content = document.getElementById('content');
            const responseMessage = document.getElementById('responseMessage');

            content.addEventListener('submit', async (e) => {
                const form = e.target;
                if (!(form instanceof HTMLFormElement)) return;

                e.preventDefault();
                if (!form.reportValidity()) return;

                const body = new URLSearchParams(new FormData(form));

                const res = await fetch(form.action, {
                    method: (form.method || 'POST').toUpperCase(),
                    body,
                    credentials: 'same-origin',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                const html = await res.text();
                responseMessage.innerHTML = html; // aggiorna solo il div messaggi
            }, true);
        });
    </script>
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
            <h3>ACCOUNT</h3>
            <% model.UserBean user = (model.UserBean) session.getAttribute("authUser"); %>
            <% if (user != null)  { %>
                Ciao <%= user.getName() %> (<%= user.getUsername() %>)

            <form id="account_form" action="${pageContext.request.contextPath}/account/edit" method="post" target="_self">
                <fieldset>
                    <legend>Modifica utente</legend>

                    <!-- Dati personali -->
                    <div>
                        <label for="nome">Nome</label>
                        <input type="text" id="nome" name="nome" required minlength="2" autocomplete="given-name" value="<%= user.getName() %>">
                    </div>

                    <div>
                        <label for="cognome">Cognome</label>
                        <input type="text" id="cognome" name="cognome" required minlength="2" autocomplete="family-name" value="<%= user.getSurname() %>">
                    </div>

                    <div>
                        <label for="indirizzo">Indirizzo</label>
                        <input type="text" id="indirizzo" name="indirizzo" required minlength="2" autocomplete="address" value="<%= user.getAddress() %>">
                    </div>

                    <div>
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required autocomplete="email" value="<%= user.getEmail() %>">
                    </div>

                    <!-- Credenziali -->
                    <div>
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required minlength="4" maxlength="20" autocomplete="username" value="<%= user.getUsername() %>">
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
                                aria-describedby="pwd-hint"
                                value="<%= user.getPasswordHash() %>">
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
                                autocomplete="new-password"
                                value="<%= user.getPasswordHash() %>">
                    </div>

                    <!-- Opzionali -->
                    <div>
                        <label for="telefono">Telefono (opzionale)</label>
                        <input type="tel" id="telefono" name="telefono" autocomplete="tel" pattern="^[0-9+\s()-]{6,}$" inputmode="tel">
                    </div>

                    <div>
                        <label for="dataNascita">Data di nascita (opzionale)</label>
                        <input type="date" id="dataNascita" name="dataNascita" autocomplete="bday" required value="<%= user.getBirthdate() %>">
                    </div>

                    <!-- Invio -->
                    <button type="submit">Modifica account</button>
                </fieldset>
            </form>
            <div id="responseMessage"></div>
            <% } %>
        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/cart.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>