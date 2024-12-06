package de.einjojo.coinflip.storage;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Because SQL Lite is locally, we do not need HikariCP or any other connection pool management.
 * This class is a simple connection provider for SQL Lite.
 */
public class SQLLiteConnectionProvider implements ConnectionProvider {
    private final JavaPlugin plugin;

    public SQLLiteConnectionProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/coinflip.db");
    }
}
