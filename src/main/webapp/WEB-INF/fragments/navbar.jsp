
<nav id="mainnav">
    <!-- bottone hamburger -->
    <button id="nav-toggle" aria-expanded="false" aria-controls="menu">☰ Menu</button>

    <ul id="menu">
        <li><a href="${pageContext.request.contextPath}/index.jsp" title="Home">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/shop" title="Shop">Shop</a></li>
        <li><a href="${pageContext.request.contextPath}/map.jsp" title="Wine Map">Wine Map</a></li>
        <li><a href="${pageContext.request.contextPath}/about.jsp" title="About">About</a></li>
        <%
            model.UserBean u = (model.UserBean) session.getAttribute("authUser");
            if (u != null && u.getUsername().equals("admin")) {
        %>
        <li><a href="${pageContext.request.contextPath}/editproducts.jsp" title="EditProducts">Add Products</a></li>
        <%
            }
        %>
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