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

package site.liangbai.realhomehunt.internal.storage.impl;

import com.dieselpoint.norm.Database;
import site.liangbai.realhomehunt.common.config.Config;

public final class MySqlStorage extends SqlStorage {
    private final Database database;

    public MySqlStorage(Config.StorageSetting.MySqlSetting setting) {
        database = new Database();

        database.setDriverClassName("com.mysql.jdbc.Driver");

        database.setJdbcUrl("jdbc:mysql://" + setting.address + ":" + setting.port + "/" + setting.options);

        database.setUser(setting.user);
        database.setPassword(setting.password);

        initTable();
    }

    @Override
    public Database getDatabase() {
        return database;
    }
}
