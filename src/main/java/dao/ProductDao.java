package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;

public class ProductDao {

    public boolean insert(ProductBean p) {
        String sql = "INSERT INTO products " +
                "(name, description, origin, manufacturer, image_path, price_cents, stock) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getOrigin());
            ps.setString(4, p.getManufacturer());
            ps.setString(5, p.getImagePath());
            ps.setInt(6, p.getPrice());
            ps.setInt(7, p.getStock());
            System.out.println(ps);
            int rows = ps.executeUpdate();
            con.commit();
            return rows == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean update(ProductBean p) {
        String sql = "UPDATE products SET " +
                "name = ?, description = ?, origin = ?, manufacturer = ?, " +
                "image_path = ?, price_cents = ?, stock = ? " +
                "WHERE id = ?";

        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getOrigin());
            ps.setString(4, p.getManufacturer());
            ps.setString(5, p.getImagePath());
            ps.setInt(6, p.getPrice());
            ps.setInt(7, p.getStock());
            ps.setInt(8, p.getId());
            System.out.println(ps);
            int rows = ps.executeUpdate();
            con.commit();
            return rows == 1;
        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean remove(ProductBean p) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, p.getId()); // cancella in base all'id
            System.out.println(ps);
            int rows = ps.executeUpdate();

            con.commit();
            return rows == 1; // true se ha eliminato 1 riga
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<ProductBean> doRetrieveAll() {
        try (Connection con = ConPool.getConnection()) {
            ArrayList<ProductBean> products = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductBean p = new ProductBean();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setOrigin(rs.getString("origin"));
                p.setManufacturer(rs.getString("manufacturer"));
                p.setImagePath(rs.getString("image_path"));
                p.setPrice(rs.getInt("price_cents"));
                p.setStock(rs.getInt("stock"));
                products.add(p);
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductBean doRetrieveById(int id) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id,name,description,origin,manufacturer, image_path, price_cents,stock FROM products WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            ProductBean p = new ProductBean();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setDescription(rs.getString("description"));
            p.setOrigin(rs.getString("origin"));
            p.setManufacturer(rs.getString("manufacturer"));
            p.setImagePath(rs.getString("image_path"));
            p.setPrice(rs.getInt("price_cents"));
            p.setStock(rs.getInt("stock"));
            return p;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updateImagePath(long productId, String imagePath) throws SQLException {
        String sql = "UPDATE products SET image_path=? WHERE id=?";
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, productId);
            ps.executeUpdate();
        }
    }
}
