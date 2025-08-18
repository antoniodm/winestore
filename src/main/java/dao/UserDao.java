package dao;

import model.UserBean;
import java.sql.*;

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
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id,email,name,surname,password_hash, address, birth_date, money FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            UserBean u = new UserBean();
            u.setId(rs.getLong("id"));
            u.setEmail(rs.getString("email"));
            u.setName(rs.getString("name"));
            u.setSurname(rs.getString("surname"));
            u.setPasswordHash(rs.getString("password_hash"));
            u.setAddress(rs.getString("address"));
            u.setBirthdate(rs.getString("birth_date"));
            u.setMoney(rs.getInt("money"));
            u.setUsername(username);
            return u;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public UserBean doRetrieveById(int id) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT username,email,name, surname, address, birth_date, money FROM users WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserBean p = new UserBean();
                p.setUsername(rs.getString(1));
                p.setEmail(rs.getString(2));
                p.setName(rs.getString(3));
                p.setSurname(rs.getString(4));
                p.setAddress(rs.getString(5));
                p.setBirthdate(rs.getString(6));
                p.setMoney(rs.getInt(7));
                p.setId(id);
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(UserBean u) throws SQLException {
        String sql = "UPDATE users set username=?, email=?, password_hash=?, name=?, surname=?, address=?, birth_date=? WHERE id=?";
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getName());
            ps.setString(5, u.getSurname());
            ps.setString(6, u.getAddress());
            ps.setString(7, u.getBirthdate());
            ps.setLong(8, u.getId());

            System.out.println(ps);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) return true;

            return false;
        } catch (SQLIntegrityConstraintViolationException dup) {
            System.out.println(dup.getMessage());
            // username o email già esistenti
            return false;
        }
    }

    public boolean updateCredit(Long user_id, int money) throws SQLException {
        String sql = "UPDATE users set money=? WHERE id=?";
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, money);
            ps.setLong(2, user_id);

            System.out.println(ps);

            int rowsUpdated = ps.executeUpdate();

            System.out.println(rowsUpdated);
            if (rowsUpdated > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            System.out.println(dup.getMessage());
            // username o email già esistenti
            return false;
        }
    }


    public boolean insert(UserBean u) throws SQLException {
        String sql = "INSERT INTO users(username,email,password_hash, name, surname, address, birth_date, money) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getName());
            ps.setString(5, u.getSurname());
            ps.setString(6, u.getAddress());
            ps.setString(7, u.getBirthdate());
            ps.setInt(8, u.getMoney());
            System.out.println(ps);
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
