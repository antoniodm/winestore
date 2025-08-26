<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/winestore.css">
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

            <!-- Se admin: mostra form; altrimenti la servlet ti ha già mandato su unauthorized.jsp -->
            <h3>${isEdit ? 'Modifica prodotto' : 'Nuovo prodotto'}</h3>

            <form action="${formAction}" method="post" enctype="multipart/form-data">
                <!-- Hidden usati in edit -->
                <input type="hidden" name="id" value="${isEdit ? prod.id : ''}">
                <input type="hidden" name="current_image" value="${isEdit && !empty prod.imagePath ? prod.imagePath : ''}">

                <label for="name">Nome</label>
                <input id="name" type="text" name="name" required
                       value="${isEdit ? prod.name : ''}"><br>

                <label for="description">Descrizione</label>
                <textarea id="description" name="description" required>${isEdit ? prod.description : ''}</textarea><br>

                <label for="origin">Origine</label>
                <input id="origin" type="text" name="origin" required
                       value="${isEdit ? prod.origin : ''}"><br>

                <label for="manufacturer">Produttore</label>
                <input id="manufacturer" type="text" name="manufacturer" required
                       value="${isEdit ? prod.manufacturer : ''}"><br>

                <label for="price_cents">Prezzo (centesimi)</label>
                <input id="price_cents" type="number" name="price_cents" required min="0"
                       value="${isEdit ? prod.price : ''}"><br>

                <label for="stock">Stock</label>
                <input id="stock" type="number" name="stock" required min="0"
                       value="${isEdit ? prod.stock : ''}"><br>

                <!-- Immagine attuale (solo se esiste) -->
                <div style="${empty currentImageUrl ? 'display:none' : ''}">
                    <p>Immagine attuale:</p>
                    <img src="${currentImageUrl}" alt="${isEdit ? prod.name : 'Prodotto'}" width="160"><br>
                </div>

                <label for="image">${imageLabel}</label>
                <input id="image" type="file" name="image" accept="image/*"><br><br>

                <button type="submit" id="add_prod_btn">
                    ${isEdit ? 'Salva modifiche' : 'Crea prodotto'}
                </button>

                <a href="${pageContext.request.contextPath}/shop">Annulla</a>
            </form>

            <br>

            <form action="${pageContext.request.contextPath}/product/resurrect" method="post">
                <fieldset>
                    <legend>Prodotti rimossi</legend>
                    <c:forEach var="product" items="${removedProducts}">
                        <label>
                            <input type="checkbox" name="resurrect_id" value="${product.id}" />
                                ${product.name}
                        </label><br>
                    </c:forEach>
                </fieldset>

                <button type="submit" id="recreate_prod_btn">Ripristina selezionati</button>
            </form>


            <!--</div> -->

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
