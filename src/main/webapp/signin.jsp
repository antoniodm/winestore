
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const content = document.getElementById('content'); // <<< MANCAVA

            // intercetta submit dei form dentro #content
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
                content.innerHTML = html; // aggiornamento nello stesso div
            }, true); // capture=true
        });
    </script>

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
            <form id="signin_form" action="${pageContext.request.contextPath}/signin" method="post" target="_self">
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
                            <input type="tel" id="telefono" name="telefono" autocomplete="tel"
                                   pattern="^[0-9+\s()-]{6,}$" inputmode="tel">
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
                        <button type="submit">Crea account</button>
                    </fieldset>
                </form>
            <h3>"main"</h3>
        </div>
    </section>
    <aside>
        <%@ include file="/WEB-INF/fragments/aside.jsp" %>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>









