package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class CartBean {
    private Integer id;            // carts.id (INT UNSIGNED)
    private Long userId;           // carts.user_id (BIGINT) se loggato
    private String sessionToken;   // carts.session_token se anonimo
    private CartStatus status = CartStatus.OPEN;

    public enum CartStatus { OPEN, CLOSED }

    private final List<CartItem> items = new ArrayList<>();

    // --- Meta ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; this.sessionToken = null; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; this.userId = null; }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }

    // --- Items ---
    public List<CartItem> getProducts() { return items; }

    public void addProduct(ProductBean product) {
        for (CartItem item : items) {
            if (item.getProductId() == product.getId()) {
                item.increaseProductQuantity();
                return;
            }
        }
        items.add(new CartItem(product));
    }

    public void removeProduct(ProductBean product) {
        if (product == null || items.isEmpty()) return;
        for (Iterator<CartItem> it = items.iterator(); it.hasNext();) {
            CartItem item = it.next();
            if (item.getProductId() == product.getId()) {
                if (item.getQuantity() > 1) item.decreaseProductQuantity();
                else it.remove();
                return;
            }
        }
    }

    public void reset() { items.clear(); }

    public int getTotalCents() {
        int tot = 0;
        for (CartItem it : items) tot += it.getLineTotalCents();
        return tot;
    }

    public String printCart() {
        if (items.isEmpty()) return "<div class='cart-empty'>Carrello vuoto</div>";
        StringBuilder sb = new StringBuilder("<ul>");
        for (CartItem it : items) {
            sb.append("<li>")
                    .append(it.getProduct().getName()).append("<br>")
                    .append(it.getQuantity()).append("<br>")
                    .append("<button type=\"button\" class=\"remove_from_cart\" data-id=\"")
                    .append(it.getProductId()).append("\">Remove</button><br>")
                    .append(it.getLineTotalCents()).append("</li>");
        }
        sb.append("</ul>")
                .append("<h3>").append(getTotalCents()).append("</h3>")
                .append("<button type=\"button\" class=\"reset_cart\">Reset</button>");
        return sb.toString();
    }
}
