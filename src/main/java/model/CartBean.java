package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.time.*;


public class CartBean {
    private Integer id;            // carts.id (INT UNSIGNED)
    private Long userId;           // carts.user_id (BIGINT) se loggato
    private String sessionToken;   // carts.session_token se anonimo
    private Timestamp ts;
    private CartStatus status = CartStatus.OPEN;
    public enum CartStatus { OPEN, CLOSED }
    private List<CartItem> items = new ArrayList<>();

    // --- Meta ---
    public Timestamp getTs() {return ts;}
    public void setTs(Timestamp ts) {this.ts = ts;}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; this.sessionToken = null; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; this.userId = null; }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }

    // --- Items ---
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

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

}
