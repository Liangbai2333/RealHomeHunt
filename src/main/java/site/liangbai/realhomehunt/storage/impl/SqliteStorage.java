/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
