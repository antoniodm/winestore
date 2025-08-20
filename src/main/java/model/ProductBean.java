package model;

public class ProductBean {
    private String name, description, origin, manufacturer, imagePath;
    private int id, price, stock; // price = price_cents
    private boolean is_removed;

    public boolean is_removed() {
        return is_removed;
    }

    public void set_removed(boolean is_removed) {
        this.is_removed = is_removed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getPrice() { return price; }          // in centesimi
    public void setPrice(int price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
