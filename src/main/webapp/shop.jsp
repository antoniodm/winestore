<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wine Store</title>
    <link rel="stylesheet" type="text/css" href="winestore.css">
</head>
<body>
<%@ include file="/WEB-INF/fragments/header.jsp" %>
<%@ include file="/WEB-INF/fragments/navbar.jsp" %>
<div class="content">
    <section id="left_bar">
        <h3>"left_bar"</h3>
    </section>
    <section id="main">
        <div id="content">
            <h3>SHOP</h3>
            <%@ page import="java.util.List, model.ProductBean"%>
            <% List<ProductBean> products = (List<ProductBean>) request.getAttribute("products"); %>
            <% if (products != null) { %>
            <ul class="products">
                <% for (ProductBean product : products) { %>
                <li>
                    Id: <%= product.getId() %><br>
                    Name: <%= product.getName() %><br>
                    Price: <%= product.getPrice() %><br>
                    <button type="button" class="add_to_cart" data-id="<%= product.getId() %>">Add to cart</button>
                </li>
                <% } %>
            </ul>
            <% } %>
        </div>
    </section>
    <aside>
        <h3>"aside"</h3>
        <div id="cart_panel"></div>
        <script>
            document.addEventListener('click', async(e) => {
                const btn = e.target.closest('.add_to_cart');
                if (!btn) return;
                e.preventDefault();
                const res = await fetch('${pageContext.request.contextPath}/cart', {
                    method : 'POST',
                    credentials : "same-origin",
                    headers : {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                        'X-Requested-With': 'XMLHttpRequest' },
                    body : new URLSearchParams({ id: btn.dataset.id })
                });
                const html = await res.text();
                document.getElementById('cart_panel').innerHTML = html;
            });
        </script>
    </aside>

</div>
<%@ include file="/WEB-INF/fragments/footer.jsp" %>
</body>
</html>