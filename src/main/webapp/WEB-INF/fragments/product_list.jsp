<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <h3>SHOP</h3>

            <ul class="products">
                <c:choose>
                    <c:when test="${not empty products}">
                        <c:forEach var="p" items="${products}">
                            <c:if test="${!p.removed}">
                                <li>
                                    <div class="product_div">
                                        Name: ${p.name}<br>
                                        Description: ${p.description}<br>
                                        Manufacturer: ${p.manufacturer}<br>
                                        Price: ${p.price / 100} &euro;<br>
                                        Stock: ${p.stock}<br>

                                        <c:if test="${not empty p.imagePath}">
                                            <img src="${pageContext.request.contextPath}/image/${p.imagePath}"
                                                 alt="${p.name}" width="160"/>
                                        </c:if>

                                        <div class="cart_btns">
                                            <c:if test="${p.stock > 0}">
                                                <button type="button" class="add_to_cart"
                                                        data-id="${p.id}">Add to cart</button>
                                            </c:if>
                                            <c:if test="${p.stock == 0}">
                                                <button type="button" class="empty" disabled>Out-of-stock</button>
                                            </c:if>

                                            <c:if test="${isAdmin}">
                                                <form action="${pageContext.request.contextPath}/product/delete" method="post">
                                                    <input type="hidden" name="prod_id" value="${p.id}"/>
                                                    <button type="submit" id="del_prod_btn">Elimina</button>
                                                </form>

                                                <form action="${pageContext.request.contextPath}/product/edit" method="post">
                                                    <input type="hidden" name="prod_id" value="${p.id}"/>
                                                    <button type="submit" id="edit_prod_btn">Modifica</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                </li>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">Nessun prodotto disponibile al momento.</div>
                    </c:otherwise>
                </c:choose>
            </ul>
