package dao;

import model.UserBean;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDao {

    public UserBean doRetrieveByInfo(String username, String email) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE username =? OR email =?");
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserBean p = new UserBean();
                p.setId(rs.getInt(1));
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserBean doRetrieveByUsername(String username) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id,email,name, surname, password_hash FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserBean p = new UserBean();
                p.setId(rs.getInt(1));
                p.setEmail(rs.getString(2));
                p.setName(rs.getString(3));
                p.setSurname(rs.getString(4));
                p.setPasswordHash(rs.getString(5));
                p.setUsername(username);
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserBean doRetrieveById(int id) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT username,email,name, surname FROM users WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserBean p = new UserBean();
                p.setUsername(rs.getString(1));
                p.setEmail(rs.getString(2));
                p.setName(rs.getString(3));
                p.setSurname(rs.getString(4));
                p.setId(id);
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean insert(UserBean u) throws SQLException {
        String sql = "INSERT INTO users(username,email,password_hash,name, surname) VALUES(?,?,?,?,?)";
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getName());
            ps.setString(5, u.getSurname());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            u.setId(id);

            return true;
        } catch (SQLIntegrityConstraintViolationException dup) {
            System.out.println(dup.getMessage());
            // username o email già esistenti
            return false;
        }
    }
}
