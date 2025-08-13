package model;

public class CartItem {
    private final int productId;
    private final int unitPriceCents;   // snapshot del prezzo unitario
    private int quantity;
    private final ProductBean product;  // opzionale per UI

    public CartItem(ProductBean product) {
        this.product = product;
        this.productId = product.getId();
        this.unitPriceCents = product.getPrice(); // in centesimi
        this.quantity = 1;
    }

    public ProductBean getProduct() { return product; }
    public int getProductId() { return productId; }
    public int getUnitPriceCents() { return unitPriceCents; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity < 1) quantity = 1;
        this.quantity = quantity;
    }

    public void increaseProductQuantity() { this.quantity++; }
    public void decreaseProductQuantity() { if (this.quantity > 1) this.quantity--; }

    public int getLineTotalCents() { return unitPriceCents * quantity; }
}
