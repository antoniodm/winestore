// src/main/java/config/DbInitListener.java
package config;

import dao.ConPool;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

@WebListener
public class DbInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String url  = ctx.getInitParameter("dbUrl");
        String user = ctx.getInitParameter("dbUser");
        String pass = ctx.getInitParameter("dbPassword");

        // Aggiunge serverTimezone se assente (utile con MySQL)
        if (url != null && !url.contains("serverTimezone=")) {
            String tz = java.util.TimeZone.getDefault().getID();
            url += (url.contains("?") ? "&" : "?") + "serverTimezone=" + tz;
        }

        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername(user);
        p.setPassword(pass);

        // impostazioni minime e sicure
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setMaxActive(50);
        p.setInitialSize(5);
        p.setMinIdle(5);
        p.setRemoveAbandoned(true);
        p.setRemoveAbandonedTimeout(60);

        DataSource ds = new DataSource();
        ds.setPoolProperties(p);

        // registra nello scope application (opzionale ma comodo)
        ctx.setAttribute("dbSource", ds);

        // registra nel ConPool usato dai DAO
        ConPool.configure(ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConPool.shutdown();
    }
}
