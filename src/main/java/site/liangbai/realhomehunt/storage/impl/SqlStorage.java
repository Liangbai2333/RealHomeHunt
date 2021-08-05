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
import com.dieselpoint.norm.Query;
import com.dieselpoint.norm.sqlmakers.StandardSqlMaker;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;
import site.liangbai.realhomehunt.util.SqlMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SqlStorage implements IStorage {

    private static final List<String> owners = new ArrayList<>();

    private long count;

    public abstract Database getDatabase();

    public void initTable() {

        StandardSqlMaker sqlMaker = ((StandardSqlMaker) getDatabase().getSqlMaker());

        SqlMaker proxy = new SqlMaker(sqlMaker);

        new Query(getDatabase()).sql(proxy.getCreateTableIfNotExistsSql(Residence.class)).execute();
    }

    @Override
    public void save(Residence residence) {
        if (!owners.contains(residence.getOwner())) {
            getDatabase().insert(residence);

            owners.add(residence.getOwner());

            return;
        }

        getDatabase().update(residence);
    }

    @Override
    public void remove(Residence residence) {
        if (owners.contains(residence.getOwner())) {
            getDatabase().delete(residence);

            owners.remove(residence.getOwner());
        }
    }

    @Override
    public List<Residence> loadAll() {
        List<Residence> list = getDatabase().results(Residence.class).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        list.stream()
                .map(Residence::getOwner)
                .forEach(owners::add);

        count = list.size();

        return list;
    }

    @Override
    public long count() {
        return count;
    }
}
