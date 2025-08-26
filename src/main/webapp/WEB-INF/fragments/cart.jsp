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
                    <li>
                            ${item.product.name}<br/>
                            ${item.quantity}<br/>
                        <button type="button" class="remove_from_cart" data-id="${item.productId}">Remove</button><br/>
                            ${item.lineTotalCents}
                    </li>
                </c:if>
            </c:forEach>
        </ul>

        <h3>${cart.totalCents}</h3>
        <button type="button" class="reset_cart">Reset</button>
        <button type="button" class="buy_cart">Buy</button>
    </c:otherwise>
</c:choose>
</div>