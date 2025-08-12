package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartBean {
    private String session_token;
    private int id, user_id, status, is_open;
    ArrayList<CartItem> cart_items;

    public CartBean() {}

    public int setId(int id) { this.id = id; return 0; }
    public void getId() { this.id = id; }

    public String printCart() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<ul>");
        int total_price = 0;
        for (CartItem p : this.cart_items) {
            stringBuilder.append("<li>" + p.getProduct().getName() + "<br>");
            stringBuilder.append(p.getQuantity() + "<br>");
            stringBuilder.append("<button type=\"button\" class=\"remove_from_cart\" data-id=\"" + p.getProduct().getId() + "\">Remove</button><br>");
            stringBuilder.append(p.getPriceCents() + "</li>");
            total_price += p.getPriceCents();
        }
        stringBuilder.append("</ul>");
        stringBuilder.append("<h3>" + total_price + "</h3>");
        stringBuilder.append("<button type=\"button\" class=\"reset_cart\">Remove</button><br>");

        return stringBuilder.toString();
    }

    public void addProduct(ProductBean product) {
        if (this.cart_items == null) {
            this.cart_items = new ArrayList<CartItem>();
        }

        for (CartItem item : this.cart_items) {
            if (item.getProductId() == product.getId()) {
                item.increaseProductQuantity();
                return;
            }
        }

        CartItem item = new CartItem(product);
        this.cart_items.add(item);
    }

    public void removeProduct(ProductBean product) {
        if (product == null || cart_items == null || cart_items.isEmpty()) return;

        for (Iterator<CartItem> it = cart_items.iterator(); it.hasNext(); ) {
            CartItem item = it.next();
            if (item.getProductId() == product.getId()) {
                if (item.getQuantity() > 1) {
                    item.decreaseProductQuantity();
                } else {
                    it.remove();
                }
                return;
            }
        }
    }

    public void reset() {
        cart_items.clear();
    }

    public ArrayList<CartItem> getProducts() { return this.cart_items; }

}
