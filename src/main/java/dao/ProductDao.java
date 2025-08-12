package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;

public class ProductDao {

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
        ProductBean p = new ProductBean();
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setOrigin(rs.getString("origin"));
                p.setManufacturer(rs.getString("manufacturer"));
                p.setPrice(rs.getInt("price_cents"));
                p.setStock(rs.getInt("stock"));
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
