package model;

import java.util.ArrayList;
import java.util.List;

public class CartBean {
    private String session_token;
    private int id, user_id, status, is_open;
    ArrayList<ProductBean> products;

    public CartBean() {}

    public int setId(int id) { this.id = id; return 0; }
    public void getId() { this.id = id; }

    public void addProduct(int product_id) {
        ProductBean product = new ProductBean();
        product.setId(product_id);
        if (this.products == null) {
            this.products = new ArrayList<ProductBean>();
        }
        this.products.add(product);
    }

    public void removeProduct(ProductBean product) { this.products.remove(product); }
    public ArrayList<ProductBean> getProducts() { return this.products; }

}
