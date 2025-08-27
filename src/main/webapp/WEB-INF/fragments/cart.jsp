<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div id="cart_panel">
<h3>CART</h3>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<c:choose>
    <c:when test="${empty cart.items}">
        <div class='cart-empty'>Carrello vuoto JSTL</div>
    </c:when>
    <c:otherwise>
        <ul>
            <c:forEach var="item" items="${cart.items}">
                <c:if test="${!item.product.removed}">
                    <li>${item.quantity}  x ${item.product.name}<br/>
                        <button type="button" class="remove_from_cart" data-id="${item.productId}">Remove</button><br/>
                            ${item.lineTotalCents / 100} &euro;
                    </li>
                </c:if>
            </c:forEach>
        </ul>

        <h3>${cart.totalCents / 100} &euro;</h3>
        <button type="button" class="reset_cart">Reset</button>
        <button type="button" class="buy_cart">Buy</button>
    </c:otherwise>
</c:choose>
    <br>
    ${requestScope.cart_message}
</div>