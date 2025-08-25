<nav id="mainnav">
    <!-- bottone hamburger -->
    <button id="nav-toggle" aria-expanded="false" aria-controls="menu">☰ Menu</button>

    <ul id="menu">
        <li><a href="${pageContext.request.contextPath}/index.jsp" title="Home">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/shop" title="Shop">Shop</a></li>
        <li><a href="${pageContext.request.contextPath}/map.jsp" title="Wine Map">Wine Map</a></li>
        <li><a href="${pageContext.request.contextPath}/about.jsp" title="About">About</a></li>

        <!-- link admin visibile solo se utente loggato ed è "admin" -->
        <li style="${not empty sessionScope.authUser and sessionScope.authUser.username eq 'admin' ? '' : 'display:none'}">
            <a href="${pageContext.request.contextPath}/editproducts.jsp" title="EditProducts">Add Products</a>
        </li>
    </ul>
</nav>

<script>
    const nav = document.getElementById('mainnav');
    const toggle = document.getElementById('nav-toggle');

    toggle.addEventListener('click', () => {
        const expanded = toggle.getAttribute('aria-expanded') === 'true';
        toggle.setAttribute('aria-expanded', String(!expanded));
        nav.setAttribute('aria-expanded', String(!expanded));
    });
</script>
