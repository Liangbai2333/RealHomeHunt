package site.liangbai.realhomehunt.util;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcUtil {
    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(Plugin plugin, String path) throws SQLException {
        return getConnection(plugin.getDataFolder().getAbsolutePath() + "/" + path);
    }

    public static Connection getConnection(String path) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public static Connection getConnection(String address, int port, String user, String password, String options) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + options, user, password);
    }
}
