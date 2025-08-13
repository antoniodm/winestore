package dao;

import model.CartBean;
import model.CartItem;
import model.ProductBean;

import java.sql.*;

public class CartDao {

    public static void printResult(ResultSet rs) throws SQLException {
        if (rs == null) {
            System.out.println("[printRS] ResultSet nullo");
            return;
        }

        // Serve uno statement creato con TYPE_SCROLL_*:
        // con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
            System.out.println("[printRS] ResultSet FORWARD_ONLY: impossibile stamparlo senza consumarlo.");
            return;
        }

        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        // salva posizione corrente
        boolean wasBeforeFirst = rs.isBeforeFirst();
        boolean wasAfterLast   = rs.isAfterLast();
        int currentRow         = rs.getRow(); // 0 se beforeFirst/afterLast

        // vai all'inizio, stampa tutto
        rs.beforeFirst();
        int r = 0;
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= cols; i++) {
                if (i > 1) row.append(" | ");
                row.append(md.getColumnLabel(i)).append('=').append(rs.getObject(i));
            }
            System.out.println(++r + ") " + row);
        }

        // ripristina posizione
        if (wasBeforeFirst) {
            rs.beforeFirst();
        } else if (wasAfterLast) {
            rs.afterLast();
        } else if (currentRow > 0) {
            rs.absolute(currentRow);
        }
    }

    /** Aggiunge +1 al carrello OPEN dell’owner (crea il carrello se manca). */
    public void addOneToOpenCart(Long userId, String sessionToken, int productId) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            con.setAutoCommit(false);
            int cartId = getOrCreateOpenCartId(con, userId, sessionToken);
            addOne(con, cartId, productId);
            con.commit();
        }
    }

    /** Decrementa di 1 (o rimuove riga se qty=1) dal carrello OPEN dell’owner. */
    public void decrementOrRemoveFromOpenCart(Long userId, String sessionToken, int productId) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            con.setAutoCommit(false);
            CartBean cart = loadOpenCart(con, userId, sessionToken);
            if (cart != null) decrementOrRemove(con, cart.getId(), productId);
            con.commit();
        }
    }

    /** Svuota il carrello OPEN dell’owner. */
    public void clearOpenCart(Long userId, String sessionToken) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            con.setAutoCommit(false);
            CartBean cart = loadOpenCart(con, userId, sessionToken);
            if (cart != null) clearCart(con, cart.getId());
            con.commit();
        }
    }

    /** Chiude il carrello OPEN (checkout). */
    public void checkout(Long userId, String sessionToken) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            con.setAutoCommit(false);
            closeOpenCart(con, userId, sessionToken);
            con.commit();
        }
    }

    /** Carica il carrello OPEN dell’owner (o null). */
    public CartBean loadOpenCart(Long userId, String sessionToken) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return loadOpenCart(con, userId, sessionToken);
        }
    }

    /** Ritorna l'id del carrello OPEN dell’owner, creandolo se manca. */
    public int getOrCreateOpenCartId(Long userId, String sessionToken) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getOrCreateOpenCartId(con, userId, sessionToken);
        }
    }


    /** Merge del carrello anonimo nell’utente (se esiste). */
    public void mergeAnonymousIntoUser(long userId, String sessionToken) throws SQLException {
        if (sessionToken == null || sessionToken.isBlank()) return;
        try (Connection con = ConPool.getConnection()) {
            con.setAutoCommit(false);

            CartBean anon  = loadOpenCart(con, null, sessionToken);
            CartBean userC = loadOpenCart(con, userId, null);

            if (anon != null) {
                if (userC == null) {
                    attachAnonymousCartToUser(con, sessionToken, userId);
                } else {
                    mergeCarts(con, anon.getId(), userC.getId());
                }
            }
            con.commit();
        }
    }

    /* ========================= Low-level (accettano Connection) ========================= */

    /** Get-or-create dell’OPEN cart con LAST_INSERT_ID sullo stesso connection. */
    public int getOrCreateOpenCartId(Connection con, Long userId, String sessionToken) throws SQLException {
        if ((userId == null) == (sessionToken == null))
            throw new IllegalArgumentException("Passa solo userId oppure solo sessionToken");

        final String sql = (userId != null)
                ? "INSERT INTO carts (user_id, status) VALUES (?, 'OPEN') " +
                "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)"
                : "INSERT INTO carts (session_token, status) VALUES (?, 'OPEN') " +
                "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)";

        try (PreparedStatement ps = con.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            if (userId != null) ps.setLong(1, userId);
            else                ps.setString(1, sessionToken);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT LAST_INSERT_ID()",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = ps.executeQuery()) {
            printResult(rs);
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Carica il carrello OPEN (con righe e prodotti) dell’owner. */
    public CartBean loadOpenCart(Connection con, Long userId, String sessionToken) throws SQLException {
        if ((userId == null) == (sessionToken == null))
            throw new IllegalArgumentException("Passa solo userId oppure solo sessionToken");

        String where = (userId != null) ? "c.user_id=?" : "c.session_token=?";
        String sql =
                "SELECT c.id AS cart_id, c.status, c.user_id, c.session_token, " +
                        "       ci.product_id, ci.quantity, ci.unit_price_cents, " +
                        "       p.name, p.description, p.origin, p.manufacturer, p.stock " +
                        "FROM carts c " +
                        "LEFT JOIN cart_items ci ON ci.cart_id = c.id " +
                        "LEFT JOIN products p    ON p.id = ci.product_id " +
                        "WHERE c.status='OPEN' AND " + where;

        try (PreparedStatement ps = con.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            if (userId != null) ps.setLong(1, userId);
            else                ps.setString(1, sessionToken);

            try (ResultSet rs = ps.executeQuery()) {
                printResult(rs);
                CartBean cart = null;
                while (rs.next()) {if (cart == null) {
                    cart = new CartBean();
                    cart.setId(rs.getInt("cart_id"));
                    cart.setStatus(CartBean.CartStatus.OPEN);
                    if (userId != null) cart.setUserId(userId);
                    else                cart.setSessionToken(sessionToken);
                }

                    /* Con LEFT JOIN queste colonne possono essere NULL */
                    Object pidObj  = rs.getObject("product_id");
                    if (pidObj == null) {
                        // carrello esistente ma senza righe: salta la creazione di CartItem
                        continue;
                    }
                    int pid = ((Number) pidObj).intValue();

                    /* quantity/unit_price_cents sono NOT NULL a schema, ma usiamo getInt che è safe */
                    int qty  = rs.getInt("quantity");
                    int unit = rs.getInt("unit_price_cents");

                    ProductBean p = new ProductBean();
                    p.setId(pid);
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setOrigin(rs.getString("origin"));
                    p.setManufacturer(rs.getString("manufacturer"));
                    p.setStock(rs.getInt("stock"));
                    p.setPrice(unit); // snapshot unitario

                    CartItem item = new CartItem(p);
                    item.setQuantity(qty);
                    cart.getProducts().add(item);

                }
                return cart;
            }
        }
    }

    /** Aggiunge +1 (snapshot da products). */
    public void addOne(Connection con, int cartId, int productId) throws SQLException {
        String sql =
                "INSERT INTO cart_items (cart_id, product_id, quantity, unit_price_cents) " +
                        "SELECT ?, p.id, 1, p.price_cents FROM products p WHERE p.id=? " +
                        "ON DUPLICATE KEY UPDATE quantity = cart_items.quantity + 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    /** Decrementa di 1; se qty diventa 0 rimuove la riga. */
    public void decrementOrRemove(Connection con, int cartId, int productId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE cart_items SET quantity = quantity - 1 " +
                        "WHERE cart_id=? AND product_id=? AND quantity > 1")) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                try (PreparedStatement del = con.prepareStatement(
                        "DELETE FROM cart_items WHERE cart_id=? AND product_id=?")) {
                    del.setInt(1, cartId);
                    del.setInt(2, productId);
                    del.executeUpdate();
                }
            }
        }
    }

    /** Svuota tutte le righe del carrello. */
    public void clearCart(Connection con, int cartId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM cart_items WHERE cart_id=?")) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    /** Chiude il carrello OPEN dell’owner. */
    public void closeOpenCart(Connection con, Long userId, String sessionToken) throws SQLException {
        if ((userId == null) == (sessionToken == null))
            throw new IllegalArgumentException("Passa solo userId oppure solo sessionToken");

        String sql = (userId != null)
                ? "UPDATE carts SET status='CLOSED' WHERE user_id=? AND status='OPEN'"
                : "UPDATE carts SET status='CLOSED' WHERE session_token=? AND status='OPEN'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId != null) ps.setLong(1, userId);
            else                ps.setString(1, sessionToken);
            ps.executeUpdate();
        }
    }

    private void attachAnonymousCartToUser(Connection con, String sessionToken, long userId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE carts SET user_id=?, session_token=NULL WHERE session_token=? AND status='OPEN'")) {
            ps.setLong(1, userId);
            ps.setString(2, sessionToken);
            ps.executeUpdate();
        }
    }



    private void mergeCarts(Connection con, int fromCartId, int toCartId) throws SQLException {
        // 1) Somma qty per i product_id già esistenti nel target
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE cart_items dst " +
                        "JOIN cart_items src ON src.cart_id=? AND dst.cart_id=? AND dst.product_id=src.product_id " +
                        "SET dst.quantity = dst.quantity + src.quantity")) {
            ps.setInt(1, fromCartId);
            ps.setInt(2, toCartId);
            ps.executeUpdate();
        }

        // 2) Inserisci le righe mancanti nel target
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cart_items (cart_id, product_id, quantity, unit_price_cents) " +
                        "SELECT ?, src.product_id, src.quantity, src.unit_price_cents " +
                        "FROM cart_items src " +
                        "LEFT JOIN cart_items dst ON dst.cart_id=? AND dst.product_id=src.product_id " +
                        "WHERE src.cart_id=? AND dst.product_id IS NULL")) {
            ps.setInt(1, toCartId);
            ps.setInt(2, toCartId);
            ps.setInt(3, fromCartId);
            ps.executeUpdate();
        }

        // 3) Pulisci il sorgente
        try (PreparedStatement delItems = con.prepareStatement("DELETE FROM cart_items WHERE cart_id=?")) {
            delItems.setInt(1, fromCartId);
            delItems.executeUpdate();
        }
        try (PreparedStatement delCart = con.prepareStatement("DELETE FROM carts WHERE id=?")) {
            delCart.setInt(1, fromCartId);
            delCart.executeUpdate();
        }
    }



}
