<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div id="user_menu">
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!-- Utente non loggato -->
    <c:if test="${not sessionScope.logged}">
        <h2>User is not logged</h2>
    </c:if>

    <!-- Utente loggato -->
    <c:if test="${sessionScope.logged}">
        <h2>User is logged: ${sessionScope.authUser.name}</h2>
        <h2>Credit: ${sessionScope.authUser.money / 100} &euro;</h2>

        <h3 id="cartsHeader" class="carts-header" aria-expanded="false">
            <a href="#" title="CarrelliChiusi" id="closed_carts">
                Carrelli chiusi (${empty sessionScope.closed_carts ? 0 : sessionScope.closed_carts.size()})
            </a>
        </h3>
    </c:if>

</div>