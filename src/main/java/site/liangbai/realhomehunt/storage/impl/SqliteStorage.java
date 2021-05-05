package site.liangbai.realhomehunt.storage.impl;

import org.bukkit.plugin.Plugin;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.util.Jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public final class SqliteStorage extends SqlStorage {

    private final Config.StorageSetting.SqliteSetting setting;

    private final Plugin plugin;

    public SqliteStorage(Plugin plugin, Config.StorageSetting.SqliteSetting setting) {
        Jdbc.init();

        this.plugin = plugin;
        this.setting = setting;

        initTable();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (setting.onlyInPluginFolder) {
            return Jdbc.getConnection(plugin, setting.databaseFile);
        } else {
            return Jdbc.getConnection(setting.databaseFile);
        }
    }
}
