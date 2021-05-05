package site.liangbai.realhomehunt.storage.impl;

import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.util.Jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public final class MySqlStorage extends SqlStorage {
    private final Config.StorageSetting.MySqlSetting setting;

    public MySqlStorage(Config.StorageSetting.MySqlSetting setting) {
        Jdbc.init();

        this.setting = setting;

        initTable();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return Jdbc.getConnection(setting.address, setting.port, setting.user, setting.password, setting.options);
    }
}
