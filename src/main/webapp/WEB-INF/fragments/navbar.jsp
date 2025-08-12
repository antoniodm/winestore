<nav>
    <ul id="menu">
        <li><a href="${pageContext.request.contextPath}/index.jsp" title="Home">Home</a></li>

        <li><a href="${pageContext.request.contextPath}/shop" title="Shop" methods="post">Shop</a></li>

        <li><a href="${pageContext.request.contextPath}/map.jsp" title="Wine Map">Wine Map</a></li>

        <li><a href="${pageContext.request.contextPath}/about.jsp" title="About">About</a></li>

        <%
            model.UserBean u = (model.UserBean) session.getAttribute("authUser");
            if (u != null && u.getUsername().equals("admin")) {
        %>
        <li><a href="${pageContext.request.contextPath}/editproducts.jsp" title="EditProducts">Edit Products</a></li>
        <%
            }
        %>

    </ul>
</nav>