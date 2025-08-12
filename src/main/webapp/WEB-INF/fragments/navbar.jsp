<nav>
    <ul id="menu">
        <li><a class="${requestScope.active == 'home' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/index.jsp" title="Home">Home</a></li>

        <li><a class="${requestScope.active == 'shop' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/shop.jsp" title="Shop">Shop</a></li>

        <li><a class="${requestScope.active == 'map' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/map.jsp" title="Wine Map">Wine Map</a></li>

        <li><a class="${requestScope.active == 'about' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/about.jsp" title="About">About</a></li>

        <%
            model.UserBean u = (model.UserBean) session.getAttribute("authUser");
            if (u != null && u.getName().equals("admin")) {
        %>
        <li><a class="${requestScope.active == 'about' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/editproducts.jsp" title="EditProducts">Edit Products</a></li>
        <%
            }
        %>

    </ul>
</nav>