package model;

public class CartItem {
    int product_id, quantity, price_cents;
    private ProductBean product;

    public CartItem(ProductBean product) {
        this.product = product;
        this.product_id = product.getId();
        this.price_cents = product.getPrice();
        this.quantity = 1;
    }

    public ProductBean getProduct() { return this.product; }

    public int getPriceCents() {
        return price_cents;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {}

    public int getProductId() {
        return product_id;
    }

    public void increaseProductQuantity() {
        this.quantity++;
        this.price_cents += product.getPrice();
    }

    public void decreaseProductQuantity() {
        this.quantity--;
        this.price_cents -= product.getPrice();
    }

}
