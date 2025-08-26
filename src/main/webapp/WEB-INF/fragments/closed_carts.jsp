<h3>CLOSED CART</h3>
<div id="closed_carts">

    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:if test="${not empty error_message}">
        <div id="error_message" data-msg="${error_message}"></div>
    </c:if>
    <h2>Storico acquisti</h2>

    <c:choose>
        <c:when test="${not empty closed_carts}">
            <c:forEach var="cart" items="${closed_carts}">
                <div class="cart">
                    <h3>Carrello #${cart.id}</h3>

                    <c:if test="${empty cart.items}">
                        <p>Carrello vuoto.</p>
                    </c:if>

                    <c:if test="${not empty cart.items}">
                        <table class="cart-table">
                            <thead>
                            <tr>
                                <th>Prodotto</th>
                                <th>Quantità</th>
                                <th>Prezzo unitario</th>
                                <th>Totale</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="item" items="${cart.items}">
                                <tr>
                                    <td>${item.product.name}</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.product.price}</td>
                                    <td>${item.lineTotalCents}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <p class="cart-total">Totale carrello: ${cart.totalCents}</p>
                    </c:if>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p>Nessun carrello chiuso trovato.</p>
        </c:otherwise>
    </c:choose>

</div>