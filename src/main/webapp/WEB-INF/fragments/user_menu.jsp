<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>

<div id="user_menu">

    <!-- Utente non loggato -->
    <div style="${sessionScope.logged ? 'display:none' : 'block'}">
        <h2>User is not logged</h2>
    </div>

    <!-- Utente loggato -->
    <div style="${sessionScope.logged ? 'block' : 'none'}">
        <h2>User is logged: ${sessionScope.authUser.name}</h2>
        <h2>Credit: ${sessionScope.authUser.money}</h2>

        <h3 id="cartsHeader" class="carts-header" aria-expanded="false">
            <a href="#" title="CarrelliChiusi" id="closed_carts">
                Carrelli chiusi (${empty sessionScope.closed_carts ? 0 : sessionScope.closed_carts.size()})
            </a>
        </h3>
    </div>

</div>
