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

import com.dieselpoint.norm.Database;
import org.bukkit.plugin.Plugin;
import site.liangbai.realhomehunt.config.Config;

public final class SqliteStorage extends SqlStorage {

    private final Database database;

    public SqliteStorage(Plugin plugin, Config.StorageSetting.SqliteSetting setting) {
        database = new Database();

        String url = setting.onlyInPluginFolder ? "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + setting.databaseFile : "jdbc:sqlite:" + setting.databaseFile;
        database.setDriverClassName("org.sqlite.JDBC");
        database.setJdbcUrl(url);

        initTable();
    }

    @Override
    public Database getDatabase() {
        return database;
    }
}
